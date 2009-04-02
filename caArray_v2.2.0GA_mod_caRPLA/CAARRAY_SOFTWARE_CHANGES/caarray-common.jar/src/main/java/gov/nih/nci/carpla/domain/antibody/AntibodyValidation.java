package gov.nih.nci.carpla.domain.antibody;

import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.Term;

public class AntibodyValidation {

	// -------------------------------------------------
	private String	_validationId;

	public String getValidationId () {

		return _validationId;
	}

	public void setValidationId ( String validationId) {
		_validationId = validationId;
	}

	// -------------------------------------------------
	private Sample	_sampleUsed;

	public Sample getSample () {

		return _sampleUsed;
	}

	public void setSample ( Sample sampleUsed) {
		_sampleUsed = sampleUsed;
	}

	// -------------------------------------------------
	private Person	_validator;

	public Person getValidator () {

		return _validator;
	}

	public void setValidator ( Person validator) {
		_validator = validator;
	}

	// -------------------------------------------------
	private Image	_image;

	public Image getImage () {
		return _image;

	}

	public void setImage ( Image image) {
		_image = image;
	}

	// -------------------------------------------------
	private Term	_validationResult;

	public Term getValidationResult () {

		return _validationResult;
	}

	public void setValidationResult ( Term validationResult) {
		_validationResult = validationResult;
	}

	// -------------------------------------------------
	private Term	_validationMethod;

	public Term getValidationMethod () {

		return _validationMethod;
	}

	public void setValidationMethod ( Term validationMethod) {
		_validationMethod = validationMethod;
	}

	// -------------------------------------------------
	private MeasurementCharacteristic	_dilution;

	public MeasurementCharacteristic getDilution () {

		return _dilution;
	}

	public void setDilution ( MeasurementCharacteristic dilution) {
		_dilution = dilution;
	}

	// -------------------------------------------------
	private String	_comment;

	public String getComment () {

		return _comment;
	}

	public void setComment ( String comment) {
		_comment = comment;
	}

}
