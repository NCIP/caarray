//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.validator;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.validation.ValidationMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * a ValidatorSet is a grouping of validators representing a particular set of rules that a MAGE-TAB fileset
 * must conform to. Different consumers of MAGE-TAB documents may define different sets of rules.
 * 
 * @author dkokotov
 */
public class ValidatorSet {
    /**
     * A Validator set with no validations defined at all.
     */
    public static final ValidatorSet NO_VALIDATION = new ValidatorSet();
    
    private Set<SdrfColumnsValidator> sdrfColumnValidators = new HashSet<SdrfColumnsValidator>();

    /**
     * @return the sdrfColumnValidators
     */
    public Set<SdrfColumnsValidator> getSdrfColumnValidators() {
        return sdrfColumnValidators;
    }

    /**
     * @param sdrfColumnValidators the sdrfColumnValidators to set
     */
    public void setSdrfColumnValidators(Set<SdrfColumnsValidator> sdrfColumnValidators) {
        this.sdrfColumnValidators = sdrfColumnValidators;
    }

    /**
     * Validate whether the given SDRF columns represent a legal configuration, and add messages for any violations.
     * 
     * @param columns the set of all columns in the SDRF
     * @param messages a collecting parameter of ValidationMessages, to which messages corresponding to violations
     *            should be added
     */
    public void validateSdrfColumns(SdrfColumns columns, List<ValidationMessage> messages) {
        for (SdrfColumnsValidator colValidator : sdrfColumnValidators) {
            colValidator.validate(columns, messages);
        }
    }
}
