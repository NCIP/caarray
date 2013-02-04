//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;

/**
 * Default implementation of ValueParser that simply uses the Java wrapper classes' parse* methods. The handling of 
 * each value type can be modified by a subclass by overriding the appropriate parse* method in this class.
 * 
 * @author dkokotov
 */
public class DefaultValueParser implements ValueParser {

    /**
     * This string will be interpreted as Float.NaN.
     */
    public static final String NA = "NA";
    
    /**
     * An alternative string which will be interpreted as Float.NaN.
     */
    public static final String ALT_NA = "N/A";

    /**
     * Set value at given row of the given column to the given value.
     * @param column the column 
     * @param rowIndex the index of the row in the column
     * @param value the value 
     */
    public void setValue(AbstractDataColumn column, int rowIndex, String value) {
        switch (column.getQuantitationType().getDataType()) {
            case BOOLEAN :
                ((BooleanColumn) column).getValues()[rowIndex] = parseBoolean(value); break;
            case SHORT :
                ((ShortColumn) column).getValues()[rowIndex] = parseShort(value); break;
            case INTEGER :
                ((IntegerColumn) column).getValues()[rowIndex] = parseInt(value); break;
            case LONG :
                ((LongColumn) column).getValues()[rowIndex] = parseLong(value); break;
            case FLOAT :
                ((FloatColumn) column).getValues()[rowIndex] = parseFloat(value); break;
            case DOUBLE :
                ((DoubleColumn) column).getValues()[rowIndex] = parseDouble(value);
            case STRING :
                ((StringColumn) column).getValues()[rowIndex] = value; break;
            default :
                throw new IllegalArgumentException("Unsupported type class "
                        + column.getQuantitationType().getDataType());
        }
    }

    /**
     * Parse a String value into a boolean.
     * @param value the value to parse.
     * @return the boolean equivalent of the String value.
     */
    protected boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    /**
     * Parse a String value into a short.
     * @param value the value to parse.
     * @return the short equivalent of the String value.
     */
    protected short parseShort(String value) {
        return Short.parseShort(value);
    }

    /**
     * Parse a String value into a integer.
     * @param value the value to parse.
     * @return the integer equivalent of the String value.
     */
    protected int parseInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Parse a String value into a long.
     * @param value the value to parse.
     * @return the long equivalent of the String value.
     */
    protected long parseLong(String value) {
        return Long.parseLong(value);
    }

    /**
     * Parse a String value into a float.
     * @param value the value to parse.
     * @return the float equivalent of the String value.
     */
    protected float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException numberFormatException) {
            if (valueIsSpecial(value)) {
                return Float.NaN;
            } else {
                throw numberFormatException;
            }
        }
    }

    /**
     * Parse a String value into a double.
     * @param value the value to parse.
     * @return the double equivalent of the String value.
     */
    protected double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    private boolean valueIsSpecial(final String value) {
        boolean valueIsSpecial = false;
        if (NA.equalsIgnoreCase(value) || ALT_NA.equalsIgnoreCase(value)) {
            valueIsSpecial = true;
        }
        return valueIsSpecial;
    }
}
