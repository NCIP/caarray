//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao.stub;

import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;

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
    public int getProjectCountForCurrentUser(boolean showPublic) {
        return 0;
    }

    public Project getProjectByPublicId(String publicId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Project> getProjectsForCurrentUser(boolean showPublic, PageSortParams<Project> pageSortParams) {
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

    public Set<Sample> getUnfilteredSamplesForProject(Project project) {
        return new HashSet<Sample>();
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

}
