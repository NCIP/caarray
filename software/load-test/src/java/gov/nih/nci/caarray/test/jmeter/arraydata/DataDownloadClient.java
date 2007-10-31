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
package gov.nih.nci.caarray.test.jmeter.arraydata;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;

import gov.nih.nci.caarray.services.CaArrayServer;
import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.search.CaArraySearchService;
import gov.nih.nci.caarray.services.data.DataRetrievalService;
import gov.nih.nci.caarray.services.data.DataRetrievalRequest;
import gov.nih.nci.caarray.test.jmeter.base.TestProperties;

import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.config.Arguments;

/**
 * A custom JMeter Sampler that acts as a client downloading array data through CaArray's Remote Java API.
 *
 * @author Rashmi Srinivasa
 */
public class DataDownloadClient implements JavaSamplerClient {
    private static final String EXPERIMENT_NAMES_PARAM = "experimentNamesCsv";
    private static final String QUANTITATION_TYPES_PARAM = "quantitationTypesCsv";

    private static final String DEFAULT_EXPERIMENT_NAME = "Glioblastoma Affymetrix 01";
    private static final String DEFAULT_QUANTITATION_TYPE = "CELintensity";

    String experimentTitlesCsv;
    String quantitationTypesCsv;

    /**
     * Sets up the data download test by initializing the <code>Experiment</code>s to download array data from, and
     * the <code>Quantitation</code> types to include in the result.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void setupTest(JavaSamplerContext context) {
        experimentTitlesCsv = context.getParameter(EXPERIMENT_NAMES_PARAM, DEFAULT_EXPERIMENT_NAME);
        quantitationTypesCsv = context.getParameter(QUANTITATION_TYPES_PARAM, DEFAULT_QUANTITATION_TYPE);
    }

    /**
     * Returns the default parameters used by the Sampler if none is specified in the JMeter test being run.
     *
     * @return the <code>Arguments</code> containing default parameters.
     */
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument(EXPERIMENT_NAMES_PARAM, DEFAULT_EXPERIMENT_NAME);
        params.addArgument(QUANTITATION_TYPES_PARAM, DEFAULT_QUANTITATION_TYPE);
        return params;
    }

    /**
     * Runs the data download test and returns the result.
     *
     * @param context the <code>JavaSamplerContext</code> to read arguments from.
     * @param the <code>SampleResult</code> containing the success/failure and timing results of the test.
     */
    public SampleResult runTest(JavaSamplerContext context) {
        SampleResult results = new SampleResult();

        DataRetrievalRequest request = new DataRetrievalRequest();
        try {
            CaArrayServer server = new CaArrayServer(TestProperties.CAARRAY_SERVER_HOSTNAME, TestProperties.CAARRAY_SERVER_JNDI_PORT);
            server.connect();
            CaArraySearchService searchService = server.getSearchService();
            lookupExperiments(searchService, request);
            lookupQuantitationTypes(searchService, request);
            DataRetrievalService dataService = server.getDataRetrievalService();
            results.sampleStart();
            DataSet dataSet = dataService.getDataSet(request);
            results.sampleEnd();
            // Check if the retrieved number of hybridizations and quantitation types are as requested.
            if ((dataSet != null) && (request.getHybridizations().size() == dataSet.getHybridizationDataList().size())) {
                if ((request.getQuantitationTypes().size() == 0)
                        || (request.getQuantitationTypes().size()) == dataSet.getQuantitationTypes().size()) {
                    results.setSuccessful(true);
                    results.setResponseCodeOK();
                }
            } else {
                results.setSuccessful(false);
                results.setResponseCode("Error: Response did not match request.");
            }
        } catch (ServerConnectionException e) {
            results.setSuccessful(false);
            results.setResponseCode("Server connection exception: " + e);
        } catch (RuntimeException e) {
            results.setSuccessful(false);
            results.setResponseCode("Runtime exception: " + e);
        } catch (Throwable t) {
            // Catches things like out-of-memory errors and logs them in the test output.
            results.setSuccessful(false);
            results.setResponseCode("Throwable: " + t);
        }
        return results;
    }

    private void lookupExperiments(CaArraySearchService service, DataRetrievalRequest request) {
        String[] experimentTitles = experimentTitlesCsv.split(",");
        if (experimentTitles == null) {
            return;
        }

        // Locate each experiment, and add its hybridizations to the request.
        Experiment exampleExperiment = new Experiment();
        try {
        for (int i = 0; i < experimentTitles.length; i++) {
            String experimentTitle = experimentTitles[i];
            exampleExperiment.setTitle(experimentTitle);
            List<Experiment> experimentList = service.search(exampleExperiment);
            Set<Hybridization> allHybs = getAllHybridizations(experimentList);
            request.getHybridizations().addAll(allHybs);
        }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void lookupQuantitationTypes(CaArraySearchService service, DataRetrievalRequest request) {
        String[] quantitationTypeNames = quantitationTypesCsv.split(",");
        if (quantitationTypeNames == null) {
            return;
        }

        // Locate each quantitation type and add it to the request.
        QuantitationType exampleQuantitationType = new QuantitationType();
        for (int i = 0; i < quantitationTypeNames.length; i++) {
            String quantitationTypeName = quantitationTypeNames[i];
            exampleQuantitationType.setName(quantitationTypeName);
            List<QuantitationType> quantitationTypeList = service.search(exampleQuantitationType);
            request.getQuantitationTypes().addAll(quantitationTypeList);
        }
    }

    private Set<Hybridization> getAllHybridizations(List<Experiment> experimentList) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        for (Experiment experiment : experimentList) {
            hybridizations.addAll(getAllHybridizations(experiment));
        }
        return hybridizations;
    }

    private Set<Hybridization> getAllHybridizations(Experiment experiment) {
        Set<Hybridization> hybridizations = new HashSet<Hybridization>();
        hybridizations.addAll(experiment.getHybridizations());
        return hybridizations;
    }

    /**
     * Cleans up after the test.
     *
     * @param context the <code>JavaSamplerContext</code> which contains the arguments passed in.
     */
    public void teardownTest(JavaSamplerContext context) {
    }
}
