//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import java.io.File;

public class RplaTabDatasetFileException extends Exception {

	private File	_file;
	private String	_message;

	public RplaTabDatasetFileException(String message) {
		_message = message;

	}

	public RplaTabDatasetFileException(String message, File file) {
		_message = message;
		_file = file;

	}

	public String getMessage () {
		// todo include file
		return _message;
	}

}
