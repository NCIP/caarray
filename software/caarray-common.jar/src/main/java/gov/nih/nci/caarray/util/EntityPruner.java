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

import gov.nih.nci.caarray.domain.MaxSerializableSize;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Class with utility methods for pruning entities to prepare them for transmittal over the wire. Caches reflection
 * data, so the same instance should be used to prune all entities for a single service method call. Not thread-safe.
 *
 * @author dkokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class EntityPruner {
    private static final Logger LOG = Logger.getLogger(EntityPruner.class);
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
     * After a call to convertUponGet, A.B will be <code>null</code>, A.C will be an empty <code>Set</code>, and
     * id will retain the value it had prior to the call. Null values are handled gracefully.
     *
     * <em>Limitations and exclusions:</em> Cutting does not occur for array types. And we set <em>all</em>
     * collections to empty, not just collections of domain objects.
     *
     * @param val object to perform cutting on
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    // Big comment causes us to go over
    public void makeLeaf(Object val) {
        if (val == null) {
            return;
        }

        ReflectionHelper helper = getOrCreateHelper(val.getClass());
        boolean initialized = false;
        for (PropertyAccessor accessor : helper.getAccessors()) {
            Class<?> type = accessor.getType();
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
                        accessor.get(val);
                        initialized = true;
                    } catch (Exception e) {
                        LOG.error("Unable to call a getter: " + e.getMessage(), e);
                    }
                }
                continue;
            }

            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Calling method: " + accessor.setter().getName());
                }
                accessor.set(val, param);
            } catch (Exception e) {
                // We catch here, rather than re-throwing. This is a violation of our standard
                // practice, but it's done for good reason. If invoking fails, that means
                // the value was unchanged. Grid or API users that follow the standard idiom
                // of requering for child objects will likely never notice that the object
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

        ReflectionHelper helper = getOrCreateHelper(val.getClass());
        for (PropertyAccessor accessor : helper.getAccessors()) {
            try {
                Object curObj = accessor.get(val);
                if (curObj instanceof Collection) {
                    handleCollection((Collection<?>) curObj, accessor);
                } else if (curObj instanceof Map) {
                    handleMap(val, accessor, (Map<?, ?>) curObj);
                } else if (curObj != null && !Serializable.class.isAssignableFrom(curObj.getClass())) {
                    accessor.set(val, null);
                } else {
                    makeLeaf(curObj);
                }
            } catch (MaxCollectionSizeExceeededException e) {
                // we do want to allow this exception to propagate as it should prevent the API
                // from succeeding
                throw new IllegalStateException(e.getMessage()); // NOPMD
            } catch (Exception e) {
                // See comment above for reasoning on catching without rethrow
                LOG.error("Unable to call a getter: " + e.getMessage(), e);
            }
        }
    }

    private void handleCollection(Collection<?> curObj, PropertyAccessor accessor)
            throws MaxCollectionSizeExceeededException {
        MaxSerializableSize maxSize = accessor.getter.getAnnotation(MaxSerializableSize.class);
        if (maxSize != null && maxSize.value() < curObj.size()) {
            throw new MaxCollectionSizeExceeededException(
                    "Couldn't prepare result for serialization: collection too large for property "
                            + accessor.getter().getName());
        }
        Iterator<?> iter = curObj.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
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

        MaxSerializableSize maxSize = accessor.getter.getAnnotation(MaxSerializableSize.class);
        if (maxSize != null && maxSize.value() < curObj.size()) {
            throw new MaxCollectionSizeExceeededException(
                    "Couldn't prepare result for serialization: map too large for property "
                            + accessor.getter().getName());
        }

        Map<Object, Object> newMap = new HashMap<Object, Object>();
        for (Map.Entry<?, ?> e : curObj.entrySet()) {
            makeLeaf(e.getKey());
            makeLeaf(e.getValue());
            newMap.put(e.getKey(), e.getValue());
        }
        accessor.set(val, newMap);
    }

    /**
     * For each String bean property on o, if o is blank or empty,
     * converts that property to null.
     *
     * @param o object to convert properties on.
     */
    public static void blankStringPropsToNull(Object o) {
        if (o == null) {
            return;
        }

        ReflectionHelper helper = createReflectionHelper(o.getClass());
        for (PropertyAccessor accessor : helper.getAccessors()) {
            if (accessor.getType().equals(String.class)) {
                try {
                    if (StringUtils.isBlank((String) accessor.get(o))) {
                        accessor.set(o, null);
                    }
                } catch (IllegalArgumentException e) {
                    LOG.debug(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    LOG.debug(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Finds getter/setter pairs.
     *
     * @param o object to inspect
     * @return getter / setter pairs, as list
     */
    @SuppressWarnings("PMD")

    private static ReflectionHelper createReflectionHelper(Class<?> clazz) {
        List<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>();

        Class<?> currentClass = clazz;
        while (currentClass != null) {
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method getter : methods) {
                if (getter.getName().startsWith("get") && getter.getParameterTypes().length == 0) {
                    for (Method setter : methods) {
                        if (setter.getName().equals('s' + getter.getName().substring(1))
                                && setter.getParameterTypes().length == 1 && Void.TYPE.equals(setter.getReturnType())
                                && getter.getReturnType().equals(setter.getParameterTypes()[0])) {
                            getter.setAccessible(true);
                            setter.setAccessible(true);
                            accessors.add(new PropertyAccessor(getter, setter));
                        }
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return new ReflectionHelper(accessors.toArray(new PropertyAccessor[accessors.size()]));
    }

    private ReflectionHelper getOrCreateHelper(Class<?> c) {
        ReflectionHelper helper = classCache.get(c);
        if (helper == null) {
            helper = createReflectionHelper(c);
            classCache.put(c, helper);
        }
        return helper;
    }

    /**
     * Utility class holding a set of PropertyAcessors for the properties of a class.
     * @author dkokotov
     */
    @SuppressWarnings("PMD")
    private static final class ReflectionHelper {
        private final PropertyAccessor[] accessors;

        /**
         * @param accessors the set of accessors for the properties of a class
         */
        public ReflectionHelper(PropertyAccessor[] accessors) {
            this.accessors = accessors;
        }

        /**
         * @return the accessors
         */
        public PropertyAccessor[] getAccessors() {
            return accessors;
        }

    }

    /**
     * Utility class representing the getter/setter pair for a property.
     * @author dkokotov
     */
    @SuppressWarnings("PMD")
    private static final class PropertyAccessor {
        private final Method getter;
        private final Method setter;

        /**
         * @param getter the getter method for the property
         * @param setter the setter method for the property
         */
        public PropertyAccessor(Method getter, Method setter) {
            this.getter = getter;
            this.setter = setter;
        }

        /**
         * @return the type of the property of this accessor
         */
        public Class<?> getType() {
            return getter.getReturnType();
        }

        /**
         * Set the value of the property of this accessor on the given object to the given value.
         * @param target the target object
         * @param val the value to set the property to
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         */
        public void set(Object target, Object val) throws IllegalAccessException, InvocationTargetException {
            setter.invoke(target, val);
        }

        /**
         * Get the value of the property of this accessor from the given object.
         * @param target the object from which to get the value of the property
         * @return the property value
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         */
        public Object get(Object target) throws IllegalAccessException, InvocationTargetException {
            return getter.invoke(target);
        }

        /**
         * @return the getter method for this property
         */
        public Method getter() {
            return getter;
        }

        /**
         * @return the setter method for this property
         */
        public Method setter() {
            return setter;
        }
    }

    /**
     * Exception thrown when a collection on an object being pruned exceeds the maximum size
     * defined by an annotation.
     * @author dkokotov
     */
    private static final class MaxCollectionSizeExceeededException extends Exception {
        private static final long serialVersionUID = 1L;

        /**
         * Create new exception with given message.
         * @param msg the message
         */
        public MaxCollectionSizeExceeededException(String msg) {
            super(msg);
        }
    }
}
