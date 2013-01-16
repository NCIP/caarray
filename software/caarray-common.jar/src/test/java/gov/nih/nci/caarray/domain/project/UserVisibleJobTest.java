//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author wcheng
 *
 */
public class UserVisibleJobTest {
    private final UUID JOB_ID_1 = UUID.randomUUID();
    private final UUID JOB_ID_2 = UUID.randomUUID();
    private final UUID JOB_ID_3 = UUID.randomUUID();
    private final Date REQUESTED = new Date();
    private final Date STARTED = new Date();

    @Test
    public void jobWithoutParent() {
        Job originalJob = mockJob(1, JOB_ID_1, "smith", "EXP-1", JobType.DATA_FILE_IMPORT, true, true, true, REQUESTED,
                STARTED, JobStatus.IN_QUEUE, 2, null);
        UserVisibleJob job = new UserVisibleJob(originalJob, 1);
        assertEquals(JOB_ID_1, job.getJobId());
        assertEquals(1, job.getJobEntityId());
        assertEquals("smith", job.getOwnerName());
        assertEquals("EXP-1", job.getJobEntityName());
        assertEquals(JobType.DATA_FILE_IMPORT, job.getJobType());
        assertTrue(job.getUserHasReadAccess());
        assertTrue(job.getUserHasWriteAccess());
        assertTrue(job.getUserHasOwnership());
        assertTrue(job.getUserCanCancelJob());
        assertEquals(JobStatus.IN_QUEUE, job.getJobStatus());
        assertFalse(job.isInProgress());
        assertEquals(1, job.getPosition());
        assertNull(job.getParent());
    }

    @Test
    public void jobWithParent() {
        long now = (new Date()).getTime();
        Date date1 = new Date(now+100);
        Date date2 = new Date(now+200);
        Date date3 = new Date(now+300);
        ParentJob parent = mockParentJob();
        Job child1 = mockJob(2, JOB_ID_1, "smith", "EXP-1", JobType.DATA_FILE_IMPORT, true, true, true,
                date1, date3, JobStatus.PROCESSED, 2, parent);
        Job child2 = mockJob(3, JOB_ID_2, "smith", "EXP-1", JobType.DATA_FILE_IMPORT, true, true, true,
                date2, date2, JobStatus.IN_QUEUE, 3, parent);
        Job child3 = mockJob(4, JOB_ID_3, "smith", "EXP-1", JobType.DATA_FILE_IMPORT, true, true, true,
                date3, date1, JobStatus.CANCELLED, 4, parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        parent.getChildren().add(child3);

        UserVisibleJob job = new UserVisibleJob(child2, 1);
        assertEquals(JOB_ID_2, job.getJobId());
        assertEquals(JobStatus.CANCELLED, job.getJobStatus());
        assertEquals(date1.getTime(), job.getTimeRequested().getTime());
        assertEquals(date1.getTime(), job.getTimeStarted().getTime());
        assertTrue(job.getUserCanCancelJob());
        assertEquals(1, job.getJobsProcessed());
        Map<JobStatus, Integer> statusCounts = job.getStatusCounts();
        assertEquals(1, statusCounts.get(JobStatus.PROCESSED).intValue());
        assertEquals(1, statusCounts.get(JobStatus.IN_QUEUE).intValue());
        assertEquals(1, statusCounts.get(JobStatus.CANCELLED).intValue());
        assertEquals(0, statusCounts.get(JobStatus.RUNNING).intValue());
    }

    @Test
    public void testTimes() {
        long now = (new Date()).getTime();
        Date date1 = new Date(now+100);
        Date date2 = new Date(now+200);
        Date date3 = new Date(now+300);

        ParentJob parent = mockParentJob();
        Job child1 = getJobWithTimes(date1, date3, parent);
        Job child2 = getJobWithTimes(date2, date2, parent);
        Job child3 = getJobWithTimes(date3, date1, parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        parent.getChildren().add(child3);

        UserVisibleJob job = new UserVisibleJob(child2, 1);
        assertEquals(date1.getTime(), job.getTimeRequested().getTime());
        assertEquals(date1.getTime(), job.getTimeStarted().getTime());
    }

    @Test
    public void testCanCancel() {
        // no ownership
        Job notOwner = getJobWithStatus(false, JobStatus.IN_QUEUE, null);
        UserVisibleJob notOwner1 = new UserVisibleJob(notOwner, 1);
        Assert.assertFalse(notOwner1.getUserCanCancelJob());

        // no parent
        Job noParent = getJobWithStatus(true, JobStatus.IN_QUEUE, null);
        UserVisibleJob noParent1 = new UserVisibleJob(noParent, 1);
        Assert.assertTrue(noParent1.getUserCanCancelJob());

        ParentJob parent = mockParentJob();
        Job childRunning = getJobWithStatus(true, JobStatus.RUNNING, parent);
        Job childInQueue = getJobWithStatus(true, JobStatus.IN_QUEUE, parent);
        Job childCancelled = getJobWithStatus(true, JobStatus.CANCELLED, parent);
        UserVisibleJob job = new UserVisibleJob(childRunning, 1);

        // no children in_queue
        parent.getChildren().add(childRunning);
        job = new UserVisibleJob(childRunning, 1);
        assertFalse(job.getUserCanCancelJob());

        parent.getChildren().add(childCancelled);
        job = new UserVisibleJob(childRunning, 1);
        assertFalse(job.getUserCanCancelJob());

        // one child in_queue
        parent.getChildren().add(childInQueue);
        job = new UserVisibleJob(childRunning, 1);
        assertTrue(job.getUserCanCancelJob());
    }

    @Test
    public void testJobStatus() {
        ParentJob parent = mockParentJob();
        Job childRunning = getJobWithStatus(true, JobStatus.RUNNING, parent);
        Job childInQueue = getJobWithStatus(true, JobStatus.IN_QUEUE, parent);
        Job childCancelled = getJobWithStatus(true, JobStatus.CANCELLED, parent);
        Job childProcessed = getJobWithStatus(true, JobStatus.PROCESSED, parent);
        UserVisibleJob job = new UserVisibleJob(childProcessed, 1);

        parent.getChildren().add(childProcessed);
        assertEquals(JobStatus.PROCESSED, job.getJobStatus());

        parent.getChildren().add(childInQueue);
        job = new UserVisibleJob(childProcessed, 1);
        assertEquals(JobStatus.IN_QUEUE, job.getJobStatus());

        parent.getChildren().add(childRunning);
        job = new UserVisibleJob(childProcessed, 1);
        assertEquals(JobStatus.RUNNING, job.getJobStatus());

        parent.getChildren().add(childCancelled);
        job = new UserVisibleJob(childProcessed, 1);
        assertEquals(JobStatus.CANCELLED, job.getJobStatus());
    }

    private Job getJobWithStatus(boolean ownership, JobStatus jobStatus, ParentJob parent) {
        Job job = mock(Job.class);
        when(job.getParent()).thenReturn(parent);
        when(job.getUserHasOwnership()).thenReturn(ownership);
        when(job.getTimeRequested()).thenReturn(REQUESTED);
        when(job.getTimeStarted()).thenReturn(STARTED);
        when(job.getJobStatus()).thenReturn(jobStatus);
        return job;
    }

    private Job getJobWithTimes(Date timeRequested, Date timeStarted, ParentJob parent) {
        Job job = mock(Job.class);
        when(job.getParent()).thenReturn(parent);
        when(job.getTimeRequested()).thenReturn(timeRequested);
        when(job.getTimeStarted()).thenReturn(timeStarted);
        when(job.getJobStatus()).thenReturn(JobStatus.IN_QUEUE);
        return job;
    }

    private Job mockJob(long jobEntityId, UUID uuid, String username, String experimentName, JobType jobType,
            boolean readAccess, boolean writeAccess, boolean ownership, Date timeRequested, Date timeStarted,
            JobStatus status, int position, ParentJob parent) {
        Job job = mock(Job.class);
        when(job.getJobId()).thenReturn(uuid);
        when(job.getParent()).thenReturn(parent);
        when(job.getOwnerName()).thenReturn(username);
        when(job.getJobEntityName()).thenReturn(experimentName);
        when(job.getJobEntityId()).thenReturn(jobEntityId);
        when(job.getJobType()).thenReturn(jobType);
        when(job.getUserHasReadAccess()).thenReturn(readAccess);
        when(job.getUserHasWriteAccess()).thenReturn(writeAccess);
        when(job.getUserHasOwnership()).thenReturn(ownership);
        when(job.getTimeRequested()).thenReturn(timeRequested);
        when(job.getTimeStarted()).thenReturn(timeStarted);
        when(job.getJobStatus()).thenReturn(status);
        when(job.getPosition()).thenReturn(position);
        return job;
    }

    private ParentJob mockParentJob() {
        ParentJob job = mock(ParentJob.class);
        List<BaseJob> children = new ArrayList<BaseJob>();
        when(job.getChildren()).thenReturn(children);
        return job;
    }
}
