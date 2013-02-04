//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Illumina Genotyping Processed Matrix importer.
 * 
 * @since 2.4.0
 * @author gax
 */
public final class GenotypingProcessedMatrixHandler extends AbstractDataFileHandler {
    private static final Logger LOG = Logger.getLogger(GenotypingProcessedMatrixHandler.class);
    private static final long ONE_MINUTE = 1000L * 60L;

    /**
     * File Type for GENOTYPING_MATRIX data files.
     */
    public static final FileType GENOTYPING_MATRIX_FILE_TYPE = new FileType("ILLUMINA_GENOTYPING_PROCESSED_MATRIX_TXT",
            FileCategory.DERIVED_DATA, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(GENOTYPING_MATRIX_FILE_TYPE);

    private final ValueParser valueParser = new DefaultValueParser();
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    /**
     * 
     */
    @Inject
    GenotypingProcessedMatrixHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
        super(dataStorageFacade);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
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
        final DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, getFile());
        final List<IlluminaGenotypingProcessedMatrixQuantitationType> l = proc.getLoaders().get(0).getQTypes();
        return l.toArray(new IlluminaGenotypingProcessedMatrixQuantitationType[l.size()]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getHybridizationNames() {
        final DefaultHeaderProcessor proc = new DefaultHeaderProcessor();
        processFile(proc, null, getFile());
        return proc.getHybNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        // cannot determine from file
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, final FileValidationResult result, ArrayDesign design) {
        final ValidatingHeaderParser headerProc = new ValidatingHeaderParser(result, mTabSet);
        final HybDataValidator<IlluminaGenotypingProcessedMatrixQuantitationType> proc =
            new HybDataValidator<IlluminaGenotypingProcessedMatrixQuantitationType>(headerProc, result, design,
                    this.arrayDao);
        processFile(headerProc, proc, getFile());
        proc.finish();
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
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        // pass 1: load design element and count row.
        DefaultHeaderProcessor header = new DefaultHeaderProcessor();
        final DesignElementBuilderParser designElementProc =
            new DesignElementBuilderParser(header, dataSet, design, this.arrayDao, this.searchDao);
        processFile(header, designElementProc, getFile());
        designElementProc.finish();
        dataSet.prepareColumns(types, designElementProc.getElementCount());
        LOG.info("Pass 1/2 loaded " + designElementProc.getElementCount() + " design elements.");
        // pass 2: fill columns.
        header = new DefaultHeaderProcessor();
        final HybDataBuilder<IlluminaGenotypingProcessedMatrixQuantitationType> loader =
            new HybDataBuilder<IlluminaGenotypingProcessedMatrixQuantitationType>(dataSet, header,
                    this.valueParser);
        processFile(header, loader, getFile());
        LOG.info("Pass 2/2 loaded data.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return IlluminaArrayDataTypes.ILLUMINA_GENOTYPING_PROCESSED_MATRIX;
    }

    private static DelimitedFileReader openReader(File dataFile) {
        try {
            return new DelimitedFileReaderFactoryImpl().createTabDelimitedFileReader(dataFile);
        } catch (final IOException e) {
            throw new IllegalStateException("File " + dataFile.getName() + " could not be read", e);
        }
    }

    private void processFile(DefaultHeaderProcessor headerProc, AbstractParser rowProc, File file) {
        final DelimitedFileReader r = openReader(file);
        long ticker = System.currentTimeMillis();
        try {
            boolean keepGoing = r.hasNextLine() && headerProc.parse(r.nextLine(), r.getCurrentLineNumber());
            while (rowProc != null && keepGoing && r.hasNextLine()) {
                keepGoing = rowProc.parse(r.nextLine(), r.getCurrentLineNumber());
                ticker = tick(ticker, r.getCurrentLineNumber(), rowProc);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(AbstractDataFileHandler.READ_FILE_ERROR_MESSAGE, e);
        } finally {
            r.close();
        }
    }

    // CHECKSTYLE:OFF
    static long tick(long lastTick, int line, Object proc) {
        final long now = System.currentTimeMillis();
        if (lastTick + ONE_MINUTE <= now) {
            final Runtime r = Runtime.getRuntime();
            LOG.info("...still processing around line " + line + " with " + proc + " free="
                    + (r.freeMemory() / 1048576) + "/" + (r.totalMemory() / 1048576) + "MB");
            return now;
        }
        return lastTick;
    }

    // CHECKSTYLE:ON
    /**
     * matrix header parser with extra validation.
     */
    static class ValidatingHeaderParser extends DefaultHeaderProcessor {
        private final MageTabDocumentSet mTabSet;

        ValidatingHeaderParser(FileValidationResult result, MageTabDocumentSet mTabSet) {
            super(result);
            this.mTabSet = mTabSet;
        }

        @Override
        protected boolean parseLoaders(List<String> line, int lineNum) {
            final boolean ok = super.parseLoaders(line, lineNum);
            if (ok) {
                validateSdrfNames(this.mTabSet, lineNum);
                validateColumnConsistency(lineNum);
            }
            return ok;
        }
    }

    /**
     * matrix header parser.
     */
    static class DefaultHeaderProcessor
    extends AbstractHeaderParser<IlluminaGenotypingProcessedMatrixQuantitationType> {
        private int period; // hyb block width
        private boolean compositColNames;

        public DefaultHeaderProcessor() {
            super();
        }

        protected DefaultHeaderProcessor(FileValidationResult results) {
            super(new MessageHandler.ValidationMessageHander(results),
                    IlluminaGenotypingProcessedMatrixQuantitationType.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean parseLoaders(List<String> row, int lineNum) {
            super.setProbIdColumn(row.size());
            super.setProbIdColumn(0);

            final boolean ok = checkMinRequiredColumns(row, lineNum);
            if (!ok) {
                return false;
            }
            // count how many times the first data column appears in the table.
            final String firstQTColName = row.get(2);
            final int dotIdx = firstQTColName.indexOf('.');
            this.compositColNames = dotIdx != -1;
            final Predicate p = new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return DefaultHeaderProcessor.this.compositColNames ? object.toString().endsWith(
                            firstQTColName.substring(dotIdx)) : object.toString().equals(firstQTColName);
                }
            };
            final int hybCount = CollectionUtils.countMatches(row, p); // repetitions
            this.period = (row.size() - 1) / hybCount;
            if ((row.size() - 1) % hybCount != 0) {
                error("Not all hybridizations have the same columns", lineNum, 3);
                return false;
            }

            int col = 1; // 0th col is the probe id.
            for (int h = 0; h < hybCount; h++, col += this.period) {
                buildHybBlock(col, row, lineNum, h);
            }

            return true;
        }

        private void buildHybBlock(int blockStart, List<String> row, int lineNum, int block) {
            int col = blockStart;

            final AbstractHeaderParser<IlluminaGenotypingProcessedMatrixQuantitationType>.ValueLoader loader =
                addValueLoader(row.get(blockStart));
            for (int c = 0; c < this.period; c++, col++) {
                String localName = row.get(col);
                if (c != 0 && this.compositColNames) {
                    localName = localName.substring(localName.indexOf('.') + 1);
                }
                Header hdr = null;
                try {
                    hdr = Header.valueOf(localName.toUpperCase(Locale.getDefault()));
                } catch (final IllegalArgumentException e) {
                    if (c == 0) {
                        hdr = Header.ALLELE;
                    } else {
                        warn("Unsupported Column " + localName, lineNum, col + 1);
                    }
                }
                if (hdr != null && hdr.getQType() != null) {
                    loader.addMapping(hdr.getQType(), col, lineNum);
                }
            }
        }

        /**
         * Check if the table has the minimum number of columns : the prob id, at least one hyb, and a data column.
         */
        private boolean checkMinRequiredColumns(List<String> row, int lineNum) {
            if (row.isEmpty()) {
                error("Not a header row", lineNum, 0);
                return false;
            } else if (!Header.isIlmnIdHeaderName(row.get(0))) {
                error("Missing IlmnID, ID_REF, or ID in first column, first line. (Found " + row.get(0) + ")", lineNum,
                        1);
                return false;
            } else if (row.size() == 1) {
                error("Missing \'Value\' (hybridization/sample name) column", lineNum, 2);
                return false;
            } else if (row.size() == 2) {
                error("Missing Quantitation Type (measurement) column", lineNum, 3);
                return false;
            } else {
                return true;
            }
        }

        /**
         * Expected column headers in the tabular TSV file.
         */
        static enum Header {
            /**
             * ILMNID (probe ID).
             */
            ILMNID,
            /**
             * ID_REF (probe ID).
             */
            ID_REF,
            /**
             * ID (probe ID).
             */
            ID,
            /**
             * Implicite, could be anything or sample name.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#ALLELE
             */
            ALLELE,
            /**
             * GC_SCORE.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#GC_SCORE
             */
            GC_SCORE,
            /**
             * THETA.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#THETA
             */
            THETA,
            /**
             * R.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#R
             */
            R,
            /**
             * B_ALLELE_FREQ.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#B_ALLELE_FREQ
             */
            B_ALLELE_FREQ,
            /**
             * LOG_R_RATIO.
             * 
             * @see IlluminaGenotypingProcessedMatrixQuantitationType#LOG_R_RATIO
             */
            LOG_R_RATIO;

            private IlluminaGenotypingProcessedMatrixQuantitationType qType;

            private Header() {
                try {
                    this.qType = IlluminaGenotypingProcessedMatrixQuantitationType.valueOf(name());
                } catch (final IllegalArgumentException e) {
                    this.qType = null;
                }
            }

            /**
             * @return the QuantitationType for the column.
             */
            public IlluminaGenotypingProcessedMatrixQuantitationType getQType() {
                return this.qType;
            }

            static boolean isIlmnIdHeaderName(String colName) {
                return ILMNID.name().equalsIgnoreCase(colName) || ID_REF.name().equalsIgnoreCase(colName)
                || ID.name().equalsIgnoreCase(colName);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }
}
