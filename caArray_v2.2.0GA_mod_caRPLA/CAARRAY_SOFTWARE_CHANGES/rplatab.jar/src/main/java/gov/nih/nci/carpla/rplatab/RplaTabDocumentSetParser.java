//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.carpla.rplatab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;

public interface RplaTabDocumentSetParser {

	RplaTabDocumentSetParser	INSTANCE	= new RplaTabDocumentSetParserImplementation();

	public RplaTabDocumentSet parse ( RplaTabInputFileSet rplaTabInputFileSet)
																				throws InvalidDataException;

	public ValidationResult validate ( RplaTabInputFileSet inputSet);

}
