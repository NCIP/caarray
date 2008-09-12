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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.file.FileManagementMDB;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.AbstractProbe;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.io.AffymetrixTsvFileReader;
import gov.nih.nci.caarray.util.io.AffymetrixTsvFileReader.Record;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * Loads array designs from Affymetrix PGF/CLF files.
 *
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD.TooManyMethods")
public class AffymetrixPgfClfDesignHandler extends AbstractAffymetrixArrayDesignHandler {

    private static final int TRANSACTION_SIZE = 5000;
    private static final Logger LOG = Logger.getLogger(AffymetrixPgfClfDesignHandler.class);

    // Shared headers, commented-out headers are unused at this time
    private static final String HEADER_CHIP_TYPE = "chip_type";
    private static final String HEADER_LIB_SET_NAME = "lib_set_name";
    private static final String HEADER_LIB_SET_VERSION = "lib_set_version";
//    private static final String HEADER_CREATE_DATE = "create_date";
//    private static final String HEADER_GUID = "guid";
    private static final String COL_PROBE_ID = "probe_id";

    // PGF-specific headers, commented-out headers are unused at this time
    private static final String HEADER_PGF_FORMAT_VERSION = "pgf_format_version";
//    private static final String COL_ROBESET_ID = "probeset_id";
//    private static final String COL_PROBESET_TYPE = "type";
    private static final String COL_PROBESET_NAME = "probeset_name";
    private static final String COL_ATOM_ID = "atom_id";
//    private static final String COL_ATOM_TYPE = "type";
//    private static final String COL_PROBE_EXON_POSITION = "exon_position";
//    private static final String COL_PROBE_TYPE = "type";
//    private static final String COL_PROBE_GC_COUNT = "gc_count";
//    private static final String COL_PROBE_LENGTH = "probe_length";
//    private static final String COL_PROBE_INTERROGATION_POS = "interrogation_position";
//    private static final String COL_PROBE_SEQUENCE = "probe_sequence";

    // CLF-specific headers, commented-out headers are unused at this time
    private static final String HEADER_CLF_FORMAT_VERSION = "clf_format_version";
    private static final String HEADER_ROWS = "rows";
    private static final String HEADER_COLS = "cols";
    private static final String HEADER_SEQUENTIAL = "sequential";
    private static final String HEADER_ORDER = "order";
//    private static final String COL_X = "x";
//    private static final String COL_Y = "y";

    private AffymetrixTsvFileReader pgfReader;
    private AffymetrixTsvFileReader clfReader;

    private final Map<String, Long> vendorIdDesignElementMap = new HashMap<String, Long>();

    AffymetrixPgfClfDesignHandler(VocabularyService vocabularyService, CaArrayDaoFactory daoFactory,
            Set<CaArrayFile> designFiles) {
        super(vocabularyService, daoFactory, designFiles);
        if (designFiles.size() != 2) {
            throw new IllegalArgumentException("Both a PGF file and a CLF file are required.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Logger getLog() {
        return LOG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void formatSpecificValidate(ValidationResult result) {
        checkLibSetNames(result);
        checkLibSetVersions(result);

        for (String designName : pgfReader.getFileHeader(HEADER_CHIP_TYPE)) {
            checkForDuplicateDesign(designName, result);
        }
    }

    private void checkLibSetVersions(ValidationResult result) {
        String pgfLibSetVersion = pgfReader.getFileHeaderAsString(HEADER_LIB_SET_VERSION);
        String clfLibSetVersion = clfReader.getFileHeaderAsString(HEADER_LIB_SET_VERSION);
        if (pgfLibSetVersion == null) {
            result.addMessage(pgfReader.getFile(), ValidationMessage.Type.ERROR,
                    "PGF file is missing the library set version header");
        } else if (clfLibSetVersion == null) {
            result.addMessage(clfReader.getFile(), ValidationMessage.Type.ERROR,
                    "CLF file is missing the library set version header");
        } else if (!pgfLibSetVersion.equals(clfLibSetVersion)) {
            result.addMessage(pgfReader.getFile(), ValidationMessage.Type.ERROR,
                    "PGF and CLF files must be from the same library set version (PGF is " + pgfLibSetVersion
                            + " and CLF is " + clfLibSetVersion + ")");
        }
    }

    private void checkLibSetNames(ValidationResult result) {
        String pgfLibSetName = pgfReader.getFileHeaderAsString(HEADER_LIB_SET_NAME);
        String clfLibSetName = clfReader.getFileHeaderAsString(HEADER_LIB_SET_NAME);
        if (pgfLibSetName == null) {
            result.addMessage(pgfReader.getFile(), ValidationMessage.Type.ERROR,
                    "PGF file is missing the library set name header");
        } else if (clfLibSetName == null) {
            result.addMessage(clfReader.getFile(), ValidationMessage.Type.ERROR,
                    "CLF file is missing the library set name header");
        } else if (!pgfLibSetName.equals(clfLibSetName)) {
            result.addMessage(pgfReader.getFile(), ValidationMessage.Type.ERROR,
                    "PGF and CLF files must be from the same library set (PGF is " + pgfLibSetName + " and CLF is "
                            + clfLibSetName + ")");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void closeFileReaders() {
        try {
            if (pgfReader != null) {
                pgfReader.close();
            }
            if (clfReader != null) {
                clfReader.close();
            }
        } catch (IOException e) {
            LOG.error("Unexpected failure closing PGF/CLF files", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getArrayDesignName() {
        if (pgfReader == null) {
            throw new IllegalStateException("Must load PGF reader before retrieving array design name.");
        }
        return pgfReader.getFileHeader(HEADER_CHIP_TYPE).get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getNumRows() {
        if (clfReader == null) {
            throw new IllegalStateException("Must load CLF Reader before retrieving row count.");
        }
        return Integer.parseInt(clfReader.getFileHeaderAsString(HEADER_ROWS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    int getNumCols() {
        if (clfReader == null) {
            throw new IllegalStateException("Must load CLF Reader before retrieving column count.");
        }
        return Integer.parseInt(clfReader.getFileHeaderAsString(HEADER_COLS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadFileReaders() throws AffymetrixArrayDesignReadException {
        Iterator<CaArrayFile> designFilesIterator = getDesignFiles().iterator();
        CaArrayFile designFile1 = designFilesIterator.next();
        CaArrayFile designFile2 = designFilesIterator.next();
        try {
            if (FileType.AFFYMETRIX_PGF == designFile1.getFileType()) {
                pgfReader = new AffymetrixTsvFileReader(getFile(designFile1));
                clfReader = new AffymetrixTsvFileReader(getFile(designFile2));
            } else {
                pgfReader = new AffymetrixTsvFileReader(getFile(designFile2));
                clfReader = new AffymetrixTsvFileReader(getFile(designFile1));
            }
            pgfReader.loadHeaders();
            clfReader.loadHeaders();
            validateFileHeaders();
        } catch (FileNotFoundException e) {
            throw new AffymetrixArrayDesignReadException("Could not find array design file", e);
        } catch (IOException e) {
            throw new AffymetrixArrayDesignReadException("Could load array design file reader", e);
        }
    }

    private void validateFileHeaders() throws AffymetrixArrayDesignReadException {
        String pgfVersion = pgfReader.getFileHeaderAsString(HEADER_PGF_FORMAT_VERSION);
        if (!"1.0".equals(pgfVersion)) {
            throw new AffymetrixArrayDesignReadException("PGF format version " + pgfVersion
                    + " is not supported for file " + pgfReader.getFile().getName());
        }
        String clfVersion = clfReader.getFileHeaderAsString(HEADER_CLF_FORMAT_VERSION);
        if (!"1.0".equals(clfVersion)) {
            throw new AffymetrixArrayDesignReadException("CLF format version " + clfVersion
                    + " is not supported for file " + clfReader.getFile().getName());
        }
        String clfSequential = clfReader.getFileHeaderAsString(HEADER_SEQUENTIAL);
        String clfOrder = clfReader.getFileHeaderAsString(HEADER_ORDER);
        if (NumberUtils.toInt(clfSequential) != 1 || !"col_major".equals(clfOrder)) {
            throw new AffymetrixArrayDesignReadException("CLF file " + clfReader.getFile().getName()
                    + " not supported: sequential=1 and order=col_major is required");
        }
    }

    void populateDesignDetails(ArrayDesignDetails designDetails) throws AffymetrixArrayDesignReadException {
        try {
            // 3 passes through PGF:
            //  1. Create features
            //  2. Create phys probes - use ID of first feature created + probe_id to figure out db id
            //  3. create logical probes - look up phys probe id via vendor id

            LOG.info("Creating array design features");
            handleProbes(designDetails);
            LOG.info("Done creating array design features, creating physical probes");
            flushAndCommitTransaction();
            handleAtoms(designDetails);
            LOG.info("Done creating array design physical probes, creating logical probes");
            flushAndCommitTransaction();
            pgfReader.reset();
            handleProbeSets(designDetails);
            LOG.info("Done creating logical probes");
            flushAndCommitTransaction();
            vendorIdDesignElementMap.clear();
        } catch (IOException e) {
            throw new AffymetrixArrayDesignReadException("Error loading design details from "
                    + pgfReader.getFile().getName(), e);
        }
    }

    private void flushAndCommitTransaction() {
        flushSession();
        FileManagementMDB currentMDB = FileManagementMDB.getCurrentMDB();
        if (currentMDB != null) {
            currentMDB.commitTransaction();
            currentMDB.beginTransaction();
        } else {
            HibernateUtil.getCurrentSession().getTransaction().commit();
            HibernateUtil.getCurrentSession().beginTransaction();
        }
    }

    void handleProbes(ArrayDesignDetails designDetails) throws AffymetrixArrayDesignReadException {
        // we can create all the features without parsing either file.  even if we needed to store the
        // feature ID (the probe_id from the files), we could calculate that based on the (x, y) coordinates
        // of the feature without parsing the CLF file, since we only process CLF files with order=col_major.
        int cols = getNumCols();
        int rows = getNumRows();
        getArrayDao().createFeatures(rows, cols, designDetails);
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    void handleAtoms(ArrayDesignDetails designDetails) throws AffymetrixArrayDesignReadException {
        Long firstFeatureId = getArrayDao().getFirstFeatureId(designDetails);
        int counter = 0;
        int features = 0;
        try {
            Record nextLine = pgfReader.readNextDataLine();
            int recordLevel = getRecordLevel(nextLine);
            while (nextLine != null) {
                if (recordLevel == 1) {
                    PhysicalProbe physicalProbe = new PhysicalProbe(designDetails, getProbeGroup());
                    String vendorId = nextLine.get(COL_ATOM_ID);
                    nextLine = pgfReader.readNextDataLine();
                    recordLevel = getRecordLevel(nextLine);
                    while (recordLevel == 2) {
                        linkFeatureToPhysicalProbe(nextLine.get(COL_PROBE_ID), firstFeatureId, physicalProbe);
                        nextLine = pgfReader.readNextDataLine();
                        recordLevel = getRecordLevel(nextLine);
                        features++;
                    }
                    getArrayDao().save(physicalProbe);
                    vendorIdDesignElementMap.put(vendorId, physicalProbe.getId());
                    manageDesignElementSession(physicalProbe, counter);
                    counter++;
                } else {
                    nextLine = pgfReader.readNextDataLine();
                    recordLevel = getRecordLevel(nextLine);
                }
            }
            LOG.debug("Linked " + features + " features to " + counter + " phys probes");
        } catch (IOException e) {
            throw new AffymetrixArrayDesignReadException("Error reading array design from file "
                    + pgfReader.getFile().getName(), e);
        } catch (NumberFormatException e) {
            throw new AffymetrixArrayDesignReadException("Non-numeric probe_id in " + pgfReader.getFile().getName(), e);
        }
    }

    private int getRecordLevel(Record nextLine) throws IOException {
        return nextLine != null ? nextLine.getRecordLevel() : -1;
    }

    private void linkFeatureToPhysicalProbe(String probeId, Long firstFeatureId, PhysicalProbe physicalProbe) {
        int probeIdInt = Integer.valueOf(probeId);
        long probeDbId = probeIdInt - clfReader.getFileHeaderAsInteger(HEADER_SEQUENTIAL) + firstFeatureId;
        Feature feature = getSearchDao().retrieveUnsecured(Feature.class, probeDbId);
        physicalProbe.addFeature(feature);
    }

    void handleProbeSets(ArrayDesignDetails designDetails) throws AffymetrixArrayDesignReadException {
        try {
            Record nextLine = pgfReader.readNextDataLine();
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
                nextLine = pgfReader.readNextDataLine();
                recordLevel = getRecordLevel(nextLine);
            }
            saveLogicalProbe(logicalProbe);
        } catch (IOException e) {
            throw new AffymetrixArrayDesignReadException("Error reading array design from file "
                    + pgfReader.getFile().getName(), e);
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
        PhysicalProbe physicalProbe = getSearchDao().retrieveUnsecured(PhysicalProbe.class,
                vendorIdDesignElementMap.get(atomId));
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
    void createProbeSetDesignElementList(ArrayDesign arrayDesign) throws AffymetrixArrayDesignReadException {
        ArrayDesign refreshedArrayDesign = getRefreshedDesign(arrayDesign);
        AffymetrixChpPgfClfDesignElementListUtility.createDesignElementList(refreshedArrayDesign, getArrayDao());
        LOG.info("Committing design element list for " + refreshedArrayDesign.getName());
        flushAndCommitTransaction();
        try {
            for (String designName : pgfReader.getFileHeader(HEADER_CHIP_TYPE)) {
                if (refreshedArrayDesign.getName().equals(designName)) {
                    continue;
                }

                refreshedArrayDesign = getRefreshedDesign(arrayDesign);
                ArrayDesign newDesign = new ArrayDesign(refreshedArrayDesign);
                newDesign.setName(designName);
                newDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + designName);
                getArrayDao().save(newDesign);
                AffymetrixChpPgfClfDesignElementListUtility.createDesignElementList(newDesign, getArrayDao());
                LOG.info("Committing design element list for " + designName);
                flushAndCommitTransaction();
            }
        } catch (Exception e) {
            throw new AffymetrixArrayDesignReadException("Exception creating array design aliases"
                    + " for pgf/clf array design: " + arrayDesign.getName(), e);
        }
        flushAndClearSession();
    }

}
