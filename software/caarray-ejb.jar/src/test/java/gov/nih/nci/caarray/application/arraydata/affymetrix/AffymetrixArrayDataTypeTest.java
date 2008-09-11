package gov.nih.nci.caarray.application.arraydata.affymetrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.data.ArrayDataType;

import org.junit.Test;

public class AffymetrixArrayDataTypeTest extends AbstractCaarrayTest {

    @Test
    public void testIsEquivalent() {
        ArrayDataType type = createType(AffymetrixArrayDataTypes.AFFYMETRIX_CEL);
        assertFalse(AffymetrixArrayDataTypes.AFFYMETRIX_CEL.isEquivalent(null));
        assertTrue(AffymetrixArrayDataTypes.AFFYMETRIX_CEL.isEquivalent(type));
        assertFalse(AffymetrixArrayDataTypes.AFFYMETRIX_SNP_CHP.isEquivalent(type));
    }

    private ArrayDataType createType(AffymetrixArrayDataTypes descriptor) {
        ArrayDataType type = new ArrayDataType();
        type.setName(descriptor.getName());
        type.setVersion(descriptor.getVersion());
        return type;
    }
}
