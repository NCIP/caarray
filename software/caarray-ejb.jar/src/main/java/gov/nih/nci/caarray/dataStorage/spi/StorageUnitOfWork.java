//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dataStorage.spi;

/**
 * Represents a session of working with the storage system, patterned on UnitOfWork from guice-persist. Clients should
 * call begin() before doing operations that will require the storage system, and call end() at the end, in a finally
 * block.
 * 
 * Storage Engine modules should define implementations that initialize and clean up any resources required.
 * 
 * @author dkokotov
 */
public interface StorageUnitOfWork {
    /**
     * Starts a storage Unit Of Work. Storage engine implementations should initialize any necessary resources, e.g. for
     * holding temporary cache copies of data.
     */
    void begin();

    /**
     * Declares an end to the current storage Unit of Work. Storage engine implementations should clean up any resources
     * used during the unit of work. If there is no Unit of work open, then the call returns silently. You can safely
     * invoke end() repeatedly.
     */
    void end();
}
