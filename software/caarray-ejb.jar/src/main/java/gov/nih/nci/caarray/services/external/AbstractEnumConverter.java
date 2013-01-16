//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import net.sf.dozer.util.mapping.converters.CustomConverter;

/**
 *
 * Convert an Enum instance to and from an AbstractCaArrayEntity.  AbstractCaArrayEntity use the name of the enum as
 * the entity id's objectId.
 * @param <A> Enum type.
 * @param <B> Entity type.
 *
 * @author gax
 */
public abstract class AbstractEnumConverter<A extends Enum<A>, B extends AbstractCaArrayEntity>
        implements CustomConverter {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object dest, Object src, Class destClass, Class srcClass) {
        if (src == null) {
            return null;
        }

        if (Enum.class.isAssignableFrom(destClass)) {
            return convertToEnum((AbstractCaArrayEntity) src, (Class<A>) destClass);
        } else if (AbstractCaArrayEntity.class.isAssignableFrom(destClass)) {
            return convertToEntity((A) src, (B) dest, (Class<B>) destClass);
        }

        throw new IllegalArgumentException("don't know ow to convert " + src + " to a " + destClass.getName());
    }
    
    private A convertToEnum(AbstractCaArrayEntity entity, Class<A> destClass) {
        if (entity.getId() == null) {
            return null;
        }
        String name = new LSID(entity.getId()).getObjectId();
        return Enum.valueOf(destClass, name);        
    }
    
    private B convertToEntity(A src, B dest, Class<B> destClass) {
        B entity;
        if (dest != null) {
            entity = dest;
        } else {
            try {
                entity = destClass.newInstance();
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        entity.setId(AbstractExternalService.makeExternalId(destClass, src.name()));
        copyPropertiesToB(src, entity);
        return entity;            
    }
    
    /**
     * Copy all properties (except id) from the enum to the external object.
     * @param a the source.
     * @param b the external object to populate.
     */
    protected abstract void copyPropertiesToB(A a, B b);
}
