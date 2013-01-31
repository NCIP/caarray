//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.SerializationHelperUtility;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.castor.util.Base64Decoder;
import org.castor.util.Base64Encoder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;

/**
 * Subclasses of <code>AbstractDataColumn</code> contain the actual array data corresponding
 * to a single <code>QuantitationType</code>.
 */
@Entity
@Table(name = "datacolumn")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "discriminator",
        discriminatorType = DiscriminatorType.STRING)
@SuppressWarnings("PMD.CyclomaticComplexity") // switch-like statement
public abstract class AbstractDataColumn extends AbstractCaArrayObject {
    private static final long serialVersionUID = 1L;

    private HybridizationData hybridizationData;
    private QuantitationType quantitationType;
    private Serializer valuesSerializer = new Serializer();

    @SuppressWarnings("PMD.CyclomaticComplexity") // switch-like statement
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
        return quantitationType;
    }

    /**
     * @param quantitationType the quantitationType to set
     */
    public void setQuantitationType(QuantitationType quantitationType) {
        this.quantitationType = quantitationType;
    }

    @Transient
    Serializable getValuesAsSerializable() {
        return valuesSerializer.getValue();
    }

    void setSerializableValues(Serializable values) {
        valuesSerializer.setValue(values);
    }
    
    @Transient
    private byte[] getValuesAsByteArray() {
        return valuesSerializer.getSerializedValues();
    }
    
    private void setValuesAsByteArray(byte[] values) {
        valuesSerializer.setValue(SerializationHelperUtility.deserialize(values));
    }

    /**
     * Returns the values of this data column, as a base64-encoded string of the byte representation of the values.
     * The byte representation of the values is obtained by applying GZip compression to the Java serialization
     * of the values.
     * @return the base64-encoded byte representation of the values of this column.
     */
    @Transient
    public String getValuesAsString() {
        return String.copyValueOf(Base64Encoder.encode(getValuesAsByteArray()));
    }

    /**
     * Set the values of this column from the given string, which contains their byte representation encoded using 
     * base64 encoding.
     * @param base64Enc the base64 encoding of the byte representation of the values, where the byte representation
     * is obtained by applying GZip compression to the Java serialization of the values.
     */
    public void setValuesAsString(String base64Enc) {
        byte[] bytes = Base64Decoder.decode(base64Enc);
        setValuesAsByteArray(bytes);
    }


    /**
     * Initializes this column to hold the number of values given.
     *
     * @param numberOfValues number of values
     */
    public abstract void initializeArray(int numberOfValues);

    /**
     * Indicates whether this column is already loaded.
     *
     * @return true if data has been loaded.
     */
    @Transient
    public boolean isLoaded() {
        return valuesSerializer.getValue() != null;
    }

    /**
     * @return the hybridizationData
     */
    @ManyToOne
    @JoinColumn(updatable = false, nullable = false)
    @ForeignKey(name = "column_hybridizationdata_fk")
    @IndexColumn(name = "column_index")
    public HybridizationData getHybridizationData() {
        return hybridizationData;
    }

    /**
     * @param hybridizationData the hybridizationData to set
     */
    public void setHybridizationData(HybridizationData hybridizationData) {
        this.hybridizationData = hybridizationData;
    }

    /**
     * @return the valuesSerializer
     */
    @Embedded
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Serializer getValuesSerializer() {
        return valuesSerializer;
    }

    /**
     * @param valuesSerializer the valuesSerializer to set
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void setValuesSerializer(Serializer valuesSerializer) {
        this.valuesSerializer = valuesSerializer;
    }

}
