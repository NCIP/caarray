//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.j2ee;

import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple stub implementation of locator -- allows clients to seed a lookup table with other stubs by JNDI name.
 */
public final class ServiceLocatorStub implements ServiceLocator {

    private static final long serialVersionUID = 4520519885611921043L;

    private final Map<String, Object> lookupMap = new HashMap<String, Object>();

    /**
     * Prevents direct construction; use the static register methods.
     */
    private ServiceLocatorStub() {
        // no-op
    }

    @Override
    public Object lookup(String jndiName) {
        return this.lookupMap.get(jndiName);
    }

    public void addLookup(String jndiName, Object object) {
        this.lookupMap.put(jndiName, object);
    }

    public static ServiceLocatorStub registerEmptyLocator() {
        final ServiceLocatorStub locatorStub = new ServiceLocatorStub();
        ServiceLocatorFactory.setLocator(locatorStub);
        return locatorStub;
    }
}
