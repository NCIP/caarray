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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
 * 3a.  saveData() -- saves Arrays of column data to the file;  OR,
 * 3b.  saveColumnData() -- saves lists of native java types to the file
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
/**
 * @author John Pike
 *
 */
/**
 * @author John Pike
 *
 */
/**
 * @author John Pike
 *
 */
/**
 * @author John Pike
 *
 */
@SuppressWarnings("PMD")
public class NetCdfDataStore implements DataStore {

    private static final Logger LOG = Logger.getLogger(NetCdfDataStore.class);
    private final NetcdfFileWriteable netcdffile;
    private NetcdfDataStoreDescriptor descriptor;
    private static final int SVAR_LEN = 80;
    private static final String DATA_DIM_STR = "data";
    private static final String SVAR_LEN_STR = "svar_len";
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
     * @param argFile the file
     * @param argDescriptor the descriptor
     */
    public NetCdfDataStore(NetcdfFileWriteable argFile, NetcdfDataStoreDescriptor argDescriptor) {
        super();

        netcdffile = argFile;
        descriptor = argDescriptor;
    }


    /**
     * @param argFile the file
     */
    public NetCdfDataStore(NetcdfFileWriteable argFile) {
        super();

        netcdffile = argFile;
        createDescriptorFromFile(netcdffile);

    }

    @SuppressWarnings("unchecked")
    private void createDescriptorFromFile(NetcdfFileWriteable argFile) {
        if (descriptor == null) {
            descriptor = new NetcdfDataStoreDescriptor();
            ArrayList<Column> columns = new ArrayList<Column>();
            List<Variable> variables = argFile.getVariables();

            for (int i = 0; i < variables.size(); i++) {
                Column column = new Column();
                Variable variable = variables.get(i);
                column.setName(variable.getName());
                column.setType(DataType.valueOf(variable.getDataType().toString().toUpperCase()));
                columns.add(i, column);
            }
            descriptor.setColumns(columns);
        }
    }

    /**
     * This method persists arrays of Column data into a previously
     * created NetCDF file.
     *
     * This lists of "files" must be of type <code>ucar.ma2.Array</code>.
     *
     * User should always call "closeFile()" after saving data.  In case of an exception,
     * this method will close the file itself.
     *
     * The List of Array objects are constructed to match the columns in the file,
     * and must have a corresponding value in the List of colNames
     *
     *      ArrayList arrayList = new ArrayList();
     *      arrayList.add(floatArrayXVar);
     *      arrayList.add(floatArrayYVar);
     *      arrayList.add(stringArrayPVar);
     *      arrayList.add(floatArrayPctVar);
     *      netCdfDS.saveData(arrayList, varList);
     *      netCdfDS.closeFile();
     * @param files list of files
     * @param colNames list of column names
     * @throws DataStoreException exception
     */
    public void saveData(List<Array> files, List<String> colNames) throws DataStoreException {
        if (files == null || colNames == null || (files.size() != colNames.size())) {
            throw new DataStoreException("Illegal arguments to saveData().  Lists must be of same length");
        }
        try {
            for (int i = 0; i < files.size(); i++) {
                writeFile(files.get(i), colNames.get(i));
            }
        } catch (DataStoreException e) {
            closeFile();
            throw e;
        }
    }


    /**This is the preferred method of saving values to an existing netcdf file.  It allows the user
     * to save lists of java objects, rather than objects of type <code>ucar.ma2.Array</code>
     * It is much more efficient than setValue() for saving large numbers of values, in that
     * the commit only happens once, rather than after each value.
     *
     * The values in the lists of column data should be of the following datatypes:
     *   java.lang.Float, java.lang.Boolean, java.lang.Byte, java.lang.Short, java.lang.Integer,
     *   java.lang.Double, java.lang.Char, java.lang.Long, java.lang.String.
     *
     * @param columns list of column data
     * @param colNames list of columNames
     * @throws DataStoreException exception
     */
    public void saveColumnData(List<ArrayList<?>> columns, List<String> colNames)
            throws DataStoreException {
        if (columns == null || colNames == null || (colNames.size() != colNames.size())) {
            throw new DataStoreException("Illegal arguments to saveColumnData().  Lists must be of same length");
        }
        List<Array> files = columnsToArrays(columns);
        saveData(files, colNames);
    }





    /**
     * User should always call "closeFile()" after calling this method.
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
            ucar.ma2.DataType dataType = column.getType().getType();
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
                (dataArray).setObject(indexObj.set(index), value);
            }
            //this is bad.  must write the file now.  not very efficient.
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
    public Object getValues(Column column) throws DataStoreException {
        Object returnObj = null;
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
    @SuppressWarnings("unchecked")
    public Object[] getValues(int index) throws DataStoreException {

        Object[] returnObj = new Object[this.getDescriptor().getColumns().size()];

        try {
            List<Variable> variables = netcdffile.getVariables();
            for (int i = 0; i < variables.size(); i++) {
                Variable var = variables.get(i);
                boolean isChar = isChar(var);
                Index idx = null;
                if (isChar) {
                    ArrayChar dataArray = (ArrayChar) var.read();
                    idx = dataArray.getIndex();
                    idx.set(index, 0);
                    returnObj[i] = dataArray.getString(idx);
                } else {
                    Array dataArray = var.read();
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
     * Creates a netcdffile of the given rowSize, properly allocating the dimensions
     * and columns per the DataStoreDescriptor file.  For CHAR or STRING datatypes,
     * this means creating a 2-dimension Character array, which is how Netcdf
     * handles Strings.
     * @param size int
     * @throws DataStoreException exception
     */
    public void createFile(int size) throws DataStoreException {
        Dimension dataDim = netcdffile.addDimension(DATA_DIM_STR, size);
        Dimension svarLen = netcdffile.addDimension(SVAR_LEN_STR, SVAR_LEN);
        Dimension[] dimList = {dataDim};
        Dimension[] dimStrList = {dataDim, svarLen};
        List<Column> columns = descriptor.getColumns();

        for (Column column : columns) {
            Dimension[] dimListToAdd = null;
            if ((column.getType().getType()).equals(ucar.ma2.DataType.CHAR)
                    || (column.getType().getType()).equals(ucar.ma2.DataType.STRING)) {
                dimListToAdd = dimStrList;
            } else {
                dimListToAdd = dimList;
            }
            netcdffile.addVariable(column.getName(), column.getType().getType(), dimListToAdd);
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


    /**
     * @param var
     * @return
     */
    private boolean isChar(Variable var) {
        return (var.getDataType().equals(ucar.ma2.DataType.CHAR)
                || var.getDataType().equals(ucar.ma2.DataType.STRING));
    }


    private List<Array> columnsToArrays(List<ArrayList<?>> columns) {
        List<Array> returnList = new ArrayList<Array>();
        for (int i = 0; i < columns.size(); i++) {
            ArrayList<?> column = columns.get(i);
            String colType = column.get(0).getClass().getSimpleName();
            if (colType.equals("Float")) {
                ArrayFloat array = (ArrayFloat) Array.factory(float.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setFloat(idx.set(j), (Float) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Integer")) {
                ArrayInt array = (ArrayInt) Array.factory(int.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setInt(idx.set(j), (Integer) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Short")) {
                ArrayShort array = (ArrayShort) Array.factory(short.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setShort(idx.set(j), (Short) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Double")) {
                ArrayDouble array = (ArrayDouble) Array.factory(double.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setDouble(idx.set(j), (Double) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Long")) {
                ArrayLong array = (ArrayLong) Array.factory(long.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setLong(idx.set(j), (Long) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Byte")) {
                ArrayByte array = (ArrayByte) Array.factory(byte.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setByte(idx.set(j), (Byte) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Boolean")) {
                ArrayBoolean array = (ArrayBoolean) Array.factory(boolean.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setBoolean(idx.set(j), (Boolean) column.get(j));
                }
                returnList.add(array);
            } else if (colType.equals("Char") || colType.equals("String")) {
                ArrayChar array = (ArrayChar.D2) Array.factory(char.class, new int[] {column.size(), SVAR_LEN});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setString(idx.set(j, 0), (String) column.get(j));
                }
                returnList.add(array);
            } else {
                Array array = Array.factory(Object.class, new int[] {column.size()});
                Index idx = array.getIndex();
                for (int j = 0; j < column.size(); j++) {
                    array.setObject(idx.set(i), column.get(i));
                }
                returnList.add(array);
            }
        }
        return returnList;
    }



    private void writeFile(Array array, String varName) throws DataStoreException {
        String dataType = array.getElementType().getCanonicalName();
        try {
            if (dataType.equals(ucar.ma2.DataType.CHAR.toString())
                    || dataType.equals(ucar.ma2.DataType.STRING)) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.FLOAT.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.BOOLEAN.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.BYTE.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.DOUBLE.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.INT.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.SHORT.toString())) {
                netcdffile.write(varName, array);
            } else if (dataType.equals(ucar.ma2.DataType.LONG.toString())) {
                netcdffile.write(varName, array);
            } else {
                netcdffile.write(varName, array);
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
            dataArray = var.read();
        } else {
            dataArray = var.read();
        }
        String dataType = dataArray.getElementType().getCanonicalName();
        idx = dataArray.getIndex();
        dim = var.getDimension(0);
        Object[] returnObj = null;
        returnObj = initArrayOfType(dataType, dim.getLength());

        for (int i = 0; i < dim.getLength(); i++) {
            if (isChar) {
                returnObj[i] = ((ArrayChar.D2) dataArray).getString(idx.set(i, 0));
            } else if (dataType.equals(ucar.ma2.DataType.FLOAT.toString())) {
                returnObj[i] = ((ArrayFloat) dataArray).getFloat(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.INT.toString())) {
                returnObj[i] = ((ArrayInt) dataArray).getInt(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.SHORT.toString())) {
                returnObj[i] = ((ArrayShort) dataArray).getShort(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.LONG.toString())) {
                returnObj[i] = ((ArrayLong) dataArray).getLong(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.DOUBLE.toString())) {
                returnObj[i] = ((ArrayDouble) dataArray).getDouble(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.BOOLEAN.toString())) {
                returnObj[i] = ((ArrayBoolean) dataArray).getBoolean(idx.set(i));
            } else if (dataType.equals(ucar.ma2.DataType.BYTE.toString())) {
                returnObj[i] = ((ArrayByte) dataArray).getByte(idx.set(i));
            } else {
                returnObj[i] = dataArray.getObject(idx.set(i));
            }
        }
        return returnObj;
    }


    private Object[] initArrayOfType(String dataType, int size) {
        if (dataType.equals(ucar.ma2.DataType.CHAR.toString())
            || dataType.equals(ucar.ma2.DataType.STRING)) {
            return new String[size];
        } else if (dataType.equals(ucar.ma2.DataType.FLOAT.toString())) {
            return new Float[size];
        } else if (dataType.equals(ucar.ma2.DataType.INT.toString())) {
            return new Integer[size];
        } else if (dataType.equals(ucar.ma2.DataType.SHORT.toString())) {
            return new Short[size];
        } else if (dataType.equals(ucar.ma2.DataType.LONG.toString())) {
            return new Long[size];
        } else if (dataType.equals(ucar.ma2.DataType.DOUBLE.toString())) {
            return new Double[size];
        } else if (dataType.equals(ucar.ma2.DataType.BOOLEAN.toString())) {
            return new Boolean[size];
        } else if (dataType.equals(ucar.ma2.DataType.BYTE.toString())) {
            return new Byte[size];
        } else {
            return new Object[size];
        }
    }


    /**
     * @return {@link NetcdfDataStoreDescriptor}
     */
    public NetcdfDataStoreDescriptor getDescriptor() {
        return this.descriptor;
    }


    /**
     * @param descriptor the DataStoreDescriptor
     */
    public void setDescriptor(NetcdfDataStoreDescriptor descriptor) {
        this.descriptor = descriptor;
    }


    /**
     * @return NetcdfFileWriteable the file
     */
    public NetcdfFileWriteable getNetcdffile() {
        return this.netcdffile;
    }


}
