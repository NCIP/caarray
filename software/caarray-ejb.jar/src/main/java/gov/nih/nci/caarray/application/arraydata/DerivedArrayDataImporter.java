//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

/**
 * Provides specialized behavior for importing <code>DerivedArrayData</code>.
 */
final class DerivedArrayDataImporter extends AbstractDataSetImporter<DerivedArrayData> {
    DerivedArrayDataImporter(CaArrayFile caArrayFile, CaArrayDaoFactory daoFactory, 
            DataImportOptions dataImportOptions) {
        super(caArrayFile, daoFactory, DerivedArrayData.class, dataImportOptions);
    }

    @Override
    void addHybridizationDatas() {
        for (Hybridization hybridization : getArrayData().getHybridizations()) {
            getDataSet().addHybridizationData(hybridization);
        }
    }

    @Override
    void associateToHybridization(Hybridization hyb) {
        DerivedArrayData derivedArrayData = getArrayData();
        derivedArrayData.getHybridizations().add(hyb);
        hyb.getDerivedDataCollection().add(derivedArrayData);        
    }

    @Override
    void lookupArrayData() {
        setArrayData(getArrayDao().getDerivedArrayData(getCaArrayFile()));
    }


}
