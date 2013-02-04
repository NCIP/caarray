//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.BlobHolder;
import gov.nih.nci.caarray.domain.MultiPartBlob;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteStreams;

/**
 * Tests of MultipartBlobDao
 * 
 * @author dkokotov
 */
public class MultipartBlobDaoTest extends AbstractDaoTest {
    private static MultipartBlobDao DAO_OBJECT;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        DAO_OBJECT = new MultipartBlobDaoImpl(this.hibernateHelper);
    }

    @Test
    public void testSaveAndRemove() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();

        MultiPartBlob mblob = new MultiPartBlob();
        mblob.writeData(new ByteArrayInputStream("Fake Data 123".getBytes()), true, 5);
        mblob.setCreationTimestamp(new Date());
        DAO_OBJECT.save(mblob);
        this.hibernateHelper.getCurrentSession().flush();
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        mblob = (MultiPartBlob) this.hibernateHelper.getCurrentSession().get(MultiPartBlob.class, mblob.getId());
        assertNotNull(mblob);
        assertTrue(mblob.getBlobParts().size() > 1);
        final String data = new String(ByteStreams.toByteArray(mblob.readUncompressedContents()));
        assertEquals("Fake Data 123", data);
        DAO_OBJECT.remove(mblob);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        mblob = (MultiPartBlob) this.hibernateHelper.getCurrentSession().get(MultiPartBlob.class, mblob.getId());
        assertNull(mblob);
        tx.commit();
    }

    @Test
    public void testDeleteByIds() throws Exception {
        final List<Long> mblobIds = new LinkedList<Long>();
        final List<Long> bhIds = new LinkedList<Long>();
        Transaction tx = this.hibernateHelper.beginTransaction();

        MultiPartBlob mblob = new MultiPartBlob();
        mblob.writeData(new ByteArrayInputStream("Fake Data 123".getBytes()), true, 5);
        mblob.setCreationTimestamp(new Date());
        DAO_OBJECT.save(mblob);
        this.hibernateHelper.getCurrentSession().flush();
        mblobIds.add(mblob.getId());
        for (final BlobHolder bh : mblob.getBlobParts()) {
            assertNotNull(bh);
            assertNotNull(bh.getId());
            bhIds.add(bh.getId());
        }

        mblob = new MultiPartBlob();
        mblob.writeData(new ByteArrayInputStream("Fake Data 456".getBytes()), true, 20000);
        mblob.setCreationTimestamp(new Date());
        DAO_OBJECT.save(mblob);
        this.hibernateHelper.getCurrentSession().flush();
        mblobIds.add(mblob.getId());
        for (final BlobHolder bh : mblob.getBlobParts()) {
            assertNotNull(bh);
            assertNotNull(bh.getId());
            bhIds.add(bh.getId());
        }
        tx.commit();

        // add in a bad id
        mblobIds.add(10001L);
        tx = this.hibernateHelper.beginTransaction();
        this.hibernateHelper.getCurrentSession().clear();
        DAO_OBJECT.deleteByIds(mblobIds);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        for (final Long mblobId : mblobIds) {
            assertNull(this.hibernateHelper.getCurrentSession().get(MultiPartBlob.class, mblobId));
        }
        for (final Long bhId : bhIds) {
            assertNull(this.hibernateHelper.getCurrentSession().get(BlobHolder.class, bhId));
        }
        tx.commit();
    }
}
