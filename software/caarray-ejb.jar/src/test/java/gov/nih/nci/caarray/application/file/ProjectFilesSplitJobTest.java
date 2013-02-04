//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.util.CaArrayFileSetSplitter;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;

/**
 * Job splitter tests.
 */
public class ProjectFilesSplitJobTest {

    DataImportOptions dataImportOptions;
    @Mock Project project;
    @Mock CaArrayFileSet fileset;
    @Mock ArrayDataImporter arrayDataImporter;
    @Mock MageTabImporter mageTabImporter;
    @Mock ProjectDao projectDao;
    @Mock FileAccessService fileAccessService;
    @Mock SearchDao searchDao;
    @Mock CaArrayFileSetSplitter fileSetSplitter;
    @Mock FileManagementJobSubmitter jobSubmitter;
    private ProjectFilesSplitJob job;
    private CaArrayFile file;
    private ProjectFilesSplitJob compositeJob;
    private Set<CaArrayFileSet> superSet;
    
    private static final int CHILD_JOB_COUNT=5;
    

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        dataImportOptions = DataImportOptions.getAutoCreatePerFileOptions();
        MockitoAnnotations.initMocks(this);
        ProjectFilesJobTest.setupProjectMock(project);
        job = new ProjectFilesSplitJob("testuser", project, fileset, arrayDataImporter, mageTabImporter, 
                fileAccessService, projectDao, 
                searchDao, dataImportOptions, fileSetSplitter, jobSubmitter);
        
        file = mock(CaArrayFile.class);

        superSet = new HashSet<CaArrayFileSet>();
        for( int i=0; i<CHILD_JOB_COUNT; i++ ) {
        	CaArrayFileSet fileSet = mock(CaArrayFileSet.class);
        	superSet.add(fileSet);
        }
        
        compositeJob = new ProjectFilesSplitJob("testuser", project, fileset, arrayDataImporter, mageTabImporter, 
                fileAccessService, projectDao, 
                searchDao, dataImportOptions, fileSetSplitter, jobSubmitter);

        when(searchDao.retrieveByIds(eq(CaArrayFile.class), 
                any(List.class))).thenReturn(Collections.singletonList(file));
        when(searchDao.retrieve(eq(Project.class), eq(1L))).thenReturn(project);
    }
    
    @Test
    public void jobType() {
        assertEquals(JobType.DATA_FILE_SPLIT, job.getJobType());
    }
    
    @Test
    public void inProgressStatus() {
        assertEquals(FileStatus.IMPORTING, job.getInProgressStatus());
    }
    
    @Test
    public void basicFlow() throws MageTabParsingException, IOException {
        when(file.getFileStatus()).thenReturn(FileStatus.VALIDATED);
        when(fileSetSplitter.split(any(CaArrayFileSet.class))).thenAnswer(new Answer<Set<CaArrayFileSet>>() {
            @Override
            public Set<CaArrayFileSet> answer(InvocationOnMock invocation)
                    throws Throwable {
                return ImmutableSet.of((CaArrayFileSet) invocation.getArguments()[0]);
            } 
        });

        job.executeProjectFilesJob();
        verifyValidateCalled();
        verifyImportCalled();
    }

    @Test
    public void childJobsVerificationFlow() throws MageTabParsingException, IOException {
        when(file.getFileStatus()).thenReturn(FileStatus.VALIDATED);
        when(fileSetSplitter.split(any(CaArrayFileSet.class))).thenAnswer(new Answer<Set<CaArrayFileSet>>() {
            @Override
            public Set<CaArrayFileSet> answer(InvocationOnMock invocation)
                    throws Throwable {
                return superSet;
            } 
        });

        compositeJob.executeProjectFilesJob();
        verifyChildrenCreation();
    }

	@Test
    public void ioExceptionFlow() throws IOException {
        when(file.getFileStatus()).thenReturn(FileStatus.VALIDATED);
        when(fileSetSplitter.split(any(CaArrayFileSet.class))).thenThrow(new IOException());
        job.executeProjectFilesJob();
        verifyValidateCalled();
        verifyImportCalled();
    }
    
    @Test
    public void validateFail() {
        when(file.getFileStatus()).thenReturn(FileStatus.VALIDATION_ERRORS);
        job.executeProjectFilesJob();
        verifyValidateCalled();
        verify(jobSubmitter, never()).submitJob(any(ProjectFilesImportJob.class));
    }

    private void verifyValidateCalled() {
        verify(mageTabImporter).validateFiles(project, job.getFileSet());
        verify(arrayDataImporter).validateFiles(job.getFileSet(), null, false);
    }

    private void verifyImportCalled() {
        verify(projectDao).flushSession();
        verify(jobSubmitter).submitJob(argThat(new ArgumentMatcher<AbstractFileManagementJob>() {
            @Override
            public boolean matches(Object argument) {
                ProjectFilesImportJob importJob = (ProjectFilesImportJob) argument;
                return importJob.getOwnerName().equals(job.getOwnerName())
                        && importJob.getProject().equals(job.getProject())
                        && importJob.getArrayDataImporter().equals(job.getArrayDataImporter())
                        && importJob.getMageTabImporter().equals(job.getMageTabImporter())
                        && importJob.getFileAccessService().equals(job.getFileAccessService())
                        && importJob.getProjectDao().equals(job.getProjectDao())
                        && importJob.getSearchDao().equals(job.getSearchDao());
            }
        }));
    }

    private void verifyChildrenCreation() {
    	assertTrue( CHILD_JOB_COUNT>1 );
    	assertEquals( CHILD_JOB_COUNT, compositeJob.getChildren().size() );
    	assertEquals( compositeJob.getChildren().get(0).getParent().getJobId(), compositeJob.getJobId() );
    	assertEquals( compositeJob.getChildren().get(CHILD_JOB_COUNT-1).getParent().getJobId(), compositeJob.getJobId() );
    	assertNotSame( compositeJob.getChildren().get(0), compositeJob.getChildren().get(CHILD_JOB_COUNT-1) );
	}

}
