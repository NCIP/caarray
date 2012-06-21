package wjhk.jupload2.testhelpers;

import java.util.concurrent.BlockingQueue;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.FileUploadManagerThread;
import wjhk.jupload2.upload.FileUploadManagerThreadImpl;
import wjhk.jupload2.upload.FileUploadThread;
import wjhk.jupload2.upload.UploadFileData;
import wjhk.jupload2.upload.UploadFilePacket;

/**
 * This class allows easy construction of non-active instances of
 * FileUploadThread. It is used to execute unit tests on
 * {@link FileUploadManagerThreadImpl}
 * 
 * @author etienne_sf
 */
public class FileUploadThreadSuccessTestHelper extends Thread implements
		FileUploadThread {

	UploadPolicy uploadPolicy = null;

	FileUploadManagerThread fileUploadManagerThread;

	BlockingQueue<UploadFilePacket> packetQueue = null;

	/**
	 * @param packetQueue
	 * @param uploadPolicy
	 * @param fileUploadManagerThread
	 */
	public FileUploadThreadSuccessTestHelper(
			BlockingQueue<UploadFilePacket> packetQueue,
			UploadPolicy uploadPolicy,
			FileUploadManagerThread fileUploadManagerThread) {
		super("FileUploadThreadSuccessTestHelper");

		this.packetQueue = packetQueue;
		this.uploadPolicy = uploadPolicy;
		this.fileUploadManagerThread = fileUploadManagerThread;
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	/**
	 * This method loops on the packetQueue. When a packet is received, it calls
	 * the doUpload() method, to send these files to the server.
	 */
	@Override
	final public void run() {
		try {
			while (!this.fileUploadManagerThread.isUploadFinished()) {
				UploadFilePacket uploadFilePacket = this.packetQueue.take();

				// Loop through the files in this packet.
				for (UploadFileData uploadFileData : uploadFilePacket) {
					// Let's simulate the upload.
					this.fileUploadManagerThread.nbBytesUploaded(uploadFileData
							.getFileLength(), uploadFileData);
					// Ok, the file has been sent (hum, almost!)
					this.fileUploadManagerThread.anotherFileHasBeenSent(
							uploadFilePacket, uploadFileData);
				}
				this.fileUploadManagerThread
						.currentRequestIsFinished(uploadFilePacket);
			}// while
		} catch (JUploadException e) {
			this.fileUploadManagerThread.setUploadException(e);
		} catch (InterruptedException e) {
			// No action, in this test class
		}
	}// run

	/** {@inheritDoc} */
	public void close() {
		// No action
	}

	/** {@inheritDoc} */
	public String getResponseMsg() {
		return this.uploadPolicy.getStringUploadSuccess();
	}

	/** {@inheritDoc} */
	public void setFileUploadThreadManager(
			FileUploadManagerThread fileUploadManagerThread) {
		this.fileUploadManagerThread = fileUploadManagerThread;
	}

	/** {@inheritDoc} */
	public BlockingQueue<UploadFilePacket> getPacketQueue() {
		return this.packetQueue;
	}

}
