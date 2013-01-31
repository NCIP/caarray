//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.BrowseDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.CollaboratorGroupDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.CountryDao;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dao.OrganismDao;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.dao.ProtocolDao;
import gov.nih.nci.caarray.dao.SampleDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.StateDao;
import gov.nih.nci.caarray.dao.VocabularyDao;

/**
 * Base adapter for DAO Stubs.
 */
public class DaoFactoryStub implements CaArrayDaoFactory {

    /**
     * {@inheritDoc}
     */
    public ArrayDao getArrayDao() {
        return new ArrayDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public ProjectDao getProjectDao() {
        return new ProjectDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public ProtocolDao getProtocolDao() {
        return new ProtocolDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public SearchDao getSearchDao() {
        return new SearchDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public VocabularyDao getVocabularyDao() {
        return new VocabularyDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public FileDao getFileDao() {
        return new FileDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public ContactDao getContactDao() {
        return new ContactDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public OrganismDao getOrganismDao() {
        return new OrganismDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public CountryDao getCountryDao() {
        return new CountryDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public CollaboratorGroupDao getCollaboratorGroupDao() {
        return new CollaboratorGroupDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public StateDao getStateDao()
    {
        return new StateDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public BrowseDao getBrowseDao() {
        return new BrowseDaoStub();
    }

    /**
     * {@inheritDoc}
     */
    public SampleDao getSampleDao() {
        return new SampleDaoStub();
    }
}
