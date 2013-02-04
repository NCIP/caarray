//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.Inject;

/**
 * Ensures that retrieved entities are ready for transport, including correct
 * 'cutting' of object graphs.
 *
 * @see EntityPruner#makeChildrenLeaves(Object)
 */
public class EntityConfiguringInterceptor {

    private static final Logger LOG = Logger.getLogger(EntityConfiguringInterceptor.class);
    @Inject private static CaArrayHibernateHelper hibernateHelper; 

    /**
     * Ensures that any object returned and its direct associated entities are loaded.
     *
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings({ "PMD.SignatureDeclareThrowsException", "unchecked", "ucd" }) 
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        // make the call to the underlying method.  This method (prepareReturnValue) wraps the intended method.
        Object returnValue = invContext.proceed();

        // flush any changes made (ie, DataSet population) to this point.  We're going to modify
        // hibernate objects in ways we /don't/ want flushed in prepareEntity

        // FIXME: we cannot flush any changes here, because the SecurityPolicyPostLoadEventListener makes
        // changes to objects (like Project) that hibernate sees as modifications.  The anonymous user
        // does not have permission to modify the objects, so we get an exception at this point.
        // Commenting this call out for now, because the only time we'd need to flush changes is for
        // parse-on-demand, which we don't currently do.
//        hibernateHelper.getCurrentSession().flush();

        if (returnValue instanceof Collection) {
            prepareEntities((Collection<Object>) returnValue);
        } else {
            prepareEntity(returnValue);
        }

        // keep hibernate from performing write behind of all the cutting we just did
        hibernateHelper.getCurrentSession().clear();

        return returnValue;
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")  // Lots of explanatory comments
    private void prepareEntities(Collection<Object> collection) {
        // Create a temporary collection for the cut objects.  We will replace the passed-in
        // collection elements with the cut elements in here.
        Collection<Object> tmpCollection = new ArrayList<Object>(collection.size());
        EntityPruner pruner = new EntityPruner();
        // A note on this loop: We refresh each element first because the session is cleared
        // each iteration, which would potentially mean that the entities un-initialized
        // collection properties would be unavailable.
        //
        // We call clear because the hibernate session could be potentially HUGE after getting
        // certain collections on objects (like ArrayDesignDetails.designElements, probes, etc.)
        //
        // Together, these calls keep the hibernate session small, and ensure that we won't get
        // LazyInitializationExceptions
        Iterator<Object> it = collection.iterator();
        while (it.hasNext()) {
            Object toCut = it.next();
            if (toCut instanceof PersistentObject) {
                // some test code has non-persistentObject entities, which can't work here
                toCut = hibernateHelper.getCurrentSession().get(toCut.getClass(),
                                                              ((PersistentObject) toCut).getId());
            }
            prepareEntity(toCut, pruner);
            hibernateHelper.getCurrentSession().clear();
            tmpCollection.add(toCut);
            try {
                it.remove();
            } catch (UnsupportedOperationException uoe) {
                // Not fatal, or even so bad, but log anyways.
                LOG.debug("Could not remove from iterator: " + uoe, uoe);
            }
        }
        // Replace collection's elements with the cut elements.  Cutting in place will not work!
        collection.clear();
        collection.addAll(tmpCollection);
    }

    private void prepareEntity(Object entity) {
        prepareEntity(entity, new EntityPruner());
    }

    private void prepareEntity(Object entity, EntityPruner pruner) {
        pruner.makeChildrenLeaves(entity);
    }
}
