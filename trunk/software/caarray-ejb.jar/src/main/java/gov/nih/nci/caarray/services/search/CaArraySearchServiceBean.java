/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb.jar Software and any
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
package gov.nih.nci.caarray.services.search;

import gov.nih.nci.caarray.dao.DAOException;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.google.inject.Inject;

/**
 * Session bean that searches for caArray entities based on various types of criteria.
 * 
 * @author Rashmi Srinivasa
 */
@Stateless
@Remote(CaArraySearchService.class)
@PermitAll
@Interceptors({AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class,
        InjectionInterceptor.class })
@TransactionTimeout(CaArraySearchServiceBean.TIMEOUT_SECONDS)
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CaArraySearchServiceBean implements CaArraySearchService {

    private static final Logger LOG = Logger.getLogger(CaArraySearchServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;

    private SearchDao searchDao;

    /**
     * 
     * @param searchDao the SearchDao dependency
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayObject> List<T> search(final T entityExample) {
        return search(entityExample, true, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractCaArrayObject> List<T> search(final T entityExample, boolean excludeNulls,
            boolean excludeZeroes) {
        List<T> retrievedList = new ArrayList<T>();
        if (entityExample == null) {
            LOG.error("Search was called with null example entity.");
            return retrievedList;
        }

        try {
            final ExampleSearchCriteria<T> ex = new ExampleSearchCriteria<T>(entityExample);
            ex.setExcludeNulls(excludeNulls);
            ex.setExcludeZeroes(excludeZeroes);
            retrievedList = this.searchDao.queryEntityByExample(ex);
        } catch (final DAOException e) {
            LOG.error("DAO exception while querying by example: ", e);
        } catch (final Exception e) {
            LOG.error("Exception while querying by example: ", e);
        }

        return retrievedList;
    }

    /**
     * Searches for entities based on the given CQL query.
     * 
     * @param cqlQuery the HQL (Hibernate Query Language) string to use as search criteria.
     * 
     * @return the matching entities.
     */
    @Override
    public List<?> search(final CQLQuery cqlQuery) {
        List<?> retrievedList = new ArrayList<Object>();
        if (cqlQuery == null) {
            LOG.error("Search was called with null CQL query.");
            return retrievedList;
        }

        try {
            retrievedList = this.searchDao.query(cqlQuery);
        } catch (final DAOException e) {
            LOG.error("DAO exception while querying by CQL: ", e);
        } catch (final Exception e) {
            LOG.error("Exception while querying by CQL: ", e);
        }

        return retrievedList;
    }
}
