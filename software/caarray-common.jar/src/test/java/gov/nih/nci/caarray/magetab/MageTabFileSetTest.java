//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.junit.Test;

/**
 * Tests for the file set.
 * 
 * @author tparnell
 */
public class MageTabFileSetTest {

    /**
     * Basic clone test.  Shallow comparison of files.
     */
    @Test
    public void makeCopy() {
        MageTabFileSet orig = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        MageTabFileSet clone = (MageTabFileSet) orig.makeCopy();
        assertNotSame(orig, clone);
        assertEquals(orig.getAllFiles().size(), clone.getAllFiles().size());
        assertTrue(orig.getAllFiles().containsAll(clone.getAllFiles()));
    }
    
    @Test
    public void removeSdrfs() {
        MageTabFileSet orig = TestMageTabSets.MAGE_TAB_SPECIFICATION_INPUT_SET;
        int totalFileCount = orig.getAllFiles().size();
        int sdrfFileCount = orig.getSdrfFiles().size();
        assumeTrue(totalFileCount != sdrfFileCount);
        
        MageTabFileSet clone = orig.makeCopy();
        clone.clearSdrfs();
        assertEquals(0, clone.getSdrfFiles().size());
        assertEquals(totalFileCount - sdrfFileCount, clone.getAllFiles().size());
    }
}
