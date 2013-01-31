//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import org.junit.Test;

/**
 * @author Scott Miller
 *
 */
public class ProtocolKeyTest extends AbstractCaarrayTest {

    @Test
    public void testEqualsHashCode() {
        TermSource source1 = new TermSource();
        String name1 = "name1";
        ProtocolKey key1 = new ProtocolKey(name1, source1);
        ProtocolKey key2 = null;

        assertFalse(key1.equals(key2));
        assertTrue(key1.equals(key1));

        key2 = new ProtocolKey(name1, source1);
        assertTrue(key1.equals(key2));
        assertTrue(key1.hashCode() == key2.hashCode());

        assertTrue(key1.equals(key2));
        assertTrue(key1.hashCode() == key2.hashCode());

        key2 = new ProtocolKey("name2", source1);
        assertFalse(key1.equals(key2));
        assertFalse(key1.hashCode() == key2.hashCode());
    }
}
