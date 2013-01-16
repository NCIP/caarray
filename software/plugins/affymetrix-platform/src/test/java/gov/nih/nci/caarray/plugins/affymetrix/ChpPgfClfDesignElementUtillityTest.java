//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.plugins.affymetrix.ChpPgfClfDesignElementListUtility;

import org.junit.Before;
import org.junit.Test;

/**
 * @author dkokotov
 *
 */
public class ChpPgfClfDesignElementUtillityTest extends AbstractCaarrayTest {
    private ChpPgfClfDesignElementListUtility chpUtility;    

    @Before
    public void setup() {
        ArrayDao arrayDao = new ArrayDaoStub() {
            public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                if (lsidObjectId.equals("LogicalProbes.DummyTestArrayDesign1")) {
                    DesignElementList list = new DesignElementList();
                    list.setDesignElementTypeEnum(DesignElementType.LOGICAL_PROBE);
                    list.getDesignElements().add(new LogicalProbe(null));
                    list.getDesignElements().add(new LogicalProbe(null));
                    return list;
                }
                return null;
            }
        };
        chpUtility = new ChpPgfClfDesignElementListUtility(arrayDao, new SessionTransactionManagerNoOpImpl());
    }
    
    @Test
    public void testGetDesignElementList() {
        ArrayDesign design = new ArrayDesign();
        design.setName("DummyTestArrayDesign1");
        design.setVersion("2.0");
        design.setLsidForEntity("authority:namespace:DummyTestArrayDesign1");
        
        DesignElementList designElementList = chpUtility.getDesignElementList(design);
        assertEquals(DesignElementType.LOGICAL_PROBE, designElementList.getDesignElementTypeEnum());
        assertEquals(2, designElementList.getDesignElements().size());        
    }
}
