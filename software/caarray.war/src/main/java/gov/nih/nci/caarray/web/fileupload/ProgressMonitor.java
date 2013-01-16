//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.fileupload;

import org.apache.commons.fileupload.ProgressListener;

/**
 * File upload progress monitor that keeps track of upload progress and reports it back to interested clients.
 * @author kokotovd
 */
public class ProgressMonitor implements ProgressListener {
    /**
     * Session attribute for storing the progress monitor.
     */
    public static final String SESSION_PROGRESS_MONITOR = "gov.nih.nci.caarray.web.fileupload.ProgressMonitor.";
    
    private static final int BYTES_READ_NOT_CHANGED_THRESHOLD = 3;
    private static final double PERCENT_MULTIPLIER = 100D;
    
    private long previousBytesRead;
    private int bytesReadNotChangedCount;
    private long bytesRead;
    private long bytesLength;
    private int itemNumber;
    private boolean aborted = false;

    /**
     * {@inheritDoc}
     */
    public void update(long pBytesRead, long pContentLength, int pItems) {
        bytesRead = pBytesRead;
        bytesLength = pContentLength;
        itemNumber = pItems;
    }

    /**
     * @return whether the upload is still in progress.
     */
    public boolean isStillProcessing() {
        return bytesReadNotChangedCount <= BYTES_READ_NOT_CHANGED_THRESHOLD;
    }

    /**
     * @return the current completion ratio, as a integer percentage (between 0 and 100).
     */
    public long percentComplete() {
        double percent = (double) bytesRead / (double) bytesLength;
        percent *= PERCENT_MULTIPLIER;
        return Math.round(percent);
    }

    /**
     * @return total bytes uploaded so far.
     */
    public long getBytesRead() {
        if (previousBytesRead == bytesRead) {
            bytesReadNotChangedCount++;
        } else {
            bytesReadNotChangedCount = 0;
            previousBytesRead = bytesRead;
        }
        return bytesRead;
    }

    /**
     * @return total number of bytes expected.
     */
    public long getBytesLength() {
        return bytesLength;
    }
        
    /**
     * @return the number of the current item being processed. Note that the items include
     * both file and non-file request parameters. 
     */
    public int getItemNumber() {
        return itemNumber;
    }

    /**
     * @return whether this upload has been aborted
     */
    public boolean isAborted() {
        return aborted;
    }

    /**
     * Indicate that the this upload has been aborted. 
     */
    public void abort() {
        this.aborted = true;
    }
}
