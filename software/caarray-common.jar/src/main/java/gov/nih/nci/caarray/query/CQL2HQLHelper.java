/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
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
package gov.nih.nci.caarray.query;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper methods for CQL-to-HQL query converter.
 * The substance of this class is taken from the caCORE SDK.
 *
 * @author Rashmi Srinivasa
 */
public final class CQL2HQLHelper {
    private static Map<CQLPredicate, String> predicateValues;
    private static Map<String, Class<?>> classCache;
    private static Map<String, Field[]> fieldCache;
    private static Map<String, Method[]> setterMethodCache;
    private static final int NUM_LETTERS_IN_SET = 3;

    /**
     * Private constructor to prevent instantiation.
     */
    private CQL2HQLHelper() {
        super();
    }

    /**
     * Gets the role name of an association relative to its parent class.
     *
     * @param parentName the class name of the parent of the association
     * @param assoc the associated object restriction
     * @return the role name of the associated object
     * @throws QueryException if role name could not be obtained.
     */
    static String getRoleName(String parentName, CQLAssociation assoc) throws QueryException {
        String roleName = assoc.getTargetRoleName();
        if (roleName == null) {
            // determine role based on object's type
            Class<?> parentClass = getParentClass(parentName);
            String associationTypeName = assoc.getName();

            // search the fields of the right type
            roleName = searchFields(parentClass, associationTypeName);

            if (roleName == null) {
                // search for a setter method
                roleName = searchSetterMethods(parentClass, associationTypeName);
            }
        }
        return roleName;
    }

    private static String searchSetterMethods(Class<?> parentClass, String associationTypeName) throws QueryException {
        String roleName = null;

        Method[] setters = getSettersForTypeFromCache(parentClass, associationTypeName);
        if (setters.length == 1) {
            String temp = setters[0].getName().substring(NUM_LETTERS_IN_SET);
            if (temp.length() == 1) {
                roleName = String.valueOf(Character.toLowerCase(temp.charAt(0)));
            } else {
                roleName = Character.toLowerCase(temp.charAt(0)) + temp.substring(1);
            }
        } else if (setters.length > 1) {
            // more than one association found
            throw new QueryException("Association from " + parentClass.getName() + " to " + associationTypeName
                    + " is ambiguous: Specify a role name");
        }
        return roleName;
    }

    private static String searchFields(Class<?> parentClass, String associationTypeName)
      throws QueryException {
        String roleName = null;

        Field[] typedFields = getFieldsOfTypeFromCache(parentClass, associationTypeName);
        if (typedFields.length == 1) {
            // found one and only one field
            roleName = typedFields[0].getName();
        } else if (typedFields.length > 1) {
            // more than one association found
            throw new QueryException("Association from " + parentClass.getName() + " to " + associationTypeName
                    + " is ambiguous: Specify a role name");
        }
        return roleName;
    }

    private static Class<?> getParentClass(String parentName) throws QueryException {
        Class<?> parentClass = null;
        try {
            parentClass = getClassFromCache(parentName);
        } catch (Exception ex) {
            throw new QueryException("Could not load class: " + ex.getMessage(), ex);
        }
        return parentClass;
    }

    /**
     * Converts a predicate to its HQL string equivalent.
     */
    static String convertPredicate(CQLPredicate p) {
        initPredicateValues();
        return predicateValues.get(p);
    }

    private static synchronized void initPredicateValues() {
        if (predicateValues == null) {
            predicateValues = new HashMap<CQLPredicate, String>();
            predicateValues.put(CQLPredicate.EQUAL_TO, "=");
            predicateValues.put(CQLPredicate.GREATER_THAN, ">");
            predicateValues.put(CQLPredicate.GREATER_THAN_EQUAL_TO, ">=");
            predicateValues.put(CQLPredicate.LESS_THAN, "<");
            predicateValues.put(CQLPredicate.LESS_THAN_EQUAL_TO, "<=");
            predicateValues.put(CQLPredicate.LIKE, "LIKE");
            predicateValues.put(CQLPredicate.NOT_EQUAL_TO, "!=");
        }
    }

    static Class<?> getClassFromCache(String name) throws ClassNotFoundException {
        initClassCache();
        Class<?> klass = classCache.get(name);
        if (klass == null) {
            klass = Class.forName(name);
            classCache.put(name, klass);
        }
        return klass;
    }

    private static synchronized void initClassCache() {
        if (classCache == null) {
            classCache = new HashMap<String, Class<?>>();
        }
    }

    static Field[] getFieldsOfTypeFromCache(Class<?> klass, String name) {
        String key = klass.getName() + "," + name;
        initFieldCache();
        Field[] fieldCollection = fieldCache.get(name);
        if (fieldCollection == null) {
            fieldCollection = getFieldsOfType(klass, name);
            fieldCache.put(key, fieldCollection);
        }
        return fieldCollection;
    }

    private static synchronized void initFieldCache() {
        if (fieldCache == null) {
            fieldCache = new HashMap<String, Field[]>();
        }
    }

    static Method[] getSettersForTypeFromCache(Class<?> klass, String name) {
        String key = klass.getName() + "," + name;
        initSetterMethodCache();
        Method[] methodCollection = setterMethodCache.get(name);
        if (methodCollection == null) {
            methodCollection = getSettersForType(klass, name);
            setterMethodCache.put(key, methodCollection);
        }
        return methodCollection;
    }

    private static synchronized void initSetterMethodCache() {
        if (setterMethodCache == null) {
            setterMethodCache = new HashMap<String, Method[]>();
        }
    }

    /**
     * Gets all fields from a class and its superclasses of a given type.
     *
     * @param clazz the class to explore for typed fields.
     * @param typeName the name of the type to search for.
     * @return an array of fields from the class and its superclasses, of the given type.
     */
    static Field[] getFieldsOfType(Class<?> clazz, String typeName) {
        Set<Field> allFields = new HashSet<Field>();
        Class<?> checkClass = clazz;
        while (checkClass != null) {
            addDeclaredFields(allFields, checkClass);
            checkClass = checkClass.getSuperclass();
        }
        List<Field> namedFields = new ArrayList<Field>();
        Iterator<Field> fieldIter = allFields.iterator();
        while (fieldIter.hasNext()) {
            Field field = fieldIter.next();
            if (field.getType().getName().equals(typeName)) {
                namedFields.add(field);
            }
        }
        Field[] fieldArray = new Field[namedFields.size()];
        namedFields.toArray(fieldArray);
        return fieldArray;
    }

    private static void addDeclaredFields(Set<Field> allFields, Class<?> checkClass) {
        Field[] classFields = checkClass.getDeclaredFields();
        if (classFields != null) {
            for (int i = 0; i < classFields.length; i++) {
                allFields.add(classFields[i]);
            }
        }
    }

    static Method[] getSettersForType(Class<?> clazz, String typeName) {
        Set<Method> allMethods = new HashSet<Method>();
        Class<?> checkClass = clazz;
        while (checkClass != null) {
            addDeclaredMethods(typeName, allMethods, checkClass);
            checkClass = checkClass.getSuperclass();
        }
        Method[] methodArray = new Method[allMethods.size()];
        allMethods.toArray(methodArray);
        return methodArray;
    }

    private static void addDeclaredMethods(String typeName, Set<Method> allMethods, Class<?> checkClass) {
        Method[] classMethods = checkClass.getDeclaredMethods();
        for (int i = 0; i < classMethods.length; i++) {
            Method current = classMethods[i];
            addIfPublicAndHasRightParamType(typeName, allMethods, current);
        }
    }

    private static void addIfPublicAndHasRightParamType(String typeName, Set<Method> allMethods, Method current) {
        if ((current.getName().startsWith("set")) && (Modifier.isPublic(current.getModifiers()))) {
            Class<?>[] paramTypes = current.getParameterTypes();
            if ((paramTypes.length == 1) && (paramTypes[0].getName().equals(typeName))) {
                allMethods.add(current);
            }
        }
    }

    static boolean existInheritance(String parent, String child) throws QueryException {
        try {
            Class<?> parentClass = getClassFromCache(parent);
            Class<?> childClass = getClassFromCache(child);
            if (childClass.getSuperclass().getName().equals(parent)
                    || parentClass.getSuperclass().getName().equals(child)) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            throw new QueryException("Could not load class: " + ex.getMessage(), ex);
        }
    }

    /**
     * Gets all fields from a class and its superclasses.
     *
     * @param clazz the class to explore for typed fields.
     * @return an array of fields from the class and its superclasses.
     */
    static Field[] getFields(Class<?> clazz) {
        Set<Field> allFields = new HashSet<Field>();
        Class<?> checkClass = clazz;
        while (checkClass != null) {
            Field[] classFields = checkClass.getDeclaredFields();
            if (classFields != null) {
                for (int i = 0; i < classFields.length; i++) {
                    allFields.add(classFields[i]);
                }
            }
            checkClass = checkClass.getSuperclass();
        }
        Field[] fieldArray = new Field[allFields.size()];
        allFields.toArray(fieldArray);
        return fieldArray;
    }

    static String getDataType(String className, String attribName) throws QueryException {
        Field[] classFields;
        try {
            classFields = getFields(getClassFromCache(className));
            for (int i = 0; i < classFields.length; i++) {
                if (classFields[i].getName().equals(attribName)) {
                    return classFields[i].getType().getName();
                }
            }
            return "";
        } catch (ClassNotFoundException e) {
            throw new QueryException("Could not determine type of attribute " + attribName
              + " in class " + className, e);
        }
    }

    static boolean isCollection(String className, String attribName) throws QueryException {
        Field[] classFields;
        try {
            classFields = getFields(getClassFromCache(className));
            for (int i = 0; i < classFields.length; i++) {
                if (classFields[i].getName().equals(attribName)) {
                    Class<?> type = classFields[i].getType();
                    if ("java.util.Collection".equals(type.getName())) {
                        return true;
                    }
                    return false;
                }
            }
            return false;
        } catch (ClassNotFoundException e) {
            throw new QueryException("Could not determine type of attribute " + attribName + " in class " + className,
                    e);
        }
    }
}
