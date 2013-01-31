//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
