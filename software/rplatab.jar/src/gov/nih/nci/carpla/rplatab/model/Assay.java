package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

public class Assay implements ArrayDataSectionPrincipal, HasName,HasAttribute {
	private String	_name;

	public String getName () {
		return _name;
	}

	public void setName ( String name) {
		// TODO Auto-generated method stub
		_name = name ; 
	}

	

}
