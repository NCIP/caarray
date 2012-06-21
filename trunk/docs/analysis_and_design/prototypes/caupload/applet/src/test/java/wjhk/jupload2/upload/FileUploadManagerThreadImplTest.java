//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2009 The JUpload Team
//
// Created: 28 mai 2009
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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.testhelpers.JUploadPanelTestHelper;

/**
 * @author etienne_sf
 */
public class FileUploadManagerThreadImplTest extends AbstractJUploadTestHelper {

	/**
	 * @throws JUploadException
	 */
	@Before
	public void setUp() throws JUploadException {
		// FIXME Create a real JUploadContext, and set it to this UploadPolicy
		// juploadContext = new JUploadContextExecutable(null, propertiesURL);

		this.fileUploadManagerThread = new FileUploadManagerThreadImpl(
				this.uploadPolicy, this.fileUploadThread);
		((JUploadPanelTestHelper) this.juploadPanel).fileUploadManagerThread = this.fileUploadManagerThread;
	}

	/**
	 * Starts the upload, and wait for the {@link FileUploadThreadManager} to
	 * finish.
	 */
	private void executeUpload() throws Exception {
		this.fileUploadManagerThread.start();
		waitForThreadToFinish((Thread) this.fileUploadManagerThread,
				"fileUploadManagerThread");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#run()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRun() throws Exception {
		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).uploadException = null;
		Assert.assertNull("Check uploadException getter",
				this.fileUploadManagerThread.getUploadException());
		executeUpload();
		Assert.assertNull("There should be no exception during upload",
				this.fileUploadManagerThread.getUploadException());
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#setUploadException(wjhk.jupload2.exception.JUploadException)}
	 * .
	 */
	@Test
	public void testSetUploadException() {
		JUploadException jue = new JUploadException(
				"A test exception, to test FileUploadManagerThreadImpl.setUploadException()");
		this.fileUploadManagerThread.setUploadException(jue);
		Assert.assertTrue(jue == this.fileUploadManagerThread
				.getUploadException());
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#getUploadException()}
	 * .
	 */
	@Test
	public void testGetUploadException() {
		testSetUploadException();
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#isUploadFinished()}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testIsUploadFinished() throws Exception {
		Assert.assertFalse("Before starting, the upload is not finished!",
				this.fileUploadManagerThread.isUploadFinished());

		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).stop = true;
		Assert.assertTrue("If the upload is stopped, the upload is finished!",
				this.fileUploadManagerThread.isUploadFinished());
		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).stop = false;

		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).uploadFinished = true;
		Assert.assertTrue(
				"If the upload is finished... the upload is finished!",
				this.fileUploadManagerThread.isUploadFinished());
		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).uploadFinished = false;

		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).uploadException = new JUploadException(
				"bla bla bla");
		Assert.assertTrue("If there is an exception, the upload is finished!",
				this.fileUploadManagerThread.isUploadFinished());
		((FileUploadManagerThreadImpl) this.fileUploadManagerThread).uploadException = null;
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#stopUpload()}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStopUpload() throws Exception {
		this.fileUploadManagerThread.start();
		this.fileUploadManagerThread.stopUpload();
		/*
		 * Assert.assertTrue("Uploade should be stopped",
		 * this.fileUploadManagerThread.uploadStopped);
		 * Assert.assertTrue("Uploade should be finished",
		 * this.fileUploadManagerThread.uploadFinished);
		 */
		Assert.assertTrue("Uploade should be finished",
				this.fileUploadManagerThread.isUploadFinished());
	}

	/**
	 * Test method for
	 * {@link wjhk.jupload2.upload.FileUploadManagerThreadImpl#currentRequestIsFinished(UploadFilePacket)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCurrentRequestIsFinishedWithOneFile() throws Exception {
		// Test with one file.
		this.fileUploadManagerThread.start();
		Thread.sleep(20);
		Assert.assertFalse("Before starting, the upload is not finished!",
				this.fileUploadManagerThread.isUploadFinished());

		// Let's simulate the call to currentRequestIsFinished, indicating that
		// the file to upload has been successfully uploaded
		UploadFilePacket uploadFilePacket = new UploadFilePacket(
				this.uploadPolicy);
		uploadFilePacket.add(new UploadFileData(this.filesToUpload.get(0), 1,
				this.fileUploadManagerThread, this.uploadPolicy));

		this.fileUploadManagerThread.currentRequestIsFinished(uploadFilePacket);
		Assert.assertNull("There should be no upload error",
				this.fileUploadManagerThread.getUploadException());
		Assert.assertTrue("After upload, the upload should be finished.",
				this.fileUploadManagerThread.isUploadFinished());
	}
}
