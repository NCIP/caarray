package gov.nih.nci.carpla.rplatab.files;

import gov.nih.nci.carpla.rplatab.RplaConstants;
import gov.nih.nci.carpla.rplatab.model.ArrayDataSectionPrincipal;
import gov.nih.nci.carpla.rplatab.model.HasName;

import java.io.File;

public class ImageFile extends ArrayDataFile {

	public ImageFile() {

		setType(RplaConstants.RplaDatasetFileType.Image);

	}

	

}
