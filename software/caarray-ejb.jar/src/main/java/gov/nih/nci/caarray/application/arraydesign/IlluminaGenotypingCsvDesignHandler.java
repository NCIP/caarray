//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.SNPProbeAnnotation;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class IlluminaGenotypingCsvDesignHandler extends AbstractIlluminaDesignHandler {

    private static final String CONTROLS_SECTION_HEADER = "[Controls]";
    private static final String DB_SNP_SOURCE_NAME = "dbSNP";
    private static final int SNP_FIELD_LENGTH = 5;
    private static final int SNP_ALLELE_A_POSITION = 1;
    private static final int SNP_ALLELE_B_POSITION = 3;

    IlluminaGenotypingCsvDesignHandler() {
        super();
    }

    LogicalProbe createLogicalProbe(ArrayDesignDetails details, List<String> values) {
        String name = getValue(values, Header.NAME);
        LogicalProbe logicalProbe = new LogicalProbe(details);
        logicalProbe.setName(name);
        SNPProbeAnnotation annotation = new SNPProbeAnnotation();
        if (isInteger(getValue(values, Header.CHR))) {
            annotation.setChromosome(getIntegerValue(values, Header.CHR));
        }
        if (!StringUtils.isBlank(getValue(values, Header.SNP))) {
            annotation.setAlleleA(String.valueOf(getValue(values, Header.SNP).charAt(SNP_ALLELE_A_POSITION)));
            annotation.setAlleleB(String.valueOf(getValue(values, Header.SNP).charAt(SNP_ALLELE_B_POSITION)));
        }
        annotation.setPhysicalPosition(getLongValue(values, Header.MAPINFO));
        if (isDbSnpEntry(values)) {
            annotation.setDbSNPId(name);
            annotation.setDbSNPVersion(getIntegerValue(values, Header.SOURCEVERSION));
        }
        logicalProbe.setAnnotation(annotation);
        return logicalProbe;
    }

    private boolean isDbSnpEntry(List<String> values) {
        return getValue(values, Header.SOURCE).startsWith(DB_SNP_SOURCE_NAME);
    }

    @Override
    void validateValues(List<String> values, FileValidationResult result, int lineNumber) {
        validateFieldLength(values, Header.SNP, result, lineNumber, SNP_FIELD_LENGTH);
        validateLongField(values, Header.MAPINFO, result, lineNumber);
        if (isDbSnpEntry(values)) {
            validateIntegerField(values, Header.SOURCEVERSION, result, lineNumber);
        }
    }

    @Override
    Enum[] getExpectedHeaders(List<String> headers) {
        if (headers.size() == Header.values().length) {
            return Header.values();
        } else {
            return Header.ALTERNATE_HEADER_LIST;
        }
    }

    @Override
    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons") // false positive
    boolean isLineFollowingAnnotation(List<String> values) {
        return !values.isEmpty() && CONTROLS_SECTION_HEADER.equals(values.get(0));
    }

    @Override
    boolean isHeaderLine(List<String> values) {
        if (values.size() != Header.values().length && values.size() != Header.ALTERNATE_HEADER_LIST.length) {
            return false;
        }
        for (int i = 0; i < values.size(); i++) {
            if (!values.get(i).equalsIgnoreCase(Header.values()[i].name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Enumeration of expected headers in Illumina genoytyping CSV descriptor.
     */
    static enum Header {
        ILMNID,
        NAME,
        ILMNSTRAND,
        SNP,
        ADDRESSA_ID,
        ALLELEA_PROBESEQ,
        ADDRESSB_ID,
        ALLELEB_PROBESEQ,
        GENOMEBUILD,
        CHR,
        MAPINFO,
        PLOIDY,
        SPECIES,
        SOURCE,
        SOURCEVERSION,
        SOURCESTRAND,
        SOURCESEQ,
        TOPGENOMICSEQ,
        BEADSETID,
        CNV_PROBE,
        INTENSITY_ONLY,
        EXP_CLUSTERS;

        private static final Header[] ALTERNATE_HEADER_LIST = new Header[] {
            ILMNID,
            NAME,
            ILMNSTRAND,
            SNP,
            ADDRESSA_ID,
            ALLELEA_PROBESEQ,
            ADDRESSB_ID,
            ALLELEB_PROBESEQ,
            GENOMEBUILD,
            CHR,
            MAPINFO,
            PLOIDY,
            SPECIES,
            SOURCE,
            SOURCEVERSION,
            SOURCESTRAND,
            SOURCESEQ,
            TOPGENOMICSEQ,
            BEADSETID
        };
    }
}
