package gov.nih.nci.caarray.test.api.external.v1_0.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.InconsistentDataSetsException;

import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.util.List;

import org.junit.Test;

import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteOutputStream;


/**
 * Search service Test
 * @author dkokotov
 */
public class DataServiceTest extends AbstractExternalJavaApiTest {
    @Test
    public void testGetDataSet() throws InvalidReferenceException {
        DataSetRequest dataRequest = new DataSetRequest();

        HybridizationSearchCriteria hsc = new HybridizationSearchCriteria();
        hsc.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
        List<Hybridization> hybs = caArrayServer.getSearchService().searchForHybridizations(hsc, null);
        for (Hybridization hyb : hybs) {
            System.out.println("hyb: " + hyb);
            dataRequest.getHybridizations().add(new CaArrayEntityReference(hyb.getId()));
        }
        
        for (int i = 16; i <= 22; i++) {
            CaArrayEntityReference qRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:" + i);                                 
            dataRequest.getQuantitationTypes().add(qRef);
        }

        try {
            DataSet dataSet = caArrayServer.getDataService().getDataSet(dataRequest);
            assertEquals(0, dataSet.getDesignElements().size());
            assertEquals(7, dataSet.getQuantitationTypes().size());
            assertEquals(12, dataSet.getDatas().size());
            for (HybridizationData hdata : dataSet.getDatas()) {
                assertEquals(7, hdata.getDataColumns().size());            
                for (AbstractDataColumn dataColumn : hdata.getDataColumns()) {
                    // do number vals assertion
                }
            }
        } catch (InconsistentDataSetsException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void testStreamFileContents() throws RemoteException {
        CaArrayEntityReference fileRef = new CaArrayEntityReference(
                "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:2"); 
        RemoteOutputStreamServer ostream = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ostream = new SimpleRemoteOutputStream(bos);
            DataFile df = caArrayServer.getDataService().streamFileContents(fileRef, false, ostream.export());
            assertEquals("H_TK6 MDR1 replicate 2.CEL", df.getName());
            assertEquals(162483, df.getUncompressedSize());
            byte[] data = bos.toByteArray();
            assertEquals(df.getUncompressedSize(), data.length);
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        } catch (DataTransferException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }        
    }
    
    @Test
    public void testExportMageTab()  {
        CaArrayEntityReference experimentRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1");
        try {
            MageTabFileSet mageTabSet = caArrayServer.getDataService().exportMageTab(experimentRef);
            assertEquals("foo.idf", mageTabSet.getIdf().getFileMetadata().getName());
            assertEquals("foo.sdrf", mageTabSet.getSdrf().getFileMetadata().getName());
            assertEquals(12, mageTabSet.getDataFiles().size());
        } catch (InvalidReferenceException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        } catch (DataTransferException e) {
            e.printStackTrace();
            fail("Unexpected exception: " + e);
        }
    }
}
