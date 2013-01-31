//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;

import org.junit.Test;

public class DeleteFileTest extends AbstractSeleniumTest {

    @Test
    public void testDeleteFile() throws Exception {
        String title = "test" + System.currentTimeMillis();

        loginAsPrincipalInvestigator();

        createExperiment(title);

        // - Go to the data tab
        selenium.click("link=Data");
        waitForTab();


        // Upload IDF file
        upload(MageTabDataFiles.TCGA_BROAD_IDF);

        // - file is present
        assertTrue(selenium.isTextPresent(MageTabDataFiles.TCGA_BROAD_IDF.getName()));

        // - Delete file
        selenium.click("selectAllCheckbox");
        selenium.click("link=Delete");
        waitForAction();

        // - File is deleted
        assertFalse(selenium.isTextPresent(MageTabDataFiles.TCGA_BROAD_IDF.getName()));
    }

}
