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
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Service facade providing remote access to a number of retrieval and update
 * methods not available through the remotely available EJBs provided by
 * caArray.
 *
 * @author eldrid01
 */
public interface CaArrayServiceFacade
{
    /**
     * The JNDI name to look up the remote <code>CaArrayServiceFacade</code> service.
     */
    String JNDI_NAME = "caarray/CaArrayServiceFacadeBean/remote";

    /**
     * @return the list of all providers in the system.
     */
    List<Organization> getAllProviders();

    /**
     * Returns the list of ArrayDesigns with the given provider and assay type.
     *
     * @param provider the provider
     * @param assayType the assay type
     * @return the List&lt;ArrayDesign&gt; of the array designs whose provider is the given provider
     */
    List<ArrayDesign> getImportedArrayDesigns(Organization provider, AssayType assayType);

    /**
     * Creates a new project with the given title, description and array design.
     *
     * @param title
     * @param description
     * @param arrayDesignId
     * @return
     * @throws ProposalWorkflowException
     * @throws InconsistentProjectStateException
     */
    Project createProject(String title, String description, long arrayDesignId) throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * Creates a new project with the given title, description and array designs.
     *
     * @param title
     * @param description
     * @param arrayDesignIds
     * @return
     * @throws ProposalWorkflowException
     * @throws InconsistentProjectStateException
     */
    Project createProject(String title, String description, long[] arrayDesignIds) throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * Deletes a project. The project must be in the draft state.
     *
     * @param project the project to save
     * @throws ProposalWorkflowException if the project cannot currently be deleted because it is not a draft
     */
    void deleteProject(Project project) throws ProposalWorkflowException;

    /**
     * Sets the experiment title for the given project.
     *
     * @param project the project.
     * @param title the title of the experiment.
     */
    void setExperimentTitle(Project project, String title) throws ProposalWorkflowException, InconsistentProjectStateException;

    /**
     * Uploads a file to the project.
     *
     * Note it is important that absolute file paths are given to this function,
     * i.e. file.getAbsoluteFile() should be passed to this function.
     * It is also necessary that this file path is visible on the caArray server
     * host.
     *
     * @param project
     * @param file this must correspond to the absolute path for the file.
     * @param isSupplemental
     * @throws ProposalWorkflowException
     * @throws IOException
     * @throws InconsistentProjectStateException
     * @throws InvalidFileException
     */
    void uploadFile(Project project, File file, boolean isSupplemental)
        throws ProposalWorkflowException, IOException, InconsistentProjectStateException, InvalidFileException;

    /**
     * Marks the given uploaded file as supplemental.
     *
     * @param project the project.
     * @param filename the name of the file to be marked as supplemental.
     * @throws FileNotFoundException
     */
    void addSupplementalFile(Project project, String filename) throws FileNotFoundException;

    /**
     * Change the file type of the given CaArrayFile object.
     *
     * @param caArrayFile the file.
     * @param fileType the new type.
     */
    void changeFileType(CaArrayFile caArrayFile, FileType fileType);

    /**
     * Removes a file from caArray file storage.
     *
     * @param file the caArrayFile to remove
     */
    void removeFile(CaArrayFile caArrayFile);

    /**
     * Initiates the validation of a set of files within the given project.
     *
     * @param project
     * @param files
     */
    void validateFiles(Project project, Set<CaArrayFile> files);

    /**
     * Initiates the import of a set of files within the given project.
     *
     * Note that the file set should include an SDRF (and accompanying IDF) file
     * that includes references to all array data files in the given set.
     *
     * @param project
     * @param files
     */
    void importFiles(Project project, Set<CaArrayFile> files);

    /**
     * Initiates the import of a set of files within the given project.
     *
     * @param project
     * @param files
     * @param dataImportOptions
     */
    void importFiles(Project project, Set<CaArrayFile> files, DataImportOptions dataImportOptions);
}
