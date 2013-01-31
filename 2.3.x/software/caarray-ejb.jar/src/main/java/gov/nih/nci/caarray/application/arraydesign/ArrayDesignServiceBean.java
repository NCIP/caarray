//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.InvalidNumberOfArgsException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.TransactionTimeout;

/**
 * Implementation entry point for the ArrayDesign subsystem.
 */
@Local(ArrayDesignService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@TransactionTimeout(ArrayDesignServiceBean.TIMEOUT_SECONDS)
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ArrayDesignServiceBean implements ArrayDesignService {

    private static final Logger LOG = Logger.getLogger(ArrayDesignServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;
    static final int DELETE_ARRAY_DELETE_TIMEOUT_SECONDS = 7200;

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(CaArrayFile designFile) {
        LogUtil.logSubsystemEntry(LOG, designFile);
        Set<CaArrayFile> files = new HashSet<CaArrayFile>();
        files.add(designFile);
        ValidationResult result = validateDesignFiles(files);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(Set<CaArrayFile> designFiles) {
        LogUtil.logSubsystemEntry(LOG, designFiles);
        ValidationResult result = validateDesignFiles(designFiles);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(ArrayDesign design) {
        LogUtil.logSubsystemEntry(LOG, design);
        ValidationResult result = validateDesignFiles(design.getDesignFiles());
        FileValidationResult duplicateResult = validateDuplicate(design);
        result.addFile(duplicateResult.getFile(), duplicateResult);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    private ValidationResult validateDesignFiles(Set<CaArrayFile> designFiles) {
        ValidationResult result = new ValidationResult();
        AbstractArrayDesignHandler handler = null;
        for (CaArrayFile designFile : designFiles) {
            if (designFile.getType() == null) {
                result.addMessage(getFile(designFile), Type.ERROR,
                        "Array design file type was unrecognized, please select a file format");
            } else if (!designFile.getFileType().isArrayDesign()) {
                result.addMessage(getFile(designFile), Type.ERROR, "File type " + designFile.getFileType().getName()
                        + " is not an array design type.");
            }
        }
        if (result.isValid()) {
            handler = getHandler(designFiles);
            result = handler.validate();
        }

        for (CaArrayFile designFile : designFiles) {
            FileValidationResult fileResult = result.getFileValidationResult(getFile(designFile));
            designFile.setValidationResult(fileResult);
            if (result.isValid()) {
                designFile.setFileStatus(handler.getValidatedStatus());
            } else if (handler != null) {
                designFile.setFileStatus(handler.getValidationErrorStatus());
            } else {
                designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            }
            getArrayDao().save(designFile);
        }
        getArrayDao().flushSession();
        return result;
    }

    private FileValidationResult validateDuplicate(ArrayDesign arrayDesign) {
        CaArrayFile designFile = arrayDesign.getDesignFiles().iterator().next();
        FileValidationResult result = designFile.getValidationResult();
        if (result == null) {
            result = new FileValidationResult(null);
        }
        if (isDuplicate(arrayDesign))   {
            result.addMessage(Type.ERROR, "An array design already exists with the name " + arrayDesign.getName()
                    + " and provider " + arrayDesign.getProvider().getName());
            designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            designFile.setValidationResult(result);
            getArrayDao().save(designFile);
            getArrayDao().flushSession();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDuplicate(ArrayDesign arrayDesign) {
        List<ArrayDesign> providerDesigns =
            getDaoFactory().getArrayDao().getArrayDesigns(arrayDesign.getProvider(), null, false);
        for (ArrayDesign providerDesign : providerDesigns) {
            if (!arrayDesign.equals(providerDesign)
                    && arrayDesign.getName().equalsIgnoreCase(providerDesign.getName())) {
                return true;
            }
        }
        return false;
    }

    private File getFile(CaArrayFile file) {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(file);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importDesign(ArrayDesign arrayDesign) {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        if (arrayDesign.getDesignFiles().isEmpty()) {
            LOG.warn("importDesign called, but no design files provided. No updates made.");
            return;
        }
        // Temporarily change the LSID so that file validation does not find this array design as a duplicate.
        String lsid = arrayDesign.getLsid();
        String tmpLsid = lsid + "tmp";
        arrayDesign.setLsidForEntity(tmpLsid);
        getArrayDao().save(arrayDesign);
        getArrayDao().flushSession();
        if (validateDesignFiles(arrayDesign.getDesignFiles()).isValid()) {
            AbstractArrayDesignHandler handler = getHandler(arrayDesign.getDesignFiles());
            handler.load(arrayDesign);
            if (validateDuplicate(arrayDesign).isValid()) {
                getArrayDao().save(arrayDesign);
            }
        }
        if (tmpLsid.equals(arrayDesign.getLsid())) {
            arrayDesign.setLsidForEntity(lsid);
            getArrayDao().save(arrayDesign);
        }
        LogUtil.logSubsystemExit(LOG);
    }
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importDesignDetails(ArrayDesign arrayDesign) {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        if (arrayDesign.getDesignFiles().isEmpty()) {
            LOG.warn("importDesignDetails called, but no design file(s) provided. No updates made.");
            return;
        }
        for (CaArrayFile designFile : arrayDesign.getDesignFiles()) {
            if (!designFile.getValidationResult().isValid()) {
                throw new IllegalArgumentException("The array design provided for import is not valid");
            }
        }

        ArrayDesignDetails designDetails = arrayDesign.getDesignDetails();
        if (designDetails != null) {
            getArrayDao().deleteArrayDesignDetails(arrayDesign);
            getArrayDao().flushSession();
        }

        doImportDesignDetails(arrayDesign);
        LogUtil.logSubsystemExit(LOG);
    }

    @SuppressWarnings("PMD.AvoidReassigningParameters")
    private void doImportDesignDetails(ArrayDesign arrayDesign) {
        AbstractArrayDesignHandler handler = getHandler(arrayDesign.getDesignFiles());
        handler.createDesignDetails(arrayDesign);
        // the handler cleared the session, so we need to merge before we update the status
        // See hibernate bug http://opensource.atlassian.com/projects/hibernate/browse/HHH-511
        // When we upgrade to hibernate 3.2.4+, we can remove the call to merge.
        arrayDesign = (ArrayDesign) getArrayDao().mergeObject(arrayDesign);
        arrayDesign.getDesignFileSet().updateStatus(FileStatus.IMPORTED);
        getArrayDao().save(arrayDesign);
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private AbstractArrayDesignHandler getHandler(Set<CaArrayFile> designFiles) {
        CaArrayFile designFile = designFiles.iterator().next();
        FileType type = designFile.getFileType();
        if (type == null) {
            throw new IllegalArgumentException("FileType was null");
        } else if (FileType.AFFYMETRIX_CDF.equals(type)
                && throwInvalidNumOfArgsExc(FileType.AFFYMETRIX_CDF.toString(), designFiles.size(), 1)) {
            return new AffymetrixCdfHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.AFFYMETRIX_CLF.equals(type) || FileType.AFFYMETRIX_PGF.equals(type)
                && throwInvalidNumOfArgsExc(FileType.AFFYMETRIX_CLF.toString(), designFiles.size(), 2)) {
            return new AffymetrixPgfClfDesignHandler(getVocabularyService(), daoFactory, designFiles);
        } else if (FileType.ILLUMINA_DESIGN_CSV.equals(type)
                && throwInvalidNumOfArgsExc(FileType.ILLUMINA_DESIGN_CSV.toString(), designFiles.size(), 1)) {
            return new IlluminaCsvDesignHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.GENEPIX_GAL.equals(type)
                && throwInvalidNumOfArgsExc(FileType.GENEPIX_GAL.toString(), designFiles.size(), 1)) {
            return new GenepixGalDesignHandler(getVocabularyService(), daoFactory, designFile);
        } else if ((FileType.AGILENT_CSV.equals(type) || FileType.AGILENT_XML.equals(type))
                && throwInvalidNumOfArgsExc(FileType.AGILENT_CSV.toString(), designFiles.size(), 1)) {
            return new AgilentUnsupportedDesignHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.IMAGENE_TPL.equals(type)
                && throwInvalidNumOfArgsExc(FileType.IMAGENE_TPL.toString(), designFiles.size(), 1)) {
            return new ImageneTplHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.UCSF_SPOT_SPT.equals(type)
            && throwInvalidNumOfArgsExc(FileType.IMAGENE_TPL.toString(), designFiles.size(), 1)) {
            return new UcsfSpotSptHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.NIMBLEGEN_NDF.equals(type)) {
            return new NimbleGenNdfHandler(getVocabularyService(), daoFactory, designFiles);
        } else if (FileType.UCSF_SPOT_SPT.equals(type)
                && throwInvalidNumOfArgsExc(FileType.UCSF_SPOT_SPT.toString(), designFiles.size(), 1)) {
                return new UcsfSpotSptHandler(getVocabularyService(), daoFactory, designFile);
        } else if (FileType.MAGE_TAB_ADF.equals(type)
            && throwInvalidNumOfArgsExc(FileType.MAGE_TAB_ADF.toString(), designFiles.size(), 1)) {
            return new MageTabAdfHandler(getVocabularyService(), daoFactory, designFile);
        } else {
            throw new InvalidNumberOfArgsException(InvalidNumberOfArgsException.UNSUPPORTED_ARRAY_DESIGN);
        }
    }

    private boolean throwInvalidNumOfArgsExc(String type, int numberOfDesigns, int numberAllowed) {
        if (numberOfDesigns != numberAllowed) {
            InvalidNumberOfArgsException inv =
                new InvalidNumberOfArgsException(InvalidNumberOfArgsException.NUMBER_OF_ARGS);
            inv.addArg(type);
            throw inv;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getAllProviders() {
        LogUtil.logSubsystemEntry(LOG);
        List<Organization> orgs = getDaoFactory().getContactDao().getAllProviders();
        LogUtil.logSubsystemExit(LOG);
        return orgs;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getArrayDesignProviders() {
        LogUtil.logSubsystemEntry(LOG);
        List<Organization> orgs = getArrayDao().getArrayDesignProviders();
        LogUtil.logSubsystemExit(LOG);
        return orgs;
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes) {
        LogUtil.logSubsystemEntry(LOG);
        List<ArrayDesign> designs = getArrayDao().getArrayDesigns(provider, assayTypes, true);
        LogUtil.logSubsystemExit(LOG);
        return designs;
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(Long id) {
        return getArrayDao().getArrayDesign(id);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return getArrayDao().getArrayDesign(lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getArrayDesigns() {
        LogUtil.logSubsystemEntry(LOG);
        List<ArrayDesign> designs = getArrayDao().getArrayDesigns();
        LogUtil.logSubsystemExit(LOG);
        return designs;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isArrayDesignLocked(Long id) {
        return getArrayDao().isArrayDesignLocked(id);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessException, InvalidDataFileException {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        FileStatus fileSetStatus = arrayDesign.getDesignFileSet().getStatus();
        if (fileSetStatus == FileStatus.IMPORTING || fileSetStatus == FileStatus.IMPORTED) {
            throw new IllegalAccessException("Cannot modify an array design while the design file is being imported"
                    + " or after it has been imported");
        }
        if (!validateDuplicate(arrayDesign).isValid()) {
            throw new InvalidDataFileException(arrayDesign.getDesignFiles().iterator().next().getValidationResult());
        }
        Long id = arrayDesign.getId();
        if (id != null && isArrayDesignLocked(id)) {
            getArrayDao().evictObject(arrayDesign);
            if (!validateLockedDesign(arrayDesign)) {
                throw new IllegalAccessException("Cannot modify locked fields on an array design");
            }
            getArrayDao().mergeObject(arrayDesign);
        } else {
            getArrayDao().save(arrayDesign);
        }
        LogUtil.logSubsystemExit(LOG);
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Sets the DAO factory to use -- test support method.
     *
     * @param daoFactory the DAO factory
     */
    public void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private ArrayDao getArrayDao() {
        return getDaoFactory().getArrayDao();
    }

    private VocabularyService getVocabularyService() {
        return (VocabularyService) ServiceLocatorFactory.getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    private boolean validateLockedDesign(ArrayDesign arrayDesign) {
        ArrayDesign loadedArrayDesign = getArrayDesign(arrayDesign.getId());
        if (!loadedArrayDesign.getProvider().equals(arrayDesign.getProvider())
                || !loadedArrayDesign.getAssayTypes().equals(arrayDesign.getAssayTypes())
                || !loadedArrayDesign.getDesignFiles().equals(arrayDesign.getDesignFiles())) {
            return false;
        }
        getArrayDao().evictObject(loadedArrayDesign);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return getArrayDao().getDesignElementList(lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(DELETE_ARRAY_DELETE_TIMEOUT_SECONDS)
    public void deleteArrayDesign(ArrayDesign arrayDesign) throws ArrayDesignDeleteException {
        Long id = arrayDesign.getId();
        boolean designLocked = (id != null && isArrayDesignLocked(id));
        if (arrayDesign.getDesignFileSet().getStatus() == FileStatus.IMPORTING || designLocked) {
            throw new ArrayDesignDeleteException(
                    "You cannot delete an array design that is currently being "
                    + "imported or that is associated with one or more experiments.");
        }

        if (getArrayDao().getArrayDesigns(arrayDesign.getDesignDetails()).size() > 1) {
            // if there's more than one array design for the design details, we don't want to delete the details
            // or the files, just the array design itself, because other array designs share the files and details.
            arrayDesign.getDesignFiles().clear();
            arrayDesign.setDesignDetails(null);
        } else {
            getArrayDao().deleteArrayDesignDetails(arrayDesign);
        }
        getArrayDao().remove(arrayDesign);
    }
}
