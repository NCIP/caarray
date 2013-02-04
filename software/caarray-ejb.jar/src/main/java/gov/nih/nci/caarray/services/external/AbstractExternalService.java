//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.sf.dozer.util.mapping.MapperIF;

import org.hibernate.criterion.MatchMode;

/**
 * Base class for external service implementations. Has utility methods.
 * 
 * @author dkokotov
 */
public abstract class AbstractExternalService {
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * @return an instance of the Dozer Mapper.
     */
    protected MapperIF getMapper() {
        return BeanMapperLookup.getMapper(getMapperVersionKey());
    }
    
    /**
     * @return the version key to use for looking up the appropriate Dozer Mapper for this API version.
     */
    protected abstract String getMapperVersionKey();

    /**
     * Map the given collection of objects from one domain model to another. Used to convert between internal and 
     * external domain models.
     * @param <S> type of objects being converted
     * @param <T> type of objects to convert to
     * @param in the collection of objects to convert
     * @param out the collection in which the converted objects should be placed.
     * @param outClass the class of the objects to convert to.
     */
    @SuppressWarnings("unchecked")
    protected <S, T> void mapCollection(Collection<S> in, Collection<? super T> out, Class<T> outClass) {
        MapperIF mapper = getMapper();
        for (S obj : in) {
            out.add((T) mapper.map(obj, outClass));
        }
    }

    /**
     * Map the given collection of objects from one domain model to another. Used to convert between internal and 
     * external domain models.
     * @param <S> type of objects being converted
     * @param <T> type of objects to convert to
     * @param in the collection of objects to convert
     * @param outClass the class of the objects to convert to.
     * @return the list of converted objects.
     */
    protected <S, T> List<T> mapCollection(Collection<S> in, Class<T> outClass) {
        List<T> out = new ArrayList<T>();
        mapCollection(in, out, outClass);
        return out;
    }

    /**
     * Map the given object from one domain model to another. Used to convert between internal and 
     * external domain models.
     * @param <T> type of object to convert to
     * @param in the object to convert
     * @param outClass the class of the object to convert to.
     * @return the converted object
     */
    @SuppressWarnings("unchecked")
    protected <T> T mapEntity(Object in, Class<T> outClass) {
        return (T) getMapper().map(in, outClass);
    }

    /**
     * Return the class of the entity identified by the given external id. 
     * 
     * @param id the external id
     * @return the class.
     */
    public static Class<?> getClassFromExternalId(String id) {
        LSID lsidObj = new LSID(id);
        try {
            return Class.forName(lsidObj.getNamespace());
        } catch (ClassNotFoundException e) {
            return null;
        }        
    }

    /**
     * Return the identifier of the entity identified by the given external id. This would be the String
     * that identifies the entity within its class. Typically it is the database identifier, but for some
     * classes in the external model it is not. 
     * 
     * @param id the external id
     * @return the entity id
     */
    public static String getIdFromExternalId(String id) {
        return new LSID(id).getObjectId();
    }

    /**
     * Apply remote api-time security policies to the given set of entities.
     * 
     * @param entities the entities to apply policies to.
     */
    protected void applySecurityPolicies(Collection<?> entities) {
        for (Object entity : entities) {
            applySecurityPolicies(entity);
        }
    }

    /**
     * Apply remote api-time security policies to the given entity.
     * 
     * @param entity the entity to apply policies to.
     */
    protected void applySecurityPolicies(Object entity) {
        if (entity instanceof AbstractCaArrayObject) {
            AbstractCaArrayObject object = (AbstractCaArrayObject) entity;
            Set<SecurityPolicy> policies = object.getRemoteApiSecurityPolicies(CaArrayUsernameHolder.getCsmUser());
            if (!policies.isEmpty()) {
                SecurityPolicy.applySecurityPolicies(object, policies);
            }
        }
    }

    /**
     * Return the hibernate MatchMode constant with given name.
     * 
     * @param matchMode the MatchMode constant to return
     * @return the MatchMode constant.
     */
    protected MatchMode getHibernateMatchMode(String matchMode) {
        try {
            return (MatchMode) MatchMode.class.getField(matchMode).get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Could not retrieve MatchMode constant: " + matchMode, e);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid MatchMode constant: " + matchMode, e);
        }
    }
 
    /**
     * @return the daoFactory
     */
    public CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }        

    /**
     * @param daoFactory the daoFactory to set
     */
    public void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Create the external id for an entity of given class with given internal id. 
     * This uses the fully qualified classname as the external id namespace.
     * 
     * @param namespaceClass the external model class for the entity
     * @param id the id of the entity in the internal model
     * @return the external id
     */
    public static String makeExternalId(Class<?> namespaceClass, Object id) {
        return makeExternalId(namespaceClass.getName(), id.toString());
    }

    /**
     * Create the external id for an entity in given namespace with given internal id.
     * 
     * @param namespace the namespace to use for the id.
     * @param id the id of the entity in the internal model
     * @return the external id
     */
    public static String makeExternalId(String namespace, String id) {
        return new LSID(gov.nih.nci.caarray.domain.AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY, namespace, id)
                .toString();
    }
}
