//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.validation.InvalidDataException;

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
     * Parses the content of the documents contained in the MAGE-TAB file set to produce
     * an object model representation of the documents and the entities contained within
     * them.
     *
     * @param fileSet the documents to parse
     * @param reimportingMagetab true if parsing additional MAGE-TAB files
     * @return the parsed result.
     * @throws InvalidDataException if one or more of the MAGE-TAB documents are invalid.
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    MageTabDocumentSet parse(MageTabFileSet fileSet, boolean reimportingMagetab) throws InvalidDataException,
            MageTabParsingException;

}
