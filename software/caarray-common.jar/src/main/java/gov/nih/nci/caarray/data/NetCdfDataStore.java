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
    private NetcdfFile netcdffile = null;
    private static final String X_VAR = "X_VAL_COL";
    private static final String Y_VAR = "Y_VAL_COL";
    private static final String PCT_VAR = "PCT_VAL_COL";
    private static final String P_VAR = "P_VAL_COL";
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

    private DataStoreDescriptor descriptor;



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
    public DataStoreDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = new DataStoreDescriptor();
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
     * @see gov.nih.nci.caarray.data.DataStore#getValues(gov.nih.nci.caarray.data.Column)
     * @param column the Column
     * @throws DataStoreException an exception
     * @return Object[] an array
     */
    public Object[] getValues(Column column) throws DataStoreException {
        Variable v = this.netcdffile.findVariable(column.getName());
        Object[] returnObj = null;
        try {
            Array dataArray = (Array) v.read();
            Dimension d = v.getDimension(0);
            returnObj = new Object[d.getLength()];
            Index idx = dataArray.getIndex();

            for (int i = 0; i < d.getLength(); i++) {
                returnObj[i] = dataArray.getObject(idx.set(i));
            }
        } catch (IOException ie) {
            throw new DataStoreException("Error reading file in getValues()", ie);
        } catch (Exception e) {
            LOG.error("Error in getvalues", e);
        }
        return returnObj;

    }

    /**
     * @see gov.nih.nci.caarray.data.DataStore#getValues(int)
     * @param index an int
     * @throws DataStoreException exception
     * @return Object[] an array
     */
    public Object[] getValues(int index) throws DataStoreException {
        Variable vXVar = this.netcdffile.findVariable(X_VAR);
        Variable vYVar = this.netcdffile.findVariable(Y_VAR);
        Variable vPVar = this.netcdffile.findVariable(P_VAR);
        Variable vPctVar = this.netcdffile.findVariable(PCT_VAR);
        Object[] returnObj = new Object[this.getDescriptor().getColumns().size()];
        try {
            Array dataArray = (Array) vXVar.read();
            Index idx = dataArray.getIndex();
            returnObj[X_VAL_COL] = dataArray.getObject(idx.set(index));
            dataArray = (Array) vYVar.read();
            idx = dataArray.getIndex();
            returnObj[Y_VAL_COL] = dataArray.getObject(idx.set(index));
            ArrayChar charArray = (ArrayChar) vPVar.read();
            idx = charArray.getIndex();
            returnObj[P_VAL_COL] = charArray.getString(idx.set(index, 0));
            dataArray = (Array) vPctVar.read();
            idx = dataArray.getIndex();
            returnObj[PCT_VAL_COL] = dataArray.getObject(idx.set(index));
        } catch (IOException ie) {
            throw new DataStoreException("Error reading file in getValues()", ie);
        } catch (Exception e) {
            LOG.error("error in getValues", e);
        }
        return returnObj;
    }


}
