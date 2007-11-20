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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Utility classes for our project.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class CaArrayUtils {

    private static final Logger LOG = Logger.getLogger(CaArrayUtils.class);

    private CaArrayUtils() {
        // prevent instantiation;
    }

    /**
     * For each String bean property on o, if o is blank or empty,
     * converts that property to null.
     *
     * @param o object to convert properties on.
     */
    public static void blankStringPropsToNull(Object o) {
        List<Method[]> l = findGettersAndSetters(o);
        for (Method[] m : l) {
            if (m[1].getParameterTypes()[0].equals(String.class)) {
                try {
                    if (StringUtils.isBlank((String) m[0].invoke(o))) {
                        m[1].setAccessible(true);
                        m[1].invoke(o, (String) null);
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
     * Helper method that performs object graph cutting.  This method takes arbitrary
     * objects and sets (to null) subobjects that are in the domain model.  Specifically,
     * for each bean property (get* / set*):
     * <ul>
     * <li>For collection properties (ie, List, Set, or Map), an empty (List, Set, Map) is
     *     substituted for the current value.
     * <li>For non-collection domain properties (ie, gov.nih.nci.caarray.domain.*), the
     *     null value is substituted for the current value.
     * <li>All other properties are unmodified.
     * </ul>
     *
     * <p>As an example, consider domain classes A, B, and C that each have a single property,
     * id, accessable via getId and setId methods.  If we have:
     * <pre>
     * public class A {
     *   private B b;
     *   private Set&lt;C&gt; c;
     *   private int id;
     * }
     * </pre>
     *
     * <p>After a call to convertUponGet, A.B will be <code>null</code>, A.C will be an empty
     * <code>Set</code>, and id will retain the value it had prior to the call.  Null values
     * are handled gracefully.
     *
     * <em>Limitations ande exclusions:</em> Cutting does not occur for array types.  And
     * we set <em>all</em> collections to empty, not just collections of domain objects.
     *
     * @param val object to perform cutting on
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength") // Big comment causes us to go over
    public static void makeLeaf(Object val) {
        if (val == null) {
            return;
        }

        HibernateUtil.getCurrentSession().evict(val);

        for (Method[] m : findGettersAndSetters(val)) {
            Class<?> type = m[1].getParameterTypes()[0];
            Object param = null;
            if (Set.class.isAssignableFrom(type)) {
                param = Collections.EMPTY_SET;
            } else if (List.class.isAssignableFrom(type)) {
                param = Collections.EMPTY_LIST;
            } else if (Map.class.isAssignableFrom(type)) {
                param = Collections.EMPTY_MAP;
            } else if (Collection.class.isAssignableFrom(type)) {
                param = Collections.EMPTY_LIST;
            } else if (!type.isPrimitive() && !Serializable.class.isAssignableFrom(type)) {
                // Don't allow non-serializable to go over the wire
                LOG.debug("Cutting non-serializable object of type: " + type);
            } else if (!PersistentObject.class.isAssignableFrom(type)) {
                // Don't call setting for primitive types, or non-domain model objects
                continue;
            }

            try {
                m[1].setAccessible(true);
                m[1].invoke(val, new Object[] {param});
            } catch (Exception e) {
                // We catch here, rather than re-throwing.  This is a violation of our standard
                // practice, but it's done for good reason.  If invoking fails, that means
                // the value was unchanged.  Grid or API users that follow the standard idiom
                // of requering for child objects will likely never notice that the object
                // was not null.  If we re-throw, clients will get an error.  This makes
                // the system more robust for Java API users.
                LOG.error("Unable to call a setter: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Calls the makeLeaf method on all bean properties for T.  This has the effect of making
     * val the root of an object graph with exactly one level of domain objects.  Also makes all
     * non-serializable properties null.
     *
     * @param val object to perform cutting on
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static void makeChildrenLeaves(Object val) {
        if (val == null) {
            return;
        }

        HibernateUtil.getCurrentSession().evict(val);

        for (Method[] m : findGettersAndSetters(val)) {
            try {
                m[0].setAccessible(true);
                Object curObj = m[0].invoke(val);
                if (curObj instanceof Collection) {
                    Iterator<?> iter = ((Collection<?>) curObj).iterator();
                    while (iter.hasNext()) {
                        Object o = iter.next();
                        if (!Serializable.class.isAssignableFrom(o.getClass())) {
                            iter.remove();
                        } else {
                            makeLeaf(o);
                        }
                    }
                } else if (curObj != null && !Serializable.class.isAssignableFrom(curObj.getClass())) {
                    m[1].setAccessible(true);
                    m[1].invoke(val, new Object[] {null});
                } else {
                    makeLeaf(m[0].invoke(val));
                }
            } catch (Exception e) {
                // See comment above for reasoning on catching without rethrow
                LOG.error("Unable to call a getter: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Finds getter/setter pairs.
     * @param o object to inspect
     * @return getter / setter pairs, as list
     */
    private static List<Method[]> findGettersAndSetters(Object o) {
        List<Method[]> result = new ArrayList<Method[]>();
        if (o == null) {
            return result;
        }

        Class<?> clazz = o.getClass();
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method getter : methods) {
                if (getter.getName().startsWith("get") && getter.getParameterTypes().length == 0) {
                    for (Method setter : methods) {
                        if (setter.getName().equals('s' + getter.getName().substring(1))
                                && setter.getParameterTypes().length == 1
                                && Void.TYPE.equals(setter.getReturnType())
                                && getter.getReturnType().equals(setter.getParameterTypes()[0])) {
                            Method[] array = new Method[] {getter, setter};
                            result.add(array);
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }

        return result;
    }
}
