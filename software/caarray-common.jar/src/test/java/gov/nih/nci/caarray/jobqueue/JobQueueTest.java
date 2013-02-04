//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
