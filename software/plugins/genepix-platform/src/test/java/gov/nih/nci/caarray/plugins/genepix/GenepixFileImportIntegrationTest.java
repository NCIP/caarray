//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.genepix;

import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.NORMALIZE;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;

import java.util.List;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file import of genepix data files
 * 
 * @author dkokotov, jscott
 */
public class GenepixFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new GenepixModule());
    }

    @Test
    public void testGenepixGprImport() throws Exception {
        importDesignAndDataFilesIntoProject(GenepixArrayDataFiles.JOE_DERISI_FIX, GprHandler.GPR_FILE_TYPE,
                GenepixArrayDataFiles.SMALL_IDF, GenepixArrayDataFiles.SMALL_SDRF, GenepixArrayDataFiles.GPR_4_1_1);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();

        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(6528, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());

        final GenepixQuantitationType[] expectedTypes =
                new GenepixQuantitationType[] {X, Y, DIA, F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN,
                        B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT, F532_MEDIAN, F532_MEAN,
                        F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD,
                        F532_PERCENT_SAT, RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532,
                        MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532, RGN_R2_635_532, F_PIXELS,
                        B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532, F635_MEDIAN_B635,
                        F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, F635_TOTAL_INTENSITY, F532_TOTAL_INTENSITY,
                        SNR_635, SNR_532, FLAGS, NORMALIZE };
        for (final HybridizationData hd : hdl) {
            assertEquals(expectedTypes.length, hd.getColumns().size());
            for (int i = 0; i < expectedTypes.length; i++) {
                assertEquals(expectedTypes[i].getName(), hd.getColumns().get(i).getQuantitationType().getName());
            }
        }
        tx.commit();
    }
}
