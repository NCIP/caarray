//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.platforms.AbstractHandlerTest;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dkokotov
 */
public class IlluminaDataHandlerTest extends AbstractHandlerTest {
    private static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();
    private static final String ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID = "Human_WG-6";

    @Override
    protected void configurePlatforms(PlatformModule platformModule) {
        platformModule.addPlatform(new IlluminaModule());
    }

    @Override
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        if (ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID.equals(lsidObjectId)) {
            return createArrayDesign(lsidObjectId, 126, 126, IlluminaArrayDesignFiles.HUMAN_WG6_CSV);
        } else {
            throw new IllegalArgumentException("Unsupported request design");
        }
    }

    @Test
    public void testIlluminaValidation() {
        final List<File> fileList = new ArrayList<File>();
        fileList.add(IlluminaArrayDataFiles.HUMAN_WG6);
        testValidFile(getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID),
                genMageTabDocSet(fileList), true);
    }

    @Test
    public void testIlluminaDataSmall() throws InvalidDataFileException {
        final CaArrayFile illuminaFile =
                getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6_SMALL, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData illuminaData =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(illuminaFile.getId());
        final DataSet dataSet = illuminaData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = getHybridizationNameToDataMap(dataSet).get("A1");
        assertEquals(4, hybridizationData.getColumns().size());
        final FloatColumn signalColumn =
                (FloatColumn) hybridizationData.getColumn(IlluminaExpressionQuantitationType.AVG_SIGNAL);
        assertNotNull(signalColumn);
        assertEquals(10, signalColumn.getValues().length);
        assertEquals(5.8, signalColumn.getValues()[0], 0.00001);
        assertEquals(3.6, signalColumn.getValues()[9], 0.00001);
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(10, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
        checkAnnotation(illuminaFile, 19);
    }

    @Test
    @Ignore(value = "Not a unit test - low value over small data for build time penality")
    public void testIlluminaDataFull() throws InvalidDataFileException {
        final CaArrayFile illuminaFile =
                getIlluminaCaArrayFile(IlluminaArrayDataFiles.HUMAN_WG6, ILLUMINA_HUMAN_WG_6_LSID_OBJECT_ID);
        this.arrayDataService.importData(illuminaFile, true, DEFAULT_IMPORT_OPTIONS);
        final DerivedArrayData illuminaData =
                (DerivedArrayData) this.daoFactoryStub.getArrayDao().getArrayData(illuminaFile.getId());
        assertEquals(19, illuminaData.getHybridizations().size());
        final DataSet dataSet = illuminaData.getDataSet();
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(19, dataSet.getHybridizationDataList().size());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        assertEquals(4, hybridizationData.getColumns().size());
        assertNotNull(hybridizationData.getHybridization().getArray());
        assertEquals(47293, illuminaData.getDataSet().getDesignElementList().getDesignElements().size());
        checkAnnotation(illuminaFile, 19);
    }

    private CaArrayFile getIlluminaCaArrayFile(File file, String lsidObjectId) {
        final CaArrayFile caArrayFile = getDataCaArrayFile(file, CsvDataHandler.DATA_CSV_FILE_TYPE);
        final ArrayDesign arrayDesign = this.daoFactoryStub.getArrayDao().getArrayDesign(null, null, lsidObjectId);
        caArrayFile.getProject().getExperiment().getArrayDesigns().add(arrayDesign);
        return caArrayFile;
    }

    private Map<String, HybridizationData> getHybridizationNameToDataMap(DataSet dataset) {
        Map<String, HybridizationData> map = new HashMap<String, HybridizationData>();
        for (HybridizationData hybData : dataset.getHybridizationDataList()) {
            map.put(hybData.getHybridization().getName(), hybData);
        }
        return map;
    }
}
