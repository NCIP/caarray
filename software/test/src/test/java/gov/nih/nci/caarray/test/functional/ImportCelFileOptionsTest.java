//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.base.TestProperties;
import gov.nih.nci.caarray.test.data.arraydata.AffymetrixArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * 
 * Tests the import options.
 */
public class ImportCelFileOptionsTest extends AbstractSeleniumTest {

    @Test
    public void testImportCelFileOptions() throws Exception {
        String title = "ImportOptions " + System.currentTimeMillis();
        long startTime = System.currentTimeMillis();
        long endTime = 0;
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        // - Login
        loginAsPrincipalInvestigator();

        // - Add the array design
        importArrayDesign(AffymetrixArrayDesignFiles.TEST3_CDF, TestProperties.getAffymetrixSpecificationDesignName());
        // Create experiment
        createExperiment(title, TestProperties.getAffymetrixSpecificationDesignName());

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        // autocreats the biomaterial chain - Source -> Sample -> Extract -> Labeled Extract -> Hybridization
        optionTest(AUTOCREATE_ANNOTATION_SET, AffymetrixArrayDataFiles.TEST3_CEL, 1, AffymetrixArrayDataFiles.TEST3_CEL
                .getName());
        verifyImport(AffymetrixArrayDataFiles.TEST3_CEL.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CEL.getName().indexOf('.')));
        selenium.click("link=Data");
        waitForTab();

        // Autocreate a single annotation set for a named annotation
        optionTest(AUTOCREATE_SINGLE_ANNOTATION, AffymetrixArrayDataFiles.TEST3_CHP, 1,
                AffymetrixArrayDataFiles.TEST3_CHP.getName());
        verifyImport(AffymetrixArrayDataFiles.TEST3_CHP.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CHP.getName().indexOf('.')));
        selenium.click("link=Data");
        waitForTab();

        // Associates selected file(s) to existing biomaterial or hybridization
        optionTest(ASSOCIATE_TO_EXISTING_BIOMATERIAL, AffymetrixArrayDataFiles.TEST3_CALVIN_CEL, 1,
                AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName());
        verifyImportToExistingBiomaterial(AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName().substring(0,
                AffymetrixArrayDataFiles.TEST3_CALVIN_CEL.getName().lastIndexOf('.')));

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }

    /**
     * Check if the biomaterial got imported
     * 
     * @param string bioMaterialName look for this name on the Source tab
     */
    private void verifyImport(String bioMaterialName) {
        selenium.click("link=Annotations");
        waitForText("Experiment Design Types");
        selenium.click("link=Sources");
        waitForText(bioMaterialName);
        assertTrue("Unable to find " + bioMaterialName + " on the Source tab", selenium.isTextPresent(bioMaterialName));
    }

    private void verifyImportToExistingBiomaterial(String bioMaterialName) {
        String relatedSample = null;
        String source = null;
     
        selenium.click("link=Annotations");
        waitForText("Quality Control Description");
        selenium.click("link=Sources");
        waitForText("Related Samples");
        boolean foundBioMaterialNameInSamplesColumn = false;
        for (int i = 1; i < 3; i++) {
            relatedSample = selenium.getTable("row." + i + ".3");
            source = selenium.getTable("row." + i + ".0");
            // ensure the biomaterial is not in the Source column
            assertFalse(bioMaterialName + " was found as a Source and should not be", source.equalsIgnoreCase(bioMaterialName));
            // check if the bioMaterial is in the Related Samples column
            if (relatedSample.contains(bioMaterialName)){
                foundBioMaterialNameInSamplesColumn = true;
            }
        }
        assertTrue("Unable to find " + bioMaterialName + " in the Related Samples column", foundBioMaterialNameInSamplesColumn);
        
        // Check the files got linked together.  See if two files are available for download
        selenium.click("link=" + DEFAULT_SOURCE_NAME);
        waitForText("Uncompressed Size");
        assertTrue("failed to find file Test3-1-121502.CHP", selenium.isTextPresent("Test3-1-121502.CHP"));
        assertTrue("failed to find file Test3-1-121502.calvin.CEL", selenium.isTextPresent("Test3-1-121502.calvin.CEL"));
    }

    /**
     * Upload the file using the various upload options
     * 
     * @param forThisOption
     * @param thisFile
     * @param numberOfFiles
     * @param lookForThisText
     * @throws IOException
     */
    private void optionTest(int forThisOption, File thisFile, int numberOfFiles, String lookForThisText)
            throws IOException {
        upload(thisFile);
        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, numberOfFiles);
        importData(forThisOption);
        // - click on the Imported data tab and re-click until data
        // - can be found
        // - validate the status
        checkFileStatus("Imported", THIRD_COLUMN, numberOfFiles);
    }
}
