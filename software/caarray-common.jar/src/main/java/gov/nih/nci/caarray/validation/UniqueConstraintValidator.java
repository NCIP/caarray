/**
 * NOTICE: This software  source code and any of  its derivatives are the
 * confidential  and  proprietary   information  of  Vecna  Technologies,
 * Inc. (such source  and its derivatives are hereinafter  referred to as
 * "Confidential Information"). The  Confidential Information is intended
 * to be  used exclusively by  individuals or entities that  have entered
 * into either  a non-disclosure agreement or license  agreement (or both
 * of  these agreements,  if  applicable) with  Vecna Technologies,  Inc.
 * ("Vecna")   regarding  the  use   of  the   Confidential  Information.
 * Furthermore,  the  Confidential  Information  shall be  used  only  in
 * accordance  with   the  terms   of  such  license   or  non-disclosure
 * agreements.   All  parties using  the  Confidential Information  shall
 * verify that their  intended use of the Confidential  Information is in
 * compliance  with and  not in  violation of  any applicable  license or
 * non-disclosure  agreements.  Unless expressly  authorized by  Vecna in
 * writing, the Confidential Information  shall not be printed, retained,
 * copied, or  otherwise disseminated,  in part or  whole.  Additionally,
 * any party using the Confidential  Information shall be held liable for
 * any and  all damages incurred  by Vecna due  to any disclosure  of the
 * Confidential  Information (including  accidental disclosure).   In the
 * event that  the applicable  non-disclosure or license  agreements with
 * Vecna  have  expired, or  if  none  currently  exists, all  copies  of
 * Confidential Information in your  possession, whether in electronic or
 * printed  form, shall be  destroyed or  returned to  Vecna immediately.
 * Vecna  makes no  representations  or warranties  hereby regarding  the
 * suitability  of  the   Confidential  Information,  either  express  or
 * implied,  including  but not  limited  to  the  implied warranties  of
 * merchantability,    fitness    for    a   particular    purpose,    or
 * non-infringement. Vecna  shall not be liable for  any damages suffered
 * by  licensee as  a result  of  using, modifying  or distributing  this
 * Confidential Information.  Please email [info@vecnatech.com]  with any
 * questions regarding the use of the Confidential Information.
 */

package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
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
    private UniqueConstraintField[] fields;
    
    /**
     * {@inheritDoc}
     */
    public void initialize(UniqueConstraint uc) {
        fields = uc.fields();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" })
    public boolean isValid(Object o) {
        Criteria crit = HibernateUtil.getCurrentSession().createCriteria(o.getClass());
        ClassMetadata metadata = getMetaData(o);
        for (UniqueConstraintField field : fields) {
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
                // special casing for entity-type properties - only include them in the criteria if they are already
                // persistent
                // otherwise, short-circuit the process and return true immediately since if the entity-type property
                // is not persistent then it will be a new value and thus different from any currently in the db, thus
                // satisfying uniqueness
                ClassMetadata fieldMetadata = getMetaData(fieldVal);
                if (fieldMetadata == null || fieldMetadata.getIdentifier(fieldVal, EntityMode.POJO) != null) {
                    crit.add(Restrictions
                            .eq(field.name(), ReflectHelper.getGetter(o.getClass(), field.name()).get(o)));
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

    /**
     * Get ClassMetadata for given persistent entity instance, properly handling proxies.
     * @param entity
     * @return
     */
    private static ClassMetadata getMetaData(Object entity) {
        if (entity instanceof HibernateProxy) {
            return HibernateUtil.getSessionFactory().getClassMetadata(
                    ((HibernateProxy) entity).getHibernateLazyInitializer().getEntityName());
        } else {
            return HibernateUtil.getSessionFactory().getClassMetadata(entity.getClass());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void apply(PersistentClass pc) {
        List<Column> columns = new ArrayList<Column>();
        for (UniqueConstraintField field : fields) {
            Property prop = pc.getProperty(field.name());
            CollectionUtils.addAll(columns, prop.getColumnIterator());
        }
        pc.getTable().createUniqueKey(columns);
    }
}

