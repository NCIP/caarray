//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.value.MeasurementValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Characteristic represents a loosely typed attribute associated with a sample. The attribute consists of a category
 * and a value.
 * 
 * @author dkokotov
 */
public class Characteristic implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Category category;
    private AbstractValue value;

    /**
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * @return the value
     */
    public AbstractValue getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(AbstractValue value) {
        this.value = value;
    }

    /**
     * @return a new Characteristic initialized with a blank measurement value;
     */
    public static Characteristic newMeasurementCharacteristic() {
        return newCharacteristic(new MeasurementValue());
    }

    /**
     * @return a new Characteristic initialized with a blank term value;
     */
    public static Characteristic newTermCharacteristic() {
        return newCharacteristic(new TermValue());
    }

    /**
     * @return a new Characteristic initialized with a blank user defined value;
     */
    public static Characteristic newUserDefinedCharacteristic() {
        return newCharacteristic(new UserDefinedValue());
    }
    
    private static Characteristic newCharacteristic(AbstractValue initialValue) {
        Characteristic fv = new Characteristic();
        fv.setValue(initialValue);
        return fv;
    }

    /**
     * Set the value of this charactestic to be a TermValue with given term.
     * 
     * @param term the term
     */
    public void setTermValue(Term term) {
        TermValue tv = new TermValue(); 
        tv.setTerm(term);
        this.value = tv;
    }

    /**
     * @return if the value of this charactestic is a TermValue, return the term for that value, else return null.
     */
    public Term getTermValue() {
        if (this.value instanceof TermValue) {
            return ((TermValue) this.value).getTerm();            
        } else {
            return null;
        }
    }

    /**
     * Set the value of this charactestic to be a MeasurementValue with given measurement.
     * 
     * @param measurement the measurement
     */
    public void setMeasurementValue(Float measurement) {
        MeasurementValue mv = new MeasurementValue(); 
        mv.setMeasurement(measurement);
        this.value = mv;
    }

    /**
     * @return if the value of this charactestic is a MeasurementValue, return the measurement for that value, else
     *         return null.
     */
    public Float getMeasurementValue() {
        if (this.value instanceof MeasurementValue) {
            return ((MeasurementValue) this.value).getMeasurement();            
        } else {
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
