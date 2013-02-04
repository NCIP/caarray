//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.data.NativeDataFile;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.validator.ValidatorSet;
import gov.nih.nci.caarray.util.io.FileUtility;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A set of parsed, interrelated MAGE-TAB documents. This class provides access to the files and the entities defined
 * within them.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class MageTabDocumentSet implements Serializable {

    private static final long serialVersionUID = -2836359210806454994L;

    private final Set<IdfDocument> idfDocuments = new HashSet<IdfDocument>();
    private final Set<SdrfDocument> sdrfDocuments = new HashSet<SdrfDocument>();
    private final Set<AdfDocument> adfDocuments = new HashSet<AdfDocument>();
    private final Set<DataMatrix> dataMatrixes = new HashSet<DataMatrix>();
    private final Set<NativeDataFile> nativeDataFiles = new HashSet<NativeDataFile>();
    private final Set<OntologyTerm> termCache = new HashSet<OntologyTerm>();
    private final Map<String, TermSource> termSourceCache = new HashMap<String, TermSource>();
    private final Map<String, ArrayDesign> arrayDesignCache = new HashMap<String, ArrayDesign>();
    private final Map<String, Protocol> protocolCache = new HashMap<String, Protocol>();
    private final ValidationResult validationResult = new ValidationResult();
    private final Map<String, List<Hybridization>> hybridizations = new HashMap<String, List<Hybridization>>();
    private final Map<String, List<Sample>> samples = new HashMap<String, List<Sample>>();
    private final ValidatorSet validatorSet;

    /**
     * Create a new MAGE-TAB document set based on the given set of input files, using no validations.
     * 
     * @param inputFileSet the set of files to hold the exported contents of the documents.
     */
    public MageTabDocumentSet(MageTabFileSet inputFileSet) {
        this(inputFileSet, ValidatorSet.NO_VALIDATION);
    }

    /**
     * Create a new MAGE-TAB document set based on the given set of input files, using the given set of validations.
     * 
     * @param inputFileSet the set of files to hold the exported contents of the documents.
     * @param validatorSet the ValidatorSet to use to validate the input files.
     */
    public MageTabDocumentSet(MageTabFileSet inputFileSet, ValidatorSet validatorSet) {
        initializeFromFileSet(inputFileSet);
        this.validatorSet = validatorSet;
    }

    /**
     * @return the validatorSet
     */
    public ValidatorSet getValidatorSet() {
        return this.validatorSet;
    }

    /**
     * @return the idfDocuments
     */
    public Set<IdfDocument> getIdfDocuments() {
        return this.idfDocuments;
    }

    /**
     * @return the sdrfDocuments
     */
    public Set<SdrfDocument> getSdrfDocuments() {
        return this.sdrfDocuments;
    }

    /**
     * @return the adfDocuments
     */
    public Set<AdfDocument> getAdfDocuments() {
        return this.adfDocuments;
    }

    /**
     * @return the dataMatrixes
     */
    public Set<DataMatrix> getDataMatrixes() {
        return this.dataMatrixes;
    }

    /**
     * @return the nativeDataFiles
     */
    public Set<NativeDataFile> getNativeDataFiles() {
        return this.nativeDataFiles;
    }

    /**
     * Returns all <code>TermSources</code> used in the document set.
     * 
     * @return the <code>TermSources</code>.
     */
    public Collection<TermSource> getTermSources() {
        return this.termSourceCache.values();
    }

    /**
     * Returns all <code>TermSources</code> used in the document set mapped to their names.
     * 
     * @return the <code>TermSources</code> map.
     */
    public Map<String, TermSource> getTermSourceMap() {
        return this.termSourceCache;
    }

    /**
     * Returns all <code>Protocols</code> defined in the document set.
     * 
     * @return the <code>Protocols</code>.
     */
    public Collection<Protocol> getProtocols() {
        return this.protocolCache.values();
    }

    /**
     * Returns all <code>OntologyTerms</code> used in the document set.
     * 
     * @return the <code>OntologyTerms</code>.
     */
    public Collection<OntologyTerm> getTerms() {
        return this.termCache;
    }

    private void initializeFromFileSet(MageTabFileSet inputFileSet) {
        initializeIdfs(inputFileSet);
        initializeAdfs(inputFileSet);
        initializeSdrfs(inputFileSet);
        initializeDataMatrixes(inputFileSet);
        initializeNativeDataFiles(inputFileSet);
    }

    private void initializeIdfs(MageTabFileSet inputFileSet) {
        for (final FileRef file : inputFileSet.getIdfFiles()) {
            this.idfDocuments.add(new IdfDocument(this, file));
        }
    }

    private void initializeAdfs(MageTabFileSet inputFileSet) {
        for (final FileRef file : inputFileSet.getAdfFiles()) {
            // TODO Implement initializeAdfs and remove placeholder line below
            FileUtility.checkFileExists(file);
        }
    }

    private void initializeSdrfs(MageTabFileSet inputFileSet) {
        for (final FileRef file : inputFileSet.getSdrfFiles()) {
            this.sdrfDocuments.add(new SdrfDocument(this, file));
        }
    }

    private void initializeDataMatrixes(MageTabFileSet inputFileSet) {
        for (final FileRef file : inputFileSet.getDataMatrixFiles()) {
            this.dataMatrixes.add(new DataMatrix(this, file));
        }
    }

    private void initializeNativeDataFiles(MageTabFileSet inputFileSet) {
        for (final FileRef file : inputFileSet.getNativeDataFiles()) {
            this.nativeDataFiles.add(new NativeDataFile(this, file));
        }
    }

    void parse() throws MageTabParsingException {
        // DEVELOPER NOTE: currently multiple IDFs are not supported, so flag this as validation
        // error and do not attempt further processing. in the future, there is intention
        // to support this, so the object model will continue to support it
        if (this.idfDocuments.size() > 1) {
            for (final IdfDocument idfDocument : this.idfDocuments) {
                getValidationResult().addMessage(idfDocument.getFile().getName(),
                        ValidationMessage.Type.ERROR, "At most one IDF document can be present in an import");
            }
            return;
        }
        parse(this.idfDocuments);
        parse(this.sdrfDocuments);

        // map sdrfs to the samples and hybridizations they contain
        generateSdrfRefHybs();
        generateSdrfRefSamples();
    }

    /**
     * Special parse method that skips adding validation results to files and does not generate hybs or samples. If More
     * than 1 idf doc is encountered a runtime exception is thrown.
     * 
     * @throws MageTabParsingException
     */
    void parseNoValidation() throws MageTabParsingException {
        if (this.idfDocuments.size() > 1) {
            throw new IllegalArgumentException("Only one IDF document can be present when parsing for data files.");
        }

        final AbstractMageTabDocument idfDoc = this.idfDocuments.iterator().next();
        idfDoc.parse();
        final IdfDocument idf = (IdfDocument) idfDoc;
        for (final SdrfDocument document : idf.getSdrfDocuments()) {
            document.parseNoIdfCheck();
        }

    }

    private void parse(Set<? extends AbstractMageTabDocument> documents) throws MageTabParsingException {
        for (final AbstractMageTabDocument document : documents) {
            document.parse();
        }
    }

    /**
     * @return the list of names of all raw data files referenced by any SDRF in this document set.
     */
    public List<String> getSdrfReferencedRawFileNames() {
        final List<String> fileNames = new ArrayList<String>();
        for (final SdrfDocument sdrf : this.sdrfDocuments) {
            fileNames.addAll(sdrf.getReferencedRawFileNames());
        }
        return fileNames;
    }

    /**
     * @return the list of names of all derived data files referenced by any SDRF in this document set.
     */
    public List<String> getSdrfReferencedDerivedFileNames() {
        final List<String> fileNames = new ArrayList<String>();
        for (final SdrfDocument sdrf : this.sdrfDocuments) {
            fileNames.addAll(sdrf.getReferencedDerivedFileNames());
        }
        return fileNames;
    }

    /**
     * @return the list of names of all data matrix files referenced by any SDRF in this document set.
     */
    public List<String> getSdrfReferencedDataMatrixFileNames() {
        final List<String> fileNames = new ArrayList<String>();
        for (final SdrfDocument sdrf : this.sdrfDocuments) {
            fileNames.addAll(sdrf.getReferencedDataMatrixFileNames());
        }
        return fileNames;
    }

    /**
     * Returns an <code>OntologyTerm</code> matching the category and name given. Reuses an existing matching
     * <code>OntologyTerm</code> in the document set if one exists, otherwise creates one.
     * 
     * @param category category of the term
     * @param value value of the term
     * @return the new or matching term.
     */
    OntologyTerm addOntologyTerm(String category, String value) {
        final OntologyTerm term = new OntologyTerm();
        term.setCategory(category);
        term.setValue(value);
        this.termCache.add(term);
        return term;
    }

    TermSource getOrCreateTermSource(String name) {
        TermSource termSource = getTermSource(name);
        if (termSource == null) {
            termSource = new TermSource(name);
            this.termSourceCache.put(name, termSource);
        }
        return termSource;
    }

    TermSource getTermSource(String name) {
        return this.termSourceCache.get(name);
    }

    ArrayDesign getArrayDesign(String name) {
        ArrayDesign arrayDesign = this.arrayDesignCache.get(name);
        if (arrayDesign == null) {
            arrayDesign = new ArrayDesign();
            arrayDesign.setValue(name);
            this.arrayDesignCache.put(name, arrayDesign);
        }
        return arrayDesign;
    }

    /**
     * Adds a new Protocol to the document set.
     * 
     * @param protocol the new protocol.
     */
    void addProtocol(Protocol protocol) {
        if (protocol.getName() == null) {
            throw new IllegalArgumentException("Protocol name was null");
        }
        this.protocolCache.put(protocol.getName(), protocol);
    }

    /**
     * Returns the protocol with the id (name) provided.
     * 
     * @param protocolId find protocol with this name.
     * @return the matching protocol or null if none exists for name.
     */
    Protocol getProtocol(String protocolId) {
        return this.protocolCache.get(protocolId);
    }

    /**
     * Returns the <code>IdfDocument</code> that matches the filename provided, or null if none match.
     * 
     * @param filename locate <code>IdfDocument</code> with this filename
     * @return the <code>IdfDocument</code>.
     */
    public IdfDocument getIdfDocument(String filename) {
        return (IdfDocument) getDocument(this.idfDocuments, filename);
    }

    /**
     * Returns the <code>SdrfDocument</code> that matches the filename provided, or null if none match.
     * 
     * @param filename locate <code>SdrfDocument</code> with this filename
     * @return the <code>SdrfDocument</code>.
     */
    public SdrfDocument getSdrfDocument(String filename) {
        return (SdrfDocument) getDocument(this.sdrfDocuments, filename);
    }

    /**
     * Returns the <code>AdfDocument</code> that matches the filename provided, or null if none match.
     * 
     * @param filename locate <code>AdfDocument</code> with this filename
     * @return the <code>AdfDocument</code>.
     */
    public AdfDocument getAdfDocument(String filename) {
        return (AdfDocument) getDocument(this.adfDocuments, filename);
    }

    /**
     * Returns the <code>DataMatrix</code> that matches the filename provided, or null if none match.
     * 
     * @param filename locate <code>DataMatrix</code> with this filename
     * @return the <code>DataMatrix</code>.
     */
    public DataMatrix getArrayDataMatrix(String filename) {
        return (DataMatrix) getDocument(this.dataMatrixes, filename);
    }

    /**
     * Return a set of the hybridizations referenced by the sdrf files.
     * 
     * @return set of Hybridization
     */
    public Map<String, List<Hybridization>> getSdrfHybridizations() {
        return this.hybridizations;
    }

    /**
     * Return a set of the sample ref'd by the sdrf files.
     * 
     * @return set of Samples
     */
    public Map<String, List<Sample>> getSdrfSamples() {
        return this.samples;
    }

    /**
     * Returns the <code>NativeDataFile</code> that matches the filename provided, or null if none match.
     * 
     * @param filename locate <code>NativeDataFile</code> with this filename
     * @return the <code>NativeDataFile</code>.
     */
    public NativeDataFile getNativeDataFile(String filename) {
        return (NativeDataFile) getDocument(this.nativeDataFiles, filename);
    }

    private AbstractMageTabDocument getDocument(Set<? extends AbstractMageTabDocument> documents, String filename) {
        for (final AbstractMageTabDocument document : documents) {
            if (document.getFile().getName().equals(filename)) {
                return document;
            }
        }
        return null;
    }

    /**
     * @return the validationResult
     */
    public ValidationResult getValidationResult() {
        return this.validationResult;
    }

    ValidationMessage createValidationMessage(FileRef file, ValidationMessage.Type type, String message) {
        return getValidationResult().addMessage(file.getName(), type, message);
    }

    private void generateSdrfRefHybs() {
        for (final SdrfDocument sdrf : this.sdrfDocuments) {
            this.hybridizations.put(sdrf.getFile().getName(), sdrf.getAllHybridizations());
        }
    }

    private void generateSdrfRefSamples() {
        for (final SdrfDocument sdrf : this.sdrfDocuments) {
            this.samples.put(sdrf.getFile().getName(), sdrf.getAllSamples());
        }
    }

    /**
     * @return if this MageTabDocumentSet references only a partial sdrf file.
     */
    public boolean hasPartialSdrf() {
        for (SdrfDocument doc : sdrfDocuments) {
            if (doc.getFile().isPartialFile()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Exports the contents of the MAGE-TAB documents into files.
     */
    public void export() {
        export(this.idfDocuments);
        export(this.sdrfDocuments);
    }

    private void export(Set<? extends AbstractMageTabDocument> documents) {
        for (final AbstractMageTabDocument document : documents) {
            document.export();
        }
    }
}
