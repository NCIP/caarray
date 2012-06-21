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
package gov.nih.nci.caarray.jobqueue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.stub.ExecutableJobStub;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobMessageSender;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;


public class JobQueueTest {
    private static final int NUMBER_OF_MOCK_JOBS = 3;
    
    private JobQueueImpl systemUnderTest;
    private List<ExecutableJob> mockJobs;
    private int jobIndex;
    private JobMessageSender jobMessageSender;
    private FileDao fileDao;

    @Before
    public void setUp() {
        jobMessageSender = mock(JobMessageSender.class);
        fileDao = mock(FileDao.class);
        systemUnderTest = new JobQueueImpl(jobMessageSender, fileDao);        
        mockJobs = createMockJobsList(NUMBER_OF_MOCK_JOBS);
        jobIndex = 0;
    }
    
    @Test
    public void newQueueIsEmpty() {
        assertThat(systemUnderTest.getLength(), is(0));
        assertThat(getNextJobIfAvailableAndSetInProgress(), is(nullValue()));
        assertThat(systemUnderTest.getJobList().size(), is(0));
    }
    
    @Test(expected=IllegalStateException.class)
    public void dequeueOnNewQueueThrowsException() {
        systemUnderTest.dequeue();
    }
    
    @Test
    public void canEnqueueJobOnNewQueue() {
        systemUnderTest.enqueue(getNextMockJob());
        
        assertThat(systemUnderTest.getLength(), is(1));
        assertThat(getNextJobIfAvailableAndSetInProgress(), isJobIndex(0));
        assertThat(systemUnderTest.getJobList(), is(mockJobs.subList(0, 1)));
    }
    
    @Test
    public void canDequeueFromLengthOneQueue() {
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.dequeue();
        
        assertThat(systemUnderTest.getLength(), is(0));
        assertThat(getNextJobIfAvailableAndSetInProgress(), is(nullValue()));
        assertThat(systemUnderTest.getJobList().size(), is(0));
    }
    
    @Test(expected=IllegalStateException.class)
    public void twoDequeuesOnLengthOneQueueThrowsException() {
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.dequeue();
        systemUnderTest.dequeue();
    }
        
    @Test
    public void canEnqueueOnLengthOneQueue() {
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.enqueue(getNextMockJob());
        
        assertThat(systemUnderTest.getLength(), is(2));
        assertThat(getNextJobIfAvailableAndSetInProgress(), isJobIndex(0));
        assertThat(systemUnderTest.getJobList(), is(mockJobs.subList(0, 2)));
    }
    
    @Test
    public void canDequeueFromLengthTwoQueue() {
        systemUnderTest.enqueue(getNextMockJob());        
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.dequeue();
        
        assertThat(systemUnderTest.getLength(), is(1));
        assertThat(getNextJobIfAvailableAndSetInProgress(), isJobIndex(1));
        assertThat(systemUnderTest.getJobList(), is(mockJobs.subList(1, 2)));
    }
    
    @Test
    public void canDequeueTwiceFromLengthTwoQueue() {
        systemUnderTest.enqueue(getNextMockJob());        
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.dequeue();
        systemUnderTest.dequeue();
        
        assertThat(systemUnderTest.getLength(), is(0));
        assertThat(getNextJobIfAvailableAndSetInProgress(), is(nullValue()));
        assertThat(systemUnderTest.getJobList().size(), is(0));
    }
    
    @Test(expected=IllegalStateException.class)
    public void threeDequeuesOnLengthTwoQueueThrowsException() {
        systemUnderTest.enqueue(getNextMockJob());        
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.dequeue();
        systemUnderTest.dequeue();
        systemUnderTest.dequeue();
    }
    
    @Test
    public void canEnqueueOnLengthTwoQueue() {
        ExecutableJob firstJob = getNextMockJob();        
        systemUnderTest.enqueue(firstJob);        
        systemUnderTest.enqueue(getNextMockJob());
        
        systemUnderTest.enqueue(getNextMockJob());
        
        assertThat(systemUnderTest.getLength(), is(3));
        assertThat(getNextJobIfAvailableAndSetInProgress(), isJobIndex(0));
        assertThat(systemUnderTest.getJobList(), is(mockJobs.subList(0, 3)));
    }
    
    @Test
    public void enqueueSendsMessage() {
        systemUnderTest.enqueue(getNextMockJob());      
        verify(jobMessageSender).sendEnqueueMessage();
    }
    
    @Test
    public void canGetTopJobAndSetJobStatusWhenJobIsNotAlreadyInProgress() {
        ExecutableJob mockJob = getNextMockJob();
        systemUnderTest.enqueue(mockJob);
        when(mockJob.isInProgress()).thenReturn(false);
        
        ExecutableJob job = getNextJobIfAvailableAndSetInProgress();
        
        assertThat(job, is(mockJob));
        verify(mockJob).markAsInProgress();
    }
    
    @Test
    public void getJobWhenJobAlreadyInProgress() {
        ExecutableJob mockJob = getNextMockJob();
        systemUnderTest.enqueue(mockJob);
        when(mockJob.isInProgress()).thenReturn(true);
        
        ExecutableJob job = getNextJobIfAvailableAndSetInProgress();
        
        assertThat(job, is(notNullValue()));
        assertTrue(job.isInProgress());
    }
    
    private ExecutableJob getNextJobIfAvailableAndSetInProgress() {
        ExecutableJob job = systemUnderTest.peekAtJobQueue();
        if (job != null) {
            job.markAsInProgress();
            return job;
        }
        return null;
    }

    private List<ExecutableJob> createMockJobsList(int listSize) {
        List<ExecutableJob> list = new ArrayList<ExecutableJob>(listSize);
        for (int i = 0; i < listSize; i++) {
            list.add(createMockJob());
        }
        return list;
    }

    private ExecutableJob createMockJob() {
        ExecutableJob job = mock(ExecutableJob.class);
        when(job.getOwnerName()).thenReturn("username");
        return job;
    }

    private ExecutableJob getNextMockJob() {
        return mockJobs.get(jobIndex++);
    }
    
    public org.hamcrest.Matcher<ExecutableJob> isJobIndex(int jobIndex) {
        return org.hamcrest.core.Is.is(mockJobs.get(jobIndex)); 
    }   
    
    @Test
    public void testGetJobsForUser() {
        // setup data for system under test. 
        setupDataForSystemUnderTest();
        
        // Assert that the number of jobs in the snapshot list equals number of queued jobs.
        List<Job> snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        assertEquals(systemUnderTest.getLength(), snapshotList.size());
    }
    
    @Test
    public void testCancelJob() {
        // setup data for system under test. 
        setupDataForSystemUnderTest();
        
        // Assert that the number of jobs in the snapshot list equals number of queued jobs.
        List<Job> snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        
        // Get the UUID and owner of a job to cancel. 
        Job originalJob = snapshotList.get(2);
        assertFalse(originalJob.getJobStatus().equals(JobStatus.RUNNING));
        
        UUID jobIdToCancel = originalJob.getJobId();
        String jobOwner = originalJob.getOwnerName();
        User user = mock(User.class);
        when(user.getLoginName()).thenReturn(jobOwner);
        systemUnderTest.cancelJob(jobIdToCancel.toString(), user);
        
        // Assert that the count of queued jobs is now one less than before.
        snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(4, snapshotList.size());
        assertEquals(snapshotList.size(), systemUnderTest.getLength());
        assertEquals(snapshotList.size(), systemUnderTest.getJobList().size());
        
        // Confirm that the correct job has been cancelled.
        for (Job job : snapshotList) {
            assertFalse(jobIdToCancel.equals(job.getJobId()));
        }
        
        // Confirm session management
        InOrder inorder = inOrder(fileDao, fileDao);
        inorder.verify(fileDao).flushSession();
        inorder.verify(fileDao).clearSession();
    }
    
    @Test
    public void testCancelJobForInProgressJob() {
        // setup data for system under test. 
        setupDataForSystemUnderTest();
        
        // Assert that the number of jobs in the snapshot list equals number of queued jobs.
        List<Job> snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        
        // Get the UUID and owner of a job to cancel. 
        Job originalJob = snapshotList.get(0);
        assertTrue(originalJob.getJobStatus().equals(JobStatus.RUNNING));
        
        UUID jobIdToCancel = originalJob.getJobId();
        String jobOwner = originalJob.getOwnerName();
        User user = mock(User.class);
        when(user.getLoginName()).thenReturn(jobOwner);
        systemUnderTest.cancelJob(jobIdToCancel.toString(), user);
        
        // Assert that the count of queued jobs is same as before.
        snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        assertEquals(snapshotList.size(), systemUnderTest.getLength());
        assertEquals(snapshotList.size(), systemUnderTest.getJobList().size());
    }
    
    @Test
    public void testCancelJobForWithDifferentOwner() {
        // setup data for system under test. 
        setupDataForSystemUnderTest();
        
        // Assert that the number of jobs in the snapshot list equals number of queued jobs.
        List<Job> snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        
        // Get the UUID and owner of a job to cancel. 
        Job originalJob = snapshotList.get(2);
        assertFalse(originalJob.getJobStatus().equals(JobStatus.RUNNING));
        
        UUID jobIdToCancel = originalJob.getJobId();
        User user = mock(User.class);
        when(user.getLoginName()).thenReturn("username");
        systemUnderTest.cancelJob(jobIdToCancel.toString(), user);
        
        // Assert that the count of queued jobs is same as before.
        snapshotList = systemUnderTest.getJobsForUser(mock(User.class));
        assertEquals(5, snapshotList.size());
        assertEquals(snapshotList.size(), systemUnderTest.getLength());
        assertEquals(snapshotList.size(), systemUnderTest.getJobList().size());
    }
    
    private void setupDataForSystemUnderTest() {
        List<ExecutableJob> jobs = createExecutableJobsList();
        assertEquals(5, jobs.size());
        // enqueue the jobs.
        for (ExecutableJob job : jobs) {
            systemUnderTest.enqueue(job);
        }
        
        // assert that count of queued jobs = 5.
        assertEquals(5, systemUnderTest.getLength());
        assertEquals(5, systemUnderTest.getJobList().size());
    }
    
    private List<ExecutableJob> createExecutableJobsList() {
        List<ExecutableJob> jobs = new ArrayList<ExecutableJob>();
        jobs.add(createExecutableJob("owner1", "exp1", 1, JobStatus.RUNNING, false));
        jobs.add(createExecutableJob("owner2", "exp2", 2, JobStatus.IN_QUEUE, false));
        jobs.add(createExecutableJob("owner3", "exp3", 3, JobStatus.IN_QUEUE, true));
        jobs.add(createExecutableJob("owner1", "exp4", 4, JobStatus.IN_QUEUE, true));
        jobs.add(createExecutableJob("owner1", "exp5", 5, JobStatus.IN_QUEUE, true));
        return jobs;
    }
    
    private ExecutableJob createExecutableJob(String ownerName, String jobEntityName, long jobEntityId, JobStatus jobStatus, boolean readWriteAccess) {
        ExecutableJobStub job = new ExecutableJobStub();
        job.setJobId(UUID.randomUUID());
        job.setOwnerName(ownerName);
        job.setJobEntityName(jobEntityName);
        job.setJobEntityId(jobEntityId);
        job.setJobStatus(jobStatus);
        job.setReadWriteAccess(readWriteAccess);
        return job;
    }
    
}
