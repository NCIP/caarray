//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class NimblegenArrayDesignFiles {
    public static final File SHORT_EXPRESSION_DESIGN = 
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/2006-08-03_HG18_60mer_expr-short.ndf").getFile());

    public static final File FULL_EXPRESSION_DESIGN = 
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/2006-08-03_HG18_60mer_expr.ndf").getFile());

    public static final File SHORT_CGH_DESIGN =
        new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/090210_HG18_WG_CGH_v3.1_HX3-short.ndf").getFile());
        
    public static final File MISSING_HEADER_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/missing-header.ndf").getFile());

    public static final File MISSING_COLUMNS_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/missing-columns.ndf").getFile());
    
    public static final File INCOMPLETE_ROW_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/incomplete-row.ndf").getFile());
    
    public static final File MISSING_COLUMN_VALUE_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/missing-column-value.ndf").getFile());

    public static final File INVALID_COLUMN_VALUE_NDF 
        = new File(NimblegenArrayDesignFiles.class.getResource("/arraydesign/nimblegen/invalid-column-value.ndf").getFile());

}
