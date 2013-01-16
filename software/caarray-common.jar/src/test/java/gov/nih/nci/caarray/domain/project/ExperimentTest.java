//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Winston Cheng
 *
 */
public class ExperimentTest {
    private static final Array DUMMY_ARRAY_1 = new Array();
    private static final Array DUMMY_ARRAY_2 = new Array();

    @Before
    public void setUp() {
        DUMMY_ARRAY_1.setId(1l);
        DUMMY_ARRAY_2.setId(2l);
    }

    @Test
    public void testGetArrays() {
        Experiment exp = new Experiment();
        Hybridization h1 = new Hybridization();
        Hybridization h2 = new Hybridization();
        h1.setArray(DUMMY_ARRAY_1);
        h2.setArray(DUMMY_ARRAY_2);
        exp.getHybridizations().add(h1);
        exp.getHybridizations().add(h2);

        Set<Array> arrays = exp.getArrays();
        assertEquals(2, arrays.size());
        assertTrue(arrays.contains(DUMMY_ARRAY_1));
        assertTrue(arrays.contains(DUMMY_ARRAY_2));
    }
}
