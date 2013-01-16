//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.util.URIUserType;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

/**
 * Subclasses of <code>AbstractDataColumn</code> contain the actual array data corresponding to a single
 * <code>QuantitationType</code>.
 * 
 * <p><b>Note:</b> AbstractDataColumn is <em>not</em> a normal hibernate object.  The values API (getValuesAsArray,
 * initializeArray, and setValuesFromArray) does not manipulate hibernate-managed information.  Instead,
 * values are managed by the DataStorageFacade.  To properly initialize this class, ParsedDataPersister
 * must be utilized.  NPEs will result from incorrect usage.
 */
@TypeDefs(@TypeDef(name = "uri", typeClass = URIUserType.class))
@Entity
@Table(name = "datacolumn")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@SuppressWarnings("PMD.CyclomaticComplexity")
public abstract class AbstractDataColumn extends AbstractCaArrayObject {
    private static final long serialVersionUID = 1L;

    /** separator to use for encoding an array of values as string, except for StringColumn. */
    protected static final String SEPARATOR = " ";
    
    /** Error message for incorrect usage of uninitialized values array. */
    protected static final String ERROR_NOT_INITIALIZED = 
            "Cannot get uninitialized values array - must be loaded from DataStorage";

    private HybridizationData hybridizationData;
    private QuantitationType quantitationType;
    private URI dataHandle;

    @SuppressWarnings("PMD.CyclomaticComplexity")
    static AbstractDataColumn create(QuantitationType type) {
        AbstractDataColumn column = null;
        if (type.getTypeClass().equals(Boolean.class)) {
            column = new BooleanColumn();
        } else if (type.getTypeClass().equals(Double.class)) {
            column = new DoubleColumn();
        } else if (type.getTypeClass().equals(Float.class)) {
            column = new FloatColumn();
        } else if (type.getTypeClass().equals(Integer.class)) {
            column = new IntegerColumn();
        } else if (type.getTypeClass().equals(Long.class)) {
            column = new LongColumn();
        } else if (type.getTypeClass().equals(Short.class)) {
            column = new ShortColumn();
        } else if (type.getTypeClass().equals(String.class)) {
            column = new StringColumn();
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + type.getType());
        }
        column.setQuantitationType(type);
        return column;
    }

    /**
     * @return the quantitationType
     */
    @ManyToOne
    @ForeignKey(name = "column_quantitationtype_fk")
    public QuantitationType getQuantitationType() {
        return this.quantitationType;
    }

    /**
     * @param quantitationType the quantitationType to set
     */
    public void setQuantitationType(QuantitationType quantitationType) {
        this.quantitationType = quantitationType;
    }

    /**
     * Indicates whether this column is already loaded, meaning its populated with an array of values.
     * 
     * @return true if data has been loaded.
     */
    @Transient
    public abstract boolean isLoaded();

    /**
     * @return the hybridizationData
     */
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    @ForeignKey(name = "column_hybridizationdata_fk")
    @IndexColumn(name = "column_index")
    public HybridizationData getHybridizationData() {
        return this.hybridizationData;
    }

    /**
     * @param hybridizationData the hybridizationData to set
     */
    public void setHybridizationData(HybridizationData hybridizationData) {
        this.hybridizationData = hybridizationData;
    }

    /**
     * @return the dataHandle
     */
    @Column(length = DEFAULT_STRING_COLUMN_SIZE)
    @Index(name = "idx_handle")
    @Type(type = "uri")
    public URI getDataHandle() {
        return this.dataHandle;
    }

    /**
     * @param dataHandle the dataHandle to set
     */
    public void setDataHandle(URI dataHandle) {
        this.dataHandle = dataHandle;
    }

    /**
     * Serialized values, from the DataStorageFacade.
     * 
     * @return the values in this column as an array. Subclasses should return an array of the appropriate primitive
     *         type or String.
     */
    @Transient
    public abstract Serializable getValuesAsArray();

    /**
     * Set the values of this column from a value array.  The incoming values should be coming
     * from either a parsed file, or the DataStorageFacade.
     * 
     * @param array the values for this column. Should be an array of the appropriate primitive or String type.
     */
    public abstract void setValuesFromArray(Serializable array);

    
    // get/setValuesAsString is called via xml-mapping.xml for the remote APIs.  These are the only clients
    // to these methods.  Do not remove them.
    
    /**
     * @return the values of this column, in a space-separated representation, where each value is encoded using the
     *         literal representation of the xs:short type defined in the XML Schema standard.
     */
    @Transient
    public abstract String getValuesAsString();

    /**
     * Set values from a String representation. The string should contain a list of space-separated values, with each
     * value encoded using the literal representation of the xs:boolean type defined in XML Schema.
     * 
     * @param s the string containing the space-separated values
     */
    public abstract void setValuesAsString(String s);

    /**
     * Initializes this column to hold the number of values given.
     * 
     * @param numberOfValues number of values
     */
    public abstract void initializeArray(int numberOfValues);
}
