//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.experiment;

import java.util.HashSet;
import java.util.Set;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * ExperimentalContact represents a person who performed one or more roles in an experiment.
 * 
 * @author dkokotov
 * 
 */
public class ExperimentalContact extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private Person person;
    private Set<Term> roles = new HashSet<Term>();

    /**
     * @return the person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * @return a set of MGED terms describing the roles played by the person on the experiment.
     */
    public Set<Term> getRoles() {
        return roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(Set<Term> roles) {
        this.roles = roles;
    }
}
