//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external;

import gov.nih.nci.caarray.domain.file.FileType;

/**
 * FileType converter.
 * 
 * @author dkokotov
 */
public class FileTypeConverter
        extends AbstractEnumConverter<FileType, gov.nih.nci.caarray.external.v1_0.data.FileType> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void copyPropertiesToB(FileType a, gov.nih.nci.caarray.external.v1_0.data.FileType b) {
        b.setName(a.getName());
    }
}
