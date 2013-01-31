//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for handling a set of uploaded files. 
 * @author kokotovd
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class FileUploadUtils {
    private static final String UNPACKING_ERROR_KEY = "errors.unpackingZip";
    private static final String UNPACKING_ERROR_MESSAGE = "Unable to unpack file";
    
    private FileUploadUtils() {
        // UTILITY CLASS 
    }

    /**
     * Handle uploaded files.
     * 
     * @param project the project the files are being uploaded in to.
     * @param files the files being uploaded.
     * @param fileNames the file names to use.
     * @param fileNamesToUnpack the file names to unpack.
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException on error
     */
    public static FileProcessingResult uploadFiles(Project project, List<File> files, List<String> fileNames,
            List<String> fileNamesToUnpack) throws InvalidFileException {
        // create set of existing files

        FileProcessingResult result = new FileProcessingResult();
        int index = 0;
        
        for (File currentFile : files) {
            String fileName = fileNames.get(index);
            if (fileNamesToUnpack != null && fileNamesToUnpack.contains(fileName)) {
                try {
                    FileProcessingResult fileResult = unpackFile(project, currentFile);
                    result.incrementCount(fileResult.getCount());
                    result.addConflictingFiles(fileResult.getConflictingFiles());
                } catch (IOException e) {
                    throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
                } catch (InvalidFileException e) {
                    result.incrementCount(e.getResult().getCount());
                    result.addConflictingFiles(e.getResult().getConflictingFiles());                
                    throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
                }
            } else if (!StringUtils.isBlank(fileName)) {
                try {
                    boolean added = addUploadedFile(project, currentFile, fileName);
                    if (added) {
                        result.incrementCount();
                    } else {
                        result.addConflictingFile(fileName);
                    }                    
                } catch (Exception e) {
                    throw new InvalidFileException(fileName, "errors.addingFile", result,
                            "Unable to add file tp persistent store", e);
                }
            }
            index++;
        }
        
        return result;
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
    public static FileProcessingResult unpackFiles(Project project, List<CaArrayFile> caFiles)
            throws InvalidFileException {

        FileProcessingResult result = new FileProcessingResult();
        for (CaArrayFile caArrayFile : caFiles) {
            project.getFiles().remove(caArrayFile);

            File f = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
            try {
                FileProcessingResult fileResult = unpackFile(project, f);
                result.incrementCount(fileResult.getCount());
                result.addConflictingFiles(fileResult.getConflictingFiles());
            } catch (IOException e) {
                throw new InvalidFileException(f.getName(), UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
            } catch (InvalidFileException e) {
                result.incrementCount(e.getResult().getCount());
                result.addConflictingFiles(e.getResult().getConflictingFiles());                
                throw new InvalidFileException(f.getName(), UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
            } finally {
                TemporaryFileCacheLocator.getTemporaryFileCache().closeFile(caArrayFile);                
            }

            if (caArrayFile.isDeletable()) {
                getFileAccessService().remove(caArrayFile);
            }                
        }

        return result;
    }


    /**
     * Unpack the given file, which must be a ZIP file, and add its files individually to the given project and save
     * them in the data store.
     * If any file within the ZIP file has the same name as a file already belonging to the project, it will not
     * be added to the project. 
     * If any file within one of the ZIP files is itself a ZIP file, it will be added to the project, and will
     * not recursively unpacked.
     * The ZIP file must not contain directories. If it does, an InvalidFileException will be thrown.
     * 
     * @param project the project to which the unpacked files should be added.
     * @param file the file to unpack. This is expected to be a ZIP file that belongs to the project.
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException if any file within the ZIP file is within a subdirectory, or if there is an error
     * adding any file to the project.
     */
    private static FileProcessingResult unpackFile(Project project, File file) 
            throws IOException, InvalidFileException {
        FileInputStream fis = new FileInputStream(file);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry entry = zis.getNextEntry();
        ProjectManagementService pms = getProjectManagementService();
        Set<String> existingFileNames = new HashSet<String>(project.getFileNames());
        FileProcessingResult result = new FileProcessingResult();
        
        while (entry != null && zis.available() > 0) {
             String entryName = entry.getName();
             if (entryName.indexOf('/') >= 0 || entryName.indexOf('\\') >= 0) {
                 throw new InvalidFileException(entryName, "errors.directoriesNotSupported", result,
                        "Directories not supported");
             }
             if (existingFileNames.contains(entryName)) {
                 result.addConflictingFile(entryName);
             } else {
                 try {
                     pms.addFile(project, zis, entryName);
                     existingFileNames.add(entryName);
                     result.incrementCount();
                 } catch (Exception e) {
                     throw new InvalidFileException(entryName, "errors.addingFile", result, "Error adding file", e);
                 }                 
             }
             entry = zis.getNextEntry();
        }
        zis.close();
        fis.close();
        
        return result;
    }
    
    private static boolean addUploadedFile(Project project, File file, String fileName)
            throws ProposalWorkflowException, InconsistentProjectStateException {
        boolean added = false;
        ProjectManagementService pms = getProjectManagementService();
        if (!project.getFileNames().contains(fileName)) {
            pms.addFile(project, file, fileName);
            added = true;
        }
        return added;
    }

    private static ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) ServiceLocatorFactory.getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    private static FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
    }
}
