//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator.caarray;

import java.util.List;

import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.magetab.validator.SdrfColumnsValidator;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

/**
 * Validator enforcing the caArray standards for minimum columns required. We require at least one biomaterial name
 * column, the hybrdization name column, and at least one data file column.
 * 
 * @author dkokotov
 */
public class SdrfMinimumColumnsValidator implements SdrfColumnsValidator {
    /**
     * {@inheritDoc}
     */
    public void validate(SdrfColumns columns, List<ValidationMessage> messages) {
        boolean foundBiomaterial = false;
        boolean foundHyb = false;
        boolean foundDataFile = false;
        
        for (SdrfColumn aColumn : columns.getColumns()) {
            foundBiomaterial |= aColumn.getType().isBiomaterialNode();
            foundHyb |= SdrfColumnType.HYBRIDIZATION_NAME.equals(aColumn.getType());
            foundDataFile |= aColumn.getType().isDataFileNode(); 
        }
        
        if (!foundBiomaterial || !foundHyb || !foundDataFile) {
            messages.add(new ValidationMessage(Type.ERROR, "SDRF file does not have the "
                    + "minimum number of columns (a biomaterial, Hybridization, and a data file)"));
        }
    }
}
