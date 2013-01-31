//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class IlluminaArrayDesignFiles {

    public static final File HUMAN_WG6_CSV =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6.csv").getFile());

    public static final File HUMAN_WG6_CSV_INVALID_HEADERS =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6_invalid_headers.csv").getFile());

    public static final File HUMAN_WG6_CSV_INVALID_CONTENT =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/Human_WG-6_invalid_content.csv").getFile());

    public static final File HUMAN_HAP_300_CSV =
        new File(IlluminaArrayDesignFiles.class.getResource("/arraydesign/illumina/HumanHap300v2_A.csv").getFile());

}
