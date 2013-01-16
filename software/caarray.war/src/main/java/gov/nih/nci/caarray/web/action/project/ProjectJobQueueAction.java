//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.jobqueue.JobQueueService;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.domain.project.ParentJob;
import gov.nih.nci.caarray.domain.project.UserVisibleJob;
import gov.nih.nci.caarray.domain.search.JobSortCriterion;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.displaytag.SortablePaginatedList;
import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Class to handle the job queue pages.
 *
 * @author Krishna Kanchinadam
 */
public class ProjectJobQueueAction extends ActionSupport {
    private static final int PAGE_SIZE = 20;
    private String jobId;

    private final SortablePaginatedList<Job, JobSortCriterion> jobs =
            new SortablePaginatedList<Job, JobSortCriterion>(PAGE_SIZE, JobSortCriterion.POSITION.name(),
                    JobSortCriterion.class);

    /**
     * Renders the job queue page.
     *
     * @return path String
     */
    @SkipValidation
    public String jobQueue() {
        JobQueueService jobQueueService = ServiceLocatorFactory.getJobQueueService();
        User user = CaArrayUsernameHolder.getCsmUser();
        List<Job> jobsList = jobQueueService.getJobsForUser(user);
        List<Job> userVisibleJobs = getUserVisibleJobs(jobsList);
        this.jobs.setList(userVisibleJobs);
        this.jobs.setFullListSize(userVisibleJobs.size());
        return Action.SUCCESS;
    }

    private List<Job> getUserVisibleJobs(List<Job> jobsList) {
        Set<ParentJob> parents = new HashSet<ParentJob>();
        List<Job> visibleJobs = new ArrayList<Job>();
        int position = 1;
        for (Job job : jobsList) {
            ParentJob parent = job.getParent();
            if (parent == null) {
                visibleJobs.add(new UserVisibleJob(job, position++));
            } else if (!parents.contains(parent)) {
                parents.add(parent);
                visibleJobs.add(new UserVisibleJob(job, position++));
            }
        }
        return visibleJobs;
    }

    /**
     * @return the jobs
     */
    public SortablePaginatedList<Job, JobSortCriterion> getJobs() {
        return jobs;
    }

    /**
     * Cancels the job.
     * @return a boolean value that indicates if the job was cancelled or not.
     */
    public String cancelJob() {
        if (!ServiceLocatorFactory.getJobQueueService().cancelJob(jobId, CaArrayUsernameHolder.getCsmUser())) {
            ActionHelper.saveMessage(getText("jobQueue.cancel.unableToCancel"));
        } else {
            JobQueueService jobQueueService = ServiceLocatorFactory.getJobQueueService();
            List<Job> jobsList = jobQueueService.getJobsForUser(CaArrayUsernameHolder.getCsmUser());
            for (Job job : jobsList) {
                if (job.getJobId().equals(UUID.fromString(jobId))) {
                    ActionHelper.saveMessage(getText("jobQueue.cancel.partialCancel"));
                }
            }
        }
        return Action.SUCCESS;
    }

    /**
     * Sets the job id.
     * @param jobId the job id to set.
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
