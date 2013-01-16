//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.List;

/**
 * Validates whether the given SDRF columns represent a legal set of columns, wrt ordering and cardinality. It is
 * expected that there will be multiple implementations of this for various rules.
 * 
 * @author dkokotov
 */
public interface SdrfColumnsValidator {
    /**
     * Validate whether the given SDRF columns represent a legal configuration, and add messages for any violations.
     * 
     * @param columns the set of all columns in the SDRF
     * @param messages a collecting parameter of ValidationMessages, to which messages corresponding to violations
     *            should be added
     */
    void validate(SdrfColumns columns, List<ValidationMessage> messages);
}
