//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class IlluminaArrayDataFiles {

    public static final File HUMAN_WG6 =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Human_WG-6_data.csv").getFile());

    public static final File HUMAN_WG6_SMALL =
        new File(IlluminaArrayDataFiles.class.getResource("/arraydata/illumina/Human_WG-6_data_small.csv").getFile());

}
