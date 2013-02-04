//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.functional;

import gov.nih.nci.caarray.test.base.AbstractSeleniumTest;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;

import java.io.File;
import java.io.FileFilter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipFile;

import org.junit.Test;

/**
 * Test case .
 * 
 * Import GSK data
 * 
 */
public class GSKImport extends AbstractSeleniumTest {

    private static String fileExtention = ".zip";

    @Test
    public void testGskImport() throws Exception {
        int numberOfFiles = 0;
        File MAPPING250K_NSP = new File(
                "L:\\NCICB\\caArray\\caArray Files\\Affymetrix_CDFs\\500kSNP\\Mapping250K_Nsp.cdf");
        File MAPPING250K_STY = new File(
                "L:\\NCICB\\caArray\\caArray Files\\Affymetrix_CDFs\\500kSNP\\Mapping250K_Sty.cdf");
        File gskDataDirectory = null;
        String[] directory = { "L:\\NCICB\\caArray\\GSK_data\\groupedZips\\dna",
                "L:\\NCICB\\caArray\\GSK_data\\groupedZips\\rna\\cel" };
        final String MAPPING_250K_NSP_NAME = MAPPING250K_NSP.getName().substring(0,
                MAPPING250K_NSP.getName().indexOf('.'));
        final String MAPPING_250K_STY_NAME = MAPPING250K_STY.getName().substring(0,
                MAPPING250K_STY.getName().indexOf('.'));
        final String HT_HG_U133A_CDF_NAME = AffymetrixArrayDesignFiles.HT_HG_U133A_CDF.getName().substring(0,
                AffymetrixArrayDesignFiles.HT_HG_U133A_CDF.getName().indexOf('.'));
        String HG_U133_PLUS_2_CDF_NAME = AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF.getName().substring(0,
                AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF.getName().indexOf('.'));
        ArrayList<String> arrayDesigns = new ArrayList<String>();

        arrayDesigns.add(MAPPING_250K_NSP_NAME);
        arrayDesigns.add(MAPPING_250K_STY_NAME);
        arrayDesigns.add(HT_HG_U133A_CDF_NAME);
        arrayDesigns.add(HG_U133_PLUS_2_CDF_NAME);

        long startTime = System.currentTimeMillis();
        long endTime = 0;
        String title = "GSK Import" + System.currentTimeMillis();
        System.out.println("Started at " + DateFormat.getTimeInstance().format(new Date()));

        loginAsPrincipalInvestigator();

        // - import array designs
        importArrayDesign(MAPPING250K_NSP, MAPPING_250K_NSP_NAME);
        importArrayDesign(MAPPING250K_STY, MAPPING_250K_STY_NAME);
        importArrayDesign(AffymetrixArrayDesignFiles.HT_HG_U133A_CDF, HT_HG_U133A_CDF_NAME);
        importArrayDesign(AffymetrixArrayDesignFiles.HG_U133_PLUS_2_CDF, HG_U133_PLUS_2_CDF_NAME);
        // - Create Experiment
        createExperiment(title, arrayDesigns);

        // - go to the data tab
        this.selenium.click("link=Data");
        waitForTab();
        for (int i = 0; i < directory.length; i++) {
            FileFilter celFilter = new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(fileExtention);
                }
            };
            gskDataDirectory = new File(directory[i]);
            for (File aZipFile : gskDataDirectory.listFiles(celFilter)) {
                ZipFile zipfile = new ZipFile(aZipFile.getAbsolutePath());
                numberOfFiles += zipfile.size();
                upload(aZipFile);
            }
        }

        // - Check if they are uploaded
        checkFileStatus("Uploaded", THIRD_COLUMN, numberOfFiles);

        // - Import files
        importData(AUTOCREATE_ANNOTATION_SET);

        // - click on the Imported data tab
        selenium.click("link=Import Data");

        checkFileStatus("Imported", SECOND_COLUMN, numberOfFiles);

        endTime = System.currentTimeMillis();
        String totalTime = df.format((endTime - startTime) / 60000f);
        System.out.println("total time = " + totalTime);
    }
}
