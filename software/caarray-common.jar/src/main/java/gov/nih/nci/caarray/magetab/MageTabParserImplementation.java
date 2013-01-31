//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.InvalidDataException;

import org.apache.log4j.Logger;

/**
 * Implementation entry point for the MAGE-TAB parsing subsystem.
 */
class MageTabParserImplementation implements MageTabParser {

    private static final Logger LOG = Logger.getLogger(MageTabParserImplementation.class);

    /**
     * {@inheritDoc}
     */
    public MageTabDocumentSet parse(MageTabFileSet inputFileSet, boolean reimportingMagetab)
            throws MageTabParsingException, InvalidDataException {
        LogUtil.logSubsystemEntry(LOG, inputFileSet);
        MageTabDocumentSet documentSet = new MageTabDocumentSet(inputFileSet, reimportingMagetab);
        documentSet.parse();
        if (!documentSet.getValidationResult().isValid()) {
            throw new InvalidDataException(documentSet.getValidationResult());
        }
        LogUtil.logSubsystemExit(LOG);
        return documentSet;
    }
}
