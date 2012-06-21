package wjhk.jupload2.upload;

import java.util.concurrent.BlockingQueue;

import javax.swing.JProgressBar;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.filepanel.FilePanel;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * This thread is responsible for preparing all files for upload. It stores each
 * prepared file in the preparedFileQueue, for further processing.
 * 
 * @author etienne_sf
 */
public class FilePreparationThread extends Thread {

    /**
     * The array of files to send, from the {@link FilePanel}
     */
    FileData[] fileDataArray = null;

    /**
     * Number of files that are prepared for upload. A file is prepared for
     * upload, if the {@link FileData#beforeUpload()} has been called.
     */
    int nbPreparedFiles = 0;

    /**
     * The total of files that must be sent. It is initialized by the total
     * number of files in the list, and is decremented each time an error occurs
     * during a file preparation, and the user wants to go on.
     */
    int nbFilesToSent = -1;

    /**
     * Sum of the length for all prepared files. This allow the calculation of
     * the estimatedTotalLength.
     * 
     * @see #anotherFileIsPrepared(UploadFileData)
     */
    long nbTotalNumberOfPreparedBytes = 0;

    /**
     * The {@link JUploadPanel} progress bar, to follow the file preparation
     * progress.
     */
    JProgressBar preparationProgressBar = null;

    /** A shortcut to the upload panel */
    JUploadPanel uploadPanel = null;

    /** The current upload policy. */
    UploadPolicy uploadPolicy = null;

    /** The thread which globally manages the upload */
    FileUploadManagerThread fileUploadManagerThread = null;

    /** The current file list. */
    FilePanel filePanel = null;

    /**
     * The queue where each prepared file will be stored, for further processing
     */
    BlockingQueue<UploadFileData> preparedFileQueue = null;

    /**
     * @param preparedFileQueue
     * @param fileUploadManagerThread
     * @param uploadPolicy
     */
    public FilePreparationThread(
            BlockingQueue<UploadFileData> preparedFileQueue,
            FileUploadManagerThread fileUploadManagerThread,
            UploadPolicy uploadPolicy) {
        // A thread name is very useful, when debugging...
        super("FilePreparationThread");

        this.preparedFileQueue = preparedFileQueue;
        this.fileUploadManagerThread = fileUploadManagerThread;
        this.uploadPolicy = uploadPolicy;
        this.uploadPanel = uploadPolicy.getContext().getUploadPanel();
        this.filePanel = this.uploadPanel.getFilePanel();
        this.preparationProgressBar = this.uploadPanel
                .getPreparationProgressBar();

        // Prepare the list of files to upload. We do this here, to minimize the
        // risk of concurrency, if the user drops or pastes files on the applet
        // while uploading.
        this.fileDataArray = this.uploadPanel.getFilePanel().getFiles();

        // We know now the total of files we should upload:
        this.nbFilesToSent = fileDataArray.length;

        this.preparationProgressBar.setMaximum(100 * fileDataArray.length);
    }

    /**
     * The actual command to prepare files.
     * 
     * @see java.lang.Thread#run()
     * @see FileData#beforeUpload()
     */
    @Override
    final public void run() {
        // We loop through all files, and check before each if we should
        // stop (for instance if an error occurs)

        // numFileInCurrentUpload is the index of the file in the current index.
        // It should be the array index. But, if a file preparation fails in
        // error, numFileInCurrentUpload will be the array index minus 1, if 2
        // files, it will be the array index minus 2...
        int numFileInCurrentUpload = 0;

        for (int i = 0; i < fileDataArray.length
                && !this.fileUploadManagerThread.isUploadFinished(); i += 1) {
            try {
                UploadFileData uploadFileData = new UploadFileData(
                        fileDataArray[i], numFileInCurrentUpload,
                        this.fileUploadManagerThread, this.uploadPolicy);
                this.uploadPolicy.displayDebug(
                        "============== Start of file preparation ("
                                + uploadFileData.getFileName() + ")", 30);

                // Let's indicate to the user what's running on.
                this.preparationProgressBar.setString(this.uploadPolicy
                        .getLocalizedString("preparingFile", Integer
                                .valueOf(i + 1), Integer
                                .valueOf(fileDataArray.length)));
                // We want an immediate repaint, to be sure that the new text is
                // displayed to the user.
                this.preparationProgressBar.repaint(0);

                // Then, we work

                // Let's check that everything is Ok
                // More debug output, to understand where the applet freezes.
                this.uploadPolicy
                        .displayDebug(
                                this.getClass().getName()
                                        + ".prepareFiles(): before call to beforeUpload()",
                                100);

                try {
                    // Let's try to prepare the upload.
                    uploadFileData.beforeUpload();

                    // If we arrive, here, it means that beforeUpload() did not
                    // throw an exception, that is: the file is now prepared.
                    // Next file will be ... next in the current upload.
                    numFileInCurrentUpload += 1;

                    // TODO Whe error during file preparation ask the user.

                    this.uploadPolicy.displayDebug(
                            "============== End of file preparation ("
                                    + uploadFileData.getFileName() + ")", 30);
                    try {
                        anotherFileIsPrepared(uploadFileData);
                    } catch (InterruptedException e) {
                        // There was a problem putting the item. Let's try again
                        // in
                        // the next loop.
                        i -= 1;
                    }
                } catch (JUploadException e) {
                    // An error occurs during file preparation. We'll send one
                    // file less than expected.
                    this.nbFilesToSent -= 1;
                    throw e;
                }

                // The file preparation is finished. Let's update the progress
                // bar.
                this.preparationProgressBar
                        .setValue(this.nbPreparedFiles * 100);
                this.preparationProgressBar.repaint();
            } catch (JUploadException e) {
                this.fileUploadManagerThread.setUploadException(e);
            }
        }// while

        // All prepared files are posted on the preparedQueue. Let's send the
        // 'End of Queue' marker.
        try {
            this.preparedFileQueue.put(new UploadFileDataPoisonned());
        } catch (InterruptedException e) {
            // We should not be interrupted here. If it happens, it should be
            // because the upload was stopped. But, then, we may have the
            // PacketConstructionThread blocked, waiting for this packet. So,
            // let's log a warning.
            this.uploadPolicy
                    .displayWarn("Got interrupted, while posting the poisoned UploadFileData on the preparedQueue!");
        }

        // Let's clear the bar, which is no more accurate. We let the value to
        // 100%
        this.preparationProgressBar.setString("");
    }

    /**
     * This method is called each time a new file is ready to upload. It
     * calculates if a new packet of files is ready to upload. It is private, as
     * it may be called only from this class.
     * 
     * @throws JUploadException
     */
    private void anotherFileIsPrepared(UploadFileData newlyPreparedFileData)
            throws JUploadException, InterruptedException {
        this.nbPreparedFiles += 1;
        this.uploadPolicy
                .displayDebug(
                        this.getClass().getName()
                                + ".anotherFileIsReady(): before call(1) to newlyPreparedFileData.getUploadLength()",
                        100);
        this.uploadPolicy
                .displayDebug(
                        this.getClass().getName()
                                + ".checkIfNextPacketIsReady(): before call(2) to currentFileData.getUploadLength()",
                        100);
        this.nbTotalNumberOfPreparedBytes += newlyPreparedFileData
                .getUploadLength();

        this.preparedFileQueue.put(newlyPreparedFileData);
    }

    /**
     * Returns the total number of bytes to upload. This takes into account only
     * the prepared file content. It ignores the possible head and tails of the
     * request (for instance: http headers...). This gives a good idea of the
     * total amount to send, and allows is suffisiant to properly manage the
     * upload progress bar.<BR>
     * The total number of bytes can only be calculated when all files are
     * prepared. When this method is called before this, an estimation is done
     * for non prepared files, based on the average size of the already prepared
     * files.
     * 
     * @return The real or estimated total number of bytes to send
     */
    public double getTotalFileBytesToSend() {
        double totalFileBytesToSend;

        // Let's estimate the total, or calculate it, of all files are
        // prepared
        if (this.nbPreparedFiles == this.fileDataArray.length) {
            // All files are prepared: it's no more an estimation !
            totalFileBytesToSend = this.nbTotalNumberOfPreparedBytes;
        } else {
            // We sum the total number of prepared bytes, and we estimate
            // the size of the files that are not prepared yet
            totalFileBytesToSend = this.nbTotalNumberOfPreparedBytes
                    +
                    // And we sum it with the average amount per file
                    // prepared for the others
                    (this.fileDataArray.length - this.nbPreparedFiles)
                    * this.nbTotalNumberOfPreparedBytes / this.nbPreparedFiles;
        }

        return totalFileBytesToSend;
    }

    /**
     * @return the nbPreparedFiles
     */
    public int getNbPreparedFiles() {
        return nbPreparedFiles;
    }

    /**
     * @return the nbFilesToSent
     */
    public int getNbFilesToSent() {
        return nbFilesToSent;
    }

    /**
     * @return the nbTotalNumberOfPreparedBytes
     */
    public long getNbTotalNumberOfPreparedBytes() {
        return nbTotalNumberOfPreparedBytes;
    }
}
