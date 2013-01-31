//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class AffymetrixArrayDesignFiles {

    public static final File TEST3_CDF =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/Test3.CDF").getFile());

    public static final File HG_U133_PLUS_2_CDF =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HG-U133_Plus_2.cdf").getFile());

    public static final File HT_HG_U133A_CDF =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HT_HG-U133A.cdf").getFile());

    public static final File HG_FOCUS_CDF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HG-Focus.CDF").getFile());

    public static final File TEN_K_CDF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/Mapping10K_Xba131-xda.CDF").getFile());

    public static final File HUEX_1_0_PGF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HuEx-1_0-st-v2.r2.pgf").getFile());

    public static final File HUEX_1_0_CLF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HuEx-1_0-st-v2.r2.clf").getFile());

    public static final File HUEX_TEST_PGF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HuEx-test.pgf").getFile());

    public static final File HUEX_TEST_CLF  =
        new File(AffymetrixArrayDesignFiles.class.getResource("/arraydesign/affymetrix/HuEx-test.clf").getFile());

}
