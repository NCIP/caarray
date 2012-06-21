//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 3 fevr. 2010
// Creator: etienne_sf
// Last modified: $Date$
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

package wjhk.jupload2.upload;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;

/**
 * @author etienne_sf
 * 
 */
public interface FileUploadManagerThread {

	/** Indicates that nothings has begun */
	public static final int UPLOAD_STATUS_NOT_STARTED = 1;

	/**
	 * We're sending data to the server, for the file identified by
	 * numOfFileInCurrentUpload.
	 */
	public static final int UPLOAD_STATUS_UPLOADING = 2;

	/**
	 * A chunk (a part) of the file identified by numOfFileInCurrentUpload has
	 * been sent. But the server response has not been received yet.
	 */
	public static final int UPLOAD_STATUS_CHUNK_UPLOADED_WAITING_FOR_RESPONSE = 3;

	/**
	 * All data for the file identified by numOfFileInCurrentUpload has been
	 * sent. But the server response has not been received yet.
	 */
	public static final int UPLOAD_STATUS_FILE_UPLOADED_WAITING_FOR_RESPONSE = 4;

	/**
	 * The upload for the file identified by numOfFileInCurrentUpload is
	 * finished
	 */
	public static final int UPLOAD_STATUS_UPLOADED = 5;

	/**
	 * The heart of the program. This method prepare the upload, then calls
	 * doUpload for each HTTP request.
	 * 
	 * @throws InterruptedException
	 * @see java.lang.Thread#join()
	 */
	public void join() throws InterruptedException;

	/**
	 * The heart of the program. This method prepare the upload, then calls
	 * doUpload for each HTTP request.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run();

	/**
	 * The heart of the program. This method prepare the upload, then calls
	 * doUpload for each HTTP request.
	 * 
	 * @see java.lang.Thread#start()
	 */
	public void start();

	/**
	 * Check if the thread is running...
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	public void interrupt();

	/**
	 * Check if the thread is running...
	 * 
	 * @return True if it's running
	 * @see java.lang.Thread#isAlive()
	 */
	public boolean isAlive();

	/**
	 * Stores the last upload exception that occurs. This method won't write to
	 * the log file.
	 * 
	 * @param uploadException
	 */
	public void setUploadException(JUploadException uploadException);

	/**
	 * Get the last upload exception that occurs.
	 * 
	 * @return The last upload exception, or null if no exception occurs.
	 */
	public JUploadException getUploadException();

	/**
	 * Indicates whether the upload is finished or not. As several conditions
	 * can make the upload being finished (all files uploaded, an error occured,
	 * the user stops the upload), a specific boolean is built. It's managed by
	 * the {@link #run()} method.
	 * 
	 * @return true if the upload is finished. False otherwise.
	 */
	public boolean isUploadFinished();

	/**
	 * Indicates if the upload has been stopped by the user, or by any upload
	 * error. This method should not be used to know if it's the end of the
	 * upload. To do this, see {@link #isUploadFinished()}
	 * 
	 * @return true if the current upload has been asked to stop by the user,
	 *         false otherwise.
	 */
	public boolean isUploadStopped();

	/**
	 * Used by the UploadFileData#uploadFile(java.io.OutputStream, long) for
	 * each uploaded buffer
	 * 
	 * @param nbBytes
	 *            Number of additional bytes that where uploaded.
	 * @param uploadFileData
	 *            The file that is currently being uploade (or null if no file
	 *            is being uploaded)
	 * @throws JUploadException
	 */
	public void nbBytesUploaded(long nbBytes, UploadFileData uploadFileData)
			throws JUploadException;

	/**
	 * Indicate the current state of the upload, to allow a correct display of
	 * UPLOAD_STATUS_UPLOADED status. the upload progress bar.
	 * 
	 * @param uploadFilePacket
	 *            The current packet. This parameter is mandatory only for the
	 * @param uploadFileData
	 *            The file whose upload begins, is going on or is finished.
	 * @param uploadStatus
	 * @throws JUploadException
	 */
	public void setUploadStatus(UploadFilePacket uploadFilePacket,
			UploadFileData uploadFileData, int uploadStatus)
			throws JUploadException;

	/**
	 * Reaction to the user click on the 'Stop' button, or any action from the
	 * user asking to stop the upload. The upload should go on for the current
	 * file, and stop before starting the next upload request to the server, to
	 * avoid strange problems on the server.
	 */
	public void stopUpload();

	/**
	 * This method is called each time a new file is sent to the server. It's
	 * main aim is to allow a proper display of the progress bar. It is public,
	 * as upload is done in another thread, whose class maybe in another
	 * package.
	 * 
	 * @param uploadFilePacket
	 * @param newlyUploadedFileData
	 * @throws JUploadException
	 */
	public void anotherFileHasBeenSent(UploadFilePacket uploadFilePacket,
			UploadFileData newlyUploadedFileData) throws JUploadException;

	/**
	 * This method is called when the server response for the upload indicates a
	 * success. It is public, as upload is done in another thread, whose class
	 * maybe in another package.
	 * 
	 * @param packet
	 *            The packet of files that was successfully uploaded.
	 * @throws JUploadException
	 */
	public void currentRequestIsFinished(UploadFilePacket packet)
			throws JUploadException;

	
    public String getProgressInfoJSON( FileData[] fileDatas );
}