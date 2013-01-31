//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.model.ArrayDataSectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.HasName;

import java.io.File;

public class ImageFile extends ArrayDataFile {

	public ImageFile() {

		setType(RplaConstants.RplaDatasetFileType.Image);

	}

	public String toString () {
		StringBuffer ret = new StringBuffer();
		ret.append("ImageFile (name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();

	}

}
