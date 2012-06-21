package wjhk.jupload2.upload;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import wjhk.jupload2.exception.JUploadEOFException;
import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.filepanel.FilePanel;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.helper.ProgressBarManager;

/**
 * This class is responsible for managing the upload. At the end of the upload,
 * the {@link JUploadPanel#updateButtonState()} is called, to refresh the button
 * state. Its job is to: <DIR> <LI>Prepare upload for the file (calls to
 * {@link FileData#beforeUpload()} for each file in the file list. <LI>Create
 * the thread to send a packet of files. <LI>Prepare the packets, that will be
 * red by the upload thread. <LI>Manage the end of upload: trigger the call to
 * {@link JUploadPanel#updateButtonState()} and the call to
 * {@link UploadPolicy#afterUpload(Exception, String)}. <LI>Manage the 'stop'
 * button reaction. </DIR> This class is created by {@link JUploadPanel}, when
 * the user clicks on the upload button.
 * 
 * @author etienne_sf
 */
public class FileUploadManagerThreadImpl extends Thread implements
        FileUploadManagerThread {

    /**
     * Maximum number of files that can be stored in the filePreparationQueue.
     * It's useless to have a too big value here, as, if too many files are
     * there, it means that the file preparation process is much quicker than
     * the file upload one.
     */
    final static int FILE_PREPARATION_QUEUE_SIZE = 50;

    // /////////////////////////////////////////////////////////////////////////////////////////
    // //////////////////// Possible Status for file upload
    // /////////////////////////////////////////////////////////////////////////////////////////

    /** The current file list. */
    FilePanel filePanel = null;

    /**
     * The file preparatoin thread prepares each file for upload, and manage
     * possible errors that can occurs at preparation time.
     */
    FilePreparationThread filePreparationThread = null;

    /**
     * This thread receives each prepared files in a queue, constructs the
     * packets of files to be sent together, and posts them in another queue.
     */
    PacketConstructionThread packetConstructionThread = null;

    /**
     * The upload thread, that will wait for the next file packet to be ready,
     * then send it.
     */
    FileUploadThread fileUploadThread = null;

    /**
     * This help controls the display of the progress bar and the status bar. It
     * updates these bar, based on a timer.
     */
    ProgressBarManager progressBarManager;

    /**
     * Number of files that have been successfully uploaded. already been sent.
     * The control on the upload success may be done or not. It's used to
     * properly display the progress bar.
     */
    int nbSuccessfullyUploadedFiles = 0;

    /**
     * Indicates whether the upload is finished or not. Passed to true as soon
     * as one of these conditions becomes true: <DIR> <LI>All files are uploaded
     * (in the {@link #currentRequestIsFinished(UploadFilePacket)} method) <LI>
     * An exception occurs (in the {@link #setUploadException(JUploadException)}
     * method) <LI>The user stops the upload (in the {@link #stopUpload()}
     * method) </DIR>
     */
    boolean uploadFinished = false;

    /**
     * If set to 'true', the thread will stop the current upload.
     * 
     * @see UploadFileData#uploadFile(java.io.OutputStream, long)
     */
    boolean stop = false;

    /** Thread Exception, if any occurred during upload. */
    JUploadException uploadException = null;

    /** A shortcut to the upload panel */
    JUploadPanel uploadPanel = null;

    /** The current upload policy. */
    UploadPolicy uploadPolicy = null;

    // ////////////////////////////////////////////////////////////////////////////
    // To follow the upload speed.
    // ////////////////////////////////////////////////////////////////////////////

    /**
     * Standard constructor of the class.
     * 
     * @param uploadPolicy
     * @throws JUploadException
     */
    public FileUploadManagerThreadImpl(UploadPolicy uploadPolicy)
            throws JUploadException {
        super("FileUploadManagerThreadImpl thread");
        constructor(uploadPolicy, null);
    }

    /**
     * Internal constructor. It is used by the JUnit test, to create a
     * FileUploadManagerThreadImpl instance, based on a non-active
     * {@link FileUploadThread}.
     * 
     * @param uploadPolicy The current uploadPolicy
     * @param fileUploadThreadParam The instance of {@link FileUploadThread}
     *            that should be used. Allows execution of unit tests, based on
     *            a specific FileUploadThread, that does ... nothing.
     * @throws JUploadException
     */
    FileUploadManagerThreadImpl(UploadPolicy uploadPolicy,
            FileUploadThread fileUploadThreadParam) throws JUploadException {
        super("FileUploadManagerThreadImpl test thread");
        constructor(uploadPolicy, fileUploadThreadParam);
    }

    /**
     * Called by the class constructors, to initialize the current instance.
     * 
     * @param uploadPolicy
     * @param fileUploadThreadParam
     * @throws JUploadException
     */
    private synchronized void constructor(UploadPolicy uploadPolicy,
            FileUploadThread fileUploadThreadParam) throws JUploadException {

        // General shortcuts on the current applet.
        this.uploadPolicy = uploadPolicy;
        this.uploadPanel = uploadPolicy.getContext().getUploadPanel();
        this.filePanel = this.uploadPanel.getFilePanel();

        BlockingQueue<UploadFileData> preparedFileQueue = new ArrayBlockingQueue<UploadFileData>(
                this.filePanel.getFilesLength());

        // If the FileUploadThread was already created, we must take the same
        // packetQueue.
        BlockingQueue<UploadFilePacket> packetQueue;
        if (fileUploadThreadParam == null) {
            packetQueue = new ArrayBlockingQueue<UploadFilePacket>(
                    this.filePanel.getFilesLength());
        } else {
            packetQueue = fileUploadThreadParam.getPacketQueue();
        }
        // Let's create (but not start) start the file preparation thread.
        this.filePreparationThread = new FilePreparationThread(
                preparedFileQueue, this, this.uploadPolicy);
        // The packet tread groups files together, depending on the current
        // upload policy.
        this.packetConstructionThread = new PacketConstructionThread(
                preparedFileQueue, packetQueue, this, this.uploadPolicy);
        // Let's start the upload thread. It will wait until the first
        // packet is ready.
        createUploadThread(packetQueue, fileUploadThreadParam);
        // We're now ready to start the bar update job.
        this.progressBarManager = new ProgressBarManager(this.uploadPolicy,
                this.filePreparationThread);
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#run()
     */
    @Override
    final public void run() {
        try {
            this.uploadPolicy.displayDebug(
                    "Start of the FileUploadManagerThreadImpl", 5);

            // Let's prepare the progress bar, to display the current upload
            // stage.
            progressBarManager.uploadIsStarted();

            // Let's start the working threads.
            this.filePreparationThread.start();
            this.packetConstructionThread.start();
            this.fileUploadThread.start();

            // Let's let the current upload policy have any preparation work
            this.uploadPolicy.beforeUpload();

            // The upload is started. Let's change the button state.
            this.uploadPanel.updateButtonState();

            // The thread upload may need some information about the current
            // one, like ... knowing that upload is actually finished (no more
            // file to send).
            // So we wait for it to finish.
            while (this.fileUploadThread.isAlive() && !isUploadFinished()) {
                try {
                    this.uploadPolicy.displayDebug(
                            "Waiting for fileUploadThread to die", 10);
                    this.fileUploadThread.join();
                } catch (InterruptedException e) {
                    // This should not occur, and should not be a problem. Let's
                    // trace a warning info.
                    this.uploadPolicy
                            .displayWarn("An InterruptedException occured in FileUploadManagerThreadImpl.run()");
                }
            }// while

            // If any error occurs, the prepared state of the file data may be
            // true. We must free resources. So, to be sure, we do it in all
            // cases.
            FileData[] fileDataArray = this.uploadPanel.getFilePanel()
                    .getFiles();
            for (int i = 0; i < fileDataArray.length; i += 1) {
                if (fileDataArray[i].isPreparedForUpload()) {
                    fileDataArray[i].afterUpload();
                }
            }

            // Let's restore the button state.
            this.uploadPanel.updateButtonState();
            this.uploadPolicy.getContext().showStatus("");
            this.uploadPolicy.getContext().getUploadPanel().getStatusLabel()
                    .setText("");

            // If no error occurs, we tell to the upload policy that a
            // successful
            // upload has been done.

            if (getUploadException() != null) {
                this.uploadPolicy.sendDebugInformation("Error in Upload",
                        getUploadException());
            } else if (isUploadStopped()) {
                this.uploadPolicy
                        .displayInfo("Upload stopped by the user. "
                                + this.nbSuccessfullyUploadedFiles
                                + " file(s) uploaded in "
                                + (int) ((System.currentTimeMillis() - this.progressBarManager
                                        .getGlobalStartTime()) / 1000)
                                + " seconds. Average upload speed: "
                                + ((this.progressBarManager.getUploadDuration() > 0) ? ((int) (this.progressBarManager
                                        .getNbUploadedBytes() / this.progressBarManager
                                        .getUploadDuration()))
                                        : 0) + " (kbytes/s)");
            } else {
                this.uploadPolicy
                        .displayInfo("Upload finished normally. "
                                + this.nbSuccessfullyUploadedFiles
                                + " file(s) uploaded in "
                                + (int) ((System.currentTimeMillis() - this.progressBarManager
                                        .getGlobalStartTime()) / 1000)
                                + " seconds. Average upload speed: "
                                + ((this.progressBarManager.getUploadDuration() > 0) ? ((int) (this.progressBarManager
                                        .getNbUploadedBytes() / this.progressBarManager
                                        .getUploadDuration()))
                                        : 0) + " (kbytes/s)");
                // FIXME uploadDuration displayed is 0!
                try {
                    this.uploadPolicy.afterUpload(this.getUploadException(),
                            this.fileUploadThread.getResponseMsg());
                } catch (JUploadException e1) {
                    this.uploadPolicy.displayErr(
                            "error in uploadPolicy.afterUpload (JUploadPanel)",
                            e1);
                }
            }

            // The job is finished. Let's stop the timer, and have a last
            // refresh of the bars.
            this.progressBarManager.uploadIsFinished();

            // We wait for 5 seconds, before clearing the progress bar.
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                // Nothing to do
            }
            // The job is finished for long enough, let's clear the progression
            // bars (and any associated ressource, like the time).
            // We'll clear the progress bar, only if this thread is in control
            // of the upload, that is: if this instance is the currently
            // FileUploadManagerThread referenced in the JUpload panel.
            if (this == this.uploadPanel.getFileUploadManagerThread()) {
                this.progressBarManager.clearBarContent();
                this.uploadPolicy.getContext().getUploadPanel()
                        .getStatusLabel().setText("");
            }

            this.uploadPolicy.displayDebug(
                    "End of the FileUploadManagerThreadImpl", 5);
        } catch (Exception e) {
            // We need a JUploadException.
            JUploadException jue = (e instanceof JUploadException) ? (JUploadException) e
                    : new JUploadException(e);
            setUploadException(jue);

            // And go back into a 'normal' way.
            stopUpload();
        } finally {
            // We restore the button state, just to be sure.
            this.uploadPanel.updateButtonState();
        }

        // And we die of our beautiful death ... until next upload.
    }// run

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#setUploadException(wjhk.jupload2.exception.JUploadException)
     */
    public synchronized void setUploadException(
            JUploadException uploadExceptionParam) {
        // If the user stops the upload, the socket on which the applet reads
        // the server response got closed. So we ignore this error.
        if (isUploadStopped()
                && uploadExceptionParam instanceof JUploadEOFException) {
            // Just ignore this error: the input stream from the server was
            // closed, but it probably occurs because the applet itself closed
            // the communicaiton.
        } else {
            // We don't override an existing exception
            if (this.uploadException != null) {
                this.uploadPolicy
                        .displayWarn("An exception has already been set in FileUploadManagerThreadImpl. The next one is just logged.");
            } else {
                this.uploadException = uploadExceptionParam;
            }

            String exceptionMsg = (uploadExceptionParam.getCause() == null) ? uploadExceptionParam
                    .getMessage()
                    : uploadExceptionParam.getCause().getMessage();
            String errMsg = this.uploadPolicy
                    .getLocalizedString("errDuringUpload")
                    + "\n\n" + exceptionMsg;
            this.uploadPolicy.displayErr(errMsg, uploadException);
        }
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#getUploadException()
     */
    public JUploadException getUploadException() {
        return this.uploadException;
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#isUploadFinished()
     */
    public boolean isUploadFinished() {
        // Indicate whether or not the upload is finished. Several conditions:
        // all files are uploaded, there was an error and the user stops the
        // upload here...
        return this.uploadFinished || this.stop || this.uploadException != null;
    }

    /**
     * @see FileUploadManagerThread#isUploadStopped()
     */
    public boolean isUploadStopped() {
        return this.stop;
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#nbBytesUploaded(long,
     *      UploadFileData)
     */
    public synchronized void nbBytesUploaded(long nbBytes,
            UploadFileData uploadFileData) throws JUploadException {
        this.progressBarManager.nbBytesUploaded(nbBytes, uploadFileData);
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#setUploadStatus(wjhk.jupload2.upload.UploadFilePacket,
     *      wjhk.jupload2.upload.UploadFileData, int)
     */
    public synchronized void setUploadStatus(UploadFilePacket uploadFilePacket,
            UploadFileData uploadFileData, int uploadStatus)
            throws JUploadException {
        this.progressBarManager.setUploadStatus(uploadFilePacket,
                uploadFileData, uploadStatus);
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#stopUpload()
     */
    public synchronized void stopUpload() {
        this.stop = true;

        // The upload is now finished ...
        this.uploadFinished = true;

        // We notify the various threads.
        if (this.filePreparationThread != null
                && this.filePreparationThread.isAlive()) {
            this.filePreparationThread.interrupt();
        }
        if (this.packetConstructionThread != null
                && this.packetConstructionThread.isAlive()) {
            this.packetConstructionThread.interrupt();
        }
        if (this.fileUploadThread != null && this.fileUploadThread.isAlive()) {
            this.fileUploadThread.interrupt();
        }

        // All 'sub-thread' is now interrupted. The upload thread can be stuck
        // while waiting for the server response. We also interrupt the current
        // thread.
        this.interrupt();
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////// SYNCHRONIZATION METHODS
    // //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#anotherFileHasBeenSent(wjhk.jupload2.upload.UploadFilePacket,
     *      wjhk.jupload2.upload.UploadFileData)
     */
    public synchronized void anotherFileHasBeenSent(
            UploadFilePacket uploadFilePacket,
            UploadFileData newlyUploadedFileData) throws JUploadException {
        this.progressBarManager.anotherFileHasBeenSent(uploadFilePacket,
                newlyUploadedFileData);
    }

    /**
     * @see wjhk.jupload2.upload.FileUploadManagerThread#currentRequestIsFinished(wjhk.jupload2.upload.UploadFilePacket)
     */
    public synchronized void currentRequestIsFinished(
            UploadFilePacket uploadFilePacket) throws JUploadException {
        // We are finished with this packet. Let's display it.
        this.progressBarManager.setUploadStatus(uploadFilePacket,
                uploadFilePacket.get(uploadFilePacket.size() - 1),
                FileUploadManagerThread.UPLOAD_STATUS_UPLOADED);

        // We should now remove this file from the list of files to upload,
        // to show the user that there is less and less work to do.
        for (FileData fileData : uploadFilePacket) {
            this.filePanel.remove(fileData);
            this.nbSuccessfullyUploadedFiles += 1;
        }

        // If all files have been sent, the upload is finished.
        if (!this.uploadFinished) {
            this.uploadFinished = (this.nbSuccessfullyUploadedFiles == this.filePreparationThread
                    .getNbFilesToSent());
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////
    // /////////////////// PRIVATE METHODS
    // //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the upload thread, but does not start it. IThis thread will wait
     * until the first packet is ready.
     * 
     * @throws JUploadException
     */
    private synchronized void createUploadThread(
            BlockingQueue<UploadFilePacket> packetQueue,
            FileUploadThread fileUploadThreadParam) throws JUploadException {
        if (fileUploadThreadParam != null) {
            // The FileUploadThread has already been created.
            // We set the FileUploadThreadManager.
            this.fileUploadThread = fileUploadThreadParam;
            fileUploadThreadParam.setFileUploadThreadManager(this);
        } else {
            try {
                if (this.uploadPolicy.getPostURL().substring(0, 4).equals(
                        "ftp:")) {
                    this.fileUploadThread = new FileUploadThreadFTP(
                            this.uploadPolicy, packetQueue, this);
                } else {
                    this.fileUploadThread = new FileUploadThreadHTTP(
                            this.uploadPolicy, packetQueue, this);
                }
            } catch (JUploadException e1) {
                // Too bad !
                this.uploadPolicy.displayErr(e1);
            }
        }
    }

    
    @Override
    public String getProgressInfoJSON( FileData[] fileDatas ) {
        return progressBarManager.getProgressInfoJSON( fileDatas );
    }
}
