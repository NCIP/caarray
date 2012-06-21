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

package wjhk.jupload2.testhelpers;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.upload.FileUploadManagerThread;
import wjhk.jupload2.upload.UploadFileData;
import wjhk.jupload2.upload.UploadFilePacket;

/**
 * @author etienne_sf
 * 
 */
public class FileUploadManagerThreadTestHelper extends Thread implements
		FileUploadManagerThread {

	/**  */
	public JUploadException uploadException = new JUploadException(
			new UnsupportedOperationException(this.getClass()
					+ ": uploadException is not supported in tests cases"));

	/** We need this status here, for all test on other threads */
	boolean uploadFinished = false;

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#anotherFileHasBeenSent(wjhk.jupload2.upload.UploadFilePacket,
	 *      wjhk.jupload2.upload.UploadFileData)
	 */
	public void anotherFileHasBeenSent(UploadFilePacket uploadFilePacket,
			UploadFileData newlyUploadedFileData) throws JUploadException {
		throw new UnsupportedOperationException(this.getClass()
				+ ".anotherFileHasBeenSent() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#currentRequestIsFinished(wjhk.jupload2.upload.UploadFilePacket)
	 */
	public void currentRequestIsFinished(UploadFilePacket packet)
			throws JUploadException {
		throw new UnsupportedOperationException(
				this.getClass()
						+ ".currentRequestIsFinished() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#getUploadException()
	 */
	public JUploadException getUploadException() {
		return this.uploadException;
	}

	/**
	 * Needs to be implemented, as the return for thie method is used in all
	 * other threads.
	 * 
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#isUploadFinished()
	 */
	public boolean isUploadFinished() {
		return this.uploadFinished;
	}

	/**
	 * Always return false.
	 * 
	 * @return true, if stopped by the 'user'.
	 */
	public boolean isUploadStopped() {
		return false;
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#nbBytesUploaded(long,
	 *      UploadFileData)
	 */
	public void nbBytesUploaded(long nbBytes, UploadFileData uploadFileData)
			throws JUploadException {
		throw new UnsupportedOperationException(this.getClass()
				+ ".nbBytesUploaded() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#run()
	 */
	@Override
	public void run() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".run() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#setUploadException(wjhk.jupload2.exception.JUploadException)
	 */
	public void setUploadException(JUploadException uploadException) {
		this.uploadException = uploadException;
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#setUploadStatus(wjhk.jupload2.upload.UploadFilePacket,
	 *      wjhk.jupload2.upload.UploadFileData, int)
	 */
	public void setUploadStatus(UploadFilePacket uploadFilePacket,
			UploadFileData uploadFileData, int uploadStatus)
			throws JUploadException {
		throw new UnsupportedOperationException(this.getClass()
				+ ".setUploadStatus() is not implemented in tests cases");
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#stopUpload()
	 */
	public void stopUpload() {
		throw new UnsupportedOperationException(this.getClass()
				+ ".stopUpload() is not implemented in tests cases");
	}

    @Override
    public String getProgressInfoJSON( FileData[] fileDatas ) {
        return "";
    }
}
