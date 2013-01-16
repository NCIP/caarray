//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.vocabulary;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;

/**
 * A Term represents a value from a controlled vocabulary / ontology.
 * 
 * @author dkokotov
 */
public class Term extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String accession;
    private String url;
    private TermSource termSource;
    private String value;

    /**
     * @return the accession for this term. The accession is a vocabulary-specific code that uniquely identifies the
     *         term within the vocabulary.
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
     * @return the url for this term.
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
     * @return the TermSource representing the vocabulary to which this Term belongs
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
    
    /**
     * @return the value for this term
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
