//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Transient;

import org.apache.commons.io.IOUtils;

/**
 * Manages serialization of Objects to and from a compressed byte[].
 */
@Embeddable
class Serializer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Serializable value;
    private MultiPartBlob serializedValue;

    @Transient
    Serializable getValue() {
        if (isSerialized()) {
            deserialize();
        }
        return value;
    }

    void setValue(Serializable value) {
        serializedValue = null;
        this.value = value;
    }

    @Transient
    byte[] getSerializedValues() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            IOUtils.copy(getSerializedValue().readUncompressedContents(), outputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't serialize data", e);
        }
        return outputStream.toByteArray();
    }

    void setSerializedValues(byte[] serializedBytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedBytes);
        MultiPartBlob multipartBlob = new MultiPartBlob();
        try {
            multipartBlob.writeDataUncompressed(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't write serialized data", e);
        }
        setSerializedValue(multipartBlob);
    }

    @Transient
    private boolean isSerialized() {
        return serializedValue != null;
    }

    private void serialize() {
        setSerializedValues(CaArrayUtils.serialize(value));
        value = null;
    }

    private void deserialize() {
        value = CaArrayUtils.deserialize(getSerializedValues());
        serializedValue = null;
    }
    
    /**
     * @return the serializedValue
     */
    @Embedded
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    MultiPartBlob getSerializedValue() {
        if (!isSerialized()) {
            serialize();
        }
        return this.serializedValue;
    }

    /**
     * @param serializedValue the serializedValue to set
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    void setSerializedValue(MultiPartBlob serializedValue) {
        this.serializedValue = serializedValue;
        this.value = null;
    }

}
