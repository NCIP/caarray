//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.cabio.domain.SNPArrayReporter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

/**
 *
 */
@Entity
@DiscriminatorValue("SNP")
public class SNPProbeAnnotation extends AbstractProbeAnnotation {

    private static final long serialVersionUID = 1L;

    private String dbSNPId;
    private Integer dbSNPVersion;
    private String alleleA;
    private String alleleB;
    private Integer chromosome;
    private Long physicalPosition;
    private String flank;
    private SNPArrayReporter snpArrayReporter;

    /**
     * @return the alleleA
     */
    public String getAlleleA() {
        return alleleA;
    }
    /**
     * @param alleleA the alleleA to set
     */
    public void setAlleleA(String alleleA) {
        this.alleleA = alleleA;
    }
    /**
     * @return the alleleB
     */
    public String getAlleleB() {
        return alleleB;
    }
    /**
     * @param alleleB the alleleB to set
     */
    public void setAlleleB(String alleleB) {
        this.alleleB = alleleB;
    }
    /**
     * @return the chromosome
     */
    public Integer getChromosome() {
        return chromosome;
    }
    /**
     * @param chromosome the chromosome to set
     */
    public void setChromosome(Integer chromosome) {
        this.chromosome = chromosome;
    }
    /**
     * @return the dbSnpId
     */
    public String getDbSNPId() {
        return dbSNPId;
    }
    /**
     * @param dbSnpId the dbSnpId to set
     */
    public void setDbSNPId(String dbSnpId) {
        this.dbSNPId = dbSnpId;
    }
    /**
     * @return the flank
     */
    public String getFlank() {
        return flank;
    }
    /**
     * @param flank the flank to set
     */
    public void setFlank(String flank) {
        this.flank = flank;
    }
    /**
     * @return the physicalPosition
     */
    public Long getPhysicalPosition() {
        return physicalPosition;
    }
    /**
     * @param physicalPosition the physicalPosition to set
     */
    public void setPhysicalPosition(Long physicalPosition) {
        this.physicalPosition = physicalPosition;
    }
    /**
     * @return the snpArrayReporter
     */
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public SNPArrayReporter getSnpArrayReporter() {
        return snpArrayReporter;
    }
    /**
     * @param snpArrayReporter the snpArrayReporter to set
     */
    public void setSnpArrayReporter(SNPArrayReporter snpArrayReporter) {
        this.snpArrayReporter = snpArrayReporter;
    }
    /**
     * @return the dbSNPVersion
     */
    public Integer getDbSNPVersion() {
        return dbSNPVersion;
    }
    /**
     * @param dbSNPVersion the dbSNPVersion to set
     */
    public void setDbSNPVersion(Integer dbSNPVersion) {
        this.dbSNPVersion = dbSNPVersion;
    }

}
