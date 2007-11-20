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
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.PersistentObject;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

/**
 * Provides helper methods for search DAOs.
 *
 * @author Rashmi Srinivasa
 */
public final class SearchCriteriaUtil {
    private static final Logger LOG = Logger.getLogger(SearchCriteriaUtil.class);
    private static final String UNABLE_TO_GET_ASSOCIATION_VAL = "Unable to get association value";

    private SearchCriteriaUtil() {
    }

    /**
     * @param entityToMatch
     * @param criteria
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    static void addCriteriaForAssociations(PersistentObject entityToMatch, Criteria criteria)
      {
        try {
            PersistentClass pclass = HibernateUtil.getConfiguration().getClassMapping(
                entityToMatch.getClass().getName());
            Iterator<Property> properties = pclass.getPropertyIterator();
            while (properties.hasNext()) {
                Property prop = properties.next();
                if (prop.getType().isAssociationType()) {
                    addCriterionForAssociation(entityToMatch, criteria, prop);
                }
            }
        } catch (IllegalAccessException iae) {
            LOG.error(UNABLE_TO_GET_ASSOCIATION_VAL, iae);
            throw new DAOException(UNABLE_TO_GET_ASSOCIATION_VAL, iae);
        } catch (InvocationTargetException ite) {
            LOG.error(UNABLE_TO_GET_ASSOCIATION_VAL, ite);
            throw new DAOException(UNABLE_TO_GET_ASSOCIATION_VAL, ite);
        }
    }

    /**
     * Add one search criterion based on the association to be matched.
     *
     * @param entityToMatch the root entity being searched on.
     * @param criteria the root Criteria to add to.
     * @param prop the association to be matched.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void addCriterionForAssociation(PersistentObject entityToMatch, Criteria criteria,
      Property prop) throws IllegalAccessException, InvocationTargetException {
            Class<?> objClass = entityToMatch.getClass();
            String fieldName = prop.getName();
            boolean isCollectionType = prop.getType().isCollectionType();
            if (isCollectionType) {
                // If the association is a collection, don't add it to the search criteria.
                return;
            }
            Method getterMethod = null;
            String getterName = "get" + capitalizeFirstLetter(fieldName);
            while (objClass != null) {
                try {
                    LOG.debug("Checking class: " + objClass.getName() + " for method: " + getterName);
                    getterMethod = objClass.getDeclaredMethod(getterName, (Class[]) null);
                    break;
                } catch (NoSuchMethodException nsme) {
                    LOG.debug("Will check if it is a method in a superclass.");
                }
                objClass = objClass.getSuperclass();
            }
            if (getterMethod == null) {
                LOG.error("No such method: " + getterName);
            } else {
                Object valueOfAssociation = getterMethod.invoke(entityToMatch, (Object[]) null);
                addCriterion(criteria, fieldName, valueOfAssociation);
            }
    }

    /**
     * Add one search criterion based on the field name and the value to be matched.
     *
     * @param criteria the root Criteria to add to.
     * @param fieldName the name of the field denoting the association.
     * @param valueOfAssociation the value of the association that is to be matched.
     */
    private static void addCriterion(Criteria criteria, String fieldName, Object valueOfAssociation) {
        if (valueOfAssociation == null) {
            return;
        }
        if ((valueOfAssociation instanceof Collection && ((Collection<?>) valueOfAssociation).size() > 0)
                || !(valueOfAssociation instanceof Collection)) {
            Criteria childCriteria = criteria.createCriteria(fieldName);
            childCriteria.add(Example.create(valueOfAssociation));
        }
    }

    private static String capitalizeFirstLetter(String inputString) {
        String firstLetter = inputString.substring(0, 1);
        return (firstLetter.toUpperCase(Locale.getDefault()) + inputString.substring(1));
    }
}
