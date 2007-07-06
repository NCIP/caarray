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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ucar.ma2.Array;
import ucar.ma2.ArrayBoolean;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayShort;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;



/**
 * This class is for DataStores of NetCDF files.  The netCDF files are simply files
 * which contain rows and columns.  The "columns" are stored as Variable objects.  Each
 * variable contains an array of data. The columns to store are obtained from the
 * DataStoreDescriptor object.  Values are obtained by obtaining the value at an
 * index point in the Column Array-- ie, array.getValue(index).  However, Strings are
 * not handled by Netcdf.  Rather, they are stored as a 2D array of CHAR, where the
 * second dimension's size is defined as "SVAR_LEN" below.  Strings are obtained as
 * follows--  array.getString(index, 0); meaning, it will return a String value
 * obtained from "index" point, and of index 0 (I could use any number here) in the
 * SVAR array...which are all CHAR arrays with lenght of SVAR_LEN.
 *
 * Usage:  to create a new NetcdfDataStore, the procedure would be as follows:
 * 1.  Factory.createDataStore()
 * 2.  createFile() -- creates a file of the correct structure in your file system
 * 3.  saveData() -- saves Arrays of column data to the file
 * 4.  closeFile() -- flushes the data from memory to the file system.
 *
 * Usage:  to use an existing DataStore, the procedure is as follows:
 * 1.  Factor.getDataStore()
 * 2.  setValue()
 * 3.  closeFile()
 *
 * @author John Pike
 *
 */
@SuppressWarnings("PMD")
public class NetCdfDataStore implements DataStore {

    private static final Log LOG = LogFactory.getLog(NetCdfDataStore.class);
    private final NetcdfFileWriteable netcdffile;
    private NetcdfDataStoreDescriptor descriptor;
    private static final int SVAR_LEN = 80;
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





    /**
     * @param file the file
     * @throws DataStoreException exception
     */
    public NetCdfDataStore(NetcdfFileWriteable file) throws DataStoreException {
        super();

        netcdffile = file;
        descriptor = getDescriptor();
    }

    /**
     * This method obtains the column data that will describe the type of
     * file (netcdf) being created here.  The file will have variables for
     * each of the Descriptor's columns, each of the DataType described
     * in the Column object.
     * @see gov.nih.nci.caarray.data.DataStore#getDescription()
     * @return DataStoreDescriptor a descriptor
     */
    public final NetcdfDataStoreDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = new NetcdfDataStoreDescriptor();
        }
        return descriptor;
    }


    /**
     * This method persists arrays of Column data into a previously
     * created NetCDF file.
     * User should always call "closeFile()" after saving data.  In case of an exception,
     * this method will close the file itself.
     * @param files list of files
     * @param colNames list of column names
     * @throws DataStoreException exception
     */
    public void saveData(List<Array> files, List<String> colNames) throws DataStoreException {
        try {
            for (int i = 0; i < files.size(); i++) {
                writeFile(files.get(i), colNames.get(i));
            }
        } catch (DataStoreException e) {
            closeFile();
            throw e;
        }
    }


    /**
     * User shoudl always call "closeFile()" after calling this method.
     * In case of exception, this method will close the file.
     *@param column the array to save
     *@throws DataStoreException exception
     */
    public void save(Column column) throws DataStoreException {
        try {
            Variable var = netcdffile.findVariable(column.getName());
            Array array = var.read();
            netcdffile.write(column.getName(), array);
        } catch (IOException ie) {
            LOG.error("error writing file", ie);
            closeFile();
            throw new DataStoreException(ie.getMessage());
        } catch (InvalidRangeException ire) {
            LOG.error("error writing file", ire);
            closeFile();
            throw new DataStoreException(ire.getMessage());

        }
    }
    /**
     * This method will modify the value of a cell in the array, and currently will
     * also save the file at the same time.  Note the maneuvering to set a String value
     * (ie, DataType.CHAR).
     *
     * User of this method should always call "closeFIle" after invoking. In case of
     * exception, this method will close the file.
     * @param index the row index
     * @param column the column of data
     * @param value to be saved
     * @throws DataStoreException exception
     * TODO  This is inefficient, since we have to write to file after saving each value.
     */
    @SuppressWarnings("PMD")
    public void setValue(int index, Column column, Object value) throws DataStoreException {
        Variable var = netcdffile.findVariable(column.getName());

        try {
            Array dataArray = var.read();
            Index indexObj = dataArray.getIndex();
            ucar.ma2.DataType dataType = column.getType();
            if (dataType.equals(ucar.ma2.DataType.CHAR)) {
                ((ArrayChar) dataArray).setString(indexObj.set(index, 0), (String) value);
            } else if (dataType.equals(ucar.ma2.DataType.SHORT)) {
                ((ArrayShort) dataArray).setShort(indexObj.set(index), ((Short) value).shortValue());
            } else if (dataType.equals(ucar.ma2.DataType.INT)) {
                ((ArrayInt) dataArray).setInt(indexObj.set(index), ((Integer) value).intValue());
            } else if (dataType.equals(ucar.ma2.DataType.LONG)) {
                ((ArrayLong) dataArray).setLong(indexObj.set(index), ((Long) value).longValue());
            } else if (dataType.equals(ucar.ma2.DataType.DOUBLE)) {
                ((ArrayDouble) dataArray).setDouble(indexObj.set(index), ((Double) value).doubleValue());
            } else if (dataType.equals(ucar.ma2.DataType.FLOAT)) {
                ((ArrayFloat) dataArray).setFloat(indexObj.set(index), ((Float) value).floatValue());
            } else if (dataType.equals(ucar.ma2.DataType.BOOLEAN)) {
                ((ArrayBoolean) dataArray).setBoolean(indexObj.set(index), ((Boolean) value).booleanValue());
            } else if (dataType.equals(ucar.ma2.DataType.BYTE)) {
                ((ArrayByte) dataArray).setByte(indexObj.set(index), ((Byte) value).byteValue());
            } else {
                ((Array) dataArray).setObject(indexObj.set(index), value);
            }
            //this sucks.  must write the file now.  not very efficient.
            writeFile(dataArray, var.getName());
        } catch (DataStoreException dse) {
            closeFile();
            LOG.error("error writing file in setValue()", dse);
            throw dse;
        } catch (IOException ie) {
            closeFile();
            LOG.error("error reading variable in setValue()", ie);
            throw new DataStoreException(ie.getMessage(), ie);
        } catch (ClassCastException cce) {
            closeFile();
            LOG.error("value " + value + " is not of the same datatype as the column " + column.getType()
                    + " into which it is being saved", cce);
            throw new DataStoreException(cce.getMessage(), cce);
        }
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
        Variable var = netcdffile.findVariable(column.getName());
        boolean isChar = isChar(var);
        try {
            Array columnArray = var.read();
            Index indObj = columnArray.getIndex();
            if (isChar) {
                value = ((ArrayChar) columnArray).getString(indObj.set(index, 0));
            } else {
                value = columnArray.getObject(indObj.set(index));
            }
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
        Variable var = netcdffile.findVariable(column.getName());
        boolean isChar = isChar(var);
        try {
            returnObj = fillArrayValues(var, isChar);
        } catch (IOException ie) {
            throw new DataStoreException("Error reading file in getValues()", ie);
        } catch (Exception e) {
            LOG.error("Exception in getvalues", e);
            throw new DataStoreException(e.getMessage(), e);
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
            List variables = netcdffile.getVariables();
            for (int i = 0; i < variables.size(); i++) {
                Variable var = (Variable) variables.get(i);
                boolean isChar = isChar(var);
                Index idx = null;
                if (isChar) {
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
            throw new DataStoreException(e.getMessage(), e);
        }
        return returnObj;
    }

    /**
     * @param var
     * @return
     */
    private boolean isChar(Variable var) {
        return var.getDataType().equals(ucar.ma2.DataType.CHAR);
    }



    /**
     * Creates a netcdffile of the given rowSize, properly allocating the dimensions
     * and columns per the DataStoreDescriptor file.
     * @param size int
     * @throws DataStoreException exception
     */
    public void createFile(int size) throws DataStoreException {
        Dimension dataDim = netcdffile.addDimension("data", size);
        Dimension svarLen = netcdffile.addDimension("svar_len", SVAR_LEN);
        Dimension[] dimList = {dataDim};
        Dimension[] dimStrList = {dataDim, svarLen};
        List<Column> columns = descriptor.getColumns();

        for (Iterator iter = columns.iterator(); iter.hasNext();) {
            Column column = (Column) iter.next();
            Dimension[] dimListToAdd = null;
            if ((column.getType()).equals(ucar.ma2.DataType.CHAR)) {
                dimListToAdd = dimStrList;
            } else {
                dimListToAdd = dimList;
            }
            netcdffile.addVariable(column.getName(), column.getType(), dimListToAdd);
        }
        try {
            netcdffile.create();
        } catch (IOException e) {
            LOG.error("Error creating file");
            throw new DataStoreException(e.getMessage(), e);
        }
    }

    /**
     *@throws DataStoreException exception
     */
    public void closeFile() throws DataStoreException {
        try {
            if (netcdffile != null) {
                netcdffile.close();
            }
        } catch (IOException e) {
            LOG.error("ERROR closing file");
            throw new DataStoreException(e.getMessage(), e);
        }

    }
    @SuppressWarnings("PMD")
    private void writeFile(Array array, String varName) throws DataStoreException {
        String dataType = array.getElementType().getCanonicalName();
        try {
            if (dataType.equals(ucar.ma2.DataType.CHAR.toString())) {
                netcdffile.write(varName, (ArrayChar.D2) array);
            } else if (dataType.equals(ucar.ma2.DataType.FLOAT.toString())) {
                netcdffile.write(varName, (ArrayFloat) array);
            } else if (dataType.equals(ucar.ma2.DataType.BOOLEAN.toString())) {
                netcdffile.write(varName, (ArrayBoolean) array);
            } else if (dataType.equals(ucar.ma2.DataType.BYTE.toString())) {
                netcdffile.write(varName, (ArrayByte) array);
            } else if (dataType.equals(ucar.ma2.DataType.DOUBLE.toString())) {
                netcdffile.write(varName, (ArrayDouble) array);
            } else if (dataType.equals(ucar.ma2.DataType.INT.toString())) {
                netcdffile.write(varName, (ArrayInt) array);
            } else if (dataType.equals(ucar.ma2.DataType.SHORT.toString())) {
                netcdffile.write(varName, (ArrayShort) array);
            } else if (dataType.equals(ucar.ma2.DataType.LONG.toString())) {
                netcdffile.write(varName, (ArrayLong) array);
            } else {
                netcdffile.write(varName, (Array) array);
            }
        } catch (InvalidRangeException ire) {
            LOG.error("Invalid Range ERROR writing file");
            throw new DataStoreException(ire.getMessage(), ire);
        } catch (IOException e) {
            LOG.error("Exception writing file");
            throw new DataStoreException(e.getMessage(), e);
        } catch (ClassCastException cce) {
            LOG.error("value for " + varName + " is not of the same datatype as the column"
                    + " into which it is being saved", cce);
            throw new DataStoreException(cce.getMessage(), cce);
        }
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




}
