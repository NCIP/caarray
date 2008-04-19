package gov.nih.nci.carpla.domain.rplarray;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Entity;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;





@Entity
@BatchSize(size = AbstractCaArrayObject.DEFAULT_BATCH_SIZE)
public class RplArray extends AbstractCaArrayEntity {
	private String				_name;

	private ProtocolApplication	_creationProtocolApplication;

	private RplArrayGroup		_rplArrayGroup;

	private Person				_provider;

	private Set<RplaFeature>	_features	= new HashSet<RplaFeature>();
	private Set<RplaReporter>	_reporters	= new HashSet<RplaReporter>();

	public void setName ( String name) {
		_name = name;

	}

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	@NotNull
	@AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
	public String getName () {
		return _name;

	}

}
