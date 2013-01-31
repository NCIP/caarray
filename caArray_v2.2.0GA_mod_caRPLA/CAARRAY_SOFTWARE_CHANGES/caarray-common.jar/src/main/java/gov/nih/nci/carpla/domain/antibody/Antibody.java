//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.domain.antibody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.validation.UniqueConstraint;
import gov.nih.nci.caarray.validation.UniqueConstraintField;
import gov.nih.nci.carpla.domain.rplahybridization.RplaHybridization;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class Antibody extends AbstractCaArrayEntity

{
	
	
	private Experiment _experiment = new Experiment();
	@ManyToOne
	@Cascade( { CascadeType.SAVE_UPDATE})
	public Experiment getExperiment () {
		return _experiment;
	}

	public void setExperiment ( Experiment experiment) {
		_experiment = experiment ;
	}
	
	//========================================================

	private Set<RplaHybridization>	_rplaHybridizations	= new HashSet<RplaHybridization>();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	public Set<RplaHybridization> getRplaHybridizations () {
		return _rplaHybridizations;
	}

	public void setRplaHybridizations ( Set<RplaHybridization> rplaHybridizations)
	{
		this._rplaHybridizations = rplaHybridizations;
	}

	
	// -----------------------------------------------------------
	private List<Gene>	_targetGenes	= new ArrayList<Gene>();

	@ManyToMany
	public List<Gene> getTargetGenes () {
		return _targetGenes;
	}

	public void setTargetGenes ( List<Gene> genes) {
		_targetGenes = genes;
	}

	// -----------------------------------------------------------
	private Organization	_provider;

	@ManyToOne
	public Organization getProvider () {
		return _provider;
	}

	public void setProvider ( Organization _provider) {
		this._provider = _provider;
	}

	// -----------------------------------------------------------
	private MeasurementCharacteristic	_targetProteinMolecularWeight;

	@OneToOne
	public MeasurementCharacteristic getTargetProteinMolecularWeight () {
		return _targetProteinMolecularWeight;
	}

	public void setTargetProteinMolecularWeight ( MeasurementCharacteristic proteinMolecularWeight)
	{
		_targetProteinMolecularWeight = proteinMolecularWeight;
	}

	// -----------------------------------------------------------
	private String	_name;

	@NotNull
	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getName () {
		return _name;
	}

	public void setName ( String name) {
		_name = name;

	}

	// -----------------------------------------------------------
	private String	_catalogId;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getCatalogId () {
		return _catalogId;
	}

	public void setCatalogId ( String id) {
		_catalogId = id;
	}

	// -----------------------------------------------------------
	private String	_comment;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getComment () {
		return _comment;
	}

	public void setComment ( String comment) {
		this._comment = comment;
	}

	// -----------------------------------------------------------
	private String	_epitope;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getEpitope () {
		return _epitope;
	}

	public void setEpitope ( String _epitope) {
		this._epitope = _epitope;
	}

	// -----------------------------------------------------------
	private String	_immunogen;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getImmunogen () {
		return _immunogen;
	}

	public void setImmunogen ( String _immunogen) {
		this._immunogen = _immunogen;
	}

	// -----------------------------------------------------------
	private String	_lotId;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getLotId () {
		return _lotId;
	}

	public void setLotId ( String id) {
		_lotId = id;
	}

	// -----------------------------------------------------------
	private String	_specificity;

	@Length(min = 1, max = DEFAULT_STRING_COLUMN_SIZE)
	public String getSpecificity () {
		return _specificity;
	}

	public void setSpecificity ( String _specificity) {
		this._specificity = _specificity;
	}

}
