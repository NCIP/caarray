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
import gov.nih.nci.caarray.external.v1_0.data.ArrayDataType;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import caarray.client.test.ApiFacade;
import caarray.client.test.TestProperties;
import caarray.client.test.TestResult;
import caarray.client.test.search.ArrayDataTypeSearch;
import caarray.client.test.search.ExampleSearch;

/**
 * Encapsulates a collection of ArrayDataType search-by-example tests.
 * 
 * @author vaughng 
 * Jun 26, 2009
 */
public class ArrayDataTypeTestSuite extends SearchByExampleTestSuite
{

    private static final String CONFIG_FILE = TestProperties.CONFIG_DIR
            + File.separator + "ArrayDataType.csv";

    private static final String NAME = "Name";
    private static final String QUANT_TYPE = "Quantitation Type";
    private static final String EXPECTED_QUANT = "Expected Quantitations";

    private static final String[] COLUMN_HEADERS = new String[] { TEST_CASE,
            API, NAME, QUANT_TYPE, EXPECTED_QUANT, EXPECTED_RESULTS };

    
    public ArrayDataTypeTestSuite(ApiFacade apiFacade)
    {
        super(apiFacade);
    }

    protected void populateSearch(String[] input, ExampleSearch exampleSearch)
    {
        ArrayDataTypeSearch search = (ArrayDataTypeSearch)exampleSearch;
        ArrayDataType example = new ArrayDataType();
        if (headerIndexMap.get(API) < input.length
                && !input[headerIndexMap.get(API)].equals(""))
        {
            search.setApi(input[headerIndexMap.get(API)].trim());
        }

        if (headerIndexMap.get(NAME) < input.length && !input[headerIndexMap.get(NAME)].equals(""))
            example.setName(input[headerIndexMap.get(NAME)].trim());
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            QuantitationType qType = new QuantitationType();
            qType.setName(input[headerIndexMap.get(QUANT_TYPE)].trim());
            example.getQuantitationTypes().add(qType);
        }
        search.setArrayDataType(example);
        if (headerIndexMap.get(TEST_CASE) < input.length
                && !input[headerIndexMap.get(TEST_CASE)].equals(""))
            search.setTestCase(Float.parseFloat(input[headerIndexMap.get(TEST_CASE)]
                    .trim()));
        if (headerIndexMap.get(EXPECTED_RESULTS) < input.length
                && !input[headerIndexMap.get(EXPECTED_RESULTS)].equals(""))
            search.setExpectedResults(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_RESULTS)].trim()));
        if (headerIndexMap.get(EXPECTED_QUANT) < input.length
                && !input[headerIndexMap.get(EXPECTED_QUANT)].equals(""))
            search.setExpectedQuantitations(Integer
                    .parseInt(input[headerIndexMap.get(EXPECTED_QUANT)].trim()));
    }
    
    @Override
    protected void populateAdditionalSearchValues(String[] input,
            ExampleSearch exampleSearch)
    {
        ArrayDataTypeSearch search = (ArrayDataTypeSearch)exampleSearch;
        if (headerIndexMap.get(QUANT_TYPE) < input.length && !input[headerIndexMap.get(QUANT_TYPE)].equals(""))
        {
            QuantitationType qType = new QuantitationType();
            qType.setName(input[headerIndexMap.get(QUANT_TYPE)]);
            search.getArrayDataType().getQuantitationTypes().add(qType);
        }
    }

    
    protected void evaluateResults(List<? extends AbstractCaArrayEntity> resultsList,
            ExampleSearch search, TestResult testResult)
    {
        ArrayDataTypeSearch adtSearch = (ArrayDataTypeSearch)search;
        List<ArrayDataType> adtResults = (List<ArrayDataType>)resultsList;
        if (adtSearch.getExpectedResults() != null)
        {
            int namedResults = 0;
            for (ArrayDataType arrayDataType : adtResults)
            {
                if (arrayDataType.getName() != null)
                    namedResults++;
            }
            if (namedResults != adtSearch.getExpectedResults())
            {
                testResult.setPassed(false);
                String detail = "Failed with unexpected number of results, expected: "
                        + adtSearch.getExpectedResults()
                        + ", actual number of results: " + namedResults;
                testResult.addDetail(detail);
            }
            else
            {
                String detail = "Found expected number of results: "
                        + namedResults;
                testResult.addDetail(detail);
            }
        }
        if (testResult.isPassed() && adtSearch.getExpectedQuantitations() != null)
        {

            Set<QuantitationType> qtypeSet = new HashSet<QuantitationType>();
            int namedQuantitations = 0;
            for (ArrayDataType arrayDataType : adtResults)
            {
                
                for (QuantitationType qType : arrayDataType
                        .getQuantitationTypes())
                {
                    if (qType.getName() != null)
                        qtypeSet.add(qType);
                        //namedQuantitations++;
                }
            }
                namedQuantitations = qtypeSet.size();
                if (namedQuantitations != adtSearch.getExpectedQuantitations())
                {
                    testResult.setPassed(false);
                    String detail = "Failed with unexpected number of named quantitations, expected: "
                            + adtSearch.getExpectedQuantitations()
                            + ", actual number: " + namedQuantitations;
                    testResult.addDetail(detail);
                }
                else
                {
                    String detail = "Found expected quantitations: "
                            + namedQuantitations;
                    testResult.addDetail(detail);
                }
            
        }
    }

    @Override
    protected String getType()
    {
        return "ArrayDataType";
    }

    @Override
    protected String getConfigFilename()
    {
        return CONFIG_FILE;
    }

    @Override
    protected String[] getColumnHeaders()
    {
        return COLUMN_HEADERS;
    }
    
    @Override
    protected ExampleSearch getExampleSearch()
    {
        return new ArrayDataTypeSearch();
    }

}
