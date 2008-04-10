package gov.nih.nci.carpla.domain.rplarray;

import java.util.HashSet;
import java.util.Set;

import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

public class RplArray {

	private String				_name;
	private ProtocolApplication	_creationProtocolApplication;

	private RplArrayGroup		_rplArrayGroup;

	private Person				_provider;

	private Set<RplaFeature>	_features	= new HashSet<RplaFeature>();
	private Set<RplaReporter>	_reporters	= new HashSet<RplaReporter>();

}
