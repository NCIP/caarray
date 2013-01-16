//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.idf;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.io.Serializable;

/**
 * A published article or other document related to an investigation.
 */
public final class Publication implements Serializable {

    private static final long serialVersionUID = -6258924837346624954L;

    private String pubMedId;
    private String doi;
    private String authorList;
    private String title;
    private OntologyTerm status;

    /**
     * @return the authorList
     */
    public String getAuthorList() {
        return authorList;
    }

    /**
     * @param authorList the authorList to set
     */
    public void setAuthorList(String authorList) {
        this.authorList = authorList;
    }

    /**
     * @return the doi
     */
    public String getDoi() {
        return doi;
    }

    /**
     * @param doi the doi to set
     */
    public void setDoi(String doi) {
        this.doi = doi;
    }

    /**
     * @return the pubMedId
     */
    public String getPubMedId() {
        return pubMedId;
    }

    /**
     * @param pubMedId the pubMedId to set
     */
    public void setPubMedId(String pubMedId) {
        this.pubMedId = pubMedId;
    }

    /**
     * @return the status
     */
    public OntologyTerm getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(OntologyTerm status) {
        this.status = status;
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

}
