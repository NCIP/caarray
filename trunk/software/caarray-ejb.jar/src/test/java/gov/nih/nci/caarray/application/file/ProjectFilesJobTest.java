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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Verifies project files job functionality.
 */
public class ProjectFilesJobTest {

    AbstractProjectFilesJob job;
    @Mock Project project;
    @Mock CaArrayFileSet fileSet;
    @Mock ArrayDataImporter arrayDataImporter;
    @Mock MageTabImporter mageTabImporter;
    @Mock ProjectDao projectDao;
    @Mock SearchDao searchDao;
    @Mock FileAccessService fileAccessService;

    @Before
    public void setUp() {
        job = mock(AbstractProjectFilesJob.class, Mockito.CALLS_REAL_METHODS);
        MockitoAnnotations.initMocks(this);

        setupProjectMock(project);
        setupNonChildFileSet();

        when(job.getFileAccessService()).thenReturn(fileAccessService);
        when(searchDao.retrieve(eq(Project.class), eq(1L))).thenReturn(project);


        job.init("testuser", project, fileSet, arrayDataImporter, mageTabImporter,
                projectDao, searchDao);
    }

    @SuppressWarnings("unchecked")
    private void initSearchDao() {
        ImmutableList<CaArrayFile> filesList = ImmutableList.copyOf(fileSet.getFiles().iterator());
        when(searchDao.retrieveByIds(eq(CaArrayFile.class),
                any(List.class))).thenReturn(filesList);
    }

    private void setupNonChildFileSet() {
        CaArrayFile file = mock(CaArrayFile.class);
        when(file.getId()).thenReturn(1L);
        when(fileSet.getFiles()).thenReturn(ImmutableSet.of(file));
        initSearchDao();
    }

    private void setupParentChildFileSet() {
        CaArrayFile parent = mock(CaArrayFile.class);
        when(parent.getId()).thenReturn(2L);

        CaArrayFile child = mock(CaArrayFile.class);
        when(child.getId()).thenReturn(3L);

        when(child.getParent()).thenReturn(parent);
        when(parent.getChildren()).thenReturn(Collections.singleton(child));

        Set<CaArrayFile> files = ImmutableSet.of(parent, child);

        when(fileSet.getFiles()).thenReturn(files);
        initSearchDao();
    }

    /**
     * Creates reasonable mock project suitable for construction of a project files job.
     * @param project mock project to set up
     */
    static void setupProjectMock(Project project) {
        when(project.getId()).thenReturn(1L);
        Experiment e = mock(Experiment.class);
        when(e.getTitle()).thenReturn("experimentTitle");
        when(project.getExperiment()).thenReturn(e);
    }

    @Test
    public void construction() {
        assertEquals("testuser", job.getOwnerName());
        assertEquals("experimentTitle", job.getJobEntityName());
        assertEquals(project.getId().longValue(), job.getJobEntityId());
        assertEquals(fileSet.getFiles().size(), job.getFileSet().getFiles().size());
        assertEquals(project, job.getProject());
        assertEquals(mageTabImporter, job.getMageTabImporter());
        assertEquals(projectDao, job.getProjectDao());
    }

    @Test
    public void hasReadAccess() {
        User canRead = mock(User.class);
        when(project.hasReadPermission(canRead)).thenReturn(true);
        assertTrue(job.userHasReadAccess(canRead));
    }

    @Test
    public void noReadAccess() {
        User canRead = mock(User.class);
        when(project.hasReadPermission(canRead)).thenReturn(false);
        assertFalse(job.userHasReadAccess(canRead));
    }

    @Test
    public void hasWriteAccess() {
        User canWrite = mock(User.class);
        when(project.hasWritePermission(canWrite)).thenReturn(true);
        assertTrue(job.userHasWriteAccess(canWrite));
    }

    @Test
    public void noWriteAccess() {
        User noWrite = mock(User.class);
        when(project.hasWritePermission(noWrite)).thenReturn(false);
        assertFalse(job.userHasWriteAccess(noWrite));
    }

    @Test
    public void doValidate() {
        job.doValidate(job.getFileSet());
        verify(mageTabImporter).validateFiles(project, job.getFileSet());
        verify(arrayDataImporter).validateFiles(job.getFileSet(), null, false);
    }

    @Test
    public void pullUpValidationMessagesOnExecute() {
        doNothing().when(job).executeProjectFilesJob();
        doReturn(fileSet).when(job).getFileSet();
        job.doExecute();
        verify(job).executeProjectFilesJob();
        verify(fileSet).pullUpValidationMessages();
    }

    @Test
    public void deleteChildFilesOnExecute() {
        setupParentChildFileSet();
        doNothing().when(job).executeProjectFilesJob();
        doReturn(fileSet).when(job).getFileSet();
        job.doExecute();

        for (CaArrayFile file : fileSet.getFiles()) {
            if (file.getParent() != null) {
                verify(fileAccessService).remove(eq(file));
            }
        }
        verifyNoMoreInteractions(fileAccessService);
    }
}
