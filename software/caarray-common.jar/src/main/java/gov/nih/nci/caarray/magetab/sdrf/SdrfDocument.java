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
import gov.nih.nci.caarray.magetab.data.NativeDataFile;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Represents a Sample and Data Relationship Format (SDRF) file - a tab-delimited file describing the relationships
 * between samples, arrays, data, and other objects used or produced in the investigation, and providing all MIAME
 * information that is not provided elsewhere. This is often the least trivial part of the experiment description due to
 * the complex relationshipswhich are possible between samples and their respective hybridizations; however, for simple
 * experimental designs, constructing the SDRF file is straightforward, and even complex loop designs can be expressed
 * in this format.
 */
@SuppressWarnings("PMD")
// Exception to PMD checking due to cycolmetric complexity and number of fields
public final class SdrfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 1116542609494378874L;
    private static final String EMPTY_SYMBOL = "->";
    private IdfDocument idfDocument;
    private final List<SdrfColumn> columns = new ArrayList<SdrfColumn>();
    private final Map<NodeKey, AbstractSampleDataRelationshipNode> nodeCache =
        new HashMap<NodeKey, AbstractSampleDataRelationshipNode>();
    private AbstractSampleDataRelationshipNode currentNode;
    private Unitable currentUnitable;
    private TermSourceable currentTermSourceable;
    private final List<AbstractSampleDataRelationshipNode> leftmostNodes =
        new ArrayList<AbstractSampleDataRelationshipNode>();
    private ProtocolApplication currentProtocolApp;
    private Hybridization currentHybridization;
    private ArrayDesign currentArrayDesign;
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
     * Parses the MAGE-TAB document, creating the object graph of entities.
     *
     * @throws MageTabParsingException
     *
     * @throws MageTabParsingException if the document couldn't be read.
     * @throws MageTabTextFileLoaderException
     */
    @Override
    protected void parse() throws MageTabParsingException {
        if (checkHasIdf()) {
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
        }
    }

    private List<String> getHeaderLine(DelimitedFileReader tabDelimitedReader) {
        List<String> nextLine = tabDelimitedReader.nextLine();
        while (isComment(nextLine)) {
            nextLine = tabDelimitedReader.nextLine();
        }
        return nextLine;
    }

    private boolean isComment(List<String> values) {
        return !values.isEmpty() && values.get(0).startsWith(COMMENT_CHARACTER);
    }

    private boolean minimumColumnCheck() {
        boolean bioMaterial = false;
        boolean hybridization = false;
        boolean dataFile = false;
        // file should have
        // 1. BioMaterial (Souce, Sample, Extract, Labeled Extract)
        // 2. Hybridization
        // 3. data file
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
                handleValue(columns.get(i), values.get(i));
            }
            currentNode = null;
            currentArrayDesign = null;
            currentHybridization = null;
        }
    }

    @SuppressWarnings("PMD")
    // warnings suppressed due to long switch statement
    private void handleValue(SdrfColumn column, String value) {
        if (isBlank(value)) {
            return;
        }
        switch (column.getType()) {
        case SOURCE_NAME:
        case SAMPLE_NAME:
        case EXTRACT_NAME:
        case LABELED_EXTRACT_NAME:
        case SCAN_NAME:
        case NORMALIZATION_NAME:
            handleNode(column, value);
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
            handleArrayDesignFile(column, value);
            break;
        case ARRAY_DESIGN_REF:
            handleArrayDesignRef(column, value);
            break;
        case DERIVED_ARRAY_DATA_FILE:
            handleDerivedArrayDataFile(value);
            break;
        case ARRAY_DATA_FILE:
            handleArrayDataFile(value);
            break;
        case ARRAY_DATA_MATRIX_FILE:
            handleArrayDataMatrixFile(value);
            break;
        case DERIVED_ARRAY_DATA_MATRIX_FILE:
            handleDerivedArrayDataMatrixFile(value);
            break;
        case IMAGE_FILE:
            handleImageFile(column, value);
            break;
        case FACTOR_VALUE:
            handleFactorValue(column, value);
            break;
        case PERFORMER:
            // handlePerformer(value);
            break;
        case DATE:
            // handleDate(value);
            break;
        case DESCRIPTION:
            // handleDescription(value);
            break;
        case COMMENT:
            // handleComment(column, value);
            break;
        default:
            break;
        }
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
        image.link(currentNode);
        currentNode = image;
    }

    private void handleArrayDesignFile(SdrfColumn column, String value) {
        ArrayDesign arrayDesign = arrayDesignHelper(column, value);
        arrayDesign.setFile(getDocumentSet().getAdfDocument(value).getFile());
        arrayDesign.setArrayDesignRef(false);

    }

    private void handleArrayDesignRef(SdrfColumn column, String value) {
        ArrayDesign arrayDesign = arrayDesignHelper(column, value);
        arrayDesign.setArrayDesignRef(true);
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
        factorValue.addToSdrfList(this);
        factorValue.setValue(value);
        currentUnitable = factorValue;
        // set it on a Hybridization
        allHybridizations.get(allHybridizations.size() - 1).getFactorValues().add(factorValue);
    }

    private void handleDerivedArrayDataFile(String value) {
        DerivedArrayDataFile adf = new DerivedArrayDataFile();
        adf.setNativeDataFile(getDocumentSet().getNativeDataFile(value));
        if (adf.getNativeDataFile() == null) {
            addErrorMessage("Referenced Derived Array Data File " + value + " was not found in the document set");
        }
        adf.setName(value);
        adf.addToSdrfList(this);
        adf.link(currentNode);
        currentNode = adf;
    }

    private void handleArrayDataFile(String value) {
        ArrayDataFile adf = new ArrayDataFile();
        NativeDataFile nativeDataFile = getDocumentSet().getNativeDataFile(value);
        if (nativeDataFile == null) {
            addErrorMessage("Referenced Array Data File " + value + " was not found in the document set");
        }
        adf.setNativeDataFile(nativeDataFile);
        adf.setName(value);
        adf.addToSdrfList(this);
        adf.link(currentNode);
        currentNode = adf;
    }

    private void handleDerivedArrayDataMatrixFile(String value) {
        DerivedArrayDataMatrixFile admf = new DerivedArrayDataMatrixFile();
        admf.setDataMatrix(getDocumentSet().getArrayDataMatrix(value));
        admf.setName(value);
        admf.addToSdrfList(this);
        admf.link(currentNode);
        currentNode = admf;
    }

    private void handleArrayDataMatrixFile(String value) {
        ArrayDataMatrixFile admf = new ArrayDataMatrixFile();
        admf.setDataMatrix(getDocumentSet().getArrayDataMatrix(value));
        admf.setName(value);
        admf.addToSdrfList(this);
        admf.link(currentNode);
        currentNode = admf;
    }

    private void handleLabel(SdrfColumn column, String value) {
        LabeledExtract labeledExtract = (LabeledExtract) currentNode;
        labeledExtract.setLabel(addMgedOntologyTerm(MageTabOntologyCategory.LABEL_COMPOUND, value));
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

    private void handleUnit(SdrfColumn column, String value, SdrfColumn nextColumn) {
        OntologyTerm unit = addOntologyTerm(column.getHeading().getQualifier(), value);
        unit.setValue(value);
        if (currentUnitable != null) {
            currentUnitable.setUnit(unit);
        } else {
            addErrorMessage("Illegal Unit column: Unit must follow a Characteristic, ParameterValue or FactorValue");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            currentTermSourceable = unit;
        }
    }

    private void handleMaterialType(SdrfColumn column, String value) {
        OntologyTerm materialType = addMgedOntologyTerm(MageTabOntologyCategory.MATERIAL_TYPE, value);
        ((AbstractBioMaterial) currentNode).setMaterialType(materialType);
        currentTermSourceable = materialType;
    }

    private void handleTermSourceRef(String value) {
        TermSource termSource = getTermSource(value);
        if (termSource == null) {
            addWarningMessage(currentLineNumber, currentColumnNumber,
                    "Term Source " + value + " is not defined in the IDF document");
        }
        currentTermSourceable.setTermSource(termSource);
    }

    private void handleCharacteristic(String value, SdrfColumn currentColumn, SdrfColumn nextColumn) {
        Characteristic characteristic = new Characteristic();
        ((AbstractBioMaterial) currentNode).getCharacteristics().add(characteristic);
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
        currentNode.getProtocolApplications().add(protocolApp);
        currentProtocolApp = protocolApp;
        if (protocolApp.getProtocol() == null) {
            protocolApp.setProtocol(new Protocol());
            protocolApp.getProtocol().setName(value);
            addWarningMessage("Protocol " + value + " is not defined in the IDF document");
        }
        if (nextColumn != null && nextColumn.getType() == SdrfColumnType.TERM_SOURCE_REF) {
            currentTermSourceable = protocolApp.getProtocol();
        }
    }

    private void handleParameterValue(SdrfColumn column, String value) {
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setValue(value);
        Parameter param = new Parameter();
        param.setName(column.getHeading().getQualifier());
        parameterValue.setParameter(param);
        currentUnitable = parameterValue;
        currentProtocolApp.getParameterValues().add(parameterValue);
    }

    private void handleNode(SdrfColumn column, String value) {
        AbstractSampleDataRelationshipNode node = getOrCreateNode(column, value);
        if (currentNode == null) {
            if (!leftmostNodes.contains(node)) {
                leftmostNodes.add(node);
            }
        } else {
            node.link(currentNode);
        }
        currentNode = node;
    }

    private AbstractSampleDataRelationshipNode getOrCreateNode(SdrfColumn column, String value) {
        NodeKey nodeKey = getNodeKey(column, value);
        AbstractSampleDataRelationshipNode node = nodeCache.get(nodeKey);
        if (node == null) {
            node = createNode(column, value);
            nodeCache.put(nodeKey, node);
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
        String[] providerNames = value.split(";");
        if (providerNames == null) {
            return;
        }
        int numProviders = providerNames.length;
        for (int i = 0; i < numProviders; i++) {
            Provider provider = new Provider();
            provider.setName(providerNames[i]);
            ((Source) currentNode).getProviders().add(provider);
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
                return nodeClass.equals(nodeKey.nodeClass) && nodeName.equals(nodeKey.nodeName);
            }
        }

        @Override
        public int hashCode() {
            return nodeClass.hashCode() + nodeName.hashCode();
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

}
