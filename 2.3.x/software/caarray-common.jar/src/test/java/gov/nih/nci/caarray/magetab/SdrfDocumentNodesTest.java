//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Extract;
import gov.nih.nci.caarray.magetab.sdrf.Sample;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocumentNodes;
import gov.nih.nci.caarray.magetab.sdrf.Source;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the SdrfDocumentNodes class.
 *
 * @author Rashmi Srinivasa
 */
public class SdrfDocumentNodesTest extends AbstractCaarrayTest {
    private final SdrfDocumentNodes sdrfDocumentNodes = new SdrfDocumentNodes();

    private final Set<gov.nih.nci.caarray.magetab.sdrf.Source> allSources =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Source>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Sample> allSamples =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Sample>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Extract> allExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Extract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract> allLabeledExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Hybridization> allHybridizations =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Hybridization>();
    private final Set<ArrayDataFile> allArrayDataFiles = new HashSet<ArrayDataFile>();
    private final Set<DerivedArrayDataFile> allDerivedArrayDataFiles = new HashSet<DerivedArrayDataFile>();
    private final Set<ArrayDataMatrixFile> allArrayDataMatrixFiles = new HashSet<ArrayDataMatrixFile>();
    private final Set<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles =
        new HashSet<DerivedArrayDataMatrixFile>();

    /**
     * Create a few nodes to initialize the SdrfDocumentNodes object with.
     */
    @Before
    public void setup() throws IOException {
        Source source = new Source();
        Sample sample = new Sample();
        sample.getPredecessors().add(source);
        source.getSuccessors().add(sample);
        Extract extract1 = new Extract();
        extract1.getPredecessors().add(sample);
        sample.getSuccessors().add(extract1);
        Extract extract2 = new Extract();
        extract2.getPredecessors().add(sample);
        sample.getSuccessors().add(extract2);
        allSources.add(source);
        allSamples.add(sample);
        allExtracts.add(extract1);
        allExtracts.add(extract2);
    }

    @Test
    public void testInitializeNodesComplete() {
        // Initialize the SDRF document with all the object graph nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                allDerivedArrayDataMatrixFiles);
        assertTrue("All SDRF document nodes have not been initialized.", sdrfDocumentNodes.isInitialized());
    }

    @Test
    public void testInitializeNodesIncomplete() {
        // Initialize the SDRF document with an incomplete set of nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, null, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, null, null, null);
        assertFalse("All SDRF document nodes have been initialized.", sdrfDocumentNodes.isInitialized());
    }

    @Test
    public void testAllNodes() {
        // Initialize the SDRF document with all the object graph nodes.
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                allDerivedArrayDataMatrixFiles);
        assertEquals(1, sdrfDocumentNodes.getAllSources().size());
        assertEquals(1, sdrfDocumentNodes.getAllSamples().size());
        assertEquals(2, sdrfDocumentNodes.getAllExtracts().size());
        assertEquals(0, sdrfDocumentNodes.getAllLabeledExtracts().size());
        assertEquals(0, sdrfDocumentNodes.getAllHybridizations().size());
        assertEquals(0, sdrfDocumentNodes.getAllArrayDataFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllArrayDataMatrixFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllDerivedArrayDataFiles().size());
        assertEquals(0, sdrfDocumentNodes.getAllDerivedArrayDataMatrixFiles().size());
    }
}
