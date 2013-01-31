//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.arraydesign;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD")
public class ArrayDesignDetailsServiceBeanTest extends AbstractCaarrayTest {

    private ArrayDesignDetailsService arrayDesignDetailsService;
    private LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() {
        ArrayDesignDetailsServiceBean arrayDesignDetailsServiceBean = new ArrayDesignDetailsServiceBean();
        arrayDesignDetailsServiceBean.setDaoFactory(daoFactoryStub);
        arrayDesignDetailsService = arrayDesignDetailsServiceBean;
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetDesignDetails() {
        ArrayDesign design = new ArrayDesign();
        design.setId(1L);
        ArrayDesignDetails designDetails = new ArrayDesignDetails();
        design.setDesignDetails(designDetails);
        daoFactoryStub.setDesignForRetrieval(design);
        assertEquals(designDetails, arrayDesignDetailsService.getDesignDetails(design));
        daoFactoryStub.setDesignForRetrieval(null);
        assertEquals(null, arrayDesignDetailsService.getDesignDetails(design));
    }

    static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalArrayDao arrayDao = new LocalArrayDao();

        void setDesignForRetrieval(ArrayDesign design) {
            arrayDao.design = design;
        }

        @Override
        public ArrayDao getArrayDao() {
            return arrayDao;
        }

    }

    static class LocalArrayDao extends ArrayDaoStub {

        private ArrayDesign design;

        @Override
        public ArrayDesign getArrayDesign(long id) {
            return design;
        }

    }

}
