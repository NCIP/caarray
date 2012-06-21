//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 25 fevr. 2010
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
package wjhk.jupload2.filedata;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.testhelpers.JUploadContextTestHelper;
import wjhk.jupload2.testhelpers.UploadPolicyTestHelper;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;
import wjhk.jupload2.upload.helper.ByteArrayEncoder;
import wjhk.jupload2.upload.helper.ByteArrayEncoderHTTP;

/**
 * @author etienne_sf
 */
public class DefaultFileDataTest extends AbstractJUploadTestHelper {

	private String rootPath;

	private DefaultFileData fileData;

	private File file;

	private File root;

	/** */
	@Before
	public void setUp() {
		this.rootPath = AbstractJUploadTestHelper.getTestFilesRootPath();
		String filePath = this.rootPath + File.separator + "level1"
				+ File.separator + "ATestFile.txt";
		this.file = new File(filePath);
		this.root = new File(this.rootPath);

		Assert.assertTrue("The file must exist (" + filePath + ")", this.file
				.exists());
		Assert.assertTrue("The root must exist (" + this.rootPath + ")",
				this.root.exists());
		Assert.assertTrue("The root must be a valid folder", this.root
				.isDirectory());
		Assert
				.assertTrue("The test file must be readable", this.file
						.canRead());

		this.fileData = new DefaultFileData(this.file, this.root,
				this.uploadPolicy);
	}

	/** */
	@Test
	public void testConstructor() {
		// Check file attribute and getter
		Assert.assertEquals("Check of the file attribute", this.file,
				this.fileData.file);
		Assert.assertEquals("Check of the file getter", this.file,
				this.fileData.getFile());

		// Check fileSize attribute and getter
		Assert.assertEquals("Check of the fileSize attribute", this.file
				.length(), this.fileData.fileSize);
		Assert.assertEquals("Check of the fileSize getter", this.file.length(),
				this.fileData.getFileLength());

		// Check lastModified attribute and getter
		Assert.assertEquals("Check of the lastModified attribute", new Date(
				this.file.lastModified()), this.fileData.fileModified);
		Assert.assertEquals("Check of the lastModified getter", new Date(
				this.file.lastModified()), this.fileData.getLastModified());

		// Check mimeType attribute and getter
		Assert.assertEquals("Check of the mimeType attribute",
				JUploadContextTestHelper.TEST_CASE_MIME_TYPE,
				this.fileData.mimeType);
		Assert.assertEquals("Check of the mimeType getter",
				JUploadContextTestHelper.TEST_CASE_MIME_TYPE, this.fileData
						.getMimeType());

		// Check parent attribute and getter
		Assert.assertEquals("Check of the parent attribute", this.file
				.getAbsoluteFile().getParent(), this.fileData.fileDir);
		Assert.assertEquals("Check of the parent getter", this.file
				.getAbsoluteFile().getParent(), this.fileData.getDirectory());

		// Check root attribute
		Assert.assertEquals("Check of the root attribute", this.root
				.getAbsolutePath(), this.fileData.fileRoot);

		// Check uploadPolicy attribute
		Assert.assertEquals("Check of the uploadPolicy attribute",
				this.uploadPolicy, this.fileData.uploadPolicy);

		// Check preparedForUpload attribute
		Assert.assertEquals("Check of the preparedForUpload attribute", false,
				this.fileData.preparedForUpload);
	}

	/**
	 * @throws JUploadIOException
	 */
	@Test
	public void testAppendFileProperties() throws JUploadIOException {
		// /////////////////////////////////////////////////////////////////////////////
		// ////////// A Utility class, to check the properties wich are managed
		// /////////////////////////////////////////////////////////////////////////////
		class ByteArrayEncoderHTTP_CheckAppendTextProperty extends
				ByteArrayEncoderHTTP {
			String mimeType = null;

			String pathInfo = null;

			String relpathinfo = null;

			String filemodificationdate = null;

			ByteArrayEncoderHTTP_CheckAppendTextProperty()
					throws JUploadIOException {
				super(DefaultFileDataTest.this.uploadPolicy);
			}

			@Override
			public ByteArrayEncoder appendTextProperty(String name,
					String value, int index) throws JUploadIOException {
				if (name.equals("mimetype")) {
					this.mimeType = value;
				} else if (name.equals("pathinfo")) {
					this.pathInfo = value;
				} else if (name.equals("relpathinfo")) {
					this.relpathinfo = value;
				} else if (name.equals("filemodificationdate")) {
					this.filemodificationdate = value;
				} else {
					throw new java.lang.IllegalArgumentException(
							"Unknown property : " + name);
				}
				return this;
			}
		}
		// /////////////////////////////////////////////////////////////////////////////
		ByteArrayEncoderHTTP_CheckAppendTextProperty bae = new ByteArrayEncoderHTTP_CheckAppendTextProperty();

		int index = 58;
		this.fileData.appendFileProperties(bae, index);
		Assert.assertEquals("Check mimeType value",
				JUploadContextTestHelper.TEST_CASE_MIME_TYPE, bae.mimeType);
		Assert.assertEquals("Check pathInfo value", this.file.getParentFile()
				.getAbsolutePath(), bae.pathInfo);

		// The relpathinfo, is the path relative to the file root.
		Assert
				.assertEquals(
						"The relpathinfo is the part of the file absolute path, wich is after the file root",
						this.file.getParentFile().getAbsolutePath(), this.root
								.getAbsolutePath()
								+ File.separator + bae.relpathinfo);
		// date here.
		Assert.assertNotNull("Check mimeType value", bae.filemodificationdate);
	}

	/**
	 * @throws JUploadException
	 */
	@Test
	public void testBeforeUpload() throws JUploadException {
		// The next call should not throw an exception.
		this.fileData.beforeUpload();

		this.fileData.preparedForUpload = false;
		((UploadPolicyTestHelper) this.uploadPolicy).maxFileSize = 5;
		try {
			// The next call should throw an exception.
			this.fileData.beforeUpload();
			Assert.fail("The file should be too big!");
		} catch (JUploadException e) {
			// Success !
		}
	}

	/** */
	@Test
	public void testGetUploadLength() {
		this.fileData.preparedForUpload = false;
		try {
			this.fileData.getUploadLength();
			Assert
					.fail("getUploadLength should raise an exception when the file is not prepared for upload");
		} catch (IllegalStateException e) {
			// Success!
		}

		this.fileData.preparedForUpload = true;
		Assert.assertEquals("Check upload length", this.file.length(),
				this.fileData.getUploadLength());
	}

	/** */
	@Test
	public void testAfterUpload() {
		this.fileData.preparedForUpload = false;
		try {
			this.fileData.afterUpload();
			Assert
					.fail("getUploadLength should raise an exception when the file is not prepared for upload");
		} catch (IllegalStateException e) {
			// Success!
		}

		this.fileData.preparedForUpload = true;
		this.fileData.afterUpload();
		Assert.assertEquals(
				"After afterUpload(), the file is no more prepared", false,
				this.fileData.preparedForUpload);
	}

	/**
	 * @throws JUploadException
	 * @throws IOException
	 */
	@Test
	public void testGetInputStream() throws JUploadException, IOException {
		this.fileData.preparedForUpload = false;
		try {
			this.fileData.afterUpload();
			Assert
					.fail("getUploadLength should raise an exception when the file is not prepared for upload");
		} catch (IllegalStateException e) {
			// Success!
		}

		this.fileData.preparedForUpload = true;
		InputStream is = this.fileData.getInputStream();
		// Success !
		is.close();
	}

	/** */
	@Test
	public void testGetFileName() {
		Assert.assertEquals("Check file name", this.file.getName(),
				this.fileData.getFileName());
	}

	/** */
	@Test
	public void testGetFileExtension() {
		int lastPoint = this.file.getName().lastIndexOf(".");
		Assert.assertTrue("We must have a point, to find the extension!",
				lastPoint >= 0);
		String extension = this.file.getName().substring(lastPoint + 1);
		Assert.assertEquals("Check file extension", extension, DefaultFileData
				.getExtension(this.file));
	}

	/** */
	@Test
	public void testCanRead() {
		Assert.assertTrue("should be able to read the test file (attribute)",
				this.fileData.canRead());
		Assert.assertTrue("should be able to read the test file (getter)",
				this.fileData.canRead.booleanValue());

		this.fileData = new DefaultFileData(new File("This is not a file"),
				null, this.uploadPolicy);
		Assert.assertFalse("should be able to read the test file (attribute)",
				this.fileData.canRead());
		Assert.assertFalse("should be able to read the test file (getter)",
				this.fileData.canRead.booleanValue());
	}

	/** */
	@Test
	public void testGetRelativeDire() {
		// The relpathinfo, is the path relative to the file root.
		Assert
				.assertEquals(
						"The relpathinfo is the part of the file absolute path, wich is after the file root",
						this.file.getParentFile().getAbsolutePath(), this.root
								.getAbsolutePath()
								+ File.separator
								+ this.fileData.getRelativeDir());

	}

	/** */
	@Test
	public void getPreparedForUpload() {
		this.fileData.preparedForUpload = true;
		Assert.assertTrue("File is prepared", this.fileData
				.isPreparedForUpload());
		this.fileData.preparedForUpload = false;
		Assert.assertFalse("File is not prepared", this.fileData
				.isPreparedForUpload());
	}

	/** */
	@Test
	public void testGetRoot() {
		File[] fileArray = new File[5];

		fileArray[0] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[1] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		fileArray[2] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[3] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		fileArray[4] = new File(this.rootPath + "level1/level2/level3/file.txt");
		String result = DefaultFileData.getRoot(fileArray).getAbsolutePath();
		String expected = this.rootPath + "level1" + File.separator + "level2";
		Assert.assertTrue("Check getRoot: checking '" + result + "' against '"
				+ expected + "'", result.endsWith(expected));

		fileArray[0] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[1] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		fileArray[2] = new File(this.rootPath
				+ "level1/level22/level3/file.txt");
		fileArray[3] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		fileArray[4] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		result = DefaultFileData.getRoot(fileArray).getAbsolutePath();
		expected = this.rootPath + "level1";
		Assert.assertTrue("Check getRoot: checking '" + result + "' against '"
				+ expected + "'", result.endsWith(expected));

		fileArray[0] = new File(this.rootPath
				+ "level11/level2/level3/file.txt");
		fileArray[1] = new File(this.rootPath
				+ "level1/level2/level33/file.txt");
		fileArray[2] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[3] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[4] = new File(this.rootPath + "level1.txt");
		result = DefaultFileData.getRoot(fileArray).getAbsolutePath()
				+ File.separator;
		expected = this.rootPath;
		Assert.assertTrue("Check getRoot: checking '" + result + "' against '"
				+ expected + "'", result.endsWith(expected));

		fileArray[0] = new File(this.rootPath);
		fileArray[1] = new File(this.rootPath
				+ "level1/level2/level3333/file.txt");
		fileArray[2] = new File(this.rootPath + "level1/level2/level3/file.txt");
		fileArray[3] = new File(this.rootPath
				+ "/level1/level2/level3/level4444/level5/file.txt");
		fileArray[4] = new File(this.rootPath
				+ "/level1/level2/level3/level4/level5555/file.txt");
		result = DefaultFileData.getRoot(fileArray).getAbsolutePath()
				+ File.separator;
		expected = new File(this.rootPath).getParent() + File.separator;
		Assert.assertTrue("Check getRoot: checking '" + result + "' against '"
				+ expected + "'", result.endsWith(expected));
	}
}