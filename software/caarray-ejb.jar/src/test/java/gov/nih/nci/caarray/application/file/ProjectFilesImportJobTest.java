//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.ServiceLocator;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Verifies import job functionality.
 */
public class ProjectFilesImportJobTest {

    String username;
    DataImportOptions dataImportOptions;
    @Mock Project project;
    @Mock CaArrayFileSet fileset;
    @Mock ArrayDataImporter arrayDataImporter;
    @Mock MageTabImporter mageTabImporter;
    @Mock FileAccessService fileAccessService;
    @Mock ProjectDao projectDao;
    @Mock SearchDao searchDao;
    @Mock FileManagementService fileManagementService;
    @Mock ServiceLocator serviceLocator;
    ProjectFilesImportJob job;
    
    @Before
    public void setUp() {
        username = "testuser";
        dataImportOptions = DataImportOptions.getAutoCreatePerFileOptions();
        MockitoAnnotations.initMocks(this);
        ServiceLocatorFactory.setLocator(serviceLocator);
        ProjectFilesJobTest.setupProjectMock(project);
        job = new ProjectFilesImportJob(username, project, fileset, dataImportOptions, 
                arrayDataImporter, mageTabImporter, fileAccessService, projectDao, searchDao);
        
    }
    
    @Test
    public void jobType() {
        assertEquals(JobType.DATA_FILE_IMPORT, job.getJobType());
    }
    
    @Test
    public void inProgressStatus() {
        assertEquals(FileStatus.IMPORTING, job.getInProgressStatus());
    }
    
    @Test
    public void basicFlow() throws MageTabParsingException {
        CaArrayFile file = checkValidateExecuted(FileStatus.VALIDATED);
        assertImportDidHappen(file);
    }

    @Test
    public void validationFail() throws MageTabParsingException {
        checkValidateExecuted(FileStatus.VALIDATION_ERRORS);
        assertImportDidNotHappen();
    }
    
    private void assertImportDidHappen(CaArrayFile file)
            throws MageTabParsingException {
        verify(mageTabImporter).importFiles(eq(project), argThat(new SingleFileCaArrayFileSetMatcher(file)));
        verify(projectDao).flushSession();
        verify(projectDao).clearSession();
        verify(arrayDataImporter).importFiles(argThat(new SingleFileCaArrayFileSetMatcher(file)), 
                eq(dataImportOptions), any(MageTabDocumentSet.class));
    }
    
    private void assertImportDidNotHappen() throws MageTabParsingException {
        verify(mageTabImporter, never()).importFiles(any(Project.class), any(CaArrayFileSet.class));
        verify(projectDao, never()).flushSession();
        verify(projectDao, never()).clearSession();
        verify(arrayDataImporter, never()).importFiles(any(CaArrayFileSet.class), 
                any(DataImportOptions.class), any(MageTabDocumentSet.class));
    }
    
    @SuppressWarnings("unchecked")
    private CaArrayFile checkValidateExecuted(FileStatus singleFileStatus) throws MageTabParsingException {
        CaArrayFile file = mock(CaArrayFile.class);
        when(file.getFileStatus()).thenReturn(singleFileStatus);
        
        when(searchDao.retrieveByIds(eq(CaArrayFile.class), 
                any(List.class))).thenReturn(Collections.singletonList(file));
        when(searchDao.retrieve(eq(Project.class), eq(1L))).thenReturn(project);

        MageTabDocumentSet mageTabDocSet = mock(MageTabDocumentSet.class);
        when(mageTabImporter.importFiles(eq(project), 
                argThat(new SingleFileCaArrayFileSetMatcher(file)))).thenReturn(mageTabDocSet);
        
        job.executeProjectFilesJob();
        
        verify(mageTabImporter).validateFiles(project, job.getFileSet());
        verify(arrayDataImporter).validateFiles(job.getFileSet(), null, false);
        return file;
    }
}
