//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.common;

import gov.nih.nci.caarray.external.v1_0.data.FileCategory;
import gov.nih.nci.caarray.services.grid.util.EnumFieldHandler;

/**
 * @author dkokotov
 */
public class FileCategoryFieldHandler extends EnumFieldHandler<FileCategory>{
    public FileCategoryFieldHandler() {
        super (FileCategory.class);
    }
}
