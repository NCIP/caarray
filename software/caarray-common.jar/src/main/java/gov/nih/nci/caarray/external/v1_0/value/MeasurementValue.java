//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.value;


/**
 * A MeasurementValue is a numerical value.
 * 
 * @author dkokotov
 */
public class MeasurementValue extends AbstractValue {
    private static final long serialVersionUID = 1L;
    
    private Float measurement;

    /**
     * @return the value
     */
    public Float getMeasurement() {
        return measurement;
    }

    /**
     * @param value the value to set
     */
    public void setMeasurement(Float value) {
        this.measurement = value;
    }
}
