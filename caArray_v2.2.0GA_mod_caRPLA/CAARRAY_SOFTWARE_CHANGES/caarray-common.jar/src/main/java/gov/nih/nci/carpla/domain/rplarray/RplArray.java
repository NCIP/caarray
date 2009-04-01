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

	
	
	
	
	
	// --------------------------------------------------------
	private String	_name;

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return this._name;
	}

	public void setName ( String name) {
		_name = name;
	}

	// --------------------------------------------------------
	private String	_comment;

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getComment () {
		return this._comment;
	}

	public void setComment ( String comment) {
		_comment = comment;
	}
	
	
	// --------------------------------------------------------
	private String	_barcode;

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getBarcode () {
		return this._barcode;
	}

	public void setBarcode ( String barcode) {
		_barcode = barcode;
	}
	
	
	
	
	// --------------------------------------------------------
	private RplaHybridization	_rplaHybridization;

	public RplaHybridization getRplaHybridization () {
		return _rplaHybridization;
	}

	public void setRplaHybridization ( RplaHybridization rplaHybridization) {
		_rplaHybridization = rplaHybridization;
	}
	
	
	
	
	
	// --------------------------------------------------------
	private ProtocolApplication	_creationProtocolApplication;

	public ProtocolApplication getCreationProtocolApplication () {
		return _creationProtocolApplication;
	}

	public void setCreationProtocolApplication ( ProtocolApplication protocolApplication)
	{
		this._creationProtocolApplication = protocolApplication;
	}

	// --------------------------------------------------------
	private RplArrayGroup	_rplArrayGroup;

	public RplArrayGroup getRplArrayGroup () {
		return _rplArrayGroup;
	}

	public void setRplArrayGroup ( RplArrayGroup arrayGroup) {
		_rplArrayGroup = arrayGroup;
	}

	// --------------------------------------------------------

	private Organization	_providerOrganization;

	public Organization getProvider () {
		return _providerOrganization;
	}

	public void setProvider ( Organization provider) {
		this._providerOrganization = provider;
	}

	// --------------------------------------------------------

	private Set<RplaFeature>	_features	= new HashSet<RplaFeature>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaFeature> getRplaFeatures () {
		return _features;
	}

	public void setRplaFeatures ( Set<RplaFeature> _features) {
		this._features = _features;
	}

	
	// --------------------------------------------------------

	private RplaHybridization _rplaHybridization ;
	
	public RplaHybridization getRplaHybridization
	
	
	
	
	
	
	
	
	
}
