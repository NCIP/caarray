//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator.caarray;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.magetab.EntryHeading;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author dkokotov
 */
public class SdrfMinimumColumnValidatorTest {
    private static final SdrfMinimumColumnsValidator VALIDATOR = new SdrfMinimumColumnsValidator();;
       
    @Test
    public void testValidate() {
        SdrfColumns columns = new SdrfColumns();
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);
        
        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Characteristics[Age]")));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Hybridization Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Derived Array Data File")));
        VALIDATOR.validate(columns, messages);
        assertEquals(0, messages.size());

        columns.getColumns().remove(2);
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);        
    }
    
    private void checkHasError(List<ValidationMessage> messages) {
        assertEquals(1, messages.size());
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals(
                "SDRF file does not have the minimum number of columns (a biomaterial, Hybridization, and a data file)",
                messages.get(0).getMessage());        
    }
}
