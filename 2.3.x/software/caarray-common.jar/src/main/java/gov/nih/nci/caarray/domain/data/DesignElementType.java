//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Specifies whether a microarray element refers to a location (feature), probe, or probeset.
 */
public enum DesignElementType {

    /**
     * A specific position on a microarray.
     */
    FEATURE("feature"),
    
    /**
     * A reporter with a specific location on a microarray that quantities the amount of a specifc genomic sequence.
     */
    PHYSICAL_PROBE("physicalProbe"),
    
    /**
     * A collection of reporters that together provide the quantification of a single genomic sequence.
     */
    LOGICAL_PROBE("logicalProbe");
    
    private static Map<String, DesignElementType> valueToTypeMap = new HashMap<String, DesignElementType>();
    
    private final String value;

    /**
     * Constructor.
     * 
     * @param value this is the value registered in caDSR for the ValueDomain DesignElementType.
     */
    private DesignElementType(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    private static Map<String, DesignElementType> getValueToTypeMap() {
        if (valueToTypeMap.isEmpty()) {
            for (DesignElementType type : values()) {
                valueToTypeMap.put(type.getValue(), type);
            }
        }
        return valueToTypeMap;
    }
    
    /**
     * Returns the <code>DesignElementType</code> corresponding to the given value. Returns null
     * for null value.
     * 
     * @param value the value to match
     * @return the matching type.
     */
    public static DesignElementType getByValue(String value) {
        checkType(value);
        return getValueToTypeMap().get(value);
    }

    /**
     * Checks to see that the value given is a legal <code>DesignElementType</code> value.
     * 
     * @param value the value to check;
     */
    public static void checkType(String value) {
        if (value != null && !getValueToTypeMap().containsKey(value)) {
            throw new IllegalArgumentException("No matching type for " + value);
        }
    }

}
