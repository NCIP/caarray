package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.util.io.FileUtility;
import gov.nih.nci.carpla.rplatab.files.ImageFile;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class RplaTabInputFileSet {

	private  RplaIdfFile	_rplaIdfFile ;	

	private  SradfFile	_sradfFile	;

	private Set<ImageFile> _imagefiles = new HashSet<ImageFile>();
	
	public RplaIdfFile getRplaIdfFile(){
		return _rplaIdfFile ;
	}
	
	public SradfFile getSradfFile(){
		return _sradfFile;
	}
	
	
	
	public void setRplaIdf ( RplaIdfFile file) {
		//checkFile(file);
		
		_rplaIdfFile = file ;
	}

	
	public void setSradfFile ( SradfFile sradffile) {
		//checkFile(file);
		_sradfFile = sradffile ;
	}

	
	
	

	
}
