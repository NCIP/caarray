//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.arraydesign;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD")
public class ArrayDesignDetailsServiceBeanTest extends AbstractServiceTest {

    private ArrayDesignDetailsService arrayDesignDetailsService;
    private final LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();

    @Before
    public void setUp() {
        final ArrayDao arrayDao = this.daoFactoryStub.getArrayDao();

        final ArrayDesignDetailsServiceBean arrayDesignDetailsServiceBean = new ArrayDesignDetailsServiceBean();
        arrayDesignDetailsServiceBean.setArrayDao(arrayDao);
        this.arrayDesignDetailsService = arrayDesignDetailsServiceBean;
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetDesignDetails() {
        final ArrayDesign design = new ArrayDesign();
        design.setId(1L);
        final ArrayDesignDetails designDetails = new ArrayDesignDetails();
        design.setDesignDetails(designDetails);
        this.daoFactoryStub.setDesignForRetrieval(design);
        assertEquals(designDetails, this.arrayDesignDetailsService.getDesignDetails(design));
        this.daoFactoryStub.setDesignForRetrieval(null);
        assertEquals(null, this.arrayDesignDetailsService.getDesignDetails(design));
    }

    static class LocalDaoFactoryStub extends DaoFactoryStub {

        LocalArrayDao arrayDao = new LocalArrayDao();

        void setDesignForRetrieval(ArrayDesign design) {
            this.arrayDao.design = design;
        }

        @Override
        public ArrayDao getArrayDao() {
            return this.arrayDao;
        }

    }

    static class LocalArrayDao extends ArrayDaoStub {

        private ArrayDesign design;

        @Override
        public ArrayDesign getArrayDesign(long id) {
            return this.design;
        }

    }

}
