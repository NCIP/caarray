//
// $Id: FileUploadThread.java 1026 2010-02-24 10:12:19Z etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: ?
// Creator: William JinHua Kwong
// Last modified: $Date: 2010-02-24 11:12:19 +0100 (mer., 24 févr. 2010) $
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

package wjhk.jupload2.upload;

import java.util.concurrent.BlockingQueue;

import wjhk.jupload2.exception.JUploadException;
import wjhk.jupload2.policies.UploadPolicy;

/**
 * This interface defines the methods of the various FileUploadThread classes.
 * The default implementation is in the {@link DefaultFileUploadThread}. It
 * allows retries, for network errors. See
 * {@link UploadPolicy#PROP_RETRY_MAX_NUMBER_OF} and
 * {@link UploadPolicy#PROP_RETRY_NB_SECONDS_BETWEEN} for details.
 */
public interface FileUploadThread {
    /**
     * @return the packetQueue
     */
    public BlockingQueue<UploadFilePacket> getPacketQueue();

    /**
     * Get the server response message. In HTTP mode, it's the body part,
     * without the HTTP headers.<BR>
     * Note: was getResponseMsg until release 3.4.1.
     * 
     * @return The String that contains the HTTP response message (e.g.
     *         "SUCCESS")
     */
    public String getResponseMsg();

    /**
     * Closes the connection to the server and releases resources.
     */
    public void close();

    /**
     * @return The Thread state
     * @see Thread#getState()
     */
    public Thread.State getState();

    /**
     * This method is created in this interface, and is implemented by
     * {@link DefaultFileUploadThread}, as this class is a subclass of
     * {@link Thread}.
     * 
     * @return true if the thread is currently working.
     * @see java.lang.Thread#isAlive()
     */
    public boolean isAlive();

    /**
     * This method is created in this interface, and is implemented by
     * {@link DefaultFileUploadThread}, as this class is a subclass of
     * {@link Thread}.
     * 
     * @throws InterruptedException
     * @see java.lang.Thread#join()
     */
    public void join() throws InterruptedException;

    /**
     * This method is created in this interface, and is implemented by
     * {@link DefaultFileUploadThread}, as this class is a subclass of
     * {@link Thread}.
     * 
     * @param millisec
     * @throws InterruptedException
     * @see java.lang.Thread#join(long)
     */
    public void join(long millisec) throws InterruptedException;

    /**
     * This method is created in this interface, and is implemented by
     * {@link DefaultFileUploadThread}, as this class is a subclass of
     * {@link Thread}.
     * 
     * @see java.lang.Thread#start()
     */
    public void start();

    /**
     * This method is created in this interface, and is implemented by
     * {@link DefaultFileUploadThread}, as this class is a subclass of
     * {@link Thread}.
     * 
     * @see java.lang.Thread#interrupt()
     */
    public void interrupt();

    /**
     * Changes the FileUploadManagerThread. The standard way is to give the
     * FileUploadManagerThread to the constructor. This method is used by JUnit
     * tests, to be able to control which FileUploadThread is created.
     * 
     * @param fileUploadManagerThread
     * @throws JUploadException
     * @see FileUploadManagerThread
     */
    void setFileUploadThreadManager(
            FileUploadManagerThread fileUploadManagerThread)
            throws JUploadException;

}
