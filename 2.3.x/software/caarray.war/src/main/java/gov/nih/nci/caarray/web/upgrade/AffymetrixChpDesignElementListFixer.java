//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.application.arraydesign.AbstractAffymetrixChpDesignElementListUtility;
import gov.nih.nci.caarray.application.arraydesign.AffymetrixArrayDesignReadException;
import gov.nih.nci.caarray.application.arraydesign.AffymetrixChpCdfDesignElementListUtility;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.FileType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPGenericData;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionCHPTilingData;

/**
 * Creates corrected <code>DesignElementLists</code> for any Affymetrix designs and associates
 * lists to CHP <code>DataSets</code>.
 */
public class AffymetrixChpDesignElementListFixer extends AbstractMigrator {

    private static final Logger LOG = Logger.getLogger(AffymetrixChpDesignElementListFixer.class);

    /**
     * {@inheritDoc}
     */
    public void migrate() throws MigrationStepFailedException {
        initialize();
        try {
            createDesignElementLists();
            fixChpDataSets();
        } catch (AffymetrixArrayDesignReadException e) {
            throw new MigrationStepFailedException(e);
        }
    }

    private void initialize() {
        FusionCHPLegacyData.registerReader();
        FusionCHPGenericData.registerReader();
        FusionCHPTilingData.registerReader();
    }

    private void createDesignElementLists() throws AffymetrixArrayDesignReadException {
        ArrayDao arrayDao = getDaoFactory().getArrayDao();
        List<Long> designIds = getAllAffymetrixDesignIds();
        for (Long designId : designIds) {
            ArrayDesign design = getDesignService().getArrayDesign(designId);
            LOG.info("Creating fixed DesignElementList for design " + design.getName());
            AffymetrixChpCdfDesignElementListUtility.createDesignElementList(design, arrayDao);
            arrayDao.flushSession();
            arrayDao.clearSession();
        }
    }

    private void fixChpDataSets() throws MigrationStepFailedException {
        List<DerivedArrayData> chpDatas = getAllChpDatas();
        for (DerivedArrayData chpData : chpDatas) {
            fix(chpData);
        }
    }

    private void fix(DerivedArrayData chpData) {
        LOG.info("Fixing CHP data " + chpData.getName());
        ArrayDesign design = getDesign(chpData);
        DesignElementList designElementList =
            AbstractAffymetrixChpDesignElementListUtility.getDesignElementList(design, getDesignService());
        chpData.getDataSet().setDesignElementList(designElementList);
        getDaoFactory().getArrayDao().save(chpData.getDataSet());
    }

    private ArrayDesign getDesign(DerivedArrayData chpData) {
        File chpFile = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(chpData.getDataFile());
        FusionCHPLegacyData affyChpData =
            FusionCHPLegacyData.fromBase(FusionCHPDataReg.read(chpFile.getAbsolutePath()));
        String objectId = affyChpData.getHeader().getChipType();
        affyChpData.clear();
        affyChpData = null;
        System.gc();
        return getDesignService().getArrayDesign("Affymetrix.com", "PhysicalArrayDesign", objectId);
    }

    private List<DerivedArrayData> getAllChpDatas() throws MigrationStepFailedException {
        List<DerivedArrayData> chpDatas = new ArrayList<DerivedArrayData>();
        List<DerivedArrayData> allDatas = getAllDerivedDatas();
        for (DerivedArrayData data : allDatas) {
            if (isChpData(data)) {
                chpDatas.add(data);
            }
        }
        return chpDatas;
    }

    private boolean isChpData(DerivedArrayData data) {
        return data.getType() != null
            && (AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CHP.getName().equals(data.getType().getName())
            || AffymetrixArrayDataTypes.AFFYMETRIX_SNP_CHP.getName().equals(data.getType().getName()));
    }

    private List<DerivedArrayData> getAllDerivedDatas() throws MigrationStepFailedException {
        try {
            return getDataService().retrieveAll(DerivedArrayData.class);
        } catch (IllegalAccessException e) {
            throw new MigrationStepFailedException(e);
        } catch (InstantiationException e) {
            throw new MigrationStepFailedException(e);
        }
    }

    private List<Long> getAllAffymetrixDesignIds() {
        List<ArrayDesign> allDesigns = getDesignService().getArrayDesigns();
        List<Long> affyDesignIds = new ArrayList<Long>();
        for (ArrayDesign design : allDesigns) {
            if (isAffymetrixDesign(design)) {
                affyDesignIds.add(design.getId());
            }
        }
        return affyDesignIds;
    }

    private boolean isAffymetrixDesign(ArrayDesign design) {
        return design.getFirstDesignFile() != null
                && FileType.AFFYMETRIX_CDF.equals(design.getFirstDesignFile().getFileType());
    }

}
