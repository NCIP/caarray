//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.ParameterValue;
import gov.nih.nci.caarray.magetab.TermSource;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of legal column headings in an SDRF document.
 */
public enum SdrfColumnType {
    /**
     * Source Name.
     */
    SOURCE_NAME(Source.class, "Source Name"),

    /**
     * Sample Name.
     */
    SAMPLE_NAME(Sample.class, "Sample Name"),

    /**
     * Extract Name.
     */
    EXTRACT_NAME(Extract.class, "Extract Name"),

    /**
     * Labeled Extract Name.
     */
    LABELED_EXTRACT_NAME(LabeledExtract.class, "Labeled Extract Name"),

    /**
     * Hybridization Name.
     */
    HYBRIDIZATION_NAME(Hybridization.class, "Hybridization Name"),

    /**
     * Scan Name.
     */
    SCAN_NAME(Scan.class, "Scan Name"),

    /**
     * Normalization Name.
     */
    NORMALIZATION_NAME(Normalization.class, "Normalization Name"),

    /**
     * Array Data File.
     */
    ARRAY_DATA_FILE(ArrayDataFile.class, "Array Data File"),

    /**
     * Derived Array Data File.
     */
    DERIVED_ARRAY_DATA_FILE(DerivedArrayDataFile.class, "Derived Array Data File"),

    /**
     * Array Data Matrix File.
     */
    ARRAY_DATA_MATRIX_FILE(ArrayDataMatrixFile.class, "Array Data Matrix File"),

    /**
     * Derived Array Data Matrix File.
     */
    DERIVED_ARRAY_DATA_MATRIX_FILE(DerivedArrayDataMatrixFile.class, "Derived Array Data Matrix File"),

    /**
     * Image File.
     */
    IMAGE_FILE(Image.class, "Image File"),


    /**
     * Array Design REF.
     */
    ARRAY_DESIGN_REF (ArrayDesign.class, "Array Design REF"),

    /**
     * Array Design File.
     */
    ARRAY_DESIGN_FILE (ArrayDesign.class, "Array Design File"),

    /**
     * Protocol REF.
     */
    PROTOCOL_REF ("Protocol REF"),

    /**
     * Characteristics.
     */
    CHARACTERISTICS (Characteristic.class, "Characteristics"),

    /**
     * Provider.
     */
    PROVIDER ("Provider"),

    /**
     * Material Type.
     */
    MATERIAL_TYPE (OntologyTerm.class, "Material Type"),

    /**
     * Label.
     */
    LABEL ("Label"),

    /**
     * Factor Value.
     */
    FACTOR_VALUE ("Factor Value"),

    /**
     * Performer.
     */
    PERFORMER ("Performer"),

    /**
     * Date.
     */
    DATE ("Date"),

    /**
     * Parameter Value.
     */
    PARAMETER_VALUE (ParameterValue.class, "Parameter Value"),

    /**
     * Unit.
     */
    UNIT ("Unit"),

    /**
     * Description.
     */
    DESCRIPTION ("Description"),

    /**
     * Term Source REF.
     */
    TERM_SOURCE_REF (TermSource.class, "Term Source REF"),

    /**
     * Comment.
     */
    COMMENT (Comment.class, "Comment");

    private static final EnumSet<SdrfColumnType> BIOMATERIAL_NODES = EnumSet.of(SOURCE_NAME, SAMPLE_NAME, EXTRACT_NAME,
            LABELED_EXTRACT_NAME);

    private static final EnumSet<SdrfColumnType> DATA_FILE_NODES = EnumSet.of(ARRAY_DATA_FILE, ARRAY_DATA_MATRIX_FILE,
            DERIVED_ARRAY_DATA_FILE, DERIVED_ARRAY_DATA_MATRIX_FILE);

    private static final EnumSet<SdrfColumnType> NODES = EnumSet.of(SOURCE_NAME, SAMPLE_NAME, EXTRACT_NAME,
            LABELED_EXTRACT_NAME, HYBRIDIZATION_NAME, SCAN_NAME, IMAGE_FILE, ARRAY_DATA_FILE, ARRAY_DATA_MATRIX_FILE,
            NORMALIZATION_NAME, DERIVED_ARRAY_DATA_FILE, DERIVED_ARRAY_DATA_MATRIX_FILE);
    
    private static final EnumSet<SdrfColumnType> TERM_SOURCEABLES = EnumSet.of(PROTOCOL_REF, CHARACTERISTICS, 
            MATERIAL_TYPE, UNIT, LABEL, ARRAY_DESIGN_REF, PARAMETER_VALUE, FACTOR_VALUE);

    private final Class<?> nodeClass;
    private final String displayName;

    /**
     * An SDRF column header with the specified display name.
     *
     * @param displayName the actual name of the column that will be displayed.
     */
    SdrfColumnType(String displayName) {
        this(null, displayName);
    }

    /**
     * An SDRF column header with the specified class and display name.
     *
     * @param nodeClass the Java class that represents this column.
     * @param displayName the actual name of the column that will be displayed.
     */
    SdrfColumnType(Class<?> nodeClass, String displayName) {
        this.nodeClass = nodeClass;
        this.displayName = displayName;
    }

    static SdrfColumnType get(String name) {
        String enumName = StringUtils.replaceChars(name, ' ', '_').toUpperCase();
        return valueOf(enumName);
    }

    Class<?> getNodeClass() {
        return nodeClass;
    }

    /**
     * @return whether a column of this type can be followed by a Term Source REF column.
     */
    public boolean isTermSourceable() {
        return TERM_SOURCEABLES.contains(this);
    }

    /**
     * @return whether a column of this type is a sample-relationship graph node (a biomaterial, hybridization or data
     *         product column)
     */
    public boolean isNode() {
        return NODES.contains(this);
    }

    /**
     * @return whether a column of this type is a biomaterial graph node
     */
    public boolean isBiomaterialNode() {
        return BIOMATERIAL_NODES.contains(this);
    }

    /**
     * @return whether a column of this type is a data file graph node
     */
    public boolean isDataFileNode() {
        return DATA_FILE_NODES.contains(this);
    }

    /**
     * Returns the display name of the column.
     *
     * @return the display name of the column.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return displayName;
    }
}
