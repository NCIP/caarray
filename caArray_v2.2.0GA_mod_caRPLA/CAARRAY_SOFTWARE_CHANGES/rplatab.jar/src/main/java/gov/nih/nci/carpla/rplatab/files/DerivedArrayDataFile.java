//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.RplaConstants;

public class DerivedArrayDataFile extends ArrayDataFile {

	public DerivedArrayDataFile() {
		super.setType(RplaConstants.RplaDatasetFileType.DerivedArrayData);
	}

	public String toString () {
		StringBuffer ret = new StringBuffer();
		ret.append("DerivedArrayDataFile(name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();

	}

}
