//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.jobqueue;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.domain.project.BaseJob;
import gov.nih.nci.caarray.domain.project.Job;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Implementation entry point.
 */
@Local(JobQueueService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JobQueueServiceBean implements JobQueueService {
    private static final Logger LOG = Logger.getLogger(JobQueueServiceBean.class);
    private JobQueue jobQueue;

    /**
     * No-arg ctor required for all EJB Beans. Dependencies should be added through the setters.
     */
    public JobQueueServiceBean() {
        super();
    }

    /**
     * Set the JobQueue to use.
     *
     * @param jobQueue the jobQueue dependency
     */
    @Inject
    public void setJobQueue(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    /**
     * STARTING WITH caArray 2.5.0 THIS VERSION OF THE CTOR IS A CONVENIENCE FOR TESTING USE ONLY.
     *
     * @param jobQueue the jobQueue dependency
     */
    @Inject
    JobQueueServiceBean(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Job> getJobsForUser(User user) {
        LogUtil.logSubsystemEntry(LOG);
        final List<Job> result = this.jobQueue.getJobsForUser(user);
        LogUtil.logSubsystemExit(LOG);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getJobCount(User user) {
        return this.jobQueue.getJobsForUser(user).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean cancelJob(String jobId, User user) {
        List<Job> jobsList = getJobsForUser(user);
        List<BaseJob> jobsToCancel = new ArrayList<BaseJob>();
        for (Job job : jobsList) {
            if (job.getJobId().equals(UUID.fromString(jobId))) {
                if (job.getParent() == null) {
                    jobsToCancel.add(job);
                } else {
                    jobsToCancel.addAll(job.getParent().getChildren());
                }
                break;
            }
        }
        boolean cancelledJob = false;
        for (BaseJob job : jobsToCancel) {
            if (jobQueue.cancelJob(job.getJobId().toString(), user)) {
               cancelledJob = true;
            }
        }

        return cancelledJob;
    }

}
