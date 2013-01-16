//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services;

import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.MaxSerializableSize;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.util.PropertyAccessor;
import gov.nih.nci.caarray.util.ReflectionHelper;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.Inject;

/**
 * Class with utility methods for pruning entities to prepare them for transmittal over the wire. Caches reflection
 * data, so the same instance should be used to prune all entities for a single service method call. Not thread-safe.
 * 
 * @author dkokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class EntityPruner {
    private static final Logger LOG = Logger.getLogger(EntityPruner.class);
    @Inject
    private static CaArrayHibernateHelper hibernateHelper;
    @Inject
    private static ParsedDataPersister parsedDataPersister;
    private final Map<Class<?>, ReflectionHelper> classCache = new HashMap<Class<?>, ReflectionHelper>();

    /**
     * Helper method that performs object graph cutting. This method takes arbitrary objects and sets (to null)
     * subobjects that are in the domain model. Specifically, for each bean property (get* / set*):
     * <ul>
     * <li>For collection properties (ie, List, Set, or Map), an empty (List, Set, Map) is substituted for the current
     * value.
     * <li>For non-collection domain properties (ie, gov.nih.nci.caarray.domain.*), the null value is substituted for
     * the current value.
     * <li>All other properties are unmodified.
     * </ul>
     * 
     * <p>
     * As an example, consider domain classes A, B, and C that each have a single property, id, accessible via getId and
     * setId methods. If we have:
     * 
     * <pre>
     * public class A {
     *     private B b;
     *     private Set&lt;C&gt; c;
     *     private int id;
     * }
     * </pre>
     * 
     * <p>
     * After a call to convertUponGet, A.B will be <code>null</code>, A.C will be an empty <code>Set</code>, and id will
     * retain the value it had prior to the call. Null values are handled gracefully.
     * 
     * <em>Limitations and exclusions:</em> Cutting does not occur for array types. And we set <em>all</em> collections
     * to empty, not just collections of domain objects.
     * 
     * @param val object to perform cutting on
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void makeLeaf(Object val) {
        if (val == null) {
            return;
        }

        // prune column data.
        if (val instanceof AbstractDataColumn) {
            ((AbstractDataColumn) val).initializeArray(0);
        }

        // ensure we are dealing with the real underyling object, not a proxy
        final Object actualVal = hibernateHelper.unwrapProxy(val);
        applySecurityPolicies(actualVal);
        final ReflectionHelper helper = getOrCreateHelper(actualVal.getClass());
        boolean initialized = false;
        for (final PropertyAccessor accessor : helper.getAccessors()) {
            final Class<?> type = accessor.getType();
            Object param = null;
            if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
                param = CaArrayUtils.emptyCollectionOrMapFor(type);
            } else if (!type.isPrimitive() && !Serializable.class.isAssignableFrom(type)) {
                // Don't allow non-serializable to go over the wire
                LOG.debug("Cutting non-serializable object of type: " + type);
            } else if (!PersistentObject.class.isAssignableFrom(type)) {
                // Ensures that object is initialized and not a Hibernate proxy
                if (!initialized && !accessor.getter().getName().equals("getId")) {
                    try {
                        accessor.get(actualVal);
                        initialized = true;
                    } catch (final Exception e) {
                        LOG.error("Unable to call a getter: " + e.getMessage(), e);
                    }
                }
                continue;
            }

            try {
                accessor.set(actualVal, param);
            } catch (final Exception e) {
                // We catch here, rather than re-throwing. This is a violation of our standard
                // practice, but it's done for good reason. If invoking fails, that means
                // the value was unchanged. Grid or API users that follow the standard idiom
                // of requerying for child objects will likely never notice that the object
                // was not null. If we re-throw, clients will get an error. This makes
                // the system more robust for Java API users.
                LOG.error("Unable to call a setter: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Calls the makeLeaf method on all bean properties for val. This has the effect of making val the root of an object
     * graph with exactly one level of domain objects. Also makes all non-serializable properties null.
     * 
     * @param val object to perform cutting on
     */
    public void makeChildrenLeaves(Object val) {
        if (val == null) {
            return;
        }

        // make sure blobs are loaded
        if (val instanceof AbstractDataColumn) {
            parsedDataPersister.loadFromStorage((AbstractDataColumn) val);
        }

        applySecurityPolicies(val);
        final ReflectionHelper helper = getOrCreateHelper(val.getClass());
        for (final PropertyAccessor accessor : helper.getAccessors()) {
            try {
                final Object curObj = accessor.get(val);
                if (curObj instanceof Collection) {
                    handleCollection((Collection<?>) curObj, accessor);
                } else if (curObj instanceof Map) {
                    handleMap(val, accessor, (Map<?, ?>) curObj);
                } else if (curObj != null && !Serializable.class.isAssignableFrom(curObj.getClass())) {
                    accessor.set(val, null);
                } else {
                    makeLeaf(curObj);
                }
            } catch (final MaxCollectionSizeExceeededException e) {
                // we do want to allow this exception to propagate as it should prevent the API
                // from succeeding
                throw new IllegalStateException(e.getMessage()); // NOPMD
            } catch (final Exception e) {
                // See comment above for reasoning on catching without rethrow
                LOG.error("Unable to call a getter: " + e.getMessage(), e);
            }
        }
    }

    private void handleCollection(Collection<?> curObj, PropertyAccessor accessor)
            throws MaxCollectionSizeExceeededException {
        final MaxSerializableSize maxSize = accessor.getter().getAnnotation(MaxSerializableSize.class);
        if (maxSize != null && maxSize.value() < curObj.size()) {
            throw new MaxCollectionSizeExceeededException(
                    "Couldn't prepare result for serialization: collection too large for property "
                            + accessor.getter().getName());
        }
        final Iterator<?> iter = curObj.iterator();
        while (iter.hasNext()) {
            final Object o = iter.next();
            if (!Serializable.class.isAssignableFrom(o.getClass())) {
                iter.remove();
            } else {
                makeLeaf(o);
            }
        }
    }

    private void handleMap(Object val, PropertyAccessor accessor, Map<?, ?> curObj) throws IllegalAccessException,
            InvocationTargetException, MaxCollectionSizeExceeededException {
        // Maps are a pain. Because hibernate keeps track of the original state of a map,
        // we cannot simply trim all (key, value) pairs in the original map. Instead, we
        // create a new map, and put the trimmed entries into the new map, and then set
        // the new map to be the value in the object.

        final MaxSerializableSize maxSize = accessor.getter().getAnnotation(MaxSerializableSize.class);
        if (maxSize != null && maxSize.value() < curObj.size()) {
            throw new MaxCollectionSizeExceeededException(
                    "Couldn't prepare result for serialization: map too large for property "
                            + accessor.getter().getName());
        }

        final Map<Object, Object> newMap = new HashMap<Object, Object>();
        for (final Map.Entry<?, ?> e : curObj.entrySet()) {
            makeLeaf(e.getKey());
            makeLeaf(e.getValue());
            newMap.put(e.getKey(), e.getValue());
        }
        accessor.set(val, newMap);
    }

    private void applySecurityPolicies(Object entity) {
        if (entity instanceof AbstractCaArrayObject) {
            final AbstractCaArrayObject object = (AbstractCaArrayObject) entity;
            final Set<SecurityPolicy> policies =
                    object.getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            if (!policies.isEmpty()) {
                SecurityPolicy.applySecurityPolicies(object, policies);
            }
        }
    }

    private ReflectionHelper getOrCreateHelper(Class<?> c) {
        ReflectionHelper helper = this.classCache.get(c);
        if (helper == null) {
            helper = CaArrayUtils.createReflectionHelper(c);
            this.classCache.put(c, helper);
        }
        return helper;
    }

    /**
     * Exception thrown when a collection on an object being pruned exceeds the maximum size defined by an annotation.
     * 
     * @author dkokotov
     */
    public static final class MaxCollectionSizeExceeededException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Create new exception with given message.
         * 
         * @param msg the message
         */
        public MaxCollectionSizeExceeededException(String msg) {
            super(msg);
        }
    }
}
