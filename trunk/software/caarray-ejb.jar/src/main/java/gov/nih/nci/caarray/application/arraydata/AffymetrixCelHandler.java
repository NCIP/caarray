//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
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
    void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, FileValidationResult result,
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
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


