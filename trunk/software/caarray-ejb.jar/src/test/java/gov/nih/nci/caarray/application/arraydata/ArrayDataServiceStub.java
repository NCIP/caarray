//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple stub for array data service.
 */
public class ArrayDataServiceStub implements ArrayDataService {

    public DataSet getData(AbstractArrayData arrayData) {
        List<QuantitationType> types = new ArrayList<QuantitationType>();
        return getData(arrayData, types);
    }

    public DataSet getData(AbstractArrayData arrayData, List<QuantitationType> types) {
        DataSet dataSet = new DataSet();
        dataSet.getQuantitationTypes().addAll(types);
        return dataSet;
    }

    public void importData(CaArrayFile caArrayFile, boolean createAnnotation, DataImportOptions importOptions)
            throws InvalidDataFileException {
        // no-op
    }
    
    public ArrayDesign getArrayDesign(CaArrayFile file) {
        return null;
    }

    public void initialize() {
        // no-op
    }

    public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet) {
        return new FileValidationResult(new File(arrayDataFile.getName()));
    }

}
