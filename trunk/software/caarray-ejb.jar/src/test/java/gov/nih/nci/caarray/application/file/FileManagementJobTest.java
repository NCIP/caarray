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
