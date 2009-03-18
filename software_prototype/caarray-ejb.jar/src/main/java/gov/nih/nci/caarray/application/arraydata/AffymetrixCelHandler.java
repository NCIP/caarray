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
import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixCelQuantitationType;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
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
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import affymetrix.fusion.cel.FusionCELData;
import affymetrix.fusion.cel.FusionCELFileEntryType;


/**
 * Array data handler for all versions of the Affymetrix CEL file format.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // Switch-like statement setValue()
class AffymetrixCelHandler extends AbstractDataFileHandler {

    private static final Logger LOG = Logger.getLogger(AffymetrixCelHandler.class);
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";

    private FusionCELData celData = new FusionCELData();

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File celFile) {
        return AffymetrixCelQuantitationType.values();
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        try {
            String celDataFileName;
            celData.setFileName(file.getAbsolutePath());
            celDataFileName = StringUtils.defaultIfEmpty(celData.getFileName(), "<MISSING FILE NAME>");
            if (!readCelData(celDataFileName)) {
                result.addMessage(ValidationMessage.Type.ERROR, "Unable to read the CEL file: "
                        + celDataFileName);
            } else {
                validateHeader(result);
                validateAgainstDesign(result, arrayDesignService);
            }
        } finally {
            closeCelData();
        }
    }

    private void validateAgainstDesign(FileValidationResult result, ArrayDesignService arrayDesignService) {
        validateDesignExists(result, arrayDesignService);
        if (result.isValid()) {
            validateFeatures(result, arrayDesignService);
        }
    }

    private void validateDesignExists(FileValidationResult result, ArrayDesignService arrayDesignService) {
        if (arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, getLsidObjectId()) == null) {
            result.addMessage(Type.ERROR, "The system doesn't contain the required Affymetrix array design: "
                    + getLsidObjectId());
        }
    }

    private String getLsidObjectId() {
        return celData.getChipType();
    }

    private void validateFeatures(FileValidationResult result, ArrayDesignService arrayDesignService) {
        ArrayDesign arrayDesign = getDesign(arrayDesignService);
        if (celData.getCells() != arrayDesign.getNumberOfFeatures()) {
            result.addMessage(Type.ERROR, "The CEL file is inconsistent with the array design: "
                    + "the CEL file contains data for " + celData.getCells() + " features, but the "
                    + "array design contains " + arrayDesign.getNumberOfFeatures() + " features");
        }
    }

    private ArrayDesign getDesign(ArrayDesignService arrayDesignService) {
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, getLsidObjectId());
    }

    private void validateHeader(FileValidationResult result) {
        if (celData.getRows() == 0) {
            result.addMessage(Type.ERROR, "Invalid CEL file: header specified 0 rows.");
        }
        if (celData.getCols() == 0) {
            result.addMessage(Type.ERROR, "Invalid CEL file: header specified 0 columns.");
        }
        if (celData.getCells() == 0) {
            result.addMessage(Type.ERROR, "Invalid CEL file: header specified 0 cells.");
        }
        if (StringUtils.isEmpty(celData.getChipType())) {
            result.addMessage(Type.ERROR, "Invalid CEL file: no array design type was specified.");
        }
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File celFile, ArrayDesignService arrayDesignService) {
        try {
            LOG.debug("Started loadData for file: " + celFile.getName());
            readCelData(celFile.getAbsolutePath());
            if (dataSet.getDesignElementList() == null) {
                loadDesignElementList(dataSet);
            }
            prepareColumns(dataSet, types, celData.getCells());
            loadDataIntoColumns(dataSet.getHybridizationDataList().get(0), types);
        } finally {
            closeCelData();
        }
        LOG.debug("Completed loadData for file: " + celFile.getName());
    }

    private void loadDesignElementList(DataSet dataSet) {
        DesignElementList probeList = new DesignElementList();
        probeList.setDesignElementTypeEnum(DesignElementType.PHYSICAL_PROBE);
        dataSet.setDesignElementList(probeList);
    }

    /**
     * @param celFile
     */
    private boolean readCelData(String filename) {
        celData.setFileName(filename);
        boolean success = celData.read();
        if (!success) {
            // This invokes a fileChannel.map call that could possibly fail due to a bug in Java
            // that causes previous memory mapped files to not be released until after GC.  So
            // we force a gc here to ensure that is not the cause of our problems
            System.gc();
            celData.clear();
            success = celData.read();
        }
        return success;
    }

    private void closeCelData() {
        // See development tracker issue #9735 and dev tracker #10925 for details on why System.gc() used here
        celData.clear();
        celData = null;
        System.gc();
    }

    private void loadDataIntoColumns(HybridizationData hybridizationData, List<QuantitationType> types) {
        Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        FusionCELFileEntryType entry = new FusionCELFileEntryType();
        int numberOfCells = celData.getCells();
        for (int cellIndex = 0; cellIndex < numberOfCells; cellIndex++) {
            celData.getEntry(cellIndex, entry);
            handleEntry(hybridizationData, entry, cellIndex, typeSet);
        }
    }

    private void handleEntry(HybridizationData hybridizationData, FusionCELFileEntryType entry,
            int cellIndex, Set<QuantitationType> typeSet) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (typeSet.contains(column.getQuantitationType())) {
                setValue(column, cellIndex, entry);
            }
        }
    }

    @SuppressWarnings("PMD.CyclomaticComplexity") // Switch-like statement
    private void setValue(AbstractDataColumn column, int cellIndex, FusionCELFileEntryType entry) {
        QuantitationType quantitationType = column.getQuantitationType();
        if (AffymetrixCelQuantitationType.CEL_X.isEquivalent(quantitationType)) {
            ((ShortColumn) column).getValues()[cellIndex] = (short) celData.indexToX(cellIndex);
        } else if (AffymetrixCelQuantitationType.CEL_Y.isEquivalent(quantitationType)) {
            ((ShortColumn) column).getValues()[cellIndex] = (short) celData.indexToY(cellIndex);
        } else if (AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(quantitationType)) {
            ((FloatColumn) column).getValues()[cellIndex] = entry.getIntensity();
        } else if (AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV.isEquivalent(quantitationType)) {
            ((FloatColumn) column).getValues()[cellIndex] = entry.getStdv();
        } else if (AffymetrixCelQuantitationType.CEL_MASK.isEquivalent(quantitationType)) {
            ((BooleanColumn) column).getValues()[cellIndex] = celData.isMasked(cellIndex);
        } else if (AffymetrixCelQuantitationType.CEL_OUTLIER.isEquivalent(quantitationType)) {
            ((BooleanColumn) column).getValues()[cellIndex] = celData.isOutlier(cellIndex);
        } else if (AffymetrixCelQuantitationType.CEL_PIXELS.isEquivalent(quantitationType)) {
            ((ShortColumn) column).getValues()[cellIndex] = entry.getPixels();
        } else {
            throw new IllegalArgumentException("Unsupported QuantitationType for CEL data: " + quantitationType);
        }
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        return AffymetrixArrayDataTypes.AFFYMETRIX_CEL;
    }

    @Override
    ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        String objectId = null;
        try {
            celData = new FusionCELData();
            celData.setFileName(file.getAbsolutePath());
            readCelData(celData.getFileName());
            objectId = getLsidObjectId();
        } finally {
            closeCelData();
        }
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE, objectId);
    }

}

