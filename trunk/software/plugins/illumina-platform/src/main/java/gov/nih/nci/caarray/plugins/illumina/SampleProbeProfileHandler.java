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

package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DefaultValueParser;
import gov.nih.nci.caarray.platforms.ValueParser;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Illumina Sample Probe Profile file loader.
 * 
 * @since 2.4.0
 * @author gax
 */
@SuppressWarnings("PMD.ExcessiveClassLength")
public final class SampleProbeProfileHandler extends AbstractDataFileHandler {
    private static final Logger LOG = Logger.getLogger(SampleProbeProfileHandler.class);

    /**
     * File Type for SAMPLE_PROBE data files.
     */
    public static final FileType SAMPLE_PROBE_PROFILE_FILE_TYPE = new FileType("ILLUMINA_SAMPLE_PROBE_PROFILE_TXT",
            FileCategory.DERIVED_DATA, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(SAMPLE_PROBE_PROFILE_FILE_TYPE);

    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * 
     */
    @Inject
    SampleProbeProfileHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
        super(dataStorageFacade);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
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
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        final DataHeaderParser p = new DataHeaderParser(null);
        processFile(getFile(), p);
        // assume all hybs have the same QTypes as the first.
        final List<SampleProbeProfileQuantitationType> l = p.getLoaders().get(0).getQTypes();
        return l.toArray(new SampleProbeProfileQuantitationType[l.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        final InfoHeadingParser p = new InfoHeadingParser();
        processFile(getFile(), p);
        String fileName = p.getValues().get(InfoHeadingParser.KEY_ARRAY_CONTENT);
        if (fileName == null) {
            throw new IllegalStateException("Missing header field '" + InfoHeadingParser.KEY_ARRAY_CONTENT + "'");
        }
        final List<LSID> lsids = new ArrayList<LSID>();
        while (!StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
            lsids.add(new LSID(IlluminaCsvDesignHandler.LSID_AUTHORITY, IlluminaCsvDesignHandler.LSID_NAMESPACE,
                    fileName));
            fileName = FilenameUtils.getBaseName(fileName);
        }
        lsids.add(new LSID(IlluminaCsvDesignHandler.LSID_AUTHORITY, IlluminaCsvDesignHandler.LSID_NAMESPACE, fileName));
        return lsids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        DataHeaderParser header = new DataHeaderParser(design.getFirstDesignFile().getFileType());
        final DesignElementBuilderParser designElementBuilder =
            new DesignElementBuilderParser(header, dataSet, design, this.arrayDao, this.searchDao);
        processFile(getFile(), null, header, designElementBuilder);
        designElementBuilder.finish();
        dataSet.prepareColumns(types, designElementBuilder.getElementCount());
        LOG.info("Pass 1/2 loaded " + designElementBuilder.getElementCount() + " design elements.");
        header = new DataHeaderParser(design.getFirstDesignFile().getFileType());
        final HybDataBuilder<SampleProbeProfileQuantitationType> loader =
            new HybDataBuilder<SampleProbeProfileQuantitationType>(dataSet, header, this.valueParser);
        processFile(getFile(), null, header, loader);
        LOG.info("Pass 2/2 loaded data.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        final InfoHeadingParser p = new InfoHeadingParser();
        processFile(getFile(), p);
        final String magic = p.getValues().get(InfoHeadingParser.MAGIC);
        if (magic == null || !magic.startsWith("Illumina Inc. BeadStudio")) {
            LOG.warn("...Not a familiar file format " + magic);
        }

        if (design == null) {
            result.addMessage(Type.ERROR, "Array design not found");
            return;
        }

        final ValidatingHeaderParser headerValidator =
            new ValidatingHeaderParser(design.getFirstDesignFile().getFileType(), result, mTabSet);
        final HybDataValidator<SampleProbeProfileQuantitationType> dataValidator =
            new HybDataValidator<SampleProbeProfileQuantitationType>(headerValidator, result, design,
                    this.arrayDao);
        processFile(getFile(), null, headerValidator, dataValidator);
        dataValidator.finish();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHybridizationNames() {
        final DataHeaderParser proc = new DataHeaderParser(null);
        processFile(getFile(), proc);
        return proc.getHybNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return IlluminaArrayDataTypes.ILLUMINA_SAMPLE_PROBE_PROFILE;
    }

    private static DelimitedFileReader openReader(File dataFile) {
        try {
            return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(dataFile);
        } catch (final IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }
    }

    private void processFile(File file, InfoHeadingParser info) {
        processFile(file, info, null, null);
    }

    private void processFile(File file, AbstractHeaderParser header) {
        processFile(file, null, header, null);
    }

    private void processFile(File file, InfoHeadingParser info, AbstractHeaderParser header, AbstractParser row) {
        final DelimitedFileReader r = openReader(file);
        try {
            parseHeadingSection(r, info);
            parseDataSection(r, header, row);
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            r.close();

        }
    }

    private static void skeepBlankLines(DelimitedFileReader r) throws IOException {
        while (r.hasNextLine() && isBlankLine(r.peek())) {
            r.nextLine();
        }
    }

    private static boolean isBlankLine(List<String> line) {
        return line.isEmpty() || (line.size() == 1 && line.get(0).length() == 0);
    }

    private void parseHeadingSection(DelimitedFileReader r, InfoHeadingParser info) throws IOException {
        skeepBlankLines(r);
        while (r.hasNextLine()) {
            List<String> line = r.peek();
            if (isBlankLine(line)) {
                // end of heading section
                r.nextLine();
                break;
            } else if (line.size() > 1) {
                // probably the table header. there was no newline to mark the end of the heading section.
                break;
            } else {
                line = r.nextLine();
                if (info != null) {
                    info.parse(line, r.getCurrentLineNumber());
                }
            }
        }
    }

    private void parseDataSection(DelimitedFileReader r, AbstractHeaderParser header, AbstractParser row)
    throws IOException {
        // parse table header
        if (r.hasNextLine() && header != null) {
            final List<String> line = r.nextLine();
            boolean keepGoing = header.parse(line, r.getCurrentLineNumber());
            // parse rows
            long ticker = System.currentTimeMillis();
            while (keepGoing && r.hasNextLine() && row != null) {
                keepGoing = row.parse(r.nextLine(), r.getCurrentLineNumber());
                ticker = GenotypingProcessedMatrixHandler.tick(ticker, r.getCurrentLineNumber(), row);
            }
        }
    }

    /**
     * file heading parser.
     */
    private static class InfoHeadingParser {
        private static final String MAGIC = "MAGIC";
        private static final String KEY_ARRAY_CONTENT = "ARRAY CONTENT";
        private final Map<String, String> values = new HashMap<String, String>();

        void parse(List<String> line, int lineNum) {
            final String[] pair = line.get(0).split("=");
            if (pair.length == 1 && lineNum == 1) {
                this.values.put(MAGIC, line.get(0));
            } else if (pair.length == 2) {
                final String n = pair[0].trim().toUpperCase(Locale.getDefault());
                final String v = pair[1].trim();
                this.values.put(n, v);
            }
        }

        public Map<String, String> getValues() {
            return this.values;
        }
    }

    /**
     * matrix header parser with extra validation.
     */
    static class ValidatingHeaderParser extends DataHeaderParser {
        private final MageTabDocumentSet mTabSet;

        ValidatingHeaderParser(FileType designType, FileValidationResult result, MageTabDocumentSet mTabSet) {
            super(designType, result);
            this.mTabSet = mTabSet;
        }

        @Override
        protected boolean parseLoaders(List<String> line, int lineNum) {
            final boolean ok = super.parseLoaders(line, lineNum);
            if (ok) {
                if (mTabSet != null && !mTabSet.hasPartialSdrf()) {
                    validateSdrfNames(mTabSet, lineNum);
                }
                validateColumnConsistency(lineNum);
            }
            return ok;
        }
    }

    /**
     * matrix header parser.
     */
    static class DataHeaderParser extends AbstractHeaderParser<SampleProbeProfileQuantitationType> {
        private ColNameFormat format;
        private final int[] rowHeaderindexes = new int[RowHeader.values().length];
        private final FileType designType;

        public DataHeaderParser(FileType designType) {
            super(SampleProbeProfileQuantitationType.class);
            this.designType = designType;
            Arrays.fill(this.rowHeaderindexes, -1);
        }

        protected DataHeaderParser(FileType designType, FileValidationResult result) {
            super(new MessageHandler.ValidationMessageHander(result), SampleProbeProfileQuantitationType.class);
            this.designType = designType;
            Arrays.fill(this.rowHeaderindexes, -1);
        }

        @Override
        protected boolean parseLoaders(List<String> line, int lineNum) {
            final List<String> hybNames = findHybNames(line, lineNum);
            if (hybNames.isEmpty()) {
                error("No samples found", lineNum, 0);
                return false;
            }
            // start out with all columns, and remove the ones that are processed/used.
            final List<String> unusedCols = new ArrayList<String>(line);
            for (final String hybName : hybNames) {
                final ValueLoader hyb = addValueLoader(hybName);
                loadQTypeMap(hyb, line, lineNum, unusedCols);
            }

            loadCommonColMap(line, lineNum, unusedCols);
            final RowHeader probeIdCol = findProbeIdColumn(lineNum);
            if (probeIdCol == null) {
                return false;
            } else {
                setProbIdColumn(this.rowHeaderindexes[probeIdCol.ordinal()]);
            }

            for (final String col : unusedCols) {
                warn("Ignored column " + col, lineNum, line.indexOf(col) + 1);
            }
            return true;
        }

        /**
         * @param unusedCols used/processed columns should be removed from this list.
         */
        void loadQTypeMap(ValueLoader hyb, List<String> line, int lineNum, List<String> unusedCols) {
            final EnumSet<SampleProbeProfileQuantitationType> mandatory =
                EnumSet.of(SampleProbeProfileQuantitationType.DETECTION,
                        SampleProbeProfileQuantitationType.AVG_SIGNAL); // always
            // present,
            // but
            // ckeck
            // anyway.
            for (int i = 0; i < line.size(); i++) {
                final String compositeName = line.get(i);
                String localName = this.format.getLocalColName(compositeName, hyb.getHybName());
                if (localName == null) {
                    continue;
                }
                localName = localName.toUpperCase(Locale.getDefault());
                final HybHeader hdr = HybHeader.forAltName(localName);
                if (hdr != null) {
                    hyb.addMapping(hdr.getQType(), i, lineNum);
                    mandatory.remove(hdr.getQType());
                    unusedCols.remove(line.get(i));
                } else {
                    warn("Unsupported column " + compositeName, lineNum, i + 1);
                }
            }
            if (!mandatory.isEmpty()) {
                error("Missing quantitation type(s) " + mandatory.toString() + " for sample " + hyb.getHybName(),
                        lineNum, 0);
            }
        }

        /**
         * find columns that define the "AVG_SIGNAL" QType, and extract the hyb name from the full column name. Note
         * that if a hyb is missing the mandatory "AVG_SIGNAL" QType, it will be ignored w/o warning.
         */
        @SuppressWarnings("PMD.CompareObjectsWithEquals")
        private List<String> findHybNames(List<String> line, int lineNum) {
            final List<String> l = new ArrayList<String>();
            int col = 0;
            for (final String s : line) {
                col++;
                final ColNameFormat tmp = findFormatIfAVGSignalCol(s);
                if (tmp == null) {
                    continue;
                } else {
                    l.add(getHybNameFromAVGSignalCol(s, tmp));
                }
                if (this.format == null) {
                    this.format = tmp;
                } else if (this.format != tmp) { // PMD suppressed
                    error("Mixed column name formats", lineNum, col);
                }
            }
            if (l.isEmpty()) {
                error("No samples with quantitation type " + SampleProbeProfileQuantitationType.AVG_SIGNAL + " found",
                        lineNum, 0);
            }
            return l;
        }

        /**
         * @param unusedCols used/processed columns should be removed from this list.
         */
        private void loadCommonColMap(List<String> line, int lineNum, List<String> unusedCols) {
            for (int i = 0; i < line.size(); i++) {
                try {
                    final RowHeader h = RowHeader.valueOf(line.get(i).toUpperCase(Locale.getDefault()));
                    if (isPresent(h)) {
                        warn("Column " + h + " already defined at column " + (this.rowHeaderindexes[h.ordinal()] + 1),
                                lineNum, i + 1);
                    } else {
                        this.rowHeaderindexes[h.ordinal()] = i;
                        unusedCols.remove(line.get(i));
                    }
                } catch (final IllegalArgumentException e) {
                    continue;
                }
            }

        }

        private static ColNameFormat findFormatIfAVGSignalCol(String compisteName) {
            final String upper = compisteName.toUpperCase(Locale.getDefault());
            if (upper.endsWith(".AVG_SIGNAL")) {
                return DOT_FORMAT;
            } else if (upper.startsWith("AVG_SIGNAL-")) {
                return DASH_FORMAT;
            } else {
                return null;
            }
        }

        private static String getHybNameFromAVGSignalCol(String compositeName, ColNameFormat format) {
            return format.getHybName(compositeName, "AVG_SIGNAL");
        }

        private boolean isPresent(RowHeader header) {
            return this.rowHeaderindexes[header.ordinal()] != -1;
        }

        private RowHeader findExpectedProbeIdColumn() {
            if (IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE.equals(this.designType)
                    && isPresent(RowHeader.TARGETID)) {
                return RowHeader.TARGETID;
            }
            if (BgxDesignHandler.BGX_FILE_TYPE.equals(this.designType)) {
                return isPresent(RowHeader.PROBE_ID) ? RowHeader.PROBE_ID
                        : (isPresent(RowHeader.ID_REF) ? RowHeader.ID_REF : null);
            }
            return null;
        }

        private RowHeader findProbeIdColumn(int lineNum) {
            // look for the right header.
            RowHeader col = findExpectedProbeIdColumn();

            // look for any probe id header.
            if (col == null) {
                for (final RowHeader rh : EnumSet.of(RowHeader.TARGETID, RowHeader.PROBE_ID, RowHeader.ID_REF)) {
                    if (isPresent(rh)) {
                        col = rh;
                        warn("Using column " + col + " with design type "
                                + (this.designType == null ? "" : this.designType.getName()), lineNum, 0);
                        break;
                    }
                }
            }
            if (col == null) {
                error("Missing probe Id column", lineNum, 0);
            }
            return col;
        }

        /**
         * parse composite column names.
         */
        private static interface ColNameFormat {
            String getHybName(String compositeColName, String qTypeName);

            String getLocalColName(String compositColName, String hybName);
        }

        /**
         * "ARRAY_STDEV-1745080067_B" style column names.
         */
        private static final ColNameFormat DASH_FORMAT = new ColNameFormat() {
            @Override
            public String getHybName(String compositeColName, String localName) {
                if (compositeColName.toUpperCase(Locale.getDefault()).startsWith(localName + '-')) {
                    return compositeColName.substring(localName.length() + 1);
                }
                return null;
            }

            @Override
            public String getLocalColName(String compositColName, String hybName) {
                if (compositColName.endsWith('-' + hybName)) {
                    return compositColName.substring(0, compositColName.length() - hybName.length() - 1);
                }
                return null;
            }
        };

        /**
         * "Patient 1-Tumor.ARRAY_STDEV" style column names.
         */
        private static final ColNameFormat DOT_FORMAT = new ColNameFormat() {
            @Override
            public String getHybName(String compositeColName, String localName) {
                if (compositeColName.toUpperCase(Locale.getDefault()).endsWith('.' + localName)) {
                    return compositeColName.substring(0, compositeColName.length() - localName.length() - 1);
                }
                return null;
            }

            @Override
            public String getLocalColName(String compositColName, String hybName) {
                if (compositColName.startsWith(hybName + '.')) {
                    return compositColName.substring(hybName.length() + 1);
                }
                return null;
            }
        };

        /**
         * This map is defined here since the enum cannot use statics in the ctor.
         */
        private static final Map<String, HybHeader> HEADER_ALT_NAME_MAP = new HashMap<String, HybHeader>();

        /**
         * Columns per hyb (QTypes).
         */
        private static enum HybHeader {
            MIN_SIGNAL, AVG_SIGNAL, MAX_SIGNAL, NARRAYS, ARRAY_STDEV, BEAD_STDEV, AVG_NBEADS, DETECTION(
                    "DETECTION PVAL", "DETECTIONPVAL");

            private final SampleProbeProfileQuantitationType qType;

            HybHeader(String... altNames) {
                this.qType = SampleProbeProfileQuantitationType.valueOf(name());
                for (final String n : altNames) {
                    HEADER_ALT_NAME_MAP.put(n, this);
                }
                HEADER_ALT_NAME_MAP.put(name(), this);
            }

            public SampleProbeProfileQuantitationType getQType() {
                return this.qType;
            }

            static HybHeader forAltName(String name) {
                return HEADER_ALT_NAME_MAP.get(name);
            }
        }

        /**
         * Known common/shared column headers.
         */
        private static enum RowHeader {
            TARGETID, PROBEID, // expected column, but unused.
            PROBE_ID, ID_REF;
        }
    }
}
