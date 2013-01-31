//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.adf.AdfDocument;
import gov.nih.nci.caarray.magetab.data.DataMatrix;
import gov.nih.nci.caarray.magetab.data.NativeDataFile;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import gov.nih.nci.caarray.magetab.sdrf.Hybridization;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.util.io.FileUtility;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
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
public final class MageTabDocumentSet implements Serializable {

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
    private final Set<Hybridization> hybridizations = new HashSet<Hybridization>();
    private final Set<Sample> samples = new HashSet<Sample>();

    /**
     * Initialize the MAGE-TAB document set with the given files which will hold the exported contents.
     *
     * @param inputFileSet the set of files to hold the exported contents of the documents.
     */
    public MageTabDocumentSet(MageTabFileSet inputFileSet) {
        initializeFromFileSet(inputFileSet);
    }

    /**
     * @return the idfDocuments
     */
    public Set<IdfDocument> getIdfDocuments() {
        return idfDocuments;
    }

    /**
     * @return the sdrfDocuments
     */
    public Set<SdrfDocument> getSdrfDocuments() {
        return sdrfDocuments;
    }

    /**
     * @return the adfDocuments
     */
    public Set<AdfDocument> getAdfDocuments() {
        return adfDocuments;
    }

    /**
     * @return the dataMatrixes
     */
    public Set<DataMatrix> getDataMatrixes() {
        return dataMatrixes;
    }

    /**
     * @return the nativeDataFiles
     */
    public Set<NativeDataFile> getNativeDataFiles() {
        return nativeDataFiles;
    }

    /**
     * Returns all <code>TermSources</code> used in the document set.
     *
     * @return the <code>TermSources</code>.
     */
    public Collection<TermSource> getTermSources() {
        return termSourceCache.values();
    }

    /**
     * Returns all <code>TermSources</code> used in the document set mapped to their names.
     *
     * @return the <code>TermSources</code> map.
     */
    public Map<String, TermSource> getTermSourceMap() {
        return termSourceCache;
    }

    /**
     * Returns all <code>Protocols</code> defined in the document set.
     *
     * @return the <code>Protocols</code>.
     */
    public Collection<Protocol> getProtocols() {
        return protocolCache.values();
    }

    /**
     * Returns all <code>OntologyTerms</code> used in the document set.
     *
     * @return the <code>OntologyTerms</code>.
     */
    public Collection<OntologyTerm> getTerms() {
        return termCache;
    }

    private void initializeFromFileSet(MageTabFileSet inputFileSet) {
        initializeIdfs(inputFileSet);
        initializeAdfs(inputFileSet);
        initializeSdrfs(inputFileSet);
        initializeDataMatrixes(inputFileSet);
        initializeNativeDataFiles(inputFileSet);
    }

    private void initializeIdfs(MageTabFileSet inputFileSet) {
        for (File file : inputFileSet.getIdfFiles()) {
            idfDocuments.add(new IdfDocument(this, file));
        }
    }

    private void initializeAdfs(MageTabFileSet inputFileSet) {
        for (File file : inputFileSet.getAdfFiles()) {
            // TODO Implement initializeAdfs and remove placeholder line below
            FileUtility.checkFileExists(file);
        }
    }

    private void initializeSdrfs(MageTabFileSet inputFileSet) {
        for (File file : inputFileSet.getSdrfFiles()) {
            sdrfDocuments.add(new SdrfDocument(this, file));
        }
    }

    private void initializeDataMatrixes(MageTabFileSet inputFileSet) {
        for (File file : inputFileSet.getDataMatrixFiles()) {
            dataMatrixes.add(new DataMatrix(this, file));
        }
    }

    private void initializeNativeDataFiles(MageTabFileSet inputFileSet) {
        for (File file : inputFileSet.getNativeDataFiles()) {
            nativeDataFiles.add(new NativeDataFile(this, file));
        }
    }

    void parse() throws MageTabParsingException {
        // DEVELOPER NOTE: currently multiple IDFs are not supported, so flag this as validation
        // error and do not attempt further processing. in the future, there is intention
        // to support this, so the object model will continue to support it
        if (idfDocuments.size() > 1) {
            for (IdfDocument idfDocument : idfDocuments) {
                getValidationResult().addMessage(idfDocument.getFile(), ValidationMessage.Type.ERROR,
                        "At most one IDF document can be present in an import");
            }
            return;
        }
        parse(idfDocuments);
        // DEVELOPER NOTE: ADF documents currently not parsed
        parse(sdrfDocuments);
        // DEVELOPER NOTE: DATA MATRIX documents currently not parsed

        checkSdrfRefDataFiles();

        generateSdrfRefHybs();

        generateSdrfRefSamples();
    }

    private void parse(Set<? extends AbstractMageTabDocument> documents) throws MageTabParsingException {
        for (AbstractMageTabDocument document : documents) {
            document.parse();
        }
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
        OntologyTerm term = new OntologyTerm();
        term.setCategory(category);
        term.setValue(value);
        termCache.add(term);
        return term;
    }

    TermSource getOrCreateTermSource(String name) {
        TermSource termSource = getTermSource(name);
        if (termSource == null) {
            termSource = new TermSource(name);
            termSourceCache.put(name, termSource);
        }
        return termSource;
    }

    TermSource getTermSource(String name) {
        return termSourceCache.get(name);
    }

    ArrayDesign getArrayDesign(String name) {
        ArrayDesign arrayDesign = arrayDesignCache.get(name);
        if (arrayDesign == null) {
            arrayDesign = new ArrayDesign();
            arrayDesign.setName(name);
            arrayDesignCache.put(name, arrayDesign);
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
        protocolCache.put(protocol.getName(), protocol);
    }

    /**
     * Returns the protocol with the id (name) provided.
     *
     * @param protocolId find protocol with this name.
     * @return the matching protocol or null if none exists for name.
     */
    Protocol getProtocol(String protocolId) {
        return protocolCache.get(protocolId);
    }

    /**
     * Returns the <code>IdfDocument</code> that matches the filename provided, or null if none match.
     *
     * @param filename locate <code>IdfDocument</code> with this filename
     * @return the <code>IdfDocument</code>.
     */
    public IdfDocument getIdfDocument(String filename) {
        return (IdfDocument) getDocument(idfDocuments, filename);
    }

    /**
     * Returns the <code>SdrfDocument</code> that matches the filename provided, or null if none match.
     *
     * @param filename locate <code>SdrfDocument</code> with this filename
     * @return the <code>SdrfDocument</code>.
     */
    public SdrfDocument getSdrfDocument(String filename) {
        return (SdrfDocument) getDocument(sdrfDocuments, filename);
    }

    /**
     * Returns the <code>AdfDocument</code> that matches the filename provided, or null if none match.
     *
     * @param filename locate <code>AdfDocument</code> with this filename
     * @return the <code>AdfDocument</code>.
     */
    public AdfDocument getAdfDocument(String filename) {
        return (AdfDocument) getDocument(adfDocuments, filename);
    }

    /**
     * Returns the <code>DataMatrix</code> that matches the filename provided, or null if none match.
     *
     * @param filename locate <code>DataMatrix</code> with this filename
     * @return the <code>DataMatrix</code>.
     */
    public DataMatrix getArrayDataMatrix(String filename) {
        return (DataMatrix) getDocument(dataMatrixes, filename);
    }

    /**
     * Return a set of the hybridizations ref'd by the sdrf files.
     * @return set of Hybs
     */
    public Set<Hybridization> getSdrfHybridizations() {
        return this.hybridizations;
    }

    /**
     * Return a set of the sample ref'd by the sdrf files.
     * @return set of Samples
     */
    public Set<Sample> getSdrfSamples() {
        return this.samples;
    }

    /**
     * Returns the <code>NativeDataFile</code> that matches the filename provided, or null if none match.
     *
     * @param filename locate <code>NativeDataFile</code> with this filename
     * @return the <code>NativeDataFile</code>.
     */
    public NativeDataFile getNativeDataFile(String filename) {
        return (NativeDataFile) getDocument(nativeDataFiles, filename);
    }

    private AbstractMageTabDocument getDocument(Set<? extends AbstractMageTabDocument> documents, String filename) {
        for (AbstractMageTabDocument document : documents) {
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
        return validationResult;
    }

    ValidationMessage createValidationMessage(File file, ValidationMessage.Type type, String message) {
        return getValidationResult().addMessage(file, type, message);
    }

    private void checkSdrfRefDataFiles() {
        // check that all native data files are mentioned in the sdrf
        // get all the file names

        List<String> fileNames = generateSdrfRefFileNames();

        checkFileNames(fileNames);

        if (this.getNativeDataFiles() != null && !this.getNativeDataFiles().isEmpty() && fileNames.isEmpty()) {
            addSdrfErrorMessage("any files");
        }
    }

    private void checkFileNames(List<String> fileNames) {
        if (this.getNativeDataFiles() != null && !this.getNativeDataFiles().isEmpty() && !fileNames.isEmpty()) {
            for (NativeDataFile ndf : this.getNativeDataFiles()) {
                if (!fileNames.contains(ndf.getFile().getName())) {
                    addSdrfErrorMessage(ndf.getFile().getName());
                }
            }
        }
    }



    private void addSdrfErrorMessage(String txt) {
        for (SdrfDocument sdrf : sdrfDocuments) {
            this.createValidationMessage(sdrf.getFile(),
                    Type.ERROR, "SDRF file does not reference "
                    + txt + " being validated.");
        }
    }

    private List<String> generateSdrfRefFileNames() {
        List<String> fileNames = new ArrayList<String>();
        for (SdrfDocument sdrf : sdrfDocuments) {
            fileNames.addAll(sdrf.getAllDataFiles());
        }
        return fileNames;
    }

    private void generateSdrfRefHybs() {
        for (SdrfDocument sdrf : sdrfDocuments) {
            hybridizations.addAll(sdrf.getAllHybridizations());
        }
    }

    private void generateSdrfRefSamples() {
        for (SdrfDocument sdrf : sdrfDocuments) {
            samples.addAll(sdrf.getAllSamples());
        }
    }

    /**
     * Exports the contents of the MAGE-TAB documents into files.
     */
    public void export() {
        export(idfDocuments);
        export(sdrfDocuments);
    }

    private void export(Set<? extends AbstractMageTabDocument> documents) {
        for (AbstractMageTabDocument document : documents) {
            document.export();
        }
    }

}
