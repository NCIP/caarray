//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class NimblegenArrayDesignFiles {

    public static final File[] CGH_DESIGN = new File[] {
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/090210_HG18_WG_CGH_v3.1_HX3.ndf").getFile()),
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/090210_HG18_WG_CGH_v3.1_HX3.pos").getFile()),
    };

    public static final File[] PROMOTER_DESIGN = new File[] {
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/HG18_Deluxe_Promoter_HX1.ndf").getFile()),
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/HG18_Deluxe_Promoter_HX1.pos").getFile()),
    };

    public static final File[] EXPRESSION_DESIGN = new File[] {
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/2006-08-03_HG18_60mer_expr.ndf").getFile()),
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/2006-08-03_HG18_60mer_expr.ngd").getFile()),
    };

    public static final File SHORT_EXPRESSION_DESIGN = 
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/2006-08-03_HG18_60mer_expr-short.ndf").getFile());

    public static final File SHORT_PROMOTER_DESIGN = 
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/HG18_Deluxe_Promoter_HX1-short.ndf").getFile());

    public static final File SHORT_CGH_DESIGN =
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/090210_HG18_WG_CGH_v3.1_HX3-short.ndf").getFile());
        
    public static final File MISSING_HEADER_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/missing-header.ndf").getFile());

    public static final File MISSING_COLUMNS_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/missing-columns.ndf").getFile());



}
