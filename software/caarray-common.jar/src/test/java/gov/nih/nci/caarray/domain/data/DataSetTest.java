//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.data;

import gov.nih.nci.caarray.domain.hybridization.Hybridization;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD")
public class DataSetTest {

    private final List<Hybridization> hybridizations = new ArrayList<Hybridization>();
    private final List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();

    @Before
    public void setupLists() {
        quantitationTypes.add(createQuantitationType("float1", Float.class));
        quantitationTypes.add(createQuantitationType("float2", Float.class));
        quantitationTypes.add(createQuantitationType("string1", String.class));

        hybridizations.add(createHybridization("hybridization1"));
        hybridizations.add(createHybridization("hybridization2"));
        hybridizations.add(createHybridization("hybridization3"));
    }

    private Hybridization createHybridization(String name) {
        Hybridization hybridization = new Hybridization();
        hybridization.setName(name);
        hybridization.setLsidForEntity("authority:namespace:" + name);
        return hybridization;
    }

    private QuantitationType createQuantitationType(String name, Class<?> type) {
        QuantitationType quantitationType = new QuantitationType();
        quantitationType.setName(name);
        quantitationType.setTypeClass(type);
        return quantitationType;
    }
    
    @Test
    public void testGetQuantitationTypes() {
        DataSet dataSet = new DataSet();
        assertNotNull(dataSet.getQuantitationTypes());
    }

}
