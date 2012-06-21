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

import java.util.ArrayList;

import wjhk.jupload2.policies.UploadPolicy;

/**
 * This file contains a packet of files, which will be sent in one request to
 * the server.
 * 
 * @author etienne_sf
 */
@SuppressWarnings("serial")
public class UploadFilePacket extends ArrayList<UploadFileData> {

    /**
     * The uploadPolicy, useful to get the max number of files and max packet
     * size ... and perhaps other parameters in the future.
     */
    UploadPolicy uploadPolicy = null;

    /**
     * The sum of the size of all files in the packet.
     */
    long nbBytes = 0;

    /** The maximum number of bytes in one packet to the server */
    long maxNbBytes = -1;

    /** The maximum number of files in one packet to the server */
    long maxNbFiles = -1;

    /**
     * The standard constructor.
     * 
     * @param uploadPolicy
     */
    UploadFilePacket(UploadPolicy uploadPolicy) {
        // nbFilesPerRequest may be very very big. Let's have a more realistic
        // value, in this case (to avoid outOfMemoryError)
        super(Math.min(uploadPolicy.getNbFilesPerRequest(), uploadPolicy
                .getContext().getUploadPanel().getFilePanel().getFilesLength()));
        this.uploadPolicy = uploadPolicy;
        this.maxNbBytes = this.uploadPolicy.getMaxChunkSize();
        this.maxNbFiles = Math.min(uploadPolicy.getNbFilesPerRequest(),
                uploadPolicy.getContext().getUploadPanel().getFilePanel()
                        .getFilesLength());
    }

    /**
     * Checks if this packet can accept this file, according to the current
     * {@link UploadPolicy}.
     * 
     * @param uploadFileData
     * @return True if the file can be added, false otherwise.
     */
    public synchronized boolean canAdd(UploadFileData uploadFileData) {
        // Here is the list of conditions. This code could be smalled. But I
        // want it to be clear.
        if (size() == 0) {
            return true;
        } else if (size() == this.maxNbFiles) {
            // The packet is already full of files
            return false;
        } else if (this.nbBytes + uploadFileData.getUploadLength() > maxNbBytes) {
            // The packet would be too big
            return false;
        } else {
            // No reason to refuse this packet ...
            return true;
        }
    }

    /**
     * Indicates whether it is possible to add a file or not, to this packet,
     * according to the current upload policy.
     * 
     * @return true if the packet is full, that is: no file can be added to this
     *         packet.
     */
    public synchronized boolean isFull() {
        return (size() == this.maxNbFiles) || (this.nbBytes >= maxNbBytes);
    }

    /**
     * @param uploadFileData The file to add to the packet
     * @return true if the collection changed, that is: if the file was actually
     *         added. false if the Collection didn't change, that is: the packet
     *         is full, or th file is already there.
     */
    public synchronized boolean add(UploadFileData uploadFileData) {
        if (!canAdd(uploadFileData)) {
            return false;
        } else if (!super.add(uploadFileData)) {
            return false;
        } else {
            // The file was correctly added.
            nbBytes += uploadFileData.getUploadLength();
            ;
            return true;
        }
    }

    /**
     * @return the poisonned status. Returns always false, as this instance is a
     *         true one. false indicates the 'End Of Queue' marker in the
     *         preparedFileQueue, which is not the case here
     * @see UploadFileDataPoisonned
     */
    public boolean isPoisonned() {
        return false;
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Some ArrayList methods are prohibited here
    // ////////////////////////////////////////////////////////////////////////////
    /**
     * Prohibited !
     * 
     * @see ArrayList#remove(int)
     */
    public UploadFileData remove(int index) {
        throw new java.lang.UnsupportedOperationException(
                "Removing a file from an UploadFilePacket is prohibited");
    }

    /**
     * Prohibited !
     * 
     * @see ArrayList#remove(Object)
     */
    public boolean remove(Object o) {
        throw new java.lang.UnsupportedOperationException(
                "Removing a file from an UploadFilePacket is prohibited");
    }

    /**
     * Prohibited !
     * 
     * @see ArrayList#removeRange(int, int)
     */
    protected void removeRange(int fromIndex, int toIndex) {
        throw new java.lang.UnsupportedOperationException(
                "Removing a file from an UploadFilePacket is prohibited");
    }
}
