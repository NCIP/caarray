//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class GenepixArrayDataFiles {

    public static final File JOE_DERISI_FIX =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/JoeDeRisi-fix.gal").getFile());

    public static final File GPR_3_0_6 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/3_0_6_x.gpr").getFile());

    public static final File GPR_3_0_6_mod =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/3_0_6_mod.gpr").getFile());

    public static final File GPR_4_0_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/4_0_1_x.gpr").getFile());

    public static final File GPR_4_1_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/4_1_1_x.gpr").getFile());

    public static final File BAD_GPR_4_1_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/4_1_1_x.bad.gpr").getFile());

    public static final File GPR_5_0_1 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/5_0_1_26.gpr").getFile());

    public static final File GPR_DEFECT_18652_NEU9 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/defect-18652/21160_neu9.gpr").getFile());

    public static final File GPR_DEFECT_18652_NEU10 =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/defect-18652/21161_neu10.gpr").getFile());

    public static final File GPR_DEFECT_18652_IDF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/defect-18652/experiment-id-1015897540258547.jia.idf").getFile());

    public static final File GPR_DEFECT_18652_SDRF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/defect-18652/experiment-id-1015897540258547_curated.sdrf").getFile());

    public static final File GPR_DEFECT_18652_FIXED_SDRF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/defect-18652/fixed/experiment-id-1015897540258547_curated.sdrf").getFile());

    public static final File EXPORTED_IDF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/exportedGprMageTab.idf").getFile());

    public static final File EXPORTED_SDRF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/exportedGprMageTab.sdrf").getFile());
    
    public static final File SMALL_IDF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/smallGprMageTab.idf").getFile());
    
    public static final File BAD_SMALL_IDF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/smallGprMageTab.bad.idf").getFile());

    public static final File SMALL_SDRF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/smallGprMageTab.sdrf").getFile());

    public static final File BAD_SMALL_SDRF =
        new File(GenepixArrayDataFiles.class.getResource("/arraydata/genepix/smallGprMageTab.bad.sdrf").getFile());

}
