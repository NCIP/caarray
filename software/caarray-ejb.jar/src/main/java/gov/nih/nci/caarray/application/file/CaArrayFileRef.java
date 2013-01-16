//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.io.FileRef;

import java.io.File;

import com.google.common.base.Preconditions;

/**
 * CaArrayFile wrapper for magetab API.
 * 
 * @author gax
 * @since 2.4.0
 */
public class CaArrayFileRef implements FileRef {
    private final CaArrayFile file;
    private final DataStorageFacade dataStorageFacade;

    /**
     * wrapper ctor.
     * 
     * @param file the CaArrayFile to wrap.
     * @param dataStorageFacade the dataStorageFacade to use for retrieving data for the file
     */
    public CaArrayFileRef(CaArrayFile file, DataStorageFacade dataStorageFacade) {
        Preconditions.checkNotNull(file, "file cannot be null");
        this.file = file;
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * @return name the CaArrayFile file.
     */
    @Override
    public String getName() {
        return this.file.getName();
    }

    /**
     * @return if the referenced file is a partial file or not.
     */
    @Override
    public boolean isPartialFile() {
        return file.isPartial();
    }

    /**
     * @return a java.io.File which has the contents of the CaArrayFile this wraps
     */
    @Override
    public File getAsFile() {
        return this.dataStorageFacade.openFile(this.file.getDataHandle(), false);
    }

    /**
     * CaArrayFiles always exist.
     * 
     * @return true.
     */
    @Override
    public boolean exists() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.file.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.file.equals(((CaArrayFileRef) obj).file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.file.hashCode();
    }

}
