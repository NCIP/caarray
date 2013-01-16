//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.project;

import java.io.File;

/**
 * @author wcheng
 *
 */
public class FileWrapper {
    private File file;
    private String fileName;
    private long totalFileSize;
    private boolean compressed;

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }
    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * @return the totalFileSize
     */
    public long getTotalFileSize() {
        return totalFileSize;
    }
    /**
     * @param totalFileSize the totalFileSize to set
     */
    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }
    /**
     * @return the unpack
     */
    public boolean isCompressed() {
        return compressed;
    }
    /**
     * @param compressed the unpack to set
     */
    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
    /**
     * @return the partial
     */
    public boolean isPartial() {
        return file.length() != totalFileSize;
    }
}
