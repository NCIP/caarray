/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cruk.cri.caarray;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.file.FileManagementService;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.project.FileUploadUtils;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Service facade providing remote access to a number of retrieval and update
 * methods not available through the remotely available EJBs provided by
 * caArray.
 *
 * @author eldrid01
 */
@Stateless
@Remote(CaArrayServiceFacade.class)
@Interceptors({ AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class })
@TransactionManagement(TransactionManagementType.BEAN)
public class CaArrayServiceFacadeBean implements CaArrayServiceFacade
{
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CaArrayServiceFacadeBean.class);

    @EJB
    private ProjectManagementService projectManagementService;

    @EJB
    private ArrayDesignService arrayDesignService;
    
    @EJB
    private VocabularyService vocabularyService;

    @EJB
    private FileManagementService fileManagementService;

    @EJB
    private FileAccessService fileAccessService;
    
    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#getAllProviders()
     */
    public List<Organization> getAllProviders()
    {
        return arrayDesignService.getAllProviders();
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#getImportedArrayDesigns(gov.nih.nci.caarray.domain.contact.Organization, gov.nih.nci.caarray.domain.project.AssayType)
     */
    public List<ArrayDesign> getImportedArrayDesigns(Organization provider, AssayType assayType)
    {
        return arrayDesignService.getImportedArrayDesigns(provider, assayType);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#createProject(java.lang.String, java.lang.String, long)
     */
    public Project createProject(String title, String description, long arrayDesignId) throws ProposalWorkflowException, InconsistentProjectStateException
    {
        return createProject(title, description, new long[] { arrayDesignId });
    }

    public Project createProject(String title, String description, long[] arrayDesignIds) throws ProposalWorkflowException, InconsistentProjectStateException
    {
        Experiment experiment = new Experiment();
        experiment.setTitle(title);
        experiment.setDescription(description);

        Set<ArrayDesign> arrayDesignSet = new HashSet<ArrayDesign>();
        boolean first = true;
        for (long arrayDesignId : arrayDesignIds)
        {
            ArrayDesign arrayDesign = arrayDesignService.getArrayDesign(arrayDesignId);
            arrayDesignSet.add(arrayDesign);
            if (first)
            {
                experiment.setAssayTypeEnum(arrayDesign.getAssayTypeEnum());
                experiment.setManufacturer(arrayDesign.getProvider());
                experiment.setOrganism(arrayDesign.getOrganism());
                first = false;
            }
        }
        experiment.setArrayDesigns(arrayDesignSet);

        Person person = new Person(UsernameHolder.getCsmUser());
        Term piRole = getTerm(ExperimentOntology.MGED_ONTOLOGY, ExperimentContact.PI_ROLE);
        Term mainPocRole = getTerm(ExperimentOntology.MGED_ONTOLOGY, ExperimentContact.MAIN_POC_ROLE);
        ExperimentContact contact = new ExperimentContact(experiment, person, Arrays.asList(piRole, mainPocRole));
        experiment.getExperimentContacts().add(contact);

        Project project = new Project();
        project.setExperiment(experiment);

        projectManagementService.saveProject(project, new PersistentObject[0]);
        
        return project;
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#deleteProject(gov.nih.nci.caarray.domain.project.Project)
     */
    public void deleteProject(Project project) throws ProposalWorkflowException
    {
        projectManagementService.deleteProject(project);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#setExperimentTitle(gov.nih.nci.caarray.domain.project.Project, java.lang.String)
     */
    public void setExperimentTitle(Project project, String title) throws ProposalWorkflowException, InconsistentProjectStateException
    {
        project = refresh(project);
        project.getExperiment().setTitle(title);
        projectManagementService.saveProject(project, new PersistentObject[0]);
    }

    /**
     * Retrieve the term source corresponding to the given ExperimentOntology constant.
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term source, or null if none exists
     */
    private TermSource getTermSource(ExperimentOntology ontology) {
        return vocabularyService.getSource(ontology.getOntologyName(), ontology.getVersion());
    }

    /**
     * Retrieve the term with given value from the term source corresponding to given ExperimentOntology constant.
     * @param value value of the term to retrieve
     * @param ontology an ExperimentOntology constant describing a TermSource
     * @return the term, or null if the term does not exist in the term source
     */
    private Term getTerm(ExperimentOntology ontology, String value) {
        TermSource ts = getTermSource(ontology);
        return vocabularyService.getTerm(ts, value);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#uploadFiles(gov.nih.nci.caarray.domain.project.Project, java.util.List)
     */
    public void uploadFile(Project project, File file, boolean isSupplemental)
        throws ProposalWorkflowException, IOException, InconsistentProjectStateException, InvalidFileException
    {
        List<File> files = new ArrayList<File>();
        files.add(file);

        String fileName = file.getName();

        List<String> fileNames = new ArrayList<String>();
        fileNames.add(fileName);

        List<String> filesToUnpack = new ArrayList<String>();
        if (fileName.toLowerCase().endsWith(".zip"))
        {
            filesToUnpack.add(fileName);
        }

        FileUploadUtils.uploadFiles(project, files, fileNames, filesToUnpack);

        if (isSupplemental)
        {
            project = refresh(project);
            Set<CaArrayFile> caArrayFiles = project.getFiles();
            for (CaArrayFile caArrayFile : caArrayFiles)
            {
                if (caArrayFile.getName().equals(fileName))
                {
                    CaArrayFileSet fileSet = new CaArrayFileSet(project);
                    fileSet.add(caArrayFile);
                    fileManagementService.addSupplementalFiles(project, fileSet);
                    break;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#addSupplementalFile(gov.nih.nci.caarray.domain.project.Project, java.lang.String)
     */
    public void addSupplementalFile(Project project, String filename)
        throws FileNotFoundException
    {
        project = refresh(project);

        for (CaArrayFile caArrayFile : project.getFiles())
        {
            if (caArrayFile.getName().equals(filename))
            {
                CaArrayFileSet fileSet = new CaArrayFileSet(project);
                fileSet.add(caArrayFile);
                fileManagementService.addSupplementalFiles(project, fileSet);
                return;
            }
        }

        throw new FileNotFoundException("Could not find file " + filename + " within project " + project.getExperiment().getPublicIdentifier());
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#changeFileType(gov.nih.nci.caarray.domain.file.CaArrayFile, gov.nih.nci.caarray.domain.file.FileType)
     */
    public void changeFileType(CaArrayFile caArrayFile, FileType fileType)
    {
    	caArrayFile = refresh(caArrayFile);
    	caArrayFile.setFileType(fileType);
    	fileAccessService.save(caArrayFile);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#removeFile(gov.nih.nci.caarray.domain.file.CaArrayFile)
     */
    public void removeFile(CaArrayFile caArrayFile)
    {
        // Look up the fully-populated CaArray object since the one passed in by remote clients will have contents set
        // to null (not serializable).
        caArrayFile = refresh(caArrayFile);
        fileAccessService.remove(caArrayFile);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#validateFiles(gov.nih.nci.caarray.domain.project.Project, gov.nih.nci.caarray.domain.file.CaArrayFileSet)
     */
    public void validateFiles(Project project, Set<CaArrayFile> files)
    {
        project = refresh(project);
        CaArrayFileSet fileSet = getCaArrayFileSet(project, files);
        fileManagementService.validateFiles(project, fileSet);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#importFiles(gov.nih.nci.caarray.domain.project.Project, gov.nih.nci.caarray.domain.file.CaArrayFileSet)
     */
    public void importFiles(Project project, Set<CaArrayFile> files)
    {
        DataImportOptions dataImportOptions = DataImportOptions.getDataImportOptions(null, null, null, new ArrayList<Long>());
        importFiles(project, files, dataImportOptions);
    }

    /* (non-Javadoc)
     * @see org.cruk.cri.caarray.CaArrayServiceFacade#importFiles(gov.nih.nci.caarray.domain.project.Project, java.util.Set, gov.nih.nci.caarray.application.arraydata.DataImportOptions)
     */
    public void importFiles(Project project, Set<CaArrayFile> files, DataImportOptions dataImportOptions)
    {
        project = refresh(project);
        CaArrayFileSet fileSet = getCaArrayFileSet(project, files);
        fileManagementService.importFiles(project, fileSet, dataImportOptions);
    }

    /**
     * Refreshes the given persistent object by re-retrieving it using the
     * search DAO to circumvent problems associated with object graph trimming
     * during remote API calls.
     *
     * @param <T> the type of the persistent object.
     * @param persistentObject the persistent object.
     * @return a newly retrieved copy of the persistent object.
     */
    @SuppressWarnings("unchecked")
    private <T extends PersistentObject> T refresh(PersistentObject persistentObject)
    {
        return (T)getSearchDao().retrieve(persistentObject.getClass(), persistentObject.getId());
    }

    /**
     * Builds a CaArrayFileSet object for the given project and set of files
     * re-retrieving the files to circumvent problems associated with object
     * graph trimming.
     *
     * @param project the project.
     * @param files the set of files.
     * @return a CaArrayFileSet.
     */
    private CaArrayFileSet getCaArrayFileSet(Project project, Collection<CaArrayFile> files)
    {
        Set<Long> fileIds = new HashSet<Long>();
        for (CaArrayFile file : files) {
            fileIds.add(file.getId());
        }
        files = getSearchDao().retrieveByIds(CaArrayFile.class, new ArrayList<Long>(fileIds));
        CaArrayFileSet fileSet = new CaArrayFileSet(project);
        fileSet.addAll(files);
        return fileSet;
    }

    /**
     * Return the DAO factory.
     *
     * @return the DAO factory.
     */
    private CaArrayDaoFactory getDaoFactory()
    {
        return CaArrayDaoFactory.INSTANCE;
    }

    /**
     * Returns a DAO for searching domain objects.
     * Note that the SearchDao is used instead of the CaArraySearchService to
     * avoid object cutting that gets performed by its EntityConfiguringInterceptor
     * interceptor.
     *
     * @return a search data access object.
     */
    private SearchDao getSearchDao()
    {
        return getDaoFactory().getSearchDao();
    }
}
