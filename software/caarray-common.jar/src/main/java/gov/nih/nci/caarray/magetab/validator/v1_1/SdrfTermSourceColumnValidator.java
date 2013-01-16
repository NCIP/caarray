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

import java.util.List;

/**
 * Validates that the term source columns are in valid spots (following term sourceable columns, as 
 * defined by the Mage-TAB v1.1 spec.
 * 
 * @author dkokotov
 */
public class SdrfTermSourceColumnValidator extends AbstractSingleColumnValidator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appliesTo(SdrfColumn column) {
        return column.getType() == SdrfColumnType.TERM_SOURCE_REF;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(SdrfColumns columns, SdrfColumn columnToValidate, List<ValidationMessage> messages) {        
        SdrfColumn prevColumn = columns.getPreviousColumn(columnToValidate);

        if (prevColumn == null || !prevColumn.getType().isTermSourceable()) {
            messages.add(new ValidationMessage(0, columns.getColumns().indexOf(columnToValidate), Type.ERROR,
                    "Term Source Ref is not preceded by valid data type"));
        }
    }

}
