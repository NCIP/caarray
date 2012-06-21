//
// $Id: DefaultFileUploadThread.java 287 2007-06-17 09:07:04 +0000 (dim., 17
// juin 2007) felfert $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2011-01-12 18:49:37 +0100 (mer., 12 janv. 2011) $
//
// This program is free software; you can redistribute it and/or modify it under
// the terms of the GNU General Public License as published by the Free Software
// Foundation; either version 2 of the License, or (at your option) any later
// version. This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details. You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation, Inc.,
// 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.upload;

import java.io.OutputStream;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadExceptionUploadFailed;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.exception.JUploadInterrupted;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.DialogUploadRetry;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * This class is based on the {@link FileUploadThread} class. It's an abstract
 * class that contains the default implementation for the
 * {@link FileUploadThread} interface. <BR>
 * It contains the following abstract methods, which must be implemented in the
 * children classes. These methods are called in this order: <DIR> <LI>For each
 * upload request (for instance, upload of 3 files with nbFilesPerRequest to 2,
 * makes 2 request: 2 files, then the last one): <DIR> <LI><I>try</I> <LI>
 * {@link #startRequest}: start of the UploadRequest. <LI>Then, for each file to
 * upload (according to the nbFilesPerRequest and maxChunkSize applet
 * parameters) <DIR> <LI>beforeFile(int) is called before writting the bytes for
 * this file (or this chunk) <LI>afterFile(int) is called after writting the
 * bytes for this file (or this chunk) </DIR> <LI>finishRequest() </DIR></LI>
 * <I>finally</I>cleanRequest() <LI>Call of cleanAll(), to clean up any used
 * resources, common to the whole upload. </DIR>
 */
public abstract class DefaultFileUploadThread extends Thread implements
        FileUploadThread {

    // ////////////////////////////////////////////////////////////////////////
    // /////////////////////// VARIABLES //////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    /**
     * The queue that'll transmit each packet to upload to the server.
     */
    BlockingQueue<UploadFilePacket> packetQueue = null;

    /**
     * The upload manager. The thread that prepares files, and is responsible to
     * manage the upload process.
     * 
     * @see FileUploadManagerThread
     */
    FileUploadManagerThread fileUploadManagerThread = null;

    /**
     * The upload policy contains all parameters needed to define the way files
     * should be uploaded, including the URL.
     */
    protected UploadPolicy uploadPolicy = null;

    /**
     * The value of the applet parameter maxChunkSize, or its default value.
     */
    private long maxChunkSize;

    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////// PRIVATE ATTRIBUTES
    // ////////////////////////////////////////////////////////////////////////////////////

    // This variable should only be set to false. Setting to true should be used only to 
    // debug the JUPload retry feature. 
    static boolean sendResumableError = false;

    /**
     * Current retry number, for the resume upload feature. The first try occurs
     * with nbRetry=0.
     */
    int nbRetry = 0;

    /**
     * The full response message from the server, if any. For instance, in HTTP
     * mode, this contains both the headers and the body.
     */
    protected String responseMsg = "";

    /**
     * The response message from the application. For instance, in HTTP mode,
     * this contains the body response.<BR>
     * Note: for easier management on the various server configurations, all end
     * or line characters (CR, LF or CRLF) are changed to uniform CRLF.
     */
    protected String responseBody = "";

    /**
     * Creates a new instance.
     * 
     * @param threadName The name of the thread, that will be displayed in the
     *            debugger and in the logs.
     * @param packetQueue The queue from wich packets to upload are available.
     * @param uploadPolicy The upload policy to be applied.
     * @param fileUploadManagerThread The thread that is managing the upload.
     */
    public DefaultFileUploadThread(String threadName,
            BlockingQueue<UploadFilePacket> packetQueue,
            UploadPolicy uploadPolicy,
            FileUploadManagerThread fileUploadManagerThread) {
        // Thread parameters.
        super(threadName);

        // Specific stuff.
        this.packetQueue = packetQueue;
        this.uploadPolicy = uploadPolicy;
        this.fileUploadManagerThread = fileUploadManagerThread;
        // Let's read up to date upload parameters.
        this.maxChunkSize = this.uploadPolicy.getMaxChunkSize();

        this.uploadPolicy.displayDebug("DefaultFileUploadThread created", 30);
    }

    /**
     * This method is called before the upload. It calls the
     * {@link FileData#beforeUpload()} method for all files to upload, and
     * prepares the progressBar bar (if any), with total number of bytes to
     * upload. final private void beforeUpload() throws JUploadException { for
     * (int i = 0; i < this.filesToUpload.length &&
     * !this.fileUploadManager.isUploadStopped(); i++) {
     * this.filesToUpload[i].beforeUpload(); } } /** This methods upload
     * overhead for the file number indexFile in the filesDataParam given to the
     * constructor. For instance, in HTTP, the upload contains a head and a tail
     * for each files.
     * 
     * @param uploadFileData The file whose additional length is asked.
     * @return The additional number of bytes for this file.
     */
    abstract long getAdditionnalBytesForUpload(UploadFileData uploadFileData)
            throws JUploadIOException;

    /**
     * This method is called before starting of each request. It can be used to
     * prepare any work, before starting the request. For instance, in HTTP, the
     * tail must be properly calculated, as the last one must be different from
     * the others.<BR>
     * The packets to send are available through the {@link #packetQueue} queue.
     */
    abstract void beforeRequest(UploadFilePacket packet)
            throws JUploadException;

    /**
     * This method is called for each upload request to the server. The number
     * of request to the server depends on: <DIR> <LI>The total number of files
     * to upload. <LI>The value of the nbFilesPerRequest applet parameter. <LI>
     * The value of the maxChunkSize applet parameter. </DIR> The main objective
     * of this method is to open the connection to the server, where the files
     * to upload will be written. It should also send any header necessary for
     * this upload request. The {@link #getOutputStream()} methods is then
     * called to know where the uploaded files should be written. <BR>
     * Note: it's up to the class containing this method to internally manage
     * the connection.
     * 
     * @param contentLength The total number of bytes for the files (or the
     *            chunk) to upload in this query.
     * @param bChunkEnabled True if this upload is part of a file (can occurs
     *            only if the maxChunkSize applet parameter is set). False
     *            otherwise.
     * @param chunkPart The chunk number. Should be ignored if bChunkEnabled is
     *            false.
     * @param bLastChunk True if in chunk mode, and this upload is the last one.
     *            Should be ignored if bChunkEnabled is false.
     */
    abstract void startRequest(long contentLength, boolean bChunkEnabled,
            int chunkPart, boolean bLastChunk) throws JUploadException;

    /**
     * This method is called at the end of each request.
     * 
     * @return The response status code from the server (200 == OK)
     * @see #startRequest(long, boolean, int, boolean)
     */
    abstract int finishRequest() throws JUploadException;

    /**
     * Reaction of the upload thread, when an interruption has been received.
     * This method should close all resource to the server, to allow the server
     * to free any resource (temporary file, network connection...).
     */
    abstract void interruptionReceived();

    /**
     * This method is called before sending the bytes corresponding to the file
     * whose index is given in argument. If the file is splitted in chunks (see
     * the maxChunkSize applet parameter), this method is called before each
     * chunk for this file.
     * 
     * @param uploadFilePacket The bunch of files in the current request
     * @param uploadFileData The next file that will be sent
     */
    abstract void beforeFile(UploadFilePacket uploadFilePacket,
            UploadFileData uploadFileData) throws JUploadException;

    /**
     * Idem as {@link #beforeFile(UploadFilePacket, UploadFileData)}, but is
     * called after each file (and each chunks for each file).
     * 
     * @param uploadFileData The file that was just sent.
     */
    abstract void afterFile(UploadFileData uploadFileData)
            throws JUploadException;

    /**
     * Clean any used resource of the last executed request. In HTTP mode, the
     * output stream, input stream and the socket should be cleaned here.
     */
    abstract void cleanRequest() throws JUploadException;

    /**
     * Clean any used resource, like a 'permanent' connection. This method is
     * called after the end of the last request (see on the top of this page for
     * details).
     */
    abstract void cleanAll() throws JUploadException;

    /**
     * Get the output stream where the files should be written for upload.
     * 
     * @return The target output stream for upload.
     */
    abstract OutputStream getOutputStream() throws JUploadException;

    /**
     * Return the the body for the server response. That is: the server response
     * without the http header. This is the functional response from the server
     * application, that has been as the HTTP reply body, for instance: all
     * 'echo' PHP commands. <BR>
     * 
     * @return The last application response (HTTP body, in HTTP upload)
     */
    public String getResponseBody() {
        return this.responseBody;
    }

    /**
     * Get the server Output.
     * 
     * @return The status message from the first line of the response (e.g. "200
     *         OK").
     */
    public String getResponseMsg() {
        return this.responseMsg;
    }

    /**
     * @return the packetQueue
     */
    public BlockingQueue<UploadFilePacket> getPacketQueue() {
        return packetQueue;
    }

    /**
     * Unused Store the String that contains the server response body.
     * 
     * @param body The response body that has been read.
     */
    void setResponseBody(String body) {
        this.responseBody = normalizeCRLF(body);
    }

    /**
     * Add a String that has been read from the server response.
     * 
     * @param msg The status message from the first line of the response (e.g.
     *            "200 OK").
     */
    void setResponseMsg(String msg) {
        this.responseMsg = normalizeCRLF(msg);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // /////////////////////// PRIVATE FUNCTIONS
    // ////////////////////////////////////////////////////////////////////////////////////

    /**
     * This method waits for a packet on the packetQueue. Then, it calls the
     * doUpload() method, to send these files to the server.
     */
    @Override
    final public void run() {
        this.uploadPolicy.displayDebug("Start of the FileUploadThread", 5);

        // We'll stop the upload if an error occurs. So the try/catch is
        // outside the while.
        while (!this.fileUploadManagerThread.isUploadFinished()) {
            UploadFilePacket packet = null;

            try {
                // We take the next packet. This method will block until a
                // packet is ready.
                packet = packetQueue.take();

                // If the packet is 'poisonned', then it's the standard end of
                // work.
                if (packet.isPoisonned()) {
                    break;
                }

                // /////////////////////////////////////////////////////////////////////////////////
                // Let's go to work : THIS IS THE UPLOAD, surrounded by the
                // RESUME LOOP
                // /////////////////////////////////////////////////////////////////////////////////
                nbRetry = 0;
                while (true) {
                    try {
                        // Let's try to upload the current packet.
                        doUpload(packet);

                        // If we are here, the last upload is a success. Let's
                        // exit the loop.
                        break;
                    } catch (JUploadException jue) {
                        // manageRetry throw the exception, if no retry should
                        // be done.
                        manageRetry(jue);

                        // If we are here, the applet should retry the upload.
                        // We let it loop again.
                        nbRetry += 1;

                        // We must clean the resources of the previous attempt.
                        beforeRetry(packet);
                    }
                }// while(resume)
                // /////////////////////////////////////////////////////////////////////////////////
                // //////////////// ENF OF RESUME LOOP
                // /////////////////////////////////////////////////////////////////////////////////

                this.uploadPolicy.displayDebug("After do upload", 50);
            } catch (InterruptedException e) {
                this.uploadPolicy.displayWarn(this.getClass().getName()
                        + ".run(): received in " + e.getClass().getName()
                        + ", exiting...");
                break;
            } catch (JUploadException e) {
                if (this.fileUploadManagerThread.isUploadFinished()) {
                    // We ignore the error
                    this.uploadPolicy.displayWarn("Ignoring "
                            + e.getClass().getName()
                            + " because upload is finished");
                } else {
                    this.fileUploadManagerThread.setUploadException(e);
                }
            } catch (JUploadInterrupted e) {
                // The upload has been interrupted, probably by the user
                // (stop
                // button). The fileManagerUploadThread aleady knows this.
                this.uploadPolicy.displayInfo("Upload stopped by the user");
                this.uploadPolicy.displayDebug(e.getMessage(), 30);
			} finally {
				// Let's free any locked resource for the current packet.
				// This is done here, to allow the resume feature (and, even in
				// case an error occurs, we free resources only after the last
				// retry)
				for (UploadFileData uploadFileData : packet) {
					if (uploadFileData.isPreparedForUpload()) {
						uploadFileData.afterUpload();
					}
				}
            }
        }// while (!isUploadFinished)

        this.uploadPolicy.displayDebug("End of the FileUploadThread", 5);
    }// run

    /**
     * @param jue
     * @throws JUploadException
     */
    private void manageRetry(JUploadException jue) throws JUploadException {
        String exceptionCauseClass = (jue.getCause() == null) ? "no exception cause"
                : jue.getCause().getClass().getName();
        String errMsg = jue.getClass().getName() + " (" + jue.getMessage()
                + "), caused by: " + exceptionCauseClass;

        if (this.fileUploadManagerThread.isUploadFinished()) {
            // The upload is stopped. This error may be caused
            this.uploadPolicy
                    .displayWarn("The following error occurs, but the upload is stopped, ignoring it ]"
                            + errMsg + "]");
            throw jue;
        } else if (jue.getCause() instanceof SocketException) {
            this.uploadPolicy.displayWarn("A 'resumable' error occurred: "
                    + errMsg);
            // If it was the last retry, we stop here.
            if (nbRetry >= this.uploadPolicy.getRetryMaxNumberOf()) {
                this.uploadPolicy.displayWarn("Too much retries (" + nbRetry
                        + "), exiting...");
                throw jue;
            }

            DialogUploadRetry dialogUploadRetry = new DialogUploadRetry(
                    this.uploadPolicy.getContext().getFrame(), jue, nbRetry,
                    this.uploadPolicy);
            // The constructor returns, when the dialog is closed. Let's check
            // the user answer:
            if (dialogUploadRetry.isRetryValidated()) {
                this.uploadPolicy.displayDebug(
                        "The user (or the timer) choosed to retry the upload",
                        30);
            } else {
                this.uploadPolicy.displayDebug(
                        "The user refuses to retry the upload, exiting...", 30);
                // No retry, let's note the exception and go out
                throw jue;
            }// End of resumable exceptions management.
        } else {
            // This exception can't be resumed. We transmit it.
            this.uploadPolicy
                    .displayWarn("Non resumable error occured, exiting...");
            throw jue;
        }
    }

    /**
     * Actual execution file(s) upload. It's called by the run methods, once for
     * all files, or file by file, depending on the UploadPolicy. The list of
     * files to upload is stored in the packet parameter.<BR>
     * This method is called by the run() method. The prerequisite about the
     * filesToUpload array are: <DIR> <LI>If the sum of contentLength for the
     * files in the array is more than the maxChunkSize, then
     * nbFilesToUploadParam is one. <LI>The number of elements in filesToUpload
     * is less (or equal) than the nbMaxFilesPerUpload. </DIR>
     * 
     * @throws JUploadException
     * @throws JUploadInterrupted Thrown when an interruption of the thread is
     *             detected.
     */
    final private void doUpload(UploadFilePacket packet)
            throws JUploadException, JUploadInterrupted {
        boolean bChunkEnabled = false;
        long totalContentLength = 0;
        long totalFileLength = 0;

        // We are about to start a new upload.
        this.fileUploadManagerThread.setUploadStatus(packet, packet.get(0),
                FileUploadManagerThread.UPLOAD_STATUS_UPLOADING);

        // Prepare upload, for all files to be uploaded.
        beforeRequest(packet);

        for (UploadFileData uploadFileData : packet) {
            // The upload may be finished, while we're working on the files...
            if (this.fileUploadManagerThread.isUploadFinished()) {
                // Let's stop our work here.
                return;
            }
            // Total length, for HTTP upload.
            totalContentLength += uploadFileData.getUploadLength();
            totalContentLength += getAdditionnalBytesForUpload(uploadFileData);
            // Total file length: used to manage the progress bar (we don't
            // follow the bytes uploaded within headers and forms).
            totalFileLength += uploadFileData.getUploadLength();

            this.uploadPolicy.displayDebug("file "
                    + uploadFileData.getFileName() + ": content="
                    + uploadFileData.getUploadLength()
                    + " bytes, getAdditionnalBytesForUpload="
                    + getAdditionnalBytesForUpload(uploadFileData) + " bytes",
                    50);
        }// for

        // Ok, now we check that the totalContentLength is less than the chunk
        // size.
        if (totalFileLength >= this.maxChunkSize) {
            // hum, hum, we have to download file by file, with chunk enabled.
            // This a prerequisite of this method.
            if (packet.size() > 1) {
                this.fileUploadManagerThread
                        .setUploadException(new JUploadException(
                                "totalContentLength >= chunkSize: this.filesToUpload.length should be 1 (doUpload)"));
            }
            bChunkEnabled = true;
        }

        // Now, we can actually do the job. This is delegate into smaller
        // method, for easier understanding.
        if (bChunkEnabled) {
            // No more than one file, when in chunk mode.
            if (packet.size() > 1) {
                throw new JUploadException(
                        "totalContentLength >= chunkSize: this.filesToUpload.length should not be more than 1 (doUpload)");
            }
            doChunkedUpload(packet);
        } else {
            doNonChunkedUpload(packet, totalContentLength, totalFileLength);
        }

        // If the request properly finished, we remove the files from the list
        // of files to upload.
        if (this.fileUploadManagerThread.getUploadException() == null
                && !this.fileUploadManagerThread.isUploadStopped()) {
            this.fileUploadManagerThread.currentRequestIsFinished(packet);
        }
    }

    /**
     * Execution of an upload, in chunk mode. This method expects that the given
     * packet contains only one file.
     * 
     * @param packet The packet that contains the file to upload in chunk mode
     * @throws JUploadException When any error occurs, or when there is more
     *             than one file in packet.
     * @throws JUploadInterrupted Thrown when an interruption of the thread is
     *             detected.
     */
    final private void doChunkedUpload(UploadFilePacket packet)
            throws JUploadException, JUploadInterrupted {
        boolean bLastChunk = false;
        int chunkPart = 0;

        long contentLength = 0;
        long thisChunkSize = 0;

        if (packet.size() > 1) {
            throw new JUploadException(
                    "doChunkedUpload called with a packet of more than 1 file ("
                            + packet.size() + " files)");
        }
        UploadFileData uploadFileData = packet.get(0);

        // This while enables the chunk management:
        // In chunk mode, it loops until the last chunk is uploaded. This works
        // only because, in chunk mode, files are uploaded one y one (the for
        // loop within the while loops through ... 1 unique file).
        // In normal mode, it does nothing, as the bLastChunk is set to true in
        // the first test, within the while.
        while (!bLastChunk && !this.fileUploadManagerThread.isUploadFinished()) {
            // Let's manage chunk:
            // Files are uploaded one by one. This is checked just above.
            chunkPart += 1;
            bLastChunk = (contentLength > uploadFileData.getRemainingLength());

            // Is this the last chunk ?
            if (bLastChunk) {
                thisChunkSize = uploadFileData.getRemainingLength();
            } else {
                thisChunkSize = this.maxChunkSize;
            }
            contentLength = thisChunkSize
                    + getAdditionnalBytesForUpload(uploadFileData);

            // We are about to start a new upload.
            this.fileUploadManagerThread.setUploadStatus(packet,
                    uploadFileData,
                    FileUploadManagerThread.UPLOAD_STATUS_UPLOADING);

            // Ok, we've prepare the job for chunk upload. Let's do it!
            startRequest(contentLength, true, chunkPart, bLastChunk);

            try {

                // Let's add any file-specific header.
                beforeFile(packet, uploadFileData);

                // Actual upload of the file:
                uploadFileData.uploadFile(getOutputStream(), thisChunkSize);

                // Caution : this is for debug only. In production mode, sendResumableError should always be 'false'
                if (chunkPart == 2 && sendResumableError) {
                    sendResumableError = false;
                    throw new JUploadException(
                            new SocketException(
                                    "This is a debug error. Should not happen in production."));
                }

                // If we are not in chunk mode, or if it was the last chunk,
                // upload should be finished.
                if (bLastChunk && uploadFileData.getRemainingLength() > 0) {
                    throw new JUploadExceptionUploadFailed(
                            "Files has not be entirely uploaded. The remaining size is "
                                    + uploadFileData.getRemainingLength()
                                    + " bytes. File size was: "
                                    + uploadFileData.getUploadLength()
                                    + " bytes.");

                }
                // Let's add any file-specific header.
                afterFile(uploadFileData);

                // Let's finish the request, and wait for the server Output, if
                // any (not applicable in FTP)
                int status = finishRequest();

                if (bLastChunk) {
                    // We are finished with this one. Let's display it.
                    this.fileUploadManagerThread
                            .setUploadStatus(
                                    packet,
                                    uploadFileData,
                                    FileUploadManagerThread.UPLOAD_STATUS_FILE_UPLOADED_WAITING_FOR_RESPONSE);
                } else {
                    // We are finished with the current chunk, but not with the
                    // file. Let's display it.
                    this.fileUploadManagerThread
                            .setUploadStatus(
                                    packet,
                                    uploadFileData,
                                    FileUploadManagerThread.UPLOAD_STATUS_CHUNK_UPLOADED_WAITING_FOR_RESPONSE);
                }

                // We now ask to the uploadPolicy, if it was a success.
                // If not, the isUploadSuccessful should raise an exception.
                this.uploadPolicy.checkUploadSuccess(status, getResponseMsg(),
                        getResponseBody());
            }// try
            finally {
                cleanRequest();
            }
        }// while
        // Let's tell our manager that we've done the job!
        this.fileUploadManagerThread.anotherFileHasBeenSent(packet,
                uploadFileData);

    }// doChunkedUpload

    /**
     * Execution of an upload, in standard mode. This method uploads all files
     * in the given packet.
     * 
     * @param packet The files to upload in the current request to the server
     * @param totalContentLength The total size of the upload, including any
     *            protocol-specific header or footer.
     * @param totalFileLength The sum of each file length.
     * @throws JUploadException When any error occurs
     * @throws JUploadInterrupted Thrown when an interruption of the thread is
     *             detected.
     */
    final private void doNonChunkedUpload(UploadFilePacket packet,
            final long totalContentLength, final long totalFileLength)
            throws JUploadException, JUploadInterrupted {

        // First step is to prepare all files.
        startRequest(totalContentLength, false, 0, true);

        try {

            // Then, upload each file.
            for (UploadFileData uploadFileData : packet) {
                if (this.fileUploadManagerThread.isUploadFinished()) {
                    // Upload is finished (by the user or because of an error,
                    // or
                    // instance)
                    break;
                }
                // We are about to start a new upload.
                this.fileUploadManagerThread.setUploadStatus(packet,
                        uploadFileData,
                        FileUploadManagerThread.UPLOAD_STATUS_UPLOADING);

                // Let's add any file-specific header.
                beforeFile(packet, uploadFileData);

                // Actual upload of the file:
                if (!this.fileUploadManagerThread.isUploadFinished()) {
                    uploadFileData.uploadFile(getOutputStream(), uploadFileData
                            .getUploadLength());
                }

                // Let's add any file-specific header.
                if (!this.fileUploadManagerThread.isUploadFinished()) {
                    afterFile(uploadFileData);

                    // Let's tell our manager that we've done the job!
                    // Ok, maybe the server will refuse it, but we won't say
                    // that
                    // now!
                    this.fileUploadManagerThread.anotherFileHasBeenSent(packet,
                            uploadFileData);
                }
            }

            // We are finished with this one. Let's display it.
            if (!this.fileUploadManagerThread.isUploadFinished()) {
                this.fileUploadManagerThread
                        .setUploadStatus(
                                packet,
                                packet.get(packet.size() - 1),
                                FileUploadManagerThread.UPLOAD_STATUS_FILE_UPLOADED_WAITING_FOR_RESPONSE);

                // Let's finish the request, and wait for the server Output, if
                // any (not applicable in FTP)
                int status = finishRequest();

                // We now ask to the uploadPolicy, if it was a success.
                // If not, the isUploadSuccessful should raise an exception.
                this.uploadPolicy.checkUploadSuccess(status, getResponseMsg(),
                        getResponseBody());
            }
        }// try
        finally {
            cleanRequest();
        }

    }// doNonChunkedUpload

    /**
     * Clean any resource of the last attempt for this packet, which would be in
     * inconsistent step, in order to retry the upload of the current packet.
     */
    private void beforeRetry(UploadFilePacket packet) throws JUploadException {
        if (packet != null) {
            for (UploadFileData uploadFileData : packet) {
                if (uploadFileData.isPreparedForUpload()) {
                    uploadFileData.beforeRetry(); 
                }
            }
        }
    }

    /** @see FileUploadThread#close() */
    public void close() {
        try {
            cleanAll();
        } catch (JUploadException e) {
            this.uploadPolicy.displayErr(e);
        }
    }

    /**
     * Replace single \r and \n by uniform end of line characters (CRLF). This
     * makes it easier, to search for string within the body.
     * 
     * @param s The original string
     * @return The string with single \r and \n modified changed to CRLF (\r\n).
     */
    public final String normalizeCRLF(String s) {
        Pattern p = Pattern.compile("\\r\\n|\\r|\\n", Pattern.MULTILINE);
        String[] lines = p.split(s);
        // Worst case: the s string contains only \n or \r characters: we then
        // need to triple the string length. Let's say double is enough.
        StringBuffer sb = new StringBuffer(s.length() * 2);
        for (int i = 0; i < lines.length; i += 1) {
            sb.append(lines[i]).append("\r\n");
        }

        return sb.toString();
    }

    /**
     * Replace \r and \n by correctly displayed end of line characters. Used to
     * display debug output. It also replace any single \r or \n by \r\n, to
     * make it easier, to search for string within the body.
     * 
     * @param s The original string
     * @return The string with \r and \n modified, to be correctly displayed.
     */
    public final String quoteCRLF(String s) {
        return s.replaceAll("\r\n", "\\\\r\\\\n\n");
    }

    /**
     * {@inheritDoc}
     */
    public void setFileUploadThreadManager(
            FileUploadManagerThread fileUploadManagerThread)
            throws JUploadException {
        if (this.fileUploadManagerThread != null) {
            throw new JUploadException(
                    "Can not override fileUploadManagerThread (in DefaultFileUpload.setFileUploadThreadManager()");
        }
        this.fileUploadManagerThread = fileUploadManagerThread;
    }
}
