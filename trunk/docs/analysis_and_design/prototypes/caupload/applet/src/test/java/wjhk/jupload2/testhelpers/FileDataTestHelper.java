//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 5 fevr. 2010
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.filedata.FileData;
import wjhk.jupload2.upload.AbstractJUploadTestHelper;
import wjhk.jupload2.upload.helper.ByteArrayEncoder;

/**
 * @author etienne_sf
 * 
 */
public class FileDataTestHelper implements FileData {
	/**	 */
	public String relativeDir = File.separator + "files";

	/** */
	public String fileName;

	/** */
	public File file;

	/** */
	public int fileNumber;

	/** */
	public String fileContent;

	/** */
	public String fileExtention = "txt";

	/** */
	public String md5sum = null;

	/** */
	public String mimeType = "text/plain";

	/** */
	public Date lastModified = new Date();

	/** */
	public boolean preparedForUpload = true;

	/**
	 * @param fileNumber
	 */
	public FileDataTestHelper(int fileNumber) {
		this.fileNumber = fileNumber;
		this.fileName = fileNumber + "." + this.fileExtention;
		this.file = AbstractJUploadTestHelper.getTestFile(this.relativeDir
				+ File.separator + this.fileName);
		this.fileContent = "This is the file content for the file number "
				+ fileNumber;
	}

	/** */
	public void afterUpload() {
		// No action
	}

	/**
	 * @param bae
	 * @param index
	 * @throws JUploadIOException
	 */
	public void appendFileProperties(ByteArrayEncoder bae, int index)
			throws JUploadIOException {
		throw new UnsupportedOperationException(this.getClass()
				+ ".appendFileProperties() is not implemented in tests cases");
	}

	/**
	 * @throws JUploadException
	 */
	public void beforeUpload() throws JUploadException {
		// No action
	}

	/**
	 * @return canRead
	 * */
	public boolean canRead() {
		return true;
	}

	/**
	 * @return dir
	 */
	public String getDirectory() {
		return this.file.getParent();
	}

	/**
	 * @return file
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * @return extention
	 */
	public String getFileExtension() {
		return this.fileExtention;
	}

	/**
	 * @return length
	 */
	public long getFileLength() {
		return this.fileContent.length();
	}

	/**
	 * @return filename
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * @return inputStream
	 * @throws JUploadException
	 */
	public InputStream getInputStream() throws JUploadException {
		return new ByteArrayInputStream(this.fileContent.getBytes());
	}

	/**
	 * @return last modified
	 */
	public Date getLastModified() {
		return this.lastModified;
	}

	/**
	 * @see wjhk.jupload2.filedata.FileData#getMD5()
	 */
	public String getMD5() throws JUploadException {
		return this.md5sum;
	}

	/**
	 * @return mime type
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * @return rel dir
	 */
	public String getRelativeDir() {
		return this.relativeDir;
	}

	/**
	 * @return upload length
	 */
	public long getUploadLength() {
		return this.fileContent.length();
	}

	/**
	 * @return is prepared
	 */
	public boolean isPreparedForUpload() {
		return this.preparedForUpload;
	}

}
