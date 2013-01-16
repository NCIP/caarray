//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.validator.v1_1;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.magetab.EntryHeading;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumn;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumns;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author dkokotov
 */
public class SdrfNodeColumnValidatorTest {
    private static final SdrfNodeColumnValidator VALIDATOR = new SdrfNodeColumnValidator();
       
    @Test
    public void testValidate() {
        SdrfColumns columns = new SdrfColumns();
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Characteristics[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Sample Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Extract Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Hybridization Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Factor Value[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Normalization Name")));
        VALIDATOR.validate(columns, messages);
        assertEquals(0, messages.size());

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Derived Array Data File")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Normalization Name")));
        VALIDATOR.validate(columns, messages);
        assertEquals(0, messages.size());

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Array Data File")));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages, SdrfColumnType.ARRAY_DATA_FILE, 9);
        
        messages.clear();
        columns.getColumns().clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Sample Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Extract Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Characteristics[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Hybridization Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Factor Value[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Normalization Name")));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages, SdrfColumnType.SOURCE_NAME, 2);
    }
    
    private void checkHasError(List<ValidationMessage> messages, SdrfColumnType columnType, int column) {
        assertEquals(1, messages.size());
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals(0, messages.get(0).getLine());
        assertEquals(column, messages.get(0).getColumn());        
        assertEquals("Column " + columnType.getDisplayName() + " is not valid at this location", messages.get(0)
                .getMessage());
    }
}
