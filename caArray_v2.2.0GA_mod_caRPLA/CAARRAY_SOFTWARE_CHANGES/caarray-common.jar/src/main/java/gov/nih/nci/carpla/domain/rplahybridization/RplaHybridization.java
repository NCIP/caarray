package gov.nih.nci.carpla.domain.rplahybridization;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.carpla.domain.rplarray.RplArray;
import gov.nih.nci.carpla.domain.antibody.Antibody;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;

@Entity
@Table(name = "rplahybridization")
public class RplaHybridization extends AbstractCaArrayEntity {

	
	

	private Date	_date;

	public Date getDate () {
		return _date;
	}

	public void setDate ( Date date) {
		_date = date;
	}

	// #################################################################
	private RplArray	_rplArray;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.SAVE_UPDATE)
	// Could eventually add DELETE cascade, but Arrays are shared...is this
	// relevant here?
	@ForeignKey(name = "rplahybridization_rplarray_fk")
	public RplArray getRplArray () {
		return _rplArray;

	}

	public void setRplArray ( RplArray rarray) {
		_rplArray = rarray;
	}

	// #################################################################

	private ProtocolApplication	_protocolApplication;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@ForeignKey(name = "rplahybridization_protocolapp_fk")
	public ProtocolApplication getProtocolApplication () {
		return this._protocolApplication;
	}

	public void setProtocolApplication ( ProtocolApplication protocolApplication)
	{
		_protocolApplication = protocolApplication;
	}

	// #################################################################
	private Antibody	_antibody;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@ForeignKey(name = "rplahybridization_antibody_fk")
	public Antibody getAntibody () {
		return _antibody;
	}

	public void setAntibody(Antibody antibody){
		_antibody = antibody ;
		
	}
	
	
	// #################################################################
	private String	_name;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return _name;
	}

	public void setName ( String name) {
		_name = name;

	}
	// #################################################################

}
