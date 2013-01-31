//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;



public interface RplaTabTranslator {

    String JNDI_NAME = "caarray/RplaTabTranslatorBean/local";

    
    RplaTabTranslationResult translate(RplaTabDocumentSet documentSet, CaArrayFileSet fileSet);

   
    ValidationResult validate(RplaTabDocumentSet documentSet, CaArrayFileSet fileSet);

}
