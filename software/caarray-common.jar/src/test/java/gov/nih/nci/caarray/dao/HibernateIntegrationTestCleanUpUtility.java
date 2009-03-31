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
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.authorization.domainobjects.ProtectionElement;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.EmptyInterceptor;
import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;

/**
 * Provides methods to remove objects created during Hibernate integration tests.
 */
public final class HibernateIntegrationTestCleanUpUtility {

    private static final Logger LOG = Logger.getLogger(HibernateIntegrationTestCleanUpUtility.class);
    private static List<Class<?>> classesToRemove;
    private static final String SELF_GROUP_PATTERN = "'" + SecurityUtils.SELF_GROUP_PREFIX + "%'";

    private static final Class<?>[] CSM_CLASSES_NOT_TO_REMOVE = {gov.nih.nci.security.authorization.domainobjects.Application.class,
        gov.nih.nci.security.authorization.domainobjects.Group.class,
        gov.nih.nci.security.authorization.domainobjects.Privilege.class,
        gov.nih.nci.security.authorization.domainobjects.ProtectionElement.class,
        gov.nih.nci.security.authorization.domainobjects.Role.class,
        gov.nih.nci.security.authorization.domainobjects.User.class,
        gov.nih.nci.security.authorization.domainobjects.FilterClause.class };

    private HibernateIntegrationTestCleanUpUtility() {
        super();
    }

    @SuppressWarnings("PMD")
    public static void cleanUp() {
        boolean cleanupComplete = doCleanUp();
        if (!cleanupComplete) {
            // This means we saw an object again, and that's a problem
            throw new IllegalStateException("Last unit test didn't fully clean up after itself");
        }
    }

    private static boolean doCleanUp() {
        if (classesToRemove == null) {
            retrieveClassMetadata();
        }
        int numIterations = classesToRemove.size() + 1;
        boolean done = false;
        for (int i = 0; i < numIterations && !done; i++) {
            done = true;
            for (Class<?> c : classesToRemove) {
                boolean removed = doCleanUp(c);
                done &= !removed;
            }
            done &= !doCleanUpProtectionElements();
            done &= !doCleanUpGroups();
        }
        return done;
    }

    private static boolean doCleanUp(Class<?> c) {
        return doCleanUp("DELETE FROM " + c.getName());
    }
    
    private static boolean doCleanUpGroups() {
        return doCleanUp("DELETE FROM " + Group.class.getName() + " where id > 8 and not (groupName like "
                + SELF_GROUP_PATTERN + ")");
    }

    private static boolean doCleanUpProtectionElements() {
        return doCleanUp("DELETE FROM " + ProtectionElement.class.getName() + " where id > 2");
    }

    private static boolean doCleanUp(String deleteSql) {
        Transaction tx = null;
        boolean removed = false;
        Session s = null;
        try {
            s = getSession();
            s.setFlushMode(FlushMode.MANUAL);
            tx = s.beginTransaction();
            disableForeignKeyChecks(s);
            int deletedObjs = s.createQuery(deleteSql).executeUpdate();
            if (deletedObjs > 0) {
                removed = true;
            }
            s.flush();
            tx.commit();
        } catch (DAOException deleteException) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", deleteException);
        } catch (HibernateException he) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.warn("Error cleaning up test objects.", he);
        } finally {
            s.close();
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    private static void retrieveClassMetadata() {
        Map<String, ClassMetadata> classMetadataMap = HibernateUtil.getSessionFactory().getAllClassMetadata();
        classesToRemove = new LinkedList<Class<?>>();
        for (ClassMetadata classMetadata : classMetadataMap.values()) {
            Class<?> persistentClass = classMetadata.getMappedClass(EntityMode.POJO);
            if (!ArrayUtils.contains(CSM_CLASSES_NOT_TO_REMOVE, persistentClass)) {
                classesToRemove.add(persistentClass);
            }
        }
    }

    private static Session getSession() {
        // we need a session that bypasses security, so override the security interceptor here
        return HibernateUtil.getSessionFactory().openSession(new EmptyInterceptor() {

            private static final long serialVersionUID = 1L;
        });
    }

    private static void disableForeignKeyChecks(Session s) {
        // this may be database-specific. for now, we know it works in mysql
        s.createSQLQuery("set foreign_key_checks = 0").executeUpdate();
    }
}
