//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

/**
 * Enumerates the data types allowed for quantitation types.
 */
public enum DataType {

    /**
     * Values should be represented as Java <code>boolean</code> primitives.
     */
    BOOLEAN(Boolean.class),

    /**
     * Values should be represented as Java <code>short</code> primitives.
     */
    SHORT(Short.class),

    /**
     * Values should be represented as Java <code>int</code> primitives.
     */
    INTEGER(Integer.class),

    /**
     * Values should be represented as Java <code>long</code> primitives.
     */
    LONG(Long.class),

    /**
     * Values should be represented as Java <code>float</code> primitives.
     */
    FLOAT(Float.class),

    /**
     * Values should be represented as Java <code>double</code> primitives.
     */
    DOUBLE(Double.class),

    /**
     * Values should be represented as Java <code>Strings</code>.
     */
    STRING(String.class);

    private Class<?> typeClass;

    private DataType(Class<?> typeClass) {
        this.typeClass = typeClass;
    }

    /**
     * Returns the DataType enumeration value corresponding to the given type class.
     * @param typeClass the type class
     * @return the matching DataType
     */
    public static DataType fromTypeClass(Class<?> typeClass) {
        for (DataType dt : values()) {
            if (dt.getTypeClass().equals(typeClass)) {
                return dt;
            }
        }
        throw new IllegalArgumentException("Type class " + typeClass + " does not correspond to a DataType constant");
    }

    /**
     * Returns the Java class that corresponds to the data type.
     *
     * @return the corresponding class.
     */
    public Class<?> getTypeClass() {
        return typeClass;
    }
    
    /**
     * @return result of calling name(), to enable retrieving it using JavaBean conventions.
     */
    public String getName() {
        return name();
    }
}
