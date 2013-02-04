//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web;

import static org.junit.Assert.assertNotNull;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.util.LabelValue;

import org.junit.Test;

/**
 * @author John Hedden
 */
public class LabelValueTest extends AbstractCaarrayTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testLabelValue() {
        LabelValue label1 = new LabelValue();
        label1.setLabel("label1");
        label1.setValue("value1");
        assertNotNull(label1.getValue());
        LabelValue label2 = new LabelValue("label2", "value2");
        assertNotNull(label2.getValue());
        assertNotNull(label2.toString());
        label1.compareTo(label2);
        LabelValue.CASE_INSENSITIVE_ORDER.compare(label1, label2);
    }

}
