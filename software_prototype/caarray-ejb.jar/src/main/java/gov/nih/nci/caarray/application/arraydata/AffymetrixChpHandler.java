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
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixExpressionChpQuantitationType;
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixSnpChpQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.AffymetrixChpDesignElementListUtility;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPGenericData;
import affymetrix.fusion.chp.FusionCHPHeader;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionCHPTilingData;
import affymetrix.fusion.chp.FusionExpressionProbeSetResults;
import affymetrix.fusion.chp.FusionGenotypeProbeSetResults;

/**
 * Array data handler for all versions and types of the Affymetrix CHP file format.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // long switch statements
final class AffymetrixChpHandler extends AbstractDataFileHandler {

    private static final Logger LOG = Logger.getLogger(AffymetrixChpHandler.class);
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_DESIGN = "PhysicalArrayDesign";

    private static final Map<String, AffymetrixExpressionChpQuantitationType> EXPRESSION_TYPE_MAP =
        new HashMap<String, AffymetrixExpressionChpQuantitationType>();
    private static final Map<String, AffymetrixSnpChpQuantitationType> SNP_TYPE_MAP =
        new HashMap<String, AffymetrixSnpChpQuantitationType>();

    static {
        initializeExpressionTypeMap();
        initializeSnpTypeMap();
    }

    AffymetrixChpHandler() {
        FusionCHPLegacyData.registerReader();
        FusionCHPGenericData.registerReader();
        FusionCHPTilingData.registerReader();
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File chpFile) {
        FusionCHPLegacyData chpData = getChpData(chpFile);
        int assayType = chpData.getHeader().getAssayType();
        switch (assayType) {
        case FusionCHPHeader.EXPRESSION_ASSAY:
            return AffymetrixExpressionChpQuantitationType.values();
        case FusionCHPHeader.GENOTYPING_ASSAY:
            return AffymetrixSnpChpQuantitationType.values();
        default:
            throw new IllegalArgumentException("Unsupported Affymetrix CHP type");
        }
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file, ArrayDesignService arrayDesignService) {
        Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        if (dataSet.getDesignElementList() == null) {
            getDesignElementList(dataSet, file, arrayDesignService);
        }
        FusionCHPLegacyData chpData = getChpData(file);
        prepareColumns(dataSet, types, chpData.getHeader().getNumProbeSets());
        HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        int assayType = chpData.getHeader().getAssayType();
        switch (assayType) {
        case FusionCHPHeader.EXPRESSION_ASSAY:
            loadExpressionData(hybridizationData, typeSet, chpData);
            break;
        case FusionCHPHeader.GENOTYPING_ASSAY:
            loadSnpData(hybridizationData, typeSet, chpData);
            break;
        default:
            throw new IllegalArgumentException("Unsupported Affymetrix CHP type");
        }
    }

    private void getDesignElementList(DataSet dataSet, File file,
            ArrayDesignService arrayDesignService) {
        ArrayDesign design = getArrayDesign(arrayDesignService, file);
        DesignElementList probeList =
            AffymetrixChpDesignElementListUtility.getDesignElementList(design, arrayDesignService);
        dataSet.setDesignElementList(probeList);
    }

    private void loadExpressionData(HybridizationData hybridizationData, Set<QuantitationType> typeSet,
            FusionCHPLegacyData chpData) {
        int numberOfProbeSets = chpData.getHeader().getNumProbeSets();
        FusionExpressionProbeSetResults entry = new FusionExpressionProbeSetResults();
        for (int probeSetIndex = 0; probeSetIndex < numberOfProbeSets; probeSetIndex++) {
            chpData.getExpressionResults(probeSetIndex, entry);
            handleExpresionEntry(hybridizationData, entry, typeSet, probeSetIndex);
        }
    }

    private void handleExpresionEntry(HybridizationData hybridizationData, FusionExpressionProbeSetResults entry,
            Set<QuantitationType> typeSet, int probeSetIndex) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                setExpressionValue(column, probeSetIndex, entry);
            }
        }
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" }) // long switch statement
    private void setExpressionValue(AbstractDataColumn column, int index, FusionExpressionProbeSetResults entry) {
        QuantitationType quantitationType = column.getQuantitationType();
        AffymetrixExpressionChpQuantitationType typeDescriptor = getExpressionTypeDescriptor(quantitationType);
        switch (typeDescriptor) {
        case CHP_CHANGE:
            ((StringColumn) column).getValues()[index] = entry.getChangeString();
            break;
        case CHP_COMMON_PAIRS:
            ((ShortColumn) column).getValues()[index] = entry.getNumCommonPairs();
            break;
        case CHP_CHANGE_PVALUE:
            ((FloatColumn) column).getValues()[index] = entry.getChangePValue();
            break;
        case CHP_DETECTION:
            ((StringColumn) column).getValues()[index] = entry.getDetectionString();
            break;
        case CHP_DETECTION_PVALUE:
            ((FloatColumn) column).getValues()[index] = entry.getDetectionPValue();
            break;
        case CHP_PAIRS:
            ((ShortColumn) column).getValues()[index] = entry.getNumPairs();
            break;
        case CHP_PAIRS_USED:
            ((ShortColumn) column).getValues()[index] = entry.getNumUsedPairs();
            break;
        case CHP_SIGNAL:
            ((FloatColumn) column).getValues()[index] = entry.getSignal();
            break;
        case CHP_SIGNAL_LOG_RATIO:
            ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatio();
            break;
        case CHP_SIGNAL_LOG_RATIO_HIGH:
            ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatioHigh();
            break;
        case CHP_SIGNAL_LOG_RATIO_LOW:
            ((FloatColumn) column).getValues()[index] = entry.getSignalLogRatioLow();
            break;
        default:
            throw new IllegalArgumentException("Unsupported QuantitationType for expression CHP data: "
                    + quantitationType);
        }
    }

    private AffymetrixExpressionChpQuantitationType getExpressionTypeDescriptor(QuantitationType quantitationType) {
        return EXPRESSION_TYPE_MAP.get(quantitationType.getName());
    }

    private static void initializeExpressionTypeMap() {
        for (AffymetrixExpressionChpQuantitationType descriptor : AffymetrixExpressionChpQuantitationType.values()) {
            EXPRESSION_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    private static void initializeSnpTypeMap() {
        for (AffymetrixSnpChpQuantitationType descriptor : AffymetrixSnpChpQuantitationType.values()) {
            SNP_TYPE_MAP.put(descriptor.getName(), descriptor);
        }
    }

    private AffymetrixSnpChpQuantitationType getSnpTypeDescriptor(QuantitationType quantitationType) {
        return SNP_TYPE_MAP.get(quantitationType.getName());
    }

    private void loadSnpData(HybridizationData hybridizationData, Set<QuantitationType> typeSet,
            FusionCHPLegacyData chpData) {
        int numberOfProbeSets = chpData.getHeader().getNumProbeSets();
        FusionGenotypeProbeSetResults entry = new FusionGenotypeProbeSetResults();
        for (int probeSetIndex = 0; probeSetIndex < numberOfProbeSets; probeSetIndex++) {
            chpData.getGenotypingResults(probeSetIndex, entry);
            handleSnpEntry(hybridizationData, entry, typeSet, probeSetIndex);
        }
    }

    private void handleSnpEntry(HybridizationData hybridizationData, FusionGenotypeProbeSetResults entry,
            Set<QuantitationType> typeSet, int probeSetIndex) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                setSnpValue(column, probeSetIndex, entry);
            }
        }
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength" }) // long switch statement
    private void setSnpValue(AbstractDataColumn column, int probeSetIndex, FusionGenotypeProbeSetResults entry) {
        QuantitationType quantitationType = column.getQuantitationType();
        AffymetrixSnpChpQuantitationType typeDescriptor = getSnpTypeDescriptor(quantitationType);
        switch (typeDescriptor) {
        case CHP_ALLELE:
            ((StringColumn) column).getValues()[probeSetIndex] = entry.getAlleleCallString();
            break;
        case CHP_ALLELE_PVALUE:
            ((FloatColumn) column).getValues()[probeSetIndex] = getAllelePValue(entry);
            break;
        case CHP_RAS1:
            ((FloatColumn) column).getValues()[probeSetIndex] = entry.getRAS1();
            break;
        case CHP_RAS2:
            ((FloatColumn) column).getValues()[probeSetIndex] = entry.getRAS2();
            break;
        default:
            throw new IllegalArgumentException("Unsupported QuantitationType for SNP CHP data: " + quantitationType);
        }
    }

    private float getAllelePValue(FusionGenotypeProbeSetResults entry) {
        switch (entry.getAlleleCall()) {
        case FusionGenotypeProbeSetResults.ALLELE_A_CALL:
            return entry.getPValue_AA();
        case FusionGenotypeProbeSetResults.ALLELE_AB_CALL:
            return entry.getPValue_AB();
        case FusionGenotypeProbeSetResults.ALLELE_B_CALL:
            return entry.getPValue_BB();
        default:
            return entry.getPValue_NoCall();
        }
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        FusionCHPLegacyData chpData = getChpData(file);
        if (chpData == null) {
            result.addMessage(Type.ERROR, "Couldn't read Affymetrix CHP file: " + file.getName());
        } else {
            validateAgainstDesign(chpData, result, arrayDesignService);
        }
    }

    private void validateAgainstDesign(FusionCHPLegacyData chpData, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        validateDesignExists(chpData, result, arrayDesignService);
    }

    private void validateDesignExists(FusionCHPLegacyData chpData, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        String lsidObjectId = chpData.getHeader().getChipType();
        if (arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE_DESIGN, lsidObjectId) == null) {
            result.addMessage(Type.ERROR, "The system doesn't contain the required Affymetrix array design: "
                    + lsidObjectId);
        }
    }


    private FusionCHPLegacyData getChpData(File file) {
        return FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(file.getAbsolutePath()));
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        FusionCHPLegacyData chpData = getChpData(dataFile);
        int assayType = chpData.getHeader().getAssayType();
        switch (assayType) {
        case FusionCHPHeader.EXPRESSION_ASSAY:
            return AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP;
        case FusionCHPHeader.GENOTYPING_ASSAY:
            return AffymetrixArrayDataTypes.AFFYMETRIX_SNP_CHP;
        default:
            throw new IllegalArgumentException("Unsupported Affymetrix CHP type");
        }
    }

    @Override
    ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        return getArrayDesign(arrayDesignService, getChpData(file));
    }

    private ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, FusionCHPLegacyData chpData) {
        String lsidObjectId = chpData.getHeader().getChipType();
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE_DESIGN, lsidObjectId);
    }

}