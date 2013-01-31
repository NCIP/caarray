//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.carpla.rplatab.RplaConstants;

import java.io.File;

public class RplaIdfFile extends RplaTabDatasetFile {

	public RplaIdfFile() {
		setType(RplaConstants.RplaDatasetFileType.RplaIdf);
	}

}
