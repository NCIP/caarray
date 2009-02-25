package gov.nih.nci.caarray.application.arraydata.affymetrix;

import static org.junit.Assert.*;

import java.util.Comparator;

import gov.nih.nci.caarray.domain.data.QuantitationType;

import org.junit.Test;

public class AffymetrixCelQuantitationTypeTest {

    @Test
    public void testIsEquivalent() {
        QuantitationType type = createType(AffymetrixCelQuantitationType.CEL_INTENSITY);
        assertFalse(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(null));
        assertTrue(AffymetrixCelQuantitationType.CEL_INTENSITY.isEquivalent(type));
        assertFalse(AffymetrixCelQuantitationType.CEL_INTENSITY_STD_DEV.isEquivalent(type));
    }

    private QuantitationType createType(AffymetrixCelQuantitationType descriptor) {
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
