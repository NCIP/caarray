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
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabExportException;
import gov.nih.nci.caarray.magetab.MageTabOntologyCategory;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.MageTabParsingRuntimeException;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.Parameter;
import gov.nih.nci.caarray.magetab.ParameterValue;
import gov.nih.nci.caarray.magetab.Protocol;
import gov.nih.nci.caarray.magetab.ProtocolApplication;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.TermSourceable;
import gov.nih.nci.caarray.magetab.Unitable;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedWriter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.NCICommonsUtils;

/**
 * Represents a Sample and Data Relationship Format (SDRF) file - a tab-delimited file describing the relationships
 * between samples, arrays, data, and other objects used or produced in the investigation, and providing all MIAME
 * information that is not provided elsewhere. This is often the least trivial part of the experiment description due to
 * the complex relationships which are possible between samples and their respective hybridizations; however, for simple
 * experimental designs, constructing the SDRF file is straightforward, and even complex loop designs can be expressed
 * in this format.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyFields", "PMD.TooManyMethods", "PMD.ExcessiveClassLength",
        "unchecked", "PMD.UnusedFormalParameter" })
public final class SdrfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 1116542609494378874L;
    private static final String EMPTY_SYMBOL = "-&gt;";
    private static final String EXPORT_EMPTY_SYMBOL = "->";

    private IdfDocument idfDocument;
    private final List<SdrfColumn> columns = new ArrayList<SdrfColumn>();
    private final Map<NodeKey, AbstractSampleDataRelationshipNode> nodeCache =
        new HashMap<NodeKey, AbstractSampleDataRelationshipNode>();
    private final Map<NodeKey, AbstractSampleDataRelationshipNode> lineNodeCache =
        new HashMap<NodeKey, AbstractSampleDataRelationshipNode>();
    private AbstractSampleDataRelationshipNode currentNode;
    private Unitable currentUnitable;
    private TermSourceable currentTermSourceable;
    private AbstractBioMaterial currentBioMaterial;
    private final List<AbstractSampleDataRelationshipNode> leftmostNodes =
        new ArrayList<AbstractSampleDataRelationshipNode>();
    private ProtocolApplication currentProtocolApp;
    private Hybridization currentHybridization;
    private Scan currentScan;
    private Normalization currentNormalization;
    private ArrayDesign currentArrayDesign;
    private AbstractSampleDataRelationshipNode currentFile;
    private int currentLineNumber;
    private int currentColumnNumber;

    private final List<ArrayDesign> allArrayDesigns = new ArrayList<ArrayDesign>();
    private final List<Comment> allComments = new ArrayList<Comment>();
    private final List<Source> allSources = new ArrayList<Source>();
    private final List<FactorValue> allFactorValues = new ArrayList<FactorValue>();
    private final List<Sample> allSamples = new ArrayList<Sample>();
    private final List<Extract> allExtracts = new ArrayList<Extract>();
    private final List<LabeledExtract> allLabeledExtracts = new ArrayList<LabeledExtract>();
    private final List<Hybridization> allHybridizations = new ArrayList<Hybridization>();
    private final List<Scan> allScans = new ArrayList<Scan>();
    private final List<Normalization> allNormalizations = new ArrayList<Normalization>();
    private final List<ArrayDataFile> allArrayDataFiles = new ArrayList<ArrayDataFile>();
    private final List<DerivedArrayDataFile> allDerivedArrayDataFiles = new ArrayList<DerivedArrayDataFile>();
    private final List<ArrayDataMatrixFile> allArrayDataMatrixFiles = new ArrayList<ArrayDataMatrixFile>();
    private final List<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles =
        new ArrayList<DerivedArrayDataMatrixFile>();
    private final List<Image> allImages = new ArrayList<Image>();

    // Inclusion of columns in the SDRF
    private final Set<SdrfColumnType> allSourceColumns = new HashSet<SdrfColumnType>();
    private final Set<SdrfColumnType> allSampleColumns = new HashSet<SdrfColumnType>();
    private final Set<SdrfColumnType> allExtractColumns = new HashSet<SdrfColumnType>();
    private final Set<SdrfColumnType> allLabeledExtractColumns = new HashSet<SdrfColumnType>();
    // The ordering of the characteristics in the SDRF.
    private final Set<SdrfCharacteristic> allSourceCharacteristics = new LinkedHashSet<SdrfCharacteristic>();
    private final Set<SdrfCharacteristic> allSampleCharacteristics = new LinkedHashSet();
    private final Set<SdrfCharacteristic> allExtractCharacteristics = new LinkedHashSet();
    private final Set<SdrfCharacteristic> allLabeledExtractCharacteristics = new LinkedHashSet();

    private boolean reimportingMagetab = false;

    /**
     * Creates a new SDRF from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the SDRF belongs to.
     * @param file the file containing the SDRF content.
     */
    public SdrfDocument(MageTabDocumentSet documentSet, File file) {
        super(documentSet, file);
    }

    /**
     * Initializes the SDRF document with a MAGE-TAB object graph. It is assumed that this is a full graph, meaning that
     * the leftmost nodes are all Sources.
     *
     * @param nodes all the sources, samples, extracts, labeled extracts, hybridizations, and data file nodes.
     */
    public void initializeNodes(SdrfDocumentNodes nodes) {
        if (!(nodes.isInitialized())) {
            throw new MageTabExportException("All nodes in the SDRF document have not been initialized.");
        }
        leftmostNodes.addAll(nodes.getAllSources());
        allSources.addAll(nodes.getAllSources());
        allSamples.addAll(nodes.getAllSamples());
        allExtracts.addAll(nodes.getAllExtracts());
        allLabeledExtracts.addAll(nodes.getAllLabeledExtracts());
        allHybridizations.addAll(nodes.getAllHybridizations());
        allArrayDataFiles.addAll(nodes.getAllArrayDataFiles());
        allArrayDataMatrixFiles.addAll(nodes.getAllArrayDataMatrixFiles());
        allDerivedArrayDataFiles.addAll(nodes.getAllDerivedArrayDataFiles());
        allDerivedArrayDataMatrixFiles.addAll(nodes.getAllDerivedArrayDataMatrixFiles());
        // TODO Populate other members, e.g., images, scans, normalizations.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(boolean isReimportingMagetab) throws MageTabParsingException {
        if (checkHasIdf()) {
            this.reimportingMagetab = isReimportingMagetab;
            parseSdrf();
        } else {
            addErrorMessage("This SDRF file is not referenced by an IDF file.");
        }
    }

    private boolean checkHasIdf() {
        return getIdfDocument() != null;
    }

    private void parseSdrf() throws MageTabParsingException {
        DelimitedFileReader tabDelimitedReader = createTabDelimitedReader();
        try {
            handleHeaderLine(getHeaderLine(tabDelimitedReader));
            if (minimumColumnCheck()) {
                if (!validColumnOrderCheck()) {
                    addErrorMessage("Term Source Ref is not preceded by valid data type");
                }
                while (tabDelimitedReader.hasNextLine()) {
                    List<String> values = tabDelimitedReader.nextLine();
                    currentLineNumber = tabDelimitedReader.getCurrentLineNumber();
                    handleLine(values);
                }
            } else {
                addErrorMessage("SDRF file does not have the "
                        + "minimum number of columns (Source, Hybridization, and a data file)");
            }
        } catch (IllegalArgumentException e) {
            addErrorMessage("SDRF type not found: " + e.getMessage());
        } catch (IOException e) {
            addErrorMessage("Error while reading next line: " + e.getMessage());
        } finally {
            tabDelimitedReader.close();
        }
    }

    /**
     * Exports the SDRF elements into the file corresponding to this document.
     */
    @Override
    protected void export() {
        DelimitedWriter writer = createTabDelimitedWriter();

        // Write header row.
        List<String> headerRow = compileHeaders();
        writeRow(headerRow, writer);

        // Generate and write all non-header rows.
        List<AbstractSampleDataRelationshipNode> currentRowNodes = new ArrayList<AbstractSampleDataRelationshipNode>();
        for (AbstractSampleDataRelationshipNode source : leftmostNodes) {
            generateEntry(source, currentRowNodes, writer);
        }

        writer.close();
    }

    /**
     * Recursively generates rows in the table to represent node relationships.
     *
     * @param leftNode the left node in the pair of nodes whose relationship is being translated.
     * @param currentRow the current row as translated so far.
     * @param writer the delimited writer to write the row to.
     */
    private void generateEntry(AbstractSampleDataRelationshipNode leftNode,
            List<AbstractSampleDataRelationshipNode> currentRowNodes, DelimitedWriter writer) {
        currentRowNodes.add(leftNode);
        if (leftNode.getSuccessors().isEmpty()) {
            // Reached end of row. Add row to table.
            addRowToTable(currentRowNodes, writer);
        } else {
            // Recursively generate entries for all successors of this node.
            for (AbstractSampleDataRelationshipNode rightNode : leftNode.getSuccessors()) {
                generateEntry(rightNode, currentRowNodes, writer);
            }
        }

        removeLastNode(currentRowNodes);
    }

    /**
     * Remove the last node in the row.
     */
    private void removeLastNode(List<AbstractSampleDataRelationshipNode> nodeList) {
        final int listSize = nodeList.size();
        if (listSize >= 1) {
            nodeList.remove(listSize - 1);
        }
    }

    private void addRowToTable(List<AbstractSampleDataRelationshipNode> nodes, DelimitedWriter writer) {
        List<String> rowValues = new ArrayList<String>();
        for (AbstractSampleDataRelationshipNode node : nodes) {
            rowValues.add(node.getName());
            switch (node.getNodeType()) {
            case SOURCE:
                addSourceValues(rowValues, (Source) node);
                break;
            case SAMPLE:
                addBiomaterialValues(rowValues, (AbstractBioMaterial) node, SdrfNodeType.SAMPLE);
                break;
            case EXTRACT:
                addBiomaterialValues(rowValues, (AbstractBioMaterial) node, SdrfNodeType.EXTRACT);
                break;
            case LABELED_EXTRACT:
                addLabeledExtractValues(rowValues, (LabeledExtract) node);
                break;
            default:
                break;
            }
        }
        Collections.replaceAll(rowValues, EMPTY_SYMBOL, EXPORT_EMPTY_SYMBOL);
        writeRow(rowValues, writer);
    }

    private List<String> compileHeaders() {
        List<String> headerRow = new ArrayList<String>();
        addSourceHeaders(headerRow);
        addSampleHeaders(headerRow);
        addExtractHeaders(headerRow);
        addLabeledExtractHeaders(headerRow);
        addHybridizationHeaders(headerRow);
        addRawDataHeaders(headerRow);
        addDerivedDataHeaders(headerRow);
        return headerRow;
    }

    private void addSourceHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.SOURCE_NAME.toString());
        boolean addProviderColumn = false;
        for (Source source : allSources) {
            if (!(source.getProviders().isEmpty())) {
                addProviderColumn = true;
            }
        }
        if (addProviderColumn) {
            allSourceColumns.add(SdrfColumnType.PROVIDER);
            headerRow.add(SdrfColumnType.PROVIDER.toString());
        }
        boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, allSources, SdrfNodeType.SOURCE);
        if (addMaterialTypeColumn) {
            allSourceColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addSampleHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.SAMPLE_NAME.toString());
        boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, allSamples, SdrfNodeType.SAMPLE);
        if (addMaterialTypeColumn) {
            allSampleColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addExtractHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.EXTRACT_NAME.toString());
        boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, allExtracts, SdrfNodeType.EXTRACT);
        if (addMaterialTypeColumn) {
            allExtractColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addLabeledExtractHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.LABELED_EXTRACT_NAME.toString());
        boolean addLabelColumn = false;
        for (LabeledExtract labeledExtract : allLabeledExtracts) {
            if (labeledExtract.getLabel() != null) {
                addLabelColumn = true;
            }
        }
        if (addLabelColumn) {
            allLabeledExtractColumns.add(SdrfColumnType.LABEL);
            headerRow.add(SdrfColumnType.LABEL.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
        boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, allLabeledExtracts,
                SdrfNodeType.LABELED_EXTRACT);
        if (addMaterialTypeColumn) {
            allLabeledExtractColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private boolean addBiomaterialHeaders(List<String> headerRow, List<? extends AbstractBioMaterial> biomaterials,
            SdrfNodeType nodeType) {
        boolean addMaterialTypeColumn = false;
        for (AbstractBioMaterial biomaterial : biomaterials) {
            if (biomaterial.getMaterialType() != null) {
                addMaterialTypeColumn = true;
            }
            for (Characteristic characteristic : biomaterial.getCharacteristics()) {
                handleNewCharacteristic(characteristic, headerRow, nodeType);
            }
        }
        return addMaterialTypeColumn;
    }

    private void handleNewCharacteristic(Characteristic characteristic, List<String> headerRow, SdrfNodeType nodeType) {
        String category = characteristic.getCategory();
        SdrfCharacteristic sdrfCharacteristic = new SdrfCharacteristic(category);
        boolean isNewCharacteristic = false;
        switch (nodeType) {
        case SOURCE:
            isNewCharacteristic = allSourceCharacteristics.add(sdrfCharacteristic);
            break;
        case SAMPLE:
            isNewCharacteristic = allSampleCharacteristics.add(sdrfCharacteristic);
            break;
        case EXTRACT:
            isNewCharacteristic = allExtractCharacteristics.add(sdrfCharacteristic);
            break;
        case LABELED_EXTRACT:
            isNewCharacteristic = allLabeledExtractCharacteristics.add(sdrfCharacteristic);
            break;
        default:
            // Should never get here.
            break;
        }
        // If it's a new category, add it to the headers.
        if (isNewCharacteristic) {
            addNewCharacteristic(characteristic, headerRow, category, sdrfCharacteristic);
        }
    }

    private void addNewCharacteristic(Characteristic characteristic, List<String> headerRow, String category,
            SdrfCharacteristic sdrfCharacteristic) {
        String columnHeader = SdrfColumnType.CHARACTERISTICS.toString() + "[" + category + "]";
        headerRow.add(columnHeader);
        if (characteristic.getTerm() != null) {
            // Term-based characteristic
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
            sdrfCharacteristic.setType(false);
        } else {
            // Measurement characteristic
            headerRow.add(SdrfColumnType.UNIT.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
            sdrfCharacteristic.setType(true);
        }
    }

    private void addHybridizationHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.HYBRIDIZATION_NAME.toString());
    }

    private void addRawDataHeaders(List<String> headerRow) {
        //TODO Handle case where raw data file and raw data matrix file are both present in a row.
        if (!(allArrayDataFiles.isEmpty())) {
            headerRow.add(SdrfColumnType.ARRAY_DATA_FILE.toString());
        } else {
            headerRow.add(SdrfColumnType.ARRAY_DATA_MATRIX_FILE.toString());
        }
    }

    private void addDerivedDataHeaders(List<String> headerRow) {
        //TODO Handle case where derived data file and derived data matrix file are both present in a row.
        if (!(allDerivedArrayDataFiles.isEmpty())) {
            headerRow.add(SdrfColumnType.DERIVED_ARRAY_DATA_FILE.toString());
        } else {
            headerRow.add(SdrfColumnType.DERIVED_ARRAY_DATA_MATRIX_FILE.toString());
        }
    }

    private void addSourceValues(List<String> row, Source source) {
        if (allSourceColumns.contains(SdrfColumnType.PROVIDER)) {
            List<Provider> providers = source.getProviders();
            if (!(providers.isEmpty())) {
                row.add(providers.get(0).getName());
            } else {
                row.add("");
            }
        }
        addBiomaterialValues(row, source, SdrfNodeType.SOURCE);
    }

    private void addLabeledExtractValues(List<String> row, LabeledExtract labeledExtract) {
        if (allLabeledExtractColumns.contains(SdrfColumnType.LABEL)) {
            OntologyTerm label = labeledExtract.getLabel();
            if (label != null) {
                row.add(label.getValue());
                TermSource termSource = label.getTermSource();
                if ((termSource == null) || StringUtils.isBlank(termSource.getName())) {
                    row.add("");
                } else {
                    row.add(termSource.getName());
                }
            } else {
                // Add empty label and Term Source REF.
                row.add("");
                row.add("");
            }
        }
        addBiomaterialValues(row, labeledExtract, SdrfNodeType.LABELED_EXTRACT);
    }

    private void addBiomaterialValues(List<String> row, AbstractBioMaterial biomaterial, SdrfNodeType nodeType) {
        Set<SdrfCharacteristic> allCharacteristics = null;
        Set<SdrfColumnType> allColumns = null;
        switch (nodeType) {
        case SOURCE:
            allCharacteristics = allSourceCharacteristics;
            allColumns = allSourceColumns;
            break;
        case SAMPLE:
            allCharacteristics = allSampleCharacteristics;
            allColumns = allSampleColumns;
            break;
        case EXTRACT:
            allCharacteristics = allExtractCharacteristics;
            allColumns = allExtractColumns;
            break;
        case LABELED_EXTRACT:
            allCharacteristics = allLabeledExtractCharacteristics;
            allColumns = allLabeledExtractColumns;
            break;
        default: // Should never get here.
            return;
        }
        addCharacteristics(row, biomaterial, allCharacteristics);
        addMaterialType(row, biomaterial, allColumns);
    }

    private void addMaterialType(List<String> row, AbstractBioMaterial biomaterial, Set<SdrfColumnType> allColumns) {
        if (allColumns.contains(SdrfColumnType.MATERIAL_TYPE)) {
            OntologyTerm materialType = biomaterial.getMaterialType();
            if (materialType != null) {
                row.add(materialType.getValue());
                TermSource termSource = materialType.getTermSource();
                if ((termSource == null) || StringUtils.isBlank(termSource.getName())) {
                    row.add(termSource.getName());
                } else {
                    row.add("");
                }
            } else {
                // Add empty material type and Term Source REF.
                row.add("");
                row.add("");
            }
        }
    }

    private void addCharacteristics(List<String> row, AbstractBioMaterial biomaterial,
            Set<SdrfCharacteristic> allCharacteristics) {
        for (SdrfCharacteristic sdrfCharacteristic : allCharacteristics) {
            String category = sdrfCharacteristic.getCategory();
            Characteristic characteristic = biomaterial.getCharacteristic(category);
            if (sdrfCharacteristic.isMeasurementCharacteristic()) {
                addMeasurementCharacteristic(row, characteristic);
            } else {
                addTermCharacteristic(row, characteristic);
            }
        }
    }

    private void addMeasurementCharacteristic(List<String> row, Characteristic characteristic) {
        // Measurement characteristic: Add value, unit and the unit's term source.
        if (characteristic == null || characteristic.getValue() == null) {
            row.add("");
            row.add("");
            row.add("");
        } else {
            row.add(characteristic.getValue());
            OntologyTerm unit = characteristic.getUnit();
            if (unit == null) {
                row.add("");
                row.add("");
            } else {
                row.add(unit.getValue());
                TermSource termSource = unit.getTermSource();
                if (termSource == null || StringUtils.isBlank(termSource.getName())) {
                    row.add("");
                } else {
                    row.add(termSource.getName());
                }
            }
        }
    }

    private void addTermCharacteristic(List<String> row, Characteristic characteristic) {
        // Term-based characteristic: Add value and its term source.
        if (characteristic == null || characteristic.getTerm().getValue() == null) {
            row.add("");
            row.add("");
        } else {
            OntologyTerm term = characteristic.getTerm();
            row.add(term.getValue());
            TermSource termSource = term.getTermSource();
            if (termSource == null || StringUtils.isBlank(termSource.getName())) {
                row.add("");
            } else {
                row.add(termSource.getName());
            }
        }
    }

    private List<String> getHeaderLine(DelimitedFileReader tabDelimitedReader) throws IOException {
        List<String> nextLine = tabDelimitedReader.nextLine();
        while (isComment(nextLine)) {
            nextLine = tabDelimitedReader.nextLine();
        }
        return nextLine;
    }

    private boolean validColumnOrderCheck() {
        boolean valid = true;
        for (SdrfColumn aColumn : columns) {
            if (aColumn.getType().equals(SdrfColumnType.TERM_SOURCE_REF)) {
                valid = valid && termSrcRefPreceedingColValid(aColumn);
            }
        }
        return valid;
    }

    private boolean isComment(List<String> values) {
        return !values.isEmpty() && values.get(0).startsWith(COMMENT_CHARACTER);
    }

    private boolean minimumColumnCheck() {
        boolean bioMaterial = false;
        boolean hybridization = false;
        boolean dataFile = false;
        // file should have
        // 1. BioMaterial (Source, Sample, Extract, Labeled Extract), 2. Hybridization, 3. data file
        for (SdrfColumn aColumn : columns) {
            switch (aColumn.getType()) {
            case SOURCE_NAME:
            case SAMPLE_NAME:
            case EXTRACT_NAME:
            case LABELED_EXTRACT_NAME:
                bioMaterial = true;
                break;
            case HYBRIDIZATION_NAME:
                hybridization = true;
                break;
            case ARRAY_DESIGN_FILE:
            case DERIVED_ARRAY_DATA_FILE:
            case ARRAY_DATA_FILE:
            case ARRAY_DATA_MATRIX_FILE:
            case DERIVED_ARRAY_DATA_MATRIX_FILE:
                dataFile = true;
                break;
            default:
                break;
            }
        }
        return bioMaterial && hybridization && dataFile;
    }

    private void handleHeaderLine(List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            columns.add(new SdrfColumn(createHeading(values.get(i))));
        }
    }

    private void handleLine(List<String> values) {
        if (!isComment(values)) {
            for (int i = 0; i < values.size(); i++) {
                currentColumnNumber = i + 1;
                try {
                    String value = NCICommonsUtils.performXSSFilter(StringUtils.trim(values.get(i)));
                    handleValue(columns.get(i), value);
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    addError(e.toString() + ": " + sw.toString());
                }
            }
            currentNode = null;
            currentArrayDesign = null;
            currentHybridization = null;
            currentScan = null;
            currentNormalization = null;
            currentFile = null;
            lineNodeCache.clear();
        }
    }

    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private void handleValue(SdrfColumn column, String value) {
        if (isBlank(value)) {
            switch (column.getType()) {
            case SOURCE_NAME:
            case SAMPLE_NAME:
            case EXTRACT_NAME:
            case LABELED_EXTRACT_NAME:
            case HYBRIDIZATION_NAME:
                addError(column.getHeading() + " cannot be blank");
                break;
            case CHARACTERISTICS:
            case PROTOCOL_REF:
            case MATERIAL_TYPE:
            case UNIT:
            case LABEL:
            case ARRAY_DESIGN_REF:
            case PARAMETER_VALUE:
                currentTermSourceable = null;
                break;
            default:
                return;
            }
        }
        switch (column.getType()) {
        case SOURCE_NAME:
        case SAMPLE_NAME:
        case EXTRACT_NAME:
        case LABELED_EXTRACT_NAME:
            handleBioMaterial(column, value);
            break;
        case SCAN_NAME:
            handleScan(column, value);
            break;
        case NORMALIZATION_NAME:
            handleNormalization(column, value);
            break;
        case HYBRIDIZATION_NAME:
            handleHybridization(column, value);
            break;
        case PROVIDER:
            handleProviders(value);
            break;
        case PROTOCOL_REF:
            handleProtocolRef(value, getNextColumn(column));
            break;
        case CHARACTERISTICS:
            handleCharacteristic(value, column, getNextColumn(column));
            break;
        case MATERIAL_TYPE:
            handleMaterialType(column, value);
            break;
        case PARAMETER_VALUE:
            handleParameterValue(column, value);
            break;
        case TERM_SOURCE_REF:
            handleTermSourceRef(value);
            break;
        case UNIT:
            handleUnit(column, value, getNextColumn(column));
            break;
        case LABEL:
            handleLabel(column, value);
            break;
        case ARRAY_DESIGN_FILE:
            addError(column.getHeading() + " not allowed via experiment data import.");
            break;
        case ARRAY_DESIGN_REF:
            handleArrayDesignRef(column, value);
            break;
        case DERIVED_ARRAY_DATA_FILE:
            handleArrayDataFile(column, value, true);
            break;
        case ARRAY_DATA_FILE:
            handleArrayDataFile(column, value, false);
            break;
        case ARRAY_DATA_MATRIX_FILE:
            handleArrayDataMatrixFile(column, value, false);
            break;
        case DERIVED_ARRAY_DATA_MATRIX_FILE:
            handleArrayDataMatrixFile(column, value, true);
            break;
        case IMAGE_FILE:
            handleImageFile(column, value);
            break;
        case FACTOR_VALUE:
            handleFactorValue(column, value);
            break;
        case PERFORMER:
            handlePerformer(value);
            break;
        case DATE:
            handleProtocolDate(value);
            break;
        case DESCRIPTION:
            handleDescription(value);
            break;
        case COMMENT:
            // DEVELOPER NOTE: we ignore comments
            break;
        default:
            break;
        }
    }

    private void handleDescription(String value) {
        if (currentBioMaterial == null) {
            addError("Description must be preceded by a Source, Sample, Extract, or LabeledExtract");
        } else {
            currentBioMaterial.setDescription(value);
        }
    }

    private void handleBioMaterial(SdrfColumn column, String value) {
        handleNode(column, value);
        currentBioMaterial = (AbstractBioMaterial) currentNode;
    }

    private boolean isBlank(String value) {
        return StringUtils.isBlank(value) || EMPTY_SYMBOL.equals(value);
    }

    private void handleHybridization(SdrfColumn column, String value) {
        handleNode(column, value);
        currentHybridization = (Hybridization) currentNode;
        if (currentArrayDesign != null) {
            currentHybridization.setArrayDesign(currentArrayDesign);
        }
    }

    private void handleImageFile(SdrfColumn column, String value) {
        Image image = new Image();
        image.setName(value);
        image.link(currentHybridization);
        currentNode = image;
    }

    private void handleScan(SdrfColumn column, String value) {
        handleNode(column, value, currentHybridization);
        currentScan = (Scan) currentNode;
    }

    private void handleNormalization(SdrfColumn column, String value) {
        handleNode(column, value, currentHybridization);
        currentNormalization = (Normalization) currentNode;
    }

    private void handleArrayDesignRef(SdrfColumn column, String value) {
        ArrayDesign arrayDesign = arrayDesignHelper(column, value);
        currentTermSourceable = arrayDesign;
    }

    private ArrayDesign arrayDesignHelper(SdrfColumn column, String value) {
        ArrayDesign arrayDesign = getArrayDesign(value);
        arrayDesign.setName(value);
        arrayDesign.addToSdrfList(this);
        if (currentHybridization != null) {
            currentHybridization.setArrayDesign(arrayDesign);
        }
        currentArrayDesign = arrayDesign;
        return arrayDesign;
    }

    private void handleFactorValue(SdrfColumn column, String value) {
        FactorValue factorValue = new FactorValue();
        factorValue.setFactor(idfDocument.getFactor(column.getHeading().getQualifier()));
        if (factorValue.getFactor() != null) {
            factorValue.addToSdrfList(this);
            factorValue.setValue(value);
            currentUnitable = factorValue;
            if (currentHybridization != null) {
                currentHybridization.getFactorValues().add(factorValue);
            } else {
                addError("Factor Value columns must come after (to the right of) a Hybridization column");
            }
        } else {
            addError("Referenced Factor Name " + column.getHeading().getQualifier() + " was not found in the IDF");
        }
    }

    private AbstractSampleDataRelationshipNode getNodeToLinkToForArrayData(boolean derived) {
        AbstractSampleDataRelationshipNode explicitNode = derived ? currentNormalization : currentScan;
        return explicitNode != null ? explicitNode : currentHybridization;
    }

    private void handleArrayDataFile(SdrfColumn column, String value, boolean derived) {
        handleNode(column, value, getNodeToLinkToForArrayData(derived));
        AbstractNativeFileReference adf = (AbstractNativeFileReference) currentNode;
        adf.setNativeDataFile(getDocumentSet().getNativeDataFile(value));
        if (adf.getNativeDataFile() == null && !reimportingMagetab) {
            addErrorMessage("Referenced " + (derived ? "Derived " : "") + " Array Data File " + value
                    + " was not found in the document set");
        }
        if (derived && currentFile != null) {
            adf.link(currentFile);
        }
        currentFile = adf;
    }

    private void handleArrayDataMatrixFile(SdrfColumn column, String value, boolean derived) {
        handleNode(column, value, getNodeToLinkToForArrayData(derived));
        AbstractDataMatrixReference admf = (AbstractDataMatrixReference) currentNode;
        admf.setDataMatrix(getDocumentSet().getArrayDataMatrix(value));
        if (admf.getDataMatrix() == null && !reimportingMagetab) {
            addErrorMessage("Referenced " + (derived ? "Derived " : "") + "Array Data Matrix File " + value
                    + " was not found in the document set");
        }
        if (derived && currentFile != null) {
            admf.link(currentFile);
        }
        currentFile = admf;
    }

    private void handleLabel(SdrfColumn column, String value) {
        LabeledExtract labeledExtract = (LabeledExtract) currentBioMaterial;
        labeledExtract.setLabel(addOntologyTerm(MageTabOntologyCategory.LABEL_COMPOUND, value));
        currentTermSourceable = labeledExtract.getLabel();
    }

    private SdrfColumn getNextColumn(SdrfColumn column) {
        int nextColumnIndex = columns.indexOf(column) + 1;
        if (nextColumnIndex < columns.size()) {
            return columns.get(nextColumnIndex);
        } else {
            return null;
        }
    }

    private SdrfColumn getPreviousColumn(SdrfColumn column) {
        int previousColumnIndex = columns.indexOf(column) - 1;
        if (previousColumnIndex > -1) {
            return columns.get(previousColumnIndex);
        } else {
            return null;
        }
    }

    private boolean termSrcRefPreceedingColValid(SdrfColumn column) {
        SdrfColumn prevColumn = getPreviousColumn(column);
        return prevColumn != null && prevColumn.getType().isTermSourceable();
    }

    private void handleUnit(SdrfColumn column, String value, SdrfColumn nextColumn) {
        OntologyTerm unit = addOntologyTerm(column.getHeading().getQualifier(), value);
        unit.setValue(value);
        if (currentUnitable != null) {
            currentUnitable.setUnit(unit);
        } else {
            addError("Illegal Unit column: Unit must follow a Characteristic, ParameterValue or FactorValue");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            currentTermSourceable = unit;
        }
    }

    private void handleMaterialType(SdrfColumn column, String value) {
        OntologyTerm materialType = addOntologyTerm(MageTabOntologyCategory.MATERIAL_TYPE, value);
        currentBioMaterial.setMaterialType(materialType);
        currentTermSourceable = materialType;
    }

    private void handleTermSourceRef(String value) {
        TermSource termSource = getTermSource(value);
        if (termSource == null) {
            addError("Term Source " + value + " is not defined in the IDF document");
        }
        if (currentTermSourceable != null) {
            currentTermSourceable.setTermSource(termSource);
        }
    }

    private void handleCharacteristic(String value, SdrfColumn currentColumn, SdrfColumn nextColumn) {
        Characteristic characteristic = new Characteristic();
        if (!currentBioMaterial.isRepeated()) {
            currentBioMaterial.getCharacteristics().add(characteristic);
        }
        characteristic.setCategory(currentColumn.getHeading().getQualifier());
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.UNIT) {
            characteristic.setValue(value);
            currentUnitable = characteristic;
        } else {
            OntologyTerm term = addOntologyTerm(characteristic.getCategory(), value);
            characteristic.setTerm(term);
            if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
                currentTermSourceable = term;
            }
        }
    }

    private void handleProtocolRef(String value, SdrfColumn nextColumn) {
        ProtocolApplication protocolApp = new ProtocolApplication();
        protocolApp.setProtocol(getProtocol(value));
        if (!currentNode.isRepeated()) {
            currentNode.getProtocolApplications().add(protocolApp);
        }
        currentProtocolApp = protocolApp;
        if (protocolApp.getProtocol() == null) {
            protocolApp.setProtocol(new Protocol());
            protocolApp.getProtocol().setName(value);
            addWarning("Protocol " + value + " is not defined in the IDF document");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            currentTermSourceable = protocolApp.getProtocol();
        }
    }

    private void handlePerformer(String value) {
        if (currentProtocolApp != null) {
            currentProtocolApp.setPerformer(value);
        } else {
            addWarning("Performer column with value " + value + " does not follow a Protocol REF column");
        }
    }

    private void handleProtocolDate(String value) {
        if (currentProtocolApp != null) {
            currentProtocolApp.setDate(parseDateValue(value, "Protocol Date"));
        } else {
            addWarning("Date column with value " + value + " does not follow a Protocol REF column");
        }
    }

    private void handleParameterValue(SdrfColumn column, String value) {
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setValue(value);
        Parameter param = new Parameter();
        param.setName(column.getHeading().getQualifier());
        parameterValue.setParameter(param);
        currentUnitable = parameterValue;
        if (!currentNode.isRepeated()) {
            currentProtocolApp.getParameterValues().add(parameterValue);
        }
    }

    private void handleNode(SdrfColumn column, String value, AbstractSampleDataRelationshipNode nodeToLinkTo) {
        AbstractSampleDataRelationshipNode node = getOrCreateNode(column, value);
        if (nodeToLinkTo == null) {
            if (!leftmostNodes.contains(node)) {
                leftmostNodes.add(node);
            }
        } else {
            node.link(nodeToLinkTo);
        }
        currentNode = node;
    }

    private void handleNode(SdrfColumn column, String value) {
        handleNode(column, value, currentNode);
    }

    private AbstractSampleDataRelationshipNode getOrCreateNode(SdrfColumn column, String value) {
        NodeKey nodeKey = getNodeKey(column, value);
        AbstractSampleDataRelationshipNode node = nodeCache.get(nodeKey);
        if (node == null) {
            node = createNode(column, value);
            nodeCache.put(nodeKey, node);
            lineNodeCache.put(nodeKey, node);
        } else {
            if (!lineNodeCache.containsKey(nodeKey)) {
                lineNodeCache.put(nodeKey, node);
            } else if (node.getNodeType().isName()) {
                addErrorMessage(currentLineNumber, currentColumnNumber, "Duplicate "
                        + column.getHeading().getTypeName() + " " + value);
            }
            node.setRepeated(true);
        }
        return node;
    }

    private AbstractSampleDataRelationshipNode createNode(SdrfColumn column, String value) {
        try {
            AbstractSampleDataRelationshipNode node = (AbstractSampleDataRelationshipNode) column.getType()
                    .getNodeClass().newInstance();
            node.setName(value);
            node.addToSdrfList(this);
            return node;
        } catch (InstantiationException e) {
            throw new MageTabParsingRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new MageTabParsingRuntimeException(e);
        }
    }

    private NodeKey getNodeKey(SdrfColumn column, String value) {
        return getNodeKey(column.getType(), value);
    }

    private NodeKey getNodeKey(SdrfColumnType heading, String value) {
        return new NodeKey(heading.getNodeClass(), value);
    }

    private void handleProviders(String value) {
        if (currentBioMaterial.isRepeated()) {
            return;
        }
        String[] providerNames = value.split(";");
        int numProviders = providerNames.length;
        for (int i = 0; i < numProviders; i++) {
            Provider provider = new Provider();
            provider.setName(providerNames[i]);
            if (currentBioMaterial instanceof Source) {
                ((Source) currentBioMaterial).getProviders().add(provider);
            } else {
                addError("Provider must be preceded by a Source");
                return;
            }
        }
    }

    /**
     * @return the leftmostNodes
     */
    public List<AbstractSampleDataRelationshipNode> getLeftmostNodes() {
        return leftmostNodes;
    }

    /**
     * Compound key for node lookup and caching.
     */
    private static final class NodeKey {

        private final Class<?> nodeClass;
        private final String nodeName;

        NodeKey(Class<?> nodeClass, String nodeName) {
            this.nodeClass = nodeClass;
            this.nodeName = nodeName;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NodeKey)) {
                return false;
            } else {
                NodeKey nodeKey = (NodeKey) obj;
                return nodeClass.equals(nodeKey.nodeClass) && nodeName.equalsIgnoreCase(nodeKey.nodeName);
            }
        }

        @Override
        public int hashCode() {
            // PMD is erroneously complaining about the call to toLowerCase, even though a Locale is used
            return nodeClass.hashCode() + nodeName.toLowerCase(Locale.getDefault()).hashCode(); // NOPMD
        }

    }

    /**
     * @return the allArrayDesigns
     */
    public List<ArrayDesign> getAllArrayDesigns() {
        return allArrayDesigns;
    }

    /**
     * @return the allDerivedArrayDataFiles
     */
    public List<DerivedArrayDataFile> getAllDerivedArrayDataFiles() {
        return allDerivedArrayDataFiles;
    }

    /**
     * @return the allExtracts
     */
    public List<Extract> getAllExtracts() {
        return allExtracts;
    }

    /**
     * @return the allHybridizations
     */
    public List<Hybridization> getAllHybridizations() {
        return allHybridizations;
    }

    /**
     * @return the allImages
     */
    public List<Image> getAllImages() {
        return allImages;
    }

    /**
     * @return the allLabeledExtracts
     */
    public List<LabeledExtract> getAllLabeledExtracts() {
        return allLabeledExtracts;
    }

    /**
     * @return the allNormalizations
     */
    public List<Normalization> getAllNormalizations() {
        return allNormalizations;
    }

    /**
     * @return the allSamples
     */
    public List<Sample> getAllSamples() {
        return allSamples;
    }

    /**
     * @return the allScans
     */
    public List<Scan> getAllScans() {
        return allScans;
    }

    /**
     * @return the allSources
     */
    public List<Source> getAllSources() {
        return allSources;
    }

    /**
     * @return the allArrayDataFiles
     */
    public List<ArrayDataFile> getAllArrayDataFiles() {
        return allArrayDataFiles;
    }

    /**
     * @return the idfDocument
     */
    public IdfDocument getIdfDocument() {
        return idfDocument;
    }

    /**
     * @param idfDocument the idfDocument to set
     */
    public void setIdfDocument(IdfDocument idfDocument) {
        this.idfDocument = idfDocument;
    }

    /**
     * @return the allArrayDataMatrixFiles
     */
    public List<ArrayDataMatrixFile> getAllArrayDataMatrixFiles() {
        return allArrayDataMatrixFiles;
    }

    /**
     * @return the allDerivedArrayDataMatrixFiles
     */
    public List<DerivedArrayDataMatrixFile> getAllDerivedArrayDataMatrixFiles() {
        return allDerivedArrayDataMatrixFiles;
    }

    /**
     * @return the allFactorValues
     */
    public List<FactorValue> getAllFactorValues() {
        return allFactorValues;
    }

    /**
     * @return the allComments
     */
    public List<Comment> getAllComments() {
        return allComments;
    }

    /**
     * Adds an error message with the current line and column number.
     *
     * @param message error message
     */
    private void addError(String message) {
        addErrorMessage(currentLineNumber, currentColumnNumber, message);
    }

    /**
     * Adds a warning message with the current line and column number.
     *
     * @param message warning message
     */
    private void addWarning(String message) {
        addWarningMessage(currentLineNumber, currentColumnNumber, message);
    }

    /**
     * Get names of raw data files referenced by this sdrf.
     *
     * @return list of file names
     */
    public List<String> getReferencedRawFileNames() {
        return getFileNames(this.getAllArrayDataFiles());
    }

    /**
     * Get names of derived data files referenced by this sdrf.
     *
     * @return list of file names
     */
    public List<String> getReferencedDerivedFileNames() {
        return getFileNames(this.getAllDerivedArrayDataFiles());
    }

    /**
     * Get names of data matrix files referenced by this sdrf.
     *
     * @return list of file names
     */
    public List<String> getReferencedDataMatrixFileNames() {
        List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(getFileNames(this.getAllArrayDataMatrixFiles()));
        fileNames.addAll(getFileNames(this.getAllDerivedArrayDataMatrixFiles()));
        return fileNames;
    }

    /**
     * Encapsulates the defining attributes of a characteristic.
     */
    private class SdrfCharacteristic {
        private final String category;
        private boolean isMeasurement = false;

        SdrfCharacteristic(String categoryName) {
            if (StringUtils.isBlank(categoryName)) {
                throw new IllegalArgumentException("Characteristic should not have an empty category.");
            }
            category = categoryName;
        }

        String getCategory() {
            return category;
        }

        void setType(boolean isMeasurementCharacteristic) {
            this.isMeasurement = isMeasurementCharacteristic;
        }

        boolean isMeasurementCharacteristic() {
            return isMeasurement;
        }

        /**
         * Tests if the given object is equal to this object.
         *
         * @param otherObject the object to compare to this object.
         * @return true if the 2 objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object otherObject) {
            if (!(otherObject instanceof SdrfCharacteristic)) {
                return false;
            }
            SdrfCharacteristic otherCharacteristic = (SdrfCharacteristic) otherObject;
            return (category.equals(otherCharacteristic.category));
        }

        /**
         * Default hashCode works on category.
         *
         * @return the hashCode of this object
         */
        @Override
        public int hashCode() {
            if (StringUtils.isBlank(category)) {
                return System.identityHashCode(this);
            }

            return category.hashCode();
        }
    }
}
