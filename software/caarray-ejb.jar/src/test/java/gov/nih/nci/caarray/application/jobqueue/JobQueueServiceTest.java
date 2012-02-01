/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.application.jobqueue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.ExecutableJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.JobSnapshot;
import gov.nih.nci.caarray.domain.project.JobStatus;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

@SuppressWarnings("PMD")
public class JobQueueServiceTest {
    private final UUID JOB_ID_1 = UUID.randomUUID();
    private final UUID JOB_ID_2 = UUID.randomUUID();
    private final UUID CHILD_ID_1 = UUID.randomUUID();
    private final UUID CHILD_ID_2 = UUID.randomUUID();
    private final UUID CHILD_ID_3 = UUID.randomUUID();
    private JobQueueService bean;
    private JobQueue jobQueue;
    private List<Job> jobs;
    private User user;

    @Before
    public void setUp() {
        jobQueue = mock(JobQueue.class);
        user = mock(User.class);
        bean = new JobQueueServiceBean(jobQueue);

        final Module testModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(JobQueue.class).toInstance(jobQueue);
                bind(JobQueueService.class).toInstance(bean);
            }
        };

        final Injector injector = Guice.createInjector(testModule);
        // setup data.
        jobs = getJobs();
    }

    @Test
    public void testGetJobCount() {
        when(jobQueue.getJobsForUser(user)).thenReturn(jobs);
        assertEquals(7, bean.getJobCount(user));
    }

    @Test
    public void testGetJobsForUser() {
        when(jobQueue.getJobsForUser(user)).thenReturn(jobs);
        assertEquals(7, bean.getJobCount(user));
        assertEquals(jobs, bean.getJobsForUser(user));
    }

    @Test
    public void testCancelJob() {
        when(jobQueue.getJobsForUser(user)).thenReturn(jobs);
        when(jobQueue.cancelJob(JOB_ID_1.toString(), user)).thenReturn(false);
        when(jobQueue.cancelJob(JOB_ID_2.toString(), user)).thenReturn(true);
        when(jobQueue.cancelJob(CHILD_ID_1.toString(), user)).thenReturn(false);
        when(jobQueue.cancelJob(CHILD_ID_2.toString(), user)).thenReturn(true);
        when(jobQueue.cancelJob(CHILD_ID_3.toString(), user)).thenReturn(true);

        assertEquals(false, bean.cancelJob(JOB_ID_1.toString(), user));
        verify(jobQueue).cancelJob(JOB_ID_1.toString(), user);

        assertEquals(true, bean.cancelJob(JOB_ID_2.toString(), user));
        verify(jobQueue).cancelJob(JOB_ID_2.toString(), user);

        assertEquals(true, bean.cancelJob(CHILD_ID_2.toString(), user));
        verify(jobQueue).cancelJob(CHILD_ID_1.toString(), user);
        verify(jobQueue).cancelJob(CHILD_ID_2.toString(), user);
        verify(jobQueue).cancelJob(CHILD_ID_3.toString(), user);
    }

    private List<Job> getJobs() {
        ParentJob parent = mockParentJob();
        ExecutableJob child1 = mockExecutableJob(CHILD_ID_1, "owner1", "exp6", 6, JobStatus.PROCESSED, true, parent);
        ExecutableJob child2 = mockExecutableJob(CHILD_ID_2, "owner1", "exp6", 6, JobStatus.IN_QUEUE, true, parent);
        ExecutableJob child3 = mockExecutableJob(CHILD_ID_3, "owner1", "exp6", 6, JobStatus.IN_QUEUE, true, parent);
        parent.getChildren().add(child1);
        parent.getChildren().add(child2);
        parent.getChildren().add(child3);

        List<ExecutableJob> originalJobs = new ArrayList<ExecutableJob>();
        originalJobs.add(mockExecutableJob(JOB_ID_1, "owner1", "exp1", 1, JobStatus.RUNNING, false, null));
        originalJobs.add(mockExecutableJob(JOB_ID_2, "owner2", "exp2", 2, JobStatus.IN_QUEUE, false, null));
        originalJobs.add(mockExecutableJob(UUID.randomUUID(), "owner3", "exp3", 3, JobStatus.IN_QUEUE, true, null));
        originalJobs.add(mockExecutableJob(UUID.randomUUID(), "owner1", "exp4", 4, JobStatus.IN_QUEUE, true, null));
        originalJobs.add(mockExecutableJob(UUID.randomUUID(), "owner1", "exp5", 5, JobStatus.IN_QUEUE, true, null));
        originalJobs.add(child2);
        originalJobs.add(child3);

        List<Job> snapshotList = new ArrayList<Job>();
        int position = 1;
        for (ExecutableJob originalJob : originalJobs) {
            snapshotList.add(new JobSnapshot(user, originalJob, position++));
        }

        return snapshotList;
    }

    private ExecutableJob mockExecutableJob(UUID uuid, String ownerName, String jobEntityName, long jobEntityId,
            JobStatus jobStatus, boolean readWriteAccess, ParentJob parent) {
        ExecutableJob job = mock(ExecutableJob.class);
        when(job.getJobId()).thenReturn(uuid);
        when(job.getOwnerName()).thenReturn(ownerName);
        when(job.getJobEntityName()).thenReturn(jobEntityName);
        when(job.getJobEntityId()).thenReturn(jobEntityId);
        when(job.getJobStatus()).thenReturn(jobStatus);
        when(job.userHasReadAccess(user)).thenReturn(readWriteAccess);
        when(job.userHasWriteAccess(user)).thenReturn(readWriteAccess);
        when(job.getParent()).thenReturn(parent);
        return job;
    }

    private ParentJob mockParentJob() {
        ParentJob job = mock(ParentJob.class);
        List<BaseJob> children = new ArrayList<BaseJob>();
        when(job.getChildren()).thenReturn(children);
        return job;
    }
}
