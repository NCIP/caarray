//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An AbstractDataColumn represents a list of values for a particular measurement within a hybridization data set. 
 * There are subclasses of this class for different value types (e.g. integers, floats, booleans, etc). 
 * 
 * @author dkokotov
 */
public abstract class AbstractDataColumn implements Externalizable {
    private static final long serialVersionUID = 1L;
    
    /** separator to use for encoding an array of values as string, except for StringColumn. */
    protected static final String SEPARATOR = " ";
    
    private QuantitationType quantitationType;

    /**
     * @return the value type of values in this column
     */
    public QuantitationType getQuantitationType() {
        return quantitationType;
    }

    /**
     * @param quantitationType the quantitationType to set
     */
    public void setQuantitationType(QuantitationType quantitationType) {
        this.quantitationType = quantitationType;
    }

    /**
     * Optimizes the serialized form of this class by compressing the values array.
     * 
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput oo) throws IOException {
        oo.writeObject(this.quantitationType);
        byte[] serializedValues = CaArrayUtils.serialize(getSerializableValues());
        oo.writeInt(serializedValues.length);
        oo.write(serializedValues, 0, serializedValues.length);
    }
    
    /**
     * Optimizes the serialized form of this class by compressing the values array.
     * 
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        this.quantitationType = (QuantitationType) oi.readObject();
        int valuesLength = oi.readInt();
        byte[] serializedValues = new byte[valuesLength];
        oi.readFully(serializedValues);
        setSerializableValues(CaArrayUtils.deserialize(serializedValues));
    }
 
    /**
     * Get the values array of this column. This should be implemented by a simple call to getValues in the
     * subclasses; this separate method is only needed because generics do not work well with 
     * primitive arrays. 
     * @return the values array for this column
     */
    protected abstract Serializable getSerializableValues();

    /**
     * Set the values array of this column. This should be implemented by a simple call to setValues in the
     * subclasses; this separate method is only needed because generics do not work well with 
     * primitive arrays. 
     * @param values the values array to set
     */
    protected abstract void setSerializableValues(Serializable values);
    
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
