package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;

public interface RplaTabDocumentSetParser {

	RplaTabDocumentSetParser	INSTANCE	= new RplaTabDocumentSetParserImplementation();

	public RplaTabDocumentSet parse ( RplaTabInputFileSet rplaTabInputFileSet);

	public ValidationResult validate ( RplaTabInputFileSet inputSet);

}
