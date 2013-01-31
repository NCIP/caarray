//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Performs file cleanup operations. Files must be added to the FileCleanupThread
 * Collection in order to be removed when this threads run method is executed.
 * @author Jevon Gill
 */
public final class FileCleanupThread extends TimerTask {

    private static final Logger LOG = Logger.getLogger(FileCleanupThread.class);
    private final Collection<File> filesToRemove = Collections.synchronizedList(new ArrayList<File>());
    private static final FileCleanupThread INSTANCE =
      new FileCleanupThread();

    /**
     * private constructor.
     */
    private FileCleanupThread() {
    }

    /**
     * Gets instance of FileCleanupThread.
     * @return instance of FileCleanupThread
     */
    public static FileCleanupThread getInstance() {
      return INSTANCE;
    }

    /**
     * Files added using this method will be deleted when
     * the FileCleanupThread's run method is executed.
     * @param file the logical file to delete
     */
    public void addFile(File file) {
        if (file != null) {
            filesToRemove.add(file);
            LOG.warn(file.getAbsolutePath()
                    + " added to FileCleanupThread collection");
        }
    }

    /**
     * Attempts to delete all files in the filesToRemove list. If the file is
     * successfully deleted or does not exist it will be removed from the
     * filesToRemove list. If unsuccessful in deleting the file it will remain
     * in the filesToRemove list.
     */
    public void deleteFiles() {
        LOG.info("Removing " + filesToRemove.size() + " files");
        if (!filesToRemove.isEmpty()) {
            ArrayList<File> filesRemoved = new ArrayList<File>();
            for (File file : filesToRemove) {
                if (file.exists() && !FileUtils.deleteQuietly(file)) {
                    LOG.warn("Still unable to delete file: " + file.getAbsolutePath());
                } else {
                    filesRemoved.add(file);
                    LOG.info("Deleted file: " + file.getAbsolutePath());
                }
            }
            filesToRemove.removeAll(filesRemoved);
        }
    }

    /**
     * {@inheritDoc}
     * Attempts to delete all files in the filesToRemove list.
     * If the file is successfully deleted or does not exist it
     * will be removed from the filesToRemove list. If unsuccessful
     * in deleting the file it will remain in the filesToRemove list.
     */
    @Override
    public void run() {
        deleteFiles();
    }
}
