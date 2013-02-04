//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.idf;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Protocol;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A microarray investigation.
 */
public final class Investigation implements Serializable {

    private static final long serialVersionUID = -345179453106139343L;

    private String title;
    private final List<OntologyTerm> designs = new ArrayList<OntologyTerm>();
    private final List<ExperimentalFactor> factors = new ArrayList<ExperimentalFactor>();
    private final List<Person> persons = new ArrayList<Person>();
    private final List<OntologyTerm> qualityControlTypes = new ArrayList<OntologyTerm>();
    private final List<OntologyTerm> replicateTypes = new ArrayList<OntologyTerm>();
    private final List<OntologyTerm> normalizationTypes = new ArrayList<OntologyTerm>();
    private Date dateOfExperiment;
    private Date publicReleaseDate;
    private final List<Publication> publications = new ArrayList<Publication>();
    private String description;
    private final List<Protocol> protocols = new ArrayList<Protocol>();
    private final List<AbstractSampleDataRelationshipNode> entryNodes =
        new ArrayList<AbstractSampleDataRelationshipNode>();

    /**
     * @return the designs
     */
    public List<OntologyTerm> getDesigns() {
        return designs;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the persons
     */
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * @return the normalizationTypes
     */
    public List<OntologyTerm> getNormalizationTypes() {
        return normalizationTypes;
    }

    /**
     * @return the qualityControlTypes
     */
    public List<OntologyTerm> getQualityControlTypes() {
        return qualityControlTypes;
    }

    /**
     * @return the replicateTypes
     */
    public List<OntologyTerm> getReplicateTypes() {
        return replicateTypes;
    }

    /**
     * @return the publications
     */
    public List<Publication> getPublications() {
        return publications;
    }


    Publication getOrCreatePublication(int index) {
        while (publications.size() <= index) {
            publications.add(new Publication());
        }
        return publications.get(index);
    }
    /**
     * @return the dateOfExperiment
     */
    public Date getDateOfExperiment() {
        return dateOfExperiment;
    }

    /**
     * @param dateOfExperiment the dateOfExperiment to set
     */
    public void setDateOfExperiment(Date dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the publicReleaseDate
     */
    public Date getPublicReleaseDate() {
        return publicReleaseDate;
    }

    /**
     * @param publicReleaseDate the publicReleaseDate to set
     */
    public void setPublicReleaseDate(Date publicReleaseDate) {
        this.publicReleaseDate = publicReleaseDate;
    }

    /**
     * @return the protocols
     */
    public List<Protocol> getProtocols() {
        return protocols;
    }

    /**
     * @return the entryNodes
     */
    public List<AbstractSampleDataRelationshipNode> getEntryNodes() {
        return entryNodes;
    }

    /**
     * @return the factors
     */
    public List<ExperimentalFactor> getFactors() {
        return factors;
    }

    ExperimentalFactor getOrCreateFactor(int index) {
        while (factors.size() <= index) {
            factors.add(new ExperimentalFactor());
        }
        return factors.get(index);
    }

    Person getOrCreatePerson(int index) {
        while (persons.size() <= index) {
            persons.add(new Person());
        }
        return persons.get(index);
    }


    Protocol getOrCreateProtcol(int index) {
        while (protocols.size() <= index) {
            protocols.add(new Protocol());
        }
        return protocols.get(index);
    }

}
