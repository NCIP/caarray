/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
