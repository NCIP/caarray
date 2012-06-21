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

import java.util.concurrent.BlockingQueue;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;
import wjhk.jupload2.upload.FileUploadManagerThread;
import wjhk.jupload2.upload.FileUploadThread;
import wjhk.jupload2.upload.UploadFilePacket;

/**
 * @author etienne_sf
 * 
 */
public class FileUploadThreadTestHelper extends Thread implements
		FileUploadThread {
	/** */
	public BlockingQueue<UploadFilePacket> packetQueue;
	/** */
	public FileUploadManagerThread fileUploadManagerThread;
	/** */
	public String responseMsg = UploadPolicy.PROP_STRING_UPLOAD_SUCCESS;

	/**
	 * @param packetQueue
	 */
	public FileUploadThreadTestHelper(BlockingQueue<UploadFilePacket> packetQueue) {
		this.packetQueue = packetQueue;
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadThread#close()
	 */
	public void close() {
		// Nothing to do
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadThread#getPacketQueue()
	 */
	public BlockingQueue<UploadFilePacket> getPacketQueue() {
		return this.packetQueue;
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadThread#getResponseMsg()
	 */
	public String getResponseMsg() {
		return this.responseMsg;
	}

	/**
	 * @see wjhk.jupload2.upload.FileUploadThread#setFileUploadThreadManager(wjhk.jupload2.upload.FileUploadManagerThread)
	 */
	public void setFileUploadThreadManager(
			FileUploadManagerThread fileUploadManagerThread)
			throws JUploadException {
		this.fileUploadManagerThread = fileUploadManagerThread;
	}

}
