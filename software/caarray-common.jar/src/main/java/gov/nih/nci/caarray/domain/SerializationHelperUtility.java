//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Helper utility for reading and writing serialized objects to zipped byte arrays.
 */
public final class SerializationHelperUtility {

    private static final Logger LOG = Logger.getLogger(SerializationHelperUtility.class);

    private SerializationHelperUtility() {
        super();
    }

    /**
     * Serializes the given object (zipped) to a byte array.
     *
     * @param serializable object to serialize
     * @return the serialized object as a byte array.
     */
    public static byte[] serialize(Serializable serializable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZipOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            gZipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            objectOutputStream = new ObjectOutputStream(gZipOutputStream);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
            gZipOutputStream.flush();
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            LOG.error("Couldn't write object", e);
            throw new IllegalStateException("Couldn't serialize object", e);
        } finally {
            close(objectOutputStream);
            close(gZipOutputStream);
            close(byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes an object from a zipped serialized representation in a byte array.
     *
     * @param bytes the byte array
     * @return the deserialized object.
     */
    public static Serializable deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = null;
        ObjectInputStream objectInputStream = null;
        Serializable object = null;
        try {
            gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            objectInputStream = new ObjectInputStream(gzipInputStream);
            object = (Serializable) objectInputStream.readObject();
        } catch (IOException e) {
            String message = "Couldn't read object";
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        } catch (ClassNotFoundException e) {
            String message = "Couldn't read object";
            LOG.error(message, e);
            throw new IllegalStateException(message, e);
        } finally {
            close(objectInputStream);
            close(gzipInputStream);
            close(byteArrayInputStream);
        }
        return object;
    }

    private static void close(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                 LOG.error("Couldn't close stream", e);
            }
        }
    }

    private static void close(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                 LOG.error("Couldn't close stream", e);
            }
        }
    }

}
