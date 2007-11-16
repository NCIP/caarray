/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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

import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.CollectionType;
import org.hibernate.type.Type;
import org.hibernate.util.ReflectHelper;

/**
 * Hibernate interceptor that keeps track of object changes and queues up lists of interesting objects that will require
 * appropriate synchronization with CSM. on post-flush, the appopriate CSM operations are then performed. The actual CSM
 * logic is in SecurityUtils
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SecurityInterceptor extends EmptyInterceptor {
    //
    // DEVELOPER NOTE: this class must be thread safe, because we enable it at the
    // SessionFactory level in hibernate.
    //

    private static final Log LOG = LogFactory.getLog(SecurityInterceptor.class);
    private static final long serialVersionUID = -2071964672876972370L;

    // Indicates that some activity of interest occurred
    private static final ThreadLocal<Object> MARKER = new ThreadLocal<Object>();
    // New objects being saved
    private static final ThreadLocal<Collection<Protectable>> NEWOBJS = new ThreadLocal<Collection<Protectable>>();
    // Objects being deleted
    private static final ThreadLocal<Collection<Protectable>> DELETEDOBJS =
            new ThreadLocal<Collection<Protectable>>();
    // Projects whose browsable status changed
    private static final ThreadLocal<Collection<Project>> BROWSABLE_CHANGES = new ThreadLocal<Collection<Project>>();
    // Projects whose biomaterial collections have changed
    private static final ThreadLocal<Collection<Project>> BIOMATERIAL_CHANGES =
            new ThreadLocal<Collection<Project>>();
    // new, changed or otherwise needing processing access profiles
    private static final ThreadLocal<Collection<AccessProfile>> PROFILES =
            new ThreadLocal<Collection<AccessProfile>>();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        boolean modified = false;
        // apply attribute-level security - project/experiment only
        // DEVELOPER NOTE: any attempt to flush this object will likely result in
        // errors as it has the effect of dissociating collections from the entity.
        // This is correct anyways as the user will not have WRITE access to the project,
        // but care must be taken not to cause phantom flushes
        if (entity instanceof Experiment || entity instanceof Project) {
            Protectable p = null;
            if (entity instanceof Project) {
                p = (Protectable) entity;
            } else {
                p = (Protectable) state[ArrayUtils.indexOf(propertyNames, "project")];
            }

            for (int i = 0; i < propertyNames.length; i++) {
                if (requiresReadPrivilege(entity, propertyNames[i])
                        && !SecurityUtils.canRead(p, UsernameHolder.getCsmUser())) {
                    if (types[i] instanceof CollectionType) {
                        CollectionType ct = (CollectionType) types[i];
                        state[i] = ct.instantiate(0);
                    } else if (!isPrimitiveProperty(entity, propertyNames[i])) {
                        state[i] = null;
                    } else {
                        LOG.warn("Could not null out primitive property " + propertyNames[i]);
                    }
                    modified = true;
                }
            }
        }
        return modified;
    }

    private static boolean requiresReadPrivilege(Object entity, String property) {
        // read privilege is required unless the Browseable property is present
        Method getterMethod = ReflectHelper.getGetter(entity.getClass(), property).getMethod();
        return !getterMethod.isAnnotationPresent(BrowseableProperty.class);
    }

    private static boolean isPrimitiveProperty(Object entity, String property) {
        return ReflectHelper.getGetter(entity.getClass(), property).getReturnType().isPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCollectionRecreate(Object collection, Serializable key) {
        onCollectionChange(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCollectionRemove(Object collection, Serializable key) {
        onCollectionChange(collection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCollectionUpdate(Object collection, Serializable key) {
        onCollectionChange(collection);
    }

    private void onCollectionChange(Object collection) {
        if (collection instanceof PersistentCollection) {
            PersistentCollection pc = (PersistentCollection) collection;

            if (pc.getOwner() instanceof AccessProfile) {
                saveEntityForProcessing(PROFILES, new ArrayList<AccessProfile>(), (AccessProfile) pc.getOwner());
            }
            if (pc.getOwner() instanceof Experiment && pc.getRole() != null
                    && StringUtils.substringAfterLast(pc.getRole(), ".").equals("samples")) {
                // inefficient but seems to be the cleanest way possible: force an update of all the
                // access profiles associated with the project to make sure new or removed samples
                // are appropriately updated for
                Experiment experiment = (Experiment) pc.getOwner();
                Collection<AccessProfile> allProfiles = experiment.getProject().getAllAccessProfiles();
                for (AccessProfile ap : allProfiles) {
                    saveEntityForProcessing(PROFILES, new ArrayList<AccessProfile>(), ap);
                }
                saveEntityForProcessing(BIOMATERIAL_CHANGES, new ArrayList<Project>(), experiment.getProject());
            }
        }
    }

    /**
     * Finds protectables and queues them to have protection elements assigned.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

        if (entity instanceof Protectable) {
            saveEntityForProcessing(NEWOBJS, new ArrayList<Protectable>(), (Protectable) entity);
        }

        if (entity instanceof AccessProfile) {
            saveEntityForProcessing(PROFILES, new ArrayList<AccessProfile>(), (AccessProfile) entity);
        }

        return false;
    }

    /**
     * Finds deleted protectables and queues them to have associated protection elements removed.
     * 
     * {@inheritDoc}
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity instanceof Protectable) {
            verifyWritePrivilege((Protectable) entity, UsernameHolder.getCsmUser());
            saveEntityForProcessing(DELETEDOBJS, new ArrayList<Protectable>(), (Protectable) entity);
        }
        if (entity instanceof ProtectableDescendent) {
            verifyWritePrivilege((ProtectableDescendent) entity, UsernameHolder.getCsmUser());
        }
    }

    /**
     * Finds modified protectables and queues them for sync with CSM.
     * 
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
            String[] propertyNames, Type[] types) {
        if (entity instanceof Protectable) {
            verifyWritePrivilege((Protectable) entity, UsernameHolder.getCsmUser());
        }
        if (entity instanceof ProtectableDescendent) {
            verifyWritePrivilege((ProtectableDescendent) entity, UsernameHolder.getCsmUser());
        }

        if (entity instanceof Project && previousState != null) {
            // figure out if browsable changed
            for (int i = 0; i < propertyNames.length; ++i) {
                if (propertyNames[i].equals("browsable") && !ObjectUtils.equals(currentState[i], previousState[i])) {
                    saveEntityForProcessing(BROWSABLE_CHANGES, new ArrayList<Project>(), (Project) entity);
                }
            }
        }
        if (entity instanceof AccessProfile) {
            saveEntityForProcessing(PROFILES, new ArrayList<AccessProfile>(), (AccessProfile) entity);
        }

        return false;
    }

    /**
     * Helper method for handling a matched entity from any of the lifecycle listener methods, and placing it in the
     * appropriate ThreadLocal holder for later processing.
     * 
     * @param threadLocal the holder for the appropriate list of entities
     * @param emptyCollection empty collection for initializing the holder
     * @param entity the entity
     */
    private <T> void saveEntityForProcessing(ThreadLocal<Collection<T>> threadLocal, Collection<T> emptyCollection,
            T entity) {
        if (threadLocal.get() == null) {
            threadLocal.set(emptyCollection);
            MARKER.set(new Object());
        }
        threadLocal.get().add(entity);
    }

    /**
     * Checks whether the given user has WRITE privilege to given object, and if not, throws a
     * PermissionDeniedException.
     * 
     * @param o the Object to check
     * @param user the user to check
     */
    private static void verifyWritePrivilege(PersistentObject o, User user) {
        if (!SecurityUtils.canWrite(o, user)) {
            throw new CallbackException("Attempted operation not allowed by security", new PermissionDeniedException(
                    o, SecurityUtils.WRITE_PRIVILEGE, user.getLoginName()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void postFlush(Iterator entities) {
        if (MARKER.get() == null) {
            MARKER.set(null);
            return;
        }

        try {
            SecurityUtils.handleNewProtectables(NEWOBJS.get());
            SecurityUtils.handleBrowsableChanges(BROWSABLE_CHANGES.get());
            SecurityUtils.handleBiomaterialChanges(BIOMATERIAL_CHANGES.get());
            SecurityUtils.handleDeleted(DELETEDOBJS.get());
            SecurityUtils.handleAccessProfiles(PROFILES.get());
        } finally {
            NEWOBJS.set(null);
            BROWSABLE_CHANGES.set(null);
            BIOMATERIAL_CHANGES.set(null);
            DELETEDOBJS.set(null);
            PROFILES.set(null);
        }
    }
}
