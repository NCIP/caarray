//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import com.google.inject.Inject;

/**
 * Factory used to retrieve DAO instances.
 * 
 * @author ETavela
 */
public class CaArrayDaoFactoryImpl implements CaArrayDaoFactory {
    @Inject
    private static CaArrayHibernateHelper hibernateHelper;
    @Inject
    private static FileTypeRegistry typeRegistry;

    /**
     * Returns a <code>ProtocolDao</code>.
     * 
     * @return a <code>ProtocolDao</code>.
     */
    @Override
    public ProtocolDao getProtocolDao() {
        return new ProtocolDaoImpl(hibernateHelper);
    }

    /**
     * Returns a <code>VocabularyDao</code>.
     * 
     * @return a <code>VocabularyDao</code>.
     */
    @Override
    public VocabularyDao getVocabularyDao() {
        return new VocabularyDaoImpl(hibernateHelper);
    }

    /**
     * Returns an <code>ArrayDao</code>.
     * 
     * @return an <code>ArrayDao</code>.
     */
    @Override
    public ArrayDao getArrayDao() {
        return new ArrayDaoImpl(this.getSearchDao(), hibernateHelper, typeRegistry);
    }

    /**
     * Returns a <code>ProjectDao</code>.
     * 
     * @return a <code>ProjectDao</code>.
     */
    @Override
    public ProjectDao getProjectDao() {
        return new ProjectDaoImpl(hibernateHelper, typeRegistry);
    }

    /**
     * Returns a <code>SearchDao</code>.
     * 
     * @return a <code>SearchDao</code>.
     */
    @Override
    public SearchDao getSearchDao() {
        return new SearchDaoImpl(hibernateHelper);
    }

    /**
     * Returns a <code>FileDao</code>.
     * 
     * @return a <code>FileDao</code>.
     */
    @Override
    public FileDao getFileDao() {
        return new FileDaoImpl(hibernateHelper, typeRegistry);
    }

    /**
     * Returns a <code>ContactDao</code>.
     * 
     * @return a <code>ContactDao</code>.
     */
    @Override
    public ContactDao getContactDao() {
        return new ContactDaoImpl(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollaboratorGroupDao getCollaboratorGroupDao() {
        return new CollaboratorGroupDaoImpl(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BrowseDao getBrowseDao() {
        return new BrowseDaoImpl(this.getSearchDao(), hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SampleDao getSampleDao() {
        return new SampleDaoImpl(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HybridizationDao getHybridizationDao() {
        return new HybridizationDaoImpl(hibernateHelper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditLogDao getAuditLogDao() {
        return new AuditLogDaoImpl(hibernateHelper);
    }
}
