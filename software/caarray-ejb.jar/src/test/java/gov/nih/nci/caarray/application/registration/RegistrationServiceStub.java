//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
