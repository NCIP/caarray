//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Handler for unparsed data formats.
 */
class UnsupportedDataFormatHandler extends AbstractDataFileHandler {

    private static final Logger LOG = Logger.getLogger(IlluminaDataHandler.class);

    UnsupportedDataFormatHandler() {
        super();
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(File dataFile) {
        return UnsupportedDataFormatDescriptor.INSTANCE;
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(File file) {
        return new QuantitationTypeDescriptor[] {};
    }

    @Override
    void loadData(DataSet dataSet, List<QuantitationType> types, File file, ArrayDesignService arrayDesignService) {
        // no-op, data parsing not supported for the current type
    }

    @Override
    void validate(CaArrayFile caArrayFile, File file, MageTabDocumentSet mTabSet, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        // no-op, data parsing not supported for the current type
    }

    @Override
    FileStatus getImportedStatus() {
        return FileStatus.IMPORTED_NOT_PARSED;
    }

    @Override
    FileStatus getValidatedStatus() {
        return FileStatus.VALIDATED_NOT_PARSED;
    }

    @Override
    ArrayDesign getArrayDesign(ArrayDesignService arrayDesignService, File file) {
        // data parsing not supported for the current type
        return null;
    }

    @Override
    void validateArrayDesignInExperiment(CaArrayFile caArrayFile, File file, FileValidationResult result,
            ArrayDesignService arrayDesignService) {
        // no-op, data parsing not supported for the current type
    }

}
