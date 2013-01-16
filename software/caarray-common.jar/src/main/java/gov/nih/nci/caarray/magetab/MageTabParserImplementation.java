//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.validator.ValidatorSet;
import gov.nih.nci.caarray.magetab.validator.caarray.SdrfDuplicateNodeValidator;
import gov.nih.nci.caarray.magetab.validator.caarray.SdrfMinimumColumnsValidator;
import gov.nih.nci.caarray.magetab.validator.v1_1.SdrfNodeColumnValidator;
import gov.nih.nci.caarray.magetab.validator.v1_1.SdrfTermSourceColumnValidator;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.InvalidDataException;

import org.apache.log4j.Logger;

/**
 * Implementation entry point for the MAGE-TAB parsing subsystem.
 */
public class MageTabParserImplementation implements MageTabParser {
    private static final Logger LOG = Logger.getLogger(MageTabParserImplementation.class);
    
    /**
     * The ValidatorSet for the standard caArray set of MAGE-TAB validations.
     */
    public static final ValidatorSet CAARRAY_VALIDATION_SET = new ValidatorSet();
    static {
        CAARRAY_VALIDATION_SET.getSdrfColumnValidators().add(new SdrfNodeColumnValidator());
        CAARRAY_VALIDATION_SET.getSdrfColumnValidators().add(new SdrfTermSourceColumnValidator());
        CAARRAY_VALIDATION_SET.getSdrfColumnValidators().add(new SdrfMinimumColumnsValidator());
        CAARRAY_VALIDATION_SET.getSdrfColumnValidators().add(new SdrfDuplicateNodeValidator());
    }

    /**
     * {@inheritDoc}
     */
    public MageTabDocumentSet parse(MageTabFileSet inputFileSet) throws MageTabParsingException, InvalidDataException {
        LogUtil.logSubsystemEntry(LOG, inputFileSet);
        MageTabDocumentSet documentSet = new MageTabDocumentSet(inputFileSet, CAARRAY_VALIDATION_SET);
        documentSet.parse();
        if (!documentSet.getValidationResult().isValid()) {
            throw new InvalidDataException(documentSet.getValidationResult());
        }
        LogUtil.logSubsystemExit(LOG);
        return documentSet;
    }

    /**
     * {@inheritDoc}
     */
    public MageTabDocumentSet parseDataFileNames(MageTabFileSet idfFileSet) throws InvalidDataException,
            MageTabParsingException {
         LogUtil.logSubsystemEntry(LOG, idfFileSet);
         MageTabDocumentSet documentSet = new MageTabDocumentSet(idfFileSet);
         documentSet.parseNoValidation();
         LogUtil.logSubsystemExit(LOG);
         return documentSet;
    }
}
