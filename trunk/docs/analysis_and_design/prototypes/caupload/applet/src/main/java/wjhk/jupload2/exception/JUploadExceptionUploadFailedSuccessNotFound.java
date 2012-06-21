//
// $Id: JUploadExceptionUploadFailed.java 887 2009-11-06 21:07:21Z etienne_sf $
//
// jupload - A file upload applet.
// Copyright 2007 The JUpload Team
//
// Created: 2006-09-15
// Creator: etienne_sf
// Last modified: $Date: 2009-11-06 22:07:21 +0100 (ven., 06 nov. 2009) $
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

package wjhk.jupload2.exception;

/**
 * @author etienne_sf
 * 
 */
public class JUploadExceptionUploadFailedSuccessNotFound extends
		JUploadExceptionUploadFailed {

	/** */
	private static final long serialVersionUID = -5899077547982544361L;

	/** @param msg */
	public JUploadExceptionUploadFailedSuccessNotFound(String msg) {
		super(msg);
	}

	/** @param cause */
	public JUploadExceptionUploadFailedSuccessNotFound(Exception cause) {
		super(cause);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public JUploadExceptionUploadFailedSuccessNotFound(String msg,
			Throwable cause) {
		super(msg, cause);
	}

}
