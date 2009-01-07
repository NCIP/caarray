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
@SuppressWarnings("PMD.TooManyMethods")
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

    private final boolean reimportingMagetab;

    /**
     * Initialize the MAGE-TAB document set with the given files which will hold the exported contents.
     *
     * @param inputFileSet the set of files to hold the exported contents of the documents.
     */
    public MageTabDocumentSet(MageTabFileSet inputFileSet) {
        initializeFromFileSet(inputFileSet);
        reimportingMagetab = false;
    }

    /**
     * Initialize the MAGE-TAB document set with the given files which will hold the exported contents.
     *
     * @param inputFileSet the set of files to hold the exported contents of the documents.
     * @param reimportingMagetab true if this document set is being used to re-import additional MAGE-TAB documents,
     *        false if this document set contains the initial MAGE-TAB files for an experiment
     */
    public MageTabDocumentSet(MageTabFileSet inputFileSet, boolean reimportingMagetab) {
        initializeFromFileSet(inputFileSet);
        this.reimportingMagetab = reimportingMagetab;
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

        generateSdrfRefHybs();
        generateSdrfRefSamples();
    }
    /**
     * Special parse method that skips adding validation results to files and does not generate
     * hybs or samples. If More than 1 idf doc is encountered a runtime exception is thrown.
     * @throws MageTabParsingException
     */
    void parseNoValidation() throws MageTabParsingException {
        if (idfDocuments.size() > 1) {
            throw new IllegalArgumentException("Only one IDF document can be present when parsing for data files.");
        }

        AbstractMageTabDocument idfDoc = idfDocuments.iterator().next();
        idfDoc.parse(false);
        IdfDocument idf = (IdfDocument) idfDoc;
        for (SdrfDocument document : idf.getSdrfDocuments()) {
                document.parseNoIdfCheck();
        }

    }

    private void parse(Set<? extends AbstractMageTabDocument> documents) throws MageTabParsingException {
        for (AbstractMageTabDocument document : documents) {
            document.parse(reimportingMagetab);
        }
    }

    /**
     * @return the list of names of all raw data files referenced by any
     * SDRF in this document set.
     */
    public List<String> getSdrfReferencedRawFileNames() {
        List<String> fileNames = new ArrayList<String>();
        for (SdrfDocument sdrf : sdrfDocuments) {
            fileNames.addAll(sdrf.getReferencedRawFileNames());
        }
        return fileNames;
    }

    /**
     * @return the list of names of all derived data files referenced by any
     * SDRF in this document set.
     */
    public List<String> getSdrfReferencedDerivedFileNames() {
        List<String> fileNames = new ArrayList<String>();
        for (SdrfDocument sdrf : sdrfDocuments) {
            fileNames.addAll(sdrf.getReferencedDerivedFileNames());
        }
        return fileNames;
    }

    /**
     * @return the list of names of all data matrix files referenced by any
     * SDRF in this document set.
     */
    public List<String> getSdrfReferencedDataMatrixFileNames() {
        List<String> fileNames = new ArrayList<String>();
        for (SdrfDocument sdrf : sdrfDocuments) {
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
     * Return a set of the hybridizations referenced by the sdrf files.
     * @return set of Hybridization
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

    /**
     * @return the reimportingMagetab
     */
    public boolean isReimportingMagetab() {
        return reimportingMagetab;
    }

}
