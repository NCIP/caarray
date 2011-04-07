/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or 
 * Your) shall mean a person or an entity, and all other entities that control, 
 * are controlled by, or are under common control with the entity. Control for 
 * purposes of this definition means (i) the direct or indirect power to cause 
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares, 
 * or (iii) beneficial ownership of such entity. 
 *
 * This License is granted provided that You agree to the conditions described 
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up, 
 * no-charge, irrevocable, transferable and royalty-free right and license in 
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-ejb-jar Software and any 
 * modifications and derivative works thereof; and (iii) sublicense the 
 * foregoing rights set out in (i) and (ii) to third parties, including the 
 * right to license such rights to further third parties. For sake of clarity, 
 * and not by way of limitation, NCI shall have no right of accounting or right 
 * of payment from You or Your sub-licensees for the rights granted under this 
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the 
 * above copyright notice, this list of conditions and the disclaimer and 
 * limitation of liability of Article 6, below. Your redistributions in object 
 * code form must reproduce the above copyright notice, this list of conditions 
 * and the disclaimer of Article 6 in the documentation and/or other materials 
 * provided with the distribution, if any. 
 *
 * Your end-user documentation included with the redistribution, if any, must 
 * include the following acknowledgment: This product includes software 
 * developed by 5AM and the National Cancer Institute. If You do not include 
 * such end-user documentation, You shall include this acknowledgment in the 
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM" 
 * to endorse or promote products derived from this Software. This License does 
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the 
 * terms of this License. 
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this 
 * Software into Your proprietary programs and into any third party proprietary 
 * programs. However, if You incorporate the Software into third party 
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software 
 * into such third party proprietary programs and for informing Your 
 * sub-licensees, including without limitation Your end-users, of their 
 * obligation to secure any required permissions from such third parties before 
 * incorporating the Software into such third party proprietary software 
 * programs. In the event that You fail to obtain such permissions, You agree 
 * to indemnify NCI for any claims against NCI by such third parties, except to 
 * the extent prohibited by law, resulting from Your failure to obtain such 
 * permissions. 
 *
 * For sake of clarity, and not by way of limitation, You may add Your own 
 * copyright statement to Your modifications and to the derivative works, and 
 * You may provide additional or different license terms and conditions in Your 
 * sublicenses of modifications of the Software, or any derivative works of the 
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, 
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO 
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR 
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
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

import com.google.inject.Inject;

/**
 * Utility class for handling a set of uploaded files.
 * 
 * TODO: should some of these be moved into a service and be part of a transaction?
 * 
 * @author kokotovd
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class FileUploadUtils {
    private static final String UNPACKING_ERROR_KEY = "errors.unpackingZip";
    private static final String UNPACKING_ERROR_MESSAGE = "Unable to unpack file";

    private final DataStorageFacade dataStorageFacade;

    @Inject
    public FileUploadUtils(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
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
    public FileProcessingResult uploadFiles(Project project, List<File> files, List<String> fileNames,
            List<String> fileNamesToUnpack) throws InvalidFileException {
        // create set of existing files

        final FileProcessingResult result = new FileProcessingResult();
        int index = 0;

        for (final File currentFile : files) {
            final String fileName = fileNames.get(index);
            if (fileNamesToUnpack != null && fileNamesToUnpack.contains(fileName)) {
                try {
                    final InputStream is = FileUtils.openInputStream(currentFile);
                    final FileProcessingResult fileResult = unpackFile(project, is);
                    IOUtils.closeQuietly(is);
                    result.incrementCount(fileResult.getCount());
                    result.addConflictingFiles(fileResult.getConflictingFiles());
                } catch (final IOException e) {
                    throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
                } catch (final InvalidFileException e) {
                    result.incrementCount(e.getResult().getCount());
                    result.addConflictingFiles(e.getResult().getConflictingFiles());
                    throw new InvalidFileException(fileName, UNPACKING_ERROR_KEY, result, UNPACKING_ERROR_MESSAGE, e);
                }
            } else if (!StringUtils.isBlank(fileName)) {
                try {
                    final boolean added = addUploadedFile(project, currentFile, fileName);
                    if (added) {
                        result.incrementCount();
                    } else {
                        result.addConflictingFile(fileName);
                    }
                } catch (final Exception e) {
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
    public FileProcessingResult unpackFiles(Project project, List<CaArrayFile> caFiles) throws InvalidFileException {
        final FileProcessingResult result = new FileProcessingResult();
        for (final CaArrayFile caArrayFile : caFiles) {
            try {
                final InputStream is = this.dataStorageFacade.openInputStream(caArrayFile.getDataHandle(), false);
                final FileProcessingResult fileResult = unpackFile(project, is);
                IOUtils.closeQuietly(is);
                result.incrementCount(fileResult.getCount());
                result.addConflictingFiles(fileResult.getConflictingFiles());
            } catch (final IOException e) {
                throw new InvalidFileException(caArrayFile.getName(), UNPACKING_ERROR_KEY, result,
                        UNPACKING_ERROR_MESSAGE, e);
            } catch (final InvalidFileException e) {
                result.incrementCount(e.getResult().getCount());
                result.addConflictingFiles(e.getResult().getConflictingFiles());
                throw new InvalidFileException(caArrayFile.getName(), UNPACKING_ERROR_KEY, result,
                        UNPACKING_ERROR_MESSAGE, e);
            }

            getFileAccessService().remove(caArrayFile);
        }

        return result;
    }

    /**
     * Unpack the given file, which must be a ZIP file, and add its files individually to the given project and save
     * them in the data store. If any file within the ZIP file has the same name as a file already belonging to the
     * project, it will not be added to the project. If any file within one of the ZIP files is itself a ZIP file, it
     * will be added to the project, and will not recursively unpacked. The ZIP file must not contain directories. If it
     * does, an InvalidFileException will be thrown.
     * 
     * @param project the project to which the unpacked files should be added.
     * @param file the file to unpack. This is expected to be a ZIP file that belongs to the project.
     * @return a result object with information about number of files actually added to the project and conflicting
     *         files
     * @throws InvalidFileException if any file within the ZIP file is within a subdirectory, or if there is an error
     *             adding any file to the project.
     */
    private FileProcessingResult unpackFile(Project project, InputStream fis) throws IOException, InvalidFileException {
        final ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry entry = zis.getNextEntry();
        final ProjectManagementService pms = getProjectManagementService();
        final Set<String> existingFileNames = new HashSet<String>(project.getFileNames());
        final FileProcessingResult result = new FileProcessingResult();

        while (entry != null && zis.available() > 0) {
            final String entryName = entry.getName();
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
                } catch (final Exception e) {
                    throw new InvalidFileException(entryName, "errors.addingFile", result, "Error adding file", e);
                }
            }
            entry = zis.getNextEntry();
        }
        zis.close();

        return result;
    }

    private static boolean addUploadedFile(Project project, File file, String fileName)
            throws ProposalWorkflowException, InconsistentProjectStateException {
        boolean added = false;
        final ProjectManagementService pms = getProjectManagementService();
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
