//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.type.Type;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Hibernate interceptor that keeps track of object changes and queues up lists of interesting objects that will require
 * appropriate synchronization with CSM. on post-flush, the appropriate CSM operations are then performed. The actual
 * CSM logic is in SecurityUtils
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SecurityInterceptor extends EmptyInterceptor {
    //
    // DEVELOPER NOTE: this class must be thread safe, because we enable it at the
    // SessionFactory level in hibernate.
    //

    private static final long serialVersionUID = -2071964672876972370L;

    // Indicates that some activity of interest occurred
    private static final ThreadLocal<Object> MARKER = new ThreadLocal<Object>();
    // New objects being saved
    private static final ThreadLocal<Collection<Protectable>> NEWOBJS = new ThreadLocal<Collection<Protectable>>();
    // Objects being deleted
    private static final ThreadLocal<Collection<Protectable>> DELETEDOBJS =
            new ThreadLocal<Collection<Protectable>>();
    // Projects whose biomaterial collections have changed
    private static final ThreadLocal<Collection<Project>> BIOMATERIAL_CHANGES =
            new ThreadLocal<Collection<Project>>();
    // new, changed or otherwise needing processing access profiles
    private static final ThreadLocal<Collection<AccessProfile>> PROFILES =
            new ThreadLocal<Collection<AccessProfile>>();

    private static final ThreadLocal<Collection<CaArrayFile>> FILES = new ThreadLocal<Collection<CaArrayFile>>();

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
        if (isEnabled() && collection instanceof PersistentCollection) {
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

        if (isEnabled() && entity instanceof Protectable) {
            saveEntityForProcessing(NEWOBJS, new ArrayList<Protectable>(), (Protectable) entity);
        }

        if (isEnabled() && entity instanceof AccessProfile) {
            saveEntityForProcessing(PROFILES, new ArrayList<AccessProfile>(), (AccessProfile) entity);
        }

        if (isEnabled() && entity instanceof CaArrayFile) {
            saveEntityForProcessing(FILES, new ArrayList<CaArrayFile>(), (CaArrayFile) entity);
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
        if (isEnabled() && entity instanceof Protectable) {
            verifyWritePrivilege((Protectable) entity, UsernameHolder.getCsmUser());
            saveEntityForProcessing(DELETEDOBJS, new ArrayList<Protectable>(), (Protectable) entity);
        }
        if (isEnabled() && entity instanceof ProtectableDescendent) {
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
        if (isEnabled() && entity instanceof Protectable) {
            verifyWritePrivilege((Protectable) entity, UsernameHolder.getCsmUser());
        }
        if (isEnabled() && entity instanceof ProtectableDescendent) {
            verifyWritePrivilege((ProtectableDescendent) entity, UsernameHolder.getCsmUser());
        }
        if (isEnabled() && entity instanceof AccessProfile) {
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
            // punt any access profiles who belong to deleted projects
            if (PROFILES.get() != null && DELETEDOBJS.get() != null) {
                for (Iterator<AccessProfile> it = PROFILES.get().iterator(); it.hasNext();) {
                    AccessProfile ap = it.next();
                    if (DELETEDOBJS.get().contains(ap.getProject()) || DELETEDOBJS.get().contains(ap.getGroup())) {
                        it.remove();
                    }
                }
            }
            if (FILES.get() != null) {
                for (CaArrayFile caArrayFile : FILES.get()) {
                    cleanupFile(caArrayFile);
                }
            }

            SecurityUtils.handleNewProtectables(NEWOBJS.get());
            SecurityUtils.handleBiomaterialChanges(BIOMATERIAL_CHANGES.get(), NEWOBJS.get());
            SecurityUtils.handleDeleted(DELETEDOBJS.get());
            SecurityUtils.handleAccessProfiles(PROFILES.get());            
        } finally {
            clear();
        }
    }

    private void cleanupFile(CaArrayFile caArrayFile) {
        if (caArrayFile.getInputStreamToClose() != null) {
            IOUtils.closeQuietly(caArrayFile.getInputStreamToClose());
        }

        if (caArrayFile.getFileToDelete() != null) {
            caArrayFile.getFileToDelete().delete();
        }
    }

    /**
     * Clear the current thread's lists of entities awaiting processing.
     * To be called when a session is first opened, to prevent any stale entities from sticking around.
     */
    public static void clear() {
        MARKER.set(null);
        NEWOBJS.set(null);
        BIOMATERIAL_CHANGES.set(null);
        DELETEDOBJS.set(null);
        PROFILES.set(null);
        FILES.set(null);
    }

    private boolean isEnabled() {
        return !SecurityUtils.isPrivilegedMode();
    }
}
