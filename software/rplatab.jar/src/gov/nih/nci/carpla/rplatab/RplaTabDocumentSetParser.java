package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;

public interface RplaTabDocumentSetParser {
	// public RplaTabDataset parse ( RplaIdfFile rplaIdfFile);

	RplaTabDocumentSetParser	INSTANCE	= new RplaTabDocumentSetParserImplementation();

	public RplaTabDocumentSet parse ( RplaTabInputFileSet rplaTabInputFileSet)
			throws RplaTabParsingException,InvalidDataException;

	
	//Do we really need this? What exactly should validate do besides call parse anyway? 
	public ValidationResult validate ( RplaTabInputFileSet inputSet)
			throws RplaTabParsingException;
	

}
