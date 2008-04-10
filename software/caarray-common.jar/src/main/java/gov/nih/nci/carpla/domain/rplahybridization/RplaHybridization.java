package gov.nih.nci.carpla.domain.rplahybridization;

import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.carpla.domain.rplarray.RplArray;
import gov.nih.nci.carpla.domain.antibody.Antibody;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RplaHybridization {

	private Date				_date;
	private String				_name;
	private Antibody			_antibody;
	private ProtocolApplication	_protocolApplication;
	
	//not sure if there is any reason to have this here, except possibly to explicitly call out some factor value(s)
	// that relate to something other than the samples on the array
	//such as a protocol parameter in array creation, or in array hybridization
	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();
	
	
	
	private RplArray			_rplArray;

}
