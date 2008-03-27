package gov.nih.nci.carpla.rplatab.model;

import java.util.Vector;



public class Antibody implements ArrayDataSectionPrincipal,HasName{

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

}
