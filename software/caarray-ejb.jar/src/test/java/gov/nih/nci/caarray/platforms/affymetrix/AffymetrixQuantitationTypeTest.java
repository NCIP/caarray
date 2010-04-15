package gov.nih.nci.caarray.platforms.affymetrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.platforms.affymetrix.AffymetrixCelQuantitationType;

import org.junit.Test;

public class AffymetrixQuantitationTypeTest extends AbstractCaarrayTest {

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
}
