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

import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.Commentable;
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
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fiveamsolutions.nci.commons.util.NCICommonsUtils;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedWriter;

/**
 * Represents a Sample and Data Relationship Format (SDRF) file - a tab-delimited file describing the relationships
 * between samples, arrays, data, and other objects used or produced in the investigation, and providing all MIAME
 * information that is not provided elsewhere. This is often the least trivial part of the experiment description due to
 * the complex relationships which are possible between samples and their respective hybridizations; however, for simple
 * experimental designs, constructing the SDRF file is straightforward, and even complex loop designs can be expressed
 * in this format.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyFields", "PMD.TooManyMethods", "PMD.ExcessiveClassLength",
        "PMD.UnusedFormalParameter" })
public final class SdrfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 1116542609494378874L;
    private static final String EMPTY_SYMBOL = "-&gt;";
    private static final String EXPORT_EMPTY_SYMBOL = "->";

    private IdfDocument idfDocument;
    private final SdrfColumns columns = new SdrfColumns();
    private List<String> currentLine;
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
    private AbstractSampleDataRelationshipNode currentFile;
    private Commentable currentCommentable;
    private int currentLineNumber;
    private int currentColumnNumber;

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
    private final Map<String, SdrfCharacteristic> allSourceCharacteristics =
            new LinkedHashMap<String, SdrfCharacteristic>();
    private final Map<String, SdrfCharacteristic> allSampleCharacteristics =
            new LinkedHashMap<String, SdrfCharacteristic>();
    private final Map<String, SdrfCharacteristic> allExtractCharacteristics =
            new LinkedHashMap<String, SdrfCharacteristic>();
    private final Map<String, SdrfCharacteristic> allLabeledExtractCharacteristics =
            new LinkedHashMap<String, SdrfCharacteristic>();

    /**
     * Creates a new SDRF from an existing file.
     * 
     * @param documentSet the MAGE-TAB document set the SDRF belongs to.
     * @param file the file containing the SDRF content.
     */
    public SdrfDocument(MageTabDocumentSet documentSet, FileRef file) {
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
        this.leftmostNodes.addAll(nodes.getAllSources());
        this.allSources.addAll(nodes.getAllSources());
        this.allSamples.addAll(nodes.getAllSamples());
        this.allExtracts.addAll(nodes.getAllExtracts());
        this.allLabeledExtracts.addAll(nodes.getAllLabeledExtracts());
        this.allHybridizations.addAll(nodes.getAllHybridizations());
        this.allArrayDataFiles.addAll(nodes.getAllArrayDataFiles());
        this.allArrayDataMatrixFiles.addAll(nodes.getAllArrayDataMatrixFiles());
        this.allDerivedArrayDataFiles.addAll(nodes.getAllDerivedArrayDataFiles());
        this.allDerivedArrayDataMatrixFiles.addAll(nodes.getAllDerivedArrayDataMatrixFiles());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse() throws MageTabParsingException {
        if (checkHasIdf()) {
            parseSdrf();
        } else {
            addErrorMessage("This SDRF file is not referenced by an IDF file.");
        }
    }

    /**
     * Parses the sdrf w/out checking for idf being present.
     * 
     * @throws MageTabParsingException when cannot parse.
     */
    public void parseNoIdfCheck() throws MageTabParsingException {
        parseSdrf();
    }

    private boolean checkHasIdf() {
        return getIdfDocument() != null;
    }

    private void parseSdrf() throws MageTabParsingException {
        final DelimitedFileReader tabDelimitedReader = createTabDelimitedReader();
        try {
            handleHeaderLine(getHeaderLine(tabDelimitedReader));
            final boolean columnsOk = checkColumnsAreValid();
            if (columnsOk) {
                while (tabDelimitedReader.hasNextLine()) {
                    final List<String> values = tabDelimitedReader.nextLine();
                    this.currentLineNumber = tabDelimitedReader.getCurrentLineNumber();
                    handleLine(values);
                }
            }
        } catch (final IllegalArgumentException e) {
            addErrorMessage("SDRF type not found: " + e.getMessage());
        } catch (final IOException e) {
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
        final DelimitedWriter writer = createTabDelimitedWriter();

        // Write header row.
        final List<String> headerRow = compileHeaders();
        writeRow(headerRow, writer);

        // Generate and write all non-header rows.
        final List<AbstractSampleDataRelationshipNode> currentRowNodes =
                new ArrayList<AbstractSampleDataRelationshipNode>();
        for (final AbstractSampleDataRelationshipNode source : this.leftmostNodes) {
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
            for (final AbstractSampleDataRelationshipNode rightNode : leftNode.getSuccessors()) {
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
        final List<String> rowValues = new ArrayList<String>();
        for (final AbstractSampleDataRelationshipNode node : nodes) {
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
        final List<String> headerRow = new ArrayList<String>();
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
        for (final Source source : this.allSources) {
            if (!(source.getProviders().isEmpty())) {
                addProviderColumn = true;
            }
        }
        if (addProviderColumn) {
            this.allSourceColumns.add(SdrfColumnType.PROVIDER);
            headerRow.add(SdrfColumnType.PROVIDER.toString());
        }
        final boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, this.allSources, SdrfNodeType.SOURCE);
        if (addMaterialTypeColumn) {
            this.allSourceColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addSampleHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.SAMPLE_NAME.toString());
        final boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, this.allSamples, SdrfNodeType.SAMPLE);
        if (addMaterialTypeColumn) {
            this.allSampleColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addExtractHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.EXTRACT_NAME.toString());
        final boolean addMaterialTypeColumn = addBiomaterialHeaders(headerRow, this.allExtracts, SdrfNodeType.EXTRACT);
        if (addMaterialTypeColumn) {
            this.allExtractColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addLabeledExtractHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.LABELED_EXTRACT_NAME.toString());
        boolean addLabelColumn = false;
        for (final LabeledExtract labeledExtract : this.allLabeledExtracts) {
            if (labeledExtract.getLabel() != null) {
                addLabelColumn = true;
            }
        }
        if (addLabelColumn) {
            this.allLabeledExtractColumns.add(SdrfColumnType.LABEL);
            headerRow.add(SdrfColumnType.LABEL.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
        final boolean addMaterialTypeColumn =
                addBiomaterialHeaders(headerRow, this.allLabeledExtracts, SdrfNodeType.LABELED_EXTRACT);
        if (addMaterialTypeColumn) {
            this.allLabeledExtractColumns.add(SdrfColumnType.MATERIAL_TYPE);
            headerRow.add(SdrfColumnType.MATERIAL_TYPE.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private boolean addBiomaterialHeaders(List<String> headerRow, List<? extends AbstractBioMaterial> biomaterials,
            SdrfNodeType nodeType) {
        boolean addMaterialTypeColumn = false;
        Map<String, SdrfCharacteristic> characteristics = null;
        switch (nodeType) {
        case SOURCE:
            characteristics = this.allSourceCharacteristics;
            break;
        case SAMPLE:
            characteristics = this.allSampleCharacteristics;
            break;
        case EXTRACT:
            characteristics = this.allExtractCharacteristics;
            break;
        case LABELED_EXTRACT:
            characteristics = this.allLabeledExtractCharacteristics;
            break;
        default:
            throw new IllegalArgumentException("Invalid Biomaterial node type: " + nodeType);
        }
        for (final AbstractBioMaterial biomaterial : biomaterials) {
            if (biomaterial.getMaterialType() != null) {
                addMaterialTypeColumn = true;
            }
            for (final Characteristic characteristic : biomaterial.getCharacteristics()) {
                handleNewCharacteristic(characteristic, headerRow, characteristics);
            }
        }
        for (final SdrfCharacteristic sdrfCharacteristic : characteristics.values()) {
            addNewCharacteristic(headerRow, sdrfCharacteristic);
        }
        return addMaterialTypeColumn;
    }

    private void handleNewCharacteristic(Characteristic characteristic, List<String> headerRow,
            Map<String, SdrfCharacteristic> characteristics) {
        final String category = characteristic.getCategory();
        SdrfCharacteristic sdrfCharacteristic = characteristics.get(category);
        if (sdrfCharacteristic == null) {
            sdrfCharacteristic = new SdrfCharacteristic(category);
            characteristics.put(category, sdrfCharacteristic);
        }
        if (characteristic.getTerm() != null) {
            sdrfCharacteristic.setHasTerm(true);
        }
        if (characteristic.getUnit() != null) {
            sdrfCharacteristic.setHasUnit(true);
        }
    }

    private void addNewCharacteristic(List<String> headerRow, SdrfCharacteristic sdrfCharacteristic) {
        final String columnHeader =
                SdrfColumnType.CHARACTERISTICS.toString() + "[" + sdrfCharacteristic.getCategory() + "]";
        headerRow.add(columnHeader);
        if (sdrfCharacteristic.isHasTerm()) {
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
        if (sdrfCharacteristic.isHasUnit()) {
            headerRow.add(SdrfColumnType.UNIT.toString());
            headerRow.add(SdrfColumnType.TERM_SOURCE_REF.toString());
        }
    }

    private void addHybridizationHeaders(List<String> headerRow) {
        headerRow.add(SdrfColumnType.HYBRIDIZATION_NAME.toString());
    }

    private void addRawDataHeaders(List<String> headerRow) {
        // TODO Handle case where raw data file and raw data matrix file are both present in a row.
        if (!(this.allArrayDataFiles.isEmpty())) {
            headerRow.add(SdrfColumnType.ARRAY_DATA_FILE.toString());
        } else {
            headerRow.add(SdrfColumnType.ARRAY_DATA_MATRIX_FILE.toString());
        }
    }

    private void addDerivedDataHeaders(List<String> headerRow) {
        // TODO Handle case where derived data file and derived data matrix file are both present in a row.
        if (!(this.allDerivedArrayDataFiles.isEmpty())) {
            headerRow.add(SdrfColumnType.DERIVED_ARRAY_DATA_FILE.toString());
        } else {
            headerRow.add(SdrfColumnType.DERIVED_ARRAY_DATA_MATRIX_FILE.toString());
        }
    }

    private void addSourceValues(List<String> row, Source source) {
        if (this.allSourceColumns.contains(SdrfColumnType.PROVIDER)) {
            final List<Provider> providers = source.getProviders();
            if (!(providers.isEmpty())) {
                row.add(providers.get(0).getName());
            } else {
                row.add("");
            }
        }
        addBiomaterialValues(row, source, SdrfNodeType.SOURCE);
    }

    private void addLabeledExtractValues(List<String> row, LabeledExtract labeledExtract) {
        if (this.allLabeledExtractColumns.contains(SdrfColumnType.LABEL)) {
            final OntologyTerm label = labeledExtract.getLabel();
            if (label != null) {
                row.add(label.getValue());
                final TermSource termSource = label.getTermSource();
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
        Map<String, SdrfCharacteristic> allCharacteristics = null;
        Set<SdrfColumnType> allColumns = null;
        switch (nodeType) {
        case SOURCE:
            allCharacteristics = this.allSourceCharacteristics;
            allColumns = this.allSourceColumns;
            break;
        case SAMPLE:
            allCharacteristics = this.allSampleCharacteristics;
            allColumns = this.allSampleColumns;
            break;
        case EXTRACT:
            allCharacteristics = this.allExtractCharacteristics;
            allColumns = this.allExtractColumns;
            break;
        case LABELED_EXTRACT:
            allCharacteristics = this.allLabeledExtractCharacteristics;
            allColumns = this.allLabeledExtractColumns;
            break;
        default: // Should never get here.
            return;
        }
        addCharacteristics(row, biomaterial, allCharacteristics.values());
        addMaterialType(row, biomaterial, allColumns);
    }

    private void addMaterialType(List<String> row, AbstractBioMaterial biomaterial, Set<SdrfColumnType> allColumns) {
        if (allColumns.contains(SdrfColumnType.MATERIAL_TYPE)) {
            final OntologyTerm materialType = biomaterial.getMaterialType();
            if (materialType != null) {
                row.add(materialType.getValue());
                final TermSource termSource = materialType.getTermSource();
                if ((termSource == null) || StringUtils.isBlank(termSource.getName())) {
                    row.add("");
                } else {
                    row.add(termSource.getName());
                }
            } else {
                // Add empty material type and Term Source REF.
                row.add("");
                row.add("");
            }
        }
    }

    private void addCharacteristics(List<String> row, AbstractBioMaterial biomaterial,
            Collection<SdrfCharacteristic> allCharacteristics) {
        for (final SdrfCharacteristic sdrfCharacteristic : allCharacteristics) {
            final Characteristic characteristic = biomaterial.getCharacteristic(sdrfCharacteristic.getCategory());
            addCharacteristic(row, characteristic, sdrfCharacteristic.isHasTerm(), sdrfCharacteristic.isHasUnit());
        }
    }

    private void addCharacteristic(List<String> row, Characteristic characteristic, boolean hasTerm, boolean hasUnit) {
        final List<String> values = new LinkedList<String>();
        // Measurement characteristic: Add value, unit and the unit's term source.
        if (characteristic == null || characteristic.getTermOrDirectValue() == null) {
            values.add("");
            if (hasTerm) {
                values.add("");
            }
            if (hasUnit) {
                values.addAll(Collections.nCopies(2, ""));
            }
        } else {
            values.add(characteristic.getTermOrDirectValue());
            if (characteristic.getTerm() != null) {
                final TermSource termSource = characteristic.getTerm().getTermSource();
                if (termSource == null || StringUtils.isBlank(termSource.getName())) {
                    values.add("");
                } else {
                    values.add(termSource.getName());
                }
            } else if (hasTerm) {
                values.add("");
            }
            final OntologyTerm unit = characteristic.getUnit();
            if (unit != null) {
                values.add(unit.getValue());
                final TermSource termSource = unit.getTermSource();
                if (termSource == null || StringUtils.isBlank(termSource.getName())) {
                    values.add("");
                } else {
                    values.add(termSource.getName());
                }
            } else if (hasUnit) {
                values.add("");
                values.add("");
            }
        }
        row.addAll(values);
    }

    private List<String> getHeaderLine(DelimitedFileReader tabDelimitedReader) throws IOException {
        List<String> nextLine = tabDelimitedReader.nextLine();
        while (isComment(nextLine)) {
            nextLine = tabDelimitedReader.nextLine();
        }
        return nextLine;
    }

    private boolean checkColumnsAreValid() {
        final List<ValidationMessage> messages = new LinkedList<ValidationMessage>();
        getDocumentSet().getValidatorSet().validateSdrfColumns(this.columns, messages);

        boolean hasError = false;
        for (final ValidationMessage message : messages) {
            getDocumentSet().getValidationResult().addMessage(getFile().getName(), message);
            hasError |= message.getType() == Type.ERROR;
        }

        return !hasError;
    }

    private boolean isComment(List<String> values) {
        return !values.isEmpty() && values.get(0).startsWith(COMMENT_CHARACTER);
    }

    private void handleHeaderLine(List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            this.columns.getColumns().add(new SdrfColumn(createHeading(values.get(i))));
        }
    }

    private void handleLine(List<String> values) {
        this.currentLine = values;
        if (!isComment(values)) {
            for (int i = 0; i < values.size(); i++) {
                this.currentColumnNumber = i + 1;
                try {
                    final String value = NCICommonsUtils.performXSSFilter(StringUtils.trim(values.get(i)), true, true);
                    handleValue(this.columns.getColumns().get(i), value);
                } catch (final Exception e) {
                    final StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    addError(e.toString() + ": " + sw.toString());
                }
            }
            this.currentNode = null;
            this.currentHybridization = null;
            this.currentScan = null;
            this.currentNormalization = null;
            this.currentFile = null;
            this.currentCommentable = null;
            this.lineNodeCache.clear();
        }
    }

    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
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
                this.currentTermSourceable = null;
                // intentional fall-through
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
            handleProtocolRef(value, this.columns.getNextColumn(column));
            break;
        case CHARACTERISTICS:
            handleCharacteristic(value, column, this.columns.getNextColumn(column),
                    this.columns.getNextColumn(this.columns.getNextColumn(column)));
            break;
        case MATERIAL_TYPE:
            handleMaterialType(column, value);
            break;
        case PARAMETER_VALUE:
            handleParameterValue(column, value, this.columns.getNextColumn(column),
                    this.columns.getNextColumn(this.columns.getNextColumn(column)));
            break;
        case TERM_SOURCE_REF:
            handleTermSourceRef(value);
            break;
        case UNIT:
            handleUnit(column, value, this.columns.getNextColumn(column));
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
            handleFactorValue(column, value, this.columns.getNextColumn(column),
                    this.columns.getNextColumn(this.columns.getNextColumn(column)));
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
            handleComment(value);
            break;
        default:
            break;
        }
    }

    private void handleDescription(String value) {
        if (this.currentBioMaterial == null) {
            addError("Description must be preceded by a Source, Sample, Extract, or LabeledExtract");
        } else {
            this.currentBioMaterial.setDescription(value);
        }
    }

    private void handleComment(String value) {
        if (this.currentCommentable == null) {
            addError("Comment must be preceded by a Node or Edge column");
        } else {
            this.currentCommentable.getComments().add(new Comment(value));
        }
    }

    private void handleBioMaterial(SdrfColumn column, String value) {
        handleNode(column, value);
        this.currentBioMaterial = (AbstractBioMaterial) this.currentNode;
    }

    private boolean isBlank(String value) {
        return StringUtils.isBlank(value) || EMPTY_SYMBOL.equals(value) || EXPORT_EMPTY_SYMBOL.equals(value);
    }

    private void handleHybridization(SdrfColumn column, String value) {
        handleNode(column, value);
        this.currentHybridization = (Hybridization) this.currentNode;
    }

    private void handleImageFile(SdrfColumn column, String value) {
        final Image image = new Image();
        image.setName(value);
        image.link(this.currentHybridization);
        this.currentNode = image;
    }

    private void handleScan(SdrfColumn column, String value) {
        handleNode(column, value, this.currentHybridization);
        this.currentScan = (Scan) this.currentNode;
    }

    private void handleNormalization(SdrfColumn column, String value) {
        handleNode(column, value, this.currentHybridization);
        this.currentNormalization = (Normalization) this.currentNode;
    }

    private void handleArrayDesignRef(SdrfColumn column, String value) {
        final ArrayDesign arrayDesign = getArrayDesign(value);
        arrayDesign.setValue(value);

        if (this.currentHybridization != null) {
            this.currentHybridization.setArrayDesign(arrayDesign);
        } else {
            addError("Array Design REF column must follow a Hybridization Name column");
        }

        this.currentCommentable = arrayDesign;
        this.currentTermSourceable = arrayDesign;
    }

    private void handleFactorValue(SdrfColumn column, String value, SdrfColumn nextColumn, SdrfColumn nextNextColumn) {
        final FactorValue factorValue = new FactorValue();
        factorValue.setFactor(this.idfDocument.getFactor(column.getHeading().getQualifier()));
        if (factorValue.getFactor() != null) {
            factorValue.addToSdrfList(this);
            if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF
                    && !isBlank(getNextValue())) {
                final OntologyTerm term = addOntologyTerm((String) null, value);
                factorValue.setTerm(term);
                this.currentTermSourceable = term;
                if (nextNextColumn != null && nextNextColumn.getType() == SdrfColumnType.UNIT) {
                    this.currentUnitable = factorValue;
                }
            } else {
                factorValue.setValue(value);
                if (nextColumn != null && nextColumn.getType() == SdrfColumnType.UNIT) {
                    this.currentUnitable = factorValue;
                }
            }

            if (this.currentHybridization != null) {
                this.currentHybridization.getFactorValues().add(factorValue);
            } else {
                addError("Factor Value columns must come after (to the right of) a Hybridization column");
            }
        } else {
            addError("Referenced Factor Name " + column.getHeading().getQualifier() + " was not found in the IDF");
        }
    }

    private AbstractSampleDataRelationshipNode getNodeToLinkToForArrayData(boolean derived) {
        final AbstractSampleDataRelationshipNode explicitNode = derived ? this.currentNormalization : this.currentScan;
        return explicitNode != null ? explicitNode : this.currentHybridization;
    }

    private void handleArrayDataFile(SdrfColumn column, String value, boolean derived) {
        handleNode(column, value, getNodeToLinkToForArrayData(derived));
        final AbstractNativeFileReference adf = (AbstractNativeFileReference) this.currentNode;
        adf.setNativeDataFile(getDocumentSet().getNativeDataFile(value));
        if (adf.getNativeDataFile() == null) {
            addErrorMessage("Referenced " + (derived ? "Derived " : "") + " Array Data File " + value
                    + " was not found in the document set");
        }
        if (derived && this.currentFile != null) {
            adf.link(this.currentFile);
        }
        this.currentFile = adf;
    }

    private void handleArrayDataMatrixFile(SdrfColumn column, String value, boolean derived) {
        handleNode(column, value, getNodeToLinkToForArrayData(derived));
        final AbstractDataMatrixReference admf = (AbstractDataMatrixReference) this.currentNode;
        admf.setDataMatrix(getDocumentSet().getArrayDataMatrix(value));
        if (admf.getDataMatrix() == null) {
            addErrorMessage("Referenced " + (derived ? "Derived " : "") + "Array Data Matrix File " + value
                    + " was not found in the document set");
        }
        if (derived && this.currentFile != null) {
            admf.link(this.currentFile);
        }
        this.currentFile = admf;
    }

    private void handleLabel(SdrfColumn column, String value) {
        final LabeledExtract labeledExtract = (LabeledExtract) this.currentBioMaterial;
        labeledExtract.setLabel(addOntologyTerm(MageTabOntologyCategory.LABEL_COMPOUND, value));
        this.currentTermSourceable = labeledExtract.getLabel();
    }

    private void handleUnit(SdrfColumn column, String value, SdrfColumn nextColumn) {
        final OntologyTerm unit = addOntologyTerm(column.getHeading().getQualifier(), value);
        unit.setValue(value);
        if (this.currentUnitable != null) {
            this.currentUnitable.setUnit(unit);
            this.currentUnitable = null;
        } else {
            addError("Illegal Unit column: Unit must follow a Characteristic, ParameterValue or FactorValue");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            this.currentTermSourceable = unit;
        }
    }

    private void handleMaterialType(SdrfColumn column, String value) {
        final OntologyTerm materialType = addOntologyTerm(MageTabOntologyCategory.MATERIAL_TYPE, value);
        this.currentBioMaterial.setMaterialType(materialType);
        this.currentTermSourceable = materialType;
    }

    private void handleTermSourceRef(String value) {
        final TermSource termSource = getTermSource(value);
        if (termSource == null) {
            addError("Term Source " + value + " is not defined in the IDF document");
        }
        if (this.currentTermSourceable != null) {
            this.currentTermSourceable.setTermSource(termSource);
            this.currentTermSourceable = null;
        }
    }

    private void handleCharacteristic(String value, SdrfColumn currentColumn, SdrfColumn nextColumn,
            SdrfColumn nextNextColumn) {
        final Characteristic characteristic = new Characteristic();
        if (!this.currentBioMaterial.isRepeated()) {
            this.currentBioMaterial.getCharacteristics().add(characteristic);
        }
        characteristic.setCategory(currentColumn.getHeading().getQualifier());

        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF && !isBlank(getNextValue())) {
            final OntologyTerm term = addOntologyTerm(characteristic.getCategory(), value);
            characteristic.setTerm(term);
            this.currentTermSourceable = term;
            if (nextNextColumn != null && nextNextColumn.getType() == SdrfColumnType.UNIT) {
                this.currentUnitable = characteristic;
            }
        } else {
            if (ExperimentOntologyCategory.ORGANISM.getCategoryName().
                    equals(currentColumn.getHeading().getQualifier())) {
                final OntologyTerm term = addOntologyTerm(characteristic.getCategory(), value);
                characteristic.setTerm(term);
            }
            characteristic.setValue(value);
            if (nextColumn != null && nextColumn.getType() == SdrfColumnType.UNIT) {
                this.currentUnitable = characteristic;
            }
        }
    }

    private void handleProtocolRef(String value, SdrfColumn nextColumn) {
        final ProtocolApplication protocolApp = new ProtocolApplication();
        protocolApp.setProtocol(getProtocol(value));
        if (!this.currentNode.isRepeated()) {
            this.currentNode.getProtocolApplications().add(protocolApp);
        }
        this.currentProtocolApp = protocolApp;
        if (protocolApp.getProtocol() == null) {
            protocolApp.setProtocol(new Protocol());
            protocolApp.getProtocol().setName(value);
            addWarning("Protocol " + value + " is not defined in the IDF document");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            this.currentTermSourceable = protocolApp.getProtocol();
        }

        this.currentCommentable = protocolApp;
    }

    private void handlePerformer(String value) {
        if (this.currentProtocolApp != null) {
            this.currentProtocolApp.setPerformer(value);
        } else {
            addWarning("Performer column with value " + value + " does not follow a Protocol REF column");
        }
    }

    private void handleProtocolDate(String value) {
        if (this.currentProtocolApp != null) {
            this.currentProtocolApp.setDate(parseDateValue(value, "Protocol Date"));
        } else {
            addWarning("Date column with value " + value + " does not follow a Protocol REF column");
        }
    }

    private void
            handleParameterValue(SdrfColumn column, String value, SdrfColumn nextColumn, SdrfColumn nextNextColumn) {
        final ParameterValue parameterValue = new ParameterValue();
        final Parameter param = new Parameter();
        param.setName(column.getHeading().getQualifier());
        parameterValue.setParameter(param);

        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF && !isBlank(getNextValue())) {
            final OntologyTerm term = addOntologyTerm((String) null, value);
            parameterValue.setTerm(term);
            this.currentTermSourceable = term;
            if (nextNextColumn != null && nextNextColumn.getType() == SdrfColumnType.UNIT) {
                this.currentUnitable = parameterValue;
            }
        } else {
            parameterValue.setValue(value);
            if (nextColumn != null && nextColumn.getType() == SdrfColumnType.UNIT) {
                this.currentUnitable = parameterValue;
            }
        }

        if (!this.currentNode.isRepeated()) {
            this.currentProtocolApp.getParameterValues().add(parameterValue);
        }

        this.currentCommentable = parameterValue;
    }

    private void handleNode(SdrfColumn column, String value, AbstractSampleDataRelationshipNode nodeToLinkTo) {
        final AbstractSampleDataRelationshipNode node = getOrCreateNode(column, value);
        if (nodeToLinkTo == null) {
            if (!this.leftmostNodes.contains(node)) {
                this.leftmostNodes.add(node);
            }
        } else {
            node.link(nodeToLinkTo);
        }
        this.currentNode = node;
        this.currentCommentable = this.currentNode;
    }

    private void handleNode(SdrfColumn column, String value) {
        handleNode(column, value, this.currentNode);
    }

    private AbstractSampleDataRelationshipNode getOrCreateNode(SdrfColumn column, String value) {
        final NodeKey nodeKey = getNodeKey(column, value);
        AbstractSampleDataRelationshipNode node = this.nodeCache.get(nodeKey);
        if (node == null) {
            node = createNode(column, value);
            this.nodeCache.put(nodeKey, node);
            this.lineNodeCache.put(nodeKey, node);
        } else {
            if (!this.lineNodeCache.containsKey(nodeKey)) {
                this.lineNodeCache.put(nodeKey, node);
            } else if (node.getNodeType().isName()) {
                addErrorMessage(this.currentLineNumber, this.currentColumnNumber, "Duplicate "
                        + column.getHeading().getTypeName() + " " + value);
            }
            node.setRepeated(true);
        }
        return node;
    }

    private AbstractSampleDataRelationshipNode createNode(SdrfColumn column, String value) {
        try {
            final AbstractSampleDataRelationshipNode node =
                    (AbstractSampleDataRelationshipNode) column.getType().getNodeClass().newInstance();
            node.setName(value);
            node.addToSdrfList(this);
            return node;
        } catch (final InstantiationException e) {
            throw new MageTabParsingRuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new MageTabParsingRuntimeException(e);
        }
    }

    private NodeKey getNodeKey(SdrfColumn column, String value) {
        return getNodeKey(column.getType(), value);
    }

    private NodeKey getNodeKey(SdrfColumnType heading, String value) {
        return new NodeKey(heading.getNodeClass(), value);
    }

    // return the value of the next column
    private String getNextValue() {
        // currentColumnNumber is 1-based
        return this.currentLine.get(this.currentColumnNumber);
    }

    private void handleProviders(String value) {
        if (this.currentBioMaterial.isRepeated()) {
            return;
        }
        final String[] providerNames = value.split(";");
        final int numProviders = providerNames.length;
        for (int i = 0; i < numProviders; i++) {
            final Provider provider = new Provider();
            provider.setName(providerNames[i]);
            if (this.currentBioMaterial instanceof Source) {
                ((Source) this.currentBioMaterial).getProviders().add(provider);
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
        return this.leftmostNodes;
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
                final NodeKey nodeKey = (NodeKey) obj;
                return this.nodeClass.equals(nodeKey.nodeClass) && this.nodeName.equalsIgnoreCase(nodeKey.nodeName);
            }
        }

        @Override
        public int hashCode() {
            // PMD is erroneously complaining about the call to toLowerCase, even though a Locale is used
            return this.nodeClass.hashCode() + this.nodeName.toLowerCase(Locale.getDefault()).hashCode(); // NOPMD
        }

    }

    /**
     * @return the allArrayDesigns
     */
    public List<ArrayDesign> getAllArrayDesigns() {
        final List<ArrayDesign> l = new ArrayList<ArrayDesign>();
        for (final Hybridization h : this.allHybridizations) {
            if (h.getArrayDesign() != null && !l.contains(h.getArrayDesign())) {
                l.add(h.getArrayDesign());
            }
        }
        return l;
    }

    /**
     * @return the allDerivedArrayDataFiles
     */
    public List<DerivedArrayDataFile> getAllDerivedArrayDataFiles() {
        return this.allDerivedArrayDataFiles;
    }

    /**
     * @return the allExtracts
     */
    public List<Extract> getAllExtracts() {
        return this.allExtracts;
    }

    /**
     * @return the allHybridizations
     */
    public List<Hybridization> getAllHybridizations() {
        return this.allHybridizations;
    }

    /**
     * @return the allImages
     */
    public List<Image> getAllImages() {
        return this.allImages;
    }

    /**
     * @return the allLabeledExtracts
     */
    public List<LabeledExtract> getAllLabeledExtracts() {
        return this.allLabeledExtracts;
    }

    /**
     * @return the allNormalizations
     */
    public List<Normalization> getAllNormalizations() {
        return this.allNormalizations;
    }

    /**
     * @return all biomaterials defined in this document
     */
    public List<AbstractBioMaterial> getAllBiomaterials() {
        final List<AbstractBioMaterial> list = new LinkedList<AbstractBioMaterial>();
        list.addAll(getAllSources());
        list.addAll(getAllSamples());
        list.addAll(getAllExtracts());
        list.addAll(getAllLabeledExtracts());
        return list;
    }

    /**
     * @return the allSamples
     */
    public List<Sample> getAllSamples() {
        return this.allSamples;
    }

    /**
     * @return the allScans
     */
    public List<Scan> getAllScans() {
        return this.allScans;
    }

    /**
     * @return the allSources
     */
    public List<Source> getAllSources() {
        return this.allSources;
    }

    /**
     * @return the allArrayDataFiles
     */
    public List<ArrayDataFile> getAllArrayDataFiles() {
        return this.allArrayDataFiles;
    }

    /**
     * @return the idfDocument
     */
    public IdfDocument getIdfDocument() {
        return this.idfDocument;
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
        return this.allArrayDataMatrixFiles;
    }

    /**
     * @return the allDerivedArrayDataMatrixFiles
     */
    public List<DerivedArrayDataMatrixFile> getAllDerivedArrayDataMatrixFiles() {
        return this.allDerivedArrayDataMatrixFiles;
    }

    /**
     * @return the allFactorValues
     */
    public List<FactorValue> getAllFactorValues() {
        return this.allFactorValues;
    }

    /**
     * Adds an error message with the current line and column number.
     * 
     * @param message error message
     */
    private void addError(String message) {
        addErrorMessage(this.currentLineNumber, this.currentColumnNumber, message);
    }

    /**
     * Adds a warning message with the current line and column number.
     * 
     * @param message warning message
     */
    private void addWarning(String message) {
        addWarningMessage(this.currentLineNumber, this.currentColumnNumber, message);
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
        final List<String> fileNames = new ArrayList<String>();
        fileNames.addAll(getFileNames(this.getAllArrayDataMatrixFiles()));
        fileNames.addAll(getFileNames(this.getAllDerivedArrayDataMatrixFiles()));
        return fileNames;
    }

    /**
     * Encapsulates the defining attributes of a characteristic.
     */
    private class SdrfCharacteristic {
        private final String category;
        private boolean hasUnit;
        private boolean hasTerm;

        SdrfCharacteristic(String categoryName) {
            if (StringUtils.isBlank(categoryName)) {
                throw new IllegalArgumentException("Characteristic should not have an empty category.");
            }
            this.category = categoryName;
        }

        String getCategory() {
            return this.category;
        }

        /**
         * @return the hasUnit
         */
        public boolean isHasUnit() {
            return this.hasUnit;
        }

        /**
         * @param hasUnit the hasUnit to set
         */
        public void setHasUnit(boolean hasUnit) {
            this.hasUnit = hasUnit;
        }

        /**
         * @return the hasTerm
         */
        public boolean isHasTerm() {
            return this.hasTerm;
        }

        /**
         * @param hasTerm the hasTerm to set
         */
        public void setHasTerm(boolean hasTerm) {
            this.hasTerm = hasTerm;
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
            final SdrfCharacteristic otherCharacteristic = (SdrfCharacteristic) otherObject;
            return (this.category.equals(otherCharacteristic.category));
        }

        /**
         * Default hashCode works on category.
         * 
         * @return the hashCode of this object
         */
        @Override
        public int hashCode() {
            if (StringUtils.isBlank(this.category)) {
                return System.identityHashCode(this);
            }

            return this.category.hashCode();
        }
    }
}
