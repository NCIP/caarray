//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.file;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public enum FileType implements Comparable<FileType> {

    /**
     * Affymetrix native array design file.
     */
    AFFYMETRIX_CDF,

    /**
     * Affymetrix native CEL data format.
     */
    AFFYMETRIX_CEL,

    /**
     * Affymetrix native CHP data format.
     */
    AFFYMETRIX_CHP,

    /**
     * Affymetrix native CLF array design format (paired with an {@link #AFFYMETRIX_PGF} file).
     */
    AFFYMETRIX_CLF,

    /**
     * Affymetrix native DAT image format.
     */
    AFFYMETRIX_DAT,

    /**
     * Affymetrix EXP format.
     */
    AFFYMETRIX_EXP,

    /**
     * Affymetrix native PGF array design format (paired with an {@link #AFFYMETRIX_CLF} file).
     */
    AFFYMETRIX_PGF,

    /**
     * Affymetrix TXT format.
     */
    AFFYMETRIX_RPT,

    /**
     * Affymetrix TXT format.
     */
    AFFYMETRIX_TXT,

    /**
     * Agilent CSV format.
     */
    AGILENT_CSV,

    /**
     * Agilent TSV format.
     */
    AGILENT_TSV,

    /**
     * Agilent raw TXT format.
     */
    AGILENT_RAW_TXT,

    /**
     * Agilent derived TXT format.
     */
    AGILENT_DERIVED_TXT,

    /**
     * Agilent XML format.
     */
    AGILENT_XML,

    /**
     * Genepix array design GAL file.
     */
    GENEPIX_GAL,

    /**
     * Genepix array data GPR file.
     */
    GENEPIX_GPR,

    /**
     * Illumina raw array data file.
     */
    ILLUMINA_IDAT,

    /**
     * Illumina array data CSV file.
     */
    ILLUMINA_DATA_CSV,

    /**
     * Illumina array design CSV file.
     */
    ILLUMINA_DESIGN_CSV,

    /**
     * Illumina array data TXT file.
     */
    ILLUMINA_DATA_TXT,

    /**
     * Imagene TXT format.
     */
    IMAGENE_TXT,

    /**
     * Imagene TIF format.
     */
    IMAGENE_TIF,

    /**
     * Imagene TPL array design format.
     */
    IMAGENE_TPL,

    /**
     * Nimblegen TXT format.
     */
    NIMBLEGEN_TXT,

    /**
     * Nimblegen GFF format.
     */
    NIMBLEGEN_GFF,

    /**
     * Nimblegen NDF format.
     */
    NIMBLEGEN_NDF,

    /**
     * The MAGE_TAB Array Design Format file type.
     */
    MAGE_TAB_ADF,

    /**
     * The MAGE_TAB data file type.
     */
    MAGE_TAB_DATA_MATRIX,

    /**
     * The MAGE_TAB Investigation Description Format file type.
     */
    MAGE_TAB_IDF,

    /**
     * The MAGE_TAB Sample and Data Relationship Format file type.
     */
    MAGE_TAB_SDRF,

    /**
     * The UCSF Spot SPT array design file type.
     */
    UCSF_SPOT_SPT;

    private static final Set<FileType> ARRAY_DESIGN_FILE_TYPES = new LinkedHashSet<FileType>();
    private static final Set<FileType> PARSEABLE_ARRAY_DESIGN_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> RAW_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> DERIVED_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> PARSEABLE_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();
    private static final Map<FileType, FileType> RAW_TO_DERIVED_MAP = new HashMap<FileType, FileType>();
    private static final Map<FileType, FileType> DERIVED_TO_RAW_MAP = new HashMap<FileType, FileType>();

    static {
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(AFFYMETRIX_CDF);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(AFFYMETRIX_CLF);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(AFFYMETRIX_PGF);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(ILLUMINA_DESIGN_CSV);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(GENEPIX_GAL);
        ARRAY_DESIGN_FILE_TYPES.addAll(PARSEABLE_ARRAY_DESIGN_FILE_TYPES);
        ARRAY_DESIGN_FILE_TYPES.add(AGILENT_CSV);
        ARRAY_DESIGN_FILE_TYPES.add(AGILENT_XML);
        ARRAY_DESIGN_FILE_TYPES.add(IMAGENE_TPL);
        ARRAY_DESIGN_FILE_TYPES.add(NIMBLEGEN_NDF);
        ARRAY_DESIGN_FILE_TYPES.add(UCSF_SPOT_SPT);
        ARRAY_DESIGN_FILE_TYPES.add(MAGE_TAB_ADF);
        RAW_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_IDAT);
        RAW_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CEL);
        RAW_ARRAY_DATA_FILE_TYPES.add(AGILENT_RAW_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CHP);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_EXP);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_RPT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_CSV);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(GENEPIX_GPR);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(IMAGENE_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AGILENT_DERIVED_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(NIMBLEGEN_GFF);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(NIMBLEGEN_TXT);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CEL);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CHP);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_CSV);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(GENEPIX_GPR);
        RAW_TO_DERIVED_MAP.put(AGILENT_RAW_TXT, AGILENT_DERIVED_TXT);
        DERIVED_TO_RAW_MAP.put(AGILENT_DERIVED_TXT, AGILENT_RAW_TXT);
    }

    /**
     * @return true if this file type is an array design.
     */
    public boolean isArrayDesign() {
        return ARRAY_DESIGN_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the system supports parsing this array design format.
     */
    public boolean isParseableArrayDesign() {
        return PARSEABLE_ARRAY_DESIGN_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the system supports parsing this data format.
     */
    public boolean isParseableData() {
        return PARSEABLE_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isDerivedArrayData() {
        return DERIVED_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isRawArrayData() {
        return RAW_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if this file type is array data.
     */
    public boolean isArrayData() {
        return isRawArrayData() || isDerivedArrayData();
    }

    /**
     * @return the enum name -- necessary for bean property access.
     */
    public String getName() {
        return name();
    }

    /**
     * @return the ArrayDesignFileTypes
     */
    public static Set<FileType> getArrayDesignFileTypes() {
        return ARRAY_DESIGN_FILE_TYPES;
    }
    
    /**
     * @return if this file type has a derived variant, false otherwise.
     */
    public boolean hasRawVersion() {
        return DERIVED_TO_RAW_MAP.get(this) != null;
    }

    /**
     * @return if this file type has a derived version, false otherwise.
     */
    public boolean hasDerivedVersion() {
        return DERIVED_TO_RAW_MAP.get(this) != null;
    }

    /**
     * @return the raw version of this file type
     */
    public FileType getRawType() {
        FileType rawType = DERIVED_TO_RAW_MAP.get(this);
        return (rawType == null) ? this : rawType;
    }

    /**
     * @return the derived version of this file type
     */
    public FileType getDerivedType() {
        FileType derivedType = RAW_TO_DERIVED_MAP.get(this);
        return (derivedType == null) ? this : derivedType;
    }
}
