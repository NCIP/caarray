//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator.v1_1;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.magetab.validator.AbstractSingleColumnValidator;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates that the node columns of the SDRF are in the correct order as specified. 
 * by the Mage-TAB v1.1 spec
 * 
 * @author dkokotov
 */
public class SdrfNodeColumnValidator extends AbstractSingleColumnValidator {
    private static final List<SdrfColumnType> BIOMATERIAL_AND_HYB_ORDER = new ArrayList<SdrfColumnType>(Arrays.asList(
            SdrfColumnType.SOURCE_NAME,
            SdrfColumnType.SAMPLE_NAME,
            SdrfColumnType.EXTRACT_NAME,
            SdrfColumnType.LABELED_EXTRACT_NAME,
            SdrfColumnType.HYBRIDIZATION_NAME
            )); 

    private static final List<SdrfColumnType> RAW_DATA_COLUMN_ORDER = new ArrayList<SdrfColumnType>(Arrays.asList(
            SdrfColumnType.SCAN_NAME,
            SdrfColumnType.IMAGE_FILE,
            SdrfColumnType.ARRAY_DATA_FILE,
            SdrfColumnType.ARRAY_DATA_MATRIX_FILE            
            )); 

    private static final List<SdrfColumnType> DERIVED_DATA_COLUMN_ORDER = new ArrayList<SdrfColumnType>(Arrays.asList(
            SdrfColumnType.NORMALIZATION_NAME,
            SdrfColumnType.DERIVED_ARRAY_DATA_FILE,
            SdrfColumnType.DERIVED_ARRAY_DATA_MATRIX_FILE
            )); 

    private static final List<SdrfColumnType> VALID_COLUMN_ORDER = new ArrayList<SdrfColumnType>(
            BIOMATERIAL_AND_HYB_ORDER);
    static {
        VALID_COLUMN_ORDER.addAll(RAW_DATA_COLUMN_ORDER);
        VALID_COLUMN_ORDER.addAll(DERIVED_DATA_COLUMN_ORDER);
    }
     
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appliesTo(SdrfColumn column) {
        return column.getType().isNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(SdrfColumns columns, SdrfColumn columnToValidate, List<ValidationMessage> messages) {        
        SdrfColumn previousNode = columns.getPreviousNodeColumn(columnToValidate);

        if (previousNode != null && !isInExpectedOrder(columnToValidate, previousNode)
                && !isInRepeatingDataGroup(columnToValidate, previousNode, RAW_DATA_COLUMN_ORDER)
                && !isInRepeatingDataGroup(columnToValidate, previousNode, DERIVED_DATA_COLUMN_ORDER)) {
            messages.add(new ValidationMessage(0, columns.getColumns().indexOf(columnToValidate), Type.ERROR, "Column "
                    + columnToValidate.getType().getDisplayName() + " is not valid at this location"));
        }
    }

    private boolean isInExpectedOrder(SdrfColumn columnToValidate, SdrfColumn previousNode) {        
        return VALID_COLUMN_ORDER.indexOf(previousNode.getType()) <= VALID_COLUMN_ORDER.indexOf(columnToValidate
                .getType());
    }
    
    private boolean isInRepeatingDataGroup(SdrfColumn columnToValidate, SdrfColumn previousNode,
            List<SdrfColumnType> dataGroup) {        
        return dataGroup.contains(columnToValidate.getType()) && dataGroup.contains(previousNode.getType());
    }
        
}
