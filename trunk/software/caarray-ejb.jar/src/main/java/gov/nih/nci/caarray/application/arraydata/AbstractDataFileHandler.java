//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.BooleanColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DoubleColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.LongColumn;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.ShortColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;


/**
 * Base class for array data file handlers.
 */
@SuppressWarnings("PMD.CyclomaticComplexity") // switch-like statement
public abstract class AbstractDataFileHandler {

    static final String READ_FILE_ERROR_MESSAGE = "Couldn't read file";

    abstract QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file);

    final FileValidationResult validate(CaArrayFile caArrayFile, File file,
            MageTabDocumentSet mTabSet, ArrayDesignService arrayDesignService) {
        FileValidationResult result = new FileValidationResult(file);
        try {
            validate(caArrayFile, file, mTabSet, result, arrayDesignService);
            if (result.isValid()) {
                validateArrayDesignInExperiment(caArrayFile, file, result, arrayDesignService);
            }
        } catch (RuntimeException e) {
            getLog().error("Unexpected RuntimeException validating data file", e);
            result.addMessage(Type.ERROR, "Unexpected error validating data file: " + e.getMessage());
        }
        return result;
    }

    void validateArrayDesignInExperiment(CaArrayFile caArrayFile, File file, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        ArrayDesign design = getArrayDesign(arrayDesignService, file);
        if (design == null) {
            result.addMessage(Type.ERROR, "The array design referenced by this data file could not be found.");
        } else if (!caArrayFile.getProject().getExperiment().getArrayDesigns().contains(design)) {
            result.addMessage(Type.ERROR, "The array design referenced by this data file (" + design.getName()
                    + ") is not associated with this experiment");
        }
    }
    
    /**
     * get the array design for the array from which data in the given file comes.
     * @param arrayDesignService the ArrayDesignService instance
     * @param file the data file
     * @return the ArrayDesign 
     */
    public abstract ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file);

    abstract void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService);

    abstract void loadData(DataSet dataSet, List<QuantitationType> types, File file,
            ArrayDesignService arrayDesignService);

    void prepareColumns(DataSet dataSet, List<QuantitationType> types, int numberOfRows) {
        for (HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
            prepareColumns(hybridizationData, types, numberOfRows);
        }
    }

    private void prepareColumns(HybridizationData hybridizationData, List<QuantitationType> types, int numberOfRows) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (!column.isLoaded() && types.contains(column.getQuantitationType())) {
                getLog().debug("Preparing unloaded data column: " + column.getQuantitationType().getName());
                column.initializeArray(numberOfRows);
            }
        }
    }

    abstract Logger getLog();

    abstract ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile);

    List<String> getHybridizationNames(File dataFile) {
        return Collections.singletonList(FilenameUtils.getBaseName(dataFile.getName()));
    }



    List<String> getSampleNames(File dataFile, String hybridizationName) {
        return Collections.singletonList(hybridizationName);
    }

    /**
     * Set value at given row of the given column to the given value.
     * @param column the column 
     * @param rowIndex the index of the row in the column
     * @param value the value 
     */
    @SuppressWarnings("PMD.CyclomaticComplexity") // switch-like statement
    protected void setValue(AbstractDataColumn column, int rowIndex, String value) {
        Class<?> columnTypeClass = column.getQuantitationType().getTypeClass();
        if (columnTypeClass.equals(Boolean.class)) {
            ((BooleanColumn) column).getValues()[rowIndex] = parseBoolean(value);
        } else if (columnTypeClass.equals(Short.class)) {
            ((ShortColumn) column).getValues()[rowIndex] = parseShort(value);
        } else if (columnTypeClass.equals(Integer.class)) {
            ((IntegerColumn) column).getValues()[rowIndex] = parseInt(value);
        } else if (columnTypeClass.equals(Long.class)) {
            ((LongColumn) column).getValues()[rowIndex] = parseLong(value);
        } else if (columnTypeClass.equals(Float.class)) {
            ((FloatColumn) column).getValues()[rowIndex] = parseFloat(value);
        } else if (columnTypeClass.equals(Double.class)) {
            ((DoubleColumn) column).getValues()[rowIndex] = parseDouble(value);
        } else if (columnTypeClass.equals(String.class)) {
            ((StringColumn) column).getValues()[rowIndex] = value;
        } else {
            throw new IllegalArgumentException("Unsupported type class " + columnTypeClass.getCanonicalName());
        }
    }

    boolean parseBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    short parseShort(String value) {
        return Short.parseShort(value);
    }

    int parseInt(String value) {
        return Integer.parseInt(value);
    }

    long parseLong(String value) {
        return Long.parseLong(value);
    }

    float parseFloat(String value) {
        return Float.parseFloat(value);
    }

    double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    FileStatus getImportedStatus() {
        return FileStatus.IMPORTED;
    }

    FileStatus getValidatedStatus() {
        return FileStatus.VALIDATED;
    }

}
