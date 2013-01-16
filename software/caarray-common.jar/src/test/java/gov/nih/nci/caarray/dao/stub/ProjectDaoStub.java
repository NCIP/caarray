//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 *
 */
public class ProjectDaoStub extends AbstractDaoStub implements ProjectDao {
    /**
     * {@inheritDoc}
     */
    public int getProjectCountForCurrentUser() {
        return 0;
    }

    public Project getProjectByPublicId(String publicId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsForCurrentUser(PageSortParams<Project> pageSortParams) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsForOwner(User user) {
        return Collections.EMPTY_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> searchByCategory(PageSortParams params, String keyword, SearchCategory... categories) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public int searchCount(String keyword, SearchCategory... categories) {
        return 0;
    }
    /**
     * {@inheritDoc}
     */
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<AbstractCharacteristic> getArbitraryCharacteristicsForExperimentSamples(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getArbitraryCharacteristicsCategoriesForExperimentSamples(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Set<AbstractBioMaterial> getUnfilteredBiomaterialsForProject(Long projectId) {
        return new HashSet<AbstractBioMaterial>();
    }

    /**
     * {@inheritDoc}
     */
    public Source getSourceForExperiment(Experiment experiment, String sourceName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Sample getSampleForExperiment(Experiment experiment, String sampleName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Extract getExtractForExperiment(Experiment experiment, String extractName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public LabeledExtract getLabeledExtractForExperiment(Experiment experiment, String labeledExtractName) {
        return null;
    }

    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    public List<AssayType> getAssayTypes() {
        return null;
    }

    public List<Project> getProjectsWithReImportable() {
        return new ArrayList<Project>();
    }
}
