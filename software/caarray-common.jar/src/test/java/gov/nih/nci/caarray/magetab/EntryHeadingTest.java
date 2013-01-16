//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.*;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.junit.Test;

/**
 * 
 */
public class EntryHeadingTest extends AbstractCaarrayTest {

    @Test
    public void testEntryHeading() {
        ordinary();
        withCategory();
        withNamespace();
    }

    private void ordinary() {
        EntryHeading heading = new EntryHeading("Source Name");
        assertEquals("Source Name", heading.getTypeName());
    }

    private void withCategory() {
        EntryHeading heading = new EntryHeading("Characteristics[Age]");
        assertEquals("Characteristics", heading.getTypeName());
        assertEquals("Age", heading.getQualifier());
        
        // Test with space between name and category (seen in specification test file set)
        heading = new EntryHeading("Characteristics [Genotype]");
        assertEquals("Characteristics", heading.getTypeName());
        assertEquals("Genotype", heading.getQualifier());
    }

    private void withNamespace() {
        EntryHeading heading = new EntryHeading("Protocol REF:Affymetrix:Protocol");
        assertEquals("Protocol REF", heading.getTypeName());
        assertEquals("Affymetrix", heading.getAuthority());
        assertEquals("Protocol", heading.getNamespace());
        
        heading = new EntryHeading("Protocol REF:Affymetrix");
        assertEquals("Protocol REF", heading.getTypeName());
        assertEquals("Affymetrix", heading.getAuthority());
        assertNull(heading.getNamespace());
        
        // Case in SDRF included with specification with incorrect colon at end
        heading = new EntryHeading("Protocol REF:Affymetrix:Protocol:");
        assertEquals("Protocol REF", heading.getTypeName());
        assertEquals("Affymetrix", heading.getAuthority());
        assertEquals("Protocol", heading.getNamespace());
    }

}
