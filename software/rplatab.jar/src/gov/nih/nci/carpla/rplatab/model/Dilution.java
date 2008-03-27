package gov.nih.nci.carpla.rplatab.model;

public class Dilution implements HasName,ArraySectionPrincipal{

	private String _name;
	
	public String getName () {
		return _name ;
	}

	public void setName ( String dilutionName) {
		_name = dilutionName ;
		
	}

}
