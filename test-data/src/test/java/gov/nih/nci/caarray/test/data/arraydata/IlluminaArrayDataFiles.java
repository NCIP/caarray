//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class IlluminaArrayDataFiles {

    public static final File HUMAN_WG6 =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Human_WG-6_data.csv").getFile());

    public static final File HUMAN_WG6_SMALL =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Human_WG-6_data_small.csv").getFile());

    public static final File HUMAN_WG6_REDUCED =
            new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Human_WG-6_data_reduced.csv").getFile());

    public static final File DEFECT_18652_IDF_REDUCED =
            new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/defect18652/defect18652_reduced.idf").getFile());

        public static final File DEFECT_18652_SDRF_REDUCED =
            new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/defect18652/defect18652_reduced.sdrf").getFile());

    public static final File ILLUMINA_DERIVED_1_HYB =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/illumina-der-1.txt").getFile());

    public static final File BAD_ILLUMINA_DERIVED_1_HYB =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/illumina-der-1.bad.txt").getFile());

    public static final File ILLUMINA_DERIVED_2_HYB =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/illumina-der-2.txt").getFile());

    public static final File SAMPLE_PROBE_PROFILE =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Sample_Probe_Profile.txt").getFile());
}
