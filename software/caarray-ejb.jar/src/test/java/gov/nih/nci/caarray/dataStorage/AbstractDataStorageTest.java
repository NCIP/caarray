//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.dataStorage.spi.DataStorage;

import java.net.URI;
import java.util.Collections;

import org.junit.Test;

/**
 * @author dkokotov
 */
public abstract class AbstractDataStorageTest {
    protected DataStorage DS;

    protected abstract URI getInvalidUri();

    @Test(expected = UnsupportedSchemeException.class)
    public void testOpenFileInvalidScheme() {
        this.DS.openFile(getInvalidUri(), true);
    }

    @Test(expected = UnsupportedSchemeException.class)
    public void testOpenInputStreamInvalidScheme() {
        this.DS.openInputStream(getInvalidUri(), true);
    }

    @Test(expected = UnsupportedSchemeException.class)
    public void testReleaseFileInvalidScheme() {
        this.DS.releaseFile(getInvalidUri(), true);
    }

    @Test(expected = UnsupportedSchemeException.class)
    public void testRemoveInvalidScheme() {
        this.DS.remove(getInvalidUri());
    }

    @Test(expected = UnsupportedSchemeException.class)
    public void testRemoveMultipleInvalidScheme() {
        this.DS.remove(Collections.singleton(getInvalidUri()));
    }
}
