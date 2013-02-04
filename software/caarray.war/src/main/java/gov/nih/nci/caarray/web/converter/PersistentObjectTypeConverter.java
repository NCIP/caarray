//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.converter;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;

import com.fiveamsolutions.nci.commons.web.struts2.converter.AbstractPersistentObjectTypeConverter;

/**
 * Converter to move between persistent objects and their id's.
 * @author Scott Miller
 */
public class PersistentObjectTypeConverter extends AbstractPersistentObjectTypeConverter {
    
    private static ServiceLocator locator = ServiceLocatorFactory.getLocator();

    /**
     * @return the ServiceLocator
     */
    public static ServiceLocator getServiceLocator() {
        return locator;
    }

    /**
     * @param loc the ServiceLocator to set
     */
    public static void setServiceLocator(ServiceLocator loc) {
        PersistentObjectTypeConverter.locator = loc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GenericDataService getGenericDataService() {
        return (GenericDataService) getServiceLocator().lookup(GenericDataService.JNDI_NAME);
    }

}
