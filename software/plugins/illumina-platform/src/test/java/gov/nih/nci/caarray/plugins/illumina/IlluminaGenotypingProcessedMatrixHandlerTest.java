//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.SdrfTestFiles;
import gov.nih.nci.caarray.util.CaArrayUtils;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * 
 * @author gax
 */
public class IlluminaGenotypingProcessedMatrixHandlerTest extends AbstractServiceTest {
    private ArrayDataServiceTest helper;

    @Test
    public void codeAssumption() {
        assertEquals("new IlluminaGenotypingProcessedMatrixQuantitationType added! revisit validation of this column",
                6, IlluminaGenotypingProcessedMatrixQuantitationType.values().length);
        final Set<DataType> supportedTypes = new HashSet<DataType>(Arrays.asList(DataType.STRING, DataType.FLOAT));
        for (final IlluminaGenotypingProcessedMatrixQuantitationType t : IlluminaGenotypingProcessedMatrixQuantitationType
                .values()) {
            assertTrue("you'll need code to validate columns of type" + t.getDataType(),
                    supportedTypes.contains(t.getDataType()));
            try {
                GenotypingProcessedMatrixHandler.DefaultHeaderProcessor.Header.valueOf(t.name());
            } catch (final IllegalArgumentException e) {
                fail("expects all IlluminaGenotypingProcessedMatrixQuantitationType s to come from a column by the same name"
                        + t.name());
            }
        }
    }

    public static ArrayDesign buildArrayDesign() {
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);

        final Term term = new Term();
        term.setValue("testval");
        term.setCategory(cat);
        term.setSource(ts);

        final Organism organism = new Organism();
        organism.setScientificName("Homo sapiens");
        organism.setTermSource(ts);

        final Organization o = new Organization();
        o.setName("DummyOrganization");
        o.setProvider(true);

        final ArrayDesign design = new ArrayDesign();
        design.setName("foo");
        design.setVersion("99");
        design.setGeoAccession("GPL0001");
        design.setProvider(o);
        final AssayType at1 = new AssayType("Gene Expression");
        design.getAssayTypes().add(at1);
        design.setTechnologyType(term);
        design.setOrganism(organism);

        final ArrayDesignDetails detail = new ArrayDesignDetails();
        design.setDesignDetails(detail);
        PhysicalProbe p = new PhysicalProbe(detail, null);
        p.setName("ILMN_1725881");
        detail.getProbes().add(p);
        p = new PhysicalProbe(detail, null);
        p.setName("ILMN_1910180");
        detail.getProbes().add(p);
        p = new PhysicalProbe(detail, null);
        p.setName("ILMN_1804174");
        detail.getProbes().add(p);
        final CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED);
        f.setFileType(IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
        f.setDataHandle(CaArrayUtils.makeUriQuietly("foo:bar"));
        design.addDesignFile(f);
        return design;
    }

    private void setup() throws Exception {
        this.helper = new ArrayDataServiceTest() {
            @Override
            protected void configurePlatforms(PlatformModule platformModule) {
                platformModule.addPlatform(new IlluminaModule());
            }
        };
        this.helper.setUp();
    }

    @Test
    public void testImportSingleHybPerFile() throws Exception {
        setup();

        final CaArrayFile f =
            this.helper.getDataCaArrayFile(IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB,
                    GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE);
        f.setId(1L);
        final ArrayDesign design = buildArrayDesign();
        f.getProject().getExperiment().getArrayDesigns().add(design);

        final FileValidationResult result = this.helper.arrayDataService.validate(f, null, false);
        assertTrue("validation: " + result.getMessages().toString(), result.isValid());
        this.helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        final Hybridization hyb = f.getProject().getExperiment().getHybridizations().iterator().next();
        final List<HybridizationData> hdl =
            hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        assertEquals(6, hdl.get(0).getColumns().size());
        for (final AbstractDataColumn c : hdl.get(0).getColumns()) {
            int rows;
            try {
                rows = ((StringColumn) c).getValues().length;
            } catch (final ClassCastException e) {
                rows = ((FloatColumn) c).getValues().length;
            }
            assertEquals(3, rows);
        }
    }

    @Test
    public void testImportMultipleHybsPerFile() throws Exception {
        setup();

        final CaArrayFile f =
            this.helper.getDataCaArrayFile(IlluminaArrayDataFiles.ILLUMINA_DERIVED_2_HYB,
                    GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE);
        f.setId(2L);
        final ArrayDesign design = buildArrayDesign();
        f.getProject().getExperiment().getArrayDesigns().add(design);

        final FileValidationResult result = this.helper.arrayDataService.validate(f, null, false);
        assertTrue("validation: " + result.getMessages().toString(), result.isValid());
        this.helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        final Iterator<Hybridization> it = f.getProject().getExperiment().getHybridizations().iterator();
        final Hybridization hyb1 = it.next();
        final Hybridization hyb2 = it.next();
        assertFalse(it.hasNext());
        final List<HybridizationData> hdl1 =
            hyb1.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        final List<HybridizationData> hdl2 =
            hyb2.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(hdl2.size(), hdl1.size());
        assertEquals(hdl2.get(0).getColumns().size(), hdl1.get(0).getColumns().size());
    }

    @Test
    public void testValidationErrors_0() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"", "", "" };
        final String[] messages = {"Missing IlmnID, ID_REF, or ID in first column, first line. (Found )" };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_1() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID" };
        final String[] messages = {"Missing 'Value' (hybridization/sample name) column" };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_2() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID_REF", "MyHyb" };
        final String[] messages = {"Missing Quantitation Type (measurement) column" };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_3() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID_REF", "Hyb-1", "Foo", "Theta" };
        final String[] messages = {"Unsupported Column Foo", };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_4() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID_REF", "Hyb-1", "Foo", "Hyb-2", "Bar" };
        final String[] messages = {"Unsupported Column Foo", "Unsupported Column Hyb-2", "Unsupported Column Bar" };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_5() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.Bar", "Hyb-2", "Hyb-2.Foo", "Hyb-1.Baz" };
        final String[] messages =
        {"Unsupported Column Foo", "Unsupported Column Bar", "Unsupported Column Foo", "Unsupported Column Baz" };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_6() {
        final ArrayDesign design = buildArrayDesign();
        final String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.R", "Hyb-2", "Hyb-2.Foo", "Hyb-2.R" };
        final String[] messages = {"Unsupported Column Foo", "Unsupported Column Foo" };
        final GenotypingProcessedMatrixHandler.DefaultHeaderProcessor proc =
            processHeader(header, messages, null, design);
        assertEquals(2, proc.getLoaders().size());
    }

    @Test
    public void testValidationErrors_SDRF() {
        final ArrayDesign design = buildArrayDesign();
        final Set<String> sdrf = Collections.singleton("Hyb-1");
        final String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.R", "Hyb-2", "Hyb-2.Foo", "Hyb-2.R" };
        final String[] messages =
        {"Unsupported Column Foo", "Unsupported Column Foo", "Hybridization Hyb-2 is not referenced in SDRF" };
        final AbstractHeaderParser proc = processHeader(header, messages, sdrf, design);
    }

    static private GenotypingProcessedMatrixHandler.DefaultHeaderProcessor processHeader(String[] headerColumns,
            String[] messages, Set<String> sdrf, ArrayDesign design) {
        final FileValidationResult result = new FileValidationResult();
        MageTabDocumentSet docSet = null;
        if (sdrf != null) {
            final MageTabFileSet fset = new MageTabFileSet();
            docSet = new MageTabDocumentSet(fset);
            final SdrfDocument doc = new SdrfDocument(docSet, new JavaIOFileRef(SdrfTestFiles.MULTI_DERIVED_1_IDF));
            for (final String hn : sdrf) {
                final gov.nih.nci.caarray.magetab.sdrf.Hybridization h =
                    new gov.nih.nci.caarray.magetab.sdrf.Hybridization();
                h.setName(hn);
                docSet.getSdrfHybridizations().put(hn, Collections.singletonList(h));
            }
            docSet.getSdrfDocuments().add(doc);
        }
        final GenotypingProcessedMatrixHandler.ValidatingHeaderParser proc =
            new GenotypingProcessedMatrixHandler.ValidatingHeaderParser(result, docSet);
        proc.parse(Arrays.asList(headerColumns), 1);
        final List<String> l = new ArrayList<String>(Arrays.asList(messages));
        for (final ValidationMessage m : result.getMessages()) {
            assertTrue("unexpected message " + m.getMessage(), l.remove(m.getMessage()));
        }
        assertTrue("expected messages " + l.toString(), l.isEmpty());
        return proc;
    }
}
