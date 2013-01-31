//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import java.io.*;

public class TermSource {

	public TermSource(String value) {
		_name = value;
	}

	private String	_name;
	private String	_filename;
	private String	_version;

	/**
	 * @return the file
	 */
	public String getFileName () {
		return this._filename;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFileName ( String fileName) {
		this._filename = fileName;
	}

	/**
	 * @return the name
	 */
	public String getName () {
		return this._name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName ( String name) {
		this._name = name;
	}

	/**
	 * @return the version
	 */
	public String getVersion () {
		return this._version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion ( String version) {
		this._version = version;
	}

}
