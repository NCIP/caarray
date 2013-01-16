//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.data.FileType;
import gov.nih.nci.caarray.external.v1_0.experiment.Experiment;
import gov.nih.nci.caarray.external.v1_0.query.AnnotationSetRequest;
import gov.nih.nci.caarray.external.v1_0.query.ExampleSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.ExperimentSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.FileSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.query.HybridizationSearchCriteria;
import gov.nih.nci.caarray.external.v1_0.sample.AbstractExperimentGraphNode;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationColumn;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationSet;
import gov.nih.nci.caarray.external.v1_0.sample.AnnotationValueSet;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.external.v1_0.value.AbstractValue;
import gov.nih.nci.caarray.external.v1_0.value.MeasurementValue;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;
import gov.nih.nci.caarray.external.v1_0.value.UserDefinedValue;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Category;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.CaArraySvc_v1_0Client;
import gov.nih.nci.caarray.services.external.v1_0.grid.client.GridSearchApiUtils;
import gov.nih.nci.caarray.services.external.v1_0.search.SearchApiUtils;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.axis.types.URI.MalformedURIException;

/**
 * A client downloading sample annotations for hybridizations using the caArray Grid service API.
 *
 * @author Rashmi Srinivasa
 */
public class DownloadSampleAnnotationsForHybridizations {
    private static CaArraySvc_v1_0Client client = null;
    private static SearchApiUtils searchServiceHelper = null;
    private static final String EXPERIMENT_TITLE = BaseProperties.AFFYMETRIX_EXPERIMENT;

    public static void main(String[] args) {
        DownloadSampleAnnotationsForHybridizations downloader = new DownloadSampleAnnotationsForHybridizations();
        try {
            client = new CaArraySvc_v1_0Client(BaseProperties.getGridServiceUrl());
            searchServiceHelper = new GridSearchApiUtils(client);
            System.out.println("Downloading sample annotations for hybridizations from " + EXPERIMENT_TITLE + "...");
            downloader.download();
        } catch (Throwable t) {
            System.out.println("Error while downloading sample annotations for hybridizations.");
            t.printStackTrace();
        }
    }

    private void download() throws RemoteException, MalformedURIException, IOException, Exception {
        // Select an experiment of interest.
        CaArrayEntityReference experimentRef = selectExperiment();
        if (experimentRef == null) {
            System.err.println("Could not find experiment with the requested title or public identifier.");
            return;
        }

        // Select hybridizations in the experiment.
        List<CaArrayEntityReference> hybridizationRefs = selectHybridizations(experimentRef);
        if (hybridizationRefs == null) {
            System.err.println("Could not find any hybridizations with CHP data in the selected experiment.");
            return;
        }

        // Retrieve the annotations for samples/biomaterials associated with these hybridizations.
        AnnotationSetRequest annotationSetRequest = new AnnotationSetRequest();
        annotationSetRequest.setExperimentGraphNodes(hybridizationRefs);
        Category[] allCategories = client.getAllCharacteristicCategories(experimentRef);
        // Typically, client application will pick categories of interest; we're using all categories here.
        for (Category category : allCategories) {
            annotationSetRequest.getCategories().add(category.getReference());
        }
        AnnotationSet annotationSet = client.getAnnotationSet(annotationSetRequest);
        if (annotationSet == null) {
            System.err.println("Retrieved null annotation set.");
            return;
        }
        printAnnotationSet(annotationSet);
    }

    /**
     * Search for experiments and select one.
     */
    private CaArrayEntityReference selectExperiment() throws RemoteException {
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
     * Select all hybridizations in the given experiment that have CHP data.
     */
    private List<CaArrayEntityReference> selectHybridizations(CaArrayEntityReference experimentRef) throws RemoteException, InvalidInputException {
        HybridizationSearchCriteria searchCriteria = new HybridizationSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        List<Hybridization> hybridizations = (searchServiceHelper.hybridizationsByCriteria(searchCriteria)).list();
        if (hybridizations == null || hybridizations.size() <= 0) {
            return null;
        }

        // Get references to the hybridizations.
        List<CaArrayEntityReference> hybridizationRefs = new ArrayList<CaArrayEntityReference>();
        for (Hybridization hybridization : hybridizations) {
            hybridizationRefs.add(hybridization.getReference());
        }

        // Check if the hybridizations have CHP files associated with them.
        if (haveChpFiles(experimentRef, hybridizationRefs)) {
            return hybridizationRefs;
        } else {
            return null;
        }
    }

    private boolean haveChpFiles(CaArrayEntityReference experimentRef, List<CaArrayEntityReference> hybridizationRefs) throws RemoteException, InvalidInputException {
        FileSearchCriteria searchCriteria = new FileSearchCriteria();
        searchCriteria.setExperiment(experimentRef);
        CaArrayEntityReference chpFileTypeRef = getChpFileType();
        // CaArrayEntityReference chpFileTypeRef = new CaArrayEntityReference("URN:LSID:caarray.nci.nih.gov:gov.nih.nci.caarray.external.v1_0.data.FileType:AFFYMETRIX_CHP");
        HashSet<CaArrayEntityReference> setOfHybridizationRefs = new HashSet<CaArrayEntityReference>(hybridizationRefs);
        searchCriteria.setExperimentGraphNodes(setOfHybridizationRefs);
        searchCriteria.getTypes().add(chpFileTypeRef);
        List<File> dataFiles = (searchServiceHelper.filesByCriteria(searchCriteria)).list();
        if (dataFiles == null || dataFiles.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private CaArrayEntityReference getChpFileType() throws RemoteException {
        ExampleSearchCriteria<FileType> criteria = new ExampleSearchCriteria<FileType>();
        FileType exampleFileType = new FileType();
        exampleFileType.setName("AFFYMETRIX_CHP");
        criteria.setExample(exampleFileType);
        List<FileType> fileTypes = (client.searchByExample(criteria, null)).getResults();
        FileType chpFileType = fileTypes.iterator().next();
        return chpFileType.getReference();
    }

    private void printAnnotationSet(AnnotationSet annotationSet) {
        // Ordered list of row headers (categories)
        List<Category> categories = annotationSet.getCategories();
        printCategories(categories);
        // List of columns, one per biomaterial/hybridization
        List<AnnotationColumn> annotationColumns = annotationSet.getColumns();
        for (AnnotationColumn annotationColumn : annotationColumns) {
            // Ordered list of annotation values in the column (values are in the same order as row headers/categories)
            printColumnValues(annotationColumn);
        }
    }

    private void printCategories(List<Category> categories) {
        for (Category category : categories) {
            System.out.print(category.getName() + " ");
        }
        System.out.println();
    }

    private void printColumnValues(AnnotationColumn annotationColumn) {
        AbstractExperimentGraphNode node = annotationColumn.getNode();
        System.out.println("Annotation values for biomaterial/hybridization " + node.getName() + ":");

        List<AnnotationValueSet> annotationValues = annotationColumn.getValueSets();
        for (AnnotationValueSet valuesForOneCategory : annotationValues) {
            System.out.print(valuesForOneCategory.getCategory().getName() + " = ");
            // There will usually be only one value, but there may be multiple in some cases.
            // E.g., if a hybridization originated from 2 samples, each with conflicting values for a category, there will be multiple values.
            Set<AbstractValue> values = valuesForOneCategory.getValues();
            for (AbstractValue value : values) {
                if (TermValue.class.equals(value.getClass())) {
                    TermValue termValue = (TermValue) value;
                    System.out.println(termValue.getTerm().getValue() + ",");
                } else if (MeasurementValue.class.equals(value.getClass())) {
                    MeasurementValue measurementValue = (MeasurementValue) value;
                    System.out.print(measurementValue.getMeasurement() + ",");
                } else if (UserDefinedValue.class.equals(value.getClass())) {
                    UserDefinedValue freeTextValue = (UserDefinedValue) value;
                    System.out.print(freeTextValue.getValue() + ",");
                }
            }
            System.out.println();
        }
    }
}
