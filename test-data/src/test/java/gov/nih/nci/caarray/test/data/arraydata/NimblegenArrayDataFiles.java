//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class NimblegenArrayDataFiles {

    public static final File HUMAN_EXPRESSION =
        new File(NimblegenArrayDataFiles.class.getResource("/arraydata/nimblegen/100449_532_pair.txt").getFile());
    
    public static final File SHORT_HUMAN_EXPRESSION =
        new File(NimblegenArrayDataFiles.class.getResource("/arraydata/nimblegen/100449_532_pair_short.txt").getFile());

    public static final File BAD_SHORT_HUMAN_EXPRESSION =
        new File(NimblegenArrayDataFiles.class.getResource("/arraydata/nimblegen/100449_532_pair_short.bad.txt").getFile());

    public static final File RAW_TXT_DATA_FILE = new File(NimblegenArrayDataFiles.class.getResource(
            "/arraydata/nimblegen/dummy_nimblegen_raw_data.txt").getFile());
    
    public static final File DERIVED_TXT_DATA_FILE = new File(NimblegenArrayDataFiles.class.getResource(
            "/arraydata/nimblegen/dummy_nimblegen_derived_data.txt").getFile());

}
