/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caArray2 Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and 
 * have distributed to and by third parties the caArray2 Software and any 
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
package gov.nih.nci.caarray.domain.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.hibernate.criterion.MatchMode;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Simple bean to hold example search criteria.
 * 
 * @param <T> class of the example entity
 * @author dkokotov
 */
public class ExampleSearchCriteria<T extends PersistentObject> {
    private T example;
    private MatchMode matchMode = MatchMode.EXACT;
    private boolean excludeNulls = true;
    private boolean excludeZeroes = false;
    private Collection<String> excludeProperties = new HashSet<String>();

    /**
     * Create a new example search criteria with given example, using exact matching, excluding null properties.
     * 
     * @param example the example
     */
    public ExampleSearchCriteria(T example) {
        this.example = example;
    }
    
    /**
     * Create a new example search criteria with given example, using the given match mode, excluding null properties.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode) {
        this.example = example;
        this.matchMode = matchMode;
    }
    

    /**
     * Create a new example search criteria with given example, using the given match mode and given null property
     * handling.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     * @param excludeNulls if true, properties in the example with a null value will be excluded from comparison; if
     *            false, they will be excluded, so candidate matches must also have null values for those properties.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
    }


    /**
     * Create a new example search criteria with given example, using the given match mode and given null property
     * handling.
     * 
     * @param example the example
     * @param matchMode how string properties in the example should be compared against candidate matches.
     * @param excludeNulls if true, properties in the example with a null value will be excluded from comparison; if
     *            false, they will be excluded, so candidate matches must also have null values for those properties.
     * @param excludeProperties exclude the given properties from comparison.
     */
    public ExampleSearchCriteria(T example, MatchMode matchMode, boolean excludeNulls,
            Collection<String> excludeProperties) {
        this.example = example;
        this.matchMode = matchMode;
        this.excludeNulls = excludeNulls;
        this.excludeProperties = excludeProperties;
    }

    /**
     * Create a new example search criteria with given example, using exact matching, excluding null properties. 
     * Intended for builder-style creation of the criteria.
     * 
     * @param entity the example
     * @param <T> class of example entity
     * @return the new example criteria
     */
    public static <T extends PersistentObject> ExampleSearchCriteria<T> forEntity(T entity) {
        return new ExampleSearchCriteria<T>(entity);
    }

    /**
     * Set the match mode of this criteria to given match mode.
     * 
     * @param mode the new match mode
     * @return this
     */
    public ExampleSearchCriteria<T> matchUsing(MatchMode mode) {
        setMatchMode(mode);
        return this;
    }
    
    /**
     * Set this criteria to exclude null properties from comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> excludeNulls() {
        setExcludeNulls(true);
        return this;
    }    

    /**
     * Set this criteria to include null properties in the comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> includeNulls() {
        setExcludeNulls(false);
        return this;
    }    
    
    /**
     * Set this criteria to exclude given properties from comparison.
     * 
     * @param properties the given properties to exclude.
     * @return this
     */
    public ExampleSearchCriteria<T> excludeProperties(String... properties) {
        excludeProperties.addAll(Arrays.asList(properties));
        return this;
    }    

    /**
     * Set this criteria to include given properties from comparison.
     * 
     * @param properties the given properties to include.
     * @return this
     */
    public ExampleSearchCriteria<T> includeProperties(String... properties) {
        excludeProperties.removeAll(Arrays.asList(properties));
        return this;
    }    
    
    /**
     * Set this criteria to exclude zero-valued properties from comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> excludeZeroes() {
        setExcludeZeroes(true);
        return this;
    }    

    /**
     * Set this criteria to include zero-valued properties in the comparison.
     * 
     * @return this
     */
    public ExampleSearchCriteria<T> includeZeroes() {
        setExcludeZeroes(false);
        return this;
    }    


    /**
     * @return the example
     */
    public T getExample() {
        return example;
    }

    /**
     * @param example the example to set
     */
    public void setExample(T example) {
        this.example = example;
    }

    /**
     * @return the matchMode
     */
    public MatchMode getMatchMode() {
        return matchMode;
    }

    /**
     * @param matchMode the matchMode to set
     */
    public void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    /**
     * @return the excludeNulls
     */
    public boolean isExcludeNulls() {
        return excludeNulls;
    }

    /**
     * @param excludeNulls the excludeNulls to set
     */
    public void setExcludeNulls(boolean excludeNulls) {
        this.excludeNulls = excludeNulls;
    }

    /**
     * @return the excludeProperties
     */
    public Collection<String> getExcludeProperties() {
        return excludeProperties;
    }

    /**
     * @param excludeProperties the excludeProperties to set
     */
    public void setExcludeProperties(Collection<String> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    /**
     * @return the excludeZeroes
     */
    public boolean isExcludeZeroes() {
        return excludeZeroes;
    }

    /**
     * @param excludeZeroes the excludeZeroes to set
     */
    public void setExcludeZeroes(boolean excludeZeroes) {
        this.excludeZeroes = excludeZeroes;
    }
}
