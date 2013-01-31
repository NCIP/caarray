//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.Sha1SumUtility;
import gov.nih.nci.carpla.rplatab.RplaConstants.RplaDatasetFileType;

import java.io.File;

public abstract class RplaTabDatasetFile {

	private File				_file	= null;
	private RplaDatasetFileType	_type;

	public File getFile () {
		return this._file;
	}

	public RplaDatasetFileType getType () {
		return this._type;

	}

	protected void setType ( RplaDatasetFileType type) {

		this._type = type;
	}

	public String getSha1SumFromStreamedFile () {
		return Sha1SumUtility.getSha1SumFromStreamedFile(_file);
	}

	public void setFile ( File file) throws RplaTabDatasetFileException {

		if (file == null) {
			throw new RplaTabDatasetFileException(	"file reference is null",
													file);
		}

		if (file.length() == 0) {

			throw new RplaTabDatasetFileException("file is of zero size", file);
		}

		if (file.canRead() == false) {

			throw new RplaTabDatasetFileException("cannot read file", file);
		}

		this._file = file;

	}
}
