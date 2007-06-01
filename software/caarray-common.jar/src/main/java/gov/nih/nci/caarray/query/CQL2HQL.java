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
package gov.nih.nci.caarray.query;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Converts CQL query into HQL (Hibernate Query Language) string.
 * The substance of this class is taken from the caCORE SDK.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public final class CQL2HQL {
    private static final String OPENING_PARENTHESIS = "(";
    private static final String CLOSING_PARENTHESIS = ")";
    private static final String STRING_WHERE = " where ";
    private static final String STRING_AS = " as ";
    private static final String TARGET_ALIAS = "xxTargetAliasxx";
    private static final String SOURCE_ASSOC_ALIAS = "parent";
    private static final String TARGET_ASSOC_ALIAS = "child";
    private static final ThreadLocal<StringBuffer> HQL_HOLDER = new ThreadLocal<StringBuffer>();
    private static final ThreadLocal<List> PARAMS_HOLDER = new ThreadLocal<List>();
    private static final ThreadLocal<Boolean> CASE_HOLDER = new ThreadLocal<Boolean>();

    /**
     * Private constructor to prevent instantiation.
     */
    private CQL2HQL() {
        super();
    }

    /**
     * Translates a CQL query into an HQL string and parameters. This process assumes the
     * CQL Query has passed validation.  Processing of invalid CQL may or may not result
     * in undefined results.
     *
     * @param query the CQL Query to translate into HQL.
     * @param caseSensitive a flag indicating if the query is case-sensitive.
     * @return the HQL (Hibernate Query Language) string and parameters.
     * @throws QueryException if query cannot be translated.
     */
    public static HibernateQueryWrapper translate(CQLQuery query, boolean caseSensitive)
            throws QueryException {
        StringBuffer hql = new StringBuffer();
        HQL_HOLDER.set(hql);
        List parameters = new ArrayList();
        PARAMS_HOLDER.set(parameters);
        Boolean caseSensitivity = Boolean.valueOf(caseSensitive);
        CASE_HOLDER.set(caseSensitivity);
        CQLObject targetObj = query.getTarget();
        String targetName = targetObj.getName();
        CQLAttribute targetAttr = targetObj.getAttribute();
        CQLAssociation targetAssoc = targetObj.getAssociation();
        CQLGroup targetGrp = targetObj.getGroup();

        hql.append(" From ").append(targetName);
        hql.append(STRING_AS).append(TARGET_ALIAS);
        if (targetAttr != null || targetAssoc != null || targetGrp != null) {
            processTarget(targetObj);
        }
        HQL_HOLDER.set(null);
        PARAMS_HOLDER.set(null);
        CASE_HOLDER.set(null);
        return new HibernateQueryWrapper(hql.toString(), parameters);
    }

    private static void processTarget(CQLObject target) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        String targetName = target.getName();
        CQLAttribute targetAttr = target.getAttribute();
        CQLAssociation targetAssoc = target.getAssociation();
        CQLGroup targetGrp = target.getGroup();

        boolean andFlag = false;
        hql.append(STRING_WHERE);
        if (targetAttr != null) {
            andFlag = processAttribute(targetName, targetAttr, true);
        }
        if (targetAssoc != null) {
            addAndIfNecessary(andFlag);
            andFlag = processAssociation(targetName, targetAssoc, true);
        }
        if (targetGrp != null) {
            addAndIfNecessary(andFlag);
            processGroup(targetName, targetGrp, true);
        }
    }

    /**
     * Processes an Object of a CQL Query.
     *
     * @param hql the existing HQL query fragment.
     * @param obj the object to incorporate into HQL.
     * @throws QueryException if CQL object could not be processed.
     */
    private static void processObject(CQLObject obj) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        String objName = obj.getName();
        CQLAttribute objAttr = obj.getAttribute();
        CQLAssociation objAssoc = obj.getAssociation();
        CQLGroup objGrp = obj.getGroup();
        hql.append("from ").append(objName);

        if (objAttr == null && objAssoc == null && objGrp == null) {
            return;
        }
        processAttrAssocGroup(obj, objName, objAttr, objAssoc, objGrp);
    }

    private static void processAttrAssocGroup(CQLObject obj, String objName, CQLAttribute objAttr,
      CQLAssociation objAssoc, CQLGroup objGrp) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        boolean andFlag = false;
        hql.append(STRING_WHERE);

        if (objAttr != null) {
            andFlag = processAttribute(obj.getName(), objAttr, false);
        }
        if (objAssoc != null) {
            addAndIfNecessary(andFlag);
            andFlag = processAssociation(objName, objAssoc, false);
        }
        if (objGrp != null) {
            addAndIfNecessary(andFlag);
            processGroup(objName, objGrp, false);
        }
    }

    /**
     * Proceses an Attribute of a CQL Query.
     *
     * @param hql the existing HQL query fragment.
     * @param parentName name of the class to which the attribute belongs.
     * @param objAlias the alias of the object to which this attribute belongs.
     * @param attrib the attribute to incorporate into HQL.
     * @throws QueryException if attribute could not be processed.
     */
    private static boolean processAttribute(String parentName, CQLAttribute attrib, boolean useAlias)
      throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        String dataType = CQL2HQLHelper.getDataType(parentName, attrib.getName());
        CQLPredicate predicate = attrib.getPredicate();
        String attribName = attrib.getName();
        if (useAlias) {
            attribName = TARGET_ALIAS + "." + attribName;
        }

        if (predicate.equals(CQLPredicate.IS_NULL)) {
            hql.append(attribName).append(" is null");
        } else if (predicate.equals(CQLPredicate.IS_NOT_NULL)) {
            hql.append(attribName).append(" is not null");
        } else {
            addParameter(attrib, dataType, predicate, attribName);
        }

        return true;
    }

    private static void addParameter(CQLAttribute attrib, String dataType, CQLPredicate predicate,
      String attribName) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        String predValue = CQL2HQLHelper.convertPredicate(predicate);
        if ("java.lang.String".equals(dataType)) {
            addStringParameter(attrib, attribName, predValue);
        } else if (isWrapperType(dataType)) {
            addWrapperParameter(attrib, dataType, attribName, predValue);
        } else {
            hql.append(attribName + " " + predValue + " '" + attrib.getValue() + "' ");
        }
    }

    private static boolean isWrapperType(String dataType) {
        return "java.util.Date".equals(dataType) || "java.lang.Boolean".equals(dataType)
                || "java.lang.Long".equals(dataType) || "java.lang.Double".equals(dataType)
                || "java.lang.Float".equals(dataType) || "java.lang.Integer".equals(dataType);
    }

    private static void addWrapperParameter(CQLAttribute attrib, String dataType,
      String attribName, String predValue) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        hql.append(attribName + " " + predValue + "?");
        try {
            checkTypeAndAdd(attrib, dataType);
        } catch (ParseException e) {
            throw new QueryException("Could not parse Date parameter", e);
        } catch (NumberFormatException e) {
            throw new QueryException("Could not parse Number parameter", e);
        }
    }

    @SuppressWarnings({ "unchecked", "PMD" })
    private static void checkTypeAndAdd(CQLAttribute attrib, String dataType) throws ParseException {
        List params = PARAMS_HOLDER.get();
        if ("java.util.Date".equals(dataType)) {
            params.add(DateFormat.getInstance().parse(attrib.getValue()));
        } else if ("java.lang.Boolean".equals(dataType)) {
            params.add(Boolean.valueOf(attrib.getValue()));
        } else if ("java.lang.Long".equals(dataType)) {
            params.add(Long.valueOf(attrib.getValue()));
        } else if ("java.lang.Double".equals(dataType)) {
            params.add(Double.valueOf(attrib.getValue()));
        } else if ("java.lang.Float".equals(dataType)) {
            params.add(Float.valueOf(attrib.getValue()));
        } else if ("java.lang.Integer".equals(dataType)) {
            params.add(Integer.valueOf(attrib.getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void addStringParameter(CQLAttribute attrib, String attribName, String predValue) {
        StringBuffer hql = HQL_HOLDER.get();
        List params = PARAMS_HOLDER.get();
        boolean caseSensitive = CASE_HOLDER.get().booleanValue();
        if (caseSensitive) {
            hql.append(" " + attribName + " ");
        } else {
            hql.append(" lower(" + attribName + CLOSING_PARENTHESIS);
        }

        if (caseSensitive) {
            hql.append(" " + predValue + "?");
        } else {
            hql.append(" " + predValue + " lower(?)");
        }
        params.add(attrib.getValue());
    }

    /**
     * Processes an Association of a CQL Query.
     *
     * @param hql the existing HQL query fragment.
     * @param parentAlias the alias of the parent object.
     * @param parentName the class name of the object to which this association belongs.
     * @param assoc the association to process into HQL.
     * @throws QueryException if the association could not be processed.
     */
    private static boolean processAssociation(String parentName, CQLAssociation assoc, boolean useAlias)
      throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        String roleName = CQL2HQLHelper.getRoleName(parentName, assoc);
        String sourceRoleName = assoc.getSourceRoleName();
        checkForInvalidAssociation(parentName, assoc, roleName, sourceRoleName);
        useAliasIfNecessary(useAlias);
        if (roleName != null) {
            processRoleName(parentName, assoc, roleName);
        } else if (sourceRoleName != null) {
            processSourceRoleName(parentName, assoc, sourceRoleName);
        } else {
            hql.append("id in ( select id ");
            processObject(assoc);
            hql.append(CLOSING_PARENTHESIS);
        }

        return true;
    }

    private static void checkForInvalidAssociation(String parentName, CQLAssociation assoc, String roleName,
      String sourceRoleName) throws QueryException {
        if (roleName == null && sourceRoleName == null
          && !CQL2HQLHelper.existInheritance(parentName, assoc.getName())) {
            throw new QueryException("Association from type " + parentName + " to type " + assoc.getName()
                    + " does not exist. Use only direct associations.");
        }
    }

    private static void processSourceRoleName(String parentName, CQLAssociation assoc, String sourceRoleName)
      throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        if (CQL2HQLHelper.isCollection(assoc.getName(), sourceRoleName)) {
            processCollection(parentName, assoc, sourceRoleName, false);
        } else {
            hql.append("id in ( select " + sourceRoleName + ".id ");
            processObject(assoc);
            hql.append(CLOSING_PARENTHESIS);
        }
    }

    private static void processRoleName(String parentName, CQLAssociation assoc, String roleName)
      throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        if (CQL2HQLHelper.isCollection(parentName, roleName)) {
            processCollection(parentName, assoc, roleName, true);
        } else {
            hql.append(roleName).append(".id in ( select id ");
            processObject(assoc);
            hql.append(CLOSING_PARENTHESIS);
        }
    }

    @SuppressWarnings("PMD")
    private static void processCollection(String parentName, CQLAssociation assoc, String roleName,
      boolean sourceInTarget) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        hql.append("id in ( select ").append(TARGET_ASSOC_ALIAS).append(".id from ");
        hql.append(parentName + STRING_AS + TARGET_ASSOC_ALIAS + ",");
        hql.append(assoc.getName()).append(STRING_AS).append(SOURCE_ASSOC_ALIAS);
        hql.append(STRING_WHERE);
        if (sourceInTarget) {
            hql.append(SOURCE_ASSOC_ALIAS);
        } else {
            hql.append(TARGET_ASSOC_ALIAS);
        }
        hql.append(" in elements(");
        if (sourceInTarget) {
            hql.append(TARGET_ASSOC_ALIAS);
        } else {
            hql.append(SOURCE_ASSOC_ALIAS);
        }
        hql.append("." + roleName + ") and " + SOURCE_ASSOC_ALIAS + ".id in (select id ");
        processObject(assoc);
        hql.append("))");
    }

    /**
     * Processes a Group of a CQL Query.
     *
     * @param hql the existing HQL query fragment
     * @param parentName the type name of the parent object
     * @param group the group to process into HQL
     * @throws QueryException if group could not be processed.
     */
    private static void processGroup(String parentName, CQLGroup group, boolean useAlias) throws QueryException {
        String logicalOp = group.getLogicalOperator().getValue();
        StringBuffer hql = HQL_HOLDER.get();
        hql.append(" " + OPENING_PARENTHESIS);

        boolean logicalOpClauseNeeded = false;
        // attributes
        if (group.getAttributeCollection() != null) {
            logicalOpClauseNeeded = processAttributes(parentName, group, useAlias, logicalOp);
        }
        // associations
        if (group.getAssociationCollection() != null) {
            addLogicalOpIfNeeded(logicalOp, logicalOpClauseNeeded);
            logicalOpClauseNeeded = processAssociations(parentName, group, useAlias, logicalOp);
        }
        // subgroups
        if (group.getGroupCollection() != null) {
            addLogicalOpIfNeeded(logicalOp, logicalOpClauseNeeded);
            processGroups(parentName, group, useAlias, logicalOp);
        }

        hql.append(CLOSING_PARENTHESIS + " ");
    }

    private static void addLogicalOpIfNeeded(String logicalOp, boolean logicalOpClauseNeeded) {
        StringBuffer hql = HQL_HOLDER.get();
        if (logicalOpClauseNeeded) {
            hql.append(" " + logicalOp + " ");
        }
    }

    @SuppressWarnings("PMD")
    private static void processGroups(String parentName, CQLGroup group, boolean useAlias,
      String logicalOp) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        Iterator iterator = group.getGroupCollection().iterator();
        while (iterator.hasNext()) {
            hql.append("( ");
            processGroup(parentName, (CQLGroup) iterator.next(), useAlias);
            hql.append(" )");
            if (iterator.hasNext()) {
                hql.append(" " + logicalOp + " ");
            }
        }
    }

    private static boolean processAssociations(String parentName, CQLGroup group,
      boolean useAlias, String logicalOp) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        boolean logicalOpClauseNeeded = false;
        Iterator iterator = group.getAssociationCollection().iterator();
        while (iterator.hasNext()) {
            logicalOpClauseNeeded = true;
            processAssociation(parentName, (CQLAssociation) iterator.next(), useAlias);
            if (iterator.hasNext()) {
                hql.append(" " + logicalOp + " ");
            }
        }
        return logicalOpClauseNeeded;
    }

    private static boolean processAttributes(String parentName, CQLGroup group, boolean useAlias,
      String logicalOp) throws QueryException {
        StringBuffer hql = HQL_HOLDER.get();
        boolean logicalOpClauseNeeded = false;

        Iterator iterator = group.getAttributeCollection().iterator();
        while (iterator.hasNext()) {
            logicalOpClauseNeeded = true;
            processAttribute(parentName, (CQLAttribute) iterator.next(), useAlias);
            if (iterator.hasNext()) {
                hql.append(" " + logicalOp + " ");
            }
        }
        return logicalOpClauseNeeded;
    }

    private static void addAndIfNecessary(boolean andFlag) {
        StringBuffer hql = HQL_HOLDER.get();
        if (andFlag) {
            hql.append(" and ");
        }
    }

    private static void useAliasIfNecessary(boolean useAlias) {
        StringBuffer hql = HQL_HOLDER.get();
        if (useAlias) {
            hql.append(TARGET_ALIAS + ".");
        }
    }
}