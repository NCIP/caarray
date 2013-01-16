//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;

/**
 * @author jscott
 *
 */
interface ArrayDataImporter {

    void validateFiles(CaArrayFileSet fileSet, MageTabDocumentSet mTabSet, boolean reimport);
    void importFiles(CaArrayFileSet fileSet, DataImportOptions dataImportOptions, MageTabDocumentSet mTabSet);

}
