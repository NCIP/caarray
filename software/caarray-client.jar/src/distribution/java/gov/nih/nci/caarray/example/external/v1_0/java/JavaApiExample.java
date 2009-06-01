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
package gov.nih.nci.caarray.example.external.v1_0.java;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationCriterion;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
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
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.QuantitationTypeSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.data.DataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.data.DataTransferException;
import gov.nih.nci.caarray.services.external.v1_0.data.JavaDataApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.AbstractSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import gov.nih.nci.caarray.services.external.v1_0.search.JavaSearchApiUtils;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;

/**
 * A simple class that connects to the remote Java API of a caArray server and retrieves and
 * prints a list of<code>QuantitationTypes</code>.
 */
@SuppressWarnings("PMD")
public class JavaApiExample {

    private static final String DEFAULT_SERVER = "array.nci.nih.gov";
    private static final int DEFAULT_JNDI_PORT = 8080;

    private String hostname = DEFAULT_SERVER;
    private int port = DEFAULT_JNDI_PORT;
    private CaArrayServer server;
    private SearchService searchService;
    private AbstractSearchApiUtils searchUtils;
    private DataService dataService;
    private DataApiUtils dataUtils;
    
    private static final String TEST_FILE_TYPE_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CEL";
    private static final String TEST_ORGANISM_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:1";
    private static final String TEST_EXPERIMENT_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1";
//    private static final String TEST_HYB1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:89";
//    private static final String TEST_HYB2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:93";
//    private static final String TEST_BIOMATERIAL1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:230";
//    private static final String TEST_BIOMATERIAL2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:246";
    private static final String TEST_HYB1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:1";
    private static final String TEST_HYB2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Hybridization:2";
    private static final String TEST_BIOMATERIAL1_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:1";
    private static final String TEST_BIOMATERIAL2_ID = "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.sample.Biomaterial:2";

    public static void main(String[] args) {
        JavaApiExample client = new JavaApiExample();
        if (args.length == 2) {
            client.hostname = args[0];
            client.port = Integer.parseInt(args[1]);
        } else if (args.length != 0) {
            System.err.println("Usage ApiClientTest [hostname port]");
            System.exit(1);
        }
        System.out.println("Using values: " + client.hostname + ":" + client.port);
        client.connectToServer();
        client.runTest();
    }

    public void connectToServer() {
        server = new CaArrayServer(hostname, port);
        try {
            server.connect();
            System.out.println("Successfully connected to server");
            searchService = server.getSearchService();
            searchUtils = new JavaSearchApiUtils(searchService);
            dataService = server.getDataService();
            dataUtils = new JavaDataApiUtils(dataService);
        } catch (ServerConnectionException e) {
            System.out.println("Couldn't connect to server: likely JNDI problem");
            e.printStackTrace(System.err);
            System.exit(1);
        }        
    }
    
    /**
     * Downloads data using the caArray Remote Java API.
     */
    public void runTest() {
        try {
            StopWatch sw = new StopWatch();

            System.out.println("Experiment full list");
            for (Experiment e : searchUtils.experimentsByCriteria(new ExperimentSearchCriteria()).list()) {
                System.out.println("Next experiment: " + e);
            }
            
            System.out.println("Experiment iteration");
            for (Iterator<Experiment> expIt = searchUtils.experimentsByCriteria(new ExperimentSearchCriteria())
                    .iterate(2); expIt.hasNext();) {
                Experiment e = expIt.next();
                System.out.println("Next experiment: " + e);
            }
            
            List<Organism> organisms = searchUtils.byExample(new ExampleSearchCriteria<Organism>(new Organism()))
                    .list();
            System.out.println("Organism count " + organisms.size());
            System.out.println("Organisms: " + organisms);
            Organism exampleOrg = new Organism();
            exampleOrg.setCommonName("house mouse");
            List<Organism> mouseOrgs = searchUtils.byExample(
                    new ExampleSearchCriteria<Organism>(exampleOrg, MatchMode.START)).list();
            System.out.println("Mus orgs: " + mouseOrgs);
            Organism o = (Organism) searchService.getByReference(new CaArrayEntityReference(
                    TEST_ORGANISM_ID));
            System.out.println("Retrieved organism by reference: " + o);
            
            List<ArrayDesign> designs = searchUtils
                    .byExample(new ExampleSearchCriteria<ArrayDesign>(new ArrayDesign())).list();
            System.out.println("Design count: " + designs.size());
            System.out.println("Designs: " + designs);
            
            List<ArrayProvider> providers = searchUtils.byExample(
                    new ExampleSearchCriteria<ArrayProvider>(new ArrayProvider())).list();
            System.out.println("Providers: " + providers);
            
            Category exampleCat = new Category();
            exampleCat.setName("DiseaseState");
            List<Category> dsCats = searchUtils.byExample(new ExampleSearchCriteria<Category>(exampleCat)).list();
            System.out.println("DiseaseState category matches: " + dsCats);
            List<Term> terms = searchService.getTermsForCategory(dsCats.get(0).getReference(), null);
            System.out.println("DiseaseState terms: " + terms);
            
            List<FileType> fileTypes = searchUtils.byExample(new ExampleSearchCriteria<FileType>(new FileType()))
                    .list();
            System.out.println("File Types: " + fileTypes);
            FileType exampleType = new FileType();
            exampleType.setName("AFFYMETRIX");
            List<FileType> chpTypes = searchUtils.byExample(
                    new ExampleSearchCriteria<FileType>(exampleType, MatchMode.START)).list();
            System.out.println("Affymetrix types: " + chpTypes);
            exampleType.setCategory(FileTypeCategory.DERIVED);
            List<FileType> derivedTypes = searchUtils.byExample(
                    new ExampleSearchCriteria<FileType>(exampleType, MatchMode.START)).list();
            System.out.println("Affy Derived types: " + derivedTypes);
            FileType affyCel = (FileType) searchService.getByReference(new CaArrayEntityReference(
                    TEST_FILE_TYPE_ID));
            System.out.println("Retrieved file type by reference: " + affyCel);
            
            List<Person> pis = searchService.getAllPrincipalInvestigators();
            System.out.println("PIs: " + pis);            
            
            List<Category> categories = searchService.getAllCharacteristicCategories(null);
            System.out.println("All Characteristic Categories: " + categories);            
            categories = searchService.getAllCharacteristicCategories(new CaArrayEntityReference(TEST_EXPERIMENT_ID));
            System.out.println("Experiment Characteristic Categories: " + categories);
            
            Experiment exampleExp = new Experiment();
            exampleExp.setOrganism(exampleOrg);
            List<Experiment> mouseExps = searchUtils.byExample(
                    new ExampleSearchCriteria<Experiment>(exampleExp, MatchMode.START)).list();
            System.out.println("Mus experiments: " + mouseExps);
            
            ExperimentSearchCriteria experimentCrit = new ExperimentSearchCriteria();
            experimentCrit.setPrincipalInvestigator(new CaArrayEntityReference("URN:LSID:gov.nih.nci.caarray.external.v1_0.experiment.Person:9"));
            experimentCrit.getAnnotationCriterions().add(new AnnotationCriterion(dsCats.get(0).getReference(), "Glioblastoma Multiforme"));
            experimentCrit.setOrganism(mouseOrgs.get(0).getReference());
            List<Experiment> exps = searchUtils.experimentsByCriteria(experimentCrit).list();
            System.out.println("Experiments by criteria: " + exps);
            
            KeywordSearchCriteria experimentKeywordCrit = new KeywordSearchCriteria();
            experimentKeywordCrit.setKeyword("MDR");
            List<Experiment> keywordExps = searchService.searchForExperimentsByKeyword(experimentKeywordCrit, new LimitOffset(5, 0)).getResults();
            System.out.println("Experiments by keyword criteria: " + keywordExps); 
            
            BiomaterialKeywordSearchCriteria sampleKeywordCrit = new BiomaterialKeywordSearchCriteria();
            sampleKeywordCrit.setKeyword("MDR");
            List<Biomaterial> keywordSamples = searchUtils.biomaterialsByKeyword(sampleKeywordCrit).list();
            System.out.println("Biomaterials by keyword: " + keywordSamples); 
                                                
            DataSetRequest dataRequest = new DataSetRequest();
            FileDownloadRequest downloadRequest = new FileDownloadRequest();
            
            FileSearchCriteria fileCriteria = new FileSearchCriteria();
            fileCriteria.setExperiment(new CaArrayEntityReference(TEST_EXPERIMENT_ID));
            fileCriteria.getExperimentGraphNodes().add(new CaArrayEntityReference(TEST_HYB1_ID));
            fileCriteria.getExperimentGraphNodes().add(new CaArrayEntityReference(TEST_BIOMATERIAL2_ID));
            fileCriteria.getTypes().add(affyCel.getReference());
            List<DataFile> files = searchUtils.filesByCriteria(fileCriteria).list();

            for (DataFile file : files) {
                downloadAndPrintFile(file);
                dataRequest.getDataFiles().add(file.getReference());
                downloadRequest.getFiles().add(file.getReference());
            }
            
            downloadFileZip(downloadRequest, false);

            HybridizationSearchCriteria hsc = new HybridizationSearchCriteria();
            hsc.setExperiment(new CaArrayEntityReference(TEST_EXPERIMENT_ID));
            hsc.getBiomaterials().add(
                    new CaArrayEntityReference(
                            TEST_BIOMATERIAL1_ID));
            hsc.getBiomaterials().add(
                    new CaArrayEntityReference(
                            TEST_BIOMATERIAL2_ID));
            List<Hybridization> hybs = searchUtils.hybridizationsByCriteria(hsc).list();
            for (Hybridization hyb : hybs) {
                System.out.println("hyb: " + hyb);
                //dataRequest.getHybridizations().add(hyb.getReference());
            }

            BiomaterialSearchCriteria bsc = new BiomaterialSearchCriteria();
            bsc.setExperiment(new CaArrayEntityReference(TEST_EXPERIMENT_ID));
            //bsc.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
            bsc.getAnnotationCriterions().add(
                    new AnnotationCriterion(dsCats.get(0).getReference(), "Glioblastoma Multiforme"));
            List<Biomaterial> bms = searchUtils.biomaterialsByCriteria(bsc).list();
            System.out.println("Biomaterials by criteria: " + bms);

            ArrayDataType example = new ArrayDataType();
            example.setName("Affymetrix CEL");
            List<ArrayDataType> affyCelDataTypes = searchUtils.byExample(new ExampleSearchCriteria<ArrayDataType>(example)).list();
            System.out.println("Affy array data types: " + affyCelDataTypes);
            
            QuantitationTypeSearchCriteria qtCriteria = new QuantitationTypeSearchCriteria();
            qtCriteria.setHybridization(hybs.get(0).getReference());
            //qtCriteria.getArrayDataTypes().add(affyCelDataTypes.get(0).getReference());
            List<QuantitationType> quantitationTypes = searchService.searchForQuantitationTypes(qtCriteria);
            System.out.println("Quantitation Types from search");
            for (QuantitationType qt : quantitationTypes) {
                System.out.println("QT: " + qt);
                dataRequest.getQuantitationTypes().add(qt.getReference());
            }

            sw.reset();
            sw.start();
            DataSet dataSet = dataService.getDataSet(dataRequest);
            sw.stop();
            System.out.println("Time to retrieve parsed data set: " + sw.toString());
            System.out.println("Design element list: " + dataSet.getDesignElements());
            System.out.println("Quantitation types: " + dataSet.getQuantitationTypes());
            for (HybridizationData hdata : dataSet.getDatas()) {
                System.out.println("Data for hyb " + hdata.getHybridization().getName());
                for (AbstractDataColumn dataColumn : hdata.getDataColumns()) {
                    //System.out.println("Data column: " + dataColumn);
                    System.out.println("Retrieved data column of type: " + dataColumn.getQuantitationType());
                }
            }
            
            MageTabFileSet mageTabSet = dataService.exportMageTab(new CaArrayEntityReference(
                    TEST_EXPERIMENT_ID));
            System.out.println("Exported IDF metadata: " + mageTabSet.getIdf().getFileMetadata() + ", Contents: \n");
            IOUtils.write(mageTabSet.getIdf().getContents(), System.out);
            System.out.println("Exported SDRF metadata: " + mageTabSet.getSdrf().getFileMetadata() + ", Contents: \n");
            IOUtils.write(mageTabSet.getSdrf().getContents(), System.out);
            System.out.println("\nAssociated Data Files:");
            for (DataFile dataFile : mageTabSet.getDataFiles()) {
                System.out.println(dataFile);
            }
            
            downloadMageTabZip(new CaArrayEntityReference(TEST_EXPERIMENT_ID), false);                        
            
            AnnotationSetRequest asr = new AnnotationSetRequest();
            asr.getExperimentGraphNodes().add(new CaArrayEntityReference(TEST_HYB1_ID));
            asr.getExperimentGraphNodes().add(new CaArrayEntityReference(TEST_BIOMATERIAL2_ID));
            asr.getCategories().add(dsCats.get(0).getReference());
            AnnotationSet as = searchService.getAnnotationSet(asr);
            System.out.println("AS categories: " + as.getCategories());
            System.out.println("AS columns: " + as.getColumns());
            
            Biomaterial bm = new Biomaterial();
            bm.setName("Sample");
            bm.setType(BiomaterialType.SAMPLE);
            List<Biomaterial> matchingBms = searchUtils.byExample(
                    new ExampleSearchCriteria<Biomaterial>(bm, MatchMode.START)).list();
            System.out.println("BMs example matches: " + matchingBms);

        } catch (Throwable t) {
            System.out.println("Couldn't run query: " + t);
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }
    
    private void downloadAndPrintFile(DataFile file) throws InvalidReferenceException, DataTransferException,
            IOException {
        System.out.println("File Metadata: " + file);
        System.out.println("File Contents: ");
        dataUtils.copyFileContentsToOutputStream(file.getReference(), false, System.out);
    }

    private void downloadFileZip(FileDownloadRequest downloadRequest, boolean compressIndividually)
            throws InvalidReferenceException, DataTransferException, IOException {
        System.out.println("Downloading file zip for: " + downloadRequest.getFiles());
        File tempDir = dataUtils.downloadAndExtractFileContentsZipToTempDir(downloadRequest);
        System.out.println("Downloaded and extracted zip to " + tempDir.getAbsolutePath());
        for (File file : tempDir.listFiles()) {
            System.out.println("File " + file.getName() + ", size " + file.length());
        }
    }

    private void downloadMageTabZip(CaArrayEntityReference experimentRef, boolean compressIndividually)
            throws InvalidReferenceException, DataTransferException, IOException {
        System.out.println("Downloading mage tab zip for: " + experimentRef);
        File tempDir = dataUtils.downloadAndExtractMageTabZipToTempDir(experimentRef);
        System.out.println("Downloaded and extracted zip to " + tempDir.getAbsolutePath());
        for (File file : tempDir.listFiles()) {
            System.out.println("File " + file.getName() + ", size " + file.length());
        }
    }
}
