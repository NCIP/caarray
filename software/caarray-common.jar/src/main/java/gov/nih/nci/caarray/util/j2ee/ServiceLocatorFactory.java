//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.j2ee;

/**
 * Provides access to a <code>ServiceLocator</code>.
 */
public final class ServiceLocatorFactory {

    private static ServiceLocator locator = new ServiceLocatorImplementation();

    private ServiceLocatorFactory() {

    }

    /**
     * Returns a <code>ServiceLocator</code> instance.
     *
     * @return the locator
     */
    public static ServiceLocator getLocator() {
        return locator;
    }

    /**
     * Allows registration of a <code>ServiceLocator</code> instance; should only
     * be used in test code to replace the actual locator with a test stub.
     *
     * @param locator the locator to set
     */
    public static void setLocator(ServiceLocator locator) {
        ServiceLocatorFactory.locator = locator;
    }

}
