//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 27 janv. 2010
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

import org.junit.Assert;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.testhelpers.FileDataTestHelper;

/**
 * @author etienne_sf
 */
public class PacketConstructionThreadTest extends AbstractJUploadTestHelper {

	/**
	 * @param nbFilesInFilePanel
	 * @throws Exception
	 * @see wjhk.jupload2.upload.AbstractJUploadTestHelper#setupFullUploadEnvironment()
	 */
	private void prepareUpload(int nbFilesInFilePanel) {
		// We need nbFilesPerRequest to be set to 2.
		System.setProperty(UploadPolicy.PROP_NB_FILES_PER_REQUEST, "2");

		// We want to test the behavior, when putting the first file, while the
		// total upload contains more files.
		setupFileList(nbFilesInFilePanel);

		this.packetConstructionThread = new PacketConstructionThread(
				this.preparedFileQueue, this.packetQueue,
				this.fileUploadManagerThread, this.uploadPolicy);
	}

	/**
	 * Tests the constructor.
	 * 
	 * @throws Exception
	 */
	@Test
	public synchronized void packetConstructionThreadTest() throws Exception {
		prepareUpload(1);

		this.logger.info("Start of packetConstructionThreadTest()");
		Assert.assertEquals("Check of the preparedFileQueue",
				this.preparedFileQueue,
				this.packetConstructionThread.preparedFileQueue);
		Assert.assertEquals("Check of the packetQueue", this.packetQueue,
				this.packetConstructionThread.packetQueue);
		Assert.assertEquals("Check of the fileUploadManagerThread",
				this.fileUploadManagerThread,
				this.packetConstructionThread.fileUploadManagerThread);
		Assert.assertEquals("Check of the upload policy", this.uploadPolicy,
				this.packetConstructionThread.uploadPolicy);
		this.logger.info("End of packetConstructionThreadTest()");
	}

	/**
	 * @throws InterruptedException
	 * @throws JUploadException
	 */
	@Test
	public synchronized void runOneFile() throws InterruptedException,
			JUploadException {
		this.logger.info("Start of runOneFile()");
		prepareUpload(10);

		FileData fileData = new FileDataTestHelper(1);
		fileData.beforeUpload();

		Assert.assertEquals(
				"This test case assumes that the nbFilesPerRequest is 2", 2,
				this.uploadPolicy.getNbFilesPerRequest());

		// Check the prepared file is posted on the packetQueue.
		Assert
				.assertTrue(
						"The preparedFileQueue should be empty before starting the test",
						this.preparedFileQueue.isEmpty());
		Assert.assertTrue(
				"The packetQueue should be empty before starting the test",
				this.packetQueue.isEmpty());
		int numOfFileInCurrentRequest = 0;
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest, this.fileUploadManagerThread,
				this.uploadPolicy));
		Assert.assertFalse("The queue should contain this file",
				this.preparedFileQueue.isEmpty());
		this.packetConstructionThread.start();
		// The packetConstructionThread should take the file. We wait for that.
		waitForQueueToBeEmpty(this.preparedFileQueue, "preparedFileQueue");
		Assert.assertTrue(
				"The queue should be empty once the file is prepared",
				this.preparedFileQueue.isEmpty());
		Assert
				.assertTrue(
						"The packet is not finished (only one file: no packet is ready)",
						this.packetQueue.isEmpty());
		Assert
				.assertTrue(
						"The PacketConstructionThread should still be alive (poisonned file not received)",
						this.packetConstructionThread.isAlive());

		// Putting the poisonned file should finish the thread.
		this.preparedFileQueue.put(new UploadFileDataPoisonned());
		waitForThreadToFinish(this.packetConstructionThread,
				"packetConstructionThread");
		Assert.assertTrue(
				"The queue should be empty once the file is prepared",
				this.preparedFileQueue.isEmpty());
		Assert.assertFalse("One packet should be in the queue",
				this.packetQueue.isEmpty());
		UploadFilePacket packet = this.packetQueue.take();
		Assert.assertEquals("The packet should be one file long", 1, packet
				.size());
		Assert.assertFalse("The PacketConstructionThread should have finished",
				this.packetConstructionThread.isAlive());
		this.logger.info("End of runOneFile()");
	}

	/**
	 * @throws InterruptedException
	 * @throws JUploadException
	 */
	@Test
	public synchronized void runTwoFiles() throws InterruptedException,
			JUploadException {
		this.logger.info("Start of runTwoFiles()");
		prepareUpload(10);

		FileData fileData = new FileDataTestHelper(1);
		fileData.beforeUpload();

		Assert.assertEquals(
				"This test case assumes that the nbFilesPerRequest is 2", 2,
				this.uploadPolicy.getNbFilesPerRequest());

		// Assumes that runOneFile() was Ok
		int numOfFileInCurrentRequest = 0;
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest++, this.fileUploadManagerThread,
				this.uploadPolicy));
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest++, this.fileUploadManagerThread,
				this.uploadPolicy));

		this.packetConstructionThread.start();
		waitForQueueToBeEmpty(this.preparedFileQueue, "preparedFileQueue");
		Assert.assertTrue("The files should have been taken",
				this.preparedFileQueue.isEmpty());
		Assert.assertFalse("The packetQueue should contain one packet",
				this.packetQueue.isEmpty());
		UploadFilePacket packet = this.packetQueue.take();
		Assert.assertEquals("The packet should be two files long", 2, packet
				.size());
		Assert
				.assertTrue(
						"The PacketConstructionThread should still be alive (poisonned file not received)",
						this.packetConstructionThread.isAlive());
	}

	/**
	 * @throws InterruptedException
	 * @throws JUploadException
	 */
	@Test
	public synchronized void runThreeFiles() throws InterruptedException,
			JUploadException {
		this.logger.info("Start of runThreeFiles()");
		prepareUpload(10);

		FileData fileData = new FileDataTestHelper(1);

		Assert.assertEquals(
				"This test case assumes that the nbFilesPerRequest is 2", 2,
				this.uploadPolicy.getNbFilesPerRequest());

		// Assumes that runOneFile() was Ok
		int numOfFileInCurrentRequest = 0;
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest++, this.fileUploadManagerThread,
				this.uploadPolicy));
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest++, this.fileUploadManagerThread,
				this.uploadPolicy));
		this.preparedFileQueue.put(new UploadFileData(fileData,
				numOfFileInCurrentRequest++, this.fileUploadManagerThread,
				this.uploadPolicy));

		this.packetConstructionThread.start();
		waitForQueueToBeEmpty(this.preparedFileQueue, "preparedFileQueue");
		Assert.assertTrue("The files should have been taken",
				this.preparedFileQueue.isEmpty());
		Assert.assertFalse("The packetQueue should contain one packets",
				this.packetQueue.isEmpty());
		UploadFilePacket packet = this.packetQueue.take();
		Assert.assertTrue("The packetQueue should have contained one packet",
				this.packetQueue.isEmpty());
		Assert.assertEquals("The packet should be two files long", 2, packet
				.size());
		Assert
				.assertTrue(
						"The PacketConstructionThread should still be alive (poisonned file not received)",
						this.packetConstructionThread.isAlive());

		// Let's send the poisoned
		// Putting the poisonned file should finish the thread.
		this.preparedFileQueue.put(new UploadFileDataPoisonned());
		waitForThreadToFinish(this.packetConstructionThread,
				"packetConstructionThread");
		Assert
				.assertTrue(
						"The preparedFileQueue should be empty once the file is prepared",
						this.preparedFileQueue.isEmpty());
		Assert.assertFalse("One packet should be in the queue",
				this.packetQueue.isEmpty());
		packet = this.packetQueue.take();
		Assert.assertEquals("The packet should be one file long", 1, packet
				.size());
		Assert.assertFalse("The PacketConstructionThread should have finished",
				this.packetConstructionThread.isAlive());
		this.logger.info("End of runThreeFiless()");
	}
}
