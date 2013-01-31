//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;

/**
 * Provides specialized behavior for importing <code>RawArrayData</code>.
 */
class RawArrayDataImporter extends AbstractDataSetImporter<RawArrayData> {
    RawArrayDataImporter(CaArrayFile caArrayFile, CaArrayDaoFactory daoFactory, 
            DataImportOptions dataImportOptions) {
        super(caArrayFile, daoFactory, RawArrayData.class, dataImportOptions);
    }

    @Override
    void addHybridizationDatas() {
        for (Hybridization hybridization : getArrayData().getHybridizations()) {
            getDataSet().addHybridizationData(hybridization);
        }
    }

    @Override
    void associateToHybridization(Hybridization hyb) {
        RawArrayData rawArrayData = getArrayData();
        hyb.addRawArrayData(rawArrayData);
        rawArrayData.addHybridization(hyb);
    }

    @Override
    void lookupArrayData() {
        setArrayData(getArrayDao().getRawArrayData(getCaArrayFile()));
    }

}
