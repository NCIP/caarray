//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

/**
 * Base class for DataFileHandler implementations. Provides fields for common dependencies and takes care of basic file
 * management.
 * 
 * @author dkokotov
 */
public abstract class AbstractDataFileHandler implements DataFileHandler {
    private final DataStorageFacade dataStorageFacade;

    private CaArrayFile caArrayFile;
    private File file;

    private MageTabDocumentSet mageTabDocumentSet;

    /**
     * @param dataStorageFacade data storage facade for retrieving file data
     */
    protected AbstractDataFileHandler(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFile(CaArrayFile dataFile) throws PlatformFileReadException {
        if (!isFileSupported(dataFile)) {
            return false;
        }

        this.caArrayFile = dataFile;
        this.file = this.dataStorageFacade.openFile(dataFile.getDataHandle(), false);
        return true;
    }

    /**
     * Returns whether the given file is supported by this data handler, based on whether the file's type is among this
     * handlers's supported types.
     * 
     * @param dataFile the file to check
     * @return whether this handler supports this file
     */
    protected boolean isFileSupported(CaArrayFile dataFile) {
        return getSupportedTypes().contains(dataFile.getFileType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        this.dataStorageFacade.releaseFile(this.caArrayFile.getDataHandle(), false);
        this.caArrayFile = null;
        this.file = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHybridizationNames() {
        return Collections.singletonList(FilenameUtils.getBaseName(this.caArrayFile.getName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSampleNames(String hybridizationName) {
        return Collections.singletonList(hybridizationName);
    }

    /**
     * @return the caArrayFile
     */
    protected CaArrayFile getCaArrayFile() {
        return this.caArrayFile;
    }

    /**
     * @return the file
     */
    protected File getFile() {
        return this.file;
    }
    
    /**
     * {@inheritDoc}
     */
    public MageTabDocumentSet getMageTabDocumentSet() {
        return mageTabDocumentSet;
    }

    /**
     * {@inheritDoc}
     */
    public void setMageTabDocumentSet(MageTabDocumentSet mTabSet) {
        this.mageTabDocumentSet = mTabSet;        
    }

}
