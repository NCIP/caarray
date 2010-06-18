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

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.AbstractProbe;
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
import gov.nih.nci.caarray.platforms.FileManager;
import gov.nih.nci.caarray.platforms.ProbeLookup;
import gov.nih.nci.caarray.platforms.spi.AbstractDataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fiveamsolutions.nci.commons.util.io.ResettableFileReader;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 * Handler for Agilent raw text data formats.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // Switch-like statement
class AgilentRawTextDataHandler extends AbstractDataFileHandler {

    private static final int MIN_EXPECTED_ROW_COUNT = 1024;
    private static final Logger LOG = Logger.getLogger(AgilentRawTextDataHandler.class);
    private static final String[] MANDATORY_MIRNA = {"ProbeName", "gTotalProbeSignal" };
    private static final String[] MANDATORY_2_COLOR = {"ProbeName", "LogRatio" };
    private static final String[] MANDATORY_1_COLOR = {"ProbeName", "gProcessedSignal" };

    private List<RowData> probes;
    private int expectedRowCount;
    private Reader reader;
    private LSID arrayDesignId;
    private Collection<String> columnNames;
    private boolean headerIsRead = false;
    private boolean dataIsRead = false;
    private AgilentTextParser parser;

    @Inject
    AgilentRawTextDataHandler(FileManager fileManager) {
        super(fileManager);
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
            openReader(this.getFile());          
           return true;
        } else {
            return false;
        }
    }

    private void openReader(final File file) throws PlatformFileReadException {
        try {
            this.reader = new ResettableFileReader(file);
            this.parser = new AgilentTextParser(file);
        } catch (IOException e) {
            throw new PlatformFileReadException(file, "Could not create file reader.", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        closeReader();
        super.closeFiles();
    }

    private void closeReader() {
        if (null != this.reader) {
            try {
                this.reader.close();
                this.reader = null;
            } catch (IOException e) {
                // Attempted close failed, just move on
                return;
            }
        }
    }

    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design)
            throws PlatformFileReadException {
        readData();
        
        createDesignElementList(dataSet);
        dataSet.prepareColumns(types, this.probes.size());
        
        ProbeLookup probeLookup = new ProbeLookup(design.getDesignDetails().getProbes());
        loadData(dataSet.getHybridizationDataList().get(0), dataSet.getDesignElementList(), types, probeLookup);
    }
    
    private void createDesignElementList(final DataSet dataSet) {
        final DesignElementList probeList = new DesignElementList();
        probeList.setDesignElements(new ArrayList<AbstractDesignElement>(probes.size()));
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
     }
    
    private void loadData(final HybridizationData hybridizationData, DesignElementList designElementList,
            final List<QuantitationType> types, ProbeLookup probeLookup) {
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>(expectedRowCount);
        typeSet.addAll(types);

        final List<AbstractDesignElement> designElements = designElementList.getDesignElements();
        
        int index = 0;
        for (RowData probeElement : probes) {
            AbstractProbe probe = probeLookup.getProbe(probeElement.probeName);
            if (probe == null) {
                probe = probeLookup.getProbe(probeElement.systematicName);
            }
            designElements.add(probe);
            handleProbe(probeElement, index++, hybridizationData, typeSet);
        }
    }

    private void handleProbe(final RowData entry, final int index, final HybridizationData hybridizationData,
            final Set<QuantitationType> typeSet) {
        
        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                entry.saveColumn(column, index);
            }
        }
    }

    private void readHeader() throws PlatformFileReadException {
        if (!headerIsRead) {
           doReadHeader();
           headerIsRead = true;
        }
    }

    private void doReadHeader() throws PlatformFileReadException {
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

    /**
     * read and validate all rows.
     */
    private void readData(FileValidationResult result) throws PlatformFileReadException {
        if (!dataIsRead) {
           doReadData(result);
           dataIsRead = true;
        }
    }

    /**
     * read all rows w/o validation.
     */
    private void readData() throws PlatformFileReadException {
        doReadData(null);
    }

    private void doReadData(FileValidationResult result) throws PlatformFileReadException {
        readHeader();
        String[] mandatoryColumns = getMandatoryColumnNames();
        try {
            Set<String> probeNames = new HashSet<String>(expectedRowCount);
            probes = new ArrayList<RowData>(expectedRowCount);
            handleFeatureLine(probeNames, result, mandatoryColumns);
            while (parser.hasNext()) {
                 parser.next();                 
                 handleFeatureLine(probeNames, result, mandatoryColumns);
            }
            LOG.info("read " + probes.size() + " features");
        } catch (Exception e) {
            throw new PlatformFileReadException(getFile(), "Could not parse file", e);
        }
    }
    
    private String[] getMandatoryColumnNames() {
        if (isMiRNA()) {
            return MANDATORY_MIRNA;
        } else if (isTwoColor()) {
            return MANDATORY_2_COLOR;
        } else if (isSingleColor()) {
            return MANDATORY_1_COLOR;
        } else {
            return new String[0];
        }
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private void handleFeatureLine(Set<String> probeNames, FileValidationResult result, String[] mandatoryColumns) {
        if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
            if (result != null) {
                checkMandatoryColumns(mandatoryColumns, result);
            }
            final String probeName = parser.getStringValue("ProbeName");
            if (!probeNames.contains(probeName)) {
                probeNames.add(probeName);
                RowData probe = new RowData();
                probe.loadRow(parser);
                probes.add(probe);
            }
         }
    }

    private void checkMandatoryColumns(String[] mandatoryColumns, FileValidationResult result) {
        for (String n : mandatoryColumns) {
            String v = parser.getStringValue(n);
            if (StringUtils.isBlank(v)) {
                result.addMessage(Type.ERROR, "Missing or blank " + n, 
                        parser.getCurrentLineNumber(), parser.getColumnIndex(n) + 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        readData(result);
        if (isTwoColor()) {
            validateMageTab(mTabSet, result);
        }
        if (design == null) {
            result.addMessage(Type.ERROR, "No array design associated with this experiment");
        } else {
            validateProbeNames(result, design);
        }        
        validateMandatoryColumnsPresent(result);
    }

    
    private void validateMageTab(MageTabDocumentSet mTabSet, FileValidationResult result) {
        if (mTabSet == null || mTabSet.getIdfDocuments().isEmpty() || mTabSet.getSdrfDocuments().isEmpty()) {
            result.addMessage(Type.ERROR, "An IDF and SDRF must be provided for this data file type.");
        }
    }
    
    private void validateProbeNames(FileValidationResult result, ArrayDesign design) {
        ProbeLookup probeLookup = new ProbeLookup(design.getDesignDetails().getProbes());
        for (RowData probe : probes) {
            String probeName = probe.getProbeName();
            if (probeName == null || null == probeLookup.getProbe(probeName)) {
                String probeName2 = probe.getSystematicName();
                if (probeName2 == null || null == probeLookup.getProbe(probeName2)) {
                    result.addMessage(Type.ERROR, String.format(
                            "Probe \"%s\" or \"%s\"is not found array design \"%s\"",
                            probeName, probeName2, design.getName()));
                }
            }
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
        if (!columnExists("ProbeName")) {
            result.addMessage(Type.ERROR, "Unable to find column ProbeName");
        }
    }

    /**
     * miRNA.
     */
    private boolean isMiRNA() {
        return columnExists("gTotalProbeSignal") && !columnExists("LogRatio");
    }

    /**
     * aCGH / gene_expression-2_color.
     */
    private boolean isTwoColor() {
        return columnExists("LogRatio") && !columnExists("gTotalProbeSignal");
    }

    /**
     * gene_expression-1_color.
     */
    private boolean isSingleColor() {
        return columnExists("gProcessedSignal") && !columnExists("LogRatio") && !columnExists("gTotalProbeSignal");
    }
    
    /**
     * @param columnName
     * @return
     */
    private boolean columnExists(final String columnName) {
        return columnNames.contains(columnName.toLowerCase(Locale.ENGLISH));
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
    
    public boolean parsesData() {
        return true;
    }

    /**
     * data holder for a row in the data table.
     */
    @SuppressWarnings("PMD.TooManyFields")
    static class RowData {
        private String probeName;
        private String systematicName;
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

        void loadRow(AgilentTextParser parser) {
            probeName = parser.getStringValue("ProbeName");
            systematicName = parser.getStringValue("SystematicName");
            logRatio = parser.getFloatValue("LogRatio");
            logRatioError = parser.getFloatValue("LogRatioError");
            pValueLogRatio = parser.getFloatValue("PValueLogRatio");
            gProcessedSignal = parser.getFloatValue("gProcessedSignal");
            rProcessedSignal = parser.getFloatValue("rProcessedSignal");
            gProcessedSigError = parser.getFloatValue("gProcessedSigError");
            rProcessedSigError = parser.getFloatValue("rProcessedSigError");
            gMedianSignal = parser.getFloatValue("gMedianSignal");
            rMedianSignal = parser.getFloatValue("rMedianSignal");
            gTotalProbeSignal = parser.getFloatValue("gTotalProbeSignal");
            gTotalProbeError = parser.getFloatValue("gTotalProbeError");
            gTotalGeneSignal = parser.getFloatValue("gTotalGeneSignal");
            gTotalGeneError = parser.getFloatValue("gTotalGeneError");
            gIsGeneDetected = parser.getBooleanValue("gIsGeneDetected");
        }

        String getProbeName() {
            return probeName;
        }

        String getSystematicName() {
            return systematicName;
        }
        
        void saveColumn(final AbstractDataColumn column, final int index) {
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