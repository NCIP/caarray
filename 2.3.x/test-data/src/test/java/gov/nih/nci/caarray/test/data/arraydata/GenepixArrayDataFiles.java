//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class GenepixArrayDataFiles {

    public static final File JOE_DERISI_FIX =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/JoeDeRisi-fix.gal").getFile());

    public static final File GPR_3_0_6 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/3_0_6_x.gpr").getFile());

    public static final File GPR_4_0_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/4_0_1_x.gpr").getFile());

    public static final File GPR_4_1_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/4_1_1_x.gpr").getFile());

    public static final File GPR_5_0_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/5_0_1_26.gpr").getFile());

}
