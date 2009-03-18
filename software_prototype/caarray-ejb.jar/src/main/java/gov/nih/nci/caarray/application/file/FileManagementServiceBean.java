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
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;
import org.hibernate.LockMode;
import org.jboss.annotation.ejb.TransactionTimeout;

/**
 * EJB implementation of the entry point to the FileManagement subsystem. Delegates functionality
 * to other components in the subsystem.
 */
@Local
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
public class FileManagementServiceBean implements FileManagementService {

    private static final Logger LOG = Logger.getLogger(FileManagementServiceBean.class);
    private static final int SAVE_ARRAY_DESIGN_TIMEOUT = 1800;

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
    private FileManagementJobSubmitter submitter = new JmsJobSubmitter();

    private void checkForImport(CaArrayFileSet fileSet) {
    	LOG.info("");
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isImportable()) {
                throw new IllegalArgumentException("Illegal attempt to import file "
                        + caArrayFile.getName() + " with status " + caArrayFile.getFileStatus());
            }
        }
    }

    private void checkForValidation(CaArrayFileSet fileSet) {
    	LOG.info("");
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            if (!caArrayFile.getFileStatus().isValidatable()) {
            	LOG.info(caArrayFile.getName() +" is not validatable" );
                throw new IllegalArgumentException("Illegal attempt to validate file "
                        + caArrayFile.getName() + " with status " + caArrayFile.getFileStatus());
            }
            else{
            	LOG.info(caArrayFile.getName() +" is validatable");
            }
            
        }
    }

    /**
     * {@inheritDoc}
     */
    public void importFiles(Project targetProject, CaArrayFileSet fileSet) {
    	LOG.info("importFiles");
        LogUtil.logSubsystemEntry(LOG, fileSet);
        checkForImport(fileSet);
        clearValidationMessages(fileSet);
        updateFileStatus(fileSet, FileStatus.IN_QUEUE);
        sendImportJobMessage(targetProject, fileSet);
        LogUtil.logSubsystemExit(LOG);
    }

    private void clearValidationMessages(CaArrayFileSet fileSet) {
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            caArrayFile.setValidationResult(null);
            getDaoFactory().getProjectDao().save(caArrayFile);
        }
    }

    private void sendImportJobMessage(Project targetProject, CaArrayFileSet fileSet) {
    	LOG.info("sendImportJobMessage");
        ProjectFilesImportJob job = new ProjectFilesImportJob(UsernameHolder.getUser(), targetProject, fileSet);
        getSubmitter().submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    public void validateFiles(Project project, CaArrayFileSet fileSet) {
    	LOG.info("");
        checkForValidation(fileSet);
        clearValidationMessages(fileSet);
        updateFileStatus(fileSet, FileStatus.IN_QUEUE);
        sendValidationJobMessage(project, fileSet);
    }

    private void sendValidationJobMessage(Project project, CaArrayFileSet fileSet) {
    	LOG.info("");
        ProjectFilesValidationJob job = new ProjectFilesValidationJob(UsernameHolder.getUser(), project, fileSet);
        getSubmitter().submitJob(job);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionTimeout(SAVE_ARRAY_DESIGN_TIMEOUT)
    public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFile designFile) throws InvalidDataFileException,
            IllegalAccessException {
        boolean newArrayDesign = arrayDesign.getId() == null;
        CaArrayFile oldFile = arrayDesign.getDesignFile();
        designFile.setFileStatus(FileStatus.VALIDATING);
        arrayDesign.setDesignFile(designFile);
        getDaoFactory().getProjectDao().save(designFile);
        getArrayDesignService().saveArrayDesign(arrayDesign);
        getArrayDesignService().importDesign(arrayDesign);            
        if (FileStatus.VALIDATION_ERRORS.equals(designFile.getFileStatus())) {
            if (newArrayDesign) {
                getDaoFactory().getArrayDao().remove(arrayDesign);
            } else {
                arrayDesign.setDesignFile(oldFile);
                getDaoFactory().getArrayDao().save(arrayDesign);
            }
            throw new InvalidDataFileException(designFile.getValidationResult());
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importArrayDesignDetails(ArrayDesign arrayDesign) {
        arrayDesign.getDesignFile().setFileStatus(FileStatus.IN_QUEUE);
        getDaoFactory().getProjectDao().save(arrayDesign.getDesignFile());
        ArrayDesignFileImportJob job = new ArrayDesignFileImportJob(UsernameHolder.getUser(), arrayDesign);
        getSubmitter().submitJob(job);
    }

    private void updateFileStatus(CaArrayFileSet fileSet, FileStatus status) {
        for (CaArrayFile file : fileSet.getFiles()) {
            file = getDaoFactory().getSearchDao().retrieve(CaArrayFile.class, file.getId(), LockMode.UPGRADE);
            file.setFileStatus(status);
            getDaoFactory().getProjectDao().save(file);
        }
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
    	LOG.info("addSupplementalFiles");
    	
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

}