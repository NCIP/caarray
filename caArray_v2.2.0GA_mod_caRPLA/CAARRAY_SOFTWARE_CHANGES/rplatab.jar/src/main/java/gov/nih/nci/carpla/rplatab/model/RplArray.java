//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.carpla.rplatab.sradf.HEADERTYPE;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

//import gov.nih.nci.carpla.modelextensions.Interface.HasRPLArrayGroup;
//
//import org.biomage.Array.*;
//import org.biomage.ArrayDesign.ArrayDesign;
//import org.biomage.Interface.HasArrayDesign;
//import org.biomage.Interface.HasArrayManufactureDeviations;
//import org.biomage.Interface.HasInformation;
import org.xml.sax.Attributes;

public class RplArray implements HasName, Serializable, ArraySectionPrincipal

// need serial id
{

	public RplArray() {
		super();

	}

	RplArrayGroup											arrayGroup;

	private String											_name;

	private SortedMap<String, ArrayList<RplArrayFeature>>	_features_by_samplename	= new TreeMap<String, ArrayList<RplArrayFeature>>();

	/**
	 * An identifying string, e.g. a barcode.
	 * 
	 */
	String													arrayIdentifier;

	public void setName ( String arrayName) {

		_name = arrayName;
	}

	public String getName () {
		return _name;
	}
	public String toString(){
		StringBuffer ret = new StringBuffer();
		ret.append("RplArray (name=");
		ret.append(getName());
		ret.append(")");
		return ret.toString();
		
	}
	

}
