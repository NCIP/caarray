//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
public class SdrfTermSourceColumnValidatorTest {
    private static final SdrfTermSourceColumnValidator VALIDATOR = new SdrfTermSourceColumnValidator();
       
    @Test
    public void testValidate() {
        SdrfColumns columns = new SdrfColumns();
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

        columns.getColumns().add(new SdrfColumn(new EntryHeading("Source Name")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Characteristics[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading(SdrfColumnType.TERM_SOURCE_REF.getDisplayName())));
        VALIDATOR.validate(columns, messages);
        assertEquals(0, messages.size());

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading(SdrfColumnType.TERM_SOURCE_REF.getDisplayName())));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);

        messages.clear();
        columns.getColumns().remove(3);
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Factor Value[cell line]")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading(SdrfColumnType.TERM_SOURCE_REF.getDisplayName())));
        VALIDATOR.validate(columns, messages);
        assertEquals(0, messages.size());

        messages.clear();
        columns.getColumns().add(new SdrfColumn(new EntryHeading("Derived Array Data File")));
        columns.getColumns().add(new SdrfColumn(new EntryHeading(SdrfColumnType.TERM_SOURCE_REF.getDisplayName())));
        VALIDATOR.validate(columns, messages);
        checkHasError(messages);
    }
    
    private void checkHasError(List<ValidationMessage> messages) {
        assertEquals(1, messages.size());
        assertEquals(Type.ERROR, messages.get(0).getType());
        assertEquals("Term Source Ref is not preceded by valid data type", messages.get(0).getMessage());
    }
}
