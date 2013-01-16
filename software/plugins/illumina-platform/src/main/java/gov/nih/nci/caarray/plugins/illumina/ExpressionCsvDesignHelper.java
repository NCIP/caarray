//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import com.google.common.collect.ImmutableSet;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class ExpressionCsvDesignHelper extends AbstractCsvDesignHelper<ExpressionCsvDesignHelper.Header> {
    private static final Set<Header> REQUIRED_COLUMNS = ImmutableSet.of(
        Header.TARGET, Header.SYMBOL, Header.DEFINITION, Header.PROBEID);
    
    ExpressionCsvDesignHelper() {
        super(Header.class, REQUIRED_COLUMNS);
    }

    @Override
    PhysicalProbe createProbe(ArrayDesignDetails details, List<String> values) {
        String target = getValue(values, Header.TARGET);
        PhysicalProbe logicalProbe = new PhysicalProbe(details, null);
        logicalProbe.setName(target);
        ExpressionProbeAnnotation annotation = new ExpressionProbeAnnotation();
        annotation.setGene(new Gene());
        annotation.getGene().setSymbol(getValue(values, Header.SYMBOL));
        annotation.getGene().setFullName(getValue(values, Header.DEFINITION));
        logicalProbe.setAnnotation(annotation);
        return logicalProbe;
    }

    @Override
    void validateValues(List<String> values, FileValidationResult result, int lineNumber) {
        String probeId = getValue(values, Header.PROBEID);
        if (StringUtils.isBlank(probeId)) {
            ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR, "Missing or blank PROBEID");
            error.setLine(lineNumber);
            error.setColumn(indexOf(Header.PROBEID) + 1);
        }
    }

    @Override
    boolean isLineFollowingAnnotation(List<String> values) {
        return false;
    }

    /**
     * Enumeration of expected headers in Illumina expression CSV descriptor.
     */
    static enum Header {
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
