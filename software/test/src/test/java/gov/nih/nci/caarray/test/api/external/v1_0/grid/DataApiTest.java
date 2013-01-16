//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api.external.v1_0.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.axis.AxisFault;
import org.apache.axis.utils.JavaUtils;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.junit.Test;

/**
 * @author gax
 */
public class DataApiTest extends AbstractExternalGridApiTest {

    @Test
    public void testGetDataSet_checkRequest() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetDataSet_checkRequest");
        logForSilverCompatibility(TEST_OUTPUT, "Null");
        DataSetRequest dataRequest = null;
        try {
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail("Expected InvalidInputFault");
        } catch(InvalidInputFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "null validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "Empty");
        dataRequest = new DataSetRequest();
        try {
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail("Expected InvalidInputFault");
        } catch(AxisFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadHib");
        dataRequest = new DataSetRequest();
        String qtid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:24";
        dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference(qtid)));
        try {
            dataRequest.setHybridizations(Collections.singleton(new CaArrayEntityReference("foo hib")));
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail("Expected InvalidReferenceFault");
        } catch(InvalidReferenceFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadDataFiles");
        dataRequest = new DataSetRequest();
        dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference(qtid)));
        try {
            dataRequest.setDataFiles(Collections.singleton(new CaArrayEntityReference("foo df")));
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail("Expected InvalidReferenceFault");
        } catch(InvalidReferenceFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadQuantitationTypes");
        dataRequest = new DataSetRequest();
        String hid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1";
        dataRequest.setHybridizations(Collections.singleton(new CaArrayEntityReference(hid)));
        dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference("foo qt")));
        try {
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail("Expected InvalidReferenceFault");
        } catch(InvalidReferenceFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }
    }

    @Test
    public void testGetDataSet_NonDataFile() throws Exception {
        gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault.class.getName();
        logForSilverCompatibility(TEST_NAME, "testGetDataSet_NonDataFile");
        Class<?> c = gov.nih.nci.caarray.external.v1_0.vocabulary.Term.class;
        assertFalse(c.isAssignableFrom(CaArrayFile.class));
        DataSetRequest dataRequest = new DataSetRequest();
        String dfid = "URN:LSID:" + c.getName()+ ":1";
        dataRequest.setDataFiles(Collections.singleton(new CaArrayEntityReference(dfid)));
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference(qtid)));
        try {
            gridClient.getDataSet(dataRequest);
            fail("expected an IncorrectEntityTypeFault");
        } catch(IncorrectEntityTypeFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.getCause());
        }
        
    }

    @Test
    public void testGetDataSet_Hybridizations1() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testGetDataSet_Hybridizations1");
        DataSetRequest dataRequest = new DataSetRequest();
        String hid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1";
        dataRequest.setHybridizations(Collections.singleton(new CaArrayEntityReference(hid)));
        String qtid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:24";
        dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference(qtid)));
        DataSet ds = gridClient.getDataSet(dataRequest);
        List<HybridizationData> l =  ds.getDatas();
        assertEquals(1, l.size());
        HybridizationData hd = l.get(0);
        assertEquals(1, hd.getDataColumns().size());
        assertEquals(qtid, hd.getDataColumns().get(0).getQuantitationType().getId());
        String resultId = hd.getHybridization().getId();
        assertEquals(hid, resultId);

    }


    //////////////////

    @Test
    public void testStreamFileContents_BadRef() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamFileContents_BadRef");
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:0";
        CaArrayEntityReference fileRef = new CaArrayEntityReference(dfid);
        try {
            TransferServiceContextReference ris =  gridClient.getFileContentsTransfer(fileRef, true);
        } catch(NoEntityMatchingReferenceFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.getCause());
        }
    }

    @Test
    public void testStreamFileContents_Compressed() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamFileContents_Compressed");
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:1";
        CaArrayEntityReference fileRef = new CaArrayEntityReference(dfid);
        TransferServiceContextReference ris =  gridClient.getFileContentsTransfer(fileRef, true);
        InputStream is = toStream(ris);
        GZIPInputStream zin = new GZIPInputStream(is);
        byte[] b = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.md5(zin);
        String hash = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.toHex(b);
        logForSilverCompatibility(TEST_OUTPUT, dfid + "MD5 hash :" + hash);
        assertEquals("7d875adfcee0e18b0cc28b1311db40ae", hash);
    }

    @Test
    public void testStreamFileContents_UnCompressed() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamFileContents_UnCompressed");
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.File:1";
        CaArrayEntityReference fileRef = new CaArrayEntityReference(dfid);
        TransferServiceContextReference ris =  gridClient.getFileContentsTransfer(fileRef, false);
        InputStream is = toStream(ris);
        try {
            GZIPInputStream zin = new GZIPInputStream(is);
            zin.read();
            logForSilverCompatibility(TEST_OUTPUT, "data was gziped");
            fail("zipped");
        } catch(IOException e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e);
        }
    }

    ////////////////////////////////


    @Test
    public void testExportMageTab_badRef() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testExportMageTab_badRef");
        String qtid = "URN:LSID:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:1";
        CaArrayEntityReference badRef = new CaArrayEntityReference(qtid);
        try {
            gridClient.getMageTabExport(badRef);
        } catch(IncorrectEntityTypeFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e);
        }
    }

    @Test
    public void testExportMageTab_1Ex() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testExportMageTab_1Ex");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:"+type+":1";
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);
        MageTabFileSet df = gridClient.getMageTabExport(ref);
        logForSilverCompatibility(TEST_OUTPUT, "exteriment has " + df.getDataFiles().size() + " data files");
        assertEquals(19, df.getDataFiles().size());
        assertEquals("admin-00001.idf", df.getIdf().getMetadata().getName());
        assertEquals("admin-00001.sdrf", df.getSdrf().getMetadata().getName());

        Map<String, String> expected = new HashMap<String, String>();
        expected.put("8kNew111_14v1p4m12.gpr", "84823097c0db8d62028cfaa896a14ff9");
        expected.put("8kNew111_14_4601_m84.gpr", "a563783e47fe4ef352f82ac1731f848b");
        expected.put("8kNew111_17m12701m36.gpr", "3aaeb9ef790bf2867750913070008f7a");
        expected.put("8kNew111_17_4601_m82.gpr", "9879a28ea8d11698d7c7ed27b9ef7f73");
        expected.put("8kNewPr111_14v1p4m11.gpr", "97df92ef864ff3edb04fffb418ea842b");
        expected.put("8kNewPr111_14v1p4m13.gpr", "2b2bb27b31c19d7a60c809148f807bb5");
        expected.put("8kNewPr111_14v1p4m14.gpr", "15b6a2d3dbb0bb01e3d3c7fc50a7e331");
        expected.put("8kNewPr111_1712701m37.gpr", "b081876b46410e6bdd9749fa9bd82bbe");
        expected.put("8kPr111_17M62103100p1090600.gpr", "cb481dadbcf00e73ab6ed9566e8a95fb");
        expected.put("8KReverNewC2_111_4601_m86.gpr", "5a38e79b403e3d0dad8053e9eda7c19c");
        expected.put("8kReversNew17_111_4601_m83.gpr", "111722aa71d45af97810f82b5715c745");
        expected.put("G_New8k111C131501m73.gpr", "eedecb1b0ea2cdfe7e6fa9a3dcf335d1");
        expected.put("New8kPr111_C221601m29.gpr", "738385806286014ae5081eb0267b70fd");
        expected.put("New8kPr111_C221601m30.gpr", "85176edd715c326005c0a2721261550c");
        expected.put("New8kPr111_C221601m31.gpr", "8954bf4a8b6dee62eab34b6eb5262ebf");
        expected.put("VG_1New8k111_C14401m78.gpr", "a2532abf56b767231492224c1ff0616f");
        expected.put("VG_8k1New111_C14401m79.gpr", "56c72116692e5888f3737991b5b41bef");
        expected.put("VG_8k3New111_C14401m80.gpr", "cedff2aa6707bc8518578d0b177cd0a5");
        expected.put("VG_8k4New111_C14401m81.gpr", "f528f00fc219062a641cff653d08456");

        for (File f : df.getDataFiles()) {
            String exHash = expected.get(f.getMetadata().getName());
            assertNotNull(exHash);
            TransferServiceContextReference ris =  gridClient.getFileContentsTransfer(f.getReference(), true);
            InputStream is = toStream(ris);
            GZIPInputStream zin = new GZIPInputStream(is);
            byte[] b = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.md5(zin);
            String hash = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.toHex(b);
            assertEquals(exHash, hash);
        }
    }

    @Test
    public void testExportMageTab_2Ex() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamMageTab_2Ex");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:"+type+":2";
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);
        MageTabFileSet df = gridClient.getMageTabExport(ref);
        assertEquals(0, df.getDataFiles().size());
        assertEquals("admin-00002.idf", df.getIdf().getMetadata().getName());
        assertEquals("admin-00002.sdrf", df.getSdrf().getMetadata().getName());
    }

    private static InputStream toStream(TransferServiceContextReference transferRef) throws Exception {
        TransferServiceContextClient tclient = new TransferServiceContextClient(transferRef.getEndpointReference());
        DataTransferDescriptor dtd = tclient.getDataTransferDescriptor();
        return TransferClientHelper.getData(dtd);
    }

    private static void assertContains(AxisFault f, String msg) {
        String s = JavaUtils.stackToString(f);
        assertTrue(s, s.indexOf(msg) > 0);
    }
}
