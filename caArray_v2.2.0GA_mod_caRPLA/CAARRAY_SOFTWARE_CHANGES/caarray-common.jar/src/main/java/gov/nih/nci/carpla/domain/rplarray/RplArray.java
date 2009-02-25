package gov.nih.nci.carpla.domain.rplarray;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.security.AttributePolicy;
import gov.nih.nci.caarray.security.SecurityPolicy;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;

@Entity
@Table(name = "rplarray")
public class RplArray extends AbstractCaArrayEntity {

	private String				_name;

	// private ProtocolApplication _creationProtocolApplication;
	//private RplArrayGroup		_rplArrayGroup;
	private Person				_provider;
	private Set<RplaFeature>	_features	= new HashSet<RplaFeature>();
	private Set<RplaReporter>	_reporters	= new HashSet<RplaReporter>();

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return this._name;
	}

	public void setName ( String name) {
		_name = name;
	}

	// public ProtocolApplication getCreationProtocolApplication () {
	// return _creationProtocolApplication;
	// }

	// public void setCreationProtocolApplication ( ProtocolApplication
	// protocolApplication)
	// {
	// _creationProtocolApplication = protocolApplication;
	// }

	//public RplArrayGroup getRplArrayGroup () {
		//return _rplArrayGroup;
	//}

	//public void setRplArrayGroup ( RplArrayGroup arrayGroup) {
		//_rplArrayGroup = arrayGroup;
	//}
	
	public Person getProvider () {
		return _provider;
	}

	public void setProvider ( Person _provider) {
		this._provider = _provider;
	}
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaFeature> getRplaFeatures () {
		return _features;
	}

	public void setRplaFeatures ( Set<RplaFeature> _features) {
		this._features = _features;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaReporter> getRplaReporters () {
		return _reporters;
	}

	public void setRplaReporters ( Set<RplaReporter> _reporters) {
		this._reporters = _reporters;
	}

}
