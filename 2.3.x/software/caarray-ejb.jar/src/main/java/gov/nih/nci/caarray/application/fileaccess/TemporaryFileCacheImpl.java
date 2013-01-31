//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.util.ConfigurationHelper;
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
 * Default implementation of TemporaryFileCache.
 * @author dkokotov
 */
public final class TemporaryFileCacheImpl implements TemporaryFileCache {
    private static final Logger LOG = Logger.getLogger(TemporaryFileCacheImpl.class);
    private static final String WORKING_DIRECTORY_PROPERTY_KEY = "caarray.working.directory";
    private static final String TEMP_DIR_PROPERTY_KEY = "java.io.tmpdir";

    private final Map<CaArrayFile, File> openFiles = new HashMap<CaArrayFile, File>();
    private File sessionWorkingDirectory;

    TemporaryFileCacheImpl() {
        // nothing to do
    }

    /**
     * Returns a file <code>java.io.File</code> which will hold the uncompressed data for the
     * <code>CaArrayFile</code> object provided. The client should eventually call closeFile() for this
     * <code>CaArrayFile</code> (or closeFiles()) to allow the temporary file to be cleanded up.
     *
     * @param caArrayFile logical file whose contents are needed
     * @return the <code>java.io.File</code> pointing to the temporary file on the filesystem which will
     * hold the uncompressed contents of the given logical file.
     */
    public File getFile(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        File file;
        if (fileAlreadyOpened(caArrayFile)) {
            file = getOpenFile(caArrayFile);
        } else {
            file = openFile(caArrayFile);
        }
        LogUtil.logSubsystemExit(LOG);
        return file;
    }

    private File openFile(CaArrayFile caArrayFile) {
        File file = new File(getSessionWorkingDirectory(), caArrayFile.getName());
        try {
            // re-fetch the CaArrayFile instance to ensure that its blob contents are loaded
            HibernateUtil.getCurrentSession().refresh(caArrayFile);

            InputStream inputStream = caArrayFile.readContents();
            OutputStream outputStream = FileUtils.openOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            caArrayFile.clearAndEvictContents();
        } catch (IOException e) {
            throw new FileAccessException("Couldn't access file contents " + caArrayFile.getName(), e);
        }
        this.openFiles.put(caArrayFile, file);
        return file;
    }

    /**
     * Returns a temporary <code>java.io.File</code> with the given name.
     * The client should eventually call delete() to allow the temporary file to be cleanded up.
     * Throws FileAccessException if file could not be created.
     *
     * @param fileName the name of the file to be created
     * @return the <code>java.io.File</code> pointing to the temporary file.
     */
    public File createFile(String fileName) {
        try {
            File file = new File(getSessionWorkingDirectory(), fileName);
            file.createNewFile();
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
        String sessionSubdirectoryName = new UID().toString().replace(':', '_');
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

    private boolean fileAlreadyOpened(CaArrayFile caArrayFile) {
        return this.openFiles.containsKey(caArrayFile);
    }

    private File getOpenFile(CaArrayFile caArrayFile) {
        return this.openFiles.get(caArrayFile);
    }

    /**
     * Closes all temporary files opened in the current session and deletes the temporary directory used to store them.
     * This method should always be called at the conclusion of a session of working with file data.
     */
    public void closeFiles() {
        if (this.sessionWorkingDirectory == null) {
            return;
        }
        Set<CaArrayFile> filesToClose = new HashSet<CaArrayFile>(this.openFiles.keySet());
        for (CaArrayFile caarrayFile : filesToClose) {
            closeFile(caarrayFile);
        }
        delete(getSessionWorkingDirectory());
        this.sessionWorkingDirectory = null;
    }

    /**
     * Closes the file corresponding to the given logical file opened in the current session. Note that at the end
     * of the session of working with file data, you should still call closeFiles() to perform final cleanup
     * even if all files had been previously closed via calls to this method
     * @param caarrayFile the logical file to close the filesystem file for
     */
    public void closeFile(CaArrayFile caarrayFile) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing session file in directory: " + getSessionWorkingDirectory() + " for caarray file "
                    + caarrayFile.getName());
        }
        if (this.sessionWorkingDirectory == null) {
            return;
        }
        File file = getOpenFile(caarrayFile);
        if (file != null) {
            delete(file);
        }
        this.openFiles.remove(caarrayFile);
    }

    /**
     * Deletes the temporary file.
     * @param file the temporary file to delete.
     */
    public void delete(File file) {
        LOG.debug("Deleting file: " + file.getAbsolutePath());
        if (!file.delete()) {
            LOG.warn("Couldn't delete file: " + file.getAbsolutePath());
            FileCleanupThread.getInstance().addFile(file);
            file.deleteOnExit();
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
