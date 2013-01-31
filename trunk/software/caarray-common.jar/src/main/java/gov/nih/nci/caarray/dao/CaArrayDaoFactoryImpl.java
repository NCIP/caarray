//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

/**
 * Factory used to retrieve DAO instances.
 *
 * @author ETavela
 */
class CaArrayDaoFactoryImpl implements CaArrayDaoFactory {

    /**
     * Returns a <code>ProtocolDao</code>.
     *
     * @return a <code>ProtocolDao</code>.
     */
    public ProtocolDao getProtocolDao() {
        return new ProtocolDaoImpl();
    }

    /**
     * Returns a <code>VocabularyDao</code>.
     *
     * @return a <code>VocabularyDao</code>.
     */
    public VocabularyDao getVocabularyDao() {
        return new VocabularyDaoImpl();
    }

    /**
     * Returns an <code>ArrayDao</code>.
     *
     * @return an <code>ArrayDao</code>.
     */
    public ArrayDao getArrayDao() {
        return new ArrayDaoImpl();
    }

    /**
     * Returns a <code>ProjectDao</code>.
     *
     * @return a <code>ProjectDao</code>.
     */
    public ProjectDao getProjectDao() {
        return new ProjectDaoImpl();
    }

    /**
     * Returns a <code>SearchDao</code>.
     *
     * @return a <code>SearchDao</code>.
     */
    public SearchDao getSearchDao() {
        return new SearchDaoImpl();
    }

    /**
     * Returns a <code>FileDao</code>.
     *
     * @return a <code>FileDao</code>.
     */
    public FileDao getFileDao() {
        return new FileDaoImpl();
    }

    /**
     * Returns a <code>ContactDao</code>.
     *
     * @return a <code>ContactDao</code>.
     */
    public ContactDao getContactDao() {
        return new ContactDaoImpl();
    }
    /**
     * Returns a <code>OrganismDao</code>.
     *
     * @return a <code>OrganismDao</code>.
     */
    public OrganismDao getOrganismDao() {
        return new OrganismDaoImpl();
    }

    /**
     * {@inheritDoc}
     */
    public CollaboratorGroupDao getCollaboratorGroupDao() {
        return new CollaboratorGroupDaoImpl();
    }

    /**
     * {@inheritDoc}
     */
    public BrowseDao getBrowseDao() {
        return new BrowseDaoImpl();
    }

    /**
     * {@inheritDoc}
     */
    public SampleDao getSampleDao() {
        return new SampleDaoImpl();
    }
    
    /**
     * {@inheritDoc}
     */
    public HybridizationDao getHybridizationDao() {
        return new HybridizationDaoImpl();
    }

    /**
     * {@inheritDoc}
     */
    public AuditLogDao getAuditLogDao() {
        return new AuditLogDaoImpl();
    }
}
