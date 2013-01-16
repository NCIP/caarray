//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.jobqueue.JobQueueService;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.caarray.domain.project.JobType;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.domain.project.UserVisibleJob;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.web.AbstractBaseStrutsTest;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;


/**
 * @author kkanchinadam
 *
 */
public class ProjectJobQueueActionTest extends AbstractBaseStrutsTest {
    private ProjectJobQueueAction action;
    private JobQueueService jqService;
    private List<Job> jobList;
    private final UUID child1Id = UUID.randomUUID();
    private final UUID child2Id = UUID.randomUUID();
    private final UUID child3Id = UUID.randomUUID();

    @Before
    public void setup() throws Exception {
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        action = new ProjectJobQueueAction();
        jqService = mock(JobQueueService.class);
        locatorStub.addLookup(JobQueueService.JNDI_NAME, jqService);
        initJobList();
        when(jqService.getJobsForUser((User) anyObject())).thenReturn(jobList);
   }

    @Test
    public void testJobQueue() {
        assertEquals("success", action.jobQueue());
        assertNotNull(action.getJobs());
        assertNotNull(action.getJobs().getList());
        verify(jqService).getJobsForUser((User) anyObject());
    }

    @Test
    public void testCancelJob() {
        action.jobQueue();
        List<Job> visibleJobs = action.getJobs().getList();

        String jobIdToCancel = visibleJobs.get(1).getJobId().toString();
        when(jqService.cancelJob(eq(jobIdToCancel), (User) anyObject())).thenReturn(true);
        action.setJobId(jobIdToCancel);
        assertEquals("success", action.cancelJob());
        verify(jqService).cancelJob(eq(jobIdToCancel), (User) anyObject());
    }

    @Test
    public void testSplitJobs() {
        assertEquals("success", action.jobQueue());

        List<Job> visibleJobs = action.getJobs().getList();
        assertNotNull(visibleJobs);
        assertEquals(6, jobList.size());
        assertEquals(5, visibleJobs.size());

        UserVisibleJob splitJob = (UserVisibleJob)visibleJobs.get(4);
        Map<JobStatus, Integer> statusCounts = splitJob.getStatusCounts();
        assertEquals(5, splitJob.getPosition());
        assertEquals(JobStatus.CANCELLED, splitJob.getJobStatus());
        assertEquals(1, splitJob.getJobsProcessed());
        assertEquals(3, splitJob.getChildren().size());
        assertEquals(1, statusCounts.get(JobStatus.PROCESSED).intValue());
        assertEquals(1, statusCounts.get(JobStatus.IN_QUEUE).intValue());
        assertEquals(1, statusCounts.get(JobStatus.CANCELLED).intValue());
        assertEquals(0, statusCounts.get(JobStatus.RUNNING).intValue());
    }

    private void initJobList() {
        // parent and child1 are not in the queue
        ParentJob parent = mockParentJob();
        Job child1 = getJob(0, child1Id, "kdallas", "Split files", JobType.DATA_FILE_IMPORT, new Date(), new Date(), JobStatus.PROCESSED, parent);
        Job child2 = getJob(5, child2Id, "kdallas", "Split files", JobType.DATA_FILE_IMPORT, new Date(), new Date(), JobStatus.IN_QUEUE, parent);
        Job child3 = getJob(6, child3Id, "kdallas", "Split files", JobType.DATA_FILE_IMPORT, new Date(), new Date(), JobStatus.CANCELLED, parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        parent.getChildren().add(child3);

        jobList = new ArrayList<Job>();
        jobList.add(getJob(1, "smith", "Private", JobType.DATA_FILE_VALIDATION, new Date(), new Date(), JobStatus.RUNNING));
        jobList.add(getJob(2, "Private", "EXP-12", JobType.DATA_FILE_IMPORT, new Date(), new Date(), JobStatus.IN_QUEUE));
        jobList.add(getJob(3, "jdoe", "Private", JobType.DATA_FILE_IMPORT, new Date(), new Date(), JobStatus.IN_QUEUE));
        jobList.add(getJob(4, "jdoe", "EXP-11", JobType.DATA_FILE_VALIDATION, new Date(), new Date(), JobStatus.IN_QUEUE));
        jobList.add(child2);
        jobList.add(child3);

    }

    private Job getJob(int position, String username, String experimentName, JobType jobType, Date timeRequested,
            Date timeStarted, JobStatus status) {
        return getJob(position, UUID.randomUUID(), username, experimentName, jobType, timeRequested, timeStarted,
                status, null);
    }

    private Job getJob(int position, UUID uuid, String username, String experimentName, JobType jobType,
            Date timeRequested, Date timeStarted, JobStatus status, ParentJob parent) {
        JobStub job = new JobStub();
        job.setJobId(uuid);
        job.setPosition(position);
        job.setUsername(username);
        job.setJobEntityName(experimentName);
        job.setJobEntityId(position);
        job.setJobType(jobType);
        job.setTimeRequested(timeRequested);
        job.setTimeStarted(timeStarted);
        job.setJobStatus(status);
        job.setParent(parent);
        return job;
    }

    private ParentJob mockParentJob() {
        ParentJob job = mock(ParentJob.class);
        List<BaseJob> children = new ArrayList<BaseJob>();
        when(job.getChildren()).thenReturn(children);
        return job;
    }
}


