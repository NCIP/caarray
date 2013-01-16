//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.splitter;

import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.io.FileRef;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

/**
 * Basic implementation of the interface.
 * 
 * @author tparnell
 */
public class MageTabFileSetSplitterImpl implements MageTabFileSetSplitter {
    private static final Logger LOG = Logger.getLogger(MageTabFileSetSplitterImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MageTabFileSet> split(MageTabFileSet largeFileSet) throws IOException {
        if (largeFileSet == null) {
            return null;
        }
        
        return handleNonNullFileSet(largeFileSet);
    }

    private Set<MageTabFileSet> handleNonNullFileSet(MageTabFileSet largeFileSet) throws IOException {
        if (largeFileSet.getSdrfFiles().isEmpty()) {
            return Sets.newHashSet(largeFileSet);
        } else if (largeFileSet.getIdfFiles().size() > 1) {
            LOG.warn(">1 IDF file.  Not splitting SDRFs.");
            return Sets.newHashSet(largeFileSet);
        } else {
            return handleSdrfs(largeFileSet);
        }
    }    

    private Set<MageTabFileSet> handleSdrfs(MageTabFileSet largeFileSet) throws IOException {
        Set<MageTabFileSet> result = new HashSet<MageTabFileSet>();
        Set<FileRef> sdrfs = new HashSet<FileRef>(largeFileSet.getSdrfFiles());
        if (sdrfs.size() > 1) {
            LOG.warn("Multiple SDRFs in import set.  Proceeding with split.  " 
                        + "Split file associates may not be accurate");
        }
        for (FileRef curSdrf : sdrfs) {
            result.addAll(handleSingleSdrf(largeFileSet, curSdrf));
        }
        return result;
    }

    private Collection<MageTabFileSet> handleSingleSdrf(MageTabFileSet largeFileSet, FileRef sdrfFile) 
            throws IOException {
        FileRef idfFile = largeFileSet.getIdfFiles().iterator().next();
        Set<MageTabFileSet> result = new HashSet<MageTabFileSet>();
        SdrfSplitter sdrfSplitter = new SdrfSplitter(sdrfFile);
        for (FileRef singleDataFile : largeFileSet.getNativeDataFiles()) {
            MageTabFileSet curSet = addIdfAndSdrfForThisBatch(sdrfSplitter, idfFile, singleDataFile);
            curSet.addNativeData(singleDataFile);
            result.add(curSet);
        }
        for (FileRef singleDataFile : largeFileSet.getDataMatrixFiles()) {
            MageTabFileSet curSet = addIdfAndSdrfForThisBatch(sdrfSplitter, idfFile, singleDataFile);
            curSet.addDataMatrix(singleDataFile);
            result.add(curSet);
        }
        addMageTabSetForRemainingSdrf(sdrfSplitter, idfFile, result);
        return result;
    }

    private MageTabFileSet addIdfAndSdrfForThisBatch(SdrfSplitter sdrfSplitter, FileRef idfFile, FileRef singleDataFile)
            throws IOException {
        MageTabFileSet curSet = new MageTabFileSet();
        curSet.addIdf(idfFile);
        FileRef smallSdrf = sdrfSplitter.splitByDataFile(singleDataFile);
        curSet.addSdrf(smallSdrf);
        return curSet;
    }

    private void addMageTabSetForRemainingSdrf(SdrfSplitter sdrfSplitter, FileRef idfFile, Set<MageTabFileSet> result)
            throws IOException {
        FileRef smallSdrf = sdrfSplitter.splitByUnusedLines();
        if (smallSdrf != null) {
            MageTabFileSet curSet = new MageTabFileSet();
            curSet.addIdf(idfFile);
            curSet.addSdrf(smallSdrf);
            result.add(curSet);
        }
    }
}
