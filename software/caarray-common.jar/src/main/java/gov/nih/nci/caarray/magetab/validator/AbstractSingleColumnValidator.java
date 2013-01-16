//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.List;

/**
 * Base class for SdrfColumnsValidators that need validate only a particular column, rather than the set of columns as a
 * whole. Subclasses should implement the appliesTo and validate methods.
 * 
 * @author dkokotov
 */
public abstract class AbstractSingleColumnValidator implements SdrfColumnsValidator {
    /**
     * {@inheritDoc}
     */
    public void validate(SdrfColumns columns, List<ValidationMessage> messages) {
        for (SdrfColumn column : columns.getColumns()) {
            if (appliesTo(column)) {
                validate(columns, column, messages);
            }
        }
    }
    
    /**
     * Return whether this validator should be applied to the given column.
     * 
     * @param column the candidate column
     * @return true if the validator should be applied, false otherwise.
     */
    public abstract boolean appliesTo(SdrfColumn column);

    /**
     * Validate whether the given SDRF column is legal, and add messages for any violations.
     * 
     * @param columns the set of all columns in the SDRF
     * @param columnToValidate the particular column to be validated
     * @param messages a collecting parameter of ValidationMessages, to which messages corresponding to violations
     *            should be added
     */
    public abstract void validate(SdrfColumns columns, SdrfColumn columnToValidate, List<ValidationMessage> messages);
}
