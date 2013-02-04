//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.domain.ConfigParamEnum;

import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default implementation of TemporaryFileCache. Stores file data in temporary directory. Note: This class is not thread
 * safe.
 * 
 * @see TemporaryFileCacheLocator
 * 
 * @author dkokotov
 */
public final class TemporaryFileCacheImpl implements TemporaryFileCache {
    private static final Logger LOG = Logger.getLogger(TemporaryFileCacheImpl.class);
    private static final String WORKING_DIRECTORY_PROPERTY_KEY = "caarray.working.directory";
    private static final String TEMP_DIR_PROPERTY_KEY = "java.io.tmpdir";

    private final Map<String, File> createdFiles = new HashMap<String, File>();
    private File sessionWorkingDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    public File createFile(String fileName) {
        try {
            final File file = new File(getSessionWorkingDirectory(), fileName);
            file.createNewFile();
            this.createdFiles.put(fileName, file);
            return file;
        } catch (final IOException e) {
            throw new FileAccessException("Could not create temporary file " + fileName, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fileExists(String fileName) {
        return this.createdFiles.containsKey(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile(String fileName) {
        return this.createdFiles.get(fileName);
    }

    private File getSessionWorkingDirectory() {
        if (this.sessionWorkingDirectory == null) {
            createSessionWorkingDirectory();
        }
        return this.sessionWorkingDirectory;
    }

    private void createSessionWorkingDirectory() {
        final String sessionSubdirectoryName = new UID().toString().replace(':', '_').replace('-', '_');
        this.sessionWorkingDirectory = new File(getWorkingDirectory(), sessionSubdirectoryName);
        if (!this.sessionWorkingDirectory.mkdirs()) {
            LOG.error("Couldn't create directory: " + this.sessionWorkingDirectory.getAbsolutePath());
            throw new IllegalStateException("Couldn't create directory: "
                    + this.sessionWorkingDirectory.getAbsolutePath());
        }
    }

    private File getWorkingDirectory() {
        final DataConfiguration config = ConfigurationHelper.getConfiguration();
        String tempDir = config.getString(ConfigParamEnum.STRUTS_MULTIPART_SAVEDIR.name());
        if (StringUtils.isBlank(tempDir)) {
            tempDir = System.getProperty(TEMP_DIR_PROPERTY_KEY);
        }
        final String workingDirectoryPath = System.getProperty(WORKING_DIRECTORY_PROPERTY_KEY, tempDir);
        return new File(workingDirectoryPath);
    }

    /**
     * Closes all temporary files opened in the current session and deletes the temporary directory used to store them.
     * This method should always be called at the conclusion of a session of working with file data.
     */
    @Override
    public void deleteFiles() {
        if (this.sessionWorkingDirectory == null) {
            LOG.debug("closeFiles called for a temp file cache that has already been closed");
            return;
        }
        LOG.debug("Cleaning up files for temp file cache in directory "
                + this.sessionWorkingDirectory.getAbsolutePath());

        final Set<String> createdFilesToClose = new HashSet<String>(this.createdFiles.keySet());
        for (final String fileName : createdFilesToClose) {
            delete(fileName);
        }

        FileUtils.deleteQuietly(getSessionWorkingDirectory());
        this.sessionWorkingDirectory = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String fileName) {
        LOG.debug("Deleting temp file: " + fileName);
        final File file = this.createdFiles.get(fileName);
        if (file == null) {
            // no such file, so ignore
            return;
        }

        if (file.exists() && !FileUtils.deleteQuietly(file)) {
            LOG.warn("Couldn't delete file: " + file.getAbsolutePath());
            FileCleanupThread.getInstance().addFile(file);
            file.deleteOnExit();
        } else {
            this.createdFiles.remove(fileName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        deleteFiles();
        super.finalize();
    }
}
