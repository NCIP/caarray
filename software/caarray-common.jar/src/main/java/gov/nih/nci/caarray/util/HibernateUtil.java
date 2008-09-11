/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-app
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-app Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-app Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-app Software; (ii) distribute and
 * have distributed to and by third parties the caarray-app Software and any
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
package gov.nih.nci.caarray.util;

import gov.nih.nci.caarray.security.SecurityInterceptor;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.security.authorization.instancelevel.InstanceLevelSecurityHelper;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * Utility class to create and retrieve Hibernate sessions.  Most methods are pass-throughs to {@link HibernateHelper},
 * except for the methods involving filters.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("unchecked")
public final class HibernateUtil {

    private static final HibernateHelper HIBERNATE_HELPER = new HibernateHelper(SecurityUtils.getAuthorizationManager(),
            new NamingStrategy(), new SecurityInterceptor());

    private static boolean filtersEnabled = true;

    /**
     * A private constructor because this class should not be instantiated. All callable methods are static methods.
     */
    private HibernateUtil() {
    }

    /**
     * Method that must be called prior to usage of HibernateUtil.
     * Intended to be called at system startup, such as from a web application startup listener.
     */
    public static void init() {
        SecurityUtils.init();
        HIBERNATE_HELPER.reinitializeCsmFilters(SecurityUtils.getAuthorizationManager());
    }

    /**
     * Get the hibernate helper.
     * @return the helper.
     */
    public static HibernateHelper getHibernateHelper() {
        return HIBERNATE_HELPER;
    }

    /**
     * Returns the Hibernate configuration.
     *
     * @return a Hibernate configuration.
     * @see HibernateHelper#getConfiguration()
     */
    public static Configuration getConfiguration() {
        return getHibernateHelper().getConfiguration();
    }

    /**
     * Returns the current Hibernate session. Note that this returns a special session that can be used only in the
     * context of a transaction. (Assuming that the hibernate properties are set to use a JTA or JDBC transaction
     * factory.)
     *
     * @return a Hibernate session.
     * @see HibernateHelper#getCurrentSession()
     */
    public static Session getCurrentSession() {
        Session result = getHibernateHelper().getCurrentSession();
        if (filtersEnabled) {
            InstanceLevelSecurityHelper.initializeFilters(UsernameHolder.getUser(), result, SecurityUtils
                    .getAuthorizationManager());
        }
        return result;
    }

    /**
     * Starts a transaction on the current Hibernate session. Intended for use in
     * unit tests - DAO / Service layer logic should rely on container-managed transactions
     *
     * @return a Hibernate session.
     * @see HibernateHelper#beginTransaction()
     */
    public static Transaction beginTransaction() {
        return getHibernateHelper().beginTransaction();
    }

    /**
     * Checks if the transaction is active and then rolls it back.
     *
     * @param tx the Transaction to roll back.
     * @see HibernateHelper#rollbackTransaction(Transaction)
     */
    public static void rollbackTransaction(Transaction tx) {
        getHibernateHelper().rollbackTransaction(tx);
    }

    /**
     * @param enable enabled. This should generally only be called via test code.
     */
    public static void enableFilters(boolean enable) {
        filtersEnabled = enable;
    }

    /**
     * Open a hibernate session and bind it as the current session via
     * {@link org.hibernate.context.ManagedSessionContext#bind(org.hibernate.classic.Session)}. The hibernate property
     * "hibernate.current_session_context_class" must be set to "managed" for this to have effect This method should be
     * called from within an Interceptor or Filter type class that is setting up the scope of the Session. This method
     * should then call {@link HibernateUtil#unbindAndCleanupSession()} when the scope of the Session is expired.
     *
     * @see HibernateHelper#openAndBindSession()
     */
    public static void openAndBindSession() {
        SecurityInterceptor.clear();
        getHibernateHelper().openAndBindSession();
    }

    /**
     * Close the current session and unbind it via
     * {@link org.hibernate.context.ManagedSessionContext#unbind(SessionFactory)}. The hibernate
     * property "hibernate.current_session_context_class" must be set to "managed" for this to have effect. This method
     * should be called from within an Interceptor or Filter type class that is setting up the scope of the Session,
     * when this scope is about to expire.
     * @see HibernateHelper#unbindAndCleanupSession()
     */
    public static void unbindAndCleanupSession() {
        getHibernateHelper().unbindAndCleanupSession();
    }

    /**
     * @return hibernate session factory
     * @see HibernateHelper#getSessionFactory()
     */
    public static SessionFactory getSessionFactory() {
        return getHibernateHelper().getSessionFactory();
    }

    /**
     * Do something in an unfiltered session.
     * @param uc callback class
     * @return the result
     */
    public static Object doUnfiltered(UnfilteredCallback uc) {
        Session session = getCurrentSession();
        disableFilters(session);
        try {
            return uc.doUnfiltered(session);
        } finally {
            enableFilters(session);
        }
    }

    private static void disableFilters(Session session) {
        Set<String> filters = session.getSessionFactory().getDefinedFilterNames();
        for (String filterName : filters) {
            session.disableFilter(filterName);
        }
    }

    private static void enableFilters(Session session) {
        if (filtersEnabled) {
            InstanceLevelSecurityHelper.initializeFilters(UsernameHolder.getUser(), session, SecurityUtils
                    .getAuthorizationManager());
        }
    }

    /**
     * Reinitialize the hibernate filters from the database.
     */
    public static void reinitializeCsmFilters() {
        getHibernateHelper().reinitializeCsmFilters(SecurityUtils.getAuthorizationManager());
    }

}
