//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

/**
 * @author jscott
 *
 */
interface MageTabImporter {

    MageTabDocumentSet validateFiles(Project targetProject,
            CaArrayFileSet fileSet);

    MageTabDocumentSet selectRefFiles(Project project, CaArrayFileSet idfFileSet);

    /**
     * 
     * @param targetProject
     * @param fileSet
     * @return MageTabDocumentSet if available, else null. 
     * @throws MageTabParsingException
     */
    MageTabDocumentSet importFiles(Project targetProject, CaArrayFileSet fileSet)
            throws MageTabParsingException;

}
