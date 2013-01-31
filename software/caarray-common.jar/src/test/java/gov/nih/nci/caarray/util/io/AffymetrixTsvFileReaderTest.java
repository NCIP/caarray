//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.util.io.AffymetrixTsvFileReader.Record;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests parsing Affymetrix TSV files
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class AffymetrixTsvFileReaderTest {

    private AffymetrixTsvFileReader tsvFile;
    private static final File V1_FILE = new File(AffymetrixTsvFileReader.class.getResource(
            "/arraydesign/affymetrix/test_v1.tsv").getFile());
    private static final File V2_FILE = new File(AffymetrixTsvFileReader.class.getResource(
            "/arraydesign/affymetrix/test_v2.tsv").getFile());

    @Before
    public void cleanup() throws Exception {
        if (tsvFile != null) {
            tsvFile.close();
        }
    }

    @Test
    public void testLoadV2Headers() throws Exception {
        tsvFile = new AffymetrixTsvFileReader(V2_FILE);
        tsvFile.loadHeaders();

        assertEquals(8, tsvFile.getFileHeaderCount());
        assertTrue(tsvFile.getFileHeader("chip_type").contains("HuEx-1_0-st-v2"));
        assertTrue(tsvFile.getFileHeader("chip_type").contains("HuEx-1_0-st-v1"));
        assertTrue(tsvFile.getFileHeader("chip_type").contains("HuEx-1_0-st-ta1"));
        assertEquals("HuEx-1_0-st", tsvFile.getFileHeaderAsString("lib_set_name"));
        assertEquals("r2", tsvFile.getFileHeaderAsString("lib_set_version"));
        assertEquals("Tue Sep 19 15:18:05 PDT 2006", tsvFile.getFileHeaderAsString("create_date"));
        assertEquals("0000008635-1158704285-0183259307-0389325148-0127012107", tsvFile.getFileHeaderAsString("guid"));
        assertEquals("1.0", tsvFile.getFileHeaderAsString("pgf_format_version"));
        assertNull(tsvFile.getFileHeader("header0"));
        assertNull(tsvFile.getFileHeader("header1"));
        assertNull(tsvFile.getFileHeaderAsString("header2"));

        List<List<String>> columnHeaders = tsvFile.getColumnHeaders();
        assertEquals(3, columnHeaders.size());
        assertEquals(2, columnHeaders.get(0).size());
        assertArrayEquals(new String[] { "probeset_id", "type" }, columnHeaders.get(0).toArray());
        assertEquals(1, columnHeaders.get(1).size());
        assertEquals("atom_id", columnHeaders.get(1).get(0));
        assertEquals(6, columnHeaders.get(2).size());
        assertArrayEquals(new String[] { "probe_id", "type", "gc_count", "probe_length", "interrogation_position",
                "probe_sequence" }, columnHeaders.get(2).toArray());
    }

    @Test
    public void testLoadV2Data() throws Exception {
        tsvFile = new AffymetrixTsvFileReader(V2_FILE);

        // reading the first line should load the headers, without explicity calling load headers
        Record probeSet = tsvFile.readNextDataLine();
        assertEquals("2590411", probeSet.get("probeset_id"));
        assertNull(probeSet.get("PROBESET_ID"));
        assertEquals("main", probeSet.get("type"));
        assertNull(probeSet.get("dummyHeader"));
        assertEquals(0, probeSet.getRecordLevel());

        Record atom = tsvFile.readNextDataLine();
        assertEquals("1", atom.get("atom_id"));
        assertEquals(1, atom.getRecordLevel());

        // reset, and the next 2 lines read should be the lines that have already been read
        tsvFile.reset();

        probeSet = tsvFile.readNextDataLine();
        assertEquals("2590411", probeSet.get("probeset_id"));
        assertNull(probeSet.get("PROBESET_ID"));
        assertEquals("main", probeSet.get("type"));
        assertNull(probeSet.get("dummyHeader"));
        assertEquals(0, probeSet.getRecordLevel());

        atom = tsvFile.readNextDataLine();
        assertEquals("1", atom.get("atom_id"));
        assertEquals(1, atom.getRecordLevel());

        Record probe = tsvFile.readNextDataLine();
        assertEquals("5402769", probe.get("probe_id"));
        assertEquals("", probe.get("probe_length"));
        assertEquals("13", probe.get("interrogation_position"));
        assertEquals("", probe.get("probe_sequence"));
        assertEquals(2, probe.getRecordLevel());

        probeSet = tsvFile.readNextDataLine();
        assertEquals("2590412", probeSet.get("probeset_id"));
        assertNull(probeSet.get("PROBESET_ID"));
        assertEquals(" main ", probeSet.get("type")); // note the leading and trailing space is kept
        assertNull(probeSet.get("dummyHeader"));
        assertEquals(0, probeSet.getRecordLevel());

        atom = tsvFile.readNextDataLine();
        assertEquals("1", atom.get("atom_id"));
        assertEquals(1, atom.getRecordLevel());

        atom = tsvFile.readNextDataLine();
        assertEquals("2", atom.get("atom_id"));
        assertEquals(1, atom.getRecordLevel());

        probe = tsvFile.readNextDataLine();
        assertEquals("3869022", probe.get("probe_id"));
        assertEquals("AGGAGTACAGGGTAAGATATGGTCT", probe.get("probe_sequence"));
        assertEquals(2, probe.getRecordLevel());

        probe = tsvFile.readNextDataLine();
        assertEquals("3869023", probe.get("probe_id"));
        assertEquals(2, probe.getRecordLevel());
    }

    @Test
    public void testLoadV1Headers() throws Exception {
        tsvFile = new AffymetrixTsvFileReader(V1_FILE);
        tsvFile.loadHeaders();

        assertEquals(0, tsvFile.getFileHeaderCount());
        assertNull(tsvFile.getFileHeader("header0"));

        List<List<String>> columnHeaders = tsvFile.getColumnHeaders();
        assertEquals(1, columnHeaders.size());
        assertEquals(3, columnHeaders.get(0).size());
        assertArrayEquals(new String[] { "pool", "cel_files", "scanner" }, columnHeaders.get(0).toArray());
    }

    @Test
    public void testLoadV1Data() throws Exception {
        tsvFile = new AffymetrixTsvFileReader(V1_FILE);

        Record record = null;
        for (int i = 1; i < 7; i++) {
            record = tsvFile.readNextDataLine();
            assertEquals(0, record.getRecordLevel());
            assertEquals(String.valueOf(i), record.get("pool"));
            assertEquals("sp" + i + ".cel", record.get("cel_files"));
            assertEquals("gcos" + i, record.get("scanner"));
        }

        assertNull(tsvFile.readNextDataLine());
    }
}
