//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab.model;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


//carplanotes
//I would have liked to used AbstractBioMaterial from magetab.sdrf., but it extends 
// the AbstractSampleDataRelationshipNode class which seems in bed with SdrfDocument 
//I just copied the code over...



public abstract class AbstractBioMaterial

implements HasName, HasCharacteristics {

	private static final long			serialVersionUID	= 690334748116662920L;

	private final List<Characteristic>	characteristics		= new ArrayList<Characteristic>();
	private OntologyTerm				materialType;
	private String						description;

	/**
	 * @return the characteristics
	 */
	public List<Characteristic> getCharacteristics () { 
		return characteristics;
	}

	/**
	 * @return the description
	 */
	public String getDescription () {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription ( String description) {
		this.description = description;
	}

	/**
	 * @return the materialType
	 */
	public OntologyTerm getMaterialType () {
		return materialType;
	}

	/**
	 * @param materialType
	 *            the materialType to set
	 */
	public void setMaterialType ( OntologyTerm materialType) {
		this.materialType = materialType;
	}

//	public  getProtocolApplications () {
//		
//		return null;
//	}

}
