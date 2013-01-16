//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

/**
 * Simple stub for array data service.
 */
public class ArrayDataServiceStub extends AbstractArrayDataService {
    @Override
    public void importData(CaArrayFile caArrayFile, boolean createAnnotation, DataImportOptions importOptions, 
            MageTabDocumentSet mTabSet)
            throws InvalidDataFileException {
        // no-op
    }

    public ArrayDesign getArrayDesign(CaArrayFile file) {
        return null;
    }

    @Override
    public void initialize() {
        // no-op
    }

    @Override
    public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet, boolean reimport) {
        return new FileValidationResult();
    }
}
