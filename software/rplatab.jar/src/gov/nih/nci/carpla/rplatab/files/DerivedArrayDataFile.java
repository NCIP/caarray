package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.RplaConstants;

public class DerivedArrayDataFile extends ArrayDataFile {

	public DerivedArrayDataFile() {
		super.setType(RplaConstants.RplaDatasetFileType.DerivedArrayData);
	}
}
