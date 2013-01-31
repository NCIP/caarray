//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import javax.persistence.Entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.validator.Length;

/**
 * A hereditary unit consisting of a sequence of DNA that occupies a specific location on a chromosome and 
 * determines a particular characteristic in an organism.  The functional and physical unit of heredity passed 
 * from parent to offspring.
 */
@Entity
@BatchSize(size = AbstractProbeAnnotation.RELATED_BATCH_SIZE)
public class Gene extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;

    private String fullName;
    private String symbol;
    private String genbankAccession;
    private String genbankAccessionVersion;
    private String ensemblgeneID;
    private String unigeneclusterID;
    private String entrezgeneID;

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    /**
     * @return the fullName
     */
    @Length(max = LARGE_TEXT_FIELD_LENGTH)
    public String getFullName() {
        return fullName;
    }
    
    /**
     * @param fullName the fullName to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * @return the ensemblgeneID
     */
    public String getEnsemblgeneID() {
        return ensemblgeneID;
    }

    /**
     * @param ensemblgeneID the ensemblgeneID to set
     */
    public void setEnsemblgeneID(String ensemblgeneID) {
        this.ensemblgeneID = ensemblgeneID;
    }

    /**
     * @return the entrezgeneID
     */
    public String getEntrezgeneID() {
        return entrezgeneID;
    }

    /**
     * @param entrezgeneID the entrezgeneID to set
     */
    public void setEntrezgeneID(String entrezgeneID) {
        this.entrezgeneID = entrezgeneID;
    }

    /**
     * @return the genbankAccession
     */
    public String getGenbankAccession() {
        return genbankAccession;
    }

    /**
     * @param genbankAccession the genbankAccession to set
     */
    public void setGenbankAccession(String genbankAccession) {
        this.genbankAccession = genbankAccession;
    }

    /**
     * @return the genbankAccessionVersion
     */
    public String getGenbankAccessionVersion() {
        return genbankAccessionVersion;
    }

    /**
     * @param genbankAccessionVersion the genbankAccessionVersion to set
     */
    public void setGenbankAccessionVersion(String genbankAccessionVersion) {
        this.genbankAccessionVersion = genbankAccessionVersion;
    }

    /**
     * @return the unigeneclusterID
     */
    public String getUnigeneclusterID() {
        return unigeneclusterID;
    }

    /**
     * @param unigeneclusterID the unigeneclusterID to set
     */
    public void setUnigeneclusterID(String unigeneclusterID) {
        this.unigeneclusterID = unigeneclusterID;
    }

}
