package gov.nih.nci.carpla.domain.rplarray;

import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.sample.Sample;

import java.util.HashSet;
import java.util.Set;

public class RplaReporter {

	private Set<FactorValue>	_factorValues	= new HashSet<FactorValue>();

	private Sample				_sample;
	// remember too that bidirectional from sample to RplaReporter is likely to
	// be needed

	private Set<RplaFeature>	_rplaFeatures	= new HashSet<RplaFeature>();

}
