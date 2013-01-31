//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;

/**
 * Base class for both gene expression and genotype Illumina CSV design handlers.
 */
abstract class AbstractIlluminaDesignHandler {

    AbstractIlluminaDesignHandler() {
        super();
    }

    abstract Enum[] getExpectedHeaders(List<String> headers);

    abstract boolean isLineFollowingAnnotation(List<String> values);

    abstract void validateValues(List<String> values, FileValidationResult result, int currentLineNumber);

    abstract LogicalProbe createLogicalProbe(ArrayDesignDetails details, List<String> name);

    abstract boolean isHeaderLine(List<String> values);

    final String getValue(List<String> values, Enum header) {
        return IlluminaCsvDesignHandler.getValue(values, header);
    }

    final void validateIntegerField(List<String> values, Enum header, FileValidationResult result, int lineNumber) {
        IlluminaCsvDesignHandler.validateIntegerField(values, header, result, lineNumber);
    }

    final boolean isInteger(String value) {
        return IlluminaCsvDesignHandler.isInteger(value);
    }

    final Long getLongValue(List<String> values, Enum header) {
        return IlluminaCsvDesignHandler.getLongValue(values, header);
    }

    final Integer getIntegerValue(List<String> values, Enum header) {
        return IlluminaCsvDesignHandler.getIntegerValue(values, header);
    }

    final void validateLongField(List<String> values, Enum header, FileValidationResult result, int lineNumber) {
        IlluminaCsvDesignHandler.validateLongField(values, header, result, lineNumber);
    }

    final void validateFieldLength(List<String> values, Enum header, FileValidationResult result,
            int lineNumber, int length) {
        IlluminaCsvDesignHandler.validateFieldLength(values, header, result, lineNumber, length);
    }

}
