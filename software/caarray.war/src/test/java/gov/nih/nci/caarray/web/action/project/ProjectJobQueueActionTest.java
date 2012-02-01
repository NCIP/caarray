/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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


