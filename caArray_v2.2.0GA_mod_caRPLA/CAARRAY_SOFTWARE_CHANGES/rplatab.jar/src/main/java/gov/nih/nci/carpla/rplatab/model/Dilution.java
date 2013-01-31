//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;
import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;
public class Dilution 
implements HasName,ArraySectionPrincipal {

	private String _name;
	
	public String getName () {
		return _name ;
	}

	public void setName ( String dilutionName) {
		_name = dilutionName ;
		
	}
	
	
	private Float _value;
    private String _unit;

	public Float getValue () {
		return _value;
	}

	public void setValue ( Float value) {
		this._value = value;
	}

	public String getUnit () {
		return _unit;
	}

	public void setUnit ( String unit) {
		this._unit = unit;
	}
	
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("Dilution(name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();
		
	}
	

	

}
