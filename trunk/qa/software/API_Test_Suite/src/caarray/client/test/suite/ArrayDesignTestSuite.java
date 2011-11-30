/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarrayGridClientExamples_v1_0
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarrayGridClientExamples_v1_0 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarrayGridClientExamples_v1_0 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarrayGridClientExamples_v1_0 Software; (ii) distribute and
 * have distributed to and by third parties the caarrayGridClientExamples_v1_0 Software and any
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
package caarray.client.test.suite;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.array.ArrayDesign;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;
import gov.nih.nci.caarray.external.v1_0.array.AssayType;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;

import java.io.File;
import java.util.List;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ArrayDesignSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of ArrayDesign search-by-example tests.
 * 
 * @author vaughng
 * Jun 27, 2009
 */
public class ArrayDesignTestSuite extends SearchByExampleTestSuite 

{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "ArrayDesign.csv";

    private static final String NAME = "Name";
    private static final String LSID = "LSID";
    private static final String PROVIDER = "Associated Provider";
    private static final String ASSAY_TYPE = "Assay Type";
    private static final String EXPECTED_PROVIDER = "Expected Provider";
    private static final String EXPECTED_ORGANISM = "Expected Organism";
    private static final String NULL = "NULL";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, LSID, PROVIDER, ASSAY_TYPE, EXPECTED_PROVIDER, MIN_RESULTS, EXPECTED_RESULTS,
            EXPECTED_ORGANISM, NULL };
    

    /**
     * @param gridClient
     * @param javaSearchService
     */
    public ArrayDesignTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        ArrayDesignSearch search = (ArrayDesignSearch)exampleSearch;
        ArrayDesign example = new ArrayDesign();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(LSID) < input.length && !input[headerIndexMap.get(LSID)].equals(""))
            example.setLsid(input[headerIndexMap.get(LSID)].trim());
        
        if (headerIndexMap.get(PROVIDER) < input.length && !input[headerIndexMap.get(PROVIDER)].equals(""))
        {
            /*ArrayProvider provider = new ArrayProvider();
            provider.setName(input[headerIndexMap.get(PROVIDER)]);
            example.setArrayProvider(provider);*/
            ArrayProvider provider = null;
            try
            {
                provider = apiFacade.getArrayProvider(search.getApi(), input[headerIndexMap.get(PROVIDER)]);   
            }
            catch (Exception e)
            {
                log.error("Error retrieving ArrayProvider for ArrayDesign search", e);
                provider = new ArrayProvider();
                provider.setName(input[headerIndexMap.get(PROVIDER)]);
            }
            
            if (provider != null)
            {
                example.setArrayProvider(provider);
            }
            
        }
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            example.getAssayTypes().add(new AssayType(input[headerIndexMap.get(ASSAY_TYPE)]));
        }
        if (headerIndexMap.get(NULL) < input.length && !input[headerIndexMap.get(NULL)].equals(""))
        {
            example = null;
        }
        search.setArrayDesign(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(MIN_RESULTS) < input.length
                && !input[headerIndexMap.get(MIN_RESULTS)].equals(""))
            search.setMinResults(Integer
                    .parseInt(input[headerIndexMap.get(MIN_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_PROVIDER) < input.length
                && !input[headerIndexMap.get(EXPECTED_PROVIDER)].equals(""))
            search.setExpectedProvider(input[headerIndexMap.get(EXPECTED_PROVIDER)].trim());
        if (headerIndexMap.get(EXPECTED_ORGANISM) < input.length
                && !input[headerIndexMap.get(EXPECTED_ORGANISM)].equals(""))
            search.setExpectedOrganism(input[headerIndexMap.get(EXPECTED_ORGANISM)]);
    }
    
    
    
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        ArrayDesignSearch search = (ArrayDesignSearch)exampleSearch;
        if (headerIndexMap.get(ASSAY_TYPE) < input.length && !input[headerIndexMap.get(ASSAY_TYPE)].equals(""))
        {
            search.getArrayDesign().getAssayTypes().add(new AssayType(input[headerIndexMap.get(ASSAY_TYPE)]));
        }
    }

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }

    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String getType()
    {
        return "ArrayDesign";
    }

    @Override
    protected void evaluateResults(
            List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        ArrayDesignSearch adSearch = (ArrayDesignSearch)search;
        List<ArrayDesign> adResults = (List<ArrayDesign>)resultsList;
        if (adSearch.getExpectedResults() != null || adSearch.getMinResults() != null)
        {
            int namedResults = 0;
            for (ArrayDesign arrayDesign : adResults)
            {
                if (arrayDesign.getName() != null)
                    namedResults++;
            }
            if (adSearch.getExpectedResults() != null && namedResults != adSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, ArrayDesign:"
                        + adSearch.getArrayDesign().getName()
                        + "- expected number of results: "
                        + adSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
            if (adSearch.getMinResults() != null && namedResults < adSearch.getMinResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected minimum number of results, ArrayDesign:"
                        + adSearch.getArrayDesign().getName()
                        + "- expected number of results: "
                        + adSearch.getMinResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (adSearch.getExample() == null)
        {
            if (adSearch.getExceptionClass() != null)
            {
                if (!adSearch.getExceptionClass().equals(InvalidInputException.class.toString())
                        && !adSearch.getExceptionClass().equals(InvalidInputFault.class.toString()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected exception class: " + adSearch.getExceptionClass();
                    testResult.addDetail(detail);
                }
                else
                {
                    testResult.addDetail("Found expected exception " + adSearch.getExceptionClass());
                }
            }
        }
        for (ArrayDesign arrayDesign : adResults)
        {
            if (adSearch.getExpectedProvider() != null)
            {
                if (arrayDesign.getArrayProvider() == null
                        || !arrayDesign.getArrayProvider().getName().equals(
                                adSearch.getExpectedProvider()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected array provider: "
                            + (arrayDesign.getArrayProvider() == null ? "null"
                                    : arrayDesign.getArrayProvider().getName())
                            + ", expected provider: "
                            + adSearch.getExpectedProvider();
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected provider: "
                            + adSearch.getExpectedProvider();
                    testResult.addDetail(detail);
                }
            }
            if (adSearch.getExpectedOrganism() != null)
            {
                if (arrayDesign.getOrganism() == null || 
                        !arrayDesign.getOrganism().getCommonName().equalsIgnoreCase(adSearch.getExpectedOrganism()))
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected organism common name: " +
                        (arrayDesign.getOrganism() == null ? "null" : arrayDesign.getOrganism().getCommonName()) +
                        ", expected name: " + adSearch.getExpectedOrganism();
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected organism: " + adSearch.getExpectedOrganism();
                    testResult.addDetail(detail);
                }
            }
        }
        
    }

    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ArrayDesignSearch();
    }
    
    

}
