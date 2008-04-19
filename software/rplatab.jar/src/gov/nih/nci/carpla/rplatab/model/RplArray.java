package gov.nih.nci.carpla.rplatab.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

//import gov.nih.nci.carpla.modelextensions.Interface.HasRPLArrayGroup;
//
//import org.biomage.Array.*;
//import org.biomage.ArrayDesign.ArrayDesign;
//import org.biomage.Interface.HasArrayDesign;
//import org.biomage.Interface.HasArrayManufactureDeviations;
//import org.biomage.Interface.HasInformation;
import org.xml.sax.Attributes;

public class RplArray  implements HasName,Serializable,ArraySectionPrincipal
		
// need serial id
		{

	public RplArray() {
		super();
		
	}

	
	RplArrayGroup arrayGroup;
	
	
	private String _name ;
	
	
	
	
	/**
	 * An identifying string, e.g. a barcode.
	 * 
	 */
	String arrayIdentifier;




	public void setName ( String arrayName) {
		
		_name = arrayName ;
	}




	public String getName () {
	return _name ;
	}
	
	
	
	
	
	
	
	
	
	
	

	
}
