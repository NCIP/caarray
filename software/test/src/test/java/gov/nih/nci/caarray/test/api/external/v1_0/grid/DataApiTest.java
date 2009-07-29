/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The test
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This test Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the test Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the test Software; (ii) distribute and
 * have distributed to and by third parties the test Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.test.api.external.v1_0.grid;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import static org.junit.Assert.*;

import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.DataStagingFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import java.io.IOException;
import java.io.InputStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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
            fail();
        } catch(AxisFault e) {
            assertContains(e, IllegalArgumentException.class.getName());
            logForSilverCompatibility(TEST_OUTPUT, "null validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "Empty");
        dataRequest = new DataSetRequest();
        try {
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail();
        } catch(AxisFault e) {
            assertContains(e, IllegalArgumentException.class.getName());
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadHib");
        dataRequest = new DataSetRequest();
        try {
            dataRequest.setHybridizations(Collections.singleton(new CaArrayEntityReference("foo hib")));
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail();
        } catch(AxisFault e) {
            assertContains(e, IllegalArgumentException.class.getName());
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadDataFiles");
        dataRequest = new DataSetRequest();
        try {
            dataRequest.setDataFiles(Collections.singleton(new CaArrayEntityReference("foo df")));
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail();
        } catch(AxisFault e) {
            assertContains(e, IllegalArgumentException.class.getName());
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e.dumpToString());
        }

        logForSilverCompatibility(TEST_OUTPUT, "BadQuantitationTypes");
        dataRequest = new DataSetRequest();
        try {
            dataRequest.setQuantitationTypes(Collections.singleton(new CaArrayEntityReference("foo qt")));
            gridClient.getDataSet(dataRequest);
            logForSilverCompatibility(TEST_OUTPUT, "unexpected validation outcome");
            fail();
        } catch(AxisFault e) {
            assertContains(e, IllegalArgumentException.class.getName());
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
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:0";
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
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:1";
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
        String dfid = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:1";
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
    public void testStreamFileContentsZip_2() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamFileContentsZip");
        String dfid1 = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:1";
        String dfid2 = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:2";
        CaArrayEntityReference fileRef1 = new CaArrayEntityReference(dfid1);
        CaArrayEntityReference fileRef2 = new CaArrayEntityReference(dfid2);
        FileDownloadRequest fdr = new FileDownloadRequest();
        fdr.getFiles().add(fileRef1);
        fdr.getFiles().add(fileRef2);
        TransferServiceContextReference ris =  gridClient.getFileContentsZipTransfer(fdr, true);
        InputStream is = toStream(ris);
        ZipInputStream zin = new ZipInputStream(is);
        ZipEntry ze = zin.getNextEntry();
        byte[] b = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.md5(zin);
        long sz = ze.getSize();
        assertEquals(114521L, ze.getSize());
        ze = zin.getNextEntry();
        assertEquals(682885L, ze.getSize());
        logForSilverCompatibility(TEST_OUTPUT, "2 zip entries");
    }

    @Test
    public void testStreamFileContentsZip_0() throws Exception {
        logForSilverCompatibility(TEST_NAME, "testStreamFileContentsZip_0");
        FileDownloadRequest fdr = new FileDownloadRequest();
        // will fail to create an empty zip.
        try {
            TransferServiceContextReference ris =  gridClient.getFileContentsZipTransfer(fdr, false);
        } catch(DataStagingFault e) {
            logForSilverCompatibility(TEST_OUTPUT, "validation :" + e);
        }
    }

    ///////////////////////////////////////////////////

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
    }

    /////////////////////////////////////////////////
    @Test
    public void testStreamMageTabZip_2() throws Exception {
        int id = 1;
        logForSilverCompatibility(TEST_NAME, "testStreamMageTabZip_2");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:"+type+":"+id;
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);

        TransferServiceContextReference ris =  gridClient.getMageTabZipTransfer(ref, false);
        InputStream is = toStream(ris);
        ZipInputStream zin = new ZipInputStream(is);
        ZipEntry ze = zin.getNextEntry();
        HashMap<String, String> expected = new HashMap<String, String>();
        expected.put("admin-0000"+id+".idf", "<synthetic>");//?? changes  dynamic?
        expected.put("admin-0000"+id+".sdrf", "<synthetic>");

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



        while (ze != null) {
            String exHash = expected.get(ze.getName());
            assertNotNull("unexpected file "+ze.getName(), exHash);
            if (!"<synthetic>".equals(exHash)) {
                byte[] b = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.md5(zin);
                String hash = gov.nih.nci.caarray.test.api.external.v1_0.java.DataServiceTest.toHex(b);
                logForSilverCompatibility(TEST_OUTPUT, "file \""+ze.getName()+"\", MD5 \""+hash+"\"");
                assertEquals("hash for "+ze.getName(), exHash, hash);
            }
            expected.remove(ze.getName());
            ze = zin.getNextEntry();
        }
        assertTrue("some didnt make it through :"+expected.keySet().toString(), expected.isEmpty());
    }

    @Test
    public void testStreamMageTabZip_0() throws Exception {
        int id = 2;
        logForSilverCompatibility(TEST_NAME, "testStreamMageTabZip_0");
        String type = gov.nih.nci.caarray.external.v1_0.experiment.Experiment.class.getName();
        String eid = "URN:LSID:"+type+":"+id;
        CaArrayEntityReference ref = new CaArrayEntityReference(eid);
        TransferServiceContextReference ris =  gridClient.getMageTabZipTransfer(ref, false);
        InputStream is = toStream(ris);
        ZipInputStream zin = new ZipInputStream(is);
        ZipEntry ze = zin.getNextEntry();
        assertEquals("admin-0000"+id+".idf", ze.getName());
        ze = zin.getNextEntry();
        assertEquals("admin-0000"+id+".sdrf", ze.getName());
        ze = zin.getNextEntry();
        logForSilverCompatibility(TEST_OUTPUT, "2 zip entries");
        assertNull("empty experiment.  should only have 2 entries", ze);
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
