package gov.nih.nci.carpla.domain.antibody;

import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;

public class Antibody

{

	private List<AntibodyValidation>	_antibodyValidations = new ArrayList<AntibodyValidation>();
	private String						_catalogId;
	private String						_comment;
	private String						_epitope;
	private String						_immunogen;
	private String						_lotId;
	private String						_name;
	private Organization				_provider;
	private String						_specificity;
	private List<Gene>					_targetGenes = new ArrayList<Gene>();
	
	
	private MeasurementCharacteristic _targetProteinMolecularWeight ;

	// support in future -Vector of Antibody Data Sheet Files

}
