//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.idf;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A person associated with a microarray investigation.
 */
public final class Person implements Serializable {

    private static final long serialVersionUID = 5928411542193605157L;

    private String lastName;
    private String firstName;
    private String midInitials;
    private String email;
    private String phone;
    private String fax;
    private String address;
    private String affiliation;
    private final List<OntologyTerm> roles = new ArrayList<OntologyTerm>();

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * @param affiliation the affiliation to set
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the firstName
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
     * @return the lastName
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
     * @return the midInitials
     */
    public String getMidInitials() {
        return midInitials;
    }

    /**
     * @param midInitials the midInitials to set
     */
    public void setMidInitials(String midInitials) {
        this.midInitials = midInitials;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the roles
     */
    public List<OntologyTerm> getRoles() {
        return roles;
    }

    /**
     * @param role to add
     */
    public void addToRoles(OntologyTerm role) {
        roles.add(role);
    }
}
