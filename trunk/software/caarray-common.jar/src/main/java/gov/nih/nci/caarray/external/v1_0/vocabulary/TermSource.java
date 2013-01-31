//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.vocabulary;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;


/**
 * A TermSource represents a controlled vocabulary / ontology.
 * 
 * @author dkokotov
 */
public class TermSource extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String version;
    private String url;

    /**
     * @return the name for this term source
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
     * @return the version for this term source
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the url of this term source.
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
}
