//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

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

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

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
     * Update the provided criteria object with values from the associated entites.
     * Ignores collection properties (ie, one-to-many), but handles all other association types (ie,
     * many-to-one, one-to-one, etc.)
     *
     * @param entityToMatch the entity to match
     * @param criteria the criteria to modify
     * @throws IllegalAccessException if unable to call setter
     * @throws InvocationTargetException if unable to call setter
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
