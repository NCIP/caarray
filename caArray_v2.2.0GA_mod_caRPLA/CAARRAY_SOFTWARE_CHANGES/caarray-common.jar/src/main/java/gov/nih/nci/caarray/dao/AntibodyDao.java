//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.dao;


import gov.nih.nci.carpla.domain.antibody.Antibody;

import java.util.List;

public interface AntibodyDao {
	

	
	
	Antibody getAntibody(long id);

	List<Antibody> getAntibodies ();
		
	//carpla TODO eventually get by LSID

	
	
	
	

}
