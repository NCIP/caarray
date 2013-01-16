//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Implementation of the FileAccess subsystem.
 */
@Local(FileAccessService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
public class FileAccessServiceBean implements FileAccessService {
    /** Minimum age of a data block that can be removed if it is unreferenced. */
    public static final int MIN_UNREFERENCABLE_DATA_AGE = 300000;

    private static final Logger LOG = Logger.getLogger(FileAccessServiceBean.class);

    private FileDao fileDao;
    private ArrayDao arrayDao;
    private DataStorageFacade dataStorageFacade;
    private FileTypeRegistry typeRegistry;

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile parent = null;
        final CaArrayFile caArrayFile = add(file, parent);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, CaArrayFile parent) {
        LogUtil.logSubsystemEntry(LOG, file, parent);
        String name = (parent == null) ? file.getName() : parent.getName();
        final CaArrayFile caArrayFile = add(file, name, parent);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename) {
        LogUtil.logSubsystemEntry(LOG, file, filename);
        final CaArrayFile caArrayFile = add(file, filename, null);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename, CaArrayFile parent) {
        LogUtil.logSubsystemEntry(LOG, file, filename, parent);
        try {
            final InputStream is = FileUtils.openInputStream(file);
            final CaArrayFile caArrayFile = add(is, filename, parent);
            IOUtils.closeQuietly(is);
            return caArrayFile;
        } catch (final IOException e) {
            LogUtil.logSubsystemExit(LOG);
            throw new FileAccessException("File " + filename + " couldn't be read", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile addChunk(File file, String fileName, Long fileSize, CaArrayFile caArrayFile) {
        if (caArrayFile == null) {
            caArrayFile = getFile(fileName, fileSize);
        }
        readChunk(file, caArrayFile);
        if (caArrayFile.getPartialSize() == fileSize) {
            finalizeUpload(caArrayFile);
        }
        return caArrayFile;
    }

    private CaArrayFile getFile(String fileName, long fileSize) {
        CaArrayFile caArrayFile = createCaArrayFile(fileName, null, FileStatus.UPLOADING);
        caArrayFile.setUncompressedSize(fileSize);
        return caArrayFile;
    }
    
    private void readChunk(File chunk, CaArrayFile caArrayFile) {
        try {
            final InputStream is = FileUtils.openInputStream(chunk);
            final StorageMetadata metadata = this.dataStorageFacade.addFileChunk(caArrayFile.getDataHandle(), is);
            caArrayFile.setDataHandle(metadata.getHandle());
            caArrayFile.setPartialSize(metadata.getPartialSize());
            IOUtils.closeQuietly(is);
        } catch (final IOException e) {
            throw new FileAccessException("File " + caArrayFile.getName() + " couldn't be read", e);
        }
    }
    
    private void finalizeUpload(CaArrayFile caArrayFile) {
        StorageMetadata metadata = dataStorageFacade.finalizeChunkedFile(caArrayFile.getDataHandle());
        caArrayFile.setDataHandle(metadata.getHandle());
        caArrayFile.setCompressedSize(metadata.getCompressedSize());
        caArrayFile.setUncompressedSize(metadata.getUncompressedSize());
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
    }
    
    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename) {
        return add(stream, filename, null);
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename, CaArrayFile parent) {
        final CaArrayFile caArrayFile = createCaArrayFile(filename, parent, FileStatus.UPLOADED);
        LOG.info("Created caArrayFile: " + filename);
        try {
            final StorageMetadata metadata = this.dataStorageFacade.addFile(stream, false);
            caArrayFile.setCompressedSize(metadata.getCompressedSize());
            caArrayFile.setUncompressedSize(metadata.getUncompressedSize());
            caArrayFile.setDataHandle(metadata.getHandle());
        } catch (final DataStoreException e) {
            throw new FileAccessException("Stream " + filename + " couldn't be written", e);
        }
        return caArrayFile;
    }

    private CaArrayFile createCaArrayFile(String filename, CaArrayFile parent, FileStatus status) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setFileStatus(status);
        caArrayFile.setName(filename);
        caArrayFile.setFileType(this.typeRegistry.getTypeFromExtension(filename));

        // set the child file's project to that of the parent. Add the child to the parent.
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }

        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        
        this.fileDao.flushSession(); // bean called in Session FlushMode.COMMIT.  Must flush prior to dao check

        if (caArrayFile.getProject() != null
                && !this.fileDao.getDeletableFiles(caArrayFile.getProject().getId()).contains(caArrayFile)) {
            return false;
        }

        final AbstractArrayData data = this.arrayDao.getArrayData(caArrayFile.getId());
        if (data != null) {
            for (final Hybridization h : data.getHybridizations()) {
                h.removeArrayData(data);
                h.propagateLastModifiedDataTime(new Date());
            }
            this.arrayDao.remove(data);
        }
        
        if (data != null || FileStatus.SUPPLEMENTAL.equals(caArrayFile.getFileStatus())) {
            caArrayFile.getProject().getExperiment().setLastDataModificationDate(new Date());
        }

        for (CaArrayFile childFile : caArrayFile.getChildren()) {
            removeFile(childFile);
        }

        if (caArrayFile.getParent() != null) {
            caArrayFile.getParent().removeChild(caArrayFile);
        }
        removeFile(caArrayFile);
        // the data storage will get cleaned up by the reaper

        LogUtil.logSubsystemExit(LOG);
        return true;
    }

    private void removeFile(CaArrayFile caArrayFile) {
        // A hibernate bug is preventing us from simply calling caArrayFile.getProject().getFiles().remove(caArrayFile)
        // https://hibernate.onjira.com/browse/HHH-3799
        // The workaround is to clear the collection and re-add everything we don't want to delete.
        // This is in reference to issue ARRAY-2349.
        SortedSet<CaArrayFile> files = caArrayFile.getProject().getFiles();
        SortedSet<CaArrayFile> filesToKeep = new TreeSet<CaArrayFile>();
        Long fileId = caArrayFile.getId();
        for (CaArrayFile file : files) {
            if (!file.getId().equals(fileId)) {
                filesToKeep.add(file);
            }
        }
        files.clear();
        files.addAll(filesToKeep);
        this.fileDao.remove(caArrayFile);
    }

    /**
     * {@inheritDoc}
     */
    public void synchronizeDataStorage() {
        final Set<URI> references = getActiveReferences();
        LOG.debug("Currently active references:" + references);
        if (references.isEmpty()) {
            LOG.warn("No active references found.  No files will be deleted.");
        } else {
            this.dataStorageFacade.removeUnreferencedData(references, MIN_UNREFERENCABLE_DATA_AGE);
        }
    }

    private Set<URI> getActiveReferences() {
        final Set<URI> references = Sets.newHashSet(this.fileDao.getAllFileHandles());
        references.addAll(this.arrayDao.getAllParsedDataHandles());
        return references;
    }

    /**
     * {@inheritDoc}
     */
    public void cleanupUnreferencedChildren() {
        LogUtil.logSubsystemEntry(LOG);
        fileDao.cleanupUnreferencedChildren();
        LogUtil.logSubsystemExit(LOG);
    }


    /**
     * @param fileDao the fileDao to set
     */
    @Inject
    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    /**
     * @param arrayDao the arrayDao to set
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * @param dataStorageFacade the dataStorageFacade to set
     */
    @Inject
    public void setDataStorageFacade(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * @param typeRegistry the typeRegistry to set
     */
    @Inject
    public void setTypeRegistry(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }
}
