//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.JobStatus;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableSet;

/**
 * Verifies the functionality of the abstract file management job.  Basic control methods
 * and status checks.
 */
public class FileManagementJobTest {

    AbstractFileManagementJob job = null;
    @Mock CaArrayFileSet fileSet;

    @Before
    public void setUp() {
        job = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void construction() {
        job.init("foo");
        assertEquals("foo", job.getOwnerName());
        assertTrue(job.getTimeRequested().getTime() <= System.currentTimeMillis());
        assertEquals(FileStatus.IN_QUEUE, job.getInQueueStatus());
        assertNull(job.getJobStatus());
        assertNull(job.getTimeStarted());
        assertNull(job.getJobId());
        assertNull(job.getParent());
    }

    @Test
    public void execute() {
        doNothing().when(job).doExecute();
        job.execute();
        assertTrue(job.getTimeStarted().getTime() <= System.currentTimeMillis());
        verify(job).doExecute();
    }

    @Test
    public void markAsCancelledWithFiles() {
        doReturn(fileSet).when(job).getFileSet();
        job.markAsCancelled();
        assertEquals(JobStatus.CANCELLED, job.getJobStatus());
        verify(fileSet).updateStatus(FileStatus.UPLOADED);
        verify(job).getFileSet();
    }

    @Test
    public void markInProgress() {
        doReturn(fileSet).when(job).getFileSet();
        doReturn(FileStatus.IMPORTING).when(job).getInProgressStatus();
        job.markAsInProgress();
        assertEquals(JobStatus.RUNNING, job.getJobStatus());
        assertTrue(job.isInProgress());
        verify(fileSet).updateStatus(FileStatus.IMPORTING);
    }

    @Test
    public void markInQueue() {
        doReturn(fileSet).when(job).getFileSet();
        doReturn(FileStatus.IN_QUEUE).when(job).getInQueueStatus();
        job.markAsInQueue();
        assertEquals(JobStatus.IN_QUEUE, job.getJobStatus());
        assertFalse(job.isInProgress());
        verify(fileSet).updateStatus(FileStatus.IN_QUEUE);
    }

    @Test
    public void markAsProcessed() {
        job.markAsProcessed();
        assertEquals(JobStatus.PROCESSED, job.getJobStatus());
        assertFalse(job.isInProgress());
    }

    @Test
    public void cancelledChildrenFlag() {
        // setup parent and child jobs
        AbstractFileManagementJob parent = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        AbstractFileManagementJob child1 = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        AbstractFileManagementJob child2 = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        AbstractFileManagementJob child3 = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        List<BaseJob> children = new ArrayList<BaseJob>();
        children.add(child1);
        children.add(child2);
        children.add(child3);
        when(parent.getChildren()).thenReturn(children);
        when(child1.getParent()).thenReturn(parent);
        when(child2.getParent()).thenReturn(parent);
        when(child3.getParent()).thenReturn(parent);

        // setup parent and child files
        CaArrayFile parentFile = getNonArrayFile();
        when(parentFile.getFileStatus()).thenReturn(FileStatus.IN_QUEUE);
        
        CaArrayFile childFile1 = new CaArrayFile(parentFile);
        CaArrayFile childFile2 = new CaArrayFile(parentFile);
        CaArrayFile childFile3 = new CaArrayFile(parentFile);
        childFile1.setFileStatus(FileStatus.IN_QUEUE);
        childFile2.setFileStatus(FileStatus.IN_QUEUE);
        childFile3.setFileStatus(FileStatus.IN_QUEUE);
        CaArrayFileSet parentFileSet = new CaArrayFileSet();
        CaArrayFileSet child1FileSet = new CaArrayFileSet();
        CaArrayFileSet child2FileSet = new CaArrayFileSet();
        CaArrayFileSet child3FileSet = new CaArrayFileSet();
        parentFileSet.add(parentFile);
        child1FileSet.add(childFile1);
        child2FileSet.add(childFile2);
        child3FileSet.add(childFile3);
        doReturn(parentFileSet).when(parent).getFileSet();
        doReturn(child1FileSet).when(child1).getFileSet();
        doReturn(child2FileSet).when(child2).getFileSet();
        doReturn(child3FileSet).when(child3).getFileSet();

        FileAccessService fas = mock(FileAccessService.class);
        when(child2.getFileAccessService()).thenReturn(fas);

        child1.markAsProcessed();
        verify(parent).handleChildProcessed();
        assertEquals(FileStatus.IN_QUEUE, parentFile.getFileStatus());

        child2.markAsCancelled();
        verify(parent).handleChildCancelled();
        assertEquals(FileStatus.UPLOADED, childFile2.getFileStatus());
        assertEquals(FileStatus.IN_QUEUE, parentFile.getFileStatus());

        child3.markAsProcessed();
        verify(parent, times(2)).handleChildProcessed();
        verify(parentFile).setFileStatus(eq(FileStatus.UPLOADED));
    }    
    
    @Test
    public void cancelKeepsDataFilesInImportedStatus() {
        CaArrayFile nonArrayDataFile = getNonArrayFile();
        
        CaArrayFile importedArrayDataFile = getArrayDataFile();
        when(importedArrayDataFile.getFileStatus()).thenReturn(FileStatus.IMPORTED);
        
        CaArrayFile importedUnparsedDataFile = getArrayDataFile();
        when(importedUnparsedDataFile.getFileStatus()).thenReturn(FileStatus.IMPORTED_NOT_PARSED);
        
        CaArrayFile validatedArrayDataFile = getArrayDataFile();
        when(validatedArrayDataFile.getFileStatus()).thenReturn(FileStatus.VALIDATED);
        
        when(fileSet.getFiles()).thenReturn(ImmutableSet.of(nonArrayDataFile, importedArrayDataFile, 
                importedUnparsedDataFile, validatedArrayDataFile));
        
        AbstractFileManagementJob job = mock(AbstractFileManagementJob.class, Mockito.CALLS_REAL_METHODS);
        doReturn(fileSet).when(job).getFileSet();
        
        job.handleChildCancelled();
        job.handleChildProcessed();
        
        verify(nonArrayDataFile).setFileStatus(eq(FileStatus.UPLOADED));
        verify(validatedArrayDataFile).setFileStatus(eq(FileStatus.UPLOADED));
        verify(importedArrayDataFile, times(0)).setFileStatus(any(FileStatus.class));
        verify(importedUnparsedDataFile, times(0)).setFileStatus(any(FileStatus.class));
    }

    private CaArrayFile getArrayDataFile() {
        FileType arrayDataType = mock(FileType.class);
        when(arrayDataType.isArrayData()).thenReturn(true);
        CaArrayFile arrayDataFile = mock(CaArrayFile.class);
        when(arrayDataFile.getFileType()).thenReturn(arrayDataType);
        return arrayDataFile;
    }

    private CaArrayFile getNonArrayFile() {
        FileType nonArrayDataType = mock(FileType.class);
        when(nonArrayDataType.isArrayData()).thenReturn(false);
        CaArrayFile nonArrayDataFile = mock(CaArrayFile.class);
        when(nonArrayDataFile.getFileType()).thenReturn(nonArrayDataType);
        return nonArrayDataFile;
    }
}
