//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
     * Parse the content of the documents contained in the MAGE-TAB file set to produce
     * an object model representation of the documents and the entities contained within
     * them.
     *
     * @param fileSet the documents to parse
     * @return the parsed result.
     * @throws InvalidDataException if one or more of the MAGE-TAB documents are invalid.
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    MageTabDocumentSet parse(MageTabFileSet fileSet) throws InvalidDataException,
            MageTabParsingException;

    /**
     * Parse the content of the IDF doc to find the SDRF documents contained in the MAGE-TAB file set to produce
     * an object model representation of the SDRF documents and the entities referenced by
     * them.
     *
     * @param sdrfFileSet the SDRF documents to parse
     * @return the parsed result.
     * @throws InvalidDataException if one or more of the MAGE-TAB documents are invalid.
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    MageTabDocumentSet parseDataFileNames(MageTabFileSet sdrfFileSet) throws InvalidDataException,
            MageTabParsingException;

}
