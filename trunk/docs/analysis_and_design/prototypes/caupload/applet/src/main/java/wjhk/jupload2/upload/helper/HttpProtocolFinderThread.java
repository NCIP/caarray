//
// $Id: DefaultUploadPolicy.java 289 2007-06-19 10:04:46 +0000 (mar., 19 juin
// 2007) etienne_sf $
//
// jupload - A file upload juploadContext.
// Copyright 2007 The JUpload Team
//
// Created: 2006-05-04
// Creator: etienne_sf
// Last modified: $Date: 2010-01-23 18:39:37 +0100 (sam., 23 janv. 2010) $
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
package wjhk.jupload2.upload.helper;

import wjhk.jupload2.policies.UploadPolicy;

/**
 * This Thread executes a HEAD request to the server. From the server response,
 * the exact HTTP protocol, and any possible redirection are checked and used
 * (if any) to update the uploadURL.<BR>
 * The entry point for this method is the static
 * {@link #computeServerProtocol(UploadPolicy, String)} method.
 * 
 * @author etienne_sf
 */
public class HttpProtocolFinderThread extends Thread {
    /** The current upload policy */
    UploadPolicy uploadPolicy;

    /** The given which should be analysed, to find the server protocol */
    String givenServerProtocol;

    /**
     * This static method is the entry point for this class. It creates a
     * thread, and launch it, returning immediatly. The finding of the server
     * protocol is then executed in a separated thread. So whenver long it is,
     * the user won't see it. A default protocol is set immediatly, in the run
     * method, to be sure that there will be no NullPointerException.
     * 
     * @param uploadPolicy The current upload policy
     * @param givenServerProtocol The protocol given as parameter. If valid it
     *            will be used. If not, the serverProtocol will be computed from
     *            the post URL (and from a HEAD request for HTTP URL).
     */
    static public void computeServerProtocol(UploadPolicy uploadPolicy,
            String givenServerProtocol) {
        new HttpProtocolFinderThread(uploadPolicy, givenServerProtocol).start();
    }

    /**
     * @param uploadPolicy The current upload policy
     * @param givenServerProtocol The protocol given as parameter. If valid it
     *            will be used. If not, the serverProtocol will be computed from
     *            the post URL (and from a HEAD request for HTTP URL).
     */
    public HttpProtocolFinderThread(UploadPolicy uploadPolicy,
            String givenServerProtocol) {
        //Let's name this thread. It's easier for debugging.
        super("HttpProtocolFinderThread");
        
        this.uploadPolicy = uploadPolicy;
        this.givenServerProtocol = givenServerProtocol;
    }

    /**
     * The job itself. Will do a HEAD request if it's a HTTP URL. Will just note
     * ftp if FTP. Otherwise: will throw an error.
     */
    public void run() {
        String computedProtocol = null;
        String postURL = this.uploadPolicy.getPostURL();

        if (null == givenServerProtocol || givenServerProtocol.equals("")) {
            if (null == postURL || postURL.equals("")) {
                this.uploadPolicy.displayErr("postURL not set");
                computedProtocol = UploadPolicy.DEFAULT_SERVER_PROTOCOL;
            } else if (postURL.substring(0, 3).equals("ftp")) {
                computedProtocol = "ftp";
            } else {
                try {
                    this.uploadPolicy.displayDebug(
                            "Getting serverProtocol from HEAD request", 30);

                    // Let's set a default protocol immediatly. It should be
                    // good, and avoid a NullPointerException if an upload
                    // starts immediatly.
                    this.uploadPolicy
                            .setServerProtocol(UploadPolicy.DEFAULT_SERVER_PROTOCOL);
                    // Then we do the head request to the server
                    computedProtocol = new HttpConnect(this.uploadPolicy)
                            .getProtocol();
                } catch (Exception e) {
                    // If we throw an error here, we prevent the applet
                    // to
                    // start. So we just log it, and try the default protocol
                    this.uploadPolicy.displayErr(
                            "Unable to access to the postURL: '" + postURL
                                    + "'", e);
                    // Let's try with default value.
                    computedProtocol = UploadPolicy.DEFAULT_SERVER_PROTOCOL;
                }
            }
        } else if (this.givenServerProtocol.startsWith("HTTP")) {
            try {
                // In HTTP mode, we always give a try to HTTPConnect, to check
                // if the page has moved, and other stuff.
                // But we keep the parameter given when calling this method.
                this.uploadPolicy.displayDebug(
                        "Checking any redirect, from HEAD request", 30);
                // Let's set a default protocol immediatly. It should be
                // good, and avoid a NullPointerException if an upload
                // starts immediatly.
                this.uploadPolicy
                        .setServerProtocol(UploadPolicy.DEFAULT_SERVER_PROTOCOL);
                // Then we do the head request to the server
                computedProtocol = new HttpConnect(this.uploadPolicy)
                        .getProtocol();
            } catch (Exception e) {
                // If we throw an error here, we prevent the applet to
                // start. So we just log it, and try the default protocol
                this.uploadPolicy.displayErr(
                        "Unknown to get protocol in the given postURL ("
                                + this.uploadPolicy.getPostURL()
                                + "), due to error: " + e.getMessage(), e);
            }
        }
        this.uploadPolicy.setServerProtocol(computedProtocol);
    }
}
