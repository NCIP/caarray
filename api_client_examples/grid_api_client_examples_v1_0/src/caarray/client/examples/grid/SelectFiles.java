//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.BiomaterialSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.Biomaterial;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A client selecting files from an experiment using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class SelectFiles {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;
    private static final String SAMPLE_NAME_01 = BaseProperties.SAMPLE_NAME_01;
    private static final String SAMPLE_NAME_02 = BaseProperties.SAMPLE_NAME_02;

    public static void main(String[] args) {
        SelectFiles selector = new SelectFiles();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            CaArrayEntityReference experimentRef = selector.searchForExperiment();
            if (experimentRef == null) {
                System.out.println("Could not find experiment with the requested title.");
                return;
            }
            System.out.println("Selecting files in experiment: " + EXPERIMENT_TITLE + "...");
            selector.selectFilesInExperiment(experimentRef);
            System.out.println("Selecting files associated with samples: " + SAMPLE_NAME_01 + ", " + SAMPLE_NAME_02 + "...");
            selector.selectFilesFromSamples(experimentRef);
        } catch (Throwable t) {
            System.err.println("Error while selecting files.");
            t.printStackTrace();
        }
    }

    private void selectFilesInExperiment(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        List<CaArrayEntityReference> fileRefs = selectRawFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any raw files in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " raw files in the experiment.");
        }
        fileRefs = selectCelFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any Affymetrix CEL files in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " Affymetrix CEL files in the experiment.");
        }
        fileRefs = selectChpFiles(experimentRef);
        if (fileRefs == null) {
            System.out.println("Could not find any derived files with extension .CHP in the experiment.");
        } else {
            System.out.println("Found " + fileRefs.size() + " derived files with extension .CHP in the experiment.");
        }
    }

    private void selectFilesFromSamples(CaArrayEntityReference experimentRef) throws RemoteException {
        Set<CaArrayEntityReference> sampleRefs = searchForSamples(experimentRef);
        if (sampleRefs == null || sampleRefs.size() <= 0) {
            System.out.println("Could not find the requested samples.");
            return;
        }
        List<CaArrayEntityReference> fileRefs = selectRawFilesFromSamples(experimentRef, sampleRefs);
        if (fileRefs == null) {
            System.out.println("Could not find any raw files associated with the given samples.");
        } else {
            System.out.println("Found " + fileRefs.size() + " raw files associated with the given samples.");
        }
    }

    /**
     * Search for an experiment based on its title or public identifier.
     */
    private CaArrayEntityReference searchForExperiment() throws RemoteException {
        // Search for experiment with the given title.
        ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        experimentSearchCriteria.setTitle(EXPERIMENT_TITLE);

        // ... OR Search for experiment with the given public identifier.
        // ExperimentSearchCriteria experimentSearchCriteria = new ExperimentSearchCriteria();
        // experimentSearchCriteria.setPublicIdentifier(EXPERIMENT_PUBLIC_IDENTIFIER);

        List<Experiment> experiments = (client.searchForExperiments(experimentSearchCriteria, null)).getResults();
        if (experiments == null || experiments.size() <= 0) {
            return null;
        }

        // Assuming that only one experiment was found, pick the first result.
        // This will always be true for a search by public identifier, but may not be true for a search by title.
        Experiment experiment = experiments.get(0);
        return experiment.getReference();
    }

    /**
     * Search for samples based on name.
     */
    private Set<CaArrayEntityReference> searchForSamples(CaArrayEntityReference experimentRef) throws RemoteException {
        BiomaterialSearchCriteria criteria = new BiomaterialSearchCriteria();
        criteria.setExperiment(experimentRef);
        criteria.getNames().add(SAMPLE_NAME_01);
        criteria.getNames().add(SAMPLE_NAME_02);
        criteria.getTypes().add(BiomaterialType.SAMPLE);
        List<Biomaterial> samples = client.searchForBiomaterials(criteria, null).getResults();
        if (samples == null || samples.size() <= 0) {
            return null;
        }
        Set<CaArrayEntityReference> sampleRefs = new HashSet<CaArrayEntityReference>();
        for (Biomaterial sample : samples) {
            sampleRefs.add(sample.getReference());
        }
        return sampleRefs;
    }

    /**
     * Select all raw data files in the experiment.
     */
    private List<CaArrayEntityReference> selectRawFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.getCategories().add(FileCategory.RAW_DATA);

        List<File> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    /**
     * Select all Affymetrix CEL data files in the experiment.
     */
    private List<CaArrayEntityReference> selectCelFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);

        CaArrayEntityReference celFileTypeRef = getCelFileType();
        //CaArrayEntityReference celFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CEL");
        fileSearchCriteria.getTypes().add(celFileTypeRef);

        List<File> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    private CaArrayEntityReference getCelFileType() throws RemoteException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CEL");
        criteria.setExample(exampleFileType);
        List<FileType> fileTypes = (client.searchByExample(criteria, null)).getResults();
        FileType celFileType = fileTypes.get(0);
        return celFileType.getReference();
    }

    /**
     * Select all derived data files with extension .CHP in the experiment.
     */
    private List<CaArrayEntityReference> selectChpFiles(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.setExtension("CHP");

        List<File> files = searchServiceHelper.filesByCriteria(fileSearchCriteria).list();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }

    /**
     * Select all raw data files associated with the given samples.
     */
    private List<CaArrayEntityReference> selectRawFilesFromSamples(CaArrayEntityReference experimentRef, Set<CaArrayEntityReference> sampleRefs) throws RemoteException {
        FileSearchCriteria fileSearchCriteria = new FileSearchCriteria();
        fileSearchCriteria.setExperiment(experimentRef);
        fileSearchCriteria.setExperimentGraphNodes(sampleRefs);
        fileSearchCriteria.getCategories().add(FileCategory.RAW_DATA);

        List<File> files = client.searchForFiles(fileSearchCriteria, null).getResults();
        if (files.size() <= 0) {
            return null;
        }

        // Return references to the files.
        List<CaArrayEntityReference> fileRefs = new ArrayList<CaArrayEntityReference>();
        for (File file : files) {
            System.out.print(file.getMetadata().getName() + "  ");
            fileRefs.add(file.getReference());
        }
        return fileRefs;
    }
}
