//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.vocabulary;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * A Category represents a class of values in a controlled vocabulary / ontology.
 * 
 * @author dkokotov
 */
public class Category extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    private String name;
    private String accession;
    private String url;
    private TermSource termSource;

    /**
     * @return the name of this category.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the accession for this category. The accession is a vocabulary-specific code that uniquely identifies the
     *         category within the vocabulary.
     */
    public String getAccession() {
        return accession;
    }

    /**
     * @param accession the accession to set
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * @return the url for this category.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the TermSource representing the vocabulary to which this Category belongs
     */
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * @param termSource the termSource to set
     */
    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
    }
}
