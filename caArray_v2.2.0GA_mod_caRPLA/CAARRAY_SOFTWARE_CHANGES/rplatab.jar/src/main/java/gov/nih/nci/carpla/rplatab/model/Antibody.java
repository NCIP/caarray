//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
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
	private String		_provider;
	private String _comment;
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

	public Vector<Gene> get_targetGenes () {
		return _targetGenes;
	}

	public void setTargetGenes ( Vector<Gene> genes) {
		_targetGenes = genes;
	}

	public String getSpecificity () {
		return _specificity;
	}

	public void setSpecificity ( String _specificity) {
		this._specificity = _specificity;
	}

	public String getEpitope () {
		return _epitope;
	}

	public void setEpitope ( String _epitope) {
		this._epitope = _epitope;
	}

	public String getImmunogen () {
		return _immunogen;
	}

	public void setImmunogen ( String _immunogen) {
		this._immunogen = _immunogen;
	}

	public String getProvider () {
		return _provider;
	}

	public void setProvider ( String _provider) {
		this._provider = _provider;
	}

	public String getCatalogId () {
		return _catalogId;
	}

	public void setCatalogId ( String id) {
		_catalogId = id;
	}

	public String getLotId () {
		return _lotId;
	}

	public void setLotId ( String id) {
		_lotId = id;
	}

	public String getComment () {
		return _comment;
	}

	public void setComment ( String comment) {
		this._comment = comment;
	}
	

}
