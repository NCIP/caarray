//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class IlluminaExpressionCsvDesignHandler extends AbstractIlluminaDesignHandler {

    IlluminaExpressionCsvDesignHandler() {
        super();
    }

    LogicalProbe createLogicalProbe(ArrayDesignDetails details, List<String> values) {
        String target = getValue(values, Header.TARGET);
        LogicalProbe logicalProbe = new LogicalProbe(details);
        logicalProbe.setName(target);
        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(new Gene());
        annotation.getGene().setSymbol(getValue(values, Header.SYMBOL));
        annotation.getGene().setFullName(getValue(values, Header.DEFINITION));
        logicalProbe.setAnnotation(annotation);
        return logicalProbe;
    }

    @Override
    Enum[] getExpectedHeaders(List<String> headers) {
        return Header.values();
    }

    @Override
    void validateValues(List<String> values, FileValidationResult result, int lineNumber) {
        validateIntegerField(values, Header.PROBEID, result, lineNumber);
    }

    @Override
    boolean isHeaderLine(List<String> values) {
        if (values.size() != Header.values().length) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            if (!values.get(i).equalsIgnoreCase(Header.values()[i].name())) {
                return false;
            }
        }
        return true;
    }

    @Override
    boolean isLineFollowingAnnotation(List<String> values) {
        return false;
    }

    /**
     * Enumeration of expected headers in Illumina expression CSV descriptor.
     */
    private static enum Header {
        SEARCH_KEY,
        TARGET,
        PROBEID,
        GID,
        TRANSCRIPT,
        ACCESSION,
        SYMBOL,
        TYPE,
        START,
        PROBE_SEQUENCE,
        DEFINITION,
        ONTOLOGY,
        SYNONYM;
    }
}
