//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

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
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.NimblegenArrayDesignFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file import of nimblegen data files
 * 
 * @author dkokotov, jscott
 */
public class NimblegenFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new NimblegenModule());
    }

    @Test
    public void testNimblegenPairImport() throws Exception {
        importDesignAndDataFilesIntoProject(NimblegenArrayDesignFiles.SHORT_EXPRESSION_DESIGN,
                PairDataHandler.NORMALIZED_PAIR_FILE_TYPE, NimblegenArrayDataFiles.SHORT_HUMAN_EXPRESSION);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();

        final ParsedDataPersister pdp = this.injector.getInstance(ParsedDataPersister.class);

        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(3, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        for (final HybridizationData hd : hdl) {
            assertEquals(5, hd.getColumns().size());
            final List<String> intColumns =
                    new ArrayList<String>(Arrays.asList(NimblegenQuantitationType.MATCH_INDEX.getName(),
                            NimblegenQuantitationType.X.getName(), NimblegenQuantitationType.Y.getName()));
            final List<String> floatColumns =
                    new ArrayList<String>(Arrays.asList(NimblegenQuantitationType.MM.getName(),
                            NimblegenQuantitationType.PM.getName()));
            for (final AbstractDataColumn c : hd.getColumns()) {
                pdp.loadFromStorage(c);
                final String name = c.getQuantitationType().getName();
                if (intColumns.contains(name)) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            intColumns.remove(c.getQuantitationType().getName()));
                } else if (floatColumns.contains(name)) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            floatColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", intColumns.isEmpty() && floatColumns.isEmpty());
        }
        tx.commit();
    }
}
