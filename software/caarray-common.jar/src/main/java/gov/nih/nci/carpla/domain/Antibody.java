package gov.nih.nci.carpla.domain;

import java.util.List;

import gov.nih.nci.caarray.domain.array.Gene;
import gov.nih.nci.caarray.domain.contact.Organization;

public class Antibody

{

	private List<AntibodyValidation>	_antibodyValidations;
	private String						_catalogId;
	private String						_comment;
	private String						_epitope;
	private String						_immunogen;
	private String						_lotId;
	private String						_name;
	private Organization				_provider;
	private String						_specificity;
	private List<Gene>					_targetGenes;

	// support in future -Vector of Antibody Data Sheet Files

}
