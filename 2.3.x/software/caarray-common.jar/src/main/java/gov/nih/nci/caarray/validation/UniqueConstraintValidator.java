//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UnfilteredCallback;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.util.ReflectHelper;
import org.hibernate.validator.PersistentClassConstraint;
import org.hibernate.validator.Validator;

/**
 * Validator for UniqueConstraint annotation.
 *
 * @author dkokotov@5amsolutions.com
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class UniqueConstraintValidator implements Validator<UniqueConstraint>, PersistentClassConstraint {
    private UniqueConstraint uniqueConstraint;

    /**
     * {@inheritDoc}
     */
    public void initialize(UniqueConstraint uc) {
        this.uniqueConstraint = uc;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    public boolean isValid(final Object o) {
        UnfilteredCallback unfilteredCallback = new UnfilteredCallback() {
            public Object doUnfiltered(Session s) {
                Criteria crit = s.createCriteria(getUnwrappedClass(o).getClass());
                ClassMetadata metadata = HibernateUtil.getSessionFactory()
                        .getClassMetadata(getUnwrappedClass(o).getClass());
                for (UniqueConstraintField field : uniqueConstraint.fields()) {
                    Object fieldVal = metadata.getPropertyValue(o, field.name(), EntityMode.POJO);
                    if (fieldVal == null) {
                        if (field.nullsEqual()) {
                            // nulls are equal, so add it to to criteria
                            crit.add(Restrictions.isNull(field.name()));
                        } else {
                            // nulls are never equal, so uniqueness is automatically satisfied
                            return true;
                        }
                    } else {
                        // special casing for entity-type properties - only include them in the criteria if they are
                        // already
                        // persistent
                        // otherwise, short-circuit the process and return true immediately since if the entity-type
                        // property
                        // is not persistent then it will be a new value and thus different from any currently in the
                        // db, thus
                        // satisfying uniqueness
                        ClassMetadata fieldMetadata = HibernateUtil
                                .getSessionFactory().getClassMetadata(
                                        getUnwrappedClass(fieldVal).getClass());
                        if (fieldMetadata == null || fieldMetadata.getIdentifier(fieldVal, EntityMode.POJO) != null) {
                            crit.add(Restrictions.eq(field.name(), ReflectHelper.getGetter(o.getClass(), field.name())
                                    .get(o)));
                        } else {
                            return true;
                        }
                    }
                }
                // if object is already persistent, then add condition to exclude it matching itself
                Object id = metadata.getIdentifier(o, EntityMode.POJO);
                if (id != null) {
                    crit.add(Restrictions.ne(metadata.getIdentifierPropertyName(), id));
                }

                return crit.list().size() == 0;
            }
        };
        return (Boolean) HibernateUtil.doUnfiltered(unfilteredCallback);

    }

    /**
     * Get Class for given persistent entity instance, properly handling
     * proxies.
     *
     * @param entity
     * @return
     */
    private static Object getUnwrappedClass(Object entity) {
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        } else {
            return entity;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void apply(PersistentClass pc) {
        if (this.uniqueConstraint.generateDDLConstraint()) {
            List<Column> columns = new ArrayList<Column>();
            for (UniqueConstraintField field : uniqueConstraint.fields()) {
                Property prop = pc.getProperty(field.name());
                CollectionUtils.addAll(columns, prop.getColumnIterator());
            }
            pc.getTable().createUniqueKey(columns);
        }
    }
}

