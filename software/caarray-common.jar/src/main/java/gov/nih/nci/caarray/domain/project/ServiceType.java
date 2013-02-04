//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.project;

/**
 * An enumeration of different lab service types that can be requested for an Experiment.
 */
public enum ServiceType {
    /** request full service from a lab.*/
    FULL("serviceType.full"),
    /** no service requested - purely a publishing of results. */
    PUBLISH("serviceType.publish");
    
    // two service types : partial and analysis - have been deferred until post 2.0

    private final String resourceKey;

    ServiceType(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * @return the resource key that should be used to retrieve a label
     * for this ServiceType in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
    }
}
