//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.domain.register.RegistrationRequest;

/**
 * @author John Hedden (Amentra, Inc.)
 *
 */
public class RegistrationServiceStub implements RegistrationService {

    /**
     * {@inheritDoc}
     */
    public void register(RegistrationRequest registrationRequest) {
        //no service
    }
}
