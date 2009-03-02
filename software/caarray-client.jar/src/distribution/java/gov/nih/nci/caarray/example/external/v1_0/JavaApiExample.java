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
package gov.nih.nci.caarray.example.external.v1_0;


import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.data.AbstractDataColumn;
import gov.nih.nci.caarray.external.v1_0.data.DataFile;
import gov.nih.nci.caarray.external.v1_0.data.DataSet;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.data.FileTypeCategory;
import gov.nih.nci.caarray.external.v1_0.data.HybridizationData;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.experiment.Person;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchField;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentKeywordSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchField;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.PagingParams;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;
import gov.nih.nci.caarray.services.external.v1_0.data.DataService;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchService;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.io.ByteArrayOutputStream;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.StopWatch;

import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import com.healthmarketscience.rmiio.SimpleRemoteOutputStream;

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
        client.runTest();
    }

    /**
     * Downloads data using the caArray Remote Java API.
     */
    public void runTest() {
        CaArrayServer server = new CaArrayServer(hostname, port);
        try {
            server.connect();
            System.out.println("Successfully connected to server");
        } catch (ServerConnectionException e) {
            System.out.println("Couldn't connect to server: likely JNDI problem");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        try {
            StopWatch sw = new StopWatch();
            
            SearchService searchService = server.getSearchService();
            
            List<Organism> organisms = searchService.getAllOrganisms(null);
            System.out.println("Organism count " + organisms.size());
            System.out.println("Organisms: " + organisms);
            
            List<ArrayDesign> designs = searchService.getAllArrayDesigns(new PagingParams(15, 5));
            System.out.println("Design count: " + designs.size());
            System.out.println("Designs: " + designs);
            
            List<ArrayProvider> providers = searchService.getAllProviders(null);
            System.out.println("Providers: " + providers);
            
            List<FileType> fileTypes = searchService.getAllFileTypes(null);
            System.out.println("File Types: " + fileTypes);
            
            List<Person> pis = searchService.getAllPrincipalInvestigators(null);
            System.out.println("PIs: " + pis);
            
            ExperimentSearchCriteria experimentCrit = new ExperimentSearchCriteria();
            experimentCrit.setTitle("fsdfds");
            //experimentCrit.getOrganisms().add(new CaArrayEntityReference("URN:LSID:edu.georgetown.pir.Organism:1"));
            //experimentCrit.getOrganisms().add(new CaArrayEntityReference("URN:LSID:edu.georgetown.pir.Organism:2"));            
            List<Experiment> exps = searchService.searchForExperiments(experimentCrit, new PagingParams(5, 0));
            System.out.println("Experiments by criteria: " + exps);
            
            ExperimentKeywordSearchCriteria experimentKeywordCrit = new ExperimentKeywordSearchCriteria();
            experimentKeywordCrit.setKeyword("MDR");
            experimentKeywordCrit.getFields().add(ExperimentSearchField.TITLE);
            experimentKeywordCrit.getFields().add(ExperimentSearchField.SAMPLE_NAME);
            List<Experiment> keywordExps = searchService.searchForExperimentsByKeyword(experimentKeywordCrit, new PagingParams(5, 0));
            System.out.println("Experiments by keyword criteria: " + keywordExps); 
            
            BiomaterialKeywordSearchCriteria sampleKeywordCrit = new BiomaterialKeywordSearchCriteria();
            sampleKeywordCrit.setKeyword("MDR");
            sampleKeywordCrit.getFields().add(BiomaterialSearchField.NAME);
            List<Biomaterial> keywordSamples = searchService.searchForBiomaterialsByKeyword(sampleKeywordCrit, new PagingParams(30, 0));
            System.out.println("Samples by name keyword: " + keywordSamples); 
            
            sampleKeywordCrit = new BiomaterialKeywordSearchCriteria();
            sampleKeywordCrit.setKeyword("Homo");
            sampleKeywordCrit.getFields().add(BiomaterialSearchField.ORGANISM);
            keywordSamples = searchService.searchForBiomaterialsByKeyword(sampleKeywordCrit, new PagingParams(30, 0));
            System.out.println("Samples by organism keyword: " + keywordSamples); 
            
            
            Organism o = (Organism) searchService.getByReference(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Organism:1"));
            System.out.println("Retrieved organism by reference: " + o);
            
            CQLQuery cqlQuery = new CQLQuery();
            Object target = new Object();
            target.setName(QuantitationType.class.getName());
            cqlQuery.setTarget(target);
            QueryModifier queryMod = new QueryModifier();
            queryMod.setCountOnly(true);
            cqlQuery.setQueryModifier(queryMod);
            
            @SuppressWarnings("unchecked")
            int count = ((List<Integer>) searchService.search(cqlQuery, null)).get(0);
            System.out.println("Num quant types: " + count);

            queryMod.setCountOnly(false);            
            PagingParams pageParams = new PagingParams(20, 0);
            for (int i = 0; i < count; i+=20) {
                System.out.println("Retrieving quant types " + i + " to " + (i + 20));
                pageParams.setFirstResult(i);
                @SuppressWarnings("unchecked")
                List<QuantitationType> quantTypes = (List<QuantitationType>) searchService.search(cqlQuery, pageParams);
                for (QuantitationType quantType : quantTypes) {
                    System.out.println("Quantitation Type: " + quantType);
                }
            }
            
            queryMod.setAttributeNames(new String[] { "name" });
            pageParams.setMaxResults(-1);
            pageParams.setFirstResult(0);
            List<java.lang.Object[]> quantAttrsList = (List<java.lang.Object[]>) searchService.search(cqlQuery, pageParams);
            for (java.lang.Object[] quantAttrs : quantAttrsList) {
                System.out.println("Quantitation Type name: " + quantAttrs[0]);
            }
            
            DataService dataService = server.getDataService();
            DataSetRequest dataRequest = new DataSetRequest();
            FileSearchCriteria fileCriteria = new FileSearchCriteria();
            fileCriteria.setExperiment(new CaArrayEntityReference(
                    "URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            fileCriteria.setCategories(EnumSet.of(FileTypeCategory.RAW));
            List<DataFile> files = searchService.searchForFiles(fileCriteria, null);
            for (DataFile file : files) {
                System.out.println("File Metadata: " + file);
                CaArrayEntityReference fileRef = new CaArrayEntityReference(file.getLsid()); 
                RemoteOutputStreamServer ostream = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {                    
                    ostream = new SimpleRemoteOutputStream(bos);
                    dataService.streamFileContents(fileRef, true, ostream.export());
                    System.out.println("File Contents: ");
                    IOUtils.write(bos.toByteArray(), System.out);
                    System.out.println();
                } finally {
                    if (ostream != null) {
                        ostream.close();
                    }
                }        
                dataRequest.getDataFiles().add(fileRef);
            }

            HybridizationSearchCriteria hsc = new HybridizationSearchCriteria();
            hsc.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            List<Hybridization> hybs = searchService.searchForHybridizations(hsc, null);
            for (Hybridization hyb : hybs) {
                System.out.println("hyb: " + hyb);
                //dataRequest.getHybridizations().add(new CaArrayEntityReference(hyb.getLsid()));
            }

            BiomaterialSearchCriteria bsc = new BiomaterialSearchCriteria();
            bsc.setExperiment(new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.experiment.Experiment:1"));
            bsc.setTypes(EnumSet.of(BiomaterialType.SOURCE, BiomaterialType.SAMPLE));
            List<Biomaterial> bms = searchService.searchForBiomaterials(bsc, null);
            for (Biomaterial bm : bms) {
                System.out.println("bm: " + bm);
            }

            
            for (int i = 16; i <= 22; i++) {
                CaArrayEntityReference qRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.QuantitationType:" + i);                                 
                dataRequest.getQuantitationTypes().add(qRef);
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
            
            
        } catch (Throwable t) {
            System.out.println("Couldn't run query: likely RMI problem");
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
