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
package org.cruk.cri.caarray.scripts;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.application.file.InvalidFileException;
import gov.nih.nci.caarray.application.project.InconsistentProjectStateException;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.services.ServerConnectionException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cruk.cri.caarray.CaArrayServiceFacade;
import org.cruk.cri.caarray.services.CaArrayServer;

/**
 * Script for uploading, validating and importing files into a project.
 *
 * @author eldrid01
 */
public class LoadArrayDataFiles
{
    public static List<FileStatus> VALIDATING_STATUS = Arrays.asList(new FileStatus[] { FileStatus.UPLOADED, FileStatus.IN_QUEUE, FileStatus.VALIDATING });
    public static List<FileStatus> VALIDATED_STATUS = Arrays.asList(new FileStatus[] { FileStatus.VALIDATED, FileStatus.VALIDATED_NOT_PARSED });
    public static List<FileStatus> IMPORTING_STATUS = Arrays.asList(new FileStatus[] { FileStatus.IN_QUEUE, FileStatus.IMPORTING, FileStatus.VALIDATED });
    public static List<FileStatus> IMPORTED_STATUS = Arrays.asList(new FileStatus[] { FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED });

    public static final int MAX_POLL_ATTEMPTS = 5000;
    public static final int POLL_INTERVAL = 10000;

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_JNDI_PORT = 31099;
    private static final String USERNAME = "eldrid01";
    private static final String PASSWORD = "caArray2!";

    private CaArrayServer server;
    private CaArrayServiceFacade service;

    private final Log log = LogFactory.getLog(getClass());

    public static void main(String[] args) throws Exception
    {
        LoadArrayDataFiles script = new LoadArrayDataFiles(SERVER_HOST, SERVER_JNDI_PORT, USERNAME, PASSWORD);
        script.loadGSKCancerCellLineData();
    }

    public LoadArrayDataFiles(String hostname, int port, String username, String password) throws FailedLoginException, ServerConnectionException 
    {
        server = new CaArrayServer(hostname, port);
        server.connect(username, password);
        service = server.getCaArrayServiceFacade();
    }

    /**
     * Sample script to load, validate and import Affymetrix SNP array data
     * from the GSK cancer cell line project.
     *
     * @throws Exception
     */
    public void loadGSKCancerCellLineData() throws Exception
    {
        // Create project
        Project project = createProject("GSK Cancer Cell Line SNP Array Data", "Affymetrix", new String[] { "Mapping250K_Nsp", "Mapping250K_Sty" }, AssayType.SNP);

        // Retrieves existing project with given ID
//        Project project = getProjectWithExperimentId("eldri-00001");

        // Retrieves existing project with given title
//      Project project = getProjectWithExperimentTitle("GSK Cancer Cell Line SNP Array Data");

        // Uploads all files from the subdirectory gsk500k
        uploadFiles(project, "gsk500k");

        // Don't actually need to validate if going on to import immediately
        // after as the import step also performs validation - shown for
        // illustrative purposes
        Set<CaArrayFile> fileSet = validateFiles(project);

        // Poll for completion of validation before continuing
        pollForStatusChange(project, fileSet, VALIDATING_STATUS, VALIDATED_STATUS, POLL_INTERVAL, MAX_POLL_ATTEMPTS);

        fileSet = importFiles(project);

        // Poll for completion of import before continuing
//        pollForStatusChange(project, fileSet, IMPORTING_STATUS, IMPORTED_STATUS, POLL_INTERVAL, MAX_POLL_ATTEMPTS);
    }

    /**
     * Returns the array provider or manufacturer with the given name.
     *
     * @param name the name of the array provider.
     * @return an organization representing the array provider.
     */
    public Organization getProvider(String name)
    {
        List<Organization> providers = service.getAllProviders();
        for (Organization provider : providers)
        {
            if (provider.getName().equals(name))
            {
                return provider;
            }
        }
        throw new RuntimeException("Provider " + name + " not found");
    }

    /**
     * Returns the array design with the given name and type from the specified
     * array provider.
     *
     * @param name the name of the array design.
     * @param providerName the name of the array provider (or manufacturer).
     * @param type the type of array.
     * @return an array design.
     */
    public ArrayDesign getArrayDesign(String name, String providerName, AssayType type)
    {
        Organization provider = getProvider(providerName);

        List<ArrayDesign> arrayDesigns = service.getImportedArrayDesigns(provider, type);
        for (ArrayDesign arrayDesign : arrayDesigns)
        {
            if (arrayDesign.getName().equals(name))
            {
                return arrayDesign;
            }
        }

        throw new RuntimeException("Array design " + name + " not found");
    }

    /**
     * Retrieves the experiment with the given public identifier.
     *
     * @param id the public identifier.
     * @return the experiment with the given public identifier.
     */
    public Experiment getExperimentWithId(String id)
    {
        Experiment experiment = new Experiment();
        experiment.setPublicIdentifier(id);
        List<Experiment> experimentList = server.getSearchService().search(experiment);
        return experimentList.get(0);
    }

    /**
     * Retrieves the experiment with the given title.
     *
     * @param title the title of the experiment.
     * @return the experiment with the given title.
     */
    public Experiment getExperimentWithTitle(String title)
    {
        Experiment experiment = new Experiment();
        experiment.setTitle(title);
        List<Experiment> experimentList = server.getSearchService().search(experiment);
        return experimentList.get(0);
    }

    /**
     * Retrieves the project for the experiment with the given title.
     *
     * @param title the title of the experiment.
     * @return the project.
     */
    public Project getProjectWithExperimentTitle(String title)
    {
        Experiment experiment = getExperimentWithTitle(title);
        Project project = experiment.getProject();
        return refresh(project);
    }

    /**
     * Returns the names of the files contained within the given project.
     *
     * @param project the project.
     * @return a set of file names.
     */
    public Set<String> getFilenames(Project project)
    {
        Set<String> files = new HashSet<String>();

        for (CaArrayFile caArrayFile : project.getFiles())
        {
            files.add(caArrayFile.getName());
        }

        return files;
    }

    /**
     * Creates a new project and experiment with the given name and specified
     * array design.
     *
     * @param experimentName the name of the experiment.
     * @param providerName the name of the array provider.
     * @param designName the name of the array design.
     * @param type the array type.
     * @return the newly created project.
     * @throws ProposalWorkflowException
     * @throws InconsistentProjectStateException
     */
    public Project createProject(String experimentName, String providerName, String designName, AssayType type) throws ProposalWorkflowException, InconsistentProjectStateException
    {
        ArrayDesign arrayDesign = getArrayDesign(designName, providerName, type);
        return service.createProject(experimentName, null, arrayDesign.getId());
    }

    /**
     * Creates a new project and experiment with the given name and specified
     * array designs.
     *
     * @param experimentName the name of the experiment.
     * @param providerName the name of the array provider.
     * @param designNames the names of the array designs.
     * @param type the array type.
     * @return the newly created project.
     * @throws ProposalWorkflowException
     * @throws InconsistentProjectStateException
     */
    public Project createProject(String experimentName, String providerName, String[] designNames, AssayType type) throws ProposalWorkflowException, InconsistentProjectStateException
    {
        int n = designNames.length;
        long[] arrayDesignIds = new long[n];
        for (int i = 0; i < n; i++)
        {
            ArrayDesign arrayDesign = getArrayDesign(designNames[i], providerName, type);
            arrayDesignIds[i] = arrayDesign.getId();
        }
        return service.createProject(experimentName, null, arrayDesignIds);
    }

    /**
     * Uploads all files in the given directory to the given project.
     * 
     * Ignores files that have already been uploaded on the basis of a case-
     * insensitive name match.
     * 
     * @param project the project.
     * @param directoryName the directory name.
     * @throws InvalidFileException
     * @throws InconsistentProjectStateException 
     * @throws ProposalWorkflowException 
     * @throws InterruptedException 
     * @throws IOException 
     * @throws Exception
     */
    public void uploadFiles(Project project, String directoryName) throws IOException, InterruptedException, ProposalWorkflowException, InconsistentProjectStateException, InvalidFileException
    {
        long t0 = System.currentTimeMillis();

        int count = 0;
        project = refresh(project);

        // get list of existing files to avoid uploading duplicates
        Set<String> existingFiles = getFilenames(project);

        File dir = new File(directoryName);
        for (File file : dir.listFiles())
        {
            String name = file.getName();
            if (!existingFiles.contains(name))
            {
                count++;
                log.info(count + " " + dir.getName() + "/" + file.getName());
                // Note it is important that absolute file paths are given to the upload method
                service.uploadFile(project, file.getAbsoluteFile(), false);
            }
        }

        log.info("Number of files uploaded: " + count);
        long t = System.currentTimeMillis() - t0;
        log.info("Time (ms): " + t);
    }

    /**
     * Validates all files in the specified project.
     *
     * @param project the project.
     * @return the files being validated.
     * @throws InterruptedException
     */

    public Set<CaArrayFile> validateFiles(Project project) throws InterruptedException
    {
        project = refresh(project);
        log.info("Number of files in project: " + project.getFiles().size());

        Set<CaArrayFile> fileSet = new HashSet<CaArrayFile>();
        for (CaArrayFile file : project.getFiles())
        {
            FileStatus status = file.getFileStatus();
            if (status.isValidatable() && !VALIDATED_STATUS.contains(status))
            {
                fileSet.add(file);
            }
        }
        log.info("Number of files to validate: " + fileSet.size());

        service.validateFiles(project, fileSet);

        return fileSet;
    }

    /**
     * Imports all importable files in the given project.
     * 
     * @param project the project.
     * @return the files being imported.
     * @throws InterruptedException
     */
    public Set<CaArrayFile> importFiles(Project project) throws InterruptedException
    {
        project = refresh(project);
        log.info("Number of files in project: " + project.getFiles().size());

        boolean foundSdrf = false;

        Set<CaArrayFile> fileSet = new HashSet<CaArrayFile>();
        for (CaArrayFile file : project.getFiles())
        {
            if (file.getFileStatus().isImportable())
            {
                fileSet.add(file);
            }
            if (file.getFileType() == FileType.MAGE_TAB_SDRF)
            {
                foundSdrf = true;
            }
        }
        log.info("Number of files to import: " + fileSet.size());

        if (foundSdrf)
        {
            service.importFiles(project, fileSet);
        }
        else
        {
            service.importFiles(project, fileSet, DataImportOptions.getAutoCreatePerFileOptions());
        }

        return fileSet;
    }

    /**
     * Refreshes the given CaArrayObject by retrieving it through the search
     * service.
     *
     * @param <T> the type of the object.
     * @param caArrayObject the object.
     * @return a newly retrieved copy of the object.
     */
    private <T extends AbstractCaArrayObject> T refresh(T caArrayObject)
    {
        return server.getSearchService().search(caArrayObject).get(0);
    }

    /**
     * Polls for status change on the given set of files within the specified project.
     *
     * @param project the project
     * @param fileSet the set of files
     * @param initialStatus the initial status
     * @param expectedStatus the expected final status
     * @param pollInterval the interval between poll attempts in milliseconds
     * @param maxPollAttempts the maximum number of poll attempts
     * @throws InterruptedException
     */
    public void pollForStatusChange(Project project, Set<CaArrayFile> fileSet, List<FileStatus> initialStatus, List<FileStatus> expectedStatus, int pollInterval, int maxPollAttempts) throws InterruptedException
    {
        Set<Long> fileIds = new HashSet<Long>();
        for (CaArrayFile file : fileSet) {
            fileIds.add(file.getId());
        }

        int count = 0;

        while (true)
        {
            Thread.sleep(pollInterval);

            count++;
            log.debug("Poll attempt " + count);

            project = refresh(project);

            boolean done = true;

            for (CaArrayFile file : project.getFiles())
            {
                if (fileIds.contains(file.getId()))
                {
                    FileStatus status = file.getFileStatus();
                    if (initialStatus.contains(status))
                    {
                        done = false;
                    }
                    else if (!expectedStatus.contains(status))
                    {
                        throw new RuntimeException("Unexpected file status for " + file.getName() + ": " + file.getStatus());
                    }
                }
            }

            if (done)
            {
                break;
            }

            if (count >= maxPollAttempts)
            {
                throw new RuntimeException("Maximum number of poll attempts exceeded");
            }
        }
    }
}
