//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceTest;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
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

import org.junit.Test;

/**
 * 
 * @author gax
 */
public class IlluminaSampleProbeProfileHandlerTest extends AbstractServiceTest {
    private ArrayDataServiceTest helper;
    Map<String, ArrayDesign> designMap = new HashMap<String, ArrayDesign>();

    ArrayDesign buildArrayDesign(String name) {
        final ArrayDesign design = new ArrayDesign();
        design.setName(name);
        design.setVersion("99");
        design.setProvider(new Organization());
        design.getProvider().setName("Illumina");
        final ArrayDesignDetails detail = new ArrayDesignDetails();
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
        final CaArrayFile f = new CaArrayFile();
        f.setFileType(BgxDesignHandler.BGX_FILE_TYPE);
        f.setFileStatus(FileStatus.IMPORTED);
        design.getDesignFiles().add(f);
        return design;
    }

    private ArrayDesign getOrCreateDesign(String name) {
        ArrayDesign ad = this.designMap.get(name);
        if (ad == null) {
            ad = buildArrayDesign(name);
            this.designMap.put(name, ad);
        }
        return ad;
    }

    private void setup() throws Exception {
        this.helper = new ArrayDataServiceTest() {
            @Override
            protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                return getOrCreateDesign(lsidObjectId);
            }

            @Override
            protected void configurePlatforms(PlatformModule platformModule) {
                platformModule.addPlatform(new IlluminaModule());
            }
        };
        this.helper.setUp();
    }

    @Test
    public void testImport() throws Exception {
        setup();

        final CaArrayFile f =
                this.helper.getDataCaArrayFile(IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE,
                        SampleProbeProfileHandler.SAMPLE_PROBE_PROFILE_FILE_TYPE);
        f.setId(1L);
        final ArrayDesign design = getOrCreateDesign("test-design.bgx.txt");
        // hack: set the id, because otherwise two designs can never be equals
        design.setId(1L);
        f.getProject().getExperiment().getArrayDesigns().add(design);

        final FileValidationResult result = this.helper.arrayDataService.validate(f, null, false);
        assertTrue("validation: " + result.getMessages().toString(), result.isValid());
        this.helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        final Hybridization hyb = f.getProject().getExperiment().getHybridizations().iterator().next();

        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(16, hdl.size());
        for (final HybridizationData hd : hdl) {
            assertEquals(8, hd.getColumns().size());
            final List<String> expected =
                    new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                            SampleProbeProfileQuantitationType.NARRAYS.getName(),
                            SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                            SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                            SampleProbeProfileQuantitationType.AVG_NBEADS.getName(),
                            SampleProbeProfileQuantitationType.DETECTION.getName()));
            for (final AbstractDataColumn c : hd.getColumns()) {
                if (c instanceof FloatColumn) {
                    assertEquals(3, ((FloatColumn) c).getValues().length);
                } else if (c instanceof IntegerColumn) {
                    assertEquals(3, ((IntegerColumn) c).getValues().length);
                }
                assertTrue("missing " + c.getQuantitationType(), expected.remove(c.getQuantitationType().getName()));
            }
            assertTrue("expected " + expected, expected.isEmpty());
        }
    }

    @Test
    public void testValidationErrors_0() {
        final String[] header = {"", "", "" };
        final String[] messages = {"No samples with quantitation type AVG_SIGNAL found", "No samples found" };
        processHeader(header, messages, null, null);
    }

    @Test
    public void testValidationErrors_1() {
        final String[] header = {"Probe_Id", "Foo", "Hyb1.MIN_Signal", "Hyb1.AVG_Signal" };
        final String[] messages =
                {"Missing quantitation type(s) [DETECTION] for sample Hyb1",
                        "Using column PROBE_ID with design type ILLUMINA_DESIGN_CSV", "Ignored column Foo" };
        processHeader(header, messages, null, IlluminaCsvDesignHandler.DESIGN_CSV_FILE_TYPE);
    }

    @Test
    public void testValidationErrors_2() {
        final String[] header = {"TargetId", "Foo", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal" };
        final String[] messages = {"Using column TARGETID with design type ILLUMINA_DESIGN_BGX", "Ignored column Foo" };
        processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
    }

    @Test
    public void testValidationErrors_3() {
        final String[] header =
                {"ID_REF", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal", "DETECTION PVAL-Hyb2", "AVG_Signal-Hyb2" };
        final String[] messages =
                {"Mixed column name formats", "Inconsistent quantitation type column between samples Hyb1 and Hyb2",
                        "Missing quantitation type(s) [AVG_SIGNAL, DETECTION] for sample Hyb2",
                        "Ignored column DETECTION PVAL-Hyb2", "Ignored column AVG_Signal-Hyb2" };
        processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
    }

    @Test
    public void testValidationErrors_4() {
        final String[] header =
                {"ID_REF", "Hyb1.DETECTION PVAL", "Hyb1.AVG_Signal", "Hyb2.DETECTION PVAL", "Hyb2.AVG_Signal",
                        "Hyb2.Foo" };
        final String[] messages = {"Unsupported column Hyb2.Foo", "Ignored column Hyb2.Foo" };
        final SampleProbeProfileHandler.ValidatingHeaderParser p =
                processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
        assertEquals(2, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        assertTrue(p.getHybNames().contains("Hyb2"));
        assertTrue(p.getLoaders().get(0).getQTypes().containsAll(p.getLoaders().get(1).getQTypes()));
        assertTrue(p.getLoaders().get(1).getQTypes().containsAll(p.getLoaders().get(0).getQTypes()));
    }

    @Test
    public void testValidationErrors_5() {
        final String[] header = {"ID_REF" };
        final String[] messages = {"No samples found", "No samples with quantitation type AVG_SIGNAL found" };
        final SampleProbeProfileHandler.ValidatingHeaderParser p =
                processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
        assertEquals(0, p.getHybNames().size());
        assertTrue(p.getHybNames().isEmpty());
    }

    @Test
    public void testInterleavedColumns() {
        final String[] header =
                {"Hyb2.AVG_Signal", "Hyb1.DETECTION PVAL", "ID_REF", "Hyb1.AVG_Signal", "Hyb2.DETECTION PVAL", };
        final String[] messages = {};
        final SampleProbeProfileHandler.ValidatingHeaderParser p =
                processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
        assertEquals(2, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        assertTrue(p.getHybNames().contains("Hyb2"));
        assertTrue(p.getLoaders().get(0).getQTypes().containsAll(p.getLoaders().get(1).getQTypes()));
        assertTrue(p.getLoaders().get(1).getQTypes().containsAll(p.getLoaders().get(0).getQTypes()));
        assertEquals(5, p.getRowWidth());
    }

    @Test
    public void testValidationPartialFile() {
        String[] header =
            {"Hyb2.AVG_Signal", "Hyb1.DETECTION PVAL", "ID_REF", "Hyb1.AVG_Signal", "Hyb2.DETECTION PVAL"};
        FileRef mockFile = mock(FileRef.class);
        when(mockFile.exists()).thenReturn(true);

        // Sdrf only references Hyb1
        when(mockFile.isPartialFile()).thenReturn(false);
        String[] messages = {"Hybridization Hyb2 is not referenced in SDRF"};
        final SampleProbeProfileHandler.ValidatingHeaderParser p =
                processHeader(header, messages, Collections.singleton("Hyb1"), BgxDesignHandler.BGX_FILE_TYPE, mockFile);

        // Sdrf is a partial file, so missing reference to Hyb2 should be ignored
        when(mockFile.isPartialFile()).thenReturn(true);
        String[] messages2 = {};
        processHeader(header, messages2, Collections.singleton("Hyb1"), BgxDesignHandler.BGX_FILE_TYPE, mockFile);
    }

    @Test
    public void testValidationErrors_AllQTypes() {
        final String[] header = {"ID_REF", // 1
                "Hyb1.MIN_Signal", // 2
                "Hyb1.AVG_Signal", // 3
                "Hyb1.MAX_Signal", // 4
                "Hyb1.NARRAYS", // 5
                "Hyb1.ARRAY_STDEV", // 6
                "Hyb1.BEAD_STDEV", // 7
                "Hyb1.Avg_NBEADS", // 8
                "Hyb1.Detection", // 9
                "Hyb1.Detection Pval", // 10 dup of Detection
                "Hyb1.DetectionPval", // 11 dup of Detection
        };
        final String[] messages = {"QuantitationType DETECTION already defined in column 9",// dup "Hyb1.Detection Pval"
                "QuantitationType DETECTION already defined in column 9",// dup "Hyb1.DetectionPval"
        };
        final SampleProbeProfileHandler.ValidatingHeaderParser p =
                processHeader(header, messages, null, BgxDesignHandler.BGX_FILE_TYPE);
        assertEquals(1, p.getHybNames().size());
        assertTrue(p.getHybNames().contains("Hyb1"));
        final EnumSet<SampleProbeProfileQuantitationType> expected =
                EnumSet.allOf(SampleProbeProfileQuantitationType.class);
        final List<SampleProbeProfileQuantitationType> result = p.getLoaders().get(0).getQTypes();
        final List<SampleProbeProfileQuantitationType> diff = new ArrayList<SampleProbeProfileQuantitationType>(result);
        diff.removeAll(expected);
        assertTrue("unexpected qtypes " + diff, diff.isEmpty());
        diff.clear();
        diff.addAll(expected);
        diff.removeAll(result);
        assertTrue("expected to find " + diff, diff.isEmpty());
    }

    static private SampleProbeProfileHandler.ValidatingHeaderParser processHeader(String[] headerColumns,
            String[] messages, Set<String> sdrf, FileType designType) {
        return processHeader(headerColumns, messages, sdrf, designType, null);
    }
    
    static private SampleProbeProfileHandler.ValidatingHeaderParser processHeader(String[] headerColumns,
            String[] messages, Set<String> sdrf, FileType designType, FileRef sdrfFile) {
        final FileValidationResult result = new FileValidationResult();
        MageTabDocumentSet docSet = null;
        if (sdrf != null) {
            final MageTabFileSet fset = new MageTabFileSet();
            docSet = new MageTabDocumentSet(fset);
            final SdrfDocument doc = new SdrfDocument(docSet, sdrfFile);
            for (final String hn : sdrf) {
                final gov.nih.nci.caarray.magetab.sdrf.Hybridization h =
                        new gov.nih.nci.caarray.magetab.sdrf.Hybridization();
                h.setName(hn);
                docSet.getSdrfHybridizations().put(hn, Collections.singletonList(h));
            }
            docSet.getSdrfDocuments().add(doc);
        }
        final SampleProbeProfileHandler.ValidatingHeaderParser proc =
                new SampleProbeProfileHandler.ValidatingHeaderParser(designType, result, docSet);
        proc.parse(Arrays.asList(headerColumns), 1);
        final List<String> l = new ArrayList<String>(Arrays.asList(messages));
        for (final ValidationMessage m : result.getMessages()) {
            assertTrue("unexpected message " + m.getMessage(), l.remove(m.getMessage()));
        }
        assertTrue("expected messages " + l.toString(), l.isEmpty());
        return proc;
    }
}
