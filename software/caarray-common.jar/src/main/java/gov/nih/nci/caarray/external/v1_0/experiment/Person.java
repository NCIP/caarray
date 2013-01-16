//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.experiment;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * Person is a simple representation of a human.
 * 
 * @author dkokotov
 */
public class Person extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String firstName;
    private String lastName;
    private String middleInitials;
    private String emailAddress;

    /**
     * @return the Person's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the Person's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the Person's middle initials
     */
    public String getMiddleInitials() {
        return middleInitials;
    }

    /**
     * @param middleInitials the middleInitials to set
     */
    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = middleInitials;
    }

    /**
     * @return the Person's email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
