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
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Order;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Implementation entry point for the ArrayDesign subsystem.
 */
@Local(ArrayDesignService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@TransactionTimeout(ArrayDesignServiceBean.TIMEOUT_SECONDS)
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public class ArrayDesignServiceBean implements ArrayDesignService {
    private static final Logger LOG = Logger.getLogger(ArrayDesignServiceBean.class);
    static final int TIMEOUT_SECONDS = 1800;
    static final int DELETE_ARRAY_DELETE_TIMEOUT_SECONDS = 7200;

    private Injector injector;
    
    /**
     * post-construct lifecycle method; intializes the Guice injector that will provide dependencies. 
     */
    @PostConstruct
    public void init() {
        this.injector = createInjector();
    }
    
    /**
     * Subclasses can override this to configure a custom injector, e.g. by overriding some modules with 
     * stubbed out functionality.
     * 
     * @return a Guice injector from which this will obtain dependencies.
     */
    protected Injector createInjector() {
        return Guice.createInjector(new ArrayDesignModule());
    }
    
    private ArrayDao getArrayDao() {
        return injector.getInstance(ArrayDao.class);
    }
    
    private SearchDao getSearchDao() {
        return injector.getInstance(SearchDao.class);        
    }

    private ContactDao getContactDao() {
        return injector.getInstance(ContactDao.class);        
    }

    private ArrayDesignPlatformFacade getFacade() {
        return injector.getInstance(ArrayDesignPlatformFacade.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(Set<CaArrayFile> designFiles) {
        LogUtil.logSubsystemEntry(LOG, designFiles);
        ValidationResult result = getFacade().validateDesignFiles(designFiles);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ValidationResult validateDesign(ArrayDesign design) {
        LogUtil.logSubsystemEntry(LOG, design);
        ValidationResult result = getFacade().validateDesignFiles(design.getDesignFiles());
        FileValidationResult duplicateResult = validateDuplicate(design);
        result.addFile(duplicateResult.getFile(), duplicateResult);
        LogUtil.logSubsystemExit(LOG);
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
            getArrayDao().getArrayDesigns(arrayDesign.getProvider(), null, false);
        for (ArrayDesign providerDesign : providerDesigns) {
            if (!arrayDesign.equals(providerDesign)
                    && arrayDesign.getName().equalsIgnoreCase(providerDesign.getName())) {
                return true;
            }
        }
        return false;
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
        ArrayDesignPlatformFacade facade = getFacade();
        if (facade.validateDesignFiles(arrayDesign.getDesignFiles()).isValid()) {
            facade.importDesign(arrayDesign);
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

        getFacade().importDesignDetails(arrayDesign);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getAllProviders() {
        LogUtil.logSubsystemEntry(LOG);
        List<Organization> orgs = getContactDao().getAllProviders();
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
        List<ArrayDesign> designs = getSearchDao().retrieveAll(ArrayDesign.class, Order.asc("name"));
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
    public ArrayDesign saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessException, 
        InvalidDataFileException {
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
            if (!validateLockedDesign(arrayDesign)) {
                throw new IllegalAccessException("Cannot modify locked fields on an array design");
            }
            LogUtil.logSubsystemExit(LOG);
            return (ArrayDesign) getArrayDao().mergeObject(arrayDesign);
        } else {
            getArrayDao().save(arrayDesign);
            LogUtil.logSubsystemExit(LOG);
            return arrayDesign;
        }
    }

    /**
     * Validated that the given array design (which is considered "locked") has not had its provider,
     * assay types, or design files properties modified.
     * @param arrayDesign the design to check
     * @return true if the design has not had the key properties above modified, false if it has.
     */
    private boolean validateLockedDesign(ArrayDesign arrayDesign) {
        Organization provider = arrayDesign.getProvider();
        SortedSet<AssayType> assayTypes = new TreeSet<AssayType>(arrayDesign.getAssayTypes());
        Set<CaArrayFile> designFiles = new HashSet<CaArrayFile>(arrayDesign.getDesignFiles());
        getArrayDao().evictObject(arrayDesign);
        ArrayDesign loadedArrayDesign = getArrayDesign(arrayDesign.getId());
        if (!loadedArrayDesign.getProvider().equals(provider) || !loadedArrayDesign.getAssayTypes().equals(assayTypes)
                || !loadedArrayDesign.getDesignFiles().equals(designFiles)) {
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
        LOG.info("Deleting array design " + arrayDesign.getName());
        Long id = arrayDesign.getId();
        boolean designLocked = (id != null && isArrayDesignLocked(id));
        if (arrayDesign.getDesignFileSet().getStatus() == FileStatus.IMPORTING || designLocked) {
            LOG.info("Array design " + arrayDesign.getName() + " is in a state that does not allow it to be deleted");
            throw new ArrayDesignDeleteException(
                    "You cannot delete an array design that is currently being "
                    + "imported or that is associated with one or more experiments.");
        }

        if (getArrayDao().getArrayDesigns(arrayDesign.getDesignDetails()).size() > 1) {
            // if there's more than one array design for the design details, we don't want to delete the details
            // or the files, just the array design itself, because other array designs share the files and details.
            LOG.debug("Deleting just the array design object");
            arrayDesign.getDesignFiles().clear();
            arrayDesign.setDesignDetails(null);
        } else {
            LOG.debug("Deleting the array design and the array design details");
            getArrayDao().deleteArrayDesignDetails(arrayDesign);
        }
        getArrayDao().remove(arrayDesign);
    }


    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getArrayDesignsWithReImportableFiles() {
        return getArrayDao().getArrayDesignsWithReImportable();
    }
}
