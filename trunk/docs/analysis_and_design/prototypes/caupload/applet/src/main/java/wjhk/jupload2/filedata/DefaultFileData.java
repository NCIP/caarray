//
// $Id: DefaultFileData.java 267 2007-06-08 13:42:02 +0000 (ven., 08 juin 2007)
// felfert $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: 2006-04-21
// Creator: etienne_sf
// Last modified: $Date: 2011-01-19 15:53:25 +0100 (mer., 19 janv. 2011) $
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

package wjhk.jupload2.filedata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.exception.JUploadExceptionTooBigFile;
import wjhk.jupload2.exception.JUploadIOException;
import wjhk.jupload2.policies.DefaultUploadPolicy;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.helper.ByteArrayEncoder;

/**
 * This class contains all data and methods for a file to upload. The current
 * {@link wjhk.jupload2.policies.UploadPolicy} contains the necessary parameters
 * to personalize the way files must be handled. <BR>
 * <BR>
 * This class is the default FileData implementation. It gives the default
 * behaviour, and is used by {@link DefaultUploadPolicy}. It provides standard
 * control on the files choosen for upload.
 * 
 * @see FileData
 * @author etienne_sf
 */
public class DefaultFileData implements FileData {

	/**
	 * The current upload policy.
	 */
	static UploadPolicy uploadPolicy;

	/**
	 * Indicates whether the file is prepared for upload or not.
	 * 
	 * @see FileData#isPreparedForUpload()
	 */
	boolean preparedForUpload = false;

	private final static int BUFLEN = 4096;

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////// Protected attributes
	// /////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Mime type of the file. It will be written in the upload HTTP request.
	 */
	protected String mimeType = "application/octet-stream";

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////// Private attributes
	// ////////////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * file is the file about which this FileData contains data.
	 */
	protected File file;

	/**
	 * Cached file size
	 */
	protected long fileSize;

	/**
	 * Cached file directory
	 */
	protected String fileDir;

	/**
	 * cached root of this file
	 */
	protected String fileRoot = "";

	/**
	 * Cached file modification time.
	 */
	protected Date fileModified;

	/**
	 * The md5sum for the prepared file. Calculated in the
	 * {@link #beforeUpload()}, and cleared in the {@link #afterUpload()}.
	 */
	protected String md5sum = null;

	/**
	 * Indicates whether the applet can read this file or not.
	 */
	protected Boolean canRead = null;

	/**
	 * Standard constructor
	 * 
	 * @param file
	 *            The file whose data this instance will give.
	 * @param root
	 *            The directory root, to be able to calculate the result of
	 *            {@link #getRelativeDir()}
	 * @param uploadPolicyParam
	 *            The current upload policy.
	 */
	public DefaultFileData(File file, File root, UploadPolicy uploadPolicyParam) {
		this.file = file;
		uploadPolicy = uploadPolicyParam;
		this.fileSize = this.file.length();
		this.fileDir = this.file.getAbsoluteFile().getParent();
		this.fileModified = new Date(this.file.lastModified());
		if (null != root) {
			this.fileRoot = root.getAbsolutePath();
			uploadPolicy.displayDebug("Creation of the DefaultFileData for "
					+ file.getAbsolutePath() + "(root: "
					+ root.getAbsolutePath() + ")", 10);
		} else {
			uploadPolicy.displayDebug("Creation of the DefaultFileData for "
					+ file.getAbsolutePath() + "(root: null)", 10);
		}

		// Let
		this.mimeType = uploadPolicy.getContext().getMimeType(
				getFileExtension());
	}

	/** {@inheritDoc} */
	public void appendFileProperties(ByteArrayEncoder bae, int index)
			throws JUploadIOException {
		bae.appendTextProperty("mimetype", getMimeType(), index);
		bae.appendTextProperty("pathinfo", getDirectory(), index);
		bae.appendTextProperty("relpathinfo", getRelativeDir(), index);
		// To add the file date/time, we first have to format this date.
		SimpleDateFormat dateformat = new SimpleDateFormat(uploadPolicy
				.getDateFormat());
		String uploadFileModificationDate = dateformat
				.format(getLastModified());
		bae.appendTextProperty("filemodificationdate",
				uploadFileModificationDate, index);
	}

	/** {@inheritDoc} */
	public synchronized void beforeUpload() throws JUploadException {
		if (this.preparedForUpload) {
			// Maybe an upload was stopped. Let's log a resume, and resume the
			// job.
			uploadPolicy.displayWarn("The file " + getFileName()
					+ " is already prepared for upload");
		} else {
			// The file is now prepared for upload.
			this.preparedForUpload = true;

			// Should we calculate the MD5Sum for this file ?
			if (uploadPolicy.getSendMD5Sum()) {
				calculateMD5Sum();
			}

			// Default : we check that the file is smaller than the maximum
			// upload size.
			if (getUploadLength() > uploadPolicy.getMaxFileSize()) {
				throw new JUploadExceptionTooBigFile(getFileName(),
						getUploadLength(), uploadPolicy);
			}
		}
	}

	/** {@inheritDoc} */
	public long getUploadLength() {
		if (!this.preparedForUpload) {
			throw new IllegalStateException("The file " + getFileName()
					+ " is not prepared for upload");
		}
		return this.fileSize;
	}

	/** {@inheritDoc} */
	public synchronized void afterUpload() {
		if (!this.preparedForUpload) {
			throw new IllegalStateException("The file " + getFileName()
					+ " is not prepared for upload");
		}
		// Let's free resources or temporary calculation in DefaultFileData
		this.md5sum = null;

		// Then, we change the preparation status.
		this.preparedForUpload = false;
	}

	/** {@inheritDoc} */
	public synchronized InputStream getInputStream() throws JUploadException {
		if (!this.preparedForUpload) {
			throw new IllegalStateException("The file " + getFileName()
					+ " is not prepared for upload");
		}
		// Standard FileData : we read the file.
		try {
			return new FileInputStream(this.file);
		} catch (FileNotFoundException e) {
			throw new JUploadIOException(e);
		}
	}

	/** {@inheritDoc} */
	public String getFileName() {
		return this.file.getName();
	}

	/** {@inheritDoc} */
	public String getFileExtension() {
		return getExtension(this.file);
	}

	/** {@inheritDoc} */
	public long getFileLength() {
		return this.fileSize;
	}

	/** {@inheritDoc} */
	public Date getLastModified() {
		return this.fileModified;
	}

	/** {@inheritDoc} */
	public String getDirectory() {
		return this.fileDir;
	}

	/** {@inheritDoc} */
	public String getMD5() throws JUploadException {
		if (this.md5sum == null) {
			throw new JUploadException("The MD5Sum has not been calculated!");
		}
		return this.md5sum;
	}

	/**
	 * Calculate the MD5Sum for the transformed file, or the original if no
	 * transformation should be done on the file, before upload.
	 * 
	 * @throws JUploadException
	 */
	public void calculateMD5Sum() throws JUploadException {
		StringBuffer ret = new StringBuffer();
		MessageDigest digest = null;
		byte md5Buffer[] = new byte[BUFLEN];
		int nbBytes;

		// Calculation of the MD5 sum. Now done before upload, to prepare the
		// file head.
		// This makes the file being parsed two times: once before upload, and
		// once for the actual upload
		InputStream md5InputStream = getInputStream();
		try {
			digest = MessageDigest.getInstance("MD5");
			while ((nbBytes = md5InputStream.read(md5Buffer, 0, BUFLEN)) > 0) {
				digest.update(md5Buffer, 0, nbBytes);
			}
		} catch (IOException e) {
			throw new JUploadIOException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new JUploadException(e);
		} finally {
			try {
				md5InputStream.close();
			} catch (IOException e) {
				throw new JUploadIOException(e);
			}
		}

		// Now properly format the md5 sum.
		byte md5sum[] = new byte[32];
		if (digest != null)
			md5sum = digest.digest();
		for (int i = 0; i < md5sum.length; i++) {
			ret.append(Integer.toHexString((md5sum[i] >> 4) & 0x0f));
			ret.append(Integer.toHexString(md5sum[i] & 0x0f));
		}

		this.md5sum = ret.toString();
	}

	/** {@inheritDoc} */
	public String getMimeType() {
		return this.mimeType;
	}

	/** {@inheritDoc} */
	public boolean canRead() {
		// The commented line below doesn't seems to work.
		// return this.file.canRead();

		// The canRead status is read once. This is done in this method, so that
		// it's available for all subclasses. If it were in the constructor, we
		// would have to initialize {@link #canRead} in all subclasses.

		// Let's store the status 'readible' only once. It's
		if (this.canRead == null) {
			try {
				InputStream is = new FileInputStream(this.file);
				is.close();
				this.canRead = Boolean.valueOf(true);
			} catch (IOException e) {
				// Can't read the file!
				this.canRead = Boolean.valueOf(false);
			}
		}

		return this.canRead.booleanValue();
	}

	/** {@inheritDoc} */
	public File getFile() {
		return this.file;
	}

	/** {@inheritDoc} */
	public String getRelativeDir() {
		if (null != this.fileRoot && (!this.fileRoot.equals(""))
				&& (this.fileDir.startsWith(this.fileRoot))) {
			int skip = this.fileRoot.length();
			if (!this.fileRoot.endsWith(File.separator))
				skip++;
			if ((skip >= 0) && (skip < this.fileDir.length()))
				return this.fileDir.substring(skip);
		}
		return "";
	}

	// ////////////////////////////////////////////////////////
	// UTILITIES
	// ////////////////////////////////////////////////////////
	/**
	 * Returns the extension of the given file. To be clear: <I>jpg</I> is the
	 * extension for the file named <I>picture.jpg</I>.
	 * 
	 * @param file
	 *            the file whose the extension is wanted!
	 * @return The extension, without the point, for the given file.
	 */
	public static String getExtension(File file) {
		String name = file.getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	/**
	 * Return the 'biggest' common ancestror of the given file array. For
	 * instance, the root for the files /usr/bin/toto and /usr/titi is /usr.
	 * 
	 * @param fileArray
	 * @return The common root for the given files.
	 */
	public static File getRoot(File[] fileArray) {
		// Let's find the common root for the dropped files.
		// If one file has been dropped (the minimum), the path of its parent
		// should be the root.
		File root = fileArray[0];
		if (root.isDirectory()) {
			root = root.getParentFile();
		}
		// Let's find the higher root level.
		while (root != null && !root.isDirectory()) {
			// We have a file. Let's take it's folder.
			root = root.getParentFile();
		}

		if (root != null) {
			// root is the root for the first file. We add all directories,
			// and higher until the root. This will allow to find the
			// 'bigger' directory, which is the common root for all dropped
			// files.
			// If several files are being added, we take the common root for
			// them.
			String pathRoot = root.getAbsolutePath() + File.separator;
			String pathCurrentFileParentPath;
			File pathCurrentFileParent;

			// We start with the second item in the list, as we already
			// extracted the first.
			for (int i = 1; i < fileArray.length && root != null; i += 1) {
				// Let's check that all files in l are parents of the current
				// file.
				pathCurrentFileParent = fileArray[i];
				pathCurrentFileParentPath = pathCurrentFileParent
						.getAbsolutePath()
						+ File.separator;

				// We loop through the parent of the file, until we find a
				// common root.
				while (pathCurrentFileParent != null
						&& !pathRoot.startsWith(pathCurrentFileParentPath)) {
					pathCurrentFileParent = pathCurrentFileParent
							.getParentFile();
					pathCurrentFileParentPath = pathCurrentFileParent
							.getAbsolutePath()
							+ File.separator;
				}

				// Let's store the new found root (which may actually be the
				// same as the last one)
				root = pathCurrentFileParent;
				pathRoot = pathCurrentFileParentPath;
			}// for

			// pathRoot contains the path for the found root.
			root = new File(pathRoot);
		}

		return root;
	}

	/** {@inheritDoc} */
	public boolean isPreparedForUpload() {
		return this.preparedForUpload;
	}
}
