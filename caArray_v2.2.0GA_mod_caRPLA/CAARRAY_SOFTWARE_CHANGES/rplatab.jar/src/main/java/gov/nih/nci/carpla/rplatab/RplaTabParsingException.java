//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab;

public class RplaTabParsingException extends Exception {
	private String	_message;

	public String getMessage () {
		return _message;
	}

	public RplaTabParsingException(String message) {
		_message = message;
	}

}
