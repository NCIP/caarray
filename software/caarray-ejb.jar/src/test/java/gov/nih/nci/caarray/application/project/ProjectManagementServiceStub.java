//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.project;

import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.BiomaterialSearchCategory;
import gov.nih.nci.caarray.domain.search.ExperimentSearchCriteria;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;
import gov.nih.nci.caarray.domain.search.SearchCategory;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.security.authorization.domainobjects.User;
import gov.nih.nci.security.exceptions.CSException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * Basic stub -- returns null for all methods returning objects. Subclass and override to provide desired functionality
 * in tests.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ProjectManagementServiceStub implements ProjectManagementService {

    private int filesAddedCount = 0;
    private int changeWorkflowStatusCount = 0;

    public void reset() {
        this.filesAddedCount = 0;
        this.changeWorkflowStatusCount = 0;
    }

    /**
     * @return the filesAdded
     */
    public int getFilesAddedCount() {
        return this.filesAddedCount;
    }

    /**
     * @return get the number of times change workflow status has been called.
     */
    public int getChangeWorkflowStatusCount() {
        return this.changeWorkflowStatusCount;
    }

    public Set<CaArrayFile> addFiles(Project project, Set<File> files) {
        return null;
    }

    public CaArrayFile addFileChunk(Project project, File file, String filename, long fileSize)
            throws ProposalWorkflowException, InconsistentProjectStateException {
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMyProjectCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getMyProjects(PageSortParams<Project> pageSortParams) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> getProjectsForOwner(User user) {
        return Collections.emptyList();
    }

    @Override
    public Project getProjectByPublicId(String publicId) {
        final Project p = new Project();
        p.getExperiment().setPublicIdentifier(publicId);
        return p;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteProject(Project project) throws ProposalWorkflowException {
        // no-op
    }

    public Organization getOrganization(long id) {
        return null;
    }

    public void submitProject(Project project) {
        // no-op
    }

    @Override
    public void saveProject(Project project, PersistentObject... orphans) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        // no-op
    }

    @Override
    public CaArrayFile addFile(Project project, File file) {
        return null;
    }

    @Override
    public CaArrayFile addFile(Project project, File file, String filename) {
        this.filesAddedCount++;
        return null;
    }

    @Override
    public CaArrayFile addFile(Project project, InputStream data, String filename) throws ProposalWorkflowException,
            InconsistentProjectStateException {
        this.filesAddedCount++;
        return null;
    }

    public File prepareForDownload(Collection<CaArrayFile> ids) {
        try {
            return File.createTempFile("tmp", ".zip");
        } catch (final IOException e) {
            return null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Factor copyFactor(Project project, long factorId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sample copySample(Project project, long sampleId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Source copySource(Project project, long sourceId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AccessProfile addGroupProfile(Project project, CollaboratorGroup group) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeProjectLockStatus(long projectId, boolean newStatus) throws ProposalWorkflowException {
        this.changeWorkflowStatusCount++;
        if (projectId == 999) {
            throw new ProposalWorkflowException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Project> searchByCategory(PageSortParams<Project> params, String keyword, SearchCategory... categories) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchCount(String keyword, SearchCategory... categories) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public File prepareHybsForDownload(Project p, Collection<Hybridization> hybridizations) throws IOException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Extract copyExtract(Project project, long extractId) throws ProposalWorkflowException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LabeledExtract copyLabeledExtract(Project project, long extractId) throws ProposalWorkflowException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public List<Term> getCellTypesForExperiment(Experiment experiment) {
        final List<Term> terms = new ArrayList<Term>();
        final Term t1 = new Term();
        t1.setId(1L);
        terms.add(t1);
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public List<Term> getDiseaseStatesForExperiment(Experiment experiment) {
        final List<Term> terms = new ArrayList<Term>();
        final Term t1 = new Term();
        t1.setId(1L);
        terms.add(t1);
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public List<Term> getMaterialTypesForExperiment(Experiment experiment) {
        final List<Term> terms = new ArrayList<Term>();
        final Term t1 = new Term();
        t1.setId(1L);
        terms.add(t1);
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public List<Term> getTissueSitesForExperiment(Experiment experiment) {
        final List<Term> terms = new ArrayList<Term>();
        final Term t1 = new Term();
        t1.setId(1L);
        terms.add(t1);
        return terms;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractCharacteristic> getArbitraryCharacteristicsForExperimentSamples(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Category> getArbitraryCharacteristicsCategoriesForExperimentSamples(Experiment experiment) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public List<AssayType> getAssayTypes() {
        final List<AssayType> assayTypes = new ArrayList<AssayType>();
        final AssayType a1 = new AssayType();
        a1.setId(1L);
        assayTypes.add(a1);
        return assayTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends AbstractBioMaterial> T getBiomaterialByExternalId(Project project, String externalId,
            Class<T> biomaterialClass) {
        T bm;
        try {
            bm = biomaterialClass.newInstance();
        } catch (final InstantiationException e) {
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass);
        } catch (final IllegalAccessException e) {
            throw new IllegalArgumentException("Could not create new instance of class " + biomaterialClass);
        }
        bm.setExternalId(externalId);
        bm.setExperiment(project.getExperiment());
        return bm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSamplesByCharacteristicCategory(Category c, String keyword) {
        return 10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countSourcesByCharacteristicCategory(Category c, String keyword) {
        return 10;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends AbstractBioMaterial> List<T> searchByCategory(PageSortParams<T> params, String keyword,
            Class<T> biomaterialClass, BiomaterialSearchCategory... categories) {

        if (Sample.class.equals(biomaterialClass)) {
            final List<T> returnVal = new ArrayList<T>();
            returnVal.add((T) new Sample());
            returnVal.add((T) new Sample());
            returnVal.add((T) new Sample());
            returnVal.add((T) new Sample());
            return returnVal;
        } else if (Source.class.equals(biomaterialClass)) {
            final List<T> returnVal = new ArrayList<T>();
            returnVal.add((T) new Source());
            returnVal.add((T) new Source());
            returnVal.add((T) new Source());
            returnVal.add((T) new Source());
            return returnVal;
        } else if (Source.class.equals(biomaterialClass)) {
            final List<T> returnVal = new ArrayList<T>();
            returnVal.add((T) new Extract());
            returnVal.add((T) new Extract());
            returnVal.add((T) new Extract());
            returnVal.add((T) new Extract());
            return returnVal;
        } else if (Source.class.equals(biomaterialClass)) {
            final List<T> returnVal = new ArrayList<T>();
            returnVal.add((T) new LabeledExtract());
            returnVal.add((T) new LabeledExtract());
            returnVal.add((T) new LabeledExtract());
            returnVal.add((T) new LabeledExtract());
            return returnVal;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchCount(String keyword, Class<? extends AbstractBioMaterial> biomaterialClass,
            BiomaterialSearchCategory... categories) {
        return 4;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> searchSamplesByExperimentAndCategory(String keyword, Experiment e, SearchSampleCategory... c) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> searchSamplesByExperimentAndArbitraryCharacteristicValue( String keyword, Experiment e, 
            Category c ) {
        return Collections.emptyList();
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public List<Sample> searchSamplesByCharacteristicCategory(PageSortParams<Sample> params, Category c, String keyword) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Source> searchSourcesByCharacteristicCategory(PageSortParams<Source> params, Category c, String keyword) {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeOwner(Long projectId, String newOwner) throws CSException {
    }

    @Override
    public List<Experiment> searchByCriteria(PageSortParams<Experiment> params, ExperimentSearchCriteria criteria) {
        return Collections.emptyList();
    }

    public List<CaArrayFile> searchFiles(PageSortParams<CaArrayFile> params, FileSearchCriteria criteria) {
        return Collections.emptyList();
    }

    @Override
    public List<CaArrayFile> getDeletableFiles(Long projectId) {
        return Collections.emptyList();
    }

    @Override
    public List<Project> getProjectsWithReImportableFiles() {
        return Collections.emptyList();
    }

    @Override
    public List<Category> getAllCharacteristicCategories(Experiment experiment) {
        return Collections.emptyList();
    }
}
