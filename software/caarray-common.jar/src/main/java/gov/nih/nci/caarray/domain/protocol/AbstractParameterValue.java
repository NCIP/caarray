//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.domain.protocol;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractUnitableValue;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;

/**
 * A value for a parameter of a Protocol for a specific ProtocolApplication of that Protocol.
 * 
 * @author Rashmi Srinivasa
 */
@Entity
@Table(name = "parameter_value")
@Inheritance(strategy = InheritanceType.JOINED)
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public abstract class AbstractParameterValue extends AbstractUnitableValue {
    /**
     * The serial version UID for serialization.
     */
    private static final long serialVersionUID = 1234567890L;

    private Parameter parameter;
    private ProtocolApplication protocolApplication;
    private Term unit;

    /**
     * Default constructor.
     */
    public AbstractParameterValue() {
        // needed by hibernate
    }

    /**
     * Constructs a new ParameterValue based on an existing one.
     * @param other other ParameterValue to copy
     */
    public AbstractParameterValue(AbstractParameterValue other) {
        this.parameter = other.parameter;
        this.protocolApplication = other.protocolApplication;
        this.unit = other.unit;
    }

    /**
     * Constructs a new ParameterValue with given unit.
     * @param unit the unit
     */
    public AbstractParameterValue(Term unit) {
        this.unit = unit;
    }

    /**
     * Gets the parameter.
     *
     * @return the parameter
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "paramvalue_parameter_fk")
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * Sets the parameter.
     *
     * @param parameterVal the parameter
     */
    public void setParameter(final Parameter parameterVal) {
        this.parameter = parameterVal;
    }

    /**
     * @return the protocolApplication
     */
    @ManyToOne
    @ForeignKey(name = "paramvalue_protocolapp_fk")
    public ProtocolApplication getProtocolApplication() {
        return protocolApplication;
    }

    /**
     * @param protocolApplication the protocolApplication to set
     */
    public void setProtocolApplication(ProtocolApplication protocolApplication) {
        this.protocolApplication = protocolApplication;
    }

    /**
     * Gets the unit.
     *
     * @return the unit
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "measurement_pv_unit_fk")
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
     * Checks if two ParameterValues are the same, ignoring ProtocolApplications.
     * Subclasses should override to define additional checks (possibly calling super.matches).
     * @param other other ParameterValue to compare to
     * @return true if they match
     */
    public boolean matches(final AbstractParameterValue other) {
        return new EqualsBuilder().append(this.parameter, other.parameter).append(unit, other.unit).isEquals();
    }
}
