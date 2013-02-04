//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.plugins.affymetrix.AffymetrixArrayDataTypes;

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
