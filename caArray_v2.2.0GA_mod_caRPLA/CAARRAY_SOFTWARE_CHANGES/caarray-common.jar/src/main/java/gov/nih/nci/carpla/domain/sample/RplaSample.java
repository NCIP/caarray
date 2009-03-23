package gov.nih.nci.carpla.domain.sample;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.CompareToBuilder;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.carpla.domain.rplarray.RplaReporter;

@Entity
public class RplaSample extends Sample implements Comparable {

	// ##############################################
	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();

	private Set<RplaFeature>	_rplaFeatures	= new HashSet<RplaFeature>();
	// ##############################################
	
	
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<FactorValue> getFactorValues () {
		return _factorValues;
	}

	public void setFactorValues ( Set<FactorValue> factorValues) {

		_factorValues = factorValues;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaFeature> getRplaFeatures () {
		return _rplaFeatures;
	}

	public void setRplaFeatures ( Set<RplaFeature> features) {
		_rplaFeatures = features;
	}

	// "sub-sampling" is deferred

	//
	// private Set<RplaSample> _descendantRplaSamples = new
	// HashSet<RplaSample>();
	//
	// @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// public Set<RplaSample> getDescendantRplaSamples () {
	//
	// return _descendantRplaSamples;
	// }
	//
	// public void setDescendantRplaSamples ( Set<RplaSample> samples) {
	// _descendantRplaSamples = samples;
	// }
	//
	// // ##############################################
	//
	// private RplaSample _sourceRplaSample;
	//
	//	
	// @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	// public RplaSample getSourceRplaSample () {
	// return _sourceRplaSample;
	// }
	//
	// public void setSourceRplaSample ( RplaSample rplaSample) {
	// _sourceRplaSample = rplaSample;
	//
	// }

	public int compareTo ( Object o) {
		RplaSample myClass = (RplaSample) o;
		return new CompareToBuilder()

		.append(getName(), myClass.getName())

		.toComparison();

	}

}
