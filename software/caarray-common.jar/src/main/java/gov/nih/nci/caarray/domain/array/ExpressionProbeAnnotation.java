//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.array;

import gov.nih.nci.cabio.domain.ExpressionArrayReporter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

/**
 *
 */
@Entity
@DiscriminatorValue("EXPRESSION")
public class ExpressionProbeAnnotation extends AbstractProbeAnnotation {

    private static final long serialVersionUID = 1L;

    private Gene gene;
    private ExpressionArrayReporter expressionArrayReporter;

    /**
     * @return the expressionArrayReporter
     */
    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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
