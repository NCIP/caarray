//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import java.io.File;

public class GenepixArrayDesignFiles {

    public static final File DEMO_GAL =
        new File(GenepixArrayDesignFiles.class.getResource("/arraydesign/genepix/Demo.gal").getFile());

    public static final File TWO_K_GAL =
        new File(GenepixArrayDesignFiles.class.getResource("/arraydesign/genepix/2K_Hs-ATC7_6k-v5px_16Bx22Cx22R.gal").getFile());

    public static final File MEEBO =
        new File(GenepixArrayDesignFiles.class.getResource("/arraydesign/genepix/MEEBO.GAL").getFile());

    public static final File INCYTE =
        new File(GenepixArrayDesignFiles.class.getResource("/arraydesign/genepix/Mm-Incyte-v1px_16Bx24Cx23R.gal").getFile());
}
