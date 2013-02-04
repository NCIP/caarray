//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ParsedDataPersisterTest {
    private static final int[] TEST_ARRAY = new int[] { 1, 2, 3 };
    private static final URI TEST_HANDLE = CaArrayUtils.makeUriQuietly("foo:bar");

    @Mock
    private DataStorageFacade dataStorageFacade;

    private ParsedDataPersister persister;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.persister = new ParsedDataPersister(this.dataStorageFacade);
    }

    @Test
    public void testLoadFromStorage() {
        // given
        final byte[] serialized = CaArrayUtils.serialize(TEST_ARRAY);
        final IntegerColumn column = new IntegerColumn();
        column.setDataHandle(TEST_HANDLE);
        given(this.dataStorageFacade.openInputStream(TEST_HANDLE, true)).willReturn(
                new ByteArrayInputStream(serialized));

        // when
        this.persister.loadFromStorage(column);

        // then
        assertTrue(Arrays.equals(TEST_ARRAY, column.getValues()));
    }

    @Test
    public void testLoadFromStorageNoHandle() {
        final IntegerColumn column = new IntegerColumn();

        // when
        this.persister.loadFromStorage(column);

        // then
        verify(this.dataStorageFacade, never()).openInputStream(any(URI.class), anyBoolean());
    }

    @Test(expected = UnsupportedSchemeException.class)
    public void testLoadFromStorageInvalidHandle() {
        final IntegerColumn column = new IntegerColumn();
        column.setDataHandle(TEST_HANDLE);
        given(this.dataStorageFacade.openInputStream(TEST_HANDLE, true)).willThrow(
                new UnsupportedSchemeException("foo"));

        // when
        this.persister.loadFromStorage(column);

        // then
        verify(this.dataStorageFacade, never()).openInputStream(any(URI.class), anyBoolean());
    }

    @Test
    public void testSaveToStorage() {
        // given
        final IntegerColumn column = new IntegerColumn();
        column.setValues(TEST_ARRAY);
        final StorageMetadata sm = new StorageMetadata();
        sm.setHandle(TEST_HANDLE);
        given(this.dataStorageFacade.addParsed(any(InputStream.class), eq(true))).willReturn(sm);

        // when
        this.persister.saveToStorage(column);

        // then
        assertEquals(TEST_HANDLE, column.getDataHandle());

        final ArgumentCaptor<InputStream> captor = ArgumentCaptor.forClass(InputStream.class);
        verify(this.dataStorageFacade).addParsed(captor.capture(), eq(true));
        assertTrue(Arrays.equals(TEST_ARRAY, (int[]) CaArrayUtils.deserialize(captor.getValue())));
    }

    @Test
    public void testSaveToStorageWithHandleIsNoOp() {
        final IntegerColumn column = new IntegerColumn();
        column.setDataHandle(TEST_HANDLE);

        // when
        this.persister.saveToStorage(column);

        // then
        verify(this.dataStorageFacade, never()).addParsed(any(InputStream.class), anyBoolean());
    }

}
