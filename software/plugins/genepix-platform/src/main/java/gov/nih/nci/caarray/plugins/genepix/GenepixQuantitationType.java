//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.genepix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * Quantitation type information for Genepix GPR files.
 */
public enum GenepixQuantitationType implements QuantitationTypeDescriptor {
    /**
     * Autoflag.
     */
    AUTOFLAG("Autoflag", DataType.BOOLEAN),

    /**
     * B Pixels.
     */
    B_PIXELS("B Pixels", DataType.INTEGER),

    /**
     * B532.
     */
    B532("B532", DataType.INTEGER),

    /**
     * B532 CV.
     */
    B532_CV("B532 CV", DataType.INTEGER),

    /**
     * B532 Mean.
     */
    B532_MEAN("B532 Mean", DataType.INTEGER),

    /**
     * B532 Median.
     */
    B532_MEDIAN("B532 Median", DataType.INTEGER),

    /**
     * B532 SD.
     */
    B532_SD("B532 SD", DataType.INTEGER),

    /**
     * B635.
     */
    B635("B635", DataType.INTEGER),

    /**
     * B635 CV.
     */
    B635_CV("B635 CV", DataType.INTEGER),

    /**
     * B635 Mean.
     */
    B635_MEAN("B635 Mean", DataType.INTEGER),

    /**
     * B635 Median.
     */
    B635_MEDIAN("B635 Median", DataType.INTEGER),

    /**
     * B635 SD.
     */
    B635_SD("B635 SD", DataType.INTEGER),

    /**
     * Circularity.
     */
    CIRCULARITY("Circularity", DataType.INTEGER),

    /**
     * Dia.
     */
    DIA("Dia.", DataType.INTEGER),

    /**
     * F Pixels.
     */
    F_PIXELS("F Pixels", DataType.INTEGER),

    /**
     * F532 CV.
     */
    F532_CV("F532 CV", DataType.INTEGER),

    /**
     * F532 Mean.
     */
    F532_MEAN("F532 Mean", DataType.INTEGER),

    /**
     * F532 Mean - B532.
     */
    F532_MEAN_B532("F532 Mean - B532", DataType.INTEGER),

    /**
     * F532 Median.
     */
    F532_MEDIAN("F532 Median", DataType.INTEGER),

    /**
     * F532 Median - B532.
     */
    F532_MEDIAN_B532("F532 Median - B532", DataType.INTEGER),

    /**
     * F532 % Sat..
     */
    F532_PERCENT_SAT("F532 % Sat.", DataType.INTEGER),

    /**
     * F532 SD.
     */
    F532_SD("F532 SD", DataType.INTEGER),

    /**
     * F532 Total Intensity.
     */
    F532_TOTAL_INTENSITY("F532 Total Intensity", DataType.INTEGER),

    /**
     * F635 CV.
     */
    F635_CV("F635 CV", DataType.INTEGER),

    /**
     * F635 Mean.
     */
    F635_MEAN("F635 Mean", DataType.INTEGER),

    /**
     * F635 Mean - B635.
     */
    F635_MEAN_B635("F635 Mean - B635", DataType.INTEGER),

    /**
     * F635 Median.
     */
    F635_MEDIAN("F635 Median", DataType.INTEGER),

    /**
     * F635 Median - B635.
     */
    F635_MEDIAN_B635("F635 Median - B635", DataType.INTEGER),

    /**
     * F635 % Sat..
     */
    F635_PERCENT_SAT("F635 % Sat.", DataType.INTEGER),

    /**
     * F635 SD.
     */
    F635_SD("F635 SD", DataType.INTEGER),

    /**
     * F635 Total Intensity.
     */
    F635_TOTAL_INTENSITY("F635 Total Intensity", DataType.INTEGER),

    /**
     * Flags.
     */
    FLAGS("Flags", DataType.INTEGER),

    /**
     * Log Ratio (635/532).
     */
    LOG_RATIO_635_532("Log Ratio (635/532)", DataType.FLOAT),

    /**
     * Mean of Ratios (635/532).
     */
    MEAN_OF_RATIOS_635_532("Mean of Ratios (635/532)", DataType.FLOAT),

    /**
     * Median of Ratios (635/532).
     */
    MEDIAN_OF_RATIOS_635_532("Median of Ratios (635/532)", DataType.FLOAT),

    /**
     * Normalize.
     */
    NORMALIZE("Normalize", DataType.BOOLEAN),

    /**
     * % > B532+1SD.
     */
    PERCENT_GT_B532_1SD("% > B532+1SD", DataType.INTEGER),

    /**
     * % > B532+2SD.
     */
    PERCENT_GT_B532_2SD("% > B532+2SD", DataType.INTEGER),

    /**
     * % > B635+1SD.
     */
    PERCENT_GT_B635_1SD("% > B635+1SD", DataType.INTEGER),

    /**
     * % > B635+2SD.
     */
    PERCENT_GT_B635_2SD("% > B635+2SD", DataType.INTEGER),

    /**
     * Ratio of Means (635/532).
     */
    RATIO_OF_MEANS_635_532("Ratio of Means (635/532)", DataType.FLOAT),

    /**
     * Ratio of Medians (635/532).
     */
    RATIO_OF_MEDIANS_635_532("Ratio of Medians (635/532)", DataType.FLOAT),

    /**
     * Ratios SD (635/532).
     */
    RATIOS_SD_635_532("Ratios SD (635/532)", DataType.FLOAT),

    /**
     * Rgn R2 (635/532).
     */
    RGN_R2_635_532("Rgn R2 (635/532)", DataType.FLOAT),

    /**
     * Rgn Ratio (635/532).
     */
    RGN_RATIO_635_532("Rgn Ratio (635/532)", DataType.FLOAT),

    /**
     * SNR 532.
     */
    SNR_532("SNR 532", DataType.FLOAT),

    /**
     * SNR 635.
     */
    SNR_635("SNR 635", DataType.FLOAT),

    /**
     * Sum of Means (635/532).
     */
    SUM_OF_MEANS_635_532("Sum of Means (635/532)", DataType.INTEGER),

    /**
     * Sum of Medians (635/532).
     */
    SUM_OF_MEDIANS_635_532("Sum of Medians (635/532)", DataType.INTEGER),

    /**
     * X.
     */
    X("X", DataType.INTEGER),

    /**
     * Y.
     */
    Y("Y", DataType.INTEGER),

    /**
     * B3 Mean.
     */
    B3_MEAN("B3 Mean", DataType.INTEGER),

    /**
     * B3 Median.
     */
    B3_MEDIAN("B3 Median", DataType.INTEGER),

    /**
     * B3 SD.
     */
    B3_SD("B3 SD", DataType.INTEGER),

    /**
     * B4 Mean.
     */
    B4_MEAN("B4 Mean", DataType.INTEGER),

    /**
     * B4 Median.
     */
    B4_MEDIAN("B4 Median", DataType.INTEGER),

    /**
     * B4 SD.
     */
    B4_SD("B4 SD", DataType.INTEGER),

    /**
     * F3 Mean.
     */
    F3_MEAN("F3 Mean", DataType.INTEGER),

    /**
     * F3 Mean - B3.
     */
    F3_MEAN_B3("F3 Mean - B3", DataType.INTEGER),

    /**
     * F3 Median.
     */
    F3_MEDIAN("F3 Median", DataType.INTEGER),

    /**
     * F3 Median - B3.
     */
    F3_MEDIAN_B3("F3 Median - B3", DataType.INTEGER),

    /**
     * F3 % Sat..
     */
    F3_PERCENT_SAT("F3 % Sat.", DataType.INTEGER),

    /**
     * F3 SD.
     */
    F3_SD("F3 SD", DataType.INTEGER),

    /**
     * F4 Mean.
     */
    F4_MEAN("F4 Mean", DataType.INTEGER),

    /**
     * F4 Mean - B4.
     */
    F4_MEAN_B4("F4 Mean - B4", DataType.INTEGER),

    /**
     * F4 Median.
     */
    F4_MEDIAN("F4 Median", DataType.INTEGER),

    /**
     * F4 Median - B4.
     */
    F4_MEDIAN_B4("F4 Median - B4", DataType.INTEGER),

    /**
     * F4 % Sat..
     */
    F4_PERCENT_SAT("F4 % Sat.", DataType.INTEGER),

    /**
     * F4 SD.
     */
    F4_SD("F4 SD", DataType.INTEGER),

    /**
     * Log Ratio (Ratio/2).
     */
    LOG_RATIO_RATIO_2("Log Ratio (Ratio/2)", DataType.FLOAT),

    /**
     * Log Ratio (Ratio/3).
     */
    LOG_RATIO_RATIO_3("Log Ratio (Ratio/3)", DataType.FLOAT),

    /**
     * Mean of Ratios (Ratio/2).
     */
    MEAN_OF_RATIOS_RATIO_2("Mean of Ratios (Ratio/2)", DataType.FLOAT),

    /**
     * Mean of Ratios (Ratio/3).
     */
    MEAN_OF_RATIOS_RATIO_3("Mean of Ratios (Ratio/3)", DataType.FLOAT),

    /**
     * Median of Ratios (Ratio/2).
     */
    MEDIAN_OF_RATIOS_RATIO_2("Median of Ratios (Ratio/2)", DataType.FLOAT),

    /**
     * Median of Ratios (Ratio/3).
     */
    MEDIAN_OF_RATIOS_RATIO_3("Median of Ratios (Ratio/3)", DataType.FLOAT),

    /**
     * % > B3+1SD.
     */
    PERCENT_GT_B3_1SD("% > B3+1SD", DataType.INTEGER),

    /**
     * % > B3+2SD.
     */
    PERCENT_GT_B3_2SD("% > B3+2SD", DataType.INTEGER),

    /**
     * % > B4+1SD.
     */
    PERCENT_GT_B4_1SD("% > B4+1SD", DataType.INTEGER),

    /**
     * % > B4+2SD.
     */
    PERCENT_GT_B4_2SD("% > B4+2SD", DataType.INTEGER),

    /**
     * Ratio of Means (Ratio/2).
     */
    RATIO_OF_MEANS_RATIO_2("Ratio of Means (Ratio/2)", DataType.FLOAT),

    /**
     * Ratio of Means (Ratio/3).
     */
    RATIO_OF_MEANS_RATIO_3("Ratio of Means (Ratio/3)", DataType.FLOAT),

    /**
     * Ratio of Medians (Ratio/2).
     */
    RATIO_OF_MEDIANS_RATIO_2("Ratio of Medians (Ratio/2)", DataType.FLOAT),

    /**
     * Ratio of Medians (Ratio/3).
     */
    RATIO_OF_MEDIANS_RATIO_3("Ratio of Medians (Ratio/3)", DataType.FLOAT),

    /**
     * Ratios SD (Ratio/2).
     */
    RATIOS_SD_RATIO_2("Ratios SD (Ratio/2)", DataType.FLOAT),

    /**
     * Ratios SD (Ratio/3).
     */
    RATIOS_SD_RATIO_3("Ratios SD (Ratio/3)", DataType.FLOAT),

    /**
     * Rgn R2 (Ratio/2).
     */
    RGN_R2_RATIO_2("Rgn R2 (Ratio/2)", DataType.FLOAT),

    /**
     * Rgn R2 (Ratio/3).
     */
    RGN_R2_RATIO_3("Rgn R2 (Ratio/3)", DataType.FLOAT),

    /**
     * Rgn Ratio (Ratio/2).
     */
    RGN_RATIO_RATIO_2("Rgn Ratio (Ratio/2)", DataType.FLOAT),

    /**
     * Rgn Ratio (Ratio/3).
     */
    RGN_RATIO_RATIO_3("Rgn Ratio (Ratio/3)", DataType.FLOAT);

    private final String name;
    private final DataType type;

    private GenepixQuantitationType(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public DataType getDataType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

}
