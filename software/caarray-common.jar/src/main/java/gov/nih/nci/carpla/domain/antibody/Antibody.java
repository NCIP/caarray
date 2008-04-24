package gov.nih.nci.carpla.domain.antibody;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;

@Entity
@Table(name = "antibody")
@UniqueConstraint(fields = @UniqueConstraintField(name = "lotId"))
public class Antibody extends AbstractCaArrayEntity

{

	// private List<AntibodyValidation> _antibodyValidations = new
	// ArrayList<AntibodyValidation>();
	private String						_catalogId;
	private String						_comment;
	private String						_epitope;
	private String						_immunogen;
	private String						_lotId;
	private String						_name;
	private Organization				_provider;
	private String						_specificity;
	private List<Gene>					_targetGenes	= new ArrayList<Gene>();
	// support in future -Vector of Antibody Data Sheet Files

	private MeasurementCharacteristic	_targetProteinMolecularWeight;

	public void setName ( String name) {
		_name = name;

	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return _name;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getCatalogId () {
		return _catalogId;
	}

	public void setCatalogId ( String id) {
		_catalogId = id;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getComment () {
		return _comment;
	}

	public void setComment ( String _comment) {
		this._comment = _comment;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getEpitope () {
		return _epitope;
	}

	public void setEpitope ( String _epitope) {
		this._epitope = _epitope;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getImmunogen () {
		return _immunogen;
	}

	public void setImmunogen ( String _immunogen) {
		this._immunogen = _immunogen;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getLotId () {
		return _lotId;
	}

	public void setLotId ( String id) {
		_lotId = id;
	}

	public Organization getProvider () {
		return _provider;
	}

	public void setProvider ( Organization _provider) {
		this._provider = _provider;
	}

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getSpecificity () {
		return _specificity;
	}

	public void setSpecificity ( String _specificity) {
		this._specificity = _specificity;
	}

	@ManyToMany
	public List<Gene> getTargetGenes () {
		return _targetGenes;
	}

	public void setTargetGenes ( List<Gene> genes) {
		_targetGenes = genes;
	}

	public MeasurementCharacteristic getTargetProteinMolecularWeight () {
		return _targetProteinMolecularWeight;
	}

	public void setTargetProteinMolecularWeight ( MeasurementCharacteristic proteinMolecularWeight)
	{
		_targetProteinMolecularWeight = proteinMolecularWeight;
	}

}
