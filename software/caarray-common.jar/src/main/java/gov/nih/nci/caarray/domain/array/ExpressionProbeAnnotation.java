//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.cabio.domain.ExpressionArrayReporter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

/**
 *
 */
@Entity
@DiscriminatorValue("EXPRESSION")
public class ExpressionProbeAnnotation extends AbstractProbeAnnotation {

    private static final long serialVersionUID = 1L;

    private Gene gene;
    private ExpressionArrayReporter expressionArrayReporter;
    private String chromosomeName;
    private Long chromosomeStartPosition;
    private Long chromosomeEndPosition;

    /**
     * @return the expressionArrayReporter
     */
    @OneToOne
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.EVICT })
    public ExpressionArrayReporter getExpressionArrayReporter() {
        return expressionArrayReporter;
    }
    
    /**
     * @param expressionArrayReporter the expressionArrayReporter to set
     */
    public void setExpressionArrayReporter(ExpressionArrayReporter expressionArrayReporter) {
        this.expressionArrayReporter = expressionArrayReporter;
    }

    /**
     * @return the gene
     */
    @OneToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.EVICT })
    public Gene getGene() {
        return gene;
    }

    /**
     * @param gene the gene to set
     */
    public void setGene(Gene gene) {
        this.gene = gene;
    }

    /**
     * @param chromosomeName chromosome number (1-22) or letter (x or y)
     */
    public void setChromosomeName(String chromosomeName) {
        this.chromosomeName = chromosomeName;
    }

    /**
     * @return chromosome number (1-22) or letter (x or y)
     */
    public String getChromosomeName() {
        return chromosomeName;
    }

    /**
     * @param chromosomeStartPosition start position on the chromosome
     */
    public void setChromosomeStartPosition(Long chromosomeStartPosition) {
        this.chromosomeStartPosition = chromosomeStartPosition;
    }

    /**
     * @return start position on the chromosome
     */
    public Long getChromosomeStartPosition() {
        return chromosomeStartPosition;
    }

    /**
     * @param chromosomeEndPosition end position on the chromosome
     */
    public void setChromosomeEndPosition(Long chromosomeEndPosition) {
        this.chromosomeEndPosition = chromosomeEndPosition;
    }

    /**
     * @return end position on the chromosome
     */
    public Long getChromosomeEndPosition() {
        return chromosomeEndPosition;
    }
    
    /**
     * @param name number (1-22) or letter (x or y)
     * @param startPosition start position on the chromosome
     * @param endPosition end position on the chromosome
     */
    public void setChromosome(String name, long startPosition, long endPosition) {
        setChromosomeName(name);
        setChromosomeStartPosition(startPosition);
        setChromosomeEndPosition(endPosition);
    }


}
