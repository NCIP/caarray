/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
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
