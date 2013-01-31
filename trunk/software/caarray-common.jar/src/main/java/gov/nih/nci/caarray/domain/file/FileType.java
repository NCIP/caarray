//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.domain.file;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
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
     * Nimblegen GFF format.
     */
    NIMBLEGEN_GFF,

    /**
     * Nimblegen NDF format.
     */
    NIMBLEGEN_NDF,

    /**
     * Nimblegen raw Pair format.
     */
    NIMBLEGEN_RAW_PAIR,

    /**
     * Nimblegen normalized Pair format.
     */
    NIMBLEGEN_NORMALIZED_PAIR,

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
    UCSF_SPOT_SPT,

    /**
     * Gene Expression Omnibus (GEO) SOFT format data type.
     */
    GEO_SOFT,

    /**
     * Gene Expression Omnibus (GEO) GSM format data type.
     */
    GEO_GSM,

    /**
     * Gene Expression Omnibus (GEO) GPL format array design.
     */
    GEO_GPL,

    /**
     * ScanArray CSV format data type.
     */
    SCANARRAY_CSV;
    
    /**
     * The set of array design file types that the caArray can parse.
     */    
    public static final Set<FileType> PARSEABLE_ARRAY_DESIGN_FILE_TYPES = EnumSet.of(AFFYMETRIX_CDF, AFFYMETRIX_CLF,
            AFFYMETRIX_PGF, ILLUMINA_DESIGN_CSV, GENEPIX_GAL, NIMBLEGEN_NDF);

    /**
     * The set of array design file types.
     */
    public static final Set<FileType> ARRAY_DESIGN_FILE_TYPES = EnumSet.of(AFFYMETRIX_CDF, AFFYMETRIX_CLF,
            AFFYMETRIX_PGF, ILLUMINA_DESIGN_CSV, GENEPIX_GAL, AGILENT_CSV, AGILENT_XML, IMAGENE_TPL, NIMBLEGEN_NDF,
            UCSF_SPOT_SPT, MAGE_TAB_ADF, GEO_GPL);
    
    /**
     * The set of raw array data file types.
     */
    public static final Set<FileType> RAW_ARRAY_DATA_FILE_TYPES
        = EnumSet.of(ILLUMINA_IDAT,
                     AFFYMETRIX_CEL,
                     AGILENT_RAW_TXT,
                     AFFYMETRIX_DAT,
                     AGILENT_TSV,
                     IMAGENE_TIF,
                     GEO_SOFT,
                     GEO_GSM,
                     SCANARRAY_CSV,
                     NIMBLEGEN_RAW_PAIR);

    /**
     * The set of parsed array data file types.
     */
    public static final Set<FileType> DERIVED_ARRAY_DATA_FILE_TYPES = EnumSet.of(AFFYMETRIX_CHP, AFFYMETRIX_EXP,
            AFFYMETRIX_TXT, AFFYMETRIX_RPT, ILLUMINA_DATA_CSV, ILLUMINA_DATA_TXT, GENEPIX_GPR, IMAGENE_TXT,
            AGILENT_DERIVED_TXT, NIMBLEGEN_GFF, NIMBLEGEN_NORMALIZED_PAIR);

    /**
     * The set of mage tab file types.
     */
    public static final Set<FileType> MAGE_TAB_FILE_TYPES = EnumSet.of(MAGE_TAB_ADF, MAGE_TAB_DATA_MATRIX,
            MAGE_TAB_IDF, MAGE_TAB_SDRF);

    /**
     * The set of array data file types that caArray can parse.
     */
    public static final Set<FileType> PARSEABLE_ARRAY_DATA_FILE_TYPES = EnumSet.of(AFFYMETRIX_CEL, AFFYMETRIX_CHP,
            ILLUMINA_DATA_CSV, GENEPIX_GPR, NIMBLEGEN_NORMALIZED_PAIR, NIMBLEGEN_RAW_PAIR);

    private static final Map<FileType, FileType> RAW_TO_DERIVED_MAP = new HashMap<FileType, FileType>();
    private static final Map<FileType, FileType> DERIVED_TO_RAW_MAP = new HashMap<FileType, FileType>();

    static {
        RAW_TO_DERIVED_MAP.put(AGILENT_RAW_TXT, AGILENT_DERIVED_TXT);
        DERIVED_TO_RAW_MAP.put(AGILENT_DERIVED_TXT, AGILENT_RAW_TXT);
        RAW_TO_DERIVED_MAP.put(NIMBLEGEN_RAW_PAIR, NIMBLEGEN_NORMALIZED_PAIR);
        DERIVED_TO_RAW_MAP.put(NIMBLEGEN_NORMALIZED_PAIR, NIMBLEGEN_RAW_PAIR);
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

    /**
     * Return the names of the given FileTypes.
     * @param types the FileTypes whose names to return
     * @return the names, e.g. the set of type.getName() for each type in types 
     */
    public static Set<String> namesForTypes(Iterable<FileType> types) {
        Set<String> names = new HashSet<String>();
        for (FileType type : types) {
            names.add(type.getName());
        }
        return names;
    }
}
