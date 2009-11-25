/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.TransactionTimeout;

/**
 * EJB implementation of the entry point to the FileManagement subsystem. Delegates functionality
 * to other components in the subsystem.
 */
@Local(FileManagementService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
@SuppressWarnings("PMD.TooManyMethods")
public class FileManagementServiceBean implements FileManagementService {

    private static final Logger LOG = Logger.getLogger(FileManagementServiceBean.class);
    private static final int SAVE_ARRAY_DESIGN_TIMEOUT = 1800;

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
    private FileManagementJobSubmitter submitter = new JmsJobSubmitter();

    private void checkForImport(CaArrayFileSet fileSet) {
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isImportable()) {
                throw new IllegalArgumentException("Illegal attempt to import file "
                        + caArrayFile.getName() + " with status " + caArrayFile.getFileStatus());
            }
        }
    }

    private void checkForValidation(CaArrayFileSet fileSet) {
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isValidatable()) {
                throw new IllegalArgumentException("Illegal attempt to validate file "
                        + caArrayFile.getName() + " with status " + caArrayFile.getFileStatus());
            }
        }
    }

    private void checkForFileType(CaArrayFile caArrayFile, FileType ft) {

        if (!ft.equals(caArrayFile.getFileType())) {
            throw new IllegalArgumentException("File "
                    + caArrayFile.getName() + " must be an " + ft.getName() + " file type.");
        }

    }

    private void addFilesToInputSet(CaArrayFileSet addTo, CaArrayFileSet addFrom, FileType ft) {
        for (CaArrayFile caArrayFile : addFrom.getFiles()) {
            if (ft.equals(caArrayFile.getFileType())) {
                addTo.add(caArrayFile);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions dataImportOptions) {
        LogUtil.logSubsystemEntry(LOG, fileSet);
        checkForImport(fileSet);
        clearValidationMessages(fileSet);
        fileSet.updateStatus(FileStatus.IN_QUEUE);
        sendImportJobMessage(targetProject, fileSet, dataImportOptions);
        LogUtil.logSubsystemExit(LOG);
    }

    private void clearValidationMessages(CaArrayFileSet fileSet) {
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            caArrayFile.setValidationResult(null);
            getDaoFactory().getProjectDao().save(caArrayFile);
        }
    }

    private void sendImportJobMessage(Project targetProject, CaArrayFileSet fileSet,
            DataImportOptions dataImportOptions) {
        ProjectFilesImportJob job = new ProjectFilesImportJob(UsernameHolder.getUser(), targetProject, fileSet,
                dataImportOptions);
        getSubmitter().submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    public void validateFiles(Project project, CaArrayFileSet fileSet) {
        checkForValidation(fileSet);
        clearValidationMessages(fileSet);
        fileSet.updateStatus(FileStatus.IN_QUEUE);
        sendValidationJobMessage(project, fileSet);
    }

    private void sendValidationJobMessage(Project project, CaArrayFileSet fileSet) {
        ProjectFilesValidationJob job = new ProjectFilesValidationJob(UsernameHolder.getUser(), project, fileSet);
        getSubmitter().submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionTimeout(SAVE_ARRAY_DESIGN_TIMEOUT)
    public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException,
            IllegalAccessException {
        boolean newArrayDesign = arrayDesign.getId() == null;
        CaArrayFileSet oldFiles = arrayDesign.getDesignFileSet();
        designFiles.updateStatus(FileStatus.VALIDATING);

        arrayDesign.setDesignFileSet(designFiles);
        getArrayDesignService().saveArrayDesign(arrayDesign);
        getArrayDesignService().importDesign(arrayDesign);

        final ArrayDao arrayDao = getDaoFactory().getArrayDao();
        if (FileStatus.VALIDATION_ERRORS.equals(designFiles.getStatus())) {
            if (newArrayDesign) {
                arrayDao.remove(arrayDesign);
                arrayDesign.getDesignFiles().clear();
            } else {
                arrayDesign.setDesignFileSet(oldFiles);
                arrayDao.save(arrayDesign);
            }
            checkDesignFiles(designFiles);
        } else if (oldFiles.getFiles().size() > 0) {
            for (CaArrayFile file : oldFiles.getFiles()) {
                arrayDao.remove(file);
            }
        }
    }

    private void checkDesignFiles(CaArrayFileSet designFiles) throws InvalidDataFileException {
        for (CaArrayFile designFile : designFiles.getFiles()) {
            if (designFile.getValidationResult() != null
                    && !designFile.getValidationResult().getMessages().isEmpty()) {
                throw new InvalidDataFileException(designFile.getValidationResult());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importArrayDesignDetails(ArrayDesign arrayDesign) {
        arrayDesign.getDesignFileSet().updateStatus(FileStatus.IN_QUEUE);
        getDaoFactory().getProjectDao().save(arrayDesign.getDesignFiles());
        ArrayDesignFileImportJob job = new ArrayDesignFileImportJob(UsernameHolder.getUser(), arrayDesign);
        getSubmitter().submitJob(job);
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void addSupplementalFiles(Project targetProject, CaArrayFileSet fileSet) {
        if (targetProject == null) {
            throw new IllegalArgumentException("targetProject was null");
        }
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            caArrayFile.setFileStatus(FileStatus.SUPPLEMENTAL);
            caArrayFile.setProject(targetProject);
            getDaoFactory().getFileDao().save(caArrayFile);
            targetProject.getFiles().add(caArrayFile);
        }
        getDaoFactory().getProjectDao().save(targetProject);
    }

    FileManagementJobSubmitter getSubmitter() {
        return submitter;
    }

    void setSubmitter(FileManagementJobSubmitter submitter) {
        this.submitter = submitter;
    }

    ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public List<String> findIdfRefFileNames(CaArrayFile idfFile, Project project) {
        List<String> filenames = new ArrayList<String>();
        checkForFileType(idfFile, FileType.MAGE_TAB_IDF);
        CaArrayFileSet inputFiles = new CaArrayFileSet(project);
        inputFiles.add(idfFile);
        addFilesToInputSet(inputFiles, project.getFileSet(), FileType.MAGE_TAB_SDRF);
        MageTabTranslator mtt = (MageTabTranslator)
            ServiceLocatorFactory.getLocator().lookup(MageTabTranslator.JNDI_NAME);
        MageTabImporter mti = new MageTabImporter(mtt, getDaoFactory());
        MageTabDocumentSet mTabSet = mti.selectRefFiles(project, inputFiles);
        // we only care about the sdrf docs connected to the idf
        for (SdrfDocument sdrfDoc : mTabSet.getIdfDocuments().iterator().next().getSdrfDocuments()) {
            filenames.addAll(getRefFileNames(sdrfDoc));
        }

        return filenames;
    }

    private List<String> getRefFileNames(SdrfDocument sdrfDoc) {
        List<String> filenames = new ArrayList<String>();
        filenames.add(sdrfDoc.getFile().getName());
        filenames.addAll(sdrfDoc.getReferencedDataMatrixFileNames());
        filenames.addAll(sdrfDoc.getReferencedDerivedFileNames());
        filenames.addAll(sdrfDoc.getReferencedRawFileNames());

        return filenames;
    }

 }
