//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.NimblegenArrayDataFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;

import org.junit.Test;

/**
 * @author dkokotov
 * 
 */
public class PairDataHandlerTest extends AbstractHandlerTest {
    private static final String NIMBLEGEN_2006_08_03_HG18_60mer_expr_LSID_OBJECT_ID = "2006-08-03_HG18_60mer_expr";
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new NimblegenModule());
    }

    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        if (NIMBLEGEN_2006_08_03_HG18_60mer_expr_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 126, 126, null);
        } else {
            throw new IllegalArgumentException("Unsupported request design");
        }
    }

    @Test
    public void testNimblegenData() throws InvalidDataFileException {
        testNimblegenDataFull();
    }

    private void testNimblegenDataFull() throws InvalidDataFileException {
        final CaArrayFile nimblegenFile =
                getNimblegenCaArrayFile(NimblegenArrayDataFiles.HUMAN_EXPRESSION,
                        this.NIMBLEGEN_2006_08_03_HG18_60mer_expr_LSID_OBJECT_ID);
        this.arrayDataService.importData(nimblegenFile, true, DEFAULT_IMPORT_OPTIONS);
        final RawArrayData nimblegenData =
                (RawArrayData) this.daoFactoryStub.getArrayDao().getArrayData(nimblegenFile.getId());
        assertEquals(1, nimblegenData.getHybridizations().size());
        final DataSet dataSet = nimblegenData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(1, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(5, hybridizationData.getColumns().size());
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(388502, nimblegenData.getDataSet().getDesignElementList().getDesignElements().size());
    }

    private CaArrayFile getNimblegenCaArrayFile(File file, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(file, PairDataHandler.RAW_PAIR_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

}
