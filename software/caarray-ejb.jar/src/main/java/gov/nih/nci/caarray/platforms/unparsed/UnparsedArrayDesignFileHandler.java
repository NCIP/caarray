//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms.unparsed;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Common handler implementation of all known unparsed array design types.
 * 
 * @author Steve Lustbader
 */
public class UnparsedArrayDesignFileHandler implements DesignFileHandler {
    private static final LSID CAARRAY_LOCAL_LSID_TEMPLATE = new LSID(AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY,
            AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE, null);
    private static final LSID AGILENT_LSID_TEMPLATE = new LSID("Agilent.com", "PhysicalArrayDesign", null);

    /**
     * FileType for Imagene TPL files.
     */
    public static final FileType IMAGENE_TPL = new FileType("IMAGENE_TPL", FileCategory.ARRAY_DESIGN, false, "TPL");
    /**
     * FileType for UCSF Spot files.
     */
    public static final FileType UCSF_SPOT_SPT = new FileType("UCSF_SPOT_SPT", FileCategory.ARRAY_DESIGN, false, "SPT");
    /**
     * FileType for MAGE-TAB ADF files.
     */
    public static final FileType MAGE_TAB_ADF = new FileType("MAGE_TAB_ADF", FileCategory.ARRAY_DESIGN, false, "ADF");
    /**
     * FileType for GEO GPL files.
     */
    public static final FileType GEO_GPL = new FileType("GEO_GPL", FileCategory.ARRAY_DESIGN, false);
    /**
     * FileType for AGILENT CSV files.
     */
    public static final FileType AGILENT_CSV = new FileType("AGILENT_CSV", FileCategory.ARRAY_DESIGN, false);
    static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(IMAGENE_TPL, UCSF_SPOT_SPT, MAGE_TAB_ADF, GEO_GPL,
            AGILENT_CSV);

    private static final Map<FileType, LSID> LSID_TEMPLATE_MAP = Maps.newHashMap();
    static {
        LSID_TEMPLATE_MAP.put(IMAGENE_TPL, CAARRAY_LOCAL_LSID_TEMPLATE);
        LSID_TEMPLATE_MAP.put(UCSF_SPOT_SPT, CAARRAY_LOCAL_LSID_TEMPLATE);
        LSID_TEMPLATE_MAP.put(MAGE_TAB_ADF, CAARRAY_LOCAL_LSID_TEMPLATE);
        LSID_TEMPLATE_MAP.put(GEO_GPL, CAARRAY_LOCAL_LSID_TEMPLATE);
        LSID_TEMPLATE_MAP.put(AGILENT_CSV, AGILENT_LSID_TEMPLATE);
    }

    private CaArrayFile designFile;
    private LSID designLsid;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openFiles(Set<CaArrayFile> designFiles) {
        if (designFiles == null || designFiles.size() != 1
                || !LSID_TEMPLATE_MAP.containsKey(designFiles.iterator().next().getFileType())) {
            return false;
        }

        this.designFile = designFiles.iterator().next();
        final LSID lsidTemplate = LSID_TEMPLATE_MAP.get(this.designFile.getFileType());
        this.designLsid =
                new LSID(lsidTemplate.getAuthority(), lsidTemplate.getNamespace(),
                        FilenameUtils.getBaseName(this.designFile.getName()));
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeFiles() {
        this.designFile = null;
        this.designLsid = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
         return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDesignDetails(ArrayDesign arrayDesign) {
        // no-op for unknown types
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(this.designLsid.getObjectId());
        arrayDesign.setLsid(this.designLsid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(ValidationResult result) {
        final FileValidationResult fileResult = result.getOrCreateFileValidationResult(this.designFile.getName());
        this.designFile.setValidationResult(fileResult);
    }
}
