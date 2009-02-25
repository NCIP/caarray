package gov.nih.nci.carpla.rplatab;

public class RplaTabParsingException extends Exception {
	private String	_message;

	public String getMessage () {
		return _message;
	}

	public RplaTabParsingException(String message) {
		_message = message;
	}

}
