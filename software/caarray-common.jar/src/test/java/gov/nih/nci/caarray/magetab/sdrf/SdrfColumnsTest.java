//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import gov.nih.nci.caarray.magetab.EntryHeading;

import org.junit.Before;
import org.junit.Test;

/**
 * Test of SDRFColumns class
 * @author dkokotov
 */
public class SdrfColumnsTest {
    private static final SdrfColumn SOURCE = new SdrfColumn(new EntryHeading("Source Name")); 
    private static final SdrfColumn EXTRACT = new SdrfColumn(new EntryHeading("Extract Name")); 
    private static final SdrfColumn HYB = new SdrfColumn(new EntryHeading("Hybridization Name")); 
    private static final SdrfColumn CHARACTERISTICS = new SdrfColumn(new EntryHeading("Characteristics[Age]"));  
    private static final SdrfColumn PROTOCOL = new SdrfColumn(new EntryHeading("Protocol REF:Affymetrix"));  
    
    private SdrfColumns columns;
    
    @Before
    public void setupData() {
        columns = new SdrfColumns();
        columns.getColumns().add(SOURCE);
        columns.getColumns().add(CHARACTERISTICS);
        columns.getColumns().add(PROTOCOL);
        columns.getColumns().add(EXTRACT);
        columns.getColumns().add(HYB);
    }
    
    @Test
    public void testPreviousColumn() {
        assertNull(columns.getPreviousColumn(SOURCE));
        assertEquals(SOURCE, columns.getPreviousColumn(CHARACTERISTICS));
        assertEquals(PROTOCOL, columns.getPreviousColumn(EXTRACT));
        assertEquals(EXTRACT, columns.getPreviousColumn(HYB));
    }

    @Test
    public void testNextColumn() {
        assertNull(columns.getNextColumn(HYB));
        assertEquals(CHARACTERISTICS, columns.getNextColumn(SOURCE));
        assertEquals(PROTOCOL, columns.getNextColumn(CHARACTERISTICS));
        assertEquals(HYB, columns.getNextColumn(EXTRACT));
    }

    @Test
    public void testPreviousNodeColumn() {
        assertNull(columns.getPreviousNodeColumn(SOURCE));
        assertEquals(SOURCE, columns.getPreviousNodeColumn(CHARACTERISTICS));
        assertEquals(SOURCE, columns.getPreviousNodeColumn(PROTOCOL));
        assertEquals(SOURCE, columns.getPreviousNodeColumn(EXTRACT));
        assertEquals(EXTRACT, columns.getPreviousNodeColumn(HYB));
    }
}
