package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydata.illumina.ValidatingProcessor;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.illumina.DefaultHeaderProcessor;
import gov.nih.nci.caarray.application.arraydata.illumina.IlluminaGenotypingProcessedMatrixQuantitationType;
import gov.nih.nci.caarray.application.arraydata.illumina.ValidatingHeaderProcessor;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
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
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class IlluminaGenotypingProcessedMatrixHandlerTest extends AbstractServiceTest {

    ArrayDataServiceTest helper = new ArrayDataServiceTest();

    @Test
    public void codeAssumption() {
        assertEquals("new IlluminaGenotypingProcessedMatrixQuantitationType added! revisit validation of this column",
                6, IlluminaGenotypingProcessedMatrixQuantitationType.values().length);
        Set<DataType> supportedTypes = new HashSet<DataType>(Arrays.asList(DataType.STRING, DataType.FLOAT));
        for (IlluminaGenotypingProcessedMatrixQuantitationType t : IlluminaGenotypingProcessedMatrixQuantitationType.values()) {
            assertTrue("you'll need code to validate columns of type" + t.getDataType(), supportedTypes.contains(t.getDataType()));
            try {
                DefaultHeaderProcessor.Header.valueOf(t.name());
            } catch (IllegalArgumentException e) {
                fail("expects all IlluminaGenotypingProcessedMatrixQuantitationType s to come from a column by the same name" + t.name());
            }
        }        
    }

    private static ArrayDesign buildArrayDesign() {
        ArrayDesign design = new ArrayDesign();
        design.setName("foo");
        design.setVersion("99");
        design.setProvider(new Organization());
        design.getProvider().setName("Illumina");
        ArrayDesignDetails detail = new ArrayDesignDetails();
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
        return design;
    }

    private void setup() throws Exception {
        helper = new ArrayDataServiceTest();
        helper.setUp();
    }

    @Test
    public void test1() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        setup();

        CaArrayFile f = helper.getDataCaArrayFile(IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB, FileType.ILLUMINA_DERIVED_TXT);
        f.setId(1L);
        ArrayDesign design = buildArrayDesign();
        f.getProject().getExperiment().getArrayDesigns().add(design);
        CaArrayDaoFactory.INSTANCE.getSearchDao().save(f);
        
        FileValidationResult result = helper.arrayDataService.validate(f, null);
        assertTrue("validation: "+result.getMessages().toString(), result.isValid());
        helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        Hybridization hyb = f.getProject().getExperiment().getHybridizations().iterator().next();
        List<AbstractDesignElement> l = hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList().getDesignElements();
        assertEquals(3, l.size());
        List<HybridizationData> hdl = hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());
        assertEquals(6, hdl.get(0).getColumns().size());
        for (AbstractDataColumn c : hdl.get(0).getColumns()) {
            int rows;
            try { rows = ((StringColumn)c).getValues().length; }
            catch (ClassCastException e) { rows = ((FloatColumn)c).getValues().length;  }
            assertEquals(3, rows);
        }
        tx.rollback();
    }

    @Test
    public void test2() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();
        setup();

        CaArrayFile f = helper.getDataCaArrayFile(IlluminaArrayDataFiles.ILLUMINA_DERIVED_2_HYB, FileType.ILLUMINA_DERIVED_TXT);
        f.setId(2L);
        ArrayDesign design = buildArrayDesign();
        f.getProject().getExperiment().getArrayDesigns().add(design);
        CaArrayDaoFactory.INSTANCE.getSearchDao().save(f);

        FileValidationResult result = helper.arrayDataService.validate(f, null);
        assertTrue("validation: "+result.getMessages().toString(), result.isValid());
        helper.arrayDataService.importData(f, true, DataImportOptions.getAutoCreatePerFileOptions());
        Iterator<Hybridization> it = f.getProject().getExperiment().getHybridizations().iterator();
        Hybridization hyb1 = it.next();
        Hybridization hyb2 = it.next();
        assertFalse(it.hasNext());
        List<HybridizationData> hdl1 = hyb1.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        List<HybridizationData> hdl2 = hyb2.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(hdl2.size(), hdl1.size());
        assertEquals(hdl2.get(0).getColumns().size(), hdl1.get(0).getColumns().size());
        tx.rollback();
    }

    @Test
    public void testValidationErrors_0() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"", "", ""};
        String [] messages = {"Missing IlmnID, ID_REF, or ID in first column, first line. (Found )"};
        processHeader(header, messages, null, design);
    }
    @Test
    public void testValidationErrors_1() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID"};
        String [] messages = {"Missing 'Value' (hybridization/sample name) column"};
        processHeader(header, messages, null, design);
    }
    @Test
    public void testValidationErrors_2() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID_REF", "MyHyb"};
        String [] messages = {"Missing Quantitation Type (measurement) column"};
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_3() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID_REF", "Hyb-1", "Foo", "Theta"};
        String [] messages = {"Unsupported Column Foo", };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_4() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID_REF", "Hyb-1", "Foo", "Hyb-2", "Bar"};
        String [] messages = {
            "Unsupported Column Foo", "Unsupported Column Hyb-2", "Unsupported Column Bar"
        };
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_5() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.Bar", "Hyb-2", "Hyb-2.Foo", "Hyb-1.Baz"};
        String [] messages = {
            "Unsupported Column Foo",
            "Unsupported Column Bar",
            "Column name Baz breaks pattern",
            "Unsupported Column Foo",
            "Unsupported Column Baz"};
        processHeader(header, messages, null, design);
    }

    @Test
    public void testValidationErrors_6() {
        ArrayDesign design = buildArrayDesign();
        String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.R", "Hyb-2", "Hyb-2.Foo", "Hyb-2.R"};
        String [] messages = {"Unsupported Column Foo", "Unsupported Column Foo"};
        DefaultHeaderProcessor proc = processHeader(header, messages, null, design);
        assertEquals(2, proc.getHybBlocks().length);
    }

    @Test
    public void testValidationErrors_SDRF() {
        ArrayDesign design = buildArrayDesign();
        Set<String> sdrf = Collections.singleton("Hyb-1");
        String[] header = {"ID_REF", "Hyb-1", "Hyb-1.Foo", "Hyb-1.R", "Hyb-2", "Hyb-2.Foo", "Hyb-2.R"};
        String [] messages = {"Unsupported Column Foo", "Unsupported Column Foo", "Hybridization Hyb-2 is not referenced in SDRF"};
        DefaultHeaderProcessor proc = processHeader(header, messages, sdrf, design);
    }

    private DefaultHeaderProcessor processHeader(String[] headerColumns, String[] messages, Set<String> sdrf, ArrayDesign design) {
        FileValidationResult result = new FileValidationResult(null);
        ValidatingHeaderProcessor proc = new ValidatingHeaderProcessor(result, sdrf);
        proc.parseHeader(Arrays.asList(headerColumns), 1);
        List<String> l = new ArrayList<String>(Arrays.asList(messages));
        for (ValidationMessage m : result.getMessages()) {
            assertTrue("unexpected message " + m.getMessage(), l.remove(m.getMessage()));
        }
        assertTrue("expected messages " + l.toString(), l.isEmpty());
        return proc;
    }
}