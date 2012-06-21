//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 10 fevr. 2010
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

package wjhk.jupload2.upload.helper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JProgressBar;
import javax.swing.Timer;

import org.json.JSONException;
import org.json.JSONObject;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.gui.JUploadPanel;
import wjhk.jupload2.gui.filepanel.SizeRenderer;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.FilePreparationThread;
import wjhk.jupload2.upload.FileUploadManagerThread;
import wjhk.jupload2.upload.UploadFileData;
import wjhk.jupload2.upload.UploadFilePacket;

/**
 * @author etienne_sf
 */
public class ProgressBarManager implements ActionListener {
	/**
	 * The delay between to updates of the progress bar, in ms.
	 */
	public final static int DELAY_FOR_UPDATE_OF_PROGRESS_BAR = 100;

	/**
	 * Contains the date/time (as a long) of the start of the current upload.
	 * This allows to sum the time of the actual upload, and ignore the time the
	 * applet is waiting for the server's response. Once the request is
	 * finished, and the applet waits for the server's response, the duration of
	 * the sending to the server is added to currentRequestStartTime, and
	 * currentRequestStartTime is reseted to 0. It's then ready for the next
	 * upload request.
	 */
	long currentRequestStartTime = 0;

	/**
	 * The file that is currently being uploaded. Allow to refresh the progress
	 * bar, with up to date information, based on a timer event.
	 */
	UploadFileData currentUploadFileData = null;

	/**
	 * The files which is currently being sent to the server.
	 */
	UploadFilePacket currentUploadFilePacket = null;

	/**
	 * The file preparatoin thread prepares each file for upload, and manage
	 * possible errors that can occurs at preparation time.
	 */
	FilePreparationThread filePreparationThread = null;

	/**
	 * Contains the system time of the start of the global upload. This is used
	 * to calculate the ETA, and display it to the user, on the status bar.
	 */
	long globalStartTime = 0;

	/**
	 * Indicated the number of bytes that have currently been sent for the
	 * current file. This allows the management of the progress bar.
	 */
	long nbBytesUploadedForCurrentFile = 0;

	/**
	 * Number of files that have already been sent. The control on the upload
	 * success may be done or not. It's used to properly display the progress
	 * bar.
	 */
	int nbSentFiles = 0;

	/** Current number of bytes that have been uploaded. */
	long nbUploadedBytes = 0;

	/**
	 * The {@link JUploadPanel} progress bar, to follow the file preparation
	 * progress.
	 */
	JProgressBar preparationProgressBar = null;

	/**
	 * The timer which schedules the update for the progress and status bar.
	 */
	Timer timer;

	/**
	 * Contains the sum of the upload duration for all requests, in
	 * milliseconds. For instance, if sending in 10 chunks one big file, the
	 * uploadDuration contains the sum of the sending of these 10 request to the
	 * server. This allows to calculate the true upload speed, and ignore the
	 * time we'll wait for the server's response.
	 */
	long totalUploadDuration = 0;

	/** The current upload policy */
	UploadPolicy uploadPolicy;

	/**
	 * The {@link JUploadPanel} progress bar, to follow the upload of the
	 * prepared files to the server.
	 */
	JProgressBar uploadProgressBar = null;

	/**
	 * Indicates what is the current file being uploaded, and its upload status.
	 */
	int uploadStatus = FileUploadManagerThread.UPLOAD_STATUS_NOT_STARTED;

	/**
	 * @param uploadPolicy
	 * @param filePreparationThread
	 */
	public ProgressBarManager(UploadPolicy uploadPolicy,
			FilePreparationThread filePreparationThread) {
		this.uploadPolicy = uploadPolicy;
		this.filePreparationThread = filePreparationThread;
		// our timer is a daemon.
		this.timer = new Timer(DELAY_FOR_UPDATE_OF_PROGRESS_BAR, this);

		JUploadPanel uploadPanel = uploadPolicy.getContext().getUploadPanel();

		this.uploadProgressBar = uploadPanel.getUploadProgressBar();
		this.preparationProgressBar = uploadPanel.getPreparationProgressBar();

		updateUploadProgressBarText();
	}

	/**
	 * The only event managed here is the timer event. We update the progress
	 * and status bar.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		updateUploadProgressBarValue();
		updateUploadStatusBar();

	}

	/**
	 * Called when a new file is uploaded. This method update the bars
	 * accordingly to this new status, by calling the
	 * updateUploadProgressBarText() method.
	 * 
	 * @param uploadFilePacket
	 * @param uploadFileData
	 * @throws JUploadException
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#anotherFileHasBeenSent(wjhk.jupload2.upload.UploadFilePacket,
	 *      wjhk.jupload2.upload.UploadFileData)
	 */
	public synchronized void anotherFileHasBeenSent(
			UploadFilePacket uploadFilePacket, UploadFileData uploadFileData)
			throws JUploadException {
		if (uploadFilePacket != this.currentUploadFilePacket) {
			throw new java.lang.AssertionError("Wrong file packet in "
					+ this.getClass().getName() + ".anotherFileHasBeenSent()");
		}
		if (uploadFileData != this.currentUploadFileData) {
			throw new java.lang.AssertionError("Wrong file packet in "
					+ this.getClass().getName() + ".anotherFileHasBeenSent()");
		}
		this.nbSentFiles += 1;
		this.nbBytesUploadedForCurrentFile = 0;
		this.uploadPolicy
				.displayDebug(
						this.getClass().getName()
								+ ".anotherFileHasBeenSent(): before call to newlyUploadedFileData.getUploadLength()",
						100);

		// We are finished with this one. Let's display it.
		this.uploadStatus = FileUploadManagerThread.UPLOAD_STATUS_UPLOADED;
		updateUploadProgressBarText();
	}

	/**
	 * Clean all bar content.
	 */
	public void clearBarContent() {
		// Let's stop the update process.
		this.timer.stop();

		this.preparationProgressBar.setValue(0);
		this.preparationProgressBar.setString("");
		this.uploadProgressBar.setValue(0);
		this.uploadProgressBar.setString("");

	}

	/**
	 * @return the globalStartTime
	 */
	public long getGlobalStartTime() {
		return this.globalStartTime;
	}

	/**
	 * @return the nbUploadedBytes
	 */
	public long getNbUploadedBytes() {
		return this.nbUploadedBytes;
	}

	/**
	 * @return the uploadDuration
	 */
	public long getUploadDuration() {
	    long currentRequestDuration = 0;
	    if (this.currentRequestStartTime!=0) {
	        //A request is running on
	        currentRequestDuration=System.currentTimeMillis()-this.currentRequestStartTime;
	    }

		return this.totalUploadDuration + currentRequestDuration;
	}

	/**
	 * Initialize the maximum value for the two progress bar: 100*the number of
	 * files to upload.
	 * 
	 * @throws JUploadException
	 * @see #updateUploadProgressBar(UploadFilePacket, UploadFileData)
	 */
	private void initProgressBar() throws JUploadException {
		// To follow the state of file preparation
		this.preparationProgressBar.setMaximum(100 * this.filePreparationThread
				.getNbFilesToSent());
		this.preparationProgressBar.setString("");

		// To follow the state of the actual upload
		this.uploadProgressBar.setMaximum(100 * this.uploadPolicy.getContext()
				.getUploadPanel().getFilePanel().getFilesLength());
		this.uploadProgressBar.setString("");
	}

	/**
	 * The progressBar is updated each 50ms and each 10% of the target file.
	 * 
	 * @param nbBytes
	 * @param uploadFileData
	 * @throws JUploadException
	 * @see wjhk.jupload2.upload.FileUploadManagerThread#nbBytesUploaded(long,
	 *      UploadFileData)
	 */
	public synchronized void nbBytesUploaded(long nbBytes,
			UploadFileData uploadFileData) throws JUploadException {
		this.nbUploadedBytes += nbBytes;
		this.nbBytesUploadedForCurrentFile += nbBytes;
	}

	/**
	 * Set an error text, that will be displayed on the progress bar
	 * 
	 * @param errorTexte
	 */
	public void setErrorMessage(String errorTexte) {
		this.preparationProgressBar.setString(errorTexte);
	}

	/**
	 * @param uploadFilePacket
	 * @param uploadFileData
	 * @param uploadStatus
	 * @throws JUploadException
	 */
	public synchronized void setUploadStatus(UploadFilePacket uploadFilePacket,
			UploadFileData uploadFileData, int uploadStatus)
			throws JUploadException {
		// Let's store the file we're working on.
		this.currentUploadFileData = uploadFileData;
		this.currentUploadFilePacket = uploadFilePacket;

		switch (uploadStatus) {
		case FileUploadManagerThread.UPLOAD_STATUS_CHUNK_UPLOADED_WAITING_FOR_RESPONSE:
		case FileUploadManagerThread.UPLOAD_STATUS_FILE_UPLOADED_WAITING_FOR_RESPONSE:
			// We're waiting for the server: let's add it to the sending
			// duration.
			this.totalUploadDuration += System.currentTimeMillis()
					- this.currentRequestStartTime;
			this.currentRequestStartTime = 0;
			break;
		case FileUploadManagerThread.UPLOAD_STATUS_UPLOADING:
		    //We mark the start of the request, if it was not already done.
			if (this.currentRequestStartTime == 0) {
				this.currentRequestStartTime = System.currentTimeMillis();
			}
			break;
		case FileUploadManagerThread.UPLOAD_STATUS_UPLOADED:
			// Indicated that the current request is finished. Nothing to do
			break;
		default:
			this.uploadPolicy.displayWarn("Unknown value for uploadStatus: "
					+ uploadStatus);
		}
		this.uploadStatus = uploadStatus;

		this.updateUploadProgressBarText();
	}

	/**
	 * Update the progress bar, based on the following data: <DIR> <LI>
	 * nbSentFiles: number of files that have already been updated. <LI>
	 * nbBytesUploadedForCurrentFile: allows calculation of the upload progress
	 * for the current file, based on it total upload length. </DIR> <BR>
	 * Note 1: The progress bar update is ignored, if last update was less than
	 * 100ms before.<BR>
	 * Note 2: This method calls the
	 * {@link #updateUploadProgressBarValue(UploadFileData)} method, to also
	 * update its value.
	 * 
	 * @throws JUploadException
	 */
	private void updateUploadProgressBarText() {
		/*
		 * final String msgInfoUploaded = this.uploadPolicy
		 * .getLocalizedString("infoUploaded"); final String msgInfoUploading =
		 * this.uploadPolicy .getLocalizedString("infoUploading"); final String
		 * msgNbUploadedFiles = this.uploadPolicy
		 * .getLocalizedString("nbUploadedFiles");
		 */
		updateUploadProgressBarValue();

		String msg = null;
		switch (this.uploadStatus) {
		case FileUploadManagerThread.UPLOAD_STATUS_NOT_STARTED:
			msg = "";
			break;
		case FileUploadManagerThread.UPLOAD_STATUS_UPLOADING:
		case FileUploadManagerThread.UPLOAD_STATUS_CHUNK_UPLOADED_WAITING_FOR_RESPONSE:
			// Uploading files %1$s
			msg = this.uploadPolicy.getLocalizedString("infoUploading",
					(this.nbSentFiles + 1));
			break;
		case FileUploadManagerThread.UPLOAD_STATUS_FILE_UPLOADED_WAITING_FOR_RESPONSE:
			// %1$s file(s) uploaded. Waiting for server response ...

			// nbSentFiles it number of files whose data is already sent to
			// the server. This include the currentUploadFileData (which
			// should be the last file in the packet)
			int firstFileInPacket = this.currentUploadFileData
					.getNumOfFileInCurrentUpload();
			int currentFile = this.nbSentFiles;

			if (this.currentUploadFilePacket.size() == 1) {
				msg = currentFile + "/"
						+ this.filePreparationThread.getNbFilesToSent();
			} else {
				msg = firstFileInPacket + "-" + currentFile + "/"
						+ this.filePreparationThread.getNbFilesToSent();
			}
			msg = this.uploadPolicy.getLocalizedString("infoUploaded", msg);

			break;
		case FileUploadManagerThread.UPLOAD_STATUS_UPLOADED:
			// %1$d file(s) uploaded
			msg = this.uploadPolicy.getLocalizedString("nbUploadedFiles",
					(this.nbSentFiles));
			break;
		default:
			// Hum, that's strange !
			this.uploadPolicy
					.displayWarn("Unknown upload status in FileUploadManagerThreadImpl.updateProgressBar(): "
							+ this.uploadStatus);
		}

		// Let's show the modifications to the user
		this.uploadProgressBar.setString(msg);
		// To be sure that the new text is displayed, we force instantaneous
		// refresh. This won't slow down the upload, as it's done in separate
		// thread.
		this.uploadProgressBar.repaint(0);
	}

	/**
	 * Update the progress bar value, that is: the percent of upload of the
	 * current file. This is based on nbBytesUploadedForCurrentFile and the
	 * total upload length of the current file.<BR>
	 * Note: The progress bar update is ignored, if last update was less than
	 * 100ms before.
	 * 
	 * @throws JUploadException
	 */
	private void updateUploadProgressBarValue() {
		/*
		 * final String msgInfoUploaded = this.uploadPolicy
		 * .getLocalizedString("infoUploaded"); final String msgInfoUploading =
		 * this.uploadPolicy .getLocalizedString("infoUploading"); final String
		 * msgNbUploadedFiles = this.uploadPolicy
		 * .getLocalizedString("nbUploadedFiles");
		 */
		int percent = 0;

		// First, we update the bar itself.
		if (this.nbBytesUploadedForCurrentFile == 0
				|| this.nbSentFiles == this.filePreparationThread
						.getNbFilesToSent()) {
			percent = 0;
		} else if (this.currentUploadFileData == null) {
			percent = 0;
		} else {
			if (this.currentUploadFileData.isPreparedForUpload()) {
				percent = (int) (this.nbBytesUploadedForCurrentFile * 100 / this.currentUploadFileData
						.getUploadLength());
			} else {
				// Hum, hum. The file is not prepared for upload yet. So we
				// actually didn't send any thing for it.
				percent = 0;
			}
			// Usually, a percentage if advancement for one file is no more than
			// 100. Let's check that.
			if (percent > 100) {
				this.uploadPolicy
						.displayWarn("percent is more than 100 ("
								+ percent
								+ ") in FileUploadManagerThreadImpl.update.UploadProgressBar");
				percent = 100;
			}
		}

		this.uploadProgressBar.setValue(100 * this.nbSentFiles + percent);
	}

	/**
	 * Displays the current upload speed on the status bar.
	 */
	private void updateUploadStatusBar() {
		// We'll update the status bar, only if it exists and if the upload
		// actually started.
		if (null != this.uploadPolicy.getContext().getUploadPanel()
				.getStatusLabel()
				&& this.nbUploadedBytes > 0) {
			double percent;
			// uploadCPS: contains the upload speed.
			double uploadSpeed;
			// globalCPS: contains the average speed, including the time the
			// applet is waiting for the server response.
			double globalCPS;
			long remaining;
			String eta;

			try {
				percent = 100.0 * this.nbUploadedBytes
						/ this.filePreparationThread.getTotalFileBytesToSend();

			} catch (ArithmeticException e1) {
				percent = 100;
			}

			// Calculation of the 'pure' upload speed.
			uploadSpeed = ((double) this.nbUploadedBytes)
					/ ((double) getUploadDuration() / 1000);
			if (uploadSpeed == Double.POSITIVE_INFINITY) {
				this.uploadPolicy.displayDebug(
						"uploadSpeed is Infinity, for nbUploadedBytes="
								+ nbUploadedBytes
								+ " and actualUploadDuration(ms)="
								+ getUploadDuration(), 80);
			}

			// Calculation of the 'global' upload speed.
			try {
				globalCPS = ((double) this.nbUploadedBytes)
						/ (System.currentTimeMillis() - this.globalStartTime)
						* 1000;
			} catch (ArithmeticException e1) {
				globalCPS = this.nbUploadedBytes;
			}

			// Calculation of the ETA. It's based on the global upload speed.
			try {
				remaining = (long) ((this.filePreparationThread
						.getTotalFileBytesToSend() - this.nbUploadedBytes) / globalCPS);
				if (remaining > 3600) {
					eta = this.uploadPolicy.getLocalizedString("timefmt_hms",
							Long.valueOf(remaining / 3600), Long
									.valueOf((remaining / 60) % 60), Long
									.valueOf(remaining % 60));
				} else if (remaining > 60) {
					eta = this.uploadPolicy.getLocalizedString("timefmt_ms",
							Long.valueOf(remaining / 60), Long
									.valueOf(remaining % 60));
				} else
					eta = this.uploadPolicy.getLocalizedString("timefmt_s",
							Long.valueOf(remaining));
			} catch (ArithmeticException e1) {
				eta = this.uploadPolicy.getLocalizedString("timefmt_unknown");
			}
			String status = this.uploadPolicy.getLocalizedString("status_msg",
					Integer.valueOf((int) percent), SizeRenderer
							.formatFileUploadSpeed(uploadSpeed,
									this.uploadPolicy), eta);
			this.uploadPolicy.getContext().getUploadPanel().getStatusLabel()
					.setText(status);
			// this.uploadPanel.getStatusLabel().repaint();
			this.uploadPolicy.getContext().showStatus(status);
			// this.uploadPolicy.displayDebug("[updateUploadStatusBar] " +
			// status, 101);
		}
	}

	/**
	 * This just stops the timer. A 'last' update is done.
	 */
	public void uploadIsFinished() {
		// Let's stop the update process.
		this.timer.stop();

		updateUploadProgressBarText();
		updateUploadStatusBar();
	}

	/**
	 * @throws JUploadException
	 */
	public void uploadIsStarted() throws JUploadException {
		// Ok, the upload just starts. We keep the date, to later calculate the
		// ETA.
		this.globalStartTime = System.currentTimeMillis();
		initProgressBar();

		// Let's start the update process.
		this.timer.start();
	}

	
    public String getProgressInfoJSON( FileData[] fileDatas ) {
        long totalBytes = 0;
        for( FileData fd:fileDatas )
            totalBytes += fd.getFileLength();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("bytes_loaded_for_current_file", nbBytesUploadedForCurrentFile);
            jsonObj.put("num_sent_files", nbSentFiles);
            jsonObj.put("all_bytes_loaded", nbUploadedBytes);
            jsonObj.put("all_bytes_total", totalBytes);
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String sRet = jsonObj.toString();
        return sRet;
    }

}
