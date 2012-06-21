/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.array.AbstractProbe;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.plugins.affymetrix.AffymetrixTsvFileReader.Record;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Loads array designs from Affymetrix PGF/CLF files.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class PgfClfDesignHandler extends AbstractAffymetrixDesignFileHandler {
    private static final int TRANSACTION_SIZE = 5000;
    private static final Logger LOG = Logger.getLogger(PgfClfDesignHandler.class);

    /**
     * FileType instance for PGF file type.
     */
    public static final FileType PGF_FILE_TYPE = new FileType("AFFYMETRIX_PGF", FileCategory.ARRAY_DESIGN, true, "PGF");
    /**
     * FileType instance for CLF file type.
     */
    public static final FileType CLF_FILE_TYPE = new FileType("AFFYMETRIX_CLF", FileCategory.ARRAY_DESIGN, true, "CLF");
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(PGF_FILE_TYPE, CLF_FILE_TYPE);

    // Shared headers, commented-out headers are unused at this time
    private static final String HEADER_CHIP_TYPE = "chip_type";
    private static final String HEADER_LIB_SET_NAME = "lib_set_name";
    private static final String HEADER_LIB_SET_VERSION = "lib_set_version";
    // private static final String HEADER_CREATE_DATE = "create_date";
    // private static final String HEADER_GUID = "guid";
    private static final String COL_PROBE_ID = "probe_id";

    // PGF-specific headers, commented-out headers are unused at this time
    private static final String HEADER_PGF_FORMAT_VERSION = "pgf_format_version";
    // private static final String COL_ROBESET_ID = "probeset_id";
    // private static final String COL_PROBESET_TYPE = "type";
    private static final String COL_PROBESET_NAME = "probeset_name";
    private static final String COL_ATOM_ID = "atom_id";
    // private static final String COL_ATOM_TYPE = "type";
    // private static final String COL_PROBE_EXON_POSITION = "exon_position";
    // private static final String COL_PROBE_TYPE = "type";
    // private static final String COL_PROBE_GC_COUNT = "gc_count";
    // private static final String COL_PROBE_LENGTH = "probe_length";
    // private static final String COL_PROBE_INTERROGATION_POS = "interrogation_position";
    // private static final String COL_PROBE_SEQUENCE = "probe_sequence";

    // CLF-specific headers, commented-out headers are unused at this time
    private static final String HEADER_CLF_FORMAT_VERSION = "clf_format_version";
    private static final String HEADER_ROWS = "rows";
    private static final String HEADER_COLS = "cols";
    private static final String HEADER_SEQUENTIAL = "sequential";
    private static final String HEADER_ORDER = "order";
    // private static final String COL_X = "x";
    // private static final String COL_Y = "y";

    private CaArrayFile pgfFile, clfFile;
    private AffymetrixTsvFileReader pgfReader, clfReader;
    private final CaArrayHibernateHelper hibernateHelper;

    private final Map<String, Long> vendorIdDesignElementMap = new HashMap<String, Long>();

    @Inject
    @SuppressWarnings("PMD.ExcessiveParameterList")
    PgfClfDesignHandler(SessionTransactionManager sessionTransactionManager, DataStorageFacade dataStorageFacade,
            ArrayDao arrayDao, SearchDao searchDao,
            @Named("pgfClf") AbstractChpDesignElementListUtility designElementListUtility,
            CaArrayHibernateHelper hibernateHelper) {
        super(sessionTransactionManager, dataStorageFacade, arrayDao, searchDao, designElementListUtility);
        this.hibernateHelper = hibernateHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException {
        if (designFiles.size() != 2) {
            return false;
        }

        for (final CaArrayFile designFile : designFiles) {
            if (PGF_FILE_TYPE.equals(designFile.getFileType())) {
                this.pgfFile = designFile;
            } else if (CLF_FILE_TYPE.equals(designFile.getFileType())) {
                this.clfFile = designFile;
            } else {
                return false;
            }
        }

        if (this.pgfFile == null || this.clfFile == null) {
            return false;
        }

        this.pgfReader = initializeReader(this.pgfFile);
        this.clfReader = initializeReader(this.clfFile);

        validateFileHeaders();

        return true;
    }

    private AffymetrixTsvFileReader initializeReader(CaArrayFile designFile) throws PlatformFileReadException {
        final File fileOnDisk = getDataStorageFacade().openFile(designFile.getDataHandle(), false);
        try {
            final AffymetrixTsvFileReader reader = new AffymetrixTsvFileReader(fileOnDisk);
            reader.loadHeaders();
            return reader;
        } catch (final FileNotFoundException e) {
            throw new PlatformFileReadException(fileOnDisk, "Could not find array design file", e);
        } catch (final IOException e) {
            throw new PlatformFileReadException(fileOnDisk, "Could load array design file reader", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        try {
            if (this.pgfReader != null) {
                this.pgfReader.close();
                this.pgfReader = null;
            }
            if (this.clfReader != null) {
                this.clfReader.close();
                this.clfReader = null;
            }
        } catch (final IOException e) {
            LOG.error("Unexpected failure closing PGF/CLF files", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        final FileValidationResult pgfResult = new FileValidationResult();
        result.addFile(this.pgfFile.getName(), pgfResult);
        this.pgfFile.setValidationResult(pgfResult);

        final FileValidationResult clfResult = new FileValidationResult();
        result.addFile(this.clfFile.getName(), clfResult);
        this.clfFile.setValidationResult(clfResult);

        checkForDuplicateDesign(getArrayDesignName(), pgfResult);
        checkLibSetNames(result);
        checkLibSetVersions(result);
    }

    private void checkLibSetVersions(ValidationResult result) {
        final String pgfLibSetVersion = this.pgfReader.getFileHeaderAsString(HEADER_LIB_SET_VERSION);
        final String clfLibSetVersion = this.clfReader.getFileHeaderAsString(HEADER_LIB_SET_VERSION);
        if (pgfLibSetVersion == null) {
            result.addMessage(this.pgfFile.getName(), ValidationMessage.Type.ERROR,
                    "PGF file is missing the library set version header");
        } else if (clfLibSetVersion == null) {
            result.addMessage(this.clfFile.getName(), ValidationMessage.Type.ERROR,
                    "CLF file is missing the library set version header");
        } else if (!pgfLibSetVersion.equals(clfLibSetVersion)) {
            result.addMessage(this.pgfFile.getName(), ValidationMessage.Type.ERROR,
                    "PGF and CLF files must be from the same library set version (PGF is " + pgfLibSetVersion
                            + " and CLF is " + clfLibSetVersion + ")");
        }
    }

    private void checkLibSetNames(ValidationResult result) {
        final String pgfLibSetName = this.pgfReader.getFileHeaderAsString(HEADER_LIB_SET_NAME);
        final String clfLibSetName = this.clfReader.getFileHeaderAsString(HEADER_LIB_SET_NAME);
        if (pgfLibSetName == null) {
            result.addMessage(this.pgfFile.getName(), ValidationMessage.Type.ERROR,
                    "PGF file is missing the library set name header");
        } else if (clfLibSetName == null) {
            result.addMessage(this.clfFile.getName(), ValidationMessage.Type.ERROR,
                    "CLF file is missing the library set name header");
        } else if (!pgfLibSetName.equals(clfLibSetName)) {
            result.addMessage(this.pgfFile.getName(), ValidationMessage.Type.ERROR,
                    "PGF and CLF files must be from the same library set (PGF is " + pgfLibSetName + " and CLF is "
                            + clfLibSetName + ")");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getArrayDesignName() {
        if (this.pgfReader == null) {
            throw new IllegalStateException("Must load PGF reader before retrieving array design name.");
        }
        return this.pgfReader.getFileHeader(HEADER_CHIP_TYPE).get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNumRows() {
        if (this.clfReader == null) {
            throw new IllegalStateException("Must load CLF Reader before retrieving row count.");
        }
        return Integer.parseInt(this.clfReader.getFileHeaderAsString(HEADER_ROWS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getNumCols() {
        if (this.clfReader == null) {
            throw new IllegalStateException("Must load CLF Reader before retrieving column count.");
        }
        return Integer.parseInt(this.clfReader.getFileHeaderAsString(HEADER_COLS));
    }

    private void validateFileHeaders() throws PlatformFileReadException {
        final String pgfVersion = this.pgfReader.getFileHeaderAsString(HEADER_PGF_FORMAT_VERSION);
        if (!"1.0".equals(pgfVersion)) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "PGF format version " + pgfVersion
                    + " is not supported for file " + this.pgfReader.getFile().getName());
        }
        final String clfVersion = this.clfReader.getFileHeaderAsString(HEADER_CLF_FORMAT_VERSION);
        if (!"1.0".equals(clfVersion)) {
            throw new PlatformFileReadException(this.clfReader.getFile(), "CLF format version " + clfVersion
                    + " is not supported for file " + this.clfReader.getFile().getName());
        }
        final String clfSequential = this.clfReader.getFileHeaderAsString(HEADER_SEQUENTIAL);
        final String clfOrder = this.clfReader.getFileHeaderAsString(HEADER_ORDER);
        if (NumberUtils.toInt(clfSequential) != 1 || !"col_major".equals(clfOrder)) {
            throw new PlatformFileReadException(this.clfReader.getFile(), "CLF file "
                    + this.clfReader.getFile().getName()
                    + " not supported: sequential=1 and order=col_major is required");
        }
    }

    @Override
    void populateDesignDetails(ArrayDesignDetails designDetails) throws PlatformFileReadException {
        try {
            // 3 passes through PGF:
            // 1. Create features
            // 2. Create phys probes - use ID of first feature created + probe_id to figure out db id
            // 3. create logical probes - look up phys probe id via vendor id

            LOG.info("Creating array design features");
            handleProbes(designDetails);
            LOG.info("Done creating array design features, creating physical probes");
            flushAndCommitTransaction();
            handleAtoms(designDetails);
            LOG.info("Done creating array design physical probes, creating logical probes");
            flushAndCommitTransaction();
            this.pgfReader.reset();
            handleProbeSets(designDetails);
            LOG.info("Done creating logical probes");
            flushAndCommitTransaction();
            this.vendorIdDesignElementMap.clear();
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "Error loading design details from "
                    + this.pgfReader.getFile().getName(), e);
        }
    }

    private void flushAndCommitTransaction() {
        flushSession();
        final SessionTransactionManager stm = getSessionTransactionManager();
        stm.commitTransaction();
        stm.beginTransaction();
    }

    void handleProbes(ArrayDesignDetails designDetails) throws PlatformFileReadException {
        // we can create all the features without parsing either file. even if we needed to store the
        // feature ID (the probe_id from the files), we could calculate that based on the (x, y) coordinates
        // of the feature without parsing the CLF file, since we only process CLF files with order=col_major.
        final int cols = getNumCols();
        final int rows = getNumRows();
        getArrayDao().createFeatures(rows, cols, designDetails);
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    void handleAtoms(ArrayDesignDetails designDetails) throws PlatformFileReadException {
        final Long firstFeatureId = getArrayDao().getFirstFeatureId(designDetails);
        int counter = 0;
        int features = 0;
        try {
            Record nextLine = this.pgfReader.readNextDataLine();
            int recordLevel = getRecordLevel(nextLine);
            while (nextLine != null) {
                if (recordLevel == 1) {
                    final PhysicalProbe physicalProbe = new PhysicalProbe(designDetails, getProbeGroup());
                    final String vendorId = nextLine.get(COL_ATOM_ID);
                    nextLine = this.pgfReader.readNextDataLine();
                    recordLevel = getRecordLevel(nextLine);
                    while (recordLevel == 2) {
                        linkFeatureToPhysicalProbe(nextLine.get(COL_PROBE_ID), firstFeatureId, physicalProbe);
                        nextLine = this.pgfReader.readNextDataLine();
                        recordLevel = getRecordLevel(nextLine);
                        features++;
                    }
                    getArrayDao().save(physicalProbe);
                    this.vendorIdDesignElementMap.put(vendorId, physicalProbe.getId());
                    manageDesignElementSession(physicalProbe, counter);
                    counter++;
                } else {
                    nextLine = this.pgfReader.readNextDataLine();
                    recordLevel = getRecordLevel(nextLine);
                }
            }
            LOG.debug("Linked " + features + " features to " + counter + " phys probes");
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "Error reading array design from file "
                    + this.pgfReader.getFile().getName(), e);
        } catch (final NumberFormatException e) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "Non-numeric probe_id in "
                    + this.pgfReader.getFile().getName(), e);
        }
    }

    private int getRecordLevel(Record nextLine) throws IOException {
        return nextLine != null ? nextLine.getRecordLevel() : -1;
    }

    private void linkFeatureToPhysicalProbe(String probeId, Long firstFeatureId, PhysicalProbe physicalProbe) {
        final int probeIdInt = Integer.valueOf(probeId);
        final long probeDbId = probeIdInt - this.clfReader.getFileHeaderAsInteger(HEADER_SEQUENTIAL) + firstFeatureId;
        final Feature feature = getSearchDao().retrieveUnsecured(Feature.class, probeDbId);
        physicalProbe.addFeature(feature);
    }

    void handleProbeSets(ArrayDesignDetails designDetails) throws PlatformFileReadException {
        try {
            Record nextLine = this.pgfReader.readNextDataLine();
            int recordLevel = getRecordLevel(nextLine);
            LogicalProbe logicalProbe = null;
            int counter = 0;
            while (nextLine != null) {
                if (recordLevel == 0) {
                    saveLogicalProbe(logicalProbe);
                    manageDesignElementSession(logicalProbe, counter);
                    logicalProbe = new LogicalProbe(designDetails);
                    logicalProbe.setName(nextLine.get(COL_PROBESET_NAME));
                    counter++;
                } else if (recordLevel == 1) {
                    linkPhysicalProbeToLogicalProbe(nextLine.get(COL_ATOM_ID), logicalProbe);
                }
                nextLine = this.pgfReader.readNextDataLine();
                recordLevel = getRecordLevel(nextLine);
            }
            saveLogicalProbe(logicalProbe);
        } catch (final IOException e) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "Error reading array design from file "
                    + this.pgfReader.getFile().getName(), e);
        }
    }

    private void manageDesignElementSession(AbstractProbe probe, int counter) {
        if (counter % PROBE_SET_BATCH_SIZE == 0 && probe != null) {
            LOG.debug("Flushing session (" + counter + " " + probe.getClass().getName() + " probes)");
            flushSession();
        }
        if (counter % TRANSACTION_SIZE == 0 && probe != null) {
            LOG.info("Committing transaction (" + counter + " " + probe.getClass().getName() + " probes)");
            flushAndCommitTransaction();
        }
    }

    private void saveLogicalProbe(LogicalProbe logicalProbe) {
        if (logicalProbe != null) {
            getArrayDao().save(logicalProbe);
        }
    }

    private void linkPhysicalProbeToLogicalProbe(String atomId, LogicalProbe logicalProbe) {
        LOG.debug("Linking physical probe atom #" + atomId + " to logical probe " + logicalProbe.getId());
        final PhysicalProbe physicalProbe =
                getSearchDao().retrieveUnsecured(PhysicalProbe.class, this.vendorIdDesignElementMap.get(atomId));
        logicalProbe.addProbe(physicalProbe);
    }

    private void flushSession() {
        flushAndClearSession();
        setProbeGroup(getSearchDao().retrieve(ProbeGroup.class, getProbeGroup().getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws PlatformFileReadException {
        ArrayDesign refreshedArrayDesign = getRefreshedDesign(arrayDesign);
        getDesignElementListUtility().createDesignElementList(refreshedArrayDesign);
        LOG.info("Committing design element list for " + refreshedArrayDesign.getName());
        flushAndCommitTransaction();
        try {
            for (final String designName : this.pgfReader.getFileHeader(HEADER_CHIP_TYPE)) {
                if (refreshedArrayDesign.getName().equals(designName)) {
                    continue;
                }

                refreshedArrayDesign = getRefreshedDesign(arrayDesign);
                final ArrayDesign newDesign = new ArrayDesign(refreshedArrayDesign);
                newDesign.setName(designName);
                newDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + designName);
                getArrayDao().save(newDesign);
                getDesignElementListUtility().createDesignElementList(newDesign);
                LOG.info("Committing design element list for " + designName);
                flushAndCommitTransaction();
            }
        } catch (final Exception e) {
            throw new PlatformFileReadException(this.pgfReader.getFile(), "Exception creating array design aliases"
                    + " for pgf/clf array design: " + arrayDesign.getName(), e);
        }
        flushAndClearSession();
    }
}
