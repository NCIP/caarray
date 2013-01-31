//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.domain.rplarray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.Protectable;

@Entity
@DiscriminatorValue("RSA")
public class RplaSample extends AbstractBioMaterial
													implements
													Comparable,
													Protectable {
	// -------------------------------------------------------------------------

	private Experiment	_experiment;

	@ManyToOne
	//@JoinTable(name = "experiment_rplasample", joinColumns = { @JoinColumn(name = "rplasample_id", insertable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "experiment_id", insertable = false, updatable = false) })
	public Experiment getExperiment () {
		return this._experiment;
	}

	public void setExperiment ( Experiment experiment) {
		this._experiment = experiment;
	}

	// -------------------------------------------------------------------------

	public static class ByNameComparator implements Comparator<RplaSample> {
		/**
		 * Compares samples by name. {@inheritDoc}
		 */
		public int compare ( RplaSample s1, RplaSample s2) {
			return new CompareToBuilder()	.append(s1.getName(), s2.getName())
											.toComparison();
		}
	}

	@Override
	@Transient
	public ExperimentDesignNodeType getNodeType () {
		return ExperimentDesignNodeType.RPLASAMPLE;
	}

	// -------------------------------------------------------------------------
	private Source	_source;

	@ManyToOne
	public Source getSource () {
		return _source;

	}

	public void setSource ( Source source) {
		_source = source;
	}

	
	
	// -------------------------------------------------------------------------
	private RplArray _rplArray ;
	
	
	@ManyToOne
	public RplArray getRplArray(){
		System.out.println("in RplaSample getRplArray()");
		return _rplArray ;
	}
	
	public void setRplArray(RplArray rplArray){
		_rplArray = rplArray ;
	}
	
	
	
	
	// -------------------------------------------------------------------------
	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<FactorValue> getFactorValues () {
		return _factorValues;
	}

	public void setFactorValues ( Set<FactorValue> factorValues) {

		_factorValues = factorValues;
	}

	// -------------------------------------------------------------------------
	private Set<RplaFeature>	_rplaFeatures	= new HashSet<RplaFeature>();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaFeature> getRplaFeatures () {
		return _rplaFeatures;
	}

	public void setRplaFeatures ( Set<RplaFeature> features) {
		_rplaFeatures = features;
	}

	

	public int compareTo ( Object o) {
		RplaSample myClass = (RplaSample) o;
		return new CompareToBuilder()

		.append(getName(), myClass.getName())

		.toComparison();

	}

	
	//AbstractBioMaterial extends AbstractExperimentDesignNode
	//and the RplaSample <->RplaSample relationship has been deferred
	@Override
	@Transient
	protected void doAddDirectPredecessor ( AbstractExperimentDesignNode predecessor)
	{
		Source src = (Source)predecessor;
		setSource(src);

	}

	@Override
	@Transient
	protected void doAddDirectSuccessor ( AbstractExperimentDesignNode successor)
	{
		throw new IllegalArgumentException("Should never be called");

	}

	@Override
	@Transient
	public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors ()
	{

		HashSet hs = new HashSet();
		hs.add(getSource());
		return hs;
		
	}

	@Override
	@Transient
	public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors () {
		return Collections.emptySet();
		
		
	}

}
