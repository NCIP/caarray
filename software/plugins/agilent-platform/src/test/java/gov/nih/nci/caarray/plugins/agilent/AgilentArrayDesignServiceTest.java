//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydesign.AgilentArrayDesignFiles;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;
import java.util.Collections;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Agilent-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of
 * ArrayDesignService.
 * 
 * @author dkokotov
 */
public class AgilentArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new AgilentModule());
    }

    // The @Ignored tests can be enabled for local testing if a specific parsing bug is identified.
    // They should be disabled for checked-in builds due to the build-time penalty.
    
    @Test
    @Ignore(value = "Full File Takes > 2 min - not a unit test")
    public void importAgilentGelmFull() {
        importDesign(AgilentArrayDesignFiles.TEST_ACGH_XML, 180880, 177071);
    }
    
    @Test
    public void importAgilentGelmShort() {
        importDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML, 12, 4);        
    }
    
    @Test
    @Ignore(value = "Full files takes > 20s - not a unit test")
    public void importAgilentGeneExpressionFull() {
        importDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_XML, 45220, 21536);
    }

    @Test
    public void importAgilentGeneExpressionReduced() {
        importDesign(AgilentArrayDesignFiles.TEST_GENE_EXPRESSION_1_REDUCED_XML, 5, 4);
    }

    @Test
    @Ignore(value = "Full file not a unit test")
    public void importAgilentMiRNA() {
        importDesign(AgilentArrayDesignFiles.TEST_MIRNA_1_XML, 15744, 2735);
    }
    
    @Test
    public void importAgilentMiRNASmall() {
        importDesign(AgilentArrayDesignFiles.TEST_MIRNA_1_XML_SMALL, 5, 5);
    }
    
    private void importDesign(File file, int features, int probes) {
        final CaArrayFile designFile = getAgilentGelmCaArrayFile(file);
        final ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);

        this.arrayDesignService.importDesign(design);

        String designName = file.getName().substring(0, file.getName().lastIndexOf('.'));
        assertEquals(designName, design.getName());
        assertEquals("Agilent.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals(designName, design.getLsidObjectId());

        this.arrayDesignService.importDesignDetails(design);

        // Feature count verified via:
        // $ grep \<feature [[FILENAME]] | wc -l
        assertEquals(features, design.getNumberOfFeatures().intValue());
        assertEquals(features, design.getDesignDetails().getFeatures().size());
        // Probe count verified via:
        // $ grep \<reporter [[FILENAME]] | sed -e 's/.*name\="\([^\"]*\).*/\1/g;' | uniq | wc -l
        assertEquals(probes, design.getDesignDetails().getProbes().size());
        assertEquals(0, design.getDesignDetails().getLogicalProbes().size());

        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        //this will be addressed with Jira issue:
        //[ARRAY-1698] need to fix incorrect probe groups return in ArrayDesignServiceTest.testImportDesign_AgilentGelm() test case.
        //assertEquals("The number of probe groups in incorrect.", 2, design.getDesignDetails().getProbeGroups().size());
    }


    @Test
    @Ignore(value = "Full file takes > 1 min - not a unit test")
    public void validateAgilentGelmFull() {
        validateAgilentGelmDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML);
    }

    @Test
    public void validateAgilentGelmShort() {
        validateAgilentGelmDesign(AgilentArrayDesignFiles.TEST_SHORT_ACGH_XML);
    }

    private void validateAgilentGelmDesign(File xmlFile) {
        final CaArrayFile designFile = getAgilentGelmCaArrayFile(xmlFile);
        final ValidationResult result = this.arrayDesignService.validateDesign(Collections.singleton(designFile));
        assertTrue(result.isValid());
    }

    private CaArrayFile getAgilentGelmCaArrayFile(File file) {
        return getCaArrayFile(file, AgilentXmlDesignFileHandler.XML_FILE_TYPE);
    }
}
