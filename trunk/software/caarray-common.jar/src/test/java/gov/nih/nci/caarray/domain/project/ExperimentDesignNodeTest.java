//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;

import org.junit.Test;

public class ExperimentDesignNodeTest {

    @Test
    public void testNodeTypes() {
        assertTrue(ExperimentDesignNodeType.SAMPLE.isSuccessorOf(ExperimentDesignNodeType.SOURCE));
        assertTrue(ExperimentDesignNodeType.SAMPLE.isDirectSuccessorOf(ExperimentDesignNodeType.SOURCE));
        assertTrue(ExperimentDesignNodeType.EXTRACT.isSuccessorOf(ExperimentDesignNodeType.SOURCE));
        assertFalse(ExperimentDesignNodeType.EXTRACT.isDirectSuccessorOf(ExperimentDesignNodeType.SOURCE));
        
        assertTrue(ExperimentDesignNodeType.SOURCE.isPredecessorOf(ExperimentDesignNodeType.SAMPLE));
        assertTrue(ExperimentDesignNodeType.SOURCE.isDirectPredecessorOf(ExperimentDesignNodeType.SAMPLE));
        assertTrue(ExperimentDesignNodeType.SOURCE.isPredecessorOf(ExperimentDesignNodeType.EXTRACT));
        assertFalse(ExperimentDesignNodeType.SOURCE.isDirectPredecessorOf(ExperimentDesignNodeType.EXTRACT));
        
        assertEquals(ExperimentDesignNodeType.SOURCE, ExperimentDesignNodeType.SAMPLE.getPredecesorType());
        assertEquals(ExperimentDesignNodeType.SAMPLE, ExperimentDesignNodeType.SOURCE.getSuccessorType());
        assertEquals(null, ExperimentDesignNodeType.SOURCE.getPredecesorType());
        assertEquals(null, ExperimentDesignNodeType.HYBRIDIZATION.getSuccessorType());
    }

    @Test
    public void testNodes(){
        Source source = new Source();
        Sample sample = new Sample();
        Extract extract = new Extract();
        
        assertEquals(ExperimentDesignNodeType.SOURCE, source.getNodeType());
        assertEquals(ExperimentDesignNodeType.SAMPLE, sample.getNodeType());

        sample.addDirectPredecessor(source);
        sample.addDirectSuccessor(extract);
        assertEquals(1, source.getSuccessorsOfType(ExperimentDesignNodeType.EXTRACT).size());
        assertEquals(0, source.getSuccessorsOfType(ExperimentDesignNodeType.HYBRIDIZATION).size());
        assertEquals(0, sample.getSuccessorsOfType(ExperimentDesignNodeType.SOURCE).size());
        assertEquals(1, sample.getPredecessorsOfType(ExperimentDesignNodeType.SOURCE).size());
        assertEquals(0, sample.getPredecessorsOfType(ExperimentDesignNodeType.SAMPLE).size());
        assertTrue(extract == source.getSuccessorsOfType(ExperimentDesignNodeType.EXTRACT).iterator().next());
        
        try {
            sample.addDirectSuccessor(source);
            fail("Expected not to be able to add source as successor to sample");
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        try {
            sample.addDirectPredecessor(sample);
            fail("Expected not to be able to add sample as predecessor to sample");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
