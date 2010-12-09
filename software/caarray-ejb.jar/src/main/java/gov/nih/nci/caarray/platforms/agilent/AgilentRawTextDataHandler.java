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
package gov.nih.nci.caarray.platforms.agilent;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.SearchDao;
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
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.DesignElementBuilder;
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.ProbeLookup;
import gov.nih.nci.caarray.platforms.ProbeNamesValidator;
import gov.nih.nci.caarray.platforms.spi.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
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

import com.google.inject.Inject;

/**
 * Handler for Agilent raw text data formats.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" }) // Switch-like statement
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
        PROBE_NAME("ProbeName"),
        SYSTEMATIC_NAME("SystematicName"),
        LOG_RATIO("LogRatio"),
        LOG_RATIO_ERROR("LogRatioError"),
        P_VALUE_LOG_RATIO("PValueLogRatio"),
        G_PROCESSED_SIGNAL("gProcessedSignal"),
        R_PROCESSED_SIGNAL("rProcessedSignal"),
        G_PROCESSED_SIG_ERROR("gProcessedSigError"),
        R_PROCESSED_SIG_ERROR("rProcessedSigError"),
        G_MEDIAN_SIGNAL("gMedianSignal"),
        R_MEDIAN_SIGNAL("rMedianSignal"),
        G_TOTAL_PROBE_SIGNAL("gTotalProbeSignal"),
        G_TOTAL_PROBE_ERROR("gTotalProbeError"),
        G_TOTAL_GENE_SIGNAL("gTotalGeneSignal"),
        G_TOTAL_GENE_ERROR("gTotalGeneError"),
        G_IS_GENE_DETECTED("gIsGeneDetected");
        
        private String name;
        
        private Columns(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private static final int MIN_EXPECTED_ROW_COUNT = 1024;
    private static final Logger LOG = Logger.getLogger(AgilentRawTextDataHandler.class);
    private static final Columns[] MANDATORY_MIRNA = {Columns.PROBE_NAME, Columns.G_TOTAL_PROBE_SIGNAL };
    private static final Columns[] MANDATORY_2_COLOR = {Columns.PROBE_NAME, Columns.LOG_RATIO };
    private static final Columns[] MANDATORY_1_COLOR = {Columns.PROBE_NAME, Columns.G_PROCESSED_SIGNAL };

    private int expectedRowCount;
    private LSID arrayDesignId;
    private Collection<String> columnNames;
    private boolean headerIsRead = false;
    
    private final ArrayDao arrayDao;
    private final SearchDao searchDao;

    @Inject
    AgilentRawTextDataHandler(FileManager fileManager, ArrayDao arrayDao, SearchDao searchDao) {
        super(fileManager);
        this.arrayDao = arrayDao;
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean acceptFileType(FileType type) {
        return type == FileType.AGILENT_RAW_TXT;
    }
    
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return AgilentArrayDataTypes.AGLIENT_RAW_TEXT_ACGH;
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        try {
            readHeader();
            List<QuantitationTypeDescriptor> tmp = new ArrayList<QuantitationTypeDescriptor>();
            for (AgilentTextQuantitationType qt : AgilentTextQuantitationType.values()) {
                if (columnNames.contains(qt.getName().toLowerCase(Locale.ENGLISH))) {
                    tmp.add(qt);
                }
            }
            return tmp.toArray(new QuantitationTypeDescriptor[tmp.size()]);

        } catch (PlatformFileReadException ex) {
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
            AgilentTextParser parser = openReader(this.getFile());
            parser.close();
           return true;
        } else {
            return false;
        }
    }

    private AgilentTextParser openReader(final File file) throws PlatformFileReadException {
        try {
            return new AgilentTextParser(file);
        } catch (Exception e) {
            throw new PlatformFileReadException(file, "Could not create file reader.", e);
        }
    }
    
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
            throws PlatformFileReadException {
               
        createDesignElementList(dataSet, expectedRowCount);
        
        final DesignElementBuilder designElementBuilder =
            new DesignElementBuilder(dataSet, design, arrayDao, searchDao);
        readData(getDesignElementHandler(designElementBuilder));
        designElementBuilder.finish();
        
        int actualProbeCount = designElementBuilder.getElementCount();
        LOG.info("read " + actualProbeCount + " features");
       
        dataSet.prepareColumns(types, actualProbeCount);
        
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>(actualProbeCount);
        typeSet.addAll(types);
        
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        
        readData(getHybridizationDataHandler(hybridizationData, typeSet));
    }
    
    private ProbeHandler getDesignElementHandler(final DesignElementBuilder designElementBuilder) {
        return new ProbeHandler() {
            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                designElementBuilder.addProbe(probeName, systematicName);                
            }            
        };
    }
    
    private ProbeHandler getHybridizationDataHandler(final HybridizationData hybridizationData,
            final Set<QuantitationType> typeSet) {
        return new ProbeHandler() {
            private int index = 0;
            private ProbeData probe = new ProbeData();
            
            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                probe.loadValuesFromParser(parser);
                handleProbe(probe, index, hybridizationData, typeSet);                
                index++;
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
        if (!headerIsRead) {
            doReadHeader(parser);
            headerIsRead = true;
        }
    }

    private void readHeader() throws PlatformFileReadException {
        if (!headerIsRead) {
            AgilentTextParser parser = openReader(this.getFile());
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
                 
                if ("FEPARAMS".equalsIgnoreCase(parser.getSectionName())) {
                    this.arrayDesignId = new LSID("Agilent.com", "PhysicalArrayDesign",
                            parser.getStringValue("Grid_Name"));
                } else if ("STATS".equalsIgnoreCase(parser.getSectionName())) {
                    // rough estimate of how many unique probe lines we'll find.
                    expectedRowCount = parser.getIntValue("TotalNumFeatures") / 2;
                    expectedRowCount = Math.max(expectedRowCount, MIN_EXPECTED_ROW_COUNT);
                } else if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
                    this.columnNames = parser.getColumnNames();
                    break; // Finished reading the header
                }
            }
        } catch (Exception e) {
            throw new PlatformFileReadException(getFile(), "Could not parse file", e);
        }
    }
    
    private void readData(ProbeHandler handler) throws PlatformFileReadException {        
        final AgilentTextParser parser = openReader(this.getFile());
        Set<String> probeNames = new HashSet<String>(expectedRowCount);
        
        readHeader(parser);
        try {
            handleFeatureLine(parser, probeNames, handler);
            while (parser.hasNext()) {
                 parser.next();                 
                 handleFeatureLine(parser, probeNames, handler);
            }
        } catch (Exception e) {
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
            private ProbeLookup probeLookup = new ProbeLookup(design.getDesignDetails().getProbes());

            public void handle(String probeName, String systematicName, AgilentTextParser parser) {
                boolean columnsOkay = checkMandatoryColumns(parser, mandatoryColumns, result);               
                
                if (columnsOkay) {
                    boolean probeNameNotFound = probeName == null || null == probeLookup.getProbe(probeName);
                    if (probeNameNotFound) {
                        boolean systematicNameNotFound = systematicName == null
                            || null == probeLookup.getProbe(systematicName);
                        if (systematicNameNotFound) {
                            result.addMessage(Type.ERROR, ProbeNamesValidator.formatErrorMessage(
                                    new String[] {probeName, systematicName}, design));
                        }
                    }
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
        for (Columns column : mandatoryColumns) {
            String name = column.getName();
            String value = parser.getStringValue(name);
            if (StringUtils.isBlank(value)) {
                result.addMessage(Type.ERROR, "Missing or blank " + name, 
                        parser.getCurrentLineNumber(), parser.getColumnIndex(name) + 1);
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
        return columnNames.contains(column.getName().toLowerCase(Locale.ENGLISH));
    }
    
    /**
     * {@inheritDoc}
     */        
    public boolean requiresMageTab() throws PlatformFileReadException {
        readHeader();
        return isTwoColor();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        try {
            readHeader();
            return Collections.singletonList(this.arrayDesignId);
        } catch (PlatformFileReadException e) {
            return Collections.emptyList();
        }
    }
    
    /**
     * {@inheritDoc}
     */
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
            logRatio = parser.getFloatValue(Columns.LOG_RATIO.getName());
            logRatioError = parser.getFloatValue(Columns.LOG_RATIO_ERROR.getName());
            pValueLogRatio = parser.getFloatValue(Columns.P_VALUE_LOG_RATIO.getName());
            gProcessedSignal = parser.getFloatValue(Columns.G_PROCESSED_SIGNAL.getName());
            rProcessedSignal = parser.getFloatValue(Columns.R_PROCESSED_SIGNAL.getName());
            gProcessedSigError = parser.getFloatValue(Columns.G_PROCESSED_SIG_ERROR.getName());
            rProcessedSigError = parser.getFloatValue(Columns.R_PROCESSED_SIG_ERROR.getName());
            gMedianSignal = parser.getFloatValue(Columns.G_MEDIAN_SIGNAL.getName());
            rMedianSignal = parser.getFloatValue(Columns.R_MEDIAN_SIGNAL.getName());
            gTotalProbeSignal = parser.getFloatValue(Columns.G_TOTAL_PROBE_SIGNAL.getName());
            gTotalProbeError = parser.getFloatValue(Columns.G_TOTAL_PROBE_ERROR.getName());
            gTotalGeneSignal = parser.getFloatValue(Columns.G_TOTAL_GENE_SIGNAL.getName());
            gTotalGeneError = parser.getFloatValue(Columns.G_TOTAL_GENE_ERROR.getName());
            gIsGeneDetected = parser.getBooleanValue(Columns.G_IS_GENE_DETECTED.getName());
        }

        void saveIntoColumn(final AbstractDataColumn column, final int index) {
            final QuantitationType quantitationType = column.getQuantitationType();
            if (AgilentTextQuantitationType.LOG_RATIO.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = logRatio;
            } else if (AgilentTextQuantitationType.LOG_RATIO_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = logRatioError;
            } else if (AgilentTextQuantitationType.P_VALUE_LOG_RATIO.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = pValueLogRatio;
            } else if (AgilentTextQuantitationType.G_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gProcessedSignal;
            } else if (AgilentTextQuantitationType.R_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = rProcessedSignal;
            } else if (AgilentTextQuantitationType.G_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gProcessedSigError;
            } else if (AgilentTextQuantitationType.R_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = rProcessedSigError;
            } else if (AgilentTextQuantitationType.G_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gMedianSignal;
            } else if (AgilentTextQuantitationType.R_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = rMedianSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_PROBE_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gTotalProbeSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_PROBE_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gTotalProbeError;
            } else if (AgilentTextQuantitationType.G_TOTAL_GENE_SIGNAL.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gTotalGeneSignal;
            } else if (AgilentTextQuantitationType.G_TOTAL_GENE_ERROR.getName().equals(quantitationType.getName())) {
                ((FloatColumn) column).getValues()[index] = gTotalGeneError;
            } else if (AgilentTextQuantitationType.G_IS_GENE_DETECTED.getName().equals(quantitationType.getName())) {
                ((BooleanColumn) column).getValues()[index] = gIsGeneDetected;
            } else {
                throw new IllegalArgumentException("Unsupported QuantitationType data: " + quantitationType);
            }
            
        }
    }
}

