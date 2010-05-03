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
import gov.nih.nci.caarray.util.io.ResettableFileReader;
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

import com.google.inject.Inject;

/**
 * Handler for Agilent raw text data formats.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // Switch-like statement
class AgilentRawTextDataHandler extends AbstractDataFileHandler {    
    private final List<ProbeElement> probes = new ArrayList<ProbeElement>();
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

    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return AgilentAcghQuantitationType.values();
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
            this.parser = new AgilentTextParser(reader);
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
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
     }
    
    private void loadData(final HybridizationData hybridizationData, DesignElementList designElementList,
            final List<QuantitationType> types, ProbeLookup probeLookup) {
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);

        final List<AbstractDesignElement> designElements = designElementList.getDesignElements();
        
        int index = 0;
        for (ProbeElement probeElement : probes) {
            final AbstractProbe probe = probeLookup.getProbe(probeElement.probeName);
            designElements.add(probe);
            handleProbe(probeElement, index++, hybridizationData, typeSet);
        }
    }

    private void handleProbe(final ProbeElement entry, final int index, final HybridizationData hybridizationData,
            final Set<QuantitationType> typeSet) {
        
        for (final AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                setValue(column, index, entry);
            }
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity") // Switch-like statement
    private void setValue(final AbstractDataColumn column, final int index, final ProbeElement probe) {
        final QuantitationType quantitationType = column.getQuantitationType();
        if (AgilentAcghQuantitationType.LOG_RATIO.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getLogRatio();
        } else if (AgilentAcghQuantitationType.LOG_RATIO_ERROR.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getLogRatioError();
        } else if (AgilentAcghQuantitationType.P_VALUE_LOG_RATIO.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getpValueLogRatio();
        } else if (AgilentAcghQuantitationType.G_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getgProcessedSignal();
        } else if (AgilentAcghQuantitationType.R_PROCESSED_SIGNAL.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getrProcessedSignal();
        } else if (AgilentAcghQuantitationType.G_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getgProcessedSigError();
        } else if (AgilentAcghQuantitationType.R_PROCESSED_SIG_ERROR.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getrProcessedSigError();
        } else if (AgilentAcghQuantitationType.G_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getgMedianSignal();
        } else if (AgilentAcghQuantitationType.R_MEDIAN_SIGNAL.getName().equals(quantitationType.getName())) {
            ((FloatColumn) column).getValues()[index] = probe.getrMedianSignal();
        } else {
            throw new IllegalArgumentException("Unsupported QuantitationType for aCGH data: " + quantitationType);
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
                 } else if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
                     this.columnNames = parser.getColumnNames();
                     break; // Finished reading the header
                 }
             } 
        } catch (Exception e) {
            throw new PlatformFileReadException(getFile(), "Could parse file", e);
        }
    }

    private void readData() throws PlatformFileReadException {
        if (!dataIsRead) {
           doReadData();
           dataIsRead = true;
        }
    }

    private void doReadData() throws PlatformFileReadException {
        readHeader();
        try {
            Set<String> probeNames = new HashSet<String>();
            handleFeatureLine(probeNames);
            while (parser.hasNext()) {
                 parser.next();                 
                 handleFeatureLine(probeNames);
             } 
        } catch (Exception e) {
            throw new PlatformFileReadException(getFile(), "Could parse file", e);
        }
    }

    private void handleFeatureLine(Set<String> probeNames) {
        if ("FEATURES".equalsIgnoreCase(parser.getSectionName())) {
             final String probeName = parser.getStringValue("ProbeName");
             if (!probeNames.contains(probeName)) {
                 probeNames.add(probeName);
                 ProbeElement probe = new ProbeElement();
                 probes.add(probe);
                 
                 setProbeData(probe);                       
             }
         }
    }

    private void setProbeData(ProbeElement probe) {
        probe.setProbeName(parser.getStringValue("ProbeName"));
        probe.setLogRatio(parser.getFloatValue("LogRatio"));
        probe.setLogRatioError(parser.getFloatValue("LogRatioError"));
        probe.setpValueLogRatio(parser.getFloatValue("PValueLogRatio"));
        probe.setgProcessedSignal(parser.getFloatValue("gProcessedSignal"));
        probe.setrProcessedSignal(parser.getFloatValue("rProcessedSignal"));
        probe.setgProcessedSigError(parser.getFloatValue("gProcessedSigError"));
        probe.setrProcessedSigError(parser.getFloatValue("rProcessedSigError"));
        probe.setgMedianSignal(parser.getFloatValue("gMedianSignal"));
        probe.setrMedianSignal(parser.getFloatValue("rMedianSignal"));
    }

    /**
     * {@inheritDoc}
     */
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException {
        readData();
        validateMageTab(mTabSet, result);       
        validateProbeNames(result, design);
        validateMandatoryColumnsPresent(result);
        validateAppropriateAdditionalColumnsPresent(result);
    }

    
    private void validateMageTab(MageTabDocumentSet mTabSet, FileValidationResult result) {
        if (mTabSet == null || mTabSet.getIdfDocuments().isEmpty() || mTabSet.getSdrfDocuments().isEmpty()) {
            result.addMessage(Type.ERROR, "An IDF and SDRF must be provided for this data file type.");
        }
    }
    
    private void validateProbeNames(FileValidationResult result, ArrayDesign design) {
        ProbeLookup probeLookup = new ProbeLookup(design.getDesignDetails().getProbes());
        
        for (ProbeElement probe : probes) {
            final String probeName = probe.getProbeName();
            if (null == probeLookup.getProbe(probeName)) {
                result.addMessage(Type.ERROR, 
                        String.format("Probe \"%s\" is not found array design \"%s\"",
                                probeName, design.getName()));
            }
        }
    }
    
    private void validateMandatoryColumnsPresent(FileValidationResult result) {
        if (!hasMandatoryColumns()) {
            result.addMessage(Type.ERROR, "Agilent Raw Text files must contain both ProbeName and LogRatio columns.");
        }
    }
    
    private void validateAppropriateAdditionalColumnsPresent(FileValidationResult result) {
        if (!isTwoColorExperiment()) {
            result.addMessage(Type.ERROR, "Not all expected columns are present in the Aligent Raw Text File.");
        }
    }
    
    private boolean hasMandatoryColumns() {
        boolean isTwoColor = true;
        
        isTwoColor &= columnExists("ProbeName");
        isTwoColor &= columnExists("LogRatio");
        
        return isTwoColor;
    }
    
    private boolean isTwoColorExperiment() {
        boolean isTwoColor = true;
        
        isTwoColor &= columnExists("LogRatioError");
        isTwoColor &= columnExists("PValueLogRatio");
        isTwoColor &= columnExists("gProcessedSignal");
        isTwoColor &= columnExists("rProcessedSignal");
        isTwoColor &= columnExists("gProcessedSigError");
        isTwoColor &= columnExists("rProcessedSigError");
        isTwoColor &= columnExists("gMedianSignal");
        isTwoColor &= columnExists("rMedianSignal");
        
        return isTwoColor;
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
     * Holds the data for each probe in the file.
     * 
     * @author jscott
     */
    private class ProbeElement {
        private String probeName;
        private float logRatio;         
        private float logRatioError;    
        private float pValueLogRatio;    
        private float gProcessedSignal;  
        private float rProcessedSignal;  
        private float gProcessedSigError;
        private float rProcessedSigError;
        private float gMedianSignal;     
        private float rMedianSignal;
        
        /**
         * @param probeName the probeName to set
         */
        public void setProbeName(String probeName) {
            this.probeName = probeName;
        }
        /**
         * @return the probeName
         */
        public String getProbeName() {
            return probeName;
        }
        /**
         * @param value the logRatio to set
         */
        public void setLogRatio(float value) {
            this.logRatio = value;
        }
        /**
         * @return the logRatio
         */
        public float getLogRatio() {
            return logRatio;
        }
        /**
         * @param value the logRatioError to set
         */
        public void setLogRatioError(float value) {
            this.logRatioError = value;
        }
        /**
         * @return the logRatioError
         */
        public float getLogRatioError() {
            return logRatioError;
        }
        /**
         * @param value the pValueLogRatio to set
         */
        public void setpValueLogRatio(float value) {
            this.pValueLogRatio = value;
        }
        /**
         * @return the pValueLogRatio
         */
        public float getpValueLogRatio() {
            return pValueLogRatio;
        }
        /**
         * @param value the gProcessedSignal to set
         */
        public void setgProcessedSignal(float value) {
            this.gProcessedSignal = value;
        }
        /**
         * @return the gProcessedSignal
         */
        public float getgProcessedSignal() {
            return gProcessedSignal;
        }
        /**
         * @param value the rProcessedSignal to set
         */
        public void setrProcessedSignal(float value) {
            this.rProcessedSignal = value;
        }
        /**
         * @return the rProcessedSignal
         */
        public float getrProcessedSignal() {
            return rProcessedSignal;
        }
        /**
         * @param value the gProcessedSigError to set
         */
        public void setgProcessedSigError(float value) {
            this.gProcessedSigError = value;
        }
        /**
         * @return the gProcessedSigError
         */
        public float getgProcessedSigError() {
            return gProcessedSigError;
        }
        /**
         * @param value the rProcessedSigError to set
         */
        public void setrProcessedSigError(float value) {
            this.rProcessedSigError = value;
        }
        /**
         * @return the rProcessedSigError
         */
        public float getrProcessedSigError() {
            return rProcessedSigError;
        }
        /**
         * @param value the gMedianSignal to set
         */
        public void setgMedianSignal(float value) {
            this.gMedianSignal = value;
        }
        /**
         * @return the gMedianSignal
         */
        public float getgMedianSignal() {
            return gMedianSignal;
        }
        /**
         * @param value the rMedianSignal to set
         */
        public void setrMedianSignal(float value) {
            this.rMedianSignal = value;
        }
        /**
         * @return the rMedianSignal
         */
        public float getrMedianSignal() {
            return rMedianSignal;
        }     
    }   
}
