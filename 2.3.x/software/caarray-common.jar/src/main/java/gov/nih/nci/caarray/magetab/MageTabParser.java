//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;

/**
 * Interface to MAGE-TAB validation and parsing functionality.
 *
 * @author tavelae
 */
public interface MageTabParser {

    /**
     * Instance of the MAGE-TAB parsing subsystem.
     */
    MageTabParser INSTANCE = new MageTabParserImplementation();

    /**
     * Validates the documents contained in the MAGE-TAB file set.
     *
     * @param fileSet the documents to validate
     * @return the validation result
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    ValidationResult validate(MageTabFileSet fileSet) throws MageTabParsingException;

    /**
     * Parses the content of the documents contained in the MAGE-TAB file set to produce
     * an object model representation of the documents and the entities contained within
     * them.
     *
     * @param fileSet the documents to parse
     * @return the parsed result.
     * @throws InvalidDataException if one or more of the MAGE-TAB documents are invalid.
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    MageTabDocumentSet parse(MageTabFileSet fileSet) throws InvalidDataException, MageTabParsingException;

}
