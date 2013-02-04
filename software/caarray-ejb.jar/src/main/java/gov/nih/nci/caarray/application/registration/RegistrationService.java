//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.domain.register.RegistrationRequest;

/**
 * Provides regisration management functionality to the application.
 */
public interface RegistrationService {

    /**
     * The default JNDI name to use to lookup <code>RegistrationService</code>.
     */
    String JNDI_NAME = "caarray/RegistrationServiceBean/local";

    /**
     * Persists a registration.
     *
     * @param registrationRequest the new registrationRequest to save
     */
    void register(RegistrationRequest registrationRequest);
}
