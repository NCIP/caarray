//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.arraydesign.AbstractArrayDesignServiceTest;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;

import java.io.File;

import org.junit.Test;

/**
 * Nimblegen-specific tests of ArrayDesignService. Temporary - need to factor our to tests independent of
 * ArrayDesignService.
 * 
 * @author dkokotov
 */
public class NdfArrayDesignServiceTest extends AbstractArrayDesignServiceTest {
    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new NimblegenModule());
    }

    @Test
    public void testImportDesign_NimblegenTestExpression() throws Exception {
        final CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN);
        final ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        assertEquals("2006-08-03_HG18_60mer_expr-short", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("2006-08-03_HG18_60mer_expr-short", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(99, design.getDesignDetails().getProbes().size());
        assertEquals(98, design.getDesignDetails().getLogicalProbes().size());
    }

    @Test
    public void testImportDesign_NimblegenTestCGH() throws Exception {
        final CaArrayFile designFile = getNimblegenCaArrayFile(NimblegenArrayDesignFiles.SHORT_CGH_DESIGN);
        final ArrayDesign design = new ArrayDesign();
        design.addDesignFile(designFile);
        this.arrayDesignService.importDesign(design);
        this.arrayDesignService.importDesignDetails(design);
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getName());
        assertEquals("nimblegen.com", design.getLsidAuthority());
        assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
        assertEquals("090210_HG18_WG_CGH_v3.1_HX3-short", design.getLsidObjectId());
        assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
        assertEquals(4999, design.getDesignDetails().getProbes().size());
        assertEquals(41, design.getDesignDetails().getLogicalProbes().size());
    }

    private CaArrayFile getNimblegenCaArrayFile(File file) {
        return getCaArrayFile(file, NdfHandler.NDF_FILE_TYPE);
    }
}
