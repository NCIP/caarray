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

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.util.CaArrayFileSetSplitter;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.Project;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableSet;

/**
 * Splits large Mage-Tab File sets into smaller chunks.  Dispatches to ProjectFilesImportJob
 * after splitting.
 */
class ProjectFilesSplitJob extends AbstractProjectFilesJob {

    private static final long serialVersionUID = -6505339669676465113L;
    private static final Logger LOG = Logger.getLogger(ProjectFilesSplitJob.class);

    private final CaArrayFileSetSplitter splitter;
    private final DataImportOptions dataImportOptions;
    private final FileManagementJobSubmitter jobSubmitter;

    /**
     * Injected constructor.
     *
     * @param username user requesting job
     * @param targetProject project
     * @param fileSet set to split
     * @param arrayDataImporter not used
     * @param mageTabImporter not used
     * @param fileAccessService for creating new files
     * @param projectDao dao
     * @param searchDao dao
     * @param dataImportOptions import options for new sets
     * @param splitter file set splitter
     */
    // CHECKSTYLE:OFF more than 7 parameters are okay for injected constructor
    @SuppressWarnings("PMD.ExcessiveParameterList")
    ProjectFilesSplitJob(String username, Project targetProject,
            CaArrayFileSet fileSet, ArrayDataImporter arrayDataImporter,
            MageTabImporter mageTabImporter,
            FileAccessService fileAccessService, ProjectDao projectDao,
            SearchDao searchDao, DataImportOptions dataImportOptions, CaArrayFileSetSplitter splitter,
            FileManagementJobSubmitter jobSubmitter) {
        // CHECKSTYLE:ON
        super(username, targetProject, fileSet, arrayDataImporter, mageTabImporter,
                fileAccessService, projectDao, searchDao);
        this.dataImportOptions = dataImportOptions;
        this.splitter = splitter;
        this.jobSubmitter = jobSubmitter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void executeProjectFilesJob() {
        CaArrayFileSet fileSet = getFileSet();
        doValidate(fileSet);

        if (fileSet.isValidated()) {
            importSplits(fileSet);
        }
    }

    private void importSplits(CaArrayFileSet origFileSet) {
        Set<CaArrayFileSet> splits = getSplitsToImport(origFileSet);
        for (CaArrayFileSet curSplit : splits) {
            curSplit.updateStatus(FileStatus.VALIDATED);
            handleSessionMess(); // new job needs the new split sdrf to have an id
            ProjectFilesImportJob job = new ProjectFilesImportJob(getOwnerName(), getProject(), curSplit,
                    dataImportOptions, getArrayDataImporter(), getMageTabImporter(), getFileAccessService(),
                    getProjectDao(), getSearchDao(), this);
            getChildren().add(job);
            jobSubmitter.submitJob(job);
        }
    }

    private Set<CaArrayFileSet> getSplitsToImport(CaArrayFileSet origFileSet) {
        try {
            return splitter.split(origFileSet);
        } catch (IOException e) {
            LOG.warn("Unable to split file set.  Falling back to non-split import.", e);
            return ImmutableSet.of(origFileSet);
        }
    }

    /**
     * This method is necessary because the Hibernate Session FlushMode is set to
     * FlushMode.COMMIT.  This means that queries will not flush prior to running.
     * Call this method only when a dependency needs database state to be correct.
     *
     * This is a compromise - it's misplaced responsibility to do session management
     * at this level.  We'd be better off not exposing CaArrayDao.flushSession(), but
     * since we have it and use it in many places, this is just one of many.
     */
    private void handleSessionMess() {
        getProjectDao().flushSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FileStatus getInProgressStatus() {
        return FileStatus.IMPORTING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobType getJobType() {
        return JobType.DATA_FILE_SPLIT;
    }
}
