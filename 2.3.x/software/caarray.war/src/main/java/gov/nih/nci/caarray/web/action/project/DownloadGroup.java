//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.util.LinkedList;
import java.util.List;

/**
 * Simple bean describing a set of files to be downloaded together.
 * @author dkokotov
 */
public class DownloadGroup {
    private final List<String> fileNames = new LinkedList<String>();
    private final List<Long> fileIds = new LinkedList<Long>();
    private long totalCompressedSize;
    private long totalUncompressedSize;

    /**
     * @return the names of the files in this download group
     */
    public List<String> getFileNames() {
        return fileNames;
    }

    /**
     * @return the ids of the files in this download group
     */
    public List<Long> getFileIds() {
        return fileIds;
    }

    /**
     * @return the total compressed size of the files in this download group
     */
    public long getTotalCompressedSize() {
        return totalCompressedSize;
    }

    /**
     * @return the total uncompressed of the files in this download group
     */
    public long getTotalUncompressedSize() {
        return totalUncompressedSize;
    }

    /**
     * Add given file to this group.
     * @param file the file to add
     */
    public void addFile(CaArrayFile file) {
        fileNames.add(file.getName());
        fileIds.add(file.getId());
        totalCompressedSize += file.getCompressedSize();
        totalUncompressedSize += file.getUncompressedSize();
    }
}
