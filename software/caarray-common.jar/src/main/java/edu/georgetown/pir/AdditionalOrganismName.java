//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package edu.georgetown.pir;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Other common or scientific names by which the organism known.
 */
@Entity
public class AdditionalOrganismName implements PersistentObject {

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String source;
    private String value;
    private String comment;
    
    private Set<Organism> organismCollection = new HashSet<Organism>();

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the organismCollection
     */
    @ManyToMany(mappedBy = "additionalOrganismNameCollection")
    public Set<Organism> getOrganismCollection() {
        return organismCollection;
    }

    /**
     * @param organismCollection the organismCollection to set
     */
    public void setOrganismCollection(Set<Organism> organismCollection) {
        this.organismCollection = organismCollection;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

}
