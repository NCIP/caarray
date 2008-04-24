package gov.nih.nci.carpla.domain.rplarray;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	private ProtocolApplication	_creationProtocolApplication;
	private RplArrayGroup		_rplArrayGroup;
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

}
