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
package gov.nih.nci.caarray.services.external.v1_0.search;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.query.LimitOffset;
import gov.nih.nci.caarray.external.v1_0.query.SearchResult;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Function;

/**
 * Generic iterator for iterating through all of the results for an API search. Will automatically take care
 * of retrieving results in batches until all the results have been obtained.
 * 
 * @param <E> the type of the result objects, and the element type for this iterator.
 * @author dkokotov
 */
public class SearchResultIterator<E extends AbstractCaArrayEntity> implements Iterator<E> {
    private SearchResult<E> currentResult;
    private LimitOffset currentPage;
    private int currentResultIndex;
    private Function<LimitOffset, SearchResult<E>> searchFunction;

    
    /**
     * Constructor for starting the iteration with the first element.
     * 
     * @param searchFunction the search function
     */
    public SearchResultIterator(Function<LimitOffset, SearchResult<E>> searchFunction) {
        this(searchFunction, 0);
    }
    
    /**
     * Constructor for starting the iteration with the element at given index.
     * 
     * @param searchFunction the search function
     * @param startIndex the index of the element to start at (0-based).
     */
    public SearchResultIterator(Function<LimitOffset, SearchResult<E>> searchFunction, int startIndex) {
        this.searchFunction = searchFunction;
        this.currentPage = new LimitOffset(-1, startIndex);
    }

    /**
     * {@inheritDoc}
     * 
     * Note that this method can cause the next batch of results to be retrieved, and thus take a long time to execute.
     */
    public boolean hasNext() {
        if (currentResult == null || currentResultIndex == currentResult.getResults().size()
                && !currentResult.isFullResult()) {
            getNext();
        }
        return currentResultIndex < currentResult.getResults().size();
    }
    
    /**
     * @return true if this iterator has more elements and has the next element available locally, e.g. calling next()
     * will not result in a remote API call; false otherwise.
     */
    public boolean hasNextLocally() {
        return currentResult != null && currentResultIndex < currentResult.getResults().size();
    }

    /**
     * {@inheritDoc}
     */
    public E next() {
        if (!hasNext()) {
            throw new NoSuchElementException();            
        }
     
        return currentResult.getResults().get(currentResultIndex++);
    }

    private void getNext() {
        if (currentResult != null) {
            currentPage.setOffset(currentResult.getFirstResultOffset() + currentResult.getResults().size());
        }
        currentResult = searchFunction.apply(currentPage);
        currentResultIndex = 0;
    }

    /**
     * {@inheritDoc}
     * 
     * This iterator does not support element removal, thus this method will always throw NotImplementedException.
     */
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }
}