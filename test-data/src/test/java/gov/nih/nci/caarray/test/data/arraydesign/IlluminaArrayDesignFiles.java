//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class IlluminaArrayDesignFiles {

    public static final File HUMAN_WG6_CSV =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6.csv").getFile());

    public static final File HUMAN_WG6_CSV_REDUCED =
            new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6_reduced.csv").getFile());

    public static final File HUMAN_WG6_CSV_INVALID_HEADERS =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6_invalid_headers.csv").getFile());

    public static final File HUMAN_WG6_CSV_INVALID_CONTENT =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6_invalid_content.csv").getFile());

    public static final File HUMAN_HAP_300_CSV =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/HumanHap300v2_A.csv").getFile());

    public static final File HUMAN_HAP_300_SMALL_CSV =
            new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/HumanHap300v2_A_small.csv").getFile());

    public static final File HUMANWG_6_V2_0_R3_11223189_A_BGX =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/HumanWG-6_V2_0_R3_11223189_A.bgx").getFile());

    public static final File ILLUMINA_SMALL_BGX_TXT =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/illumina-small.bgx.txt").getFile());

    public static final File MOUSE_REF_8 = new File(IlluminaArrayDesignFiles.class.getResource(
            "/arraydesign/illumina/MouseRef-8_V1_1_R2_11234312_A.bgx.txt").getFile());

}
