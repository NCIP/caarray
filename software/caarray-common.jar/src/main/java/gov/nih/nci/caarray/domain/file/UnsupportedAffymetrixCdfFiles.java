//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.file;

/**
 * A list of Affymetrix CDF files that the AffxFusion jar (and therefore caArray) cannot handle.
 * See GForge defect 11922 at https://gforge.nci.nih.gov/tracker/?group_id=305&atid=1344&func=detail&aid=11922
 * @author Steve Lustbader
 */
public enum UnsupportedAffymetrixCdfFiles {

    /**
     * Human Exon 1.0 ST design file.
     */
    HUMAN_EXON_1_0_ST("HuEx-1_0-st-v2.text.cdf"),

    /**
     * Human Exon 1.0 ST design file (zipped).
     */
    HUMAN_EXON_1_0_ST_ZIP("HuEx-1_0-st-v2.cdf.zip"),

    /**
     * Mouse Exon 1.0 ST design file.
     */
    MOUSE_EXON_1_0_ST("MoEx-1_0-st-v1.text.cdf"),

    /**
     * Mouse Exon 1.0 ST design file (zipped).
     */
    MOUSE_EXON_1_0_ST_ZIP("MoEx-1_0-st-v1.text.cdf.zip"),

    /**
     * Rat Exon 1.0 ST design file.
     */
    RAT_EXON_1_0_ST("RaEx-1_0-st-v1.text.cdf"),

    /**
     * Rat Exon 1.0 ST design file (zipped).
     */
    RAT_EXON_1_0_ST_ZIP("RaEx-1_0-st-v1.text.cdf.zip");

    private String filename;

    private UnsupportedAffymetrixCdfFiles(String filename) {
        this.filename = filename;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Check if the given filename is on the list of unsupported files.
     * @param filename filename to check
     * @return true if the filename is not supported, false otherwise
     */
    public static boolean isUnsupported(String filename) {
        for (UnsupportedAffymetrixCdfFiles file : values()) {
            if (file.getFilename().equals(filename)) {
                return true;
            }
        }
        return false;
    }

}
