//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.ValidationResult;

/**
 * Interface to component used to translate MAGE-TAB files to the caArray domain model.
 */
public interface MageTabTranslator {

    /**
     * The default JNDI name to use to lookup <code>MageTabTranslator</code>.
     */
    String JNDI_NAME = "caarray/MageTabTranslatorBean/local";

    /**
     * Returns a caArray object graph equivalent to the MAGE-TAB object graph. Existing objects
     * in the caArray system will be used where appropriate (e.g. existing <code>Terms</code>, etc.)
     *
     * @param documentSet the MAGE-TAB document set to translate.
     * @param fileSet the CaArray files to associate.
     * @return the corresponding caArray objects.
     */
    CaArrayTranslationResult translate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet);

    /**
     * validates translation requirements.
     * @param documentSet the set of docs to validate
     * @param fileSet the set of files
     * @return the validation results.
     */
    ValidationResult validate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet);

}
