//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.validator.InvalidStateException;

import com.google.inject.Inject;

/**
 * Utility class for handling a set of uploaded files.
 */
public class FileUploadUtils {
    private static final Logger LOG = Logger.getLogger(FileUploadUtils.class);

    static final String UNPACKING_ERROR_KEY = "errors.unpackingZip";
    static final String UNPACKING_ERROR_MESSAGE = "Unable to unpack file";
    static final String INVALID_ZIP_MESSAGE = "Provided file is not recognized as a ZIP file";
    static final String ADDING_FILE_ERROR_KEY = "errors.addingFile";
    static final String ADDING_FILE_ERROR_MESSAGE = "Unable to add file to persistent store";
    static final String DIRECTORIES_NOT_SUPPORTED_KEY = "errors.directoriesNotSupported";
    static final String DIRECTORIES_NOT_SUPPORTED_MESSAGE = "Directories not supported";

    static final String VALID_ZIP_EXTENSION = ".zip";

    private final DataStorageFacade dataStorageFacade;
    private final FileProcessingResult result = new FileProcessingResult();

    /**
     * Constructor.
     *
     * @param dataStorageFacade data storage facade to use for looking up file data.
     */
    @Inject
    public FileUploadUtils(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * Handle uploaded files.
     *
     * @param project the project the files are being uploaded in to.
     * @param files the files being uploaded.
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException on error
     */
    public FileProcessingResult uploadFiles(Project project, List<FileWrapper> files) throws InvalidFileException {
        for (final FileWrapper currentFile : files) {
            final String fileName = currentFile.getFileName();

            if (StringUtils.isNotBlank(fileName)) {
                if (currentFile.isPartial()) {
                    addFileChunk(project, currentFile);
                } else if (currentFile.isCompressed()) {
                    unpackUploadedFile(project, currentFile.getFile(), fileName);
                } else {
                    addUploadedFile(project, currentFile.getFile(), fileName);
                }
            }
        }
        return result;
    }
    
    /**
     * Uploads a file chunk to a project.
     * @param project the project the file is being uploaded in to.
     * @param wrapper FileWrapper for the chunk being uploaded.
     */
    private void addFileChunk(Project project, FileWrapper wrapper) throws InvalidFileException {
        File file = wrapper.getFile();
        String fileName = wrapper.getFileName();
        try {
            if (!checkDuplicateFilename(project, fileName)) {
                CaArrayFile caArrayFile = getProjectManagementService().addFileChunk(project, file, fileName,
                        wrapper.getTotalFileSize());
                if (caArrayFile.getFileStatus().equals(FileStatus.UPLOADED)) {
                    handleLastChunk(project, caArrayFile, fileName, wrapper.isCompressed());
                } else {
                    result.setPartialUpload(true);
                }
            }

        } catch (final Exception e) {
            if (e.getCause() instanceof InvalidStateException) {
                result.addConflictingFile(fileName);
            } else {
                throw new InvalidFileException(fileName, "errors.addingFile", result,
                        "Unable to add file tp persistent store", e);
            }
        }

    }
    
    /**
     * Additional handling if this is the last chunk of a file being uploaded.
     * 
     * @param project the project the file is being uploaded in to.
     * @param caArrayFile the uploaded file
     * @param fileName the file name
     * @param compressed set to true if this file should be unpacked.
     * @throws InvalidFileException
     */
    private void handleLastChunk(Project project, CaArrayFile caArrayFile, String fileName, boolean compressed)
            throws InvalidFileException {
        if (compressed) {
            LOG.info("opening stream for " + caArrayFile.getDataHandle());
            final InputStream is = this.dataStorageFacade.openInputStream(caArrayFile.getDataHandle(), false);
            unpackFile(project, caArrayFile.getName(), is);
            getFileAccessService().remove(caArrayFile);
        } else {
            result.addSuccessfulFile(fileName);
        }
        
    }
    
    private boolean checkDuplicateFilename(Project project, String fileName) {
        for (CaArrayFile projectFile : project.getFiles()) {
            if (projectFile.getName().equals(fileName)
                    && !projectFile.getFileStatus().equals(FileStatus.UPLOADING)) {
                result.addConflictingFile(fileName);
                return true;
            }
        }
        return false;
    }
    
    private void unpackUploadedFile(Project project, File currentFile, String fileName) throws InvalidFileException {
        checkIsZipFile(fileName);
        try {
            final InputStream input = FileUtils.openInputStream(currentFile);
            unpackFile(project, fileName, input);
        } catch (final IOException e) {
            throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
        }
    }

    private void checkIsZipFile(String fileName) throws InvalidFileException {
        if (!fileName.endsWith(VALID_ZIP_EXTENSION)) {
            throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, INVALID_ZIP_MESSAGE);
        }
    }

    /**
     * Unpack the given file, which must be a ZIP file, and add its files individually to the given project and save
     * them in the data store. If any file within the ZIP file has the same name as a file already belonging to the
     * project, it will not be added to the project. If any file within one of the ZIP files is itself a ZIP file, it
     * will be added to the project, and will not recursively unpacked. The ZIP file must not contain directories. If it
     * does, an InvalidFileException will be thrown.
     * @param project the project to which the unpacked files should be added.
     * @param fileName the name of the file being unpacked.
     * @param inputStream the InputStream object which points to the File data
     *
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException if any file within the ZIP file is within a subdirectory, or if there is an error
     *             adding any file to the project.
     */
    private void unpackFile(Project project, String fileName, InputStream inputStream) throws InvalidFileException {
        ZipInputStream zipStream = null;
        try {
            zipStream = new ZipInputStream(inputStream);
            ZipEntry entry = zipStream.getNextEntry();
            final Set<String> existingFileNames = new HashSet<String>(project.getFileNames());

            while (entry != null) {
                String entryName = entry.getName();
                checkForDirectory(entry);
                if (!checkAlreadyAdded(existingFileNames, entryName)) {
                    addEntryToProject(project, zipStream, entryName);
                    existingFileNames.add(entryName);
                }
                entry = zipStream.getNextEntry();
            }
        } catch (final IOException e) {
            throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
        } catch (final InvalidFileException e) {
            throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
        } finally {
            IOUtils.closeQuietly(zipStream);
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void checkForDirectory(ZipEntry entry) throws InvalidFileException {
        if (entry.isDirectory()) {
            throw new InvalidFileException(entry.getName(), DIRECTORIES_NOT_SUPPORTED_KEY, result,
                    DIRECTORIES_NOT_SUPPORTED_MESSAGE);
        }
    }

    private void addEntryToProject(Project project, ZipInputStream input, String entryName)
            throws InvalidFileException {
        LOG.info("Adding entry  " + entryName + " to project " + project.getId());
        try {
            final ProjectManagementService pms = getProjectManagementService();
            pms.addFile(project, input, entryName);
            result.addSuccessfulFile(entryName);
        } catch (final Exception e) {
            LOG.error("Error adding file", e);
            if (e.getCause() instanceof InvalidStateException) {
                result.addConflictingFile(entryName);
            } else {
                throw new InvalidFileException(entryName, ADDING_FILE_ERROR_KEY, result, ADDING_FILE_ERROR_MESSAGE, e);
            }
        }
    }

    private void addUploadedFile(Project project, File file, String fileName) throws InvalidFileException {
        try {
            final ProjectManagementService pms = getProjectManagementService();
            if (!checkAlreadyAdded(project.getFileNames(), fileName)) {
                pms.addFile(project, file, fileName);
                result.addSuccessfulFile(fileName);
            }
        } catch (Exception e) {
            if (e.getCause() instanceof InvalidStateException) {
                result.addConflictingFile(fileName);
            } else {
                throw new InvalidFileException(fileName, ADDING_FILE_ERROR_KEY, result, ADDING_FILE_ERROR_MESSAGE, e);
            }
        }
    }

    private boolean checkAlreadyAdded(Set<String> existingFileNames, String fileName) {
        if (existingFileNames.contains(fileName) || result.getSuccessfullyProcessedFiles().contains(fileName)) {
            result.addConflictingFile(fileName);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Unpack the given files (which must be ZIP files) belonging to given project. The individual files inside each ZIP
     * file will be added to the project and saved in the persistent store, and the ZIP files themselves will be removed
     * from the project and the persistent store. If any file within a ZIP file has the same name as a file already
     * belonging to the project, it will not be added to the project. If any file within one of the ZIP files is itself
     * a ZIP file, it will be added to the project, and will not recursively unpacked. The ZIP files must not contain
     * directories.
     *
     * @param project the project to which the files belong
     * @param caFiles the list of ZIP files, which must belong to the given project.
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException if any of the ZIP files is not a valid ZIP file, has directories, or had any files
     *             that could not be added to the project
     */
    public FileProcessingResult unpackFiles(Project project, List<CaArrayFile> caFiles) throws InvalidFileException {
        for (final CaArrayFile caArrayFile : caFiles) {
            final InputStream is = this.dataStorageFacade.openInputStream(caArrayFile.getDataHandle(), false);
            unpackFile(project, caArrayFile.getName(), is);
            getFileAccessService().remove(caArrayFile);
        }

        return result;
    }

    private ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) ServiceLocatorFactory.getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    private FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
    }
}
