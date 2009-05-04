/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.example.external.v1_0.grid;


import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.KeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.soap.SOAPElement;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.wsrf.encoding.ObjectDeserializer;

/**
 * A simple class that connects to the remote Java API of a caArray server and retrieves and
 * prints a list of<code>QuantitationTypes</code>.
 */
@SuppressWarnings("PMD")
public class GridApiExample {

    private static final String DEFAULT_SERVER = "array.nci.nih.gov";
    private static final int DEFAULT_GRID_SERVICE_PORT = 80;

    private static final String TEST_FILE_TYPE_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AGILENT_CSV";
    private static final String TEST_ORGANISM_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:1";
    private static final String TEST_EXPERIMENT_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1";
    private static final String TEST_HYB1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:89";
    private static final String TEST_HYB2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:93";
    private static final String TEST_BIOMATERIAL1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:230";
    private static final String TEST_BIOMATERIAL2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:246";
    private static final String TEST_DATAFILE1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:16";
    private static final String TEST_DATAFILE2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:17";
    private static final String TEST_DATAFILE3_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.DataFile:18";
 
    private String hostname = DEFAULT_SERVER;
    private int port = DEFAULT_GRID_SERVICE_PORT;
    private String url;

    public static void main(String[] args) {
        GridApiExample client = new GridApiExample();
        if (args.length == 2) {
            client.hostname = args[0];
            client.port = Integer.parseInt(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage ApiClientTest [hostname port]");
            System.exit(1);
        }
        client.url = "http://" + client.hostname + ":" + client.port + "/wsrf/services/cagrid/CaArraySvc_v1_0";
        System.out.println("Using URL: " + client.url);
        client.runTest();
    }

    /**
     * Downloads data using the caArray Remote Java API.
     */
    public void runTest() {
        CaArraySvc_v1_0Client client;
        try {
            client = new CaArraySvc_v1_0Client(url);
            

            StopWatch sw = new StopWatch();
            
            Organism exampleOrg = new Organism();
            exampleOrg.setCommonName("house");
            List<Organism> mouseOrgs = client.searchByExample(
                    new ExampleSearchCriteria<Organism>(exampleOrg, MatchMode.ANYWHERE)).getResults();
            System.out.println("Mus orgs: " + mouseOrgs);

            // enumerateExperiments test
            ExperimentSearchCriteria experimentCrit = new ExperimentSearchCriteria();
            experimentCrit.setAssayType(new CaArrayEntityReference("URN:LSID:gov.nih.nci.caarray.external.v1_0.array.AssayType:2"));            
            experimentCrit.setArrayProvider(new CaArrayEntityReference("URN:LSID:gov.nih.nci.caarray.external.v1_0.array.ArrayProvider:1"));            
            System.out.println("Experiment Criteria Enum Search");

            EnumerationResponseContainer expEnum = client.enumerateExperiments(experimentCrit);
            ClientEnumIterator iter = EnumerationResponseHelper.createClientIterator(expEnum, CaArraySvc_v1_0Client.class
                    .getResourceAsStream("client-config.wsdd"));
            IterationConstraints ic = new IterationConstraints(5, -1, null);
            iter.setIterationConstraints(ic);
            while (iter.hasNext()) {
                try {
                    SOAPElement elem = (SOAPElement) iter.next();
                    if (elem != null) {
                        java.lang.Object o = ObjectDeserializer.toObject(elem, Experiment.class);
                        System.out.println("Next experiment: " + o);
                    }
                } catch (NoSuchElementException e) {
                    break;
                }
            }

            // experiment keyword search test
            KeywordSearchCriteria experimentKeywordCrit = new KeywordSearchCriteria();
            experimentKeywordCrit.setKeyword("MDR");
            Experiment[] keywordExps = client.searchForExperimentsByKeyword(experimentKeywordCrit);
            System.out.println("Experiments by keyword criteria: " + Arrays.asList(keywordExps));

            // biomaterial keyword search test
            BiomaterialKeywordSearchCriteria sampleKeywordCrit = new BiomaterialKeywordSearchCriteria();
            sampleKeywordCrit.setKeyword("MDR");
            Biomaterial[] keywordSamples = client.searchForBiomaterialsByKeyword(sampleKeywordCrit);
            System.out.println("Samples by keyword criteria: " + Arrays.asList(keywordSamples));

            // ------------------ FILE DATA RETRIEVAL TESTS
            FileDownloadRequest fileReq = new FileDownloadRequest();
            CaArrayEntityReference fileRef1 = new CaArrayEntityReference(
                    TEST_DATAFILE1_ID);
            CaArrayEntityReference fileRef2 = new CaArrayEntityReference(
                    TEST_DATAFILE2_ID);
            CaArrayEntityReference fileRef3 = new CaArrayEntityReference(
                    TEST_DATAFILE3_ID);
            fileReq.getFiles().add(fileRef1);
            fileReq.getFiles().add(fileRef2);
            fileReq.getFiles().add(fileRef3);

            // single ref, uncompressed
            System.out.println("Retrieving one file using uncompressed transfer ref");
            sw.start();
            TransferServiceContextReference transferRef = client.getFileContentsTransfer(fileRef1, false);
            if (transferRef != null) {
                logAndSaveFile(transferRef, false);
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // zip, compression in ZIP
            System.out.println("Retrieving files using ZIP of files, compressing overall zip");
            sw.reset();
            sw.start();
            TransferServiceContextReference zipRef = client.getFileContentsZipTransfer(fileReq, false);
            if (zipRef != null) {
                TransferServiceContextClient zipTransferClient = new TransferServiceContextClient(zipRef
                        .getEndpointReference());
                // use the TransferClientHelper to get an InputStream to the data
                ZipInputStream zis = new ZipInputStream(TransferClientHelper.getData(zipTransferClient
                        .getDataTransferDescriptor()));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null && zis.available() > 0) {
                    System.out.println("Contents of file " + entry.getName() + ", size " + entry.getSize());
                    saveFile(zis, entry.getName(), false, false);
                    entry = zis.getNextEntry();
                }
                zis.close();
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // zip, compression in individual files
            System.out.println("Retrieving files using ZIP of files, individual files");
            sw.reset();
            sw.start();
            zipRef = client.getFileContentsZipTransfer(fileReq, true);
            if (zipRef != null) {
                TransferServiceContextClient zipTransferClient = new TransferServiceContextClient(zipRef
                        .getEndpointReference());
                // use the TransferClientHelper to get an InputStream to the data
                ZipInputStream zis = new ZipInputStream(TransferClientHelper.getData(zipTransferClient
                        .getDataTransferDescriptor()));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null && zis.available() > 0) {
                    System.out.println("Contents of file " + entry.getName() + ", size " + entry.getSize());
                    GZIPInputStream gzis = new GZIPInputStream(zis);
                    saveFile(gzis, entry.getName(), false, false);
                    entry = zis.getNextEntry();
                }
                zis.close();
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // array of references, uncompressed
            System.out.println("Retrieving files using array of uncompressed transfer refs");
            sw.reset();
            sw.start();
            TransferServiceContextReference[] transferRefs = client.getFileContentsTransfers(fileReq, false);
            for (TransferServiceContextReference tref : transferRefs) {
                logAndSaveFile(tref, false);
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // array of references, compressed
            System.out.println("Retrieving files using array of compressed transfer refs");
            sw.reset();
            sw.start();
            transferRefs = client.getFileContentsTransfers(fileReq, true);
            for (TransferServiceContextReference tref : transferRefs) {
                logAndSaveFile(tref, true);
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // enumeration of uncompressed refs
            System.out.println("Retrieving files using enumeration of uncompressed transfer refs");
            sw.reset();
            sw.start();
            EnumerationResponseContainer transferEnum = client.enumerateFileContentTransfers(fileReq, false);
            ClientEnumIterator transferIter = EnumerationResponseHelper.createClientIterator(transferEnum,
                    CaArraySvc_v1_0Client.class.getResourceAsStream("client-config.wsdd"));
            transferIter.setIterationConstraints(new IterationConstraints(1, -1, null));
            while (transferIter.hasNext()) {
                try {
                    SOAPElement elem = (SOAPElement) transferIter.next();
                    if (elem != null) {
                        TransferServiceContextReference tref = (TransferServiceContextReference) ObjectDeserializer
                                .toObject(elem, TransferServiceContextReference.class);
                        logAndSaveFile(tref, false);
                    }
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            // enumeration of compressed refs
            System.out.println("Retrieving files using enumeration of compressed transfer refs");
            sw.reset();
            sw.start();
            transferEnum = client.enumerateFileContentTransfers(fileReq, true);
            transferIter = EnumerationResponseHelper.createClientIterator(transferEnum,
                    CaArraySvc_v1_0Client.class.getResourceAsStream("client-config.wsdd"));
            transferIter.setIterationConstraints(new IterationConstraints(1, -1, null));
            while (transferIter.hasNext()) {
                try {
                    SOAPElement elem = (SOAPElement) transferIter.next();
                    if (elem != null) {
                        TransferServiceContextReference tref = (TransferServiceContextReference) ObjectDeserializer
                                .toObject(elem, TransferServiceContextReference.class);
                        logAndSaveFile(tref, true);
                    }
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            sw.stop();
            System.out.println("Time: " + sw.toString());

            
            DataSetRequest dataRequest = new DataSetRequest();

            // hybridization search test
            HybridizationSearchCriteria hsc = new HybridizationSearchCriteria();
            hsc.getBiomaterials().add(
                    new CaArrayEntityReference(
                            TEST_BIOMATERIAL1_ID));
            hsc.getBiomaterials().add(
                    new CaArrayEntityReference(
                            TEST_BIOMATERIAL2_ID));
            Hybridization[] hybs = client.searchForHybridizations(hsc);
            System.out.println("Hyb search by creteria: ");
            for (Hybridization hyb : hybs) {
                System.out.println("hyb: " + hyb);
                // dataRequest.getHybridizations().add(hyb.getReference());
            }

            // biomaterial search test
            BiomaterialSearchCriteria bsc = new BiomaterialSearchCriteria();
            bsc.setExperiment(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID));
            bsc.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
            System.out.println("Biomaterial search by creteria: ");
            Biomaterial[] bms = client.searchForBiomaterials(bsc);
            for (Biomaterial bm : bms) {
                System.out.println("BM: " + bm);
            }

            // file search test
            FileSearchCriteria fileCriteria = new FileSearchCriteria();
            fileCriteria.setExperiment(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID));
            fileCriteria.getBiomaterials().add(new CaArrayEntityReference(TEST_BIOMATERIAL2_ID));
            DataFile[] files = client.searchForFiles(fileCriteria);
            for (DataFile file : files) {
                System.out.println("File Metadata: " + file);                        
            }
            dataRequest.getDataFiles().add(files[0].getReference());

            for (int i = 16; i <= 22; i++) {
                CaArrayEntityReference qRef = new CaArrayEntityReference(
                        "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:"
                                + i);
                if (i <= 18) {
                    dataRequest.getQuantitationTypes().add(qRef);                            
                }
            }
            
            QuantitationTypeSearchCriteria qtCriteria = new QuantitationTypeSearchCriteria();
            qtCriteria.getFileTypeCategories().add(FileTypeCategory.RAW);
            qtCriteria.getFileTypeCategories().add(FileTypeCategory.DERIVED);
            qtCriteria.setHybridization(hybs[0].getReference());
            QuantitationType[] quantitationTypes = client.searchForQuantitationTypes(qtCriteria);
            System.out.println("Quantitation Types from search");
            for (QuantitationType qt : quantitationTypes) {
                System.out.println("QT: " + qt);
                //dataRequest.getQuantitationTypes().add(qt.getReference());
            }            

            // parsed data retrieval test
            sw.reset();
            sw.start();
            DataSet dataSet = client.getDataSet(dataRequest);
            sw.stop();
            System.out.println("Time: " + sw.toString());

            System.out.println("Design element list: " + dataSet.getDesignElements());
            System.out.println("Quantitation types: " + dataSet.getQuantitationTypes());
            for (HybridizationData hdata : dataSet.getDatas()) {
                System.out.println("Data for hyb ");
                for (AbstractDataColumn dataColumn : hdata.getDataColumns()) {
                    System.out.println("Data column of type: " + dataColumn.getQuantitationType());
                }
            }
            
            // mage tab export test
            sw.reset();
            sw.start();
            MageTabFileSet mageTabSet = client.getMageTabExport(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID));                    
            sw.stop();
            System.out.println("Time: " + sw.toString());
            System.out.println("Exported IDF metadata: " + mageTabSet.getIdf().getFileMetadata() + ", Contents: \n");
            IOUtils.write(mageTabSet.getIdf().getContents(), System.out);
            System.out.println("Exported SDRF metadata: " + mageTabSet.getSdrf().getFileMetadata() + ", Contents: \n");
            IOUtils.write(mageTabSet.getSdrf().getContents(), System.out);
            System.out.println("\nAssociated Data Files:");
            for (DataFile dataFile : mageTabSet.getDataFiles()) {
                System.out.println(dataFile);
            }
            
            // mage tab transfer test, individual compression
            sw.reset();
            sw.start();
            TransferServiceContextReference mageTabRef = client.getMageTabZipTransfer(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID), true);
            sw.stop();
            System.out.println("Time: " + sw.toString());
            if (mageTabRef != null) {
                TransferServiceContextClient zipTransferClient = new TransferServiceContextClient(mageTabRef
                        .getEndpointReference());
                // use the TransferClientHelper to get an InputStream to the data
                ZipInputStream zis = new ZipInputStream(TransferClientHelper.getData(zipTransferClient
                        .getDataTransferDescriptor()));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null && zis.available() > 0) {
                    System.out.println("Contents of file " + entry.getName() + ", size " + entry.getSize());
                    saveFile(zis, entry.getName(), true, false);
                    entry = zis.getNextEntry();
                }
                zis.close();
            }
            
            // mage tab transfer test, zip compression
            sw.reset();
            sw.start();
            mageTabRef = client.getMageTabZipTransfer(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID), false);
            sw.stop();
            System.out.println("Time: " + sw.toString());
            if (mageTabRef != null) {
                TransferServiceContextClient zipTransferClient = new TransferServiceContextClient(mageTabRef
                        .getEndpointReference());
                // use the TransferClientHelper to get an InputStream to the data
                ZipInputStream zis = new ZipInputStream(TransferClientHelper.getData(zipTransferClient
                        .getDataTransferDescriptor()));
                ZipEntry entry = zis.getNextEntry();
                while (entry != null && zis.available() > 0) {
                    System.out.println("Contents of file " + entry.getName() + ", size " + entry.getSize());
                    saveFile(zis, entry.getName(), false, false);
                    entry = zis.getNextEntry();
                }
                zis.close();
            }                    
        } catch (Exception e) {
            System.err.println("Received Exception " + e);
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static void logAndSaveFile(TransferServiceContextReference transferRef, boolean compressed)
            throws Exception {
        TransferServiceContextClient tclient = new TransferServiceContextClient(transferRef.getEndpointReference());
        DataTransferDescriptor dtd = tclient.getDataTransferDescriptor();
        System.out.println("Contents of file " + dtd.getDataDescriptor().getName() + ", size "
                + FileUtils.byteCountToDisplaySize((Long) dtd.getDataDescriptor().getMetadata()));
        saveFile(TransferClientHelper.getData(dtd), dtd.getDataDescriptor().getName(), compressed, true);
    }

    private static void saveFile(InputStream fileData, String name, boolean compressed, boolean close)
            throws IOException {
        InputStream in = fileData;
        if (compressed) {
            in = new GZIPInputStream(in);
        }
        File temp = File.createTempFile("retrieved_" + name, null);
        FileOutputStream fos = FileUtils.openOutputStream(temp);
        IOUtils.copy(in, fos);
        System.out.println();
        System.out.println();
        if (close) {
            fileData.close();
        }
        fos.close();
    }
}
