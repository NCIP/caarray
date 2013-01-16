//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.validation.InvalidDataFileException;


/**
 * Convenience abstract super class for ArrayDataService interface.
 * 
 */
public abstract class AbstractArrayDataService implements ArrayDataService {
    /**
     * Delegates to {@link ArrayDataService#importData(CaArrayFile, boolean, DataImportOptions, MageTabDocumentSet)} 
     * passing null for the MageTabDocumentSet param.
     * {@inheritDoc}

     */
    public final void importData(CaArrayFile file, boolean createAnnotation, DataImportOptions dataImportOptions)
            throws InvalidDataFileException {
        importData(file, createAnnotation, dataImportOptions, null);
    }
}
