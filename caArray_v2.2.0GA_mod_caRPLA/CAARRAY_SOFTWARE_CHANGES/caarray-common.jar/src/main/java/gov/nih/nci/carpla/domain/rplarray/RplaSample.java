package gov.nih.nci.carpla.domain.rplarray;

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
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial; 
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.Sample;


@Entity
public class RplaSample extends AbstractBioMaterial implements Comparable {

	// -------------------------------------------------------------------------
	private Source	_source;

	public Source getSource () {
		return _source;

	}

	public void setSource ( Source source) {
		_source = source;
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

	// "sub-sampling" is deferred

	// ---------------------------------------------------------------------------

	public int compareTo ( Object o) {
		RplaSample myClass = (RplaSample) o;
		return new CompareToBuilder()

		.append(getName(), myClass.getName())

		.toComparison();

	}

	@Override
	protected void doAddDirectPredecessor ( AbstractExperimentDesignNode predecessor)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doAddDirectSuccessor ( AbstractExperimentDesignNode successor)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<? extends AbstractExperimentDesignNode> getDirectPredecessors ()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<? extends AbstractExperimentDesignNode> getDirectSuccessors () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Experiment getExperiment () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExperimentDesignNodeType getNodeType () {
		// TODO Auto-generated method stub
		return null;
	}

}
