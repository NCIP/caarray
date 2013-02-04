//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;

import java.io.File;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class ArrayDesignTest {
    private static AssayType DUMMY_ASSAY_TYPE1 = new AssayType("Gene Expression");

    @Test
    public void testSetType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        arrayDesign.setAssayTypes(assayTypes);
        assertEquals(DUMMY_ASSAY_TYPE1.getName(), arrayDesign.getAssayTypes().first().getName());
        assertEquals(DUMMY_ASSAY_TYPE1, arrayDesign.getAssayTypes().first());
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(new AssayType("illegal value"));
        arrayDesign.setAssayTypes(assayTypes);
    }

    @Test
    public void testSetArrayType() {
        ArrayDesign arrayDesign = new ArrayDesign();
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        arrayDesign.setAssayTypes(assayTypes);
        assertEquals(DUMMY_ASSAY_TYPE1.getName(), arrayDesign.getAssayTypes().first().getName());
        assertEquals(DUMMY_ASSAY_TYPE1, arrayDesign.getAssayTypes().first());
    }

    @Test
    public void testArrayDesignIsImportableWhenStatusIsUploaded() {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFileSet mockCaArrayFileSet = mock(CaArrayFileSet.class);
        CaArrayFile mockCaArrayFile = mock(CaArrayFile.class);
        final Set<CaArrayFile> setOfCaArrayFiles = new java.util.HashSet<CaArrayFile>();
        setOfCaArrayFiles.add(mockCaArrayFile);
        when(mockCaArrayFile.getFileStatus()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return FileStatus.UPLOADED;
            }
        });
        when(mockCaArrayFileSet.getFiles()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return setOfCaArrayFiles;
            }
        });
        arrayDesign.setDesignFileSet(mockCaArrayFileSet);
        assertTrue("The array design should be importable.", arrayDesign.isUnparsedAndReimportable());
    }
}
