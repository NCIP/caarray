package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author gax
 */
public class IlluminaBgxValidatorTest {
    private FileValidationResult results;
    private BgxValidator instance;

    @Before
    public void setUp() throws Exception {
        this.results = new FileValidationResult();
        this.instance = new BgxValidator(this.results);
    }

    @Test
    public void testValidator() {
        this.instance.startDocument();
        this.instance.startSection("Probes", 1);
        final String[] header = { "Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID" };
        this.instance.parseFirstRow(header, 2);
        final String[] row = { "a", "b", "c", "b", "e", "f" };
        this.instance.parseRow(row, 3);
        this.instance.endSection("Probes", 4);
        this.instance.startSection("Controls", 5);
        this.instance.parseFirstRow(header, 6);
        this.instance.parseRow(row, 7);
        this.instance.endSection("Controls", 8);
        this.instance.endDocument();

        assertTrue(this.results.isValid());
        assertTrue(this.results.getMessages().isEmpty());
    }

    @Test
    public void testBadColumnHeader() {
        this.instance.startDocument();
        this.instance.startSection("Probes", 1);
        final String[] header = { "BAD___Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID" };
        this.instance.parseFirstRow(header, 2);
        final String[] row = { "a", "b", "c", "b", "e", "f" };
        this.instance.parseRow(row, 3);
        this.instance.endSection("Probes", 4);
        this.instance.endDocument();

        assertFalse(this.results.isValid());
        assertTrue(this.results.getMessages().size() == 2);
        ValidationMessage m = this.results.getMessages().get(0);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing column PROBE_ID", m.getMessage());
        m = this.results.getMessages().get(1);
        assertEquals(ValidationMessage.Type.WARNING, m.getType());
        assertEquals("Unexpected column name BAD___PROBE_ID", m.getMessage());
    }

    @Test
    public void testBadHeadingSection() {
        this.instance.startDocument();
        this.instance.startSection("Heading", 1);
        String[] row = { "Number of Controls", "99" };
        this.instance.parseFirstRow(row, 2);
        this.instance.endSection("Heading", 3);
        this.instance.startSection("Controls", 4);
        row = new String[] { "Probe_Id", "Symbol", "Definition", "Accession", "ENTREZ_GENE_ID", "UNIGENE_ID" };
        this.instance.parseFirstRow(row, 5);
        row = new String[] { "a", "b", "c", "b", "e", "f" };
        this.instance.parseRow(row, 6);
        row = new String[] { "", "b", "c", "b", "e", "f" };
        this.instance.parseRow(row, 7);
        row = new String[] { "a" };
        this.instance.parseRow(row, 8);
        this.instance.endSection("Controls", 9);
        this.instance.endDocument();

        assertFalse(this.results.isValid());
        assertTrue(this.results.getMessages().size() == 4);

        ValidationMessage m = this.results.getMessages().get(0);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing PROBES section", m.getMessage());
        m = this.results.getMessages().get(1);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Heading 'NUMBER OF CONTROLS' expected 99 probes, but counted 3", m.getMessage());
        m = this.results.getMessages().get(2);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Missing or blank PROBE_ID", m.getMessage());
        m = this.results.getMessages().get(3);
        assertEquals(ValidationMessage.Type.ERROR, m.getType());
        assertEquals("Expected 6 values but row has 1", m.getMessage());
    }

}