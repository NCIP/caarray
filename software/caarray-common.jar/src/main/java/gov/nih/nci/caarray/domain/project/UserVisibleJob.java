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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This object is a synthetic "job" used to represent the aggregate of a set of split jobs. Since job splitting should
 * be transparent to the user, this is the object represented on the UI instead of the individual split jobs
 * @author wcheng
 */
public class UserVisibleJob implements Job {
    private final UUID jobId;
    private final ParentJob parent;
    private final String ownerName;
    private final String jobEntityName;
    private final long jobEntityId;
    private final JobType jobType;
    private Date timeRequested;
    private Date timeStarted;
    private JobStatus jobStatus;
    private final boolean doesUserHaveReadAccess;
    private final boolean doesUserHaveWriteAccess;
    private final boolean doesUserHaveOwnership;
    private final int position;

    private final Map<JobStatus, Integer> statusCounts = new HashMap<JobStatus, Integer>();

    /**
     * Create an object representing the given job and all its siblings.
     * @param childJob one of the sibling jobs this object is an aggregate of
     * @param position the position of the job in the job queue at the time this snapshot is created
     */
    public UserVisibleJob(Job childJob, int position) {
        jobId = childJob.getJobId();
        parent = childJob.getParent();
        ownerName = childJob.getOwnerName();
        jobEntityName = childJob.getJobEntityName();
        jobEntityId = childJob.getJobEntityId();
        jobType = childJob.getJobType();
        doesUserHaveReadAccess = childJob.getUserHasReadAccess();
        doesUserHaveWriteAccess = childJob.getUserHasWriteAccess();
        doesUserHaveOwnership = childJob.getUserHasOwnership();
        timeRequested = childJob.getTimeRequested();
        timeStarted = childJob.getTimeStarted();
        jobStatus = childJob.getJobStatus();
        this.position = position;

        if (parent != null) {
            setJobStatusFromChildren();
            setTimeRequestedFromChildren();
            setTimeStartedFromChildren();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParentJob getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BaseJob> getChildren() {
        return parent.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getJobId() {
        return jobId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobEntityName() {
        return jobEntityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getJobEntityId() {
        return jobEntityId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobType getJobType() {
        return jobType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimeRequested() {
        return timeRequested;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getTimeStarted() {
        return timeStarted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobStatus getJobStatus() {
        return jobStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInProgress() {
        return jobStatus.equals(JobStatus.RUNNING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasReadAccess() {
        return doesUserHaveReadAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasWriteAccess() {
        return doesUserHaveWriteAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUserHasOwnership() {
        return doesUserHaveOwnership;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPosition() {
        return position;
    }

    /**
     * A user can only cancel a job which is in_queue, or one of its children is in_queue.
     * @return true if the user can cancel this job
     */
    public boolean getUserCanCancelJob() {
        if (!getUserHasOwnership()) { return false; }
        if (getParent() == null) {
            return JobStatus.IN_QUEUE.equals(getJobStatus());
        } else {
            boolean canCancel = false;
            for (BaseJob job : getChildren()) {
                if (JobStatus.IN_QUEUE.equals(job.getJobStatus())) {
                    canCancel = true;
                    break;
                }
            }
            return canCancel;
        }
    }

    /**
     * @return a map of the number of jobs by status
     */
    public Map<JobStatus, Integer> getStatusCounts() {
        return statusCounts;
    }

    /**
     * @return the number of jobs processed
     */
    public int getJobsProcessed() {
        Integer count = statusCounts.get(JobStatus.PROCESSED);
        return count == null ? 0 : count;
    }

    /**
     * Sets the job status based on the statuses of the children.
     */
    private void setJobStatusFromChildren() {
        for (JobStatus cStatus : JobStatus.values()) {
            statusCounts.put(cStatus, 0);
        }
        for (BaseJob job : parent.getChildren()) {
            JobStatus cStatus = job.getJobStatus();
            statusCounts.put(cStatus, statusCounts.get(cStatus) + 1);
        }
        if (statusCounts.get(JobStatus.CANCELLED) > 0) {
            jobStatus = JobStatus.CANCELLED;
        } else if (statusCounts.get(JobStatus.RUNNING) > 0) {
            jobStatus = JobStatus.RUNNING;
        } else if (statusCounts.get(JobStatus.IN_QUEUE) > 0) {
            jobStatus = JobStatus.IN_QUEUE;
        } else {
            jobStatus = JobStatus.PROCESSED;
        }
    }

    /**
     * Sets time requested to the earliest child.
     */
    private void setTimeRequestedFromChildren() {
        for (BaseJob job : parent.getChildren()) {
            Date cTimeRequested = job.getTimeRequested();
            if (cTimeRequested.before(timeRequested)) {
                timeRequested = cTimeRequested;
            }
        }
    }

    /**
     * Sets time started to the earliest child.
     */
    private void setTimeStartedFromChildren() {
        for (BaseJob job : parent.getChildren()) {
            Date cTimeStarted = job.getTimeStarted();
            if (cTimeStarted != null && cTimeStarted.before(timeStarted)) {
                timeStarted = cTimeStarted;
            }
        }
    }
}
