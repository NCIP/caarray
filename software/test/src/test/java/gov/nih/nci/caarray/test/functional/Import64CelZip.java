//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import org.junit.Test;

/**
 * Uploads and deletes 1,2,4,8,16,32,64,128 cell files
 *
 */
public class Import64CelZip extends AbstractSeleniumTest {

    private static final int FIFTY_MINUTES_IN_SECONDS = 900;
    private List<File> zipFiles = new ArrayList<File>();
    public static final String DIRECTORY =
            "L:\\NCICB\\caArray\\QA\\testdata_central_caArray2\\Affymetrix\\HG-U133_Plus_2\\CEL\\Public_Rembrandt_from_caArray1.6\\exponential_CEL_ZIPs\\";

    @Test
    public void testImportAndRetrieval() throws Exception {
        String title = "64 import " + System.currentTimeMillis();
        buildTestData();

        // - Login
        loginAsPrincipalInvestigator();

        // Create project
        createExperiment(title);

        // - go to the data tab
        selenium.click("link=Data");
        waitForTab();

        selenium.click("link=Upload New File(s)");

        // Upload the following files:

        for (File celFile : zipFiles) {
            long startTime = System.currentTimeMillis();
            long endTime = 0;
            ZipFile zipfile = new ZipFile(celFile.getAbsolutePath());
            int numberOfFiles = zipfile.size() - 1;
            System.out.println("Upload of " +celFile.getName()+ " started at " + DateFormat.getTimeInstance().format(new Date()));

            // - Upload the zip file
            upload(celFile, FIFTY_MINUTES_IN_SECONDS);
            checkFileStatus("Uploaded", THIRD_COLUMN, numberOfFiles);
            waitForAction();
            assertTrue(selenium.isTextPresent("file(s) uploaded"));
            endTime = System.currentTimeMillis();
            DecimalFormat df= new DecimalFormat("0.##");
            String totalTime = df.format((endTime - startTime)/60000f);

            // - print out the upload time
            System.out.println("Uploaded " + numberOfFiles + " files ("+celFile.getName()+") in " + totalTime + " minutes");
            startTime = System.currentTimeMillis();

            // - remove the cel files
            delete(celFile);
            endTime = System.currentTimeMillis();
            totalTime = df.format((endTime - startTime)/60000f);

            // - print out the delete time
            System.out.println("Deleted " + celFile.getName()+ " in " + totalTime + " minutes");
        }
    }

    /**
     * @param celFile
     */
    private void delete(File celFile) {
        selenium.click("selectAllCheckbox");
        selenium.click("link=Delete");
        waitForAction();
    }

    private void buildTestData() {
        zipFiles.add(new File(DIRECTORY + "001CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "002CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "004CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "008CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "016CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "032CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "064CEL.zip"));
        zipFiles.add(new File(DIRECTORY + "128CEL.zip"));
    }

}
