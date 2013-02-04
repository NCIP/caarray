//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
