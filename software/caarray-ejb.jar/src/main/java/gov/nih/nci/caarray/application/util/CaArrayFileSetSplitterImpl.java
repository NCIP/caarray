//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.util;

import gov.nih.nci.caarray.application.file.CaArrayFileRef;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.splitter.MageTabFileSetSplitter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Basic implementation of the interface.
 *
 * @author kkanchinadam
 */
public class CaArrayFileSetSplitterImpl implements CaArrayFileSetSplitter {

    private static final Logger LOG = Logger.getLogger(CaArrayFileSetSplitterImpl.class);
    
    private CaArrayFileSet inputFileSet;
    private Long projectId;
    private final DataStorageFacade dataStorageFacade;
    private final FileAccessService fileAccessService;
    private Map<FileRef, CaArrayFile> fileRefToCaArrayMap = new HashMap<FileRef, CaArrayFile>();
    private final MageTabFileSetSplitter mageTabFileSetSplitter;


    /**
     * Constructor for the implementation.
     * @param fileAccessService the FileAccessService.
     * @param dataStorageFacade the DataStorageFacade.
     * @param mageTabFileSetSplitter the MageTabFileSetSplitter.
     */
    @Inject
    public CaArrayFileSetSplitterImpl(FileAccessService fileAccessService,
            DataStorageFacade dataStorageFacade, MageTabFileSetSplitter mageTabFileSetSplitter) {
        this.fileAccessService = fileAccessService;
        this.dataStorageFacade = dataStorageFacade;
        this.mageTabFileSetSplitter = mageTabFileSetSplitter;
    }

    /**
     * Given a large CaArrayFileSet, returns a set of smaller CaArrayFileSet.
     * @param fileset the input CaArrayFileSet that needs to be split.
     * @return A set of smaller split CaArrayFileSet's.
     * @throws IOException if file management fails during split.
     */
    public Set<CaArrayFileSet> split(CaArrayFileSet fileset)
            throws IOException {
        this.inputFileSet = fileset;

        if (fileset == null) {
            return null;
        }

        if (fileset.getFiles().isEmpty()) {
            return Sets.newHashSet(this.inputFileSet);
        }

        cleanupFileOrphans();
        
        this.projectId = this.inputFileSet.getProjectId();

        // setup the initial FileRef to CaArrayFile map.
        fileRefToCaArrayMap = getFileRefToCaArrayFileMap();

        // get the large mage tab file set, and split.
        MageTabFileSet largeMageTabFileSet = getMageTabFileSet();
        Set<MageTabFileSet> splitMageTabFileSet = mageTabFileSetSplitter.split(largeMageTabFileSet);

        CaArrayFile parentSdrfCaArrayFile = getParentSdrfCaArrayFile();
        return handleNonNullSplitFileSet(splitMageTabFileSet, parentSdrfCaArrayFile);
    }

    private void cleanupFileOrphans() {
        for (CaArrayFile file : inputFileSet.getFiles()) {
            Set<CaArrayFile> orphans = new HashSet<CaArrayFile>();
            orphans.addAll(file.getChildren());
            for (CaArrayFile child : orphans) {
                LOG.info("Cleaning up orphaned child for parent file id=" + file.getId());
                fileAccessService.remove(child);
            }
        }
    }

    private Map<FileRef, CaArrayFile> getFileRefToCaArrayFileMap() {
        Map<FileRef, CaArrayFile> map = new HashMap<FileRef, CaArrayFile>();
        for (CaArrayFile caArrayFile : this.inputFileSet.getFiles()) {
            final FileRef fileRef = getFileRef(caArrayFile);
            map.put(fileRef,  caArrayFile);
        }
        return map;
    }

    private CaArrayFile getParentSdrfCaArrayFile() {
        CaArrayFile parent = null;
        Set<CaArrayFile> sdrfFiles = inputFileSet.getFilesByType(FileTypeRegistry.MAGE_TAB_SDRF);
        if (!sdrfFiles.isEmpty()) {
            parent = sdrfFiles.iterator().next();
        }
        return parent;
    }

    private Set<CaArrayFileSet> handleNonNullSplitFileSet(Set<MageTabFileSet> splitMtfs,
            CaArrayFile parentSdrf) {
        Set<CaArrayFileSet> splitCaArrayFileSets = new HashSet<CaArrayFileSet>();
        for (MageTabFileSet mtfs : splitMtfs) {
            CaArrayFileSet cafs = getCaArrayFileSet(mtfs, parentSdrf);
            splitCaArrayFileSets.add(cafs);
        }

        return splitCaArrayFileSets;
    }

    private CaArrayFileSet getCaArrayFileSet(MageTabFileSet mtfs, CaArrayFile parentSdrf) {
        CaArrayFileSet caArrayFileSet = new CaArrayFileSet(this.projectId);
        for (FileRef fileRef : mtfs.getAllFiles()) {
            CaArrayFile caArrayFile = fileRefToCaArrayMap.get(fileRef);
            if (caArrayFile == null) {
                // create the file with the parent.
                caArrayFile = getChildCaArrayFile(fileRef, parentSdrf);
            }
            caArrayFileSet.add(caArrayFile);
        }
        return caArrayFileSet;
    }

    private CaArrayFile getChildCaArrayFile(FileRef fileRef, CaArrayFile parent) {
        File file = fileRef.getAsFile();
        CaArrayFile caArrayFile = this.fileAccessService.add(file, parent);
        // this is probably not needed, but update the fileRefToCaArrayMap anyways.
        fileRefToCaArrayMap.put(fileRef, caArrayFile);
        return caArrayFile;
    }

    private FileRef getFileRef(CaArrayFile caArrayFile) {
        return new CaArrayFileRef(caArrayFile, this.dataStorageFacade);
    }

    private MageTabFileSet getMageTabFileSet() {
        MageTabFileSet mageTabFileSet  = new MageTabFileSet();
        for (CaArrayFile caArrayFile : this.inputFileSet.getFiles()) {
            final FileRef fileRef = this.getFileRef(caArrayFile);

            if (isMageTabIdf(caArrayFile)) {
                mageTabFileSet.addIdf(fileRef);
            } else if (isMageTabSdrf(caArrayFile)) {
                mageTabFileSet.addSdrf(fileRef);
            } else if (isDataMatrix(caArrayFile)) {
                mageTabFileSet.addDataMatrix(fileRef);
            } else {
                mageTabFileSet.addNativeData(fileRef);
            }
        }
        return mageTabFileSet;
    }

    private boolean isMageTabIdf(CaArrayFile caArrayFile) {
        return FileTypeRegistry.MAGE_TAB_IDF.equals(caArrayFile.getFileType());
    }

    private boolean isMageTabSdrf(CaArrayFile caArrayFile) {
        return FileTypeRegistry.MAGE_TAB_SDRF.equals(caArrayFile.getFileType());
    }

    private boolean isDataMatrix(CaArrayFile caArrayFile) {
        return caArrayFile.getFileType().isDataMatrix();
    }
}
