//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.plugins.agilent.AgilentTextParser.ParseException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handler for Agilent raw text data formats.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
class AgilentRawTextDataHandler extends AbstractDataFileHandler {
    /**
     * Handles probes during parsing.
     */
    private interface ProbeHandler {
        void handle(String probeName, String systematicName, AgilentTextParser parser);
    }

    /**
     * Relevant columns in an Agilent raw text file.
     */
    private enum Columns {
        PROBE_NAME("ProbeName"), SYSTEMATIC_NAME("SystematicName"), LOG_RATIO("LogRatio"), LOG_RATIO_ERROR(
                "LogRatioError"), P_VALUE_LOG_RATIO("PValueLogRatio"), G_PROCESSED_SIGNAL("gProcessedSignal"),
        R_PROCESSED_SIGNAL("rProcessedSignal"), G_PROCESSED_SIG_ERROR("gProcessedSigError"), R_PROCESSED_SIG_ERROR(
                "rProcessedSigError"), G_MEDIAN_SIGNAL("gMedianSignal"), R_MEDIAN_SIGNAL("rMedianSignal"),
        G_TOTAL_PROBE_SIGNAL("gTotalProbeSignal"), G_TOTAL_PROBE_ERROR("gTotalProbeError"), G_TOTAL_GENE_SIGNAL(
                "gTotalGeneSignal"), G_TOTAL_GENE_ERROR("gTotalGeneError"), G_IS_GENE_DETECTED("gIsGeneDetected");

        private String name;

        private Columns(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    private static final int MIN_EXPECTED_ROW_COUNT = 1024;
    private static final Logger LOG = Logger.getLogger(AgilentRawTextDataHandler.class);
    private static final Columns[] MANDATORY_MIRNA = {Columns.PROBE_NAME, Columns.G_TOTAL_PROBE_SIGNAL };
    private static final Columns[] MANDATORY_2_COLOR = {Columns.PROBE_NAME, Columns.LOG_RATIO };
    private static final Columns[] MANDATORY_1_COLOR = {Columns.PROBE_NAME, Columns.G_PROCESSED_SIGNAL };

    public static final FileType RAW_TXT_FILE_TYPE = new FileType("AGILENT_RAW_TXT", FileCategory.RAW_DATA, true);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(RAW_TXT_FILE_TYPE);

    private int expectedRowCount;
    private Collection<String> columnNames;
    private boolean headerIsRead = false;

    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    @Inject
    AgilentRawTextDataHandler(DataStorageFacade dataStorageFacade, ArrayDao arrayDao, SearchDao searchDao) {
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

    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return AgilentArrayDataTypes.AGLIENT_RAW_TEXT_ACGH;
    }

    @Override
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        try {
            readHeader();
            final List<QuantitationTypeDescriptor> tmp = new ArrayList<QuantitationTypeDescriptor>();
            for (final AgilentTextQuantitationType qt : AgilentTextQuantitationType.values()) {
                if (this.columnNames.contains(qt.getName().toLowerCase(Locale.ENGLISH))) {
                    tmp.add(qt);
                }
            }
            return tmp.toArray(new QuantitationTypeDescriptor[tmp.size()]);

        } catch (final PlatformFileReadException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFile(CaArrayFile dataFile) throws PlatformFileReadException {
        if (super.openFile(dataFile)) {
            // Make sure we can open a parser.
            final AgilentTextParser parser = openReader(this.getFile());
            parser.close();
            return true;
        } else {
            return false;
        }
    }

    private AgilentTextParser openReader(final File file) throws PlatformFileReadException {
        try {
            return new AgilentTextParser(file);
        } catch (final ParseException e) {
            throw new PlatformFileReadException(file, "Could not create file reader.", e);
        }
    }

    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
            throws PlatformFileReadException {

        createDesignElementList(dataSet, this.expectedRowCount);

        final DesignElementBuilder designElementBuilder =
                new DesignElementBuilder(dataSet, design, this.arrayDao, this.searchDao);
        readData(getDesignElementHandler(designElementBuilder));
        designElementBuilder.finish();

        final int actualProbeCount = designElementBuilder.getElementCount();
        LOG.info("read " + actualProbeCount + " features");

        dataSet.prepareColumns(types, actualProbeCount);

        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>(actualProbeCount);
        typeSet.addAll(types);

        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);

        readData(getHybridizationDataHandler(hybridizationData, typeSet));
    }

    private ProbeHandler getDesignElementHandler(final DesignElementBuilder designElementBuilder) {
        return new ProbeHandler() {
            @Override
            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                designElementBuilder.addProbe(probeName, systematicName);
            }
        };
    }

    private ProbeHandler getHybridizationDataHandler(final HybridizationData hybridizationData,
            final Set<QuantitationType> typeSet) {
        return new ProbeHandler() {
            private int index = 0;
            private final ProbeData probe = new ProbeData();

            @Override
            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                this.probe.loadValuesFromParser(parser);
                handleProbe(this.probe, this.index, hybridizationData, typeSet);
                this.index++;
            }
        };
    }

    private void createDesignElementList(final DataSet dataSet, final int expectedListSize) {
        final DesignElementList probeList = new DesignElementList();
        probeList.setDesignElements(new ArrayList<AbstractDesignElement>(expectedListSize));
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
    }

    private void handleProbe(final ProbeData probe, final int index, final HybridizationData hybridizationData,
            final Set<QuantitationType> typeSet) {

        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                probe.saveIntoColumn(column, index);
            }
        }
    }

    private void readHeader(AgilentTextParser parser) throws PlatformFileReadException {
        if (!this.headerIsRead) {
            doReadHeader(parser);
            this.headerIsRead = true;
        }
    }

    private void readHeader() throws PlatformFileReadException {
        if (!this.headerIsRead) {
            final AgilentTextParser parser = openReader(this.getFile());
            try {
                doReadHeader(parser);
            } finally {
                parser.close();
            }
        }
    }

    private void doReadHeader(AgilentTextParser parser) throws PlatformFileReadException {
        try {
            while (parser.hasNext()) {
                parser.next();

                if ("STATS".equalsIgnoreCase(parser.getSectionName())) {
                    // rough estimate of how many unique probe lines we'll find.
                    this.expectedRowCount = parser.getIntValue("TotalNumFeatures") / 2;
                    this.expectedRowCount = Math.max(this.expectedRowCount, MIN_EXPECTED_ROW_COUNT);
                } else if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
                    this.columnNames = parser.getColumnNames();
                    break; // Finished reading the header
                }
            }
        } catch (final Exception e) {
            throw new PlatformFileReadException(getFile(), "Could not parse file", e);
        }
    }

    private void readData(ProbeHandler handler) throws PlatformFileReadException {
        final AgilentTextParser parser = openReader(this.getFile());
        final Set<String> probeNames = new HashSet<String>(this.expectedRowCount);

        readHeader(parser);
        try {
            handleFeatureLine(parser, probeNames, handler);
            while (parser.hasNext()) {
                parser.next();
                handleFeatureLine(parser, probeNames, handler);
            }
        } catch (final Exception e) {
            throw new PlatformFileReadException(getFile(), "Could not parse file", e);
        } finally {
            parser.close();
        }
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void handleFeatureLine(AgilentTextParser parser, Set<String> probeNamesSet, ProbeHandler handler) {
        if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
            final String probeName = parser.getStringValue(Columns.PROBE_NAME.getName());
            final String systematicName = parser.getStringValue(Columns.SYSTEMATIC_NAME.getName());
            if (!probeNamesSet.contains(probeName)) {
                probeNamesSet.add(probeName);
                handler.handle(probeName, systematicName, parser);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        validateHighLevelDetails(result, design);
        if (result.isValid()) {
            validateLowLevelDetails(result, design);
        }
    }

    private void validateLowLevelDetails(FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        readData(getValidationHandler(result, design, getMandatoryColumnNames()));
    }

    private void validateHighLevelDetails(FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        readHeader();
        validateMandatoryColumnsPresent(result);
        if (design == null) {
            result.addMessage(Type.ERROR, "No array design associated with this experiment");
        } else if (design.getDesignDetails() == null) {
            result.addMessage(Type.ERROR, "Array design " + design.getName() + " was not parsed");
        }
    }

    private ProbeHandler getValidationHandler(final FileValidationResult result, final ArrayDesign design,
            final Columns[] mandatoryColumns) {
        return new ProbeHandler() {
            private final Set<String> probeList = arrayDao.getPhysicalProbeNames(design);

            @Override
            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                final boolean columnsOkay = checkMandatoryColumns(parser, mandatoryColumns, result);

                if (columnsOkay
                        && (probeName == null || !probeList.contains(probeName))
                        && (systematicName == null || !probeList.contains(systematicName))) {
                    result.addMessage(Type.ERROR, ProbeNamesValidator.formatErrorMessage(new String[] {
                            probeName, systematicName }, design));
                }
            }
        };
    }

    private Columns[] getMandatoryColumnNames() {
        if (isMiRNA()) {
            return MANDATORY_MIRNA;
        } else if (isTwoColor()) {
            return MANDATORY_2_COLOR;
        } else if (isSingleColor()) {
            return MANDATORY_1_COLOR;
        } else {
            return new Columns[0];
        }
    }

    private void validateMandatoryColumnsPresent(FileValidationResult result) {
        if (isMiRNA()) {
            result.addMessage(Type.INFO, "Processing as miRNA (found gTotalProbeSignal w/o LogRatio)");
        } else if (isTwoColor()) {
            result.addMessage(Type.INFO, "Processing as aCGH or 2 color gene expression "
                    + "(found LogRatio w/o gTotalProbeSignal)");
        } else if (isSingleColor()) {
            result.addMessage(Type.INFO, "Processing as single color gene expression (found gProcessedSignal w/o "
                    + "LogRatio or gTotalProbeSignal)");
        } else {
            result.addMessage(Type.ERROR, "Unable to find column gTotalProbeSignal without a LogRatio column (miRNA)");
            result.addMessage(Type.ERROR, "Unable to find column LogRatio without a gTotalProbeSignal column "
                    + "(aCGH or 2 color gene expression)");
            result.addMessage(Type.ERROR, "Unable to find column gProcessedSignal without a LogRatio "
                    + "or gTotalProbeSignal column (single color gene expression)");
        }
        if (!columnExists(Columns.PROBE_NAME)) {
            result.addMessage(Type.ERROR, "Unable to find column ProbeName");
        }
    }

    private boolean checkMandatoryColumns(AgilentTextParser parser, Columns[] mandatoryColumns,
            FileValidationResult result) {
        boolean columnsOkay = true;
        for (final Columns column : mandatoryColumns) {
            final String name = column.getName();
            final String value = parser.getStringValue(name);
            if (StringUtils.isBlank(value)) {
                result.addMessage(Type.ERROR, "Missing or blank " + name, parser.getCurrentLineNumber(),
                        parser.getColumnIndex(name) + 1);
                columnsOkay = false;
            }
        }
        return columnsOkay;
    }

    /**
     * miRNA.
     */
    private boolean isMiRNA() {
        return columnExists(Columns.G_TOTAL_PROBE_SIGNAL) && !columnExists(Columns.LOG_RATIO);
    }

    /**
     * aCGH / gene_expression-2_color.
     */
    private boolean isTwoColor() {
        return columnExists(Columns.LOG_RATIO) && !columnExists(Columns.G_TOTAL_PROBE_SIGNAL);
    }

    /**
     * gene_expression-1_color.
     */
    private boolean isSingleColor() {
        return columnExists(Columns.G_PROCESSED_SIGNAL) && !columnExists(Columns.LOG_RATIO)
                && !columnExists(Columns.G_TOTAL_PROBE_SIGNAL);
    }

    /**
     * @param columnName
     * @return
     */
    private boolean columnExists(final Columns column) {
        return this.columnNames.contains(column.getName().toLowerCase(Locale.ENGLISH));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() throws PlatformFileReadException {
        //readHeader();
        //return isTwoColor();
        
        //Replace the above with below. - Andrew Sy 2011-08-24
        
        //ARRAY-2059: 
        //[Multiple associated array designs causes Agilent gene expression data to fail validation and import] 
        //This handler always requires MAGE-TAB. 
        //Otherwise there is now way to map array data file to its array design, 
        //in case there are multiple array design files in the experiment. 
        
        return true;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
//        try {
//            readHeader();
//            return Collections.singletonList(this.arrayDesignId);
//        } catch (final PlatformFileReadException e) {
//            return Collections.emptyList();
//        }

        //Replace the above with below. - Andrew Sy 2011-08-24
        
        //ARRAY-2059: 
        //[Multiple associated array designs causes Agilent gene expression data to fail validation and import] 
        //Info about referenced array design(s) should come externally from SDRF file, 
        //not from the array data file being parsed
        return Collections.emptyList();
    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return true;
    }

    /**
     * data holder for a row in the data table.
     */
    @SuppressWarnings("PMD.TooManyFields")
    static class ProbeData {
        private float logRatio;
        private float logRatioError;
        private float pValueLogRatio;
        private float gProcessedSignal;
        private float rProcessedSignal;
        private float gProcessedSigError;
        private float rProcessedSigError;
        private float gMedianSignal;
        private float rMedianSignal;
        private float gTotalProbeSignal;
        private float gTotalProbeError;
        private float gTotalGeneSignal;
        private float gTotalGeneError;
        private boolean gIsGeneDetected;

        void loadValuesFromParser(AgilentTextParser parser) {
            this.logRatio = parser.getFloatValue(Columns.LOG_RATIO.getName());
            this.logRatioError = parser.getFloatValue(Columns.LOG_RATIO_ERROR.getName());
            this.pValueLogRatio = parser.getFloatValue(Columns.P_VALUE_LOG_RATIO.getName());
            this.gProcessedSignal = parser.getFloatValue(Columns.G_PROCESSED_SIGNAL.getName());
            this.rProcessedSignal = parser.getFloatValue(Columns.R_PROCESSED_SIGNAL.getName());
            this.gProcessedSigError = parser.getFloatValue(Columns.G_PROCESSED_SIG_ERROR.getName());
            this.rProcessedSigError = parser.getFloatValue(Columns.R_PROCESSED_SIG_ERROR.getName());
            this.gMedianSignal = parser.getFloatValue(Columns.G_MEDIAN_SIGNAL.getName());
            this.rMedianSignal = parser.getFloatValue(Columns.R_MEDIAN_SIGNAL.getName());
            this.gTotalProbeSignal = parser.getFloatValue(Columns.G_TOTAL_PROBE_SIGNAL.getName());
            this.gTotalProbeError = parser.getFloatValue(Columns.G_TOTAL_PROBE_ERROR.getName());
            this.gTotalGeneSignal = parser.getFloatValue(Columns.G_TOTAL_GENE_SIGNAL.getName());
            this.gTotalGeneError = parser.getFloatValue(Columns.G_TOTAL_GENE_ERROR.getName());
            this.gIsGeneDetected = parser.getBooleanValue(Columns.G_IS_GENE_DETECTED.getName());
        }

        void saveIntoColumn(final AbstractDataColumn column, final int index) {
            final QuantitationType quantitationType = column.getQuantitationType();
            if (AgilentTextQuantitationType.LOG_RATIO.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.logRatio;
            } else if (AgilentTextQuantitationType.LOG_RATIO_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.logRatioError;
            } else if (AgilentTextQuantitationType.P_VALUE_LOG_RATIO.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.pValueLogRatio;
            } else if (AgilentTextQuantitationType.G_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gProcessedSignal;
            } else if (AgilentTextQuantitationType.R_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.rProcessedSignal;
            } else if (AgilentTextQuantitationType.G_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gProcessedSigError;
            } else if (AgilentTextQuantitationType.R_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.rProcessedSigError;
            } else if (AgilentTextQuantitationType.G_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gMedianSignal;
            } else if (AgilentTextQuantitationType.R_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.rMedianSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_PROBE_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gTotalProbeSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_PROBE_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gTotalProbeError;
            } else if (AgilentTextQuantitationType.G_TOTAL_GENE_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gTotalGeneSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_GENE_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = this.gTotalGeneError;
            } else if (AgilentTextQuantitationType.G_IS_GENE_DETECTED.getName().equals(quantitationType.getName())) {
                ((BooleanColumn) column).getValues()[index] = this.gIsGeneDetected;
            } else {
                throw new IllegalArgumentException("Unsupported QuantitationType data: " + quantitationType);
            }

        }
    }
}
