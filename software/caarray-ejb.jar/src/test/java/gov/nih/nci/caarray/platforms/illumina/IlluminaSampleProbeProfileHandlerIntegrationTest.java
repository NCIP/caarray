package gov.nih.nci.caarray.platforms.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.magetab.SdrfTestFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Test;

/**
 *
 * @author gax
 */
public class IlluminaSampleProbeProfileHandlerIntegrationTest extends AbstractServiceIntegrationTest {
    ArrayDataServiceTest helper = new ArrayDataServiceTest();
    Map<String, ArrayDesign> designMap = new HashMap<String, ArrayDesign>();

    ArrayDesign buildArrayDesign(String name) {
        ArrayDesign design = new ArrayDesign();
        design.setName(name);
        design.setVersion("99");
        design.setProvider(new Organization());
        design.getProvider().setName("Illumina");
        ArrayDesignDetails detail = new ArrayDesignDetails();
        design.setDesignDetails(detail);
        PhysicalProbe p = new PhysicalProbe(detail, null);
        p.setName("0610005I04");
        detail.getProbes().add(p);
        p = new PhysicalProbe(detail, null);
        p.setName("0610006I08RIK");
        detail.getProbes().add(p);
        p = new PhysicalProbe(detail, null);
        p.setName("0610007P14RIK");
        detail.getProbes().add(p);
        CaArrayFile f = new CaArrayFile();
        f.setFileType(FileType.ILLUMINA_DESIGN_BGX);
        design.getDesignFiles().add(f);
        return design;
    }

    private ArrayDesign getOrCreateDesign(String name) {
        ArrayDesign ad = designMap.get(name);
        if (ad == null) {
            ad = buildArrayDesign(name);
            designMap.put(name, ad);
        }
        return ad;
    }

    private void setup() throws Exception {
        helper = new ArrayDataServiceTest() {
            @Override
            protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                return getOrCreateDesign(lsidObjectId);
            }
        };
        helper.setUp();
    }

    @Test
    public void test1() throws Exception {
        Transaction tx = hibernateHelper.beginTransaction();
        setup();

        CaArrayFile f = helper.getDataCaArrayFile(IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE, FileType.ILLUMINA_SAMPLE_PROBE_PROFILE_TXT);
        f.setId(1L);
        ArrayDesign design = getOrCreateDesign("test-design.bgx.txt");
        // hack: set the id, because otherwise two designs can never be equals
        design.setId(1L);
        f.getProject().getExperiment().getArrayDesigns().add(design);
        CaArrayDaoFactory.INSTANCE.getSearchDao().save(f);

        FileValidationResult result = helper.arrayDataService.validate(f, null);
        assertTrue("validation: " + result.getMessages().toString(), result.isValid());
        helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        Hybridization hyb = f.getProject().getExperiment().getHybridizations().iterator().next();
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(3, l.size());
        for (AbstractDesignElement de : l) {
            PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(16, hdl.size());
        for (HybridizationData hd : hdl){
            assertEquals(8, hd.getColumns().size());
            List<String> expected = new ArrayList<String>(Arrays.asList(
                    SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                    SampleProbeProfileQuantitationType.NARRAYS.getName(),
                    SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                    SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                    SampleProbeProfileQuantitationType.AVG_NBEADS.getName(),
                    SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (AbstractDataColumn c : hd.getColumns()) {
                assertEquals(3, ((FloatColumn)c).getValues().length);
                assertTrue("missing " + c.getQuantitationType(), expected.remove(c.getQuantitationType().getName()));
            }
            assertTrue("expected "+expected, expected.isEmpty());
        }
    }

    @Test
    public void testValidationErrors_0() {
        String[] header = {"", "", ""};
        String [] messages = {"No samples with quantitation type AVG_SIGNAL found",
                "No samples found"
        };
        processHeader(header, messages, null, null);
    }

    @Test
    public void testValidationErrors_1() {
        String[] header = {"Probe_Id", "Foo", "Hyb1.MIN_Signal", "Hyb1.AVG_Signal"};
        String [] messages = {
            "Missing quantitation type(s) [DETECTION] for sample Hyb1",
            "Using column PROBE_ID with design type ILLUMINA_DESIGN_CSV",
            "Ignored column Foo"
        };
        processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_CSV);
    }

    @Test
    public void testValidationErrors_2() {
        String[] header = {"TargetId", "Foo", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal"};
        String [] messages = {"Using column TARGETID with design type ILLUMINA_DESIGN_BGX", "Ignored column Foo"};
        processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
    }

    @Test
    public void testValidationErrors_3() {
        String[] header = {"ID_REF", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal", "DETECTION PVAL-Hyb2", "AVG_Signal-Hyb2"};
        String [] messages = {
            "Mixed column name formats",
            "Inconsistent quantitation type column between samples Hyb1 and Hyb2",
            "Missing quantitation type(s) [AVG_SIGNAL, DETECTION] for sample Hyb2",
            "Ignored column DETECTION PVAL-Hyb2",
            "Ignored column AVG_Signal-Hyb2"};
        processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
    }

    @Test
    public void testValidationErrors_4() {
        String[] header = {"ID_REF", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal", "Hyb2.DETECTION PVAL", "Hyb2.AVG_Signal", "Hyb2.Foo"};
        String [] messages = { "Unsupported column Hyb2.Foo", "Ignored column Hyb2.Foo" };
        SampleProbeProfileHandler.ValidatingHeaderParser p = processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
        assertEquals(2, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        assertTrue(p.getHybNames().contains("Hyb2"));
        assertTrue(p.getLoaders().get(0).getQTypes().containsAll(p.getLoaders().get(1).getQTypes()));
        assertTrue(p.getLoaders().get(1).getQTypes().containsAll(p.getLoaders().get(0).getQTypes()));
    }

    @Test
    public void testValidationErrors_5() {
        String[] header = {"ID_REF"};
        String [] messages = {"No samples found" , "No samples with quantitation type AVG_SIGNAL found"};
        SampleProbeProfileHandler.ValidatingHeaderParser p = processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
        assertEquals(0, p.getHybNames().size());
        assertTrue(p.getHybNames().isEmpty());
    }

    @Test
    public void testInterleavedColumns() {
        String[] header = { "Hyb2.AVG_Signal", "Hyb1.DETECTION PVAL", "ID_REF", "Hyb1.AVG_Signal", "Hyb2.DETECTION PVAL",};
        String [] messages = {};
        SampleProbeProfileHandler.ValidatingHeaderParser p = processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
        assertEquals(2, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        assertTrue(p.getHybNames().contains("Hyb2"));
        assertTrue(p.getLoaders().get(0).getQTypes().containsAll(p.getLoaders().get(1).getQTypes()));
        assertTrue(p.getLoaders().get(1).getQTypes().containsAll(p.getLoaders().get(0).getQTypes()));
        assertEquals(5, p.getRowWidth());
    }

    @Test
    public void testValidationErrors_AllQTypes() {
        String[] header = { "ID_REF", //  1
            "Hyb1.MIN_Signal",        //  2
            "Hyb1.AVG_Signal",        //  3
            "Hyb1.MAX_Signal",        //  4
            "Hyb1.NARRAYS",           //  5
            "Hyb1.ARRAY_STDEV",       //  6
            "Hyb1.BEAD_STDEV",        //  7
            "Hyb1.Avg_NBEADS",        //  8
            "Hyb1.Detection",         //  9
            "Hyb1.Detection Pval",    // 10  dup of Detection
            "Hyb1.DetectionPval",     // 11  dup of Detection
        };
        String [] messages = {
            "QuantitationType DETECTION already defined in column 9",// dup "Hyb1.Detection Pval"
            "QuantitationType DETECTION already defined in column 9",// dup "Hyb1.DetectionPval"
        };
        SampleProbeProfileHandler.ValidatingHeaderParser p = processHeader(header, messages, null, FileType.ILLUMINA_DESIGN_BGX);
        assertEquals(1, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        final EnumSet<SampleProbeProfileQuantitationType> expected = EnumSet.allOf(SampleProbeProfileQuantitationType.class);
        final List<SampleProbeProfileQuantitationType> result = p.getLoaders().get(0).getQTypes();
        List<SampleProbeProfileQuantitationType> diff = new ArrayList<SampleProbeProfileQuantitationType>(result);
        diff.removeAll(expected);
        assertTrue("unexpected qtypes " + diff, diff.isEmpty());
        diff.clear();
        diff.addAll(expected);
        diff.removeAll(result);
        assertTrue("expected to find " + diff, diff.isEmpty());
    }

    static private SampleProbeProfileHandler.ValidatingHeaderParser processHeader(String[] headerColumns, String[] messages, Set<String> sdrf, FileType designType) {
        FileValidationResult result = new FileValidationResult(null);
        MageTabDocumentSet docSet = null;
        if (sdrf != null){
            MageTabFileSet fset = new MageTabFileSet();
            docSet = new MageTabDocumentSet(fset);
            SdrfDocument doc = new SdrfDocument(docSet, new JavaIOFileRef(SdrfTestFiles.MULTI_DERIVED_1_IDF));
            for (String hn : sdrf) {
                gov.nih.nci.caarray.magetab.sdrf.Hybridization h = new gov.nih.nci.caarray.magetab.sdrf.Hybridization();
                h.setName(hn);
                docSet.getSdrfHybridizations().put(hn, Collections.singletonList(h));
            }
            docSet.getSdrfDocuments().add(doc);
        }
        SampleProbeProfileHandler.ValidatingHeaderParser proc
                = new SampleProbeProfileHandler.ValidatingHeaderParser(designType, result, docSet);
        proc.parse(Arrays.asList(headerColumns), 1);
        List<String> l = new ArrayList<String>(Arrays.asList(messages));
        for (ValidationMessage m : result.getMessages()) {
            assertTrue("unexpected message " + m.getMessage(), l.remove(m.getMessage()));
        }
        assertTrue("expected messages " + l.toString(), l.isEmpty());
        return proc;
    }

}