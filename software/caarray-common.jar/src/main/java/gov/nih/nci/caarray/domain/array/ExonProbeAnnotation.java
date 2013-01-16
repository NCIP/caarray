//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.cabio.domain.ExonArrayReporter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

/**
 *
 */
@Entity
@DiscriminatorValue("EXON")
public class ExonProbeAnnotation extends AbstractProbeAnnotation {

    private static final long serialVersionUID = 1L;

    private Long start;
    private Long stop;
    private String strand;
    private String uniprotId;
    private ExonArrayReporter exonArrayReporter;
    private Gene gene;
    
    /**
     * @return the exonArrayReporter
     */
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public ExonArrayReporter getExonArrayReporter() {
        return exonArrayReporter;
    }
    /**
     * @param exonArrayReporter the exonArrayReporter to set
     */
    public void setExonArrayReporter(ExonArrayReporter exonArrayReporter) {
        this.exonArrayReporter = exonArrayReporter;
    }
     /**
     * @return the start
     */
    public Long getStart() {
        return start;
    }
    /**
     * @param start the start to set
     */
    public void setStart(Long start) {
        this.start = start;
    }
    /**
     * @return the stop
     */
    public Long getStop() {
        return stop;
    }
    /**
     * @param stop the stop to set
     */
    public void setStop(Long stop) {
        this.stop = stop;
    }
    /**
     * @return the strand
     */
    public String getStrand() {
        return strand;
    }
    /**
     * @param strand the strand to set
     */
    public void setStrand(String strand) {
        this.strand = strand;
    }
    /**
     * @return the swissprotId
     */
    public String getUniprotId() {
        return uniprotId;
    }
    /**
     * @param swissprotId the swissprotId to set
     */
    public void setUniprotId(String swissprotId) {
        this.uniprotId = swissprotId;
    }

    /**
     * @return the gene
     */
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    public Gene getGene() {
        return gene;
    }

    /**
     * @param gene the gene to set
     */
    public void setGene(Gene gene) {
        this.gene = gene;
    }

}
