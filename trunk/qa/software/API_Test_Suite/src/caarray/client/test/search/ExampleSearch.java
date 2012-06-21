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
package caarray.client.test.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.MatchMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for beans encapsulating details of a search-by-example test search.
 * 
 * @author vaughng 
 * Jun 27, 2009
 */
public abstract class ExampleSearch extends TestBean
{

    protected String exceptionClass = null;
    protected Integer resultsPerLimitOffset = null, stopResults = null;
    protected List<Integer> resultsReturnedInPage = new ArrayList<Integer>();
    protected MatchMode matchMode;
    protected boolean enumerate = false, apiUtil = false, login = false, excludeZeros = false;

    protected ExampleSearch()
    {
    }
    
    public abstract AbstractCaArrayEntity getExample();

    /**
     * MatchMode to be set in the search criteria.
     * 
     * @return MatchMode to be set in the search criteria.
     */
    public MatchMode getMatchMode()
    {
        return matchMode;
    }

    /**
     * MatchMode to be set in the search criteria.
     * 
     * @param matchMode MatchMode to be set in the search criteria.
     */
    public void setMatchMode(MatchMode matchMode)
    {
        this.matchMode = matchMode;
    }

    /**
     * Indicates a search is an API enumeration method.
     * 
     * @return True if a search is an API enumeration method.
     */
    public boolean isEnumerate()
    {
        return enumerate;
    }

    /**
     * Indicates a search is an API enumeration method.
     * 
     * @param enumerate True if a search is an API enumeration method.
     */
    public void setEnumerate(boolean enumerate)
    {
        this.enumerate = enumerate;
    }

    /**
     * Number of per-page results to be set in a LimitOffset.
     * 
     * @return Number of per-page results to be set in a LimitOffset.
     */
    public Integer getResultsPerLimitOffset()
    {
        return resultsPerLimitOffset;
    }

    /**
     * Number of per-page results to be set in a LimitOffset.
     * 
     * @param results Number of per-page results to be set in a LimitOffset.
     */
    public void setResultsPerLimitOffset(Integer results)
    {
        this.resultsPerLimitOffset = results;
    }

    /**
     * A list of the number of results returned per page.
     * 
     * @return A list of the number of results returned per page.
     */
    public List<Integer> getResultsReturnedInPage()
    {
        return resultsReturnedInPage;
    }

    /**
     * Add a number of results returned to the list of results per page.
     * 
     * @param pageReturned A number of results returned added to the list of results per page.
     */
    public void addPageReturned(Integer pageReturned)
    {
        resultsReturnedInPage.add(pageReturned);
    }

    /**
     * Indicates a search should be executed via an API utils search.
     * 
     * @return Indicates a search should be executed via an API utils search.
     */
    public boolean isApiUtil()
    {
        return apiUtil;
    }

    /**
     * Indicates a search should be executed via an API utils search.
     * 
     * @param apiUtil Indicates a search should be executed via an API utils search.
     */
    public void setApiUtil(boolean apiUtil)
    {
        this.apiUtil = apiUtil;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @return Indicates the test user should be logged in to execute this test.
     */
    public boolean isLogin()
    {
        return login;
    }

    /**
     * Indicates the test user should be logged in to execute this test.
     * 
     * @param login Indicates the test user should be logged in to execute this test.
     */
    public void setLogin(boolean login)
    {
        this.login = login;
    }

    /**
     * Set the excludeZeros property of the search criteria.
     * 
     * @return the excludeZeros property of the search criteria.
     */
    public boolean isExcludeZeros()
    {
        return excludeZeros;
    }

    /**
     * The excludeZeros property of the search criteria.
     * 
     * @param excludeZeros the excludeZeros property of the search criteria.
     */
    public void setExcludeZeros(boolean excludeZeros)
    {
        this.excludeZeros = excludeZeros;
    }

    /**
     * Indicates the number of results at which the search should be terminated.
     * 
     * @return Indicates the number of results at which the search should be terminated.
     */
    public Integer getStopResults()
    {
        return stopResults;
    }

    /**
     * Indicates the number of results at which the search should be terminated.
     * 
     * @param stopResults Indicates the number of results at which the search should be terminated.
     */
    public void setStopResults(Integer stopResults)
    {
        this.stopResults = stopResults;
    }

    /**
     * Indicates the expected class type of an exception thrown by a search.
     * 
     * @return Indicates the expected class type of an exception thrown by a search.
     */
    public String getExceptionClass()
    {
        return exceptionClass;
    }

    /**
     * Indicates the expected class type of an exception thrown by a search.
     * 
     * @param exceptionClass Indicates the expected class type of an exception thrown by a search.
     */
    public void setExceptionClass(String exceptionClass)
    {
        this.exceptionClass = exceptionClass;
    }
    

}
