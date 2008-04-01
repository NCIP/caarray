package gov.nih.nci.carpla.domain;

import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RPLAHybridization {

	private Date				_date;
	private String				_name;
	private Antibody			_antibody;
	private ProtocolApplication	_protocolApplication;
	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();
	private RPLArray			_rplArray;

}
