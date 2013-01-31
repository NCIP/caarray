//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import java.io.File;

public class AffymetrixArrayDataFiles {

    public static final File TEST3_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3-1-121502.CEL").getFile());

    public static final File TEST3_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3-1-121502.CHP").getFile());

    public static final File TEST3_CALVIN_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3-1-121502.calvin.CEL").getFile());

    public static final File HG_FOCUS_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/HG-Focus-1-121502.cel").getFile());

    public static final File HG_FOCUS_CALVIN_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/HG-Focus-1-121502.calvin.cel").getFile());

    public static final File HG_FOCUS_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/HG-Focus-1-121502.CHP").getFile());

    public static final File HG_FOCUS_CALVIN_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/HG-Focus-1-121502.calvin.CHP").getFile());

    public static final File TEN_K_1_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/10k_1.CHP").getFile());

    public static final File TEN_K_1_CALVIN_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/10k_1-calvin.CHP").getFile());

    public static final File TEST_HG_U133_PLUS_2_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test_HG-U133_Plus_2.CEL").getFile());

    public static final File TEST3_CALVIN_CHP =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3-1-121502.calvin.CHP").getFile());

    public static final File TEST3_INVALID_HEADER_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3_invalid_header.CEL").getFile());

    public static final File TEST3_INVALID_DATA_CEL =
        new File(AffymetrixArrayDataFiles.class.getResource("/arraydata/affymetrix/Test3_invalid_data.CEL").getFile());

    public static final File TEST3_SPECIFICATION_ZIP =
        new File(AffymetrixArrayDataFiles.class.getResource("/magetab/specification/specification.zip").getFile());
}
