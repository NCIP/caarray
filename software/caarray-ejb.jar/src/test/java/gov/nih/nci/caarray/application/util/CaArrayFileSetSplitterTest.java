//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import gov.nih.nci.caarray.application.file.CaArrayFileRef;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.splitter.MageTabFileSetSplitter;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Class to test the CaArrayFileSetSplitter
 *
 * @author kkanchinadam
 */

public class CaArrayFileSetSplitterTest  {
    private CaArrayFileSetSplitterImpl cafsSplitter;

    private FileAccessService fileAccessService;
    private DataStorageFacade dataStorageFacade;
    private MageTabFileSetSplitter mageTabFileSetSplitter;

    /**
     * setup init data.
     */
    @Before
    public void setup() {
        fileAccessService = mock(FileAccessService.class);
        dataStorageFacade = mock(DataStorageFacade.class);
        mageTabFileSetSplitter = mock(MageTabFileSetSplitter.class);
        cafsSplitter = new CaArrayFileSetSplitterImpl(fileAccessService, dataStorageFacade, mageTabFileSetSplitter);
    }

    @Test
    public void nullInputCaArrayFileSet() throws IOException {
        Set<CaArrayFileSet> smallFileSets = cafsSplitter.split(null);
        assertNull(smallFileSets);
    }

    @Test
    public void  emptyInputCaArrayFileSet() throws IOException {
        CaArrayFileSet arrayFileSet = mock(CaArrayFileSet.class);
        when(arrayFileSet.getFiles()).thenReturn(Sets.<CaArrayFile> newHashSet());

        // Set up FileRefs & the mock splitter
        MageTabFileSet mtfs = mock(MageTabFileSet.class);
        when(mtfs.getAllFiles()).thenReturn(Sets.<FileRef>newHashSet());

        when(mageTabFileSetSplitter.split(any(MageTabFileSet.class))).thenReturn(Sets.newHashSet(mtfs));

        Set<CaArrayFileSet> results = cafsSplitter.split(arrayFileSet);
        assertEquals(1, results.size());
        for (CaArrayFileSet fs : results) {
            assertEquals(0, fs.getFiles().size());
        }
    }

    @Test
    public void oneSdrfs() throws IOException {
        CaArrayFile sdrfFile = getSdrfFile();
        CaArrayFile idfFile = getIdfFile(sdrfFile);
        File splitSdrfFile = setupMageTabFileSets(idfFile);

        Set<CaArrayFileSet> results = splitFiles(sdrfFile, idfFile);
        
        assertEquals(1, results.size());
        for(CaArrayFileSet fs : results) {
            assertEquals(2, fs.getFiles().size());
        }
        
        verify(mageTabFileSetSplitter).split(any(MageTabFileSet.class));
        verify(fileAccessService).add(eq(splitSdrfFile), (CaArrayFile) isNull());
    }

    @Test
    public void cleanupExistingSdrfChildren() throws IOException {
        // Need the behavior inside of fileAccessService so that naive iteration 
        // through the children will result in a concurrent modification exception
        when(fileAccessService.remove(any(CaArrayFile.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                CaArrayFile file = (CaArrayFile) invocation.getArguments()[0];
                file.getParent().getChildren().remove(file);
                
                return true;
            }
        });
        
        CaArrayFile sdrfFile = getSdrfFile();
        CaArrayFile idfFile = getIdfFile(sdrfFile);
        setupMageTabFileSets(idfFile);
        Set<CaArrayFile> orphans = addChildren(sdrfFile);
        
        splitFiles(sdrfFile, idfFile);

        for (CaArrayFile orphan : orphans) {
            verify(fileAccessService).remove(orphan);
        }
    }

    private CaArrayFile getSdrfFile() {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(1L);

        CaArrayFile childFile = mock(CaArrayFile.class);
        when(childFile.getProject()).thenReturn(project);
        when(fileAccessService.add(any(File.class), any(CaArrayFile.class))).thenReturn(childFile);

        CaArrayFile sdrfFile = mock(CaArrayFile.class);
        when(sdrfFile.getFileType()).thenReturn(FileTypeRegistry.MAGE_TAB_SDRF);
        when(sdrfFile.getProject()).thenReturn(project);
        return sdrfFile;
    }

    private CaArrayFile getIdfFile(CaArrayFile sdrfFile) {
        CaArrayFile idfFile = mock(CaArrayFile.class);
        when(idfFile.getFileType()).thenReturn(FileTypeRegistry.MAGE_TAB_IDF);
        Project project = sdrfFile.getProject();
        when(idfFile.getProject()).thenReturn(project);
        return idfFile;
    }

    private File setupMageTabFileSets(CaArrayFile idfFile) throws IOException {
        FileRef idfRef = new CaArrayFileRef(idfFile, null);
        FileRef splitSdrfFileRef = mock(FileRef.class);
        File splitSdrfFile = mock(File.class);
        when(splitSdrfFileRef.getAsFile()).thenReturn(splitSdrfFile);

        MageTabFileSet mtfs = mock(MageTabFileSet.class);
        when(mtfs.getAllFiles()).thenReturn(Sets.newHashSet(idfRef, splitSdrfFileRef));

        Set<MageTabFileSet> mageTabFileSets = Sets.newHashSet(mtfs);
        when(mageTabFileSetSplitter.split(any(MageTabFileSet.class))).thenReturn(mageTabFileSets);
        return splitSdrfFile;
    }
    
    private Set<CaArrayFileSet> splitFiles(CaArrayFile sdrfFile,
            CaArrayFile idfFile) throws IOException {
        CaArrayFileSet arrayFileSet = mock(CaArrayFileSet.class);
        when(arrayFileSet.getFiles()).thenReturn(Sets.newHashSet(sdrfFile, idfFile));
        when(arrayFileSet.getProjectId()).thenReturn(1L);

        Set<CaArrayFileSet> results = cafsSplitter.split(arrayFileSet);
        return results;
    }

    private Set<CaArrayFile> addChildren(CaArrayFile sdrfFile) {
        CaArrayFile child1 = mock(CaArrayFile.class);
        when(child1.getParent()).thenReturn(sdrfFile);
        CaArrayFile child2 = mock(CaArrayFile.class);
        when(child2.getParent()).thenReturn(sdrfFile);
        Set<CaArrayFile> result = Sets.newHashSet(child1, child2);
        when(sdrfFile.getChildren()).thenReturn(result);
        
        return result;
    }
}
