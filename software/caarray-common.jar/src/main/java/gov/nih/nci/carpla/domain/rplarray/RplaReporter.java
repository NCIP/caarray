package gov.nih.nci.carpla.domain.rplarray;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.carpla.domain.sample.RplaSample;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;


@Entity
public class RplaReporter extends AbstractCaArrayEntity {

	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();

	private RplaSample				_sample;
	

	private Set<RplaFeature>	_rplaFeatures	= new HashSet<RplaFeature>();
	
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public  Set<FactorValue> getFactorValues(){
		return _factorValues;
	}
	
	public void setFactorValues(Set<FactorValue> factorValues )
	{
		
		_factorValues = factorValues;
	}

	
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public RplaSample getRplaSample () {
		return _sample;
	}

	public void setRplaSample ( RplaSample _sample) {
		this._sample = _sample;
	}

	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Set<RplaFeature> getRplaFeatures () {
		return _rplaFeatures;
	}

	public void setRplaFeatures ( Set<RplaFeature> features) {
		_rplaFeatures = features;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
