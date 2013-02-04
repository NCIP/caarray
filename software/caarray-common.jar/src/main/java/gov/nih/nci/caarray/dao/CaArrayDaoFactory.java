//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

/**
 * Factory interface used to retrieve DAO instances.
 *
 * @author ETavela
 */
public interface CaArrayDaoFactory {

    /**
     * Factory instance for clients.
     */
    CaArrayDaoFactory INSTANCE = new CaArrayDaoFactoryImpl();

    /**
     * Returns a <code>ProtocolDao</code>.
     *
     * @return a <code>ProtocolDao</code>.
     */
    ProtocolDao getProtocolDao();

    /**
     * Returns a <code>VocabularyDao</code>.
     *
     * @return a <code>VocabularyDao</code>.
     */
    VocabularyDao getVocabularyDao();

    /**
     * Returns an <code>ArrayDao</code>.
     *
     * @return an <code>ArrayDao</code>.
     */
    ArrayDao getArrayDao();

    /**
     * Returns a <code>ProjectDao</code>.
     *
     * @return a <code>ProjectDao</code>.
     */
    ProjectDao getProjectDao();

    /**
     * Returns a <code>SearchDao</code>.
     *
     * @return a <code>SearchDao</code>.
     */
    SearchDao getSearchDao();

    /**
     * Returns a <code>FileDao</code>.
     *
     * @return a <code>FileDao</code>.
     */
    FileDao getFileDao();

    /**
     * Returns a <code>ContactDao</code>.
     *
     * @return a <code>ContactDao</code>.
     */
    ContactDao getContactDao();

    /**
     * @return a <code>CollaboratorGroupDao</code>.
     */
    CollaboratorGroupDao getCollaboratorGroupDao();
    
    /**
     * @return a <code>BrowseDao</code>.
     */
    BrowseDao getBrowseDao();

    /**
     * @return a <code>SampleDao</code>.
     */
    SampleDao getSampleDao();

    /**
     * @return a <code>HybridizationDao</code>.
     */
    HybridizationDao getHybridizationDao();

    /**
     * @return a <code>AuditLogDao</code>.
     * @see AuditLogDao
     * @see AuditLogDaoImpl
     */
    AuditLogDao getAuditLogDao();
}
