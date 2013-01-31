//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
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
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.UserProvisioningManager;
import gov.nih.nci.security.exceptions.CSException;
//carpla
import gov.nih.nci.caarray.application.antibody.AntibodyService;
//carpla
import gov.nih.nci.caarray.application.project.ProjectManagementService;
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

    
    //carpla
    public static AntibodyService getAntibodyService () {
		return (AntibodyService) ServiceLocatorFactory
				.getLocator()
				.lookup(AntibodyService.JNDI_NAME);
	}
    //carpla
    
    
    
    
    
    
    
    
    
    
    
    
    
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
            return SecurityServiceProvider.getUserProvisioningManager(
                    SecurityUtils.getApplication().getApplicationName());
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
     * Convenience method for obtaining the MAGE-TAB Exporter singleton service.
     * @return the MAGE-TAB Exporter service
     */
    public static MageTabExporter getMageTabExporter() {
        return (MageTabExporter) ServiceLocatorFactory.getLocator().lookup(MageTabExporter.JNDI_NAME);
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
