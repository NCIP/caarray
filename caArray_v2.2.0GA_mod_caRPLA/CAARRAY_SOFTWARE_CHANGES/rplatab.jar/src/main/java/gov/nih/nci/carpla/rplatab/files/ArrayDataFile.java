//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.RplaConstants.RplaDatasetFileType;
import gov.nih.nci.carpla.rplatab.model.ArrayDataSectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.HasName;

public class ArrayDataFile extends RplaTabDatasetFile
														implements
														HasName,
														ArrayDataSectionPrincipal {

	public void setType ( RplaDatasetFileType type) {
		super.setType(RplaConstants.RplaDatasetFileType.ArrayData);
	}

	public String getName () {
		return super.getFile().getName();
	}

	public String toString () {
		StringBuffer ret = new StringBuffer();
		ret.append("ArrayDataFile(name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();
	}

}
