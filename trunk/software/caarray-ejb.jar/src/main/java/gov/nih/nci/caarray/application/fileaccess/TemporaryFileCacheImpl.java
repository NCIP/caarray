//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default implementation of TemporaryFileCache. Stores file data in temporary directory.
 * Note: This class is not thead safe.
 * @see TemporaryFileCacheLocator
 * 
 * @author dkokotov
 */
public final class TemporaryFileCacheImpl implements TemporaryFileCache {
    private static final Logger LOG = Logger.getLogger(TemporaryFileCacheImpl.class);
    private static final String WORKING_DIRECTORY_PROPERTY_KEY = "caarray.working.directory";
    private static final String TEMP_DIR_PROPERTY_KEY = "java.io.tmpdir";

    private final Map<CaArrayFile, File> compressedOpenFiles = new HashMap<CaArrayFile, File>();
    private final Map<CaArrayFile, File> uncompressedOpenFiles = new HashMap<CaArrayFile, File>();
    private final Set<File> createdFiles = new HashSet<File>();
    private File sessionWorkingDirectory;

    TemporaryFileCacheImpl() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public File getFile(CaArrayFile caArrayFile) {
        return getFile(caArrayFile, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public File getFile(CaArrayFile caArrayFile, boolean uncompress) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        File file;
        if (fileAlreadyOpened(caArrayFile, uncompress)) {
            file = getOpenFile(caArrayFile, uncompress);
        } else {
            file = openFile(caArrayFile, uncompress);
        }
        LogUtil.logSubsystemExit(LOG);
        return file;
    }

    private File openFile(CaArrayFile caArrayFile, boolean uncompress) {
        File file = new File(getSessionWorkingDirectory(), caArrayFile.getName());
        try {
            // re-fetch the CaArrayFile instance to ensure that its blob contents are loaded
            HibernateUtil.getCurrentSession().refresh(caArrayFile);

            InputStream inputStream = uncompress ? caArrayFile.readContents() : caArrayFile.readCompressedContents();
            OutputStream outputStream = FileUtils.openOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            caArrayFile.clearAndEvictContents();
        } catch (IOException e) {
            throw new FileAccessException("Couldn't access file contents " + caArrayFile.getName(), e);
        }
        getOpenFileMap(uncompress).put(caArrayFile, file);            
        return file;
    }
    
    private Map<CaArrayFile, File> getOpenFileMap(boolean uncompressed) {
        return uncompressed ? uncompressedOpenFiles : compressedOpenFiles;
    }

    /**
     * Returns a temporary <code>java.io.File</code> with the given name.
     * The client should eventually call delete() to allow the temporary file to be cleaned up.
     * Throws FileAccessException if file could not be created.
     *
     * @param fileName the name of the file to be created
     * @return the <code>java.io.File</code> pointing to the temporary file.
     */
    public File createFile(String fileName) {
        try {
            File file = new File(getSessionWorkingDirectory(), fileName);
            file.createNewFile();
            createdFiles.add(file);
            return file;
        } catch (IOException e) {
            throw new FileAccessException("Could not create temporary file " + fileName, e);
        }
    }

    private File getSessionWorkingDirectory() {
        if (this.sessionWorkingDirectory == null) {
            createSessionWorkingDirectory();
        }
        return this.sessionWorkingDirectory;
    }

    private void createSessionWorkingDirectory() {
        String sessionSubdirectoryName = new UID().toString().replace(':', '_').replace('-', '_');
        this.sessionWorkingDirectory = new File(getWorkingDirectory(), sessionSubdirectoryName);
        if (!this.sessionWorkingDirectory.mkdirs()) {
            LOG.error("Couldn't create directory: " + this.sessionWorkingDirectory.getAbsolutePath());
            throw new IllegalStateException("Couldn't create directory: "
                    + this.sessionWorkingDirectory.getAbsolutePath());
        }
    }

    private File getWorkingDirectory() {
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        String tempDir = config.getString(ConfigParamEnum.STRUTS_MULTIPART_SAVEDIR.name());
        if (StringUtils.isBlank(tempDir)) {
            tempDir = System.getProperty(TEMP_DIR_PROPERTY_KEY);
        }
        String workingDirectoryPath = System.getProperty(WORKING_DIRECTORY_PROPERTY_KEY, tempDir);
        return new File(workingDirectoryPath);
    }

    private boolean fileAlreadyOpened(CaArrayFile caArrayFile, boolean uncompressed) {
        return getOpenFileMap(uncompressed).containsKey(caArrayFile);
    }

    private File getOpenFile(CaArrayFile caArrayFile, boolean uncompressed) {
        return getOpenFileMap(uncompressed).get(caArrayFile);
    }

    /**
     * Closes all temporary files opened in the current session and deletes the temporary directory used to store them.
     * This method should always be called at the conclusion of a session of working with file data.
     */
    public void closeFiles() {
        if (this.sessionWorkingDirectory == null) {
            LOG.debug("closeFiles called for a temp file cache that has already been closed");
            return;
        }
        LOG.debug("Cleaning up files for temp file cache in directory " + sessionWorkingDirectory.getAbsolutePath());

        // copy files to a temp set to allow closeFile() to modify the maps we are iterating through.
        for (CaArrayFile caarrayFile : new HashSet<CaArrayFile>(uncompressedOpenFiles.keySet())) {
            closeFile(caarrayFile, true);
        }
        for (CaArrayFile caarrayFile : new HashSet<CaArrayFile>(compressedOpenFiles.keySet())) {
            closeFile(caarrayFile, false);
        }

        Set<File> createdFilesToClose = new HashSet<File>(createdFiles);
        for (File file : createdFilesToClose) {
            delete(file);
        }
        
        delete(getSessionWorkingDirectory());
        this.sessionWorkingDirectory = null;
    }

    /**
     * {@inheritDoc}
     */
    public void closeFile(CaArrayFile caarrayFile) {
        closeFile(caarrayFile, true);
    }

    /**
     * {@inheritDoc}
     */
    public void closeFile(CaArrayFile caarrayFile, boolean uncompressed) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing session file in directory: " + getSessionWorkingDirectory() + " for caarray file "
                    + caarrayFile.getName() + " holding " + (uncompressed ? "uncompressed" : "compressed") + " data");
        }
        if (this.sessionWorkingDirectory == null) {
            return;
        }
        File file = getOpenFile(caarrayFile, uncompressed);
        if (file != null) {
            delete(file);
        }
        getOpenFileMap(uncompressed).remove(caarrayFile);
    }

    /**
     * Deletes the temporary file.
     * @param file the temporary file to delete.
     */
    public void delete(File file) {
        LOG.debug("Deleting file: " + file.getAbsolutePath());
                
        if (file.exists() && !FileUtils.deleteQuietly(file)) {
            LOG.warn("Couldn't delete file: " + file.getAbsolutePath());
            FileCleanupThread.getInstance().addFile(file);
            file.deleteOnExit();
        } else {
            createdFiles.remove(file);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        closeFiles();
        super.finalize();
    }
}
