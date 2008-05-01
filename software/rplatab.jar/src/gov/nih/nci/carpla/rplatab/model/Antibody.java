package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

import java.util.Vector;
import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

public class Antibody implements ArrayDataSectionPrincipal, HasName {

	private String			_name;
	private Vector<Gene>	_targetGenes;
	private String			_specificity;
	private String			_epitope;
	private String			_immunogen;
	private Provider		_provider;

	private String			_catalogId;
	private String			_lotId;

	// support in future -Vector of Antibody Data Sheet Files

	public Antibody(String name) {
		_name = name;
	}

	public String getName () {
		return _name;
	}

	public void setName ( String name) {
		_name = name;

	}
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("Antibody (name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();
		
	}
	

}
