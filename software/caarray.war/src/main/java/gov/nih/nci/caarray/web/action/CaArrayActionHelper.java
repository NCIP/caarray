/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.browse.BrowseService;
import gov.nih.nci.caarray.application.country.CountryService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.permissions.PermissionsManagementService;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.registration.RegistrationService;
import gov.nih.nci.caarray.application.state.StateService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.exceptions.CSException;

import java.util.Set;

/**
 * Helper class for actions.
 * @author Scott Miller
 */
public final class CaArrayActionHelper {
    
    private CaArrayActionHelper() {
        
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static ProjectManagementService getProjectManagementService() {
        return (ProjectManagementService) ServiceLocatorFactory.getLocator().lookup(ProjectManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static PermissionsManagementService getPermissionsManagementService() {
        return (PermissionsManagementService) ServiceLocatorFactory.getLocator()
                                                                   .lookup(PermissionsManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static FileAccessService getFileAccessService() {
        return (FileAccessService) ServiceLocatorFactory.getLocator().lookup(FileAccessService.JNDI_NAME);
    }
    
    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return countryService
     */
    public static CountryService getCountryService() {
        return (CountryService) ServiceLocatorFactory.getLocator().lookup(CountryService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return StateService
     */
    public static StateService getStateService() {
        return (StateService) ServiceLocatorFactory.getLocator().lookup(StateService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static FileManagementService getFileManagementService() {
        return (FileManagementService) ServiceLocatorFactory.getLocator().lookup(FileManagementService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static VocabularyService getVocabularyService() {
        return (VocabularyService) ServiceLocatorFactory.getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static GenericDataService getGenericDataService() {
        return (GenericDataService) ServiceLocatorFactory.getLocator().lookup(GenericDataService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static RegistrationService getRegistrationService() {
        return (RegistrationService) ServiceLocatorFactory.getLocator().lookup(RegistrationService.JNDI_NAME);
    }

    /**
     * get UserProvisioningManager.
     * @return UserProvisioningManager provisioning manager
     * @throws CSException on CSM error
     */
    public static UserProvisioningManager getUserProvisioningManager() throws CSException {
            return SecurityServiceProvider.getUserProvisioningManager("caarray");
    }

    /**
     * Convenience method for obtaining the singleton service. Intended to mixed in to action classes
     * via static import
     * @return the service
     */
    public static BrowseService getBrowseService() {
        return (BrowseService) ServiceLocatorFactory.getLocator().lookup(BrowseService.JNDI_NAME);
    }
    
    /**
     * Retrieve the category corresponding to the given ExperimentOntologyCategory constant.
     * @param category an ExperimentOntologyCategory constant describing a category
     * @return the category, or null if none exists
     */
    public static Category getCategory(ExperimentOntologyCategory category) {
      TermSource ts = getTermSource(category.getOntology());
      return getVocabularyService().getCategory(ts, category.getCategoryName());
    }

    /**
     * Retrieve the term source corresponding to the given ExperimentOntology constant.
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term source, or null if none exists
     */
    public static TermSource getTermSource(ExperimentOntology ontology) {
        return getVocabularyService().getSource(ontology.getOntologyName(), ontology.getVersion());
    }

    /**
     * Retrieve the term with given value from the MGED Ontology term source.
     * @param value value of the term to retrieve
     * @return the term, or null if the term does not exist in the MGED Ontology term source
     */
    public static Term getMOTerm(String value) {
        return getTerm(ExperimentOntology.MGED_ONTOLOGY, value);
    }

    /**
     * Retrieve the term with given value from the term source corresponding to given ExperimentOntology constant.
     * @param value value of the term to retrieve
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term, or null if the term does not exist in the term source
     */
    public static Term getTerm(ExperimentOntology ontology, String value) {
        TermSource ts = getTermSource(ontology);
        return getVocabularyService().getTerm(ts, value);
    }

    /**
     * Retrieve the set of terms belonging to the category corresponding to the given ExperimentOntologyCategory 
     * constant.
     * @param category an ExperimentOntologyCategory constant describing a category
     * @return the Set of Terms belonging to this category or its subcategories
     */
    public static Set<Term> getTermsFromCategory(ExperimentOntologyCategory category) {
      return getVocabularyService().getTerms(getCategory(category));
    }
}
