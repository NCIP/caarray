package gov.nih.nci.caarray.platforms.datamatrix;

import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

/**
 * 
 * @author dharley
 *
 */
public enum DataMatrixCopyNumberQuantitationTypes implements QuantitationTypeDescriptor {
    
    /**
     * Log2Ratio value.
     */
    LOG_2_RATIO("DataMatrixCopyNumber.Log2Ratio", DataType.FLOAT),
    
    /**
     * The probe's chromosome ID.
     */
    CHROMOSOME("DataMatrixCopyNumber.Chromosome", DataType.STRING),
    
    /**
     * The probe's position on the chromose.
     */
    POSITION("DataMatrixCopyNumber.Position", DataType.LONG);

    private final String name;
    private final DataType type;

    DataMatrixCopyNumberQuantitationTypes(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public DataType getDataType() {
        return type;
    }

}
