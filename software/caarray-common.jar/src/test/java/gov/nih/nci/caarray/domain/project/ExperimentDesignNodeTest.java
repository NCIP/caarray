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
    @SuppressWarnings("deprecation")
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
        assertTrue(extract == source.getSuccessorsOfType(ExperimentDesignNodeType.EXTRACT).iterator().next());
        
        try {
            sample.addDirectSuccessor(source);
            fail("Expected not to be able to add source as successor to sample");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
