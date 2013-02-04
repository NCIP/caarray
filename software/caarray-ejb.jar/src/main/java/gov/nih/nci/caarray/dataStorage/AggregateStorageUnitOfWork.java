//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage;

import gov.nih.nci.caarray.dataStorage.spi.StorageUnitOfWork;

import java.util.Set;

import com.google.inject.Inject;

/**
 * A StorageUnitOfWork implementation that serves as a composite of all units of work defined by individual storage
 * engines, so that application code need only inject a single StorageUnitOfWork implementation.
 * 
 * @author dkokotov
 */
public class AggregateStorageUnitOfWork implements StorageUnitOfWork {
    private final Set<StorageUnitOfWork> units;

    /**
     * Constructor for given defined set of storage engine-defined unit of works.
     * 
     * @param units the set of StorageUnitOfWork that this will iterate over.
     */
    @Inject
    public AggregateStorageUnitOfWork(Set<StorageUnitOfWork> units) {
        this.units = units;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin() {
        for (final StorageUnitOfWork unit : this.units) {
            unit.begin();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        for (final StorageUnitOfWork unit : this.units) {
            unit.end();
        }
    }
}
