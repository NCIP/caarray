//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.google.common.io.ByteStreams;

public class MultiPartBlob_HibernateIntegrationTest extends AbstractHibernateIntegrationTest<MultiPartBlob> {
    @Override
    protected void compareValues(MultiPartBlob original, MultiPartBlob retrieved) {
        try {
            final byte[] originalData = ByteStreams.toByteArray(original.readUncompressedContents());
            final byte[] retrievedData = ByteStreams.toByteArray(original.readUncompressedContents());
            assertTrue(Arrays.equals(originalData, retrievedData));
            assertEquals(original.getCreationTimestamp().getTime(), retrieved.getCreationTimestamp().getTime());
            assertEquals(original.getCompressedSize(), retrieved.getCompressedSize());
            assertEquals(original.getUncompressedSize(), retrieved.getUncompressedSize());
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read data from blob: ", e);
        }
    }

    @Override
    protected void setValues(MultiPartBlob object) {
        try {
            final Calendar now = GregorianCalendar.getInstance();
            now.set(Calendar.MILLISECOND, 0);
            object.setCreationTimestamp(now.getTime());
            object.writeData(new ByteArrayInputStream("Fake Data 123".getBytes()), true, 5);
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't write data into blob: ", e);
        }
    }

    @Override
    protected MultiPartBlob createTestObject() {
        return new MultiPartBlob();
    }
}
