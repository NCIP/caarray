package gov.nih.nci.caarray.dao;


import gov.nih.nci.carpla.domain.antibody.Antibody;

import java.util.List;

public interface AntibodyDao {
	

	
	
	Antibody getAntibody(long id);

	List<Antibody> getAntibodies ();
		
	//carpla TODO eventually get by LSID

	
	
	
	

}
