//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.data.arraydesign;

import static gov.nih.nci.caarray.test.data.ResourceFiles.getResourceFile;

import java.io.File;

public class AgilentArrayDesignFiles {
    public static final File TEST_ACGH_XML =
        getResourceFile("/arraydesign/agilent/aCGH/022522_D_F_20090107.xml");
    
    public static final File TEST_SHORT_ACGH_XML =
            getResourceFile("/arraydesign/agilent/aCGH/022522_D_F_20090107.short.xml");
        
    public static final File TEST_SHORT_ACGH_XML_ERRORS =
            getResourceFile("/arraydesign/agilent/aCGH/022522.short.witherrors.xml");
        
    public static final File TEST_GENE_EXPRESSION_1_XML =
            getResourceFile("/arraydesign/agilent/gene_expression/015354_D_20061130.xml");
        
    public static final File TEST_GENE_EXPRESSION_1_REDUCED_XML =
            getResourceFile("/arraydesign/agilent/gene_expression/015354_reduced.xml");
        
    public static final File TEST_GENE_EXPRESSION_2_XML =
        getResourceFile("/arraydesign/agilent/gene_expression/024656_D_F_20090719.xml");
    
    public static final File TEST_GENE_EXPRESSION_3_XML =
        getResourceFile("/arraydesign/agilent/gene_expression/Whole_Human_Genome_Microarray_4x44K.xml");
    
    public static final File TEST_MIRNA_1_XML =
        getResourceFile("/arraydesign/agilent/miRNA/Human_miRNA_Microarray_3.0.xml");

    public static final File TEST_MIRNA_1_XML_SMALL =
        getResourceFile("/arraydesign/agilent/miRNA/Human_miRNA_Microarray_3.0-small.xml");

    public static final File TEST_MIRNA_2_XML =
        getResourceFile("/arraydesign/agilent/miRNA/Mouse_miRNA_Microarray_2.0.xml");
}
