//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.data.arraydata;

import static gov.nih.nci.caarray.test.data.ResourceFiles.getResourceFile;

import java.io.File;

public class AgilentArrayDataFiles {
    public static final File TEST_ACGH_RAW_TEXT =
        getResourceFile("/arraydata/agilent/US22502676_252252210158_S01_CGH_105_Dec08_1_4.txt");

    public static final File MAGE_TAB_1_IDF = getResourceFile("/arraydata/agilent/mageTab1.idf");

    public static final File MAGE_TAB_1_SDRF = getResourceFile("/arraydata/agilent/mageTab1.sdrf");

    public static final File TINY_RAW_TEXT = getResourceFile("/arraydata/agilent/Agilent_Tiny.txt");

    public static final File TINY_IDF = getResourceFile("/arraydata/agilent/Agilent_Tiny.idf");

    public static final File TINY_SDRF = getResourceFile("/arraydata/agilent/Agilent_Tiny.sdrf");

    public static final File GENE_EXPRESSION = getResourceFile("/arraydata/agilent/gene-expression.txt");

    public static final File BAD_GENE_EXPRESSION = getResourceFile("/arraydata/agilent/gene-expression.bad.txt");

    public static final File MIRNA = getResourceFile("/arraydata/agilent/miRNA.txt");

    public static final File MIRNA_BLANKS = getResourceFile("/arraydata/agilent/miRNA_blanks.txt");

    public static final File MIRNA_BLANKS_IDF = getResourceFile("/arraydata/agilent/miRNA_blanks.idf");
    
    public static final File MIRNA_BLANKS_SRDF = getResourceFile("/arraydata/agilent/miRNA_blanks.sdrf");
    
    public static final File ERRONEOUS_LINE_ENDINGS = getResourceFile("/arraydata/agilent/erroneous_line_endings.txt");
}
