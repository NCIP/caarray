//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractUnitableValue;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 * @author Rashmi Srinivasa
 */
@Entity
@Table(name = "factor_value")
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class AbstractFactorValue extends AbstractUnitableValue {
    private static final long serialVersionUID = 1234567890L;

    private Factor factor;
    private Hybridization hybridization;
    private Term unit;

    /**
     * Hibernate-only constructor.
     */
    public AbstractFactorValue() {
        // empty
    }

    /**
     * Create a new factor value with given unit.
     * @param unit the unit for the value
     */
    public AbstractFactorValue(Term unit) {
        this.unit = unit;
    }

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
     * @return the hybridization
     */
    @ManyToOne(fetch = FetchType.LAZY)
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
     * Gets the unit.
     *
     * @return the unit
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "measurement_fv_unit_fk")
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
}
