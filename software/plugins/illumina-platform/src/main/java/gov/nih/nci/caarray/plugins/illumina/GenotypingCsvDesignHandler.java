//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import com.google.common.collect.ImmutableSet;
import gov.nih.nci.caarray.application.util.Utils;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.SNPProbeAnnotation;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Reads Illumina genotyping and gene expression array description files.
 */
final class GenotypingCsvDesignHandler extends AbstractCsvDesignHelper<GenotypingCsvDesignHandler.Header> {
    private static final String CONTROLS_SECTION_HEADER = "[Controls]";
    private static final String DB_SNP_SOURCE_NAME = "dbSNP";
    private static final int SNP_FIELD_LENGTH = 5;
    private static final int SNP_ALLELE_A_POSITION = 1;
    private static final int SNP_ALLELE_B_POSITION = 3;

    private static final Set<Header> REQUIRED_COLUMNS = ImmutableSet.of(
            Header.SNP, Header.MAPINFO, Header.SOURCE, Header.NAME);
    

    GenotypingCsvDesignHandler() {
        super(Header.class, REQUIRED_COLUMNS);
    }

    @Override
    PhysicalProbe createProbe(ArrayDesignDetails details, List<String> values) {
        String name = getValue(values, Header.ILMNID);
        PhysicalProbe probe = new PhysicalProbe(details, null);
        probe.setName(name);
        SNPProbeAnnotation annotation = new SNPProbeAnnotation();
        if (Utils.isInteger(getValue(values, Header.CHR))) {
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
        probe.setAnnotation(annotation);
        return probe;
    }

    private boolean isDbSnpEntry(List<String> values) {
        return getValue(values, Header.SOURCE).startsWith(DB_SNP_SOURCE_NAME);
    }

    @Override
    void validateValues(List<String> values, FileValidationResult result, int lineNumber) {
        int colIdx = indexOf(Header.SNP);
        IlluminaCsvDesignHandler.validateFieldLength(values.get(colIdx), Header.SNP, result, lineNumber,
                SNP_FIELD_LENGTH, colIdx + 1);
        colIdx = indexOf(Header.MAPINFO);
        IlluminaCsvDesignHandler.validateLongField(values.get(colIdx), Header.MAPINFO, result, lineNumber, colIdx + 1);
        if (isDbSnpEntry(values)) {
            colIdx = indexOf(Header.SOURCEVERSION);
            IlluminaCsvDesignHandler.validateIntegerField(values.get(colIdx), Header.SOURCEVERSION, result, lineNumber,
                    colIdx + 1);
        }
    }


    @Override
    @SuppressWarnings("PMD.PositionLiteralsFirstInComparisons") // false positive
    boolean isLineFollowingAnnotation(List<String> values) {
        return !values.isEmpty() && CONTROLS_SECTION_HEADER.equals(values.get(0));
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
    }
}
