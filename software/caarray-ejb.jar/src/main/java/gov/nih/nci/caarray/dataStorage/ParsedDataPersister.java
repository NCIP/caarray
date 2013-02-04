//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

/**
 * Helper class used to manage loading and saving parsed data to/from the data storage subsystem.
 * 
 * @author dkokotov
 */
public class ParsedDataPersister implements Serializable {
    private static final long serialVersionUID = 1L;

    private final DataStorageFacade dataStorageFacade;

    /**
     * Constructor.
     * 
     * @param dataStorageFacade the DataStorageFacade to use for interacting with the data storage subsystem.
     */
    @Inject
    public ParsedDataPersister(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * Load the contents of the given data column from storage. If the column's data handle is null, this method does
     * nothing; otherwise, it loads the data identified by the column's data handle and sets it into the column.
     * 
     * @param column the data column for which to load data from storage.
     */
    public void loadFromStorage(AbstractDataColumn column) {
        if (column.getDataHandle() == null) {
            // no data to load
            return;
        }
        final InputStream is = this.dataStorageFacade.openInputStream(column.getDataHandle(), true);
        try {
            final Serializable value = CaArrayUtils.deserialize(is);
            column.setValuesFromArray(value);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * Save the contents of the given data column to data storage. If the column's data handle is already set, this
     * method does nothing; otherwise, it writes the column's data to data storage and sets the data handle with the
     * reference to the written data.
     * 
     * @param column the data column for which to save data to storage.
     */
    public void saveToStorage(AbstractDataColumn column) {
        if (column.getDataHandle() != null) {
            // data is already saved.
            return;
        }
        final byte[] serializedValues = CaArrayUtils.serialize(column.getValuesAsArray());
        final ByteArrayInputStream bais = new ByteArrayInputStream(serializedValues);
        try {
            final StorageMetadata metadata = this.dataStorageFacade.addParsed(bais, true);
            column.setDataHandle(metadata.getHandle());
        } finally {
            IOUtils.closeQuietly(bais);
        }
    }
}
