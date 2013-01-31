//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;

  /**

   */
@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class FactorValue extends AbstractCaArrayEntity {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;


    private Factor factor;
    private Term unit;
    private String value;
    private Hybridization hybridization;

    /**
     * Gets the factor.
     *
     * @return the factor
     */
    @ManyToOne
    @ForeignKey(name = "factorvalue_factor_fk")
    public Factor getFactor() {
        return factor;
    }

    /**
     * Sets the factor.
     *
     * @param factorVal the factor
     */
    public void setFactor(final Factor factorVal) {
        this.factor = factorVal;
    }

    /**
     * Gets the unit.
     *
     * @return the unit
     */
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "factorvalue_unit_fk")
    public Term getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     *
     * @param unitVal the unit
     */
    public void setUnit(final Term unitVal) {
        this.unit = unitVal;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param valueString the value
     */
    public void setValue(final String valueString) {
        this.value = valueString;
    }

    /**
     * @return the hybridization
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "factorvalue_hybridizatation_fk")
    public Hybridization getHybridization() {
        return hybridization;
    }

    /**
     * @param hybridization the hybridization to set
     */
    public void setHybridization(Hybridization hybridization) {
        this.hybridization = hybridization;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
