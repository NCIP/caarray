//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("PMD")
public class CaArrayFileTest {

    @Test
    public void testSetFileStatus() {
        final CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTED, file.getFileStatus());
        assertEquals("IMPORTED", file.getStatus());
        assertEquals(true, file.getFileStatus().isDeletable());

        file.setFileStatus(FileStatus.UPLOADED);
        assertEquals(FileStatus.UPLOADED, file.getFileStatus());
        assertEquals("UPLOADED", file.getStatus());
        assertEquals(true, file.isValidatable());

        file.setFileStatus(FileStatus.VALIDATED);
        assertEquals(FileStatus.VALIDATED, file.getFileStatus());
        assertEquals("VALIDATED", file.getStatus());
        assertEquals(true, file.isImportable());

        file.setFileStatus(null);
        assertEquals(null, file.getFileStatus());
        assertEquals(null, file.getStatus());
    }

    @Test
    public void singleChildSetsParentStatus() {
        CaArrayFile parent = new CaArrayFile();
        parent.setFileStatus(FileStatus.IMPORTING);
        
        CaArrayFile child = new CaArrayFile(parent);
        child.setFileStatus(FileStatus.IMPORTING);        
        parent.addChild(child);
        
        child.setFileStatus(FileStatus.IMPORTED);
        
        assertEquals(FileStatus.IMPORTED, parent.getFileStatus());
    }
    
    @Test
    public void parentStatusNotSetWithMultipleChildren() {
        CaArrayFile parent = new CaArrayFile();
        parent.setFileStatus(FileStatus.IMPORTING);
        
        CaArrayFile child1 = new CaArrayFile(parent);
        child1.setFileStatus(FileStatus.IMPORTING);        
        parent.addChild(child1);
        
        CaArrayFile child2 = new CaArrayFile(parent);
        child2.setFileStatus(FileStatus.IMPORTING);        
        parent.addChild(child2);
        
        child1.setFileStatus(FileStatus.IMPORTED);
        assertEquals(FileStatus.IMPORTING, parent.getFileStatus());
        
    }
}
