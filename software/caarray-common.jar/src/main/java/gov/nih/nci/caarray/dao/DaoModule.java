//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.project.DefaultJobMessageSenderImpl;
import gov.nih.nci.caarray.domain.project.JobMessageSender;
import gov.nih.nci.caarray.jobqueue.JobQueue;
import gov.nih.nci.caarray.jobqueue.JobQueueImpl;
import com.google.inject.AbstractModule;

/**
 * @author dkokotov
 *
 */
public class DaoModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(ArrayDao.class).to(ArrayDaoImpl.class);
        bind(AuditLogDao.class).to(AuditLogDaoImpl.class);
        bind(BrowseDao.class).to(BrowseDaoImpl.class);
        bind(CollaboratorGroupDao.class).to(CollaboratorGroupDaoImpl.class);
        bind(ContactDao.class).to(ContactDaoImpl.class);
        bind(FileDao.class).to(FileDaoImpl.class);
        bind(HybridizationDao.class).to(HybridizationDaoImpl.class);
        bind(ProjectDao.class).to(ProjectDaoImpl.class);
        bind(ProtocolDao.class).to(ProtocolDaoImpl.class);
        bind(SearchDao.class).to(SearchDaoImpl.class);
        bind(SampleDao.class).to(SampleDaoImpl.class);
        bind(VocabularyDao.class).to(VocabularyDaoImpl.class);
        bind(JobMessageSender.class).to(DefaultJobMessageSenderImpl.class);
        bind(JobQueue.class).to(JobQueueImpl.class);
        bind(MultipartBlobDao.class).to(MultipartBlobDaoImpl.class);
    }
}
