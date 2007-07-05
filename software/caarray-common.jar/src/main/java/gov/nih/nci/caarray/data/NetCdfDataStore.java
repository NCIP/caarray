/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.data;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;


/**
 * @author John Pike
 *
 */
public class NetCdfDataStore implements DataStore {

    private static final Log LOG = LogFactory.getLog(NetCdfDataStore.class);
    private final NetcdfFile netcdffile;
    /**
     *
     */
    protected static final int X_VAL_COL = 0;
    /**
     *
     */
    protected static final int Y_VAL_COL = 1;
    /**
    *
    */
    protected static final int P_VAL_COL = 2;
    /**
     *
     */
    protected static final int PCT_VAL_COL = 3;

    private NetcdfDataStoreDescriptor descriptor;



    /**
     * @param file the file
     * @throws DataStoreException exception
     */
    public NetCdfDataStore(NetcdfFile file) throws DataStoreException {
        super();

        netcdffile = file;

    }

    /**
     * @see gov.nih.nci.caarray.data.DataStore#getDescription()
     * @return DataStoreDescriptor a descriptor
     */
    public NetcdfDataStoreDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = new NetcdfDataStoreDescriptor();
        }
        return descriptor;
    }


    /**
     * @see gov.nih.nci.caarray.data.DataStore#getValue(int, gov.nih.nci.caarray.data.Column)
     * @param index an int
     * @param column the Column
     * @throws DataStoreException an exception
     * @return Object
     */
    public Object getValue(int index, Column column) throws DataStoreException {
        Object value = null;
        Variable v = this.netcdffile.findVariable(column.getName());
        try {
            Array columnArray = v.read();
            Index indObj = columnArray.getIndex();
            value = columnArray.getObject(indObj.set(index));
        } catch (IOException ie) {
            throw new DataStoreException("Error reading variable in getValue()", ie);
        }
        return value;
    }

    /**
     * Returns all of the row values for a given Column.
     * @see gov.nih.nci.caarray.data.DataStore#getValues(gov.nih.nci.caarray.data.Column)
     * @param column the Column
     * @throws DataStoreException an exception
     * @return Object[] an array
     */
    public Object[] getValues(Column column) throws DataStoreException {
        Object[] returnObj = null;
        Variable var = this.netcdffile.findVariable(column.getName());
        boolean isChar = var.getDataType().equals(ucar.ma2.DataType.CHAR);
        try {
            returnObj = fillArrayValues(var, isChar);
        } catch (IOException ie) {
            throw new DataStoreException("Error reading file in getValues()", ie);
        } catch (Exception e) {
            LOG.error("Error in getvalues", e);
        }
        return returnObj;

    }

    /**
     * @param returnObj
     * @param var
     * @param isChar
     * @throws IOException
     */
    private Object[] fillArrayValues(Variable var, boolean isChar) throws IOException {
        Array dataArray;
        Index idx;
        Dimension dim;
        if (isChar) {
            dataArray = (ArrayChar) var.read();
        } else {
            dataArray = (Array) var.read();
        }
        idx = dataArray.getIndex();
        dim = var.getDimension(0);
        Object[] returnObj = new Object[dim.getLength()];

        for (int i = 0; i < dim.getLength(); i++) {
            if (isChar) {
                returnObj[i] = ((ArrayChar) dataArray).getString(idx.set(i, 0));
            } else {
                returnObj[i] = dataArray.getObject(idx.set(i));
            }
        }
        return returnObj;
    }

    /**
     * Returns all the Column values for the row at the argIndex.
     * @see gov.nih.nci.caarray.data.DataStore#getValues(int)
     * @param index an int
     * @throws DataStoreException exception
     * @return Object[] an array
     */
    public Object[] getValues(int index) throws DataStoreException {

        Object[] returnObj = new Object[this.getDescriptor().getColumns().size()];

        try {
            List variables = this.netcdffile.getVariables();
            for (int i = 0; i < variables.size(); i++) {
                Variable var = (Variable) variables.get(i);
                Index idx = null;
                if (var.getDataType().equals(ucar.ma2.DataType.CHAR)) {
                    ArrayChar dataArray = (ArrayChar) var.read();
                    idx = dataArray.getIndex();
                    idx.set(index, 0);
                    returnObj[i] = dataArray.getString(idx);
                } else {
                    Array dataArray = (Array) var.read();
                    idx = dataArray.getIndex();
                    idx.set(index);
                    returnObj[i] = dataArray.getObject(idx);
                }
            }
        } catch (IOException ie) {
            throw new DataStoreException("Error reading file in getValues()", ie);
        } catch (Exception e) {
            LOG.error("error in getValues", e);
        }
        return returnObj;
    }


}
