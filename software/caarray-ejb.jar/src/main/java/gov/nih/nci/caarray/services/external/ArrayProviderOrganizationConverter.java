//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external;

import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.external.v1_0.array.ArrayProvider;

import net.sf.dozer.util.mapping.MappingException;
import net.sf.dozer.util.mapping.converters.CustomConverter;

/**
 * Converter for array providers to Organizations.
 * 
 * @author dharley
 *
 */
@SuppressWarnings("unchecked")
public class ArrayProviderOrganizationConverter implements CustomConverter {
    
    /**
     * {@inheritDoc}
     */
    public Object convert(Object dest, Object src, Class destClass, Class srcClass) {
        Object objectToReturn = null;
        ArrayProvider arrayProvider = null;
        Organization organization = null;
        if (null != src) {
            if (srcClass.equals(ArrayProvider.class) && destClass.equals(Organization.class)) {
                arrayProvider = (ArrayProvider) src;
                if (null == dest) {
                    organization = new Organization();
                } else {
                    organization = (Organization) dest;
                }
                setupOgranizationBasedOnArrayProvider(organization, arrayProvider);
                objectToReturn = organization;
            } else if (srcClass.equals(Organization.class) && destClass.equals(ArrayProvider.class)) {
                organization = (Organization) src;
                if (null == dest) {
                    arrayProvider = new ArrayProvider();
                } else {
                    arrayProvider = (ArrayProvider) dest;
                }
                setupArrayProviderBasedOnOrganization(arrayProvider, organization);
                objectToReturn = arrayProvider;
            } else {
                throw new MappingException("Converter ArrayProviderOrganizationConverter used incorrectly. Arguments "
                        + "passed in were:" + dest + " and " + src + ".");
            }
        }
        return objectToReturn;
    }

    private void setupOgranizationBasedOnArrayProvider(Organization organization, ArrayProvider arrayProvider) {
        if (null != arrayProvider.getId()) {
            organization.setId(Long.valueOf(AbstractExternalService.getIdFromExternalId(arrayProvider.getId())));
        } else {
            organization.setId(null);
        }
        organization.setName(arrayProvider.getName());
        organization.setProvider(true);
    }
    
    private void setupArrayProviderBasedOnOrganization(ArrayProvider arrayProvider, Organization organization) {
        if (null != organization.getId()) {
            arrayProvider.setId(AbstractExternalService.makeExternalId(ArrayProvider.class.getName(),
                    organization.getId().toString()));
        } else {
            arrayProvider.setId(null);
        }
        arrayProvider.setName(organization.getName());
    }
    
}
