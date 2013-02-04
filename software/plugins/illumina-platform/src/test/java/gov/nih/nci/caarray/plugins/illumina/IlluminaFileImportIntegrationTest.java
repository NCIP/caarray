//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class IlluminaFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new IlluminaModule());
    }

    @Test
    @Ignore("Large test takes >1min, does not add to coverage.")
    public void testIlluminaCsvDataImport() throws Exception {
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.HUMAN_WG6_CSV, 
                CsvDataHandler.DATA_CSV_FILE_TYPE, IlluminaArrayDataFiles.HUMAN_WG6_SMALL);
        
        final List<String> intColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
        final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                        SampleProbeProfileQuantitationType.DETECTION.getName()));

        assertImportCorrect(10, 19, 4, intColumns, floatColumns, Collections.<String>emptyList());
    }

    @Test
    public void testIlluminaSampleProbeProfileImport() throws Exception {
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.MOUSE_REF_8,
                SampleProbeProfileHandler.SAMPLE_PROBE_PROFILE_FILE_TYPE, IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE);
        
        final List<String> intColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.NARRAYS.getName(),
                        SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                        SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                        SampleProbeProfileQuantitationType.DETECTION.getName()));

        assertImportCorrect(3, 16, 8, intColumns, floatColumns, Collections.<String>emptyList());
    }

    @Test
    public void testIlluminaGenotypingProcessedMatrixImport() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();
        ArrayDesign design = IlluminaGenotypingProcessedMatrixHandlerTest.buildArrayDesign();
        this.hibernateHelper.getCurrentSession().save(design.getAssayTypes().iterator().next());
        this.hibernateHelper.getCurrentSession().save(design);
        tx.commit();

        importDataFilesIntoProject(design, GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE,
                IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB);
        
        final List<String> stringColumns =
                new ArrayList<String>(Arrays.asList(IlluminaGenotypingProcessedMatrixQuantitationType.ALLELE.getName()));
        final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(
                        IlluminaGenotypingProcessedMatrixQuantitationType.B_ALLELE_FREQ.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.GC_SCORE.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.LOG_R_RATIO.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.R.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.THETA.getName()));
        assertImportCorrect(3, 1, 6, Collections.<String>emptyList(), floatColumns, stringColumns);
    }

    private void assertImportCorrect(int numberOfProbes, int hybridizationDataListSize,
            int hybridizationColumns, List<String> intColumns, List<String> floatColumns, List<String> stringColumns) {
        final ParsedDataPersister pdp = this.injector.getInstance(ParsedDataPersister.class);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        final List<AbstractDesignElement> l =
            hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
            .getDesignElements();
        assertEquals(numberOfProbes, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
            hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(hybridizationDataListSize, hdl.size());
        for (final HybridizationData hd : hdl) {
            List<String> hybIntColumns = new ArrayList<String>();
            List<String> hybFloatColumns = new ArrayList<String>();
            List<String> hybStringColumns = new ArrayList<String>();
            hybIntColumns.addAll(intColumns);
            hybFloatColumns.addAll(floatColumns);
            hybStringColumns.addAll(stringColumns);
            assertEquals(hybridizationColumns, hd.getColumns().size());
            for (final AbstractDataColumn c : hd.getColumns()) {
                pdp.loadFromStorage(c);
                final String name = c.getQuantitationType().getName();
                if (hybIntColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybIntColumns.remove(c.getQuantitationType().getName()));
                } else if (hybFloatColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybFloatColumns.remove(c.getQuantitationType().getName()));
                } else if (hybStringColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((StringColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybStringColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", hybIntColumns.isEmpty() 
                    && hybFloatColumns.isEmpty() && hybStringColumns.isEmpty());
        }
        tx.commit();
    }
}
