/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The CaArraySvc
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This CaArraySvc Software License (the License) is between NCI and You. You (or
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
 * its rights in the CaArraySvc Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the CaArraySvc Software; (ii) distribute and
 * have distributed to and by third parties the CaArraySvc Software and any
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
package gov.nih.nci.cagrid.caarray.util;

import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.system.query.cql.CQLAssociation;
import gov.nih.nci.system.query.cql.CQLAttribute;
import gov.nih.nci.system.query.cql.CQLGroup;
import gov.nih.nci.system.query.cql.CQLLogicalOperator;
import gov.nih.nci.system.query.cql.CQLObject;
import gov.nih.nci.system.query.cql.CQLPredicate;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Utility class to convert between CQL implementations.
 */
public final class CQL2CQL {

    /**
     * Used to remember what objects we have already converted, in case the object graph
     * has a cycle.
     */
    private static ThreadLocal<Map<java.lang.Object, java.lang.Object>> CACHE =
        new ThreadLocal<Map<java.lang.Object, java.lang.Object>>();

    private CQL2CQL() {
        // prevent instantiation
    }

    public static gov.nih.nci.system.query.cql.CQLQuery convert(final CQLQuery query) {
        CACHE.set(new HashMap<java.lang.Object, java.lang.Object>());
        try {
            if (query == null) {
                return null;
            }
            gov.nih.nci.system.query.cql.CQLQuery result = new gov.nih.nci.system.query.cql.CQLQuery();
            result.setTarget(convertObject(query.getTarget()));
            return result;
        } finally {
            CACHE.set(null);
        }
    }

    private static CQLObject convertObject(final Object from) {
        if (from == null) {
            return null;
        }

        if (CACHE.get().get(from) != null) {
            return (CQLObject) CACHE.get().get(from);
        }

        CQLObject result = new CQLObject();
        CACHE.get().put(from, result);

        result.setAssociation(convertAssociation(from.getAssociation()));
        result.setAttribute(convertAttribute(from.getAttribute()));
        result.setGroup(convertGroup(from.getGroup()));
        result.setName(from.getName());

        return result;
    }

    private static CQLAssociation convertAssociation(final Association from) {
        if (from == null) {
            return null;
        }

        if (CACHE.get().get(from) != null) {
            return (CQLAssociation) CACHE.get().get(from);
        }

        CQLAssociation result = new CQLAssociation();
        CACHE.get().put(from, result);

        result.setAssociation(convertAssociation(from.getAssociation()));
        result.setAttribute(convertAttribute(from.getAttribute()));
        result.setGroup(convertGroup(from.getGroup()));
        result.setName(from.getName());
        result.setSourceRoleName(from.getRoleName());
        result.setTargetRoleName(null); // TODO: is roleName source or target???

        return result;
    }

    private static CQLAttribute convertAttribute(final Attribute from) {
        if (from == null) {
            return null;
        }

        if (CACHE.get().get(from) != null) {
            return (CQLAttribute) CACHE.get().get(from);
        }

        CQLAttribute result = new CQLAttribute();
        CACHE.get().put(from, result);

        result.setName(from.getName());
        result.setPredicate(convertPredicate(from.getPredicate()));
        result.setValue(from.getValue());

        return result;
    }

    private static CQLGroup convertGroup(final Group from) {
        if (from == null) {
            return null;
        }

        if (CACHE.get().get(from) != null) {
            return (CQLGroup) CACHE.get().get(from);
        }

        CQLGroup result = new CQLGroup();
        CACHE.get().put(from, result);

        Collection<CQLAssociation> associationCollection = new HashSet<CQLAssociation>();
        Collection<CQLAttribute> attributeCollection = new HashSet<CQLAttribute>();
        Collection<CQLGroup> groupCollection = new HashSet<CQLGroup>();

        for (Association assoc : from.getAssociation()) {
            associationCollection.add(convertAssociation(assoc));
        }
        for (Attribute att : from.getAttribute()) {
            attributeCollection.add(convertAttribute(att));
        }
        for (Group grp : from.getGroup()) {
            groupCollection.add(convertGroup(grp));
        }

        result.setAssociationCollection(associationCollection);
        result.setAttributeCollection(attributeCollection);
        result.setGroupCollection(groupCollection);
        result.setLogicOperator(convertOperator(from.getLogicRelation()));

        return result;
    }

    private static CQLLogicalOperator convertOperator(final LogicalOperator logicRelation) {
        if (logicRelation == null) {
            return null;
        }

        if (logicRelation.equals(LogicalOperator.AND)) {
            return CQLLogicalOperator.AND;
        } else if (logicRelation.equals(LogicalOperator.OR)) {
            return CQLLogicalOperator.OR;
        }

        throw new IllegalArgumentException("Unkonwn logical operator: " + logicRelation);
    }

    private static CQLPredicate convertPredicate(final Predicate predicate) {
        if (predicate == null) {
            return null;
        }

        if (predicate.equals(Predicate.EQUAL_TO)) {
            return CQLPredicate.EQUAL_TO;
        } else if (predicate.equals(Predicate.GREATER_THAN)) {
            return CQLPredicate.GREATER_THAN;
        } else if (predicate.equals(Predicate.GREATER_THAN_EQUAL_TO)) {
            return CQLPredicate.GREATER_THAN_EQUAL_TO;
        } else if (predicate.equals(Predicate.IS_NOT_NULL)) {
            return CQLPredicate.IS_NOT_NULL;
        } else if (predicate.equals(Predicate.IS_NULL)) {
            return CQLPredicate.IS_NULL;
        } else if (predicate.equals(Predicate.LESS_THAN)) {
            return CQLPredicate.LESS_THAN;
        } else if (predicate.equals(Predicate.LESS_THAN_EQUAL_TO)) {
            return CQLPredicate.LESS_THAN_EQUAL_TO;
        } else if (predicate.equals(Predicate.LIKE)) {
            return CQLPredicate.LIKE;
        } else if (predicate.equals(Predicate.NOT_EQUAL_TO)) {
            return CQLPredicate.NOT_EQUAL_TO;
        }

        throw new IllegalArgumentException("Unknown predicate: " + predicate);
    }
}
