//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.google.inject.Inject;

/**
 * Implementation entry point for the ArrayDesign subsystem.
 */
@Local(ArrayDesignService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@TransactionTimeout(ArrayDesignServiceBean.TIMEOUT_SECONDS)
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
@Interceptors(InjectionInterceptor.class)
public class ArrayDesignServiceBean implements ArrayDesignService {
    private static final Logger LOG = Logger.getLogger(ArrayDesignServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;
    static final int DELETE_ARRAY_DELETE_TIMEOUT_SECONDS = 7200;

    private ArrayDao arrayDao;
    private SearchDao searchDao;
    private ContactDao contactDao;
    private ArrayDesignPlatformFacade arrayDesignPlatformFacade;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(Set<CaArrayFile> designFiles) {
        LogUtil.logSubsystemEntry(LOG, designFiles);
        final ValidationResult result = this.arrayDesignPlatformFacade.validateDesignFiles(designFiles);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(ArrayDesign design) {
        LogUtil.logSubsystemEntry(LOG, design);
        final ValidationResult result = this.arrayDesignPlatformFacade.validateDesignFiles(design.getDesignFiles());
        final FileValidationResult duplicateResult = validateDuplicate(design);
        result.addFile(design.getFirstDesignFile().getName(), duplicateResult);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    private FileValidationResult validateDuplicate(ArrayDesign arrayDesign) {
        final CaArrayFile designFile = arrayDesign.getFirstDesignFile();
        FileValidationResult result = designFile.getValidationResult();
        if (result == null) {
            result = new FileValidationResult();
        }
        if (isDuplicate(arrayDesign)) {
            result.addMessage(Type.ERROR, "An array design already exists with the name " + arrayDesign.getName()
                    + " and provider " + arrayDesign.getProvider().getName());
            designFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            designFile.setValidationResult(result);
            this.arrayDao.save(designFile);
            this.arrayDao.flushSession();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDuplicate(ArrayDesign arrayDesign) {
        final List<ArrayDesign> providerDesigns = this.arrayDao.getArrayDesigns(arrayDesign.getProvider(), null, false);
        for (final ArrayDesign provDesign : providerDesigns) {
            if (!arrayDesign.equals(provDesign) && arrayDesign.getName().equalsIgnoreCase(provDesign.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importDesign(ArrayDesign arrayDesign) {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        if (arrayDesign.getDesignFiles().isEmpty()) {
            LOG.warn("importDesign called, but no design files provided. No updates made.");
            return;
        }
        // Temporarily change the LSID so that file validation does not find this array design as a duplicate.
        final String lsid = arrayDesign.getLsid();
        final String tmpLsid = lsid + "tmp";
        arrayDesign.setLsidForEntity(tmpLsid);
        this.arrayDao.save(arrayDesign);
        this.arrayDao.flushSession();
        final ArrayDesignPlatformFacade facade = this.arrayDesignPlatformFacade;
        if (facade.validateDesignFiles(arrayDesign.getDesignFiles()).isValid()) {
            facade.importDesign(arrayDesign);
            if (validateDuplicate(arrayDesign).isValid()) {
                this.arrayDao.save(arrayDesign);
            }
        }
        if (tmpLsid.equals(arrayDesign.getLsid())) {
            arrayDesign.setLsidForEntity(lsid);
            this.arrayDao.save(arrayDesign);
        }
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importDesignDetails(ArrayDesign arrayDesign) {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        if (arrayDesign.getDesignFiles().isEmpty()) {
            LOG.warn("importDesignDetails called, but no design file(s) provided. No updates made.");
            return;
        }
        for (final CaArrayFile designFile : arrayDesign.getDesignFiles()) {
            if (!designFile.getValidationResult().isValid()) {
                throw new IllegalArgumentException("The array design provided for import is not valid");
            }
        }

        final ArrayDesignDetails designDetails = arrayDesign.getDesignDetails();
        if (designDetails != null) {
            this.arrayDao.deleteArrayDesignDetails(arrayDesign);
            this.arrayDao.flushSession();
        }

        this.arrayDesignPlatformFacade.importDesignDetails(arrayDesign);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Organization> getAllProviders() {
        LogUtil.logSubsystemEntry(LOG);
        final List<Organization> orgs = this.contactDao.getAllProviders();
        LogUtil.logSubsystemExit(LOG);
        return orgs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArrayDesign> getImportedArrayDesigns(Organization provider, Set<AssayType> assayTypes) {
        LogUtil.logSubsystemEntry(LOG);
        final List<ArrayDesign> designs = this.arrayDao.getArrayDesigns(provider, assayTypes, true);
        LogUtil.logSubsystemExit(LOG);
        return designs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(Long id) {
        return this.arrayDao.getArrayDesign(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return this.arrayDao.getArrayDesign(lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArrayDesign> getArrayDesigns() {
        LogUtil.logSubsystemEntry(LOG);
        final List<ArrayDesign> designs = this.searchDao.retrieveAll(ArrayDesign.class, Order.asc("name"));
        LogUtil.logSubsystemExit(LOG);
        return designs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isArrayDesignLocked(Long id) {
        return this.arrayDao.isArrayDesignLocked(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ArrayDesign saveArrayDesign(ArrayDesign arrayDesign) 
    throws IllegalAccessException, InvalidDataFileException {
        LogUtil.logSubsystemEntry(LOG, arrayDesign);
        final FileStatus fileSetStatus = arrayDesign.getDesignFileSet().getStatus();
        if (fileSetStatus == FileStatus.IMPORTING || fileSetStatus == FileStatus.IMPORTED) {
            throw new IllegalAccessException("Cannot modify an array design while the design file is being imported"
                    + " or after it has been imported");
        }
        if (!validateDuplicate(arrayDesign).isValid()) {
            throw new InvalidDataFileException(arrayDesign.getDesignFiles().iterator().next().getValidationResult());
        }
        final Long id = arrayDesign.getId();
        if (id != null && isArrayDesignLocked(id)) {
            if (!validateLockedDesign(arrayDesign)) {
                throw new IllegalAccessException("Cannot modify locked fields on an array design");
            }
            LogUtil.logSubsystemExit(LOG);
            return (ArrayDesign) this.arrayDao.mergeObject(arrayDesign);
        } else {
            this.arrayDao.save(arrayDesign);
            LogUtil.logSubsystemExit(LOG);
            return arrayDesign;
        }
    }

    /**
     * Validated that the given array design (which is considered "locked") has not had its provider, assay types, or
     * design files properties modified.
     * 
     * @param arrayDesign the design to check
     * @return true if the design has not had the key properties above modified, false if it has.
     */
    private boolean validateLockedDesign(ArrayDesign arrayDesign) {
        final Organization provider = arrayDesign.getProvider();
        final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>(arrayDesign.getAssayTypes());
        final Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>(arrayDesign.getDesignFiles());
        this.arrayDao.evictObject(arrayDesign);
        final ArrayDesign loadedArrayDesign = getArrayDesign(arrayDesign.getId());
        if (!loadedArrayDesign.getProvider().equals(provider) || !loadedArrayDesign.getAssayTypes().equals(assayTypes)
                || !loadedArrayDesign.getDesignFiles().equals(designFiles)) {
            return false;
        }
        this.arrayDao.evictObject(loadedArrayDesign);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return this.arrayDao.getDesignElementList(lsidAuthority, lsidNamespace, lsidObjectId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(DELETE_ARRAY_DELETE_TIMEOUT_SECONDS)
    public void deleteArrayDesign(ArrayDesign arrayDesign) throws ArrayDesignDeleteException {
        LOG.info("Deleting array design " + arrayDesign.getName());
        final Long id = arrayDesign.getId();
        final boolean designLocked = (id != null && isArrayDesignLocked(id));
        if (arrayDesign.getDesignFileSet().getStatus() == FileStatus.IMPORTING || designLocked) {
            LOG.info("Array design " + arrayDesign.getName() + " is in a state that does not allow it to be deleted");
            throw new ArrayDesignDeleteException("You cannot delete an array design that is currently being "
                    + "imported or that is associated with one or more experiments.");
        }

        if (this.arrayDao.getArrayDesigns(arrayDesign.getDesignDetails()).size() > 1) {
            // if there's more than one array design for the design details, we don't want to delete the details
            // or the files, just the array design itself, because other array designs share the files and details.
            LOG.debug("Deleting just the array design object");
            arrayDesign.getDesignFiles().clear();
            arrayDesign.setDesignDetails(null);
        } else {
            LOG.debug("Deleting the array design and the array design details");
            this.arrayDao.deleteArrayDesignDetails(arrayDesign);
        }
        this.arrayDao.remove(arrayDesign);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArrayDesign> getArrayDesignsWithReImportableFiles() {
        return this.arrayDao.getArrayDesignsWithReImportable();
    }

    /**
     * @param arrayDao the arrayDao to set
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * @param searchDao the searchDao to set
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * @param contactDao the contactDao to set
     */
    @Inject
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    /**
     * @param arrayDesignPlatformFacade the arrayDesignPlatformFacade to set
     */
    @Inject
    public void setArrayDesignPlatformFacade(ArrayDesignPlatformFacade arrayDesignPlatformFacade) {
        this.arrayDesignPlatformFacade = arrayDesignPlatformFacade;
    }
}
