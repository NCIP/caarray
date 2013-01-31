//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.data.QuantitationType;

import java.util.Comparator;

import org.junit.Test;

public class AffymetrixQuantitationTypeTest extends AbstractCaarrayTest {

    @Test
    public void testIsEquivalent() {
        QuantitationType type = createType(AffymetrixCelQuantitationType.CEL_INTENSITY);
        assertFalse(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(null));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(type));
        assertFalse(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV.isEquivalent(type));
        
        type = createType(AffymetrixSnpChpQuantitationType.CHP_ALLELE);
        assertFalse(AffymetrixSnpChpQuantitationType.CHP_ALLELE.isEquivalent(null));
        assertTrue(AffymetrixSnpChpQuantitationType.CHP_ALLELE.isEquivalent(type));
        assertFalse(AffymetrixSnpChpQuantitationType.CHP_RAS1.isEquivalent(type));
        
    }

    private QuantitationType createType(AffymetrixCelQuantitationType descriptor) {
        QuantitationType type = new QuantitationType();
        type.setName(descriptor.getName());
        type.setTypeClass(descriptor.getDataType().getTypeClass());
        return type;
    }

    private QuantitationType createType(AffymetrixSnpChpQuantitationType descriptor) {
        QuantitationType type = new QuantitationType();
        type.setName(descriptor.getName());
        type.setTypeClass(descriptor.getDataType().getTypeClass());
        return type;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetComparator() {
        Comparator<QuantitationType> comparator = AffymetrixCelQuantitationType.getComparator();
        QuantitationType type1 = createType(AffymetrixCelQuantitationType.CEL_X);
        QuantitationType type2 = createType(AffymetrixCelQuantitationType.CEL_PIXELS);
        assertTrue(comparator.compare(type1, type2) < 0);
        assertTrue(comparator.compare(type2, type1) > 0);
        assertEquals(0, comparator.compare(type2, type2));
        comparator.compare(type1, new QuantitationType());
    }

}
