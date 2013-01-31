//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

import java.util.Locale;

import org.apache.commons.io.FilenameUtils;

/**
 * File extensions mappable by caArray.
 */
public enum FileExtension {
    /**
     * AFFYMETRIX_CDF.
     */
    CDF(FileType.AFFYMETRIX_CDF),
    /**
     * AFFYMETRIX_CEL.
     */
    CEL(FileType.AFFYMETRIX_CEL),
    /**
     * AFFYMETRIX_CHP.
     */
    CHP(FileType.AFFYMETRIX_CHP),
    /**
     * AFFYMETRIX_CLF.
     */
    CLF(FileType.AFFYMETRIX_CLF),
    /**
     * AFFYMETRIX_PGF.
     */
    PGF(FileType.AFFYMETRIX_PGF),
    /**
     * ILLUMINA_DESIGN_CSV.
     */
    CSV(FileType.ILLUMINA_DESIGN_CSV),
    /**
     * AFFYMETRIX_DAT.
     */
    DAT(FileType.AFFYMETRIX_DAT),
    /**
     * AFFYMETRIX_EXP.
     */
    EXP(FileType.AFFYMETRIX_EXP),
    /**
     * AFFYMETRIX_RPT.
     */
    RPT(FileType.AFFYMETRIX_RPT),
    /**
     * GENEPIX_GAL.
     */
    GAL(FileType.GENEPIX_GAL),
    /**
     * GENEPIX_GPR.
     */
    GPR(FileType.GENEPIX_GPR),
    /**
     * MAGE_TAB_ADF.
     */
    ADF(FileType.MAGE_TAB_ADF),
    /**
     * ILLUMINA_IDAT.
     */
    IDAT(FileType.ILLUMINA_IDAT),
    /**
     * MAGE_TAB_IDF.
     */
    IDF(FileType.MAGE_TAB_IDF, "IDF.TXT"),
    /**
     * MAGE_TAB_SDRF.
     */
    SDRF(FileType.MAGE_TAB_SDRF, "SDRF.TXT"),
    /**
     * UCSF_SPOT_SPT.
     */
    SPT(FileType.UCSF_SPOT_SPT),
    /**
     * AGILENT_TSV.
     */
    TSV(FileType.AGILENT_TSV),
    /**
     * IMAGENE_TPL.
     */
    TPL(FileType.IMAGENE_TPL),
    /**
     * MAGE_TAB_DATA_MATRIX.
     */
    DATA(FileType.MAGE_TAB_DATA_MATRIX),
    /**
     * NIMBLEGEN_NDF.
     */
    NDF(FileType.NIMBLEGEN_NDF);

    private final FileType type;
    private final String[] altExtensions;

    FileExtension(FileType type) {
        this.type = type;
        this.altExtensions = new String[]{};
    }

    FileExtension(FileType type, String... altExtensions) {
        this.type = type;
        this.altExtensions = altExtensions;
    }

    FileType getType() {
        return type;
    }

    static FileExtension getByExtension(String extensionString) {
        if (extensionString == null) {
            throw new IllegalArgumentException("Null extensionString");
        }
        String extensionStringUpper = extensionString.toUpperCase(Locale.getDefault());
        for (FileExtension fileExtension : values()) {
            if (fileExtension.name().equals(extensionStringUpper)) {
                return fileExtension;
            }
            for (String ext : fileExtension.altExtensions) {
                if (ext.equals(extensionStringUpper)) {
                    return fileExtension;
                }
            }
        }
        return null;
    }

    /**
     * Determine a file's type based on its extension.
     * @param filename name of file
     * @return the FileType corresponding to the file extension or null if no matching file type
     */
    public static FileType getTypeFromExtension(String filename) {
        String extension = getExtension(filename);
        FileExtension fileExtension = FileExtension.getByExtension(extension);
        if (fileExtension != null) {
            return fileExtension.getType();
        }
        return null;
    }

    private static String getExtension(String filename) {
        String extension = null;
        String upperFileName = filename.toUpperCase(Locale.getDefault());
        for (FileExtension fileExtension : values()) {
            for (String ext : fileExtension.altExtensions) {
                if (upperFileName.endsWith("." + ext)) {
                    extension = ext;
                }
            }
        }
        if (extension == null) {
            extension = FilenameUtils.getExtension(filename);
        }
        return extension;
    }
}
