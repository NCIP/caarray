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
import java.io.IOException;
import java.util.Iterator;
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
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;


/**
 * @author John Pike
 *
 */
public class NetCdfDataStoreTest {

    private static final Log LOG = LogFactory.getLog(NetCdfDataStoreTest.class);
    private static final int COL_SIZE = 4;
    private static final int ROW_TEST_SIZE = 900;

    private static final int S_VAR_LEN = 80;
    private static final int TEST_VAL_1 = 100000;
    private static final int TEST_VAL_2 = 100;
    private static final int TEST_ROW = 10;

    private static final String FILENAME = "testNCDF.nc";

    /**
    *
    */
   @After
   public void deleteFile() {
       File file = new File(FILENAME);
       file = file.getAbsoluteFile();
       file.delete();
   }

   /**
     *
     */
    @Before
    public void initFile() {

        NetcdfFileWriteable ncfile = null;
        NetcdfDataStoreDescriptor descriptor = new NetcdfDataStoreDescriptor();

        try {
        File file = new File(FILENAME);
        file = file.getAbsoluteFile();
        ncfile = NetcdfFileWriteable.createNew(file.getPath(), false);
        Dimension dataDim = ncfile.addDimension("data", ROW_TEST_SIZE);
        Dimension svarLen = ncfile.addDimension("svar_len", S_VAR_LEN);
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
            ncfile.addVariable(column.getName(), column.getType(), dimListToAdd);
        }
        createFile(ncfile);
        ArrayFloat floatArrayXVar = new ArrayFloat.D1(dataDim.getLength());
        ArrayFloat floatArrayYVar = new ArrayFloat.D1(dataDim.getLength());
        ArrayChar stringArrayPVar = new ArrayChar.D2(dataDim.getLength(), svarLen.getLength());
        ArrayFloat floatArrayPctVar = new ArrayFloat.D1(dataDim.getLength());
        loadFile(dataDim, floatArrayXVar, floatArrayYVar, stringArrayPVar, floatArrayPctVar);

        writeDataToFile(ncfile, floatArrayXVar, floatArrayYVar, stringArrayPVar, floatArrayPctVar);
        } catch (InvalidRangeException ie) {
            LOG.error("Error writing file", ie);
        } catch (Exception e) {
            LOG.error("Caught Exception", e);
        }
        closeFile(ncfile);

    }
    /**
     * @param ncfile
     * @param floatArrayXVar
     * @param floatArrayYVar
     * @param stringArrayPVar
     * @param floatArrayPctVar
     * @throws InvalidRangeException
     */
    private void writeDataToFile(NetcdfFileWriteable ncfile, ArrayFloat floatArrayXVar,
            ArrayFloat floatArrayYVar, ArrayChar stringArrayPVar, ArrayFloat floatArrayPctVar)
            throws InvalidRangeException {
        writeFile(ncfile, floatArrayXVar, NetcdfDataStoreDescriptor.X_VAL_COL_NAME);
        writeFile(ncfile, floatArrayYVar, NetcdfDataStoreDescriptor.Y_VAL_COL_NAME);
        writeFileStrings(ncfile, stringArrayPVar, NetcdfDataStoreDescriptor.P_VAL_COL_NAME);
        writeFile(ncfile, floatArrayPctVar, NetcdfDataStoreDescriptor.PCT_COL_NAME);
    }
    /**
     * @param dataDim
     * @param floatArrayXVar
     * @param floatArrayYVar
     * @param stringArrayPVar
     * @param floatArrayPctVar
     */
    private void loadFile(Dimension dataDim, ArrayFloat floatArrayXVar, ArrayFloat floatArrayYVar,
            ArrayChar stringArrayPVar, ArrayFloat floatArrayPctVar) {
        Index idx;
        for (int i = 0; i < dataDim.getLength(); i++) {
            idx = floatArrayXVar.getIndex();
            floatArrayXVar.setFloat(idx.set(i), (float) (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
        for (int i = 0; i < dataDim.getLength(); i++) {
            idx = floatArrayYVar.getIndex();
            floatArrayYVar.setFloat(idx.set(i), (float) (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
        for (int i = 0; i < dataDim.getLength(); i++) {
            idx = stringArrayPVar.getIndex();
            stringArrayPVar.setString(i, "Test" + i);
        }
        for (int i = 0; i < dataDim.getLength(); i++) {
            idx = floatArrayPctVar.getIndex();
            floatArrayPctVar.setFloat(idx.set(i), (float) (i * TEST_VAL_1 + i * TEST_VAL_2));
        }
    }
    /**
     * @param ncfile
     * @param testFloats
     * @throws InvalidRangeException
     */
    private void writeFile(NetcdfFileWriteable ncfile, ArrayFloat testFloats, String varName)
        throws InvalidRangeException {
        try {
            ncfile.write(varName, testFloats);
        } catch (IOException e) {
            LOG.error("ERROR writing file");
        }
    }


    private void writeFileStrings(NetcdfFileWriteable ncfile, Array testStrings, String varName)
        throws InvalidRangeException {
        try {
            ncfile.write(varName, (ArrayChar.D2) testStrings);
          //  ncfile.writeStringData(varName, testStrings);
        } catch (IOException e) {
            LOG.error("ERROR writing file");
        }
    }
    /**
     * Test method for {@link gov.nih.nci.caarray.data.NetCdfDataStore#getValue(int, gov.nih.nci.caarray.data.Column)}.
     */
    @Test
    public void testGetValue() {
        NetCdfDataStore netCdfDS = null;
        Object value = null;

        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(NetcdfDataStoreDescriptor.X_VAL_COL_NAME);
            column.setType(ucar.ma2.DataType.FLOAT);
            value = netCdfDS.getValue(TEST_ROW, column);
        } catch (Exception e) {
            LOG.error("Error in testGetValue", e);
        }
        assertNotNull(value);
        double testVal = Double.parseDouble(String.valueOf(value));
        assertTrue(testVal > 0);
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
            column.setName(NetcdfDataStoreDescriptor.X_VAL_COL_NAME);
            column.setType(ucar.ma2.DataType.FLOAT);
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
    public void testGetValuesColumnString() {
        NetCdfDataStore netCdfDS = null;
        Object[] value = null;
        try {
            File file = new File(FILENAME);
            DataStoreFactory factory = NetcdfDataStoreFactory.getInstance();
            netCdfDS = (NetCdfDataStore) factory.getDataStore(file);

            Column column = new Column();
            column.setName(NetcdfDataStoreDescriptor.P_VAL_COL_NAME);
            column.setType(ucar.ma2.DataType.CHAR);
            value = (Object[]) netCdfDS.getValues(column);
        } catch (Exception e) {
            LOG.error("Error in testGetValuesColumn", e);
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



    /**
     * @param ncfile
     */
    private void closeFile(NetcdfFileWriteable ncfile) {
        try {
            ncfile.close();
        } catch (IOException e) {
            LOG.error("ERROR closing file");
        }
    }


    /**
     * @param ncfile
     */
    private void createFile(NetcdfFileWriteable ncfile) {
        try {
            ncfile.create();
        } catch (IOException e) {
            LOG.error("Error creating file");
        }
    }

}
