//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

 import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabOntologyCategory;
import gov.nih.nci.caarray.magetab.idf.IdfRowType;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;

/**
 * Tests the MAGE-TAB exporter by creating different experiment structures, exporting to MAGE-TAB and verifying
 * resulting IDF and/or SDRF.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class MageTabExporterTest extends AbstractServiceTest {
    /**
     * The MAGE-TAB exporter being tested.
     */
    private final MageTabExporter exporter = new MageTabExporterBean();
    private File idfFile;
    private File sdrfFile;

    /**
     * Experiment nodes and attributes used in the tests.
     */
    private static final String SOURCE_BASENAME = "TestSourceForTranslator";
    private static final String SAMPLE_BASENAME = "TestSampleForTranslator";
    private static final String EXTRACT_BASENAME = "TestExtractForTranslator";
    private static final String LABELED_EXTRACT_BASENAME = "TestLabeledExtractForTranslator";
    private static final String HYBRIDIZATION_BASENAME = "TestHybridizationForTranslator";
    private static final String EXPERIMENT_TITLE = "TestExperiment for export";
    private static final String EXPERIMENT_DESCRIPTION = "TestExperiment description: Investigation of acute myeloid leukemia";
    private static final String MATERIAL_TYPE_VALUE = "total_rna";
    private static final String CELL_TYPE_VALUE = "B_lymphoblast";
    private static final String DISEASE_STATE_VALUE = "Acute Myeloid Leukemia";
    private static final String TISSUE_SITE_VALUE = "Lung";
    private static final String PROVIDER_ORGANIZATION = "TestProvider";
    private static final String LABEL_VALUE = "biotin";
    private static final String SPECIAL_CHARACTERISTIC_CATEGORY = "TestCharacteristicCategory";
    private static final String SPECIAL_CHARACTERISTIC_VALUE = "TestCharacteristicValue";
    private static final String MEASUREMENT_CHARACTERISTIC_CATEGORY = "SurvivalTime";
    private static final String MEASUREMENT_CHARACTERISTIC_VALUE = "14";
    private static final String UNIT_CATEGORY = "TermUnit";
    private static final String UNIT_VALUE = "weeks";
    private static final String MAGETAB_FILE_BASENAME = "MageTabExporterTest";
    private static final String IDF_FILE_SUFFIX = ".idf";
    private static final String SDRF_FILE_SUFFIX = ".sdrf";
    private static final String TEST_EXTERNAL_ID = "TestExternalId";

    /**
     * Temporary variables used while verifying correctness of generated MAGE-TAB.
     */
    private int currColumnNum = -1;
    private int currSourceIndex = -1;
    private int currSampleIndex = -1;
    private int currExtractIndex = -1;
    private int currLabeledExtractIndex = -1;
    private int currHybridizationIndex = -1;
    private int currProviderIndex = -1;
    private int currSourceMaterialTypeIndex = -1;
    private int currSourceMaterialTypeTermSourceRefIndex = -1;
    private int currSourceCellTypeIndex = -1;
    private int currSourceDiseaseStateIndex = -1;
    private int currSourceTissueSiteIndex = -1;
    private int currSampleDiseaseStateIndex = -1;
    private int currSampleSpecialCharacteristicIndex = -1;
    private int currLabeledExtractSpecialCharacteristicIndex = -1;
    private int currCharacteristicUnitIndex = -1;
    private int currLabelIndex = -1;
    private int currExternalIdCharacteristicIndex = -1;

    /**
     * Creates the temporary files that will hold the exported MAGE-TAB.
     */
    @Before
    public void setup() throws IOException {
        idfFile = File.createTempFile(MAGETAB_FILE_BASENAME, IDF_FILE_SUFFIX);
        sdrfFile = File.createTempFile(MAGETAB_FILE_BASENAME, SDRF_FILE_SUFFIX);
        idfFile.deleteOnExit();
        sdrfFile.deleteOnExit();
    }

    /**
     * Deletes the temporary files that hold the exported MAGE-TAB.
     */
    @After
    public void cleanup() throws IOException {
        // Delete temporary files.
        idfFile.delete();
        sdrfFile.delete();
    }
    
    /**
     * Tests MAGE-TAB export of an experiment to ensure that ARRAY-2166 is resolved.
     *
     * @throws IOException
     */
    @Test
    public void testDefect_Array2166() throws IOException {
    	/*
    	 *  Mock the RawArrayData so that the getDataFile() always returns null.
    	 *  This will ensure that the call 
    	 *      final CaArrayFile file = ((RawArrayData) caarrayNode).getDataFile();
    	 *   in file MageTabExporterBean.java, method createNode(AbstractArrayData ...) will return null.
    	 */
    	RawArrayData file = mock(RawArrayData.class);
    	when(file.getDataFile()).thenReturn(null);
    	
        // Create experiment.
        Experiment experiment = createExperimentWithOneToOneChains();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyRowsOneToOne(reader, 2);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of an experiment that contains one-to-one biomaterial-data chains.
     *
     * @throws IOException
     */
    @Test
    public void testExportOneToOneChains() throws IOException {
        // Create experiment.
        Experiment experiment = createExperimentWithOneToOneChains();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyRowsOneToOne(reader, 2);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of an experiment that contains one-to-many biomaterial-data chains.
     *
     * @throws IOException
     */
    @Test
    public void testExportOneToManyChains() throws IOException {
        // Create experiment.
        Experiment experiment = createExperimentWithOneToManyChains();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyRowsOneToMany(reader, 4);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of an experiment that contains many-to-one biomaterial-data chains.
     *
     * @throws IOException
     */
    @Test
    public void testExportManyToOneChains() throws IOException {
        // Create experiment.
        Experiment experiment = createExperimentWithManyToOneChains();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyRowsManyToOne(reader, 2);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of an experiment that contains various biomaterial characteristics.
     *
     * @throws IOException
     */
    @Test
    public void testExportBiomaterialCharacteristics() throws IOException {
        // Create experiment.
        Experiment experiment = createExperimentWithBiomaterialCharacteristics();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyBiomaterialCharacteristics(reader);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of an experiment that contains overview information like title and description.
     *
     * @throws IOException
     */
    @Test
    public void testExportExperimentOverview() throws IOException {
        // Create experiment.
        Experiment experiment = createExperimentWithOverviewInformation();

        // Call the exporter to export the contents of the experiment to MAGE-TAB.
        exporter.exportToMageTab(experiment, idfFile, sdrfFile);

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(idfFile);
        verifyExperimentOverview(reader);
        reader.close();
    }
    
    /**
     * Create node method, array data.
     */
    @Test
    public void createArrayData() {
        assertCreateNode(new RawArrayData(), Boolean.FALSE, new int[] {1, 0, 0, 0});
    }
    
    /**
     * Create node method, array data with a null file.
     */
    @Test
    public void createArrayDataNullFile() {
        assertCreateNode(new RawArrayData(), null, new int[] {1, 0, 0, 0});
    }
    
    /**
     * Create node method, array data matrix.
     */
    @Test
    public void createArrayDataMatrix() {
        assertCreateNode(new RawArrayData(), Boolean.TRUE, new int[] {0, 1, 0, 0});
    }
    
    /**
     * Create node method, derived array data.
     */
    @Test
    public void createDerivedArrayData() {
        assertCreateNode(new DerivedArrayData(), Boolean.FALSE, new int[] {0, 0, 1, 0});
    }
    
    /**
     * Create node method, derived array data matrix.
     */
    @Test
    public void createDerivedArrayDataMatrix() {
        assertCreateNode(new DerivedArrayData(), Boolean.TRUE, new int[] {0, 0, 0, 1});
    }
    
    private void assertCreateNode(AbstractArrayData node, Boolean isDataMatrix, int[] expectedCounts) {
        Set<ArrayDataFile> arrayDataFiles = new HashSet<ArrayDataFile>();
        Set<ArrayDataMatrixFile> arrayDataMatrixFiles = new HashSet<ArrayDataMatrixFile>();
        Set<DerivedArrayDataMatrixFile> derivedArrayDataMatrixFiles = new HashSet<DerivedArrayDataMatrixFile>();
        Set<DerivedArrayDataFile> derivedArrayDataFiles = new HashSet<DerivedArrayDataFile>();

        if (isDataMatrix != null) {
            FileType fileType = mock(FileType.class);
            when(fileType.isDataMatrix()).thenReturn(isDataMatrix);
        
            CaArrayFile dataFile = mock(CaArrayFile.class);
            when(dataFile.getFileType()).thenReturn(fileType);
        
            node.setDataFile(dataFile);
        }
        
        AbstractSampleDataRelationshipNode newNode = MageTabExporterBean.createNode(node, arrayDataFiles, 
                arrayDataMatrixFiles, derivedArrayDataMatrixFiles, derivedArrayDataFiles);
        assertNotNull(newNode);
        assertEquals(expectedCounts[0], arrayDataFiles.size());
        assertEquals(expectedCounts[1], arrayDataMatrixFiles.size());
        assertEquals(expectedCounts[2], derivedArrayDataFiles.size());        
        assertEquals(expectedCounts[3], derivedArrayDataMatrixFiles.size());
    }
    

    private Experiment createExperimentWithOneToOneChains() {
        Experiment experiment = new Experiment();

        addOneToOneBiomaterialChain(experiment, "0");
        addOneToOneBiomaterialChain(experiment, "1");
        return experiment;
    }

    private Experiment createExperimentWithOneToManyChains() {
        Experiment experiment = new Experiment();

        addOneToManyBiomaterialChain(experiment, "1");
        return experiment;
    }

    private Experiment createExperimentWithManyToOneChains() {
        Experiment experiment = new Experiment();

        addManyToOneBiomaterialChain(experiment, "1");
        return experiment;
    }

    private Experiment createExperimentWithBiomaterialCharacteristics() {
        // Create an experiment with biomaterials.
        Experiment experiment = createExperimentWithOneToOneChains();

        // Create ontology categories for the predefined characteristics.
        Set<ExperimentOntologyCategory> allOntologyCategories = new HashSet<ExperimentOntologyCategory>();
        allOntologyCategories.add(ExperimentOntologyCategory.MATERIAL_TYPE);
        allOntologyCategories.add(ExperimentOntologyCategory.CELL_TYPE);
        allOntologyCategories.add(ExperimentOntologyCategory.DISEASE_STATE);
        allOntologyCategories.add(ExperimentOntologyCategory.ORGANISM_PART);
        Set<ExperimentOntologyCategory> oneOntologyCategory = new HashSet<ExperimentOntologyCategory>();
        oneOntologyCategory.add(ExperimentOntologyCategory.DISEASE_STATE);

        // Add Source provider and predefined characteristics.
        Organization provider = new Organization();
        provider.setName(PROVIDER_ORGANIZATION);
        for (Source source : experiment.getSources()) {
            source.getProviders().add(provider);
            addPredefinedCharacteristics(source, allOntologyCategories);
            source.setExternalId(TEST_EXTERNAL_ID);
        }

        // Add Sample predefined characteristics and special characteristics with a term source.
        for (Sample sample : experiment.getSamples()) {
            addPredefinedCharacteristics(sample, oneOntologyCategory);
            addSpecialCharacteristics(sample, true);
            addMeasurementCharacteristic(sample);
        }

        // Leave Extracts with no characteristics.

        // Add LabeledExtract label and special characteristics without a term source.
        Term label = new Term();
        label.setValue(LABEL_VALUE);
        Category category = new Category();
        category.setName(MageTabOntologyCategory.LABEL_COMPOUND.getCategoryName());
        label.setCategory(category);
        for (LabeledExtract labeledExtract : experiment.getLabeledExtracts()) {
            labeledExtract.setLabel(label);
            addSpecialCharacteristics(labeledExtract, false);
        }
        return experiment;
    }

    private Experiment createExperimentWithOverviewInformation() {
        Experiment experiment = new Experiment();
        experiment.setTitle(EXPERIMENT_TITLE);
        experiment.setDescription(EXPERIMENT_DESCRIPTION);
        return experiment;
    }

    private void addOneToOneBiomaterialChain(Experiment experiment, String suffix) {
        Source source = new Source();
        source.setName(SOURCE_BASENAME + suffix);
        Sample sample = new Sample();
        sample.setName(SAMPLE_BASENAME + suffix);
        Extract extract = new Extract();
        extract.setName(EXTRACT_BASENAME + suffix);
        LabeledExtract labeledExtract = new LabeledExtract();
        labeledExtract.setName(LABELED_EXTRACT_BASENAME + suffix);
        Hybridization hybridization = new Hybridization();
        hybridization.setName(HYBRIDIZATION_BASENAME + suffix);

        RawArrayData rad = new RawArrayData();
        rad.setName("rowArrayDataName");
        rad.addHybridization(hybridization);
        hybridization.getRawDataCollection().add(rad);
        
        hybridization.getLabeledExtracts().add(labeledExtract);
        labeledExtract.getHybridizations().add(hybridization);
        labeledExtract.getExtracts().add(extract);
        extract.getLabeledExtracts().add(labeledExtract);
        extract.getSamples().add(sample);
        sample.getExtracts().add(extract);
        sample.getSources().add(source);
        source.getSamples().add(sample);

        experiment.getSources().add(source);
        experiment.getSamples().add(sample);
        experiment.getExtracts().add(extract);
        experiment.getLabeledExtracts().add(labeledExtract);
        experiment.getHybridizations().add(hybridization);
    }

    private void addOneToManyBiomaterialChain(Experiment experiment, String suffix) {
        Source source = new Source();
        source.setName(SOURCE_BASENAME + suffix);
        Sample sample = new Sample();
        sample.setName(SAMPLE_BASENAME + suffix);
        Extract extract1 = new Extract();
        extract1.setName(EXTRACT_BASENAME + suffix + "a");
        Extract extract2 = new Extract();
        extract2.setName(EXTRACT_BASENAME + suffix + "b");
        LabeledExtract labeledExtract1 = new LabeledExtract();
        labeledExtract1.setName(LABELED_EXTRACT_BASENAME + suffix + "a");
        LabeledExtract labeledExtract2 = new LabeledExtract();
        labeledExtract2.setName(LABELED_EXTRACT_BASENAME + suffix + "b");
        Hybridization hybridization1 = new Hybridization();
        hybridization1.setName(HYBRIDIZATION_BASENAME + suffix + "aa");
        Hybridization hybridization2 = new Hybridization();
        hybridization2.setName(HYBRIDIZATION_BASENAME + suffix + "ab");
        Hybridization hybridization3 = new Hybridization();
        hybridization3.setName(HYBRIDIZATION_BASENAME + suffix + "ba");
        Hybridization hybridization4 = new Hybridization();
        hybridization4.setName(HYBRIDIZATION_BASENAME + suffix + "bb");

        linkNodesOneToMany(source, sample, extract1, extract2, labeledExtract1, labeledExtract2, hybridization1,
                hybridization2, hybridization3, hybridization4);
        linkExperimentOneToMany(experiment, source, sample, extract1, extract2, labeledExtract1, labeledExtract2,
                hybridization1, hybridization2, hybridization3, hybridization4);
    }

    private void linkExperimentOneToMany(Experiment experiment, Source source, Sample sample, Extract extract1,
            Extract extract2, LabeledExtract labeledExtract1, LabeledExtract labeledExtract2,
            Hybridization hybridization1, Hybridization hybridization2, Hybridization hybridization3,
            Hybridization hybridization4) {
        experiment.getSources().add(source);
        experiment.getSamples().add(sample);
        experiment.getExtracts().add(extract1);
        experiment.getExtracts().add(extract2);
        experiment.getLabeledExtracts().add(labeledExtract1);
        experiment.getLabeledExtracts().add(labeledExtract2);
        experiment.getHybridizations().add(hybridization1);
        experiment.getHybridizations().add(hybridization2);
        experiment.getHybridizations().add(hybridization3);
        experiment.getHybridizations().add(hybridization4);
    }

    private void linkNodesOneToMany(Source source, Sample sample, Extract extract1, Extract extract2,
            LabeledExtract labeledExtract1, LabeledExtract labeledExtract2, Hybridization hybridization1,
            Hybridization hybridization2, Hybridization hybridization3, Hybridization hybridization4) {
        hybridization1.getLabeledExtracts().add(labeledExtract1);
        labeledExtract1.getHybridizations().add(hybridization1);
        hybridization2.getLabeledExtracts().add(labeledExtract1);
        labeledExtract1.getHybridizations().add(hybridization2);
        hybridization3.getLabeledExtracts().add(labeledExtract2);
        labeledExtract2.getHybridizations().add(hybridization3);
        hybridization4.getLabeledExtracts().add(labeledExtract2);
        labeledExtract2.getHybridizations().add(hybridization4);

        labeledExtract1.getExtracts().add(extract1);
        extract1.getLabeledExtracts().add(labeledExtract1);
        labeledExtract2.getExtracts().add(extract2);
        extract2.getLabeledExtracts().add(labeledExtract2);

        extract1.getSamples().add(sample);
        sample.getExtracts().add(extract1);
        extract2.getSamples().add(sample);
        sample.getExtracts().add(extract2);

        sample.getSources().add(source);
        source.getSamples().add(sample);
    }

    private void addManyToOneBiomaterialChain(Experiment experiment, String suffix) {
        Source source = new Source();
        source.setName(SOURCE_BASENAME + suffix);
        Sample sample1 = new Sample();
        sample1.setName(SAMPLE_BASENAME + suffix + "a");
        Sample sample2 = new Sample();
        sample2.setName(SAMPLE_BASENAME + suffix + "b");
        Extract extract1 = new Extract();
        extract1.setName(EXTRACT_BASENAME + suffix + "a");
        Extract extract2 = new Extract();
        extract2.setName(EXTRACT_BASENAME + suffix + "b");
        LabeledExtract labeledExtract1 = new LabeledExtract();
        labeledExtract1.setName(LABELED_EXTRACT_BASENAME + suffix + "a");
        LabeledExtract labeledExtract2 = new LabeledExtract();
        labeledExtract2.setName(LABELED_EXTRACT_BASENAME + suffix + "b");
        Hybridization hybridization = new Hybridization();
        hybridization.setName(HYBRIDIZATION_BASENAME + suffix);

        linkNodesManyToOne(source, sample1, sample2, extract1, extract2, labeledExtract1, labeledExtract2,
                hybridization);
        linkExperimentManyToOne(experiment, source, sample1, sample2, extract1, extract2, labeledExtract1,
                labeledExtract2, hybridization);
    }

    private void linkExperimentManyToOne(Experiment experiment, Source source, Sample sample1, Sample sample2,
            Extract extract1, Extract extract2, LabeledExtract labeledExtract1, LabeledExtract labeledExtract2,
            Hybridization hybridization) {
        experiment.getSources().add(source);
        experiment.getSamples().add(sample1);
        experiment.getSamples().add(sample2);
        experiment.getExtracts().add(extract1);
        experiment.getExtracts().add(extract2);
        experiment.getLabeledExtracts().add(labeledExtract1);
        experiment.getLabeledExtracts().add(labeledExtract2);
        experiment.getHybridizations().add(hybridization);
    }

    private void linkNodesManyToOne(Source source, Sample sample1, Sample sample2, Extract extract1, Extract extract2,
            LabeledExtract labeledExtract1, LabeledExtract labeledExtract2, Hybridization hybridization) {
        hybridization.getLabeledExtracts().add(labeledExtract1);
        labeledExtract1.getHybridizations().add(hybridization);
        hybridization.getLabeledExtracts().add(labeledExtract2);
        labeledExtract2.getHybridizations().add(hybridization);

        labeledExtract1.getExtracts().add(extract1);
        extract1.getLabeledExtracts().add(labeledExtract1);
        labeledExtract2.getExtracts().add(extract2);
        extract2.getLabeledExtracts().add(labeledExtract2);

        extract1.getSamples().add(sample1);
        sample1.getExtracts().add(extract1);
        extract2.getSamples().add(sample2);
        sample2.getExtracts().add(extract2);

        sample1.getSources().add(source);
        source.getSamples().add(sample1);
        sample2.getSources().add(source);
        source.getSamples().add(sample2);
    }

    private void addPredefinedCharacteristics(AbstractBioMaterial biomaterial,
            Set<ExperimentOntologyCategory> categories) {
        Term term = null;
        for (ExperimentOntologyCategory category : categories) {
            switch (category) {
            case MATERIAL_TYPE:
                term = createBiomaterialTerm(ExperimentOntologyCategory.MATERIAL_TYPE, MATERIAL_TYPE_VALUE);
                biomaterial.setMaterialType(term);
                break;
            case CELL_TYPE:
                term = createBiomaterialTerm(ExperimentOntologyCategory.CELL_TYPE, CELL_TYPE_VALUE);
                biomaterial.setCellType(term);
                break;
            case DISEASE_STATE:
                term = createBiomaterialTerm(ExperimentOntologyCategory.DISEASE_STATE, DISEASE_STATE_VALUE);
                biomaterial.setDiseaseState(term);
                break;
            case ORGANISM_PART:
                term = createBiomaterialTerm(ExperimentOntologyCategory.ORGANISM_PART, TISSUE_SITE_VALUE);
                biomaterial.setTissueSite(term);
                break;
            default:
                // Should never get here
                break;
            }
        }
    }

    private Term createBiomaterialTerm(ExperimentOntologyCategory ontologyCategory, String value) {
        Term term = new Term();
        Category category = new Category();
        TermSource termSource = new TermSource();
        termSource.setName(ontologyCategory.getOntology().getOntologyName());
        termSource.setVersion(ontologyCategory.getOntology().getVersion());
        category.setName(ontologyCategory.getCategoryName());
        category.setSource(termSource);
        term.setValue(value);
        term.setCategory(category);
        term.setSource(termSource);
        return term;
    }

    // Create a special characteristic with or without a term source.
    private void addSpecialCharacteristics(AbstractBioMaterial biomaterial, boolean useTermSource) {
        Term term = new Term();
        Category category = new Category();
        category.setName(SPECIAL_CHARACTERISTIC_CATEGORY);
        term.setValue(SPECIAL_CHARACTERISTIC_VALUE);
        term.setCategory(category);
        if (useTermSource) {
            TermSource termSource = new TermSource();
            termSource.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
            termSource.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());
            category.setSource(termSource);
            term.setSource(termSource);
        }
        AbstractCharacteristic characteristic = new TermBasedCharacteristic(category, term, null);
        characteristic.setBioMaterial(biomaterial);
        biomaterial.getCharacteristics().add(characteristic);
    }

    // Create a measurement characteristic.
    private void addMeasurementCharacteristic(AbstractBioMaterial biomaterial) {
        Term unit = new Term();
        Category category = new Category();
        category.setName(UNIT_CATEGORY);
        unit.setValue(UNIT_VALUE);
        unit.setCategory(category);
        TermSource termSource = new TermSource();
        termSource.setName(ExperimentOntology.MGED_ONTOLOGY.getOntologyName());
        termSource.setVersion(ExperimentOntology.MGED_ONTOLOGY.getVersion());
        category.setSource(termSource);
        unit.setSource(termSource);
        Category characteristicCategory = new Category();
        characteristicCategory.setName(MEASUREMENT_CHARACTERISTIC_CATEGORY);
        AbstractCharacteristic characteristic = new MeasurementCharacteristic(characteristicCategory,
                Float.parseFloat(MEASUREMENT_CHARACTERISTIC_VALUE), unit);
        characteristic.setBioMaterial(biomaterial);
        biomaterial.getCharacteristics().add(characteristic);
    }

    private void verifyRowsOneToOne(DelimitedFileReader reader, int expectedNumRows) throws IOException {
        verifyHeader(reader);

        // Result must have the appropriate number of rows.
        int numRows = 0;
        boolean foundRow = false;
        while (reader.hasNextLine()) {
            List<String> line = reader.nextLine();
            String sourceName = line.get(currSourceIndex);
            // Verify the correctness of the biomaterial-data chain in one row.
            if ((SOURCE_BASENAME + "1").equals(sourceName)) {
                foundRow = verifyChainOneToOne(line);
            }
            numRows++;
        }
        assertEquals(numRows, expectedNumRows);
        assertTrue("Did not find row.", foundRow);
    }

    private boolean verifyChainOneToOne(List<String> line) {
        boolean foundRow;
        assertEquals(SAMPLE_BASENAME + "1", line.get(currSampleIndex));
        assertEquals(EXTRACT_BASENAME + "1", line.get(currExtractIndex));
        assertEquals(LABELED_EXTRACT_BASENAME + "1", line.get(currLabeledExtractIndex));
        assertEquals(HYBRIDIZATION_BASENAME + "1", line.get(currHybridizationIndex));
        foundRow = true;
        return foundRow;
    }

    private void verifyRowsOneToMany(DelimitedFileReader reader, int expectedNumRows) throws IOException {
        verifyHeader(reader);

        // Result must have the appropriate number of rows.
        int numRows = 0;
        boolean foundRow = false;
        while (reader.hasNextLine()) {
            List<String> line = reader.nextLine();
            // Verify the correctness of the biomaterial-data chain in one row.
            if ((HYBRIDIZATION_BASENAME + "1ba").equals(line.get(currHybridizationIndex))) {
                foundRow = verifyChainOneToMany(line);
            }
            numRows++;
        }
        assertEquals(numRows, expectedNumRows);
        assertTrue("Did not find row.", foundRow);
    }

    private boolean verifyChainOneToMany(List<String> line) {
        boolean foundRow;
        assertEquals(SOURCE_BASENAME + "1", line.get(currSourceIndex));
        assertEquals(SAMPLE_BASENAME + "1", line.get(currSampleIndex));
        assertEquals(EXTRACT_BASENAME + "1b", line.get(currExtractIndex));
        assertEquals(LABELED_EXTRACT_BASENAME + "1b", line.get(currLabeledExtractIndex));
        foundRow = true;
        return foundRow;
    }

    private void verifyRowsManyToOne(DelimitedFileReader reader, int expectedNumRows) throws IOException {
        verifyHeader(reader);

        // Result must have the appropriate number of rows.
        int numRows = 0;
        boolean foundRow = false;
        while (reader.hasNextLine()) {
            List<String> line = reader.nextLine();
            // Verify the correctness of the biomaterial-data chain in one row.
            if ((SAMPLE_BASENAME + "1b").equals(line.get(currSampleIndex))) {
                foundRow = verifyChainManyToOne(line);
            }
            numRows++;
        }
        assertEquals(numRows, expectedNumRows);
        assertTrue("Did not find row.", foundRow);
    }

    private boolean verifyChainManyToOne(List<String> line) {
        boolean foundRow;
        assertEquals(SOURCE_BASENAME + "1", line.get(currSourceIndex));
        assertEquals(EXTRACT_BASENAME + "1b", line.get(currExtractIndex));
        assertEquals(LABELED_EXTRACT_BASENAME + "1b", line.get(currLabeledExtractIndex));
        assertEquals(HYBRIDIZATION_BASENAME + "1", line.get(currHybridizationIndex));
        foundRow = true;
        return foundRow;
    }

    private void verifyHeader(DelimitedFileReader reader) throws IOException {
        // Result must have a header containing column names.
        assertTrue(reader.hasNextLine());
        List<String> header = reader.nextLine();
        getColumnNamesFromHeader(header);
    }

    private void getColumnNamesFromHeader(List<String> header) {
        currColumnNum = 0;
        SdrfColumnType currNode = SdrfColumnType.SOURCE_NAME;
        for (String columnName : header) {
            if (SdrfColumnType.SOURCE_NAME.toString().equals(columnName)) {
                currSourceIndex = currColumnNum;
                currNode = SdrfColumnType.SOURCE_NAME;
            } else if (SdrfColumnType.SAMPLE_NAME.toString().equals(columnName)) {
                currSampleIndex = currColumnNum;
                currNode = SdrfColumnType.SAMPLE_NAME;
            } else if (SdrfColumnType.EXTRACT_NAME.toString().equals(columnName)) {
                currExtractIndex = currColumnNum;
                currNode = SdrfColumnType.EXTRACT_NAME;
            } else if (SdrfColumnType.LABELED_EXTRACT_NAME.toString().equals(columnName)) {
                currLabeledExtractIndex = currColumnNum;
                currNode = SdrfColumnType.LABELED_EXTRACT_NAME;
            } else if (SdrfColumnType.HYBRIDIZATION_NAME.toString().equals(columnName)) {
                currHybridizationIndex = currColumnNum;
            } else if (SdrfColumnType.PROVIDER.toString().equals(columnName)) {
                currProviderIndex = currColumnNum;
            } else if (SdrfColumnType.MATERIAL_TYPE.toString().equals(columnName)) {
                // Only Source's material type expected.
                currSourceMaterialTypeIndex = currColumnNum;
                String nextColumnName = header.get(currSourceMaterialTypeIndex + 1);
                if (nextColumnName.equals("Term Source REF")) {
                    currSourceMaterialTypeTermSourceRefIndex = currSourceMaterialTypeIndex + 1;
                }
            } else if (columnName.startsWith(SdrfColumnType.CHARACTERISTICS.toString())) {
                String categoryName = columnName.substring(columnName.indexOf('[') + 1, columnName.length() - 1);
                switch (currNode) {
                case SOURCE_NAME:
                    if (ExperimentOntologyCategory.MATERIAL_TYPE.getCategoryName().equals(categoryName)) {
                        currSourceMaterialTypeIndex = currColumnNum;
                    } else if (ExperimentOntologyCategory.CELL_TYPE.getCategoryName().equals(categoryName)) {
                        currSourceCellTypeIndex = currColumnNum;
                    } else if (ExperimentOntologyCategory.DISEASE_STATE.getCategoryName().equals(categoryName)) {
                        currSourceDiseaseStateIndex = currColumnNum;
                    } else if (ExperimentOntologyCategory.ORGANISM_PART.getCategoryName().equals(categoryName)) {
                        currSourceTissueSiteIndex = currColumnNum;
                    }
                    break;
                case SAMPLE_NAME:
                    if (ExperimentOntologyCategory.DISEASE_STATE.getCategoryName().equals(categoryName)) {
                        currSampleDiseaseStateIndex = currColumnNum;
                    } else if (SPECIAL_CHARACTERISTIC_CATEGORY.equals(categoryName)) {
                        currSampleSpecialCharacteristicIndex = currColumnNum;
                    }
                    break;
                case EXTRACT_NAME:
                    // No characteristics expected here.
                    break;
                case LABELED_EXTRACT_NAME:
                    if (SPECIAL_CHARACTERISTIC_CATEGORY.equals(categoryName)) {
                        currLabeledExtractSpecialCharacteristicIndex = currColumnNum;
                    }
                    break;
                default:
                    // Do nothing
                    break;
                }
                if (categoryName.equals(ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName())) {
                    currExternalIdCharacteristicIndex = currColumnNum;
                }
            } else if (SdrfColumnType.UNIT.toString().equals(columnName)) {
                currCharacteristicUnitIndex = currColumnNum;
            } else if (SdrfColumnType.LABEL.toString().equals(columnName)) {
                currLabelIndex = currColumnNum;
            }
            currColumnNum++;
        }
    }

    private void verifyBiomaterialCharacteristics(DelimitedFileReader reader) throws IOException {
        // Parse header.
        verifyHeader(reader);

        // Verify the correctness of the biomaterial-data chain in the first row.
        assertTrue("No value rows in SDRF.", reader.hasNextLine());
        List<String> line = reader.nextLine();

        // Source should have provider and predefined characteristics.
        assertEquals(PROVIDER_ORGANIZATION, line.get(currProviderIndex));
        assertEquals(MATERIAL_TYPE_VALUE, line.get(currSourceMaterialTypeIndex));
        assertTrue(StringUtils.isNotEmpty(line.get(currSourceMaterialTypeTermSourceRefIndex)));
        assertEquals(CELL_TYPE_VALUE, line.get(currSourceCellTypeIndex));
        assertEquals(DISEASE_STATE_VALUE, line.get(currSourceDiseaseStateIndex));
        assertEquals(TISSUE_SITE_VALUE, line.get(currSourceTissueSiteIndex));

        // Sample should have disease state and special characteristics with a term source.
        assertEquals(DISEASE_STATE_VALUE, line.get(currSampleDiseaseStateIndex));
        assertEquals(SPECIAL_CHARACTERISTIC_VALUE, line.get(currSampleSpecialCharacteristicIndex));
        assertEquals(UNIT_VALUE, line.get(currCharacteristicUnitIndex));

        // Extract should have no characteristics.
        // LabeledExtract should have label and special characteristics without a term source.
        assertEquals(LABEL_VALUE, line.get(currLabelIndex));
        assertEquals(SPECIAL_CHARACTERISTIC_VALUE, line.get(currLabeledExtractSpecialCharacteristicIndex));
        
        // external ID should be set
        assertEquals(TEST_EXTERNAL_ID, line.get(currExternalIdCharacteristicIndex));
    }

    private void verifyExperimentOverview(DelimitedFileReader reader) throws IOException {
        while (reader.hasNextLine()) {
            List<String> line = reader.nextLine();
            String rowHeader = line.get(0);
            if (IdfRowType.INVESTIGATION_TITLE.toString().equals(rowHeader)) {
                assertTrue("Did not find experiment title.", (EXPERIMENT_TITLE.equals(line.get(1))));
            } else if (IdfRowType.EXPERIMENT_DESCRIPTION.toString().equals(rowHeader)) {
                assertTrue("Did not find experiment description.", (EXPERIMENT_DESCRIPTION.equals(line.get(1))));
            }
        }
    }
}
