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

public class SdrfDuplicateNodeValidatorTest {
    
    private static final SdrfDuplicateNodeValidator VALIDATOR = new SdrfDuplicateNodeValidator();

    @Test
    public void testValidate() {
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        SdrfColumns columns = new SdrfColumns();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError("Source Name", messages);
        
        columns = new SdrfColumns();
        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Sample Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Sample Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError("Sample Name", messages);
        
        columns = new SdrfColumns();
        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Extract Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Extract Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError("Extract Name", messages);
        
        columns = new SdrfColumns();
        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Labeled Extract Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Labeled Extract Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError("Labeled Extract Name", messages);
        
        columns = new SdrfColumns();
        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Hybridization Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Hybridization Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError("Hybridization Name", messages);
    }
    
    private void checkHasError(String expectedType, List<ValidationMessage> messages) {
        assertEquals(1, messages.size());
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals("SDRF file should only contain 1 '" + expectedType + "' column, but 2 were found.",
                messages.get(0).getMessage());        
    }
    
}
