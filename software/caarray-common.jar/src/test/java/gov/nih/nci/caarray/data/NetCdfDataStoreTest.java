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

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ucar.ma2.Array;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayFloat;
import ucar.ma2.Index;

/**
 * @author John Pike
 *
 * This test will create and test a file with the following structure:
 *
 * Col 1 -  name=xvalue; datatype="FLOAT"
 * Col 2 -  name=yvalue; datatype="FLOAT"
 * Col 3 -  name=pvalue; datatype="STRING"
 * Col 4 -  name=pct;    datatype="FLOAT"
 * It also presumes a row count of ROW_TEST_SIZE=900, and
 * allows String columns to be maxLength of S_VAR_LEN = 80.
 *
 */
public class NetCdfDataStoreTest {

    private static final Log LOG = LogFactory.getLog(NetCdfDataStoreTest.class);

    private static final String X_VAL_COL_NAME = "xvalue";

    private static final String Y_VAL_COL_NAME = "yvalue";

    private static final String PCT_COL_NAME = "pct";

    private static final String P_VAL_COL_NAME = "pvalue";
    private static final int COL_SIZE = 4;


    private static final int ROW_TEST_SIZE = 900;

    private static final int S_VAR_LEN = 80;

    private static final int TEST_VAL_1 = 100000;

    private static final int TEST_VAL_2 = 100;

    private static final int TEST_ROW = 10;

    private static final String FILENAME = "testNCDF.nc";

    private static final float TEST_FLOAT_VAL_1 = 9999;
    private static final String WOOHOO_STRING = "WOOHOO";

    /**
     *
     */
    @After
    public void deleteFile() {
        File file = new File(FILENAME);
        file = file.getAbsoluteFile();
        file.delete();

        File file2 = new File("test");
        file2 = file2.getAbsoluteFile();
        file2.delete();
    }

    /**
     *
     */
    @Before
    public void initFile() {
        NetcdfDataStoreDescriptor descriptor = createDescriptor();

        NetCdfDataStore netCdfDS = null;
        try {
            File file = new File(FILENAME).getAbsoluteFile();
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.createDataStore(descriptor, file);
            netCdfDS.createFile(ROW_TEST_SIZE);
            // here's where we actually load the file with data
            ArrayFloat floatArrayXVar = new ArrayFloat.D1(ROW_TEST_SIZE);
            ArrayFloat floatArrayYVar = new ArrayFloat.D1(ROW_TEST_SIZE);
            ArrayChar stringArrayPVar = new ArrayChar.D2(ROW_TEST_SIZE, S_VAR_LEN);
            ArrayFloat floatArrayPctVar = new ArrayFloat.D1(ROW_TEST_SIZE);
            loadFile(ROW_TEST_SIZE, floatArrayXVar, floatArrayYVar, stringArrayPVar, floatArrayPctVar);
            ArrayList<String> varList = new ArrayList<String>();
            for (int i = 0; i < descriptor.getColumns().size(); i++) {
                varList.add(descriptor.getColumns().get(i).getName());
            }
            ArrayList<Array> arrayList = new ArrayList<Array>();
            arrayList.add(floatArrayXVar);
            arrayList.add(floatArrayYVar);
            arrayList.add(stringArrayPVar);
            arrayList.add(floatArrayPctVar);
            netCdfDS.saveData(arrayList, varList);
            netCdfDS.closeFile();
        } catch (DataStoreException dse) {
            LOG.error("Exception init'ing file", dse);
        }
    }

    private NetcdfDataStoreDescriptor createDescriptor() {
        NetcdfDataStoreDescriptor descriptor = new NetcdfDataStoreDescriptor();
        List<Column> columns = new ArrayList<Column>();
        Column column = createColumn(X_VAL_COL_NAME, DataType.FLOAT);
        columns.add(column);
        column = createColumn(Y_VAL_COL_NAME, DataType.FLOAT);
        columns.add(column);
        column = createColumn(P_VAL_COL_NAME, DataType.CHAR);
        columns.add(column);
        column = createColumn(PCT_COL_NAME, DataType.FLOAT);
        columns.add(column);

        descriptor.setColumns(columns);
        return descriptor;
    }

    /**
     * @return
     */
    private Column createColumn(String name, DataType type) {
        Column column = new Column();
        column.setName(name);
        column.setType(type);
        return column;
    }

    /**
     * @param dataDim
     * @param floatArrayXVar
     * @param floatArrayYVar
     * @param stringArrayPVar
     * @param floatArrayPctVar
     */
    private void loadFile(int rowSize, ArrayFloat floatArrayXVar, ArrayFloat floatArrayYVar, ArrayChar stringArrayPVar,
            ArrayFloat floatArrayPctVar) {
        Index idx;
        for (int i = 0; i < rowSize; i++) {
            idx = floatArrayXVar.getIndex();
            floatArrayXVar.setFloat(idx.set(i), (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
        for (int i = 0; i < rowSize; i++) {
            idx = floatArrayYVar.getIndex();
            floatArrayYVar.setFloat(idx.set(i), (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
        for (int i = 0; i < rowSize; i++) {
            idx = stringArrayPVar.getIndex();
            stringArrayPVar.setString(i, "Test" + i);
        }
        for (int i = 0; i < rowSize; i++) {
            idx = floatArrayPctVar.getIndex();
            floatArrayPctVar.setFloat(idx.set(i), (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
    }


    /**
     *
     */
    @Test
    public void testSetValueFloat() {
        NetCdfDataStore netCdfDS = null;
        File file = new File(FILENAME);
        Column column = new Column();
        column.setName(X_VAL_COL_NAME);
        column.setType(DataType.FLOAT);
        DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
        try {
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
            netCdfDS.setValue(2, column, TEST_FLOAT_VAL_1);
            netCdfDS.closeFile();
        } catch (DataStoreException dse) {
            LOG.error("error", dse);
        }
    }

    /**
    *
    *
    */
   @Test
   public void testSetValueCHAR() {
       NetCdfDataStore netCdfDS = null;
       File file = new File(FILENAME);
       Column column = new Column();
       column.setName(P_VAL_COL_NAME);
       column.setType(DataType.CHAR);
       DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
       String value = null;
       try {
           netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
           netCdfDS.setValue(2, column, WOOHOO_STRING);
           netCdfDS.save(column);

           netCdfDS.closeFile();
           netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
           value = (String) netCdfDS.getValue(2, column);
       } catch (DataStoreException dse) {
           LOG.error("error", dse);
       }

       assertTrue(WOOHOO_STRING.equals(value));
   }

   /**
   *
   *
   */
  @Test
  public void testSetValueString() {
      NetCdfDataStore netCdfDS = null;
      File file = new File(FILENAME);
      Column column = new Column();
      column.setName(P_VAL_COL_NAME);
      column.setType(DataType.STRING);
      DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
      String value = null;
      try {
          netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
          netCdfDS.setValue(2, column, WOOHOO_STRING);
          netCdfDS.save(column);

          netCdfDS.closeFile();
          netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
          value = (String) netCdfDS.getValue(2, column);
      } catch (DataStoreException dse) {
          LOG.error("error", dse);
      }

      assertTrue(WOOHOO_STRING.equals(value));
  }

   /**
   * @throws DataStoreException exception
   */
  @Test(expected = DataStoreException.class)
  public void testSetValueStringInFloatColumn() throws DataStoreException {
      NetCdfDataStore netCdfDS = null;
      File file = new File(FILENAME);
      Column column = new Column();
      column.setName(X_VAL_COL_NAME);
      column.setType(DataType.FLOAT);
      DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();

      netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
      netCdfDS.setValue(2, column, WOOHOO_STRING);
      netCdfDS.closeFile();

  }


  /**
   * @throws DataStoreException exception
   */
  @Test(expected = DataStoreException.class)
  public void testSetValueBadFilename() throws DataStoreException {
      NetCdfDataStore netCdfDS = null;
      File file = new File("test");
      Column column = new Column();
      column.setName(X_VAL_COL_NAME);
      column.setType(DataType.FLOAT);
      DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();

      netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
      netCdfDS.setValue(2, column, WOOHOO_STRING);
      netCdfDS.closeFile();

  }
    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValue(int, gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValueFloat() {
        NetCdfDataStore netCdfDS = null;
        Object value = null;

        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(X_VAL_COL_NAME);
            column.setType(DataType.FLOAT);
            value = netCdfDS.getValue(TEST_ROW, column);
        } catch (Exception e) {
            LOG.error("Error in testGetValue", e);
        }
        assertNotNull(value);
        double testVal = Double.parseDouble(String.valueOf(value));
        assertTrue(testVal > 0);
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValue(int, gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValueCHAR() {
        NetCdfDataStore netCdfDS = null;
        Object value = null;

        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(P_VAL_COL_NAME);
            column.setType(DataType.CHAR);
            value = netCdfDS.getValue(TEST_ROW, column);
        } catch (Exception e) {
            LOG.error("Error in testGetValue", e);
        }
        assertNotNull(value);
        String testVal = (String) value;
        assertTrue(("Test" + TEST_ROW).equals(testVal));
    }


    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValue(int, gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValueString() {
        NetCdfDataStore netCdfDS = null;
        Object value = null;

        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(P_VAL_COL_NAME);
            column.setType(DataType.STRING);
            value = netCdfDS.getValue(TEST_ROW, column);
        } catch (Exception e) {
            LOG.error("Error in testGetValue", e);
        }
        assertNotNull(value);
        String testVal = (String) value;
        assertTrue(("Test" + TEST_ROW).equals(testVal));
    }
    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValues(gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValuesColumn() {
        NetCdfDataStore netCdfDS = null;
        Object[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(X_VAL_COL_NAME);
            column.setType(DataType.FLOAT);
            value = (Object[]) netCdfDS.getValues(column);
        } catch (Exception e) {
            LOG.error("Error in testGetValuesColumn", e);
        }
        assertNotNull(value);
        assertTrue(value.length == ROW_TEST_SIZE);
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValues(gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testSetColumnValuesString() {
        NetCdfDataStore netCdfDS = null;
        String[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(P_VAL_COL_NAME);
            column.setType(DataType.STRING);
            value = (String[]) netCdfDS.getValues(column);
            List<String> strList = new ArrayList<String>();
            ArrayList<String> colList = new ArrayList<String>();
            List<ArrayList<?>> listOfLists = new ArrayList<ArrayList<?>>();
            for (int i = 0; i < value.length; i++) {
                strList.add(i, (value[i]));
            }
            listOfLists.add((ArrayList<?>) strList);
            colList.add(P_VAL_COL_NAME);


            netCdfDS.saveColumnData(listOfLists, colList);

        } catch (Exception e) {
            LOG.error("Error in testGetValuesString", e);
        }
        assertNotNull(value);
        assertTrue(value.length == ROW_TEST_SIZE);
    }
    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValues(gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testSetColumnValuesFloat() {
        NetCdfDataStore netCdfDS = null;
        Float[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(X_VAL_COL_NAME);
            column.setType(DataType.FLOAT);
            value = (Float[]) netCdfDS.getValues(column);
            List<Float> floatList = new ArrayList<Float>();
            ArrayList<String> colList = new ArrayList<String>();
            List<ArrayList<?>> listOfLists = new ArrayList<ArrayList<?>>();
            for (int i = 0; i < value.length; i++) {
                floatList.add(i, new Float((value[i])));
            }
            listOfLists.add((ArrayList<?>) floatList);
            colList.add(X_VAL_COL_NAME);


            netCdfDS.saveColumnData(listOfLists, colList);

        } catch (Exception e) {
            LOG.error("Error in testGetValuesColumn", e);
        }
        assertNotNull(value);
        assertTrue(value.length == ROW_TEST_SIZE);
    }
    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValues(gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValuesColumnString() {
        NetCdfDataStore netCdfDS = null;
        Object[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(P_VAL_COL_NAME);
            column.setType(DataType.CHAR);
            value = (Object[]) netCdfDS.getValues(column);
        } catch (Exception e) {
            LOG.error("Error in testGetValuesColumnString", e);
        }
        assertNotNull(value);
        assertTrue(value.length == ROW_TEST_SIZE);
    }

    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValues(int)}.
     */
    @Test
    public void testGetValuesInt() {
        NetCdfDataStore netCdfDS = null;
        Object[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);
            value = netCdfDS.getValues(TEST_ROW);
        } catch (Exception e) {
            LOG.error("Error in testgetvaluesint", e);

        }
        assertNotNull(value);
        String testStr = (String) value[NetCdfDataStore.P_VAL_COL];
        String testVal = "Test" + TEST_ROW;
        assertTrue(testVal.equals(testStr));
        Float testFlt = (Float) value[NetCdfDataStore.PCT_VAL_COL];
        assertTrue(testFlt.floatValue() > TEST_VAL_1);
        assertTrue(value.length == COL_SIZE);

    }

}
