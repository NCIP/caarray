/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
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
