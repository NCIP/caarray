package gov.nih.nci.caarray.application.arraydesign.illumina;

import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.ExpressionProbeAnnotation;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class IlluminaBgxValidatorTest {
    private FileValidationResult results;
    private IlluminaBgxValidator instance;
    

    @Before
    public  void setUp() throws Exception {
        results = new FileValidationResult(null);
        instance = new IlluminaBgxValidator(results);
    }

    @Test
    public void testValidator() {
        instance.startDocument();
        instance.startSection("Probes", 1);
        String[] header = {"Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID"};
        instance.parseFirstRow(header, 2);
        String[] row = {"a", "b", "c", "b", "e", "f"};
        instance.parseRow(row, 3);
        instance.endSection("Probes", 4);
        instance.startSection("Controls", 5);
        instance.parseFirstRow(header, 6);
        instance.parseRow(row, 7);
        instance.endSection("Controls", 8);
        instance.endDocument();

        assertTrue(results.isValid());
        assertTrue(results.getMessages().isEmpty());
    }


    @Test
    public void testBadColumnHeader() {
        instance.startDocument();
        instance.startSection("Probes", 1);
        String[] header = {"BAD___Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID"};
        instance.parseFirstRow(header, 2);
        String[] row = {"a", "b", "c", "b", "e", "f"};
        instance.parseRow(row, 3);
        instance.endSection("Probes", 4);
        instance.endDocument();

        assertFalse(results.isValid());
        assertTrue(results.getMessages().size() == 2);
        ValidationMessage m = results.getMessages().get(0);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing column PROBE_ID", m.getMessage());
        m = results.getMessages().get(1);
        assertEquals(ValidationMessage.Type.WARNING, m.getType());
        assertEquals("Unexpected column name BAD___PROBE_ID", m.getMessage());
    }

    @Test
    public void testBadHeadingSection() {
        instance.startDocument();
        instance.startSection("Heading", 1);
        String[] row = {"Number of Controls", "99"};
        instance.parseFirstRow(row, 2);
        instance.endSection("Heading", 3);
        instance.startSection("Controls", 4);
        row = new String[]{"Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID"};
        instance.parseFirstRow(row, 5);
        row = new String[]{"a", "b", "c", "b", "e", "f"};
        instance.parseRow(row, 6);
        row = new String[]{"", "b", "c", "b", "e", "f"};
        instance.parseRow(row, 7);
        row = new String[]{"a"};
        instance.parseRow(row, 8);
        instance.endSection("Controls", 9);
        instance.endDocument();

        assertFalse(results.isValid());
        assertTrue(results.getMessages().size() == 4);

        ValidationMessage m = results.getMessages().get(0);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing PROBES section", m.getMessage());
        m = results.getMessages().get(1);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Heading 'NUMBER OF CONTROLS' expected 99 probes, but counted 3", m.getMessage());
        m = results.getMessages().get(2);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing or blank PROBE_ID", m.getMessage());
        m = results.getMessages().get(3);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Expected 6 values but row has 1", m.getMessage());
    }

}