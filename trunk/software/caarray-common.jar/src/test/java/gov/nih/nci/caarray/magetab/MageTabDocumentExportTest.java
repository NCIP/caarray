/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.IdfRowType;
import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Extract;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.magetab.sdrf.LabeledExtract;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocumentNodes;
import gov.nih.nci.caarray.magetab.sdrf.Source;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the ability of a MAGE-TAB document set to export itself into files
 * for different experiment structures.

 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class MageTabDocumentExportTest {
    private MageTabDocumentSet documentSet;
    private File idfFile;
    private File sdrfFile;

    private static final String MAGETAB_FILE_BASENAME = "MageTabDocumentExportTest";
    private static final String IDF_FILE_SUFFIX = ".idf";
    private static final String SDRF_FILE_SUFFIX = ".sdrf";

    /**
     * Nodes and attributes used in the tests.
     */
    private static final String SOURCE_BASENAME = "TestSourceForTranslator";
    private static final String SAMPLE_BASENAME = "TestSampleForTranslator";
    private static final String EXTRACT_BASENAME = "TestExtractForTranslator";
    private static final String LABELED_EXTRACT_BASENAME = "TestLabeledExtractForTranslator";
    private static final String HYBRIDIZATION_BASENAME = "TestHybridizationForTranslator";
    private static final String EXPERIMENT_TITLE = "TestExperiment for export";
    private static final String EXPERIMENT_DESCRIPTION = "TestExperiment description: Investigation of acute myeloid leukemia";
    private static final String MATERIAL_TYPE_VALUE = "total_rna";
    private static final String PROVIDER_ORGANIZATION = "TestProvider";
    private static final String LABEL_VALUE = "biotin";
    private static final String SPECIAL_CHARACTERISTIC_CATEGORY = "TestCharacteristicCategory";
    private static final String SPECIAL_CHARACTERISTIC_VALUE = "TestCharacteristicValue";
    private static final String MEASUREMENT_CHARACTERISTIC_CATEGORY = "SurvivalTime";
    private static final String MEASUREMENT_CHARACTERISTIC_VALUE = "14";
    private static final String UNIT_CATEGORY = "TermUnit";
    private static final String UNIT_VALUE = "weeks";

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
    private int currSampleSpecialCharacteristicIndex = -1;
    private int currMeasurementCharacteristicIndex = -1;
    private int currCharacteristicUnitIndex = -1;
    private int currLabelIndex = -1;

    /**
     * Creates the document set and the temporary files that will hold the exported MAGE-TAB.
     */
    @Before
    public void setup() throws IOException {
        idfFile = File.createTempFile(MAGETAB_FILE_BASENAME, IDF_FILE_SUFFIX);
        sdrfFile = File.createTempFile(MAGETAB_FILE_BASENAME, SDRF_FILE_SUFFIX);
        idfFile.deleteOnExit();
        sdrfFile.deleteOnExit();
        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(new JavaIOFileRef(idfFile));
        fileSet.addSdrf(new JavaIOFileRef(sdrfFile));
        documentSet = new MageTabDocumentSet(fileSet);
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
     * Tests MAGE-TAB export of one-to-one biomaterial-data chains.
     *
     * @throws IOException
     */
    @Test
    public void testExportOneToOneChains() throws IOException {
        initializeWithOneToOneChain(false);
        documentSet.export();

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyRowsOneToOne(reader, 1);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of one-to-one biomaterial-data chains.
     *
     * @throws IOException
     */
    @Test(expected = MageTabExportException.class)
    public void testUnitializedSdrfNodes() throws IOException {
        initializeWithOneToOneChain(false, false);
    }


    /**
     * Tests MAGE-TAB export of various biomaterial characteristics.
     *
     * @throws IOException
     */
    @Test
    public void testExportBiomaterialCharacteristics() throws IOException {
        initializeWithOneToOneChain(true);
        documentSet.export();

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(sdrfFile);
        verifyBiomaterialCharacteristics(reader);
        reader.close();
    }

    /**
     * Tests MAGE-TAB export of investigation overview information like title and description.
     *
     * @throws IOException
     */
    @Test
    public void testExportExperimentOverview() throws IOException {
        initializeWithOverview();
        documentSet.export();

        // Verify that the exported MAGE-TAB is correct.
        DelimitedFileReader reader = new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(idfFile);        
        verifyExperimentOverview(reader);
        reader.close();
    }

    private void initializeWithOneToOneChain(boolean includeCharacteristics) {
        initializeWithOneToOneChain(includeCharacteristics, true);
    }

    private void initializeWithOneToOneChain(boolean includeCharacteristics, boolean initializeSdrf) {
        Source source = new Source();
        source.setName(SOURCE_BASENAME);
        Sample sample = new Sample();
        sample.setName(SAMPLE_BASENAME);
        sample.getPredecessors().add(source);
        source.getSuccessors().add(sample);
        Extract extract = new Extract();
        extract.setName(EXTRACT_BASENAME);
        extract.getPredecessors().add(sample);
        sample.getSuccessors().add(extract);
        LabeledExtract labeledExtract = new LabeledExtract();
        labeledExtract.setName(LABELED_EXTRACT_BASENAME);
        labeledExtract.getPredecessors().add(extract);
        extract.getSuccessors().add(labeledExtract);
        Hybridization hybridization = new Hybridization();
        hybridization.setName(HYBRIDIZATION_BASENAME);
        hybridization.getPredecessors().add(labeledExtract);
        labeledExtract.getSuccessors().add(hybridization);

        Set<Source> allSources = new HashSet<Source>();
        allSources.add(source);
        Set<Sample> allSamples = new HashSet<Sample>();
        allSamples.add(sample);
        Set<Extract> allExtracts = new HashSet<Extract>();
        allExtracts.add(extract);
        Set<LabeledExtract> allLabeledExtracts = new HashSet<LabeledExtract>();
        allLabeledExtracts.add(labeledExtract);
        Set<Hybridization> allHybridizations = new HashSet<Hybridization>();
        allHybridizations.add(hybridization);
        Set<ArrayDataFile> allArrayDataFiles = new HashSet<ArrayDataFile>();
        Set<DerivedArrayDataFile> allDerivedArrayDataFiles = new HashSet<DerivedArrayDataFile>();
        Set<ArrayDataMatrixFile> allArrayDataMatrixFiles = new HashSet<ArrayDataMatrixFile>();
        Set<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles = new HashSet<DerivedArrayDataMatrixFile>();

        if (includeCharacteristics) {
            TermSource mo = new TermSource("MGED Ontology");
            // Set source provider and material type.
            Provider provider = new Provider();
            provider.setName(PROVIDER_ORGANIZATION);
            source.getProviders().add(provider);
            OntologyTerm materialType = new OntologyTerm();
            materialType.setValue(MATERIAL_TYPE_VALUE);
            materialType.setTermSource(mo);
            materialType.setCategory(MageTabOntologyCategory.MATERIAL_TYPE.getCategoryName());
            source.setMaterialType(materialType);

            // Set sample characteristics.
            // Term-based characteristic:
            OntologyTerm term = new OntologyTerm();
            term.setValue(SPECIAL_CHARACTERISTIC_VALUE);
            term.setCategory(SPECIAL_CHARACTERISTIC_CATEGORY);
            term.setTermSource(mo);
            Characteristic characteristic = new Characteristic();
            characteristic.setTerm(term);
            characteristic.setCategory(SPECIAL_CHARACTERISTIC_CATEGORY);
            sample.getCharacteristics().add(characteristic);
            // Measurement characteristic:
            Characteristic measurementCharacteristic = new Characteristic();
            measurementCharacteristic.setCategory(MEASUREMENT_CHARACTERISTIC_CATEGORY);
            measurementCharacteristic.setValue(MEASUREMENT_CHARACTERISTIC_VALUE);
            OntologyTerm unit = new OntologyTerm();
            unit.setCategory(UNIT_CATEGORY);
            unit.setValue(UNIT_VALUE);
            unit.setTermSource(mo);
            measurementCharacteristic.setUnit(unit);
            sample.getCharacteristics().add(measurementCharacteristic);

            // Set labeled extract label.
            OntologyTerm label = new OntologyTerm();
            label.setValue(LABEL_VALUE);
            label.setTermSource(mo);
            label.setCategory(MageTabOntologyCategory.LABEL_COMPOUND.getCategoryName());
            labeledExtract.setLabel(label);
        }

        // Initialize the SDRF document with all the object graph nodes.
        SdrfDocument sdrfDocument = documentSet.getSdrfDocuments().iterator().next();
        SdrfDocumentNodes sdrfDocumentNodes = new SdrfDocumentNodes();
        if (initializeSdrf) {
            sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
            sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                    allDerivedArrayDataMatrixFiles);
        }
        sdrfDocument.initializeNodes(sdrfDocumentNodes);
    }

    private void initializeWithOverview() {
        IdfDocument idfDocument = documentSet.getIdfDocuments().iterator().next();
        Investigation investigation = idfDocument.getInvestigation();
        investigation.setTitle(EXPERIMENT_TITLE);
        investigation.setDescription(EXPERIMENT_DESCRIPTION);
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
            if ((SOURCE_BASENAME).equals(sourceName)) {
                foundRow = verifyChainOneToOne(line);
            }
            numRows++;
        }
        assertEquals(numRows, expectedNumRows);
        assertTrue("Did not find row.", foundRow);
    }

    private boolean verifyChainOneToOne(List<String> line) {
        boolean foundRow;
        assertEquals(SAMPLE_BASENAME, line.get(currSampleIndex));
        assertEquals(EXTRACT_BASENAME, line.get(currExtractIndex));
        assertEquals(LABELED_EXTRACT_BASENAME, line.get(currLabeledExtractIndex));
        assertEquals(HYBRIDIZATION_BASENAME, line.get(currHybridizationIndex));
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
        for (String columnName : header) {
            if (SdrfColumnType.SOURCE_NAME.toString().equals(columnName)) {
                currSourceIndex = currColumnNum;
            } else if (SdrfColumnType.SAMPLE_NAME.toString().equals(columnName)) {
                currSampleIndex = currColumnNum;
            } else if (SdrfColumnType.EXTRACT_NAME.toString().equals(columnName)) {
                currExtractIndex = currColumnNum;
            } else if (SdrfColumnType.LABELED_EXTRACT_NAME.toString().equals(columnName)) {
                currLabeledExtractIndex = currColumnNum;
            } else if (SdrfColumnType.HYBRIDIZATION_NAME.toString().equals(columnName)) {
                currHybridizationIndex = currColumnNum;
            } else if (SdrfColumnType.PROVIDER.toString().equals(columnName)) {
                currProviderIndex = currColumnNum;
            } else if (SdrfColumnType.MATERIAL_TYPE.toString().equals(columnName)) {
                // Only Source's material type expected.
                currSourceMaterialTypeIndex = currColumnNum;
            } else if (columnName.startsWith(SdrfColumnType.CHARACTERISTICS.toString())) {
                String categoryName = columnName.substring(columnName.indexOf('[')+1, columnName.length()-1);
                if (SPECIAL_CHARACTERISTIC_CATEGORY.equals(categoryName)) {
                    currSampleSpecialCharacteristicIndex = currColumnNum;
                } else if (MEASUREMENT_CHARACTERISTIC_CATEGORY.equals(categoryName)) {
                    currMeasurementCharacteristicIndex = currColumnNum;
                }
            } else if (SdrfColumnType.UNIT.toString().equals(columnName)) {
                currCharacteristicUnitIndex = currColumnNum;
            } else if (SdrfColumnType.LABEL.toString().equals(columnName)) {
                currLabelIndex = currColumnNum;
            }
            currColumnNum ++;
        }
    }

    private void verifyBiomaterialCharacteristics(DelimitedFileReader reader) throws IOException {
        // Parse header.
        verifyHeader(reader);

        // Verify the correctness of the biomaterial-data chain in the first row.
        assertTrue("No value rows in SDRF.", reader.hasNextLine());
        List<String> line = reader.nextLine();

        // Source should have provider and material type.
        assertEquals(PROVIDER_ORGANIZATION, line.get(currProviderIndex));
        assertEquals(MATERIAL_TYPE_VALUE, line.get(currSourceMaterialTypeIndex));

        // Sample should have a special term-based characteristic and a measurement characteristic.
        assertEquals(SPECIAL_CHARACTERISTIC_VALUE, line.get(currSampleSpecialCharacteristicIndex));
        assertEquals(MEASUREMENT_CHARACTERISTIC_VALUE, line.get(currMeasurementCharacteristicIndex));
        assertEquals(UNIT_VALUE, line.get(currCharacteristicUnitIndex));

        // Extract should have no characteristics.
        // LabeledExtract should have label.
        assertEquals(LABEL_VALUE, line.get(currLabelIndex));
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
