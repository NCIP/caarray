package gov.nih.nci.carpla.domain.antibody;

import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.Term;

public abstract class AntibodyValidation {

	private String						_validationId;
	private Sample						_sampleUsed;
	private Person						_validationPerformer;
	private Image						_image;
	private Term						_result;
	private Term						_validationType;
	private Term						_bandResult;
	private MeasurementCharacteristic	_dilution;
	private String						_comment;

}
