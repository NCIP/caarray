//
// $Id$
//
// jupload - A file upload applet.
//
// Copyright 2010 The JUpload Team
//
// Created: 12 fevr. 2010
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

package wjhk.jupload2.exception;

import wjhk.jupload2.policies.UploadPolicy;

/**
 * This error is thrown, when the socket used to read bytes from the server is
 * closed. Previously, the applet would throw an 'unexpected EOF' error, in this
 * case.
 * 
 * @author etienne_sf
 */
@SuppressWarnings("serial")
public class JUploadEOFException extends JUploadIOException {

    UploadPolicy uploadPolicy;

    String actionInError = null;

    /**
     * @param uploadPolicy
     * @param actionInError
     */
    public JUploadEOFException(UploadPolicy uploadPolicy, String actionInError) {
        super("Unexpected end of communication with the server");
        this.uploadPolicy = uploadPolicy;
        this.actionInError = actionInError;
    }

    /**
     * @return The error messsage. When debug level is 30 or more, the
     *         actionInError is added to the error message.
     * 
     */
    public String getMessage() {
        if (this.uploadPolicy.getDebugLevel() >= 30) {
            return super.getMessage();
        } else {
            return super.getMessage() + " (" + this.actionInError + ")";
        }
    }
}
