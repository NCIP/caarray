//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.ValidationResult;


public class MageTabTranslatorStub implements MageTabTranslator {

    public CaArrayTranslationResult translate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet) {
        return new MageTabTranslationResult();
    }

    /**
     * {@inheritDoc}
     */
    public ValidationResult validate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet) {
        return new ValidationResult();
    }
}
