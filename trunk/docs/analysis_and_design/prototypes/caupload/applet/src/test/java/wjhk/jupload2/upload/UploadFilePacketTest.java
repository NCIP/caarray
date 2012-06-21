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

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.filedata.DefaultFileData;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.testhelpers.FileDataTestHelper;

/**
 * @author etienne_sf
 * 
 */
public class UploadFilePacketTest extends AbstractJUploadTestHelper {

	/**     */
	public final static int MAX_CHUNK_SIZE = 1000;

	/**     */
	public final static int NB_FILES_IN_FILE_PANEL = 999;

	/**     */
	public final static int NB_FILES_PER_REQUEST = 2;

	UploadFilePacket uploadFilePacket;

	File root = new File("./tests");

	File f = new File("./tests/files/fichier_1.txt");

	FileData fileData;

	UploadFileData uploadFileData;

	/**     */
	@Before
	public void setUp() {
		prepareUpload(10);
	}

	/**
	 * @param nbFilesInFilePanel
	 * @throws Exception
	 * @see wjhk.jupload2.upload.AbstractJUploadTestHelper#setupFullUploadEnvironment()
	 */
	private void prepareUpload(int nbFilesInFilePanel) {
		System.setProperty("maxChunkSize", String.valueOf(MAX_CHUNK_SIZE));
		System.setProperty("nbFilesPerRequest", String
				.valueOf(NB_FILES_PER_REQUEST));

		// We want to test the behavior, when putting the first file, while the
		// total upload contains more files.
		setupFileList(NB_FILES_IN_FILE_PANEL);

		createTestData();
		this.packetConstructionThread = new PacketConstructionThread(
				this.preparedFileQueue, this.packetQueue,
				this.fileUploadManagerThread, this.uploadPolicy);
	}

	/**
	 * This method creates or re-creates all objects for the current instance,
	 * using the current system properties. If called by {@link #setUp()}, it
	 * uses the default test configuration. If called by any test method, it
	 * uses the overriden properties.
	 */
	private void createTestData() {
		this.uploadFilePacket = new UploadFilePacket(this.uploadPolicy);

		this.fileData = new FileDataTestHelper(1);
		Assert.assertTrue("The file must be readable", this.fileData.canRead());
		this.uploadFileData = new UploadFileData(this.fileData, 0,
				this.fileUploadManagerThread, this.uploadPolicy);
	}

	/**     */
	@Test
	public void testUploadFilePacket() {
		Assert.assertEquals("The good upload policy", this.uploadPolicy,
				this.uploadFilePacket.uploadPolicy);
		Assert.assertEquals("The correct maxChunkSize", MAX_CHUNK_SIZE,
				this.uploadFilePacket.maxNbBytes);
		// This test expects
		Assert.assertEquals("The correct maxNbFiles", NB_FILES_PER_REQUEST,
				this.uploadFilePacket.maxNbFiles);

		Assert.assertFalse("This class should never be poisonned",
				this.uploadFilePacket.isPoisonned());
	}

	/**
	 * @throws JUploadException
	 */
	@Test
	public void testIsFullOfFiles() throws JUploadException {
		Assert.assertFalse("This packet is not full yet (0 files)",
				this.uploadFilePacket.isFull());
		for (int i = 1; i < NB_FILES_PER_REQUEST; i += 1) {
			this.fileData = new DefaultFileData(this.f, this.root,
					this.uploadPolicy);
			this.fileData.beforeUpload();
			Assert.assertTrue("The file must be accepted",
					this.uploadFilePacket
							.add(new UploadFileData(this.fileData, 0,
									this.fileUploadManagerThread,
									this.uploadPolicy)));
			Assert.assertFalse("This packet is not full yet (" + i + " files)",
					this.uploadFilePacket.isFull());
		}
		this.uploadFilePacket.add(new UploadFileData(this.fileData, 0,
				this.fileUploadManagerThread, this.uploadPolicy));
		Assert.assertTrue("This packet is now full", this.uploadFilePacket
				.isFull());
	}

	/**
	 * @throws JUploadException
	 */
	@Test
	public void testIsFullOfBytes() throws JUploadException {
		Assert.assertFalse("This packet is not full yet (0 files)",
				this.uploadFilePacket.isFull());

		int maxNbFiles = 1000;
		int realMaxNbFiles = Math.min(maxNbFiles, NB_FILES_IN_FILE_PANEL);
		int maxNbBytes = 100;

		System.setProperty(UploadPolicy.PROP_NB_FILES_PER_REQUEST, String
				.valueOf(maxNbFiles));
		System.setProperty(UploadPolicy.PROP_MAX_CHUNK_SIZE, String
				.valueOf(maxNbBytes));
		// We need to re-initialize this test, to use the above setting.
		createTestData();
		Assert.assertEquals("The maximal number of files", realMaxNbFiles,
				this.uploadFilePacket.maxNbFiles);
		Assert.assertEquals("The maximal number of bytes", maxNbBytes,
				this.uploadFilePacket.maxNbBytes);

		// The packet is full only if we have exactly the good number of bytes !
		String fileContent = "a";
		for (int i = 1; i < maxNbBytes; i += 1) {
			this.fileData = new FileDataTestHelper(0);
			((FileDataTestHelper) this.fileData).fileContent = fileContent;
			this.fileData.beforeUpload();
			Assert.assertTrue("The file must be accepted",
					this.uploadFilePacket
							.add(new UploadFileData(this.fileData, 0,
									this.fileUploadManagerThread,
									this.uploadPolicy)));
			Assert.assertFalse("This packet is not full yet (" + i + " files)",
					this.uploadFilePacket.isFull());
		}

		this.fileData = new FileDataTestHelper(0);
		((FileDataTestHelper) this.fileData).fileContent = fileContent;
		this.uploadFilePacket.add(new UploadFileData(this.fileData, 0,
				this.fileUploadManagerThread, this.uploadPolicy));
		Assert.assertTrue("This packet is now full", this.uploadFilePacket
				.isFull());
	}

	/**
	 * @throws JUploadException
	 * 
	 */
	@Test
	public void testCanAdd_checkBytes() throws JUploadException {
		FileData fileData = new FileDataTestHelper(0);
		int maxNbFiles = 1000;
		int maxNbBytes = 100;

		System.setProperty(UploadPolicy.PROP_NB_FILES_PER_REQUEST, String
				.valueOf(maxNbFiles));
		System.setProperty(UploadPolicy.PROP_MAX_CHUNK_SIZE, String
				.valueOf(maxNbBytes));

		String fileContent = ((FileDataTestHelper) fileData).fileContent;
		int nbLoop = maxNbBytes / fileContent.length();
		for (int i = 0; i < nbLoop; i += 1) {
			fileData = new FileDataTestHelper(0);
			fileData.beforeUpload();
			Assert.assertTrue("The file must be accepted",
					this.uploadFilePacket
							.canAdd(new UploadFileData(fileData, 0,
									this.fileUploadManagerThread,
									this.uploadPolicy)));
			Assert.assertTrue("The file must be accepted",
					this.uploadFilePacket.add(new UploadFileData(fileData, 0,
							this.fileUploadManagerThread, this.uploadPolicy)));
		}

		fileData = new FileDataTestHelper(0);
		Assert.assertFalse("The file must not be accepted",
				this.uploadFilePacket.canAdd(new UploadFileData(fileData, 0,
						this.fileUploadManagerThread, this.uploadPolicy)));
		Assert.assertFalse("The file must not be accepted",
				this.uploadFilePacket.add(new UploadFileData(fileData, 0,
						this.fileUploadManagerThread, this.uploadPolicy)));
	}

	/** */
	@Test
	public void testIsPoisonned() {
		Assert.assertFalse("This class is not poisonned", this.uploadFilePacket
				.isPoisonned());
	}
}
