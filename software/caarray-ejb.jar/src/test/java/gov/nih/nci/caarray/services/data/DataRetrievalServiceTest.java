//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

@SuppressWarnings("deprecation")
public class DataRetrievalServiceTest extends AbstractServiceTest {

    private DataRetrievalService service;
    private final ArrayDataServiceStub arrayDataService = new ArrayDataServiceStub();
    private final DaoFactoryStub daoFactory = new LocalDataFactoryStub();
    private final Hybridization hybridization1 = new Hybridization();
    private final Hybridization hybridization2 = new Hybridization();
    private final QuantitationType quantitationType1 = new QuantitationType();
    private final QuantitationType quantitationType2 = new QuantitationType();
    private final QuantitationType quantitationType3 = new QuantitationType();
    private final PhysicalProbe probe1 = new PhysicalProbe();
    private final PhysicalProbe probe2 = new PhysicalProbe();

    @Before
    public void setUp() throws Exception {
        setUpService();
        setUpData();
    }

    @SuppressWarnings("deprecation")
    private void setUpData() {
        this.quantitationType1.setId(1L);
        this.quantitationType1.setName("qt1");
        this.quantitationType1.setTypeClass(Float.class);
        this.quantitationType2.setId(2L);
        this.quantitationType2.setName("qt2");
        this.quantitationType2.setTypeClass(Integer.class);
        this.quantitationType3.setId(3L);
        this.quantitationType3.setName("qt3");
        this.quantitationType3.setTypeClass(String.class);
        this.hybridization1.setId(1L);
        setUpHybridization(this.hybridization1);
        this.hybridization2.setId(2L);
        setUpHybridization(this.hybridization2);
    }

    private void setUpHybridization(Hybridization hybridization) {
        final RawArrayData arrayData = new RawArrayData();
        final DataSet dataSet = new DataSet();
        dataSet.addHybridizationData(hybridization);
        dataSet.addQuantitationType(this.quantitationType1);
        dataSet.addQuantitationType(this.quantitationType2);
        dataSet.addQuantitationType(this.quantitationType3);
        dataSet.setDesignElementList(new DesignElementList());
        dataSet.getDesignElementList().getDesignElements().add(this.probe1);
        dataSet.getDesignElementList().getDesignElements().add(this.probe2);
        arrayData.setDataSet(dataSet);
        hybridization.addArrayData(arrayData);
    }

    private void setUpService() {
        final DataRetrievalServiceBean serviceBean = new DataRetrievalServiceBean();
        serviceBean.setSearchDao(this.daoFactory.getSearchDao());

        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, this.arrayDataService);
        this.service = serviceBean;
    }

    @Test
    @SuppressWarnings("PMD")
    public void testGetDataSet() {
        final DataRetrievalRequest request = new DataRetrievalRequest();
        try {
            this.service.getDataSet(request);
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        // Retrieve out of natural order to ensure elements are where requested by client
        request.addQuantitationType(this.quantitationType2);
        request.addQuantitationType(this.quantitationType1);
        request.addHybridization(this.hybridization2);
        request.addHybridization(this.hybridization1);
        final DataSet dataSet = this.service.getDataSet(request);
        assertEquals(2, dataSet.getQuantitationTypes().size());
        assertEquals(this.quantitationType2, dataSet.getQuantitationTypes().get(0));
        assertEquals(this.quantitationType1, dataSet.getQuantitationTypes().get(1));
        assertEquals(this.hybridization2, dataSet.getHybridizationDataList().get(0).getHybridization());
        assertEquals(2, dataSet.getHybridizationDataList().get(0).getColumns().size());
        assertEquals(this.quantitationType2, dataSet.getHybridizationDataList().get(0).getColumns().get(0)
                .getQuantitationType());
        assertEquals(this.quantitationType1, dataSet.getHybridizationDataList().get(0).getColumns().get(1)
                .getQuantitationType());
        assertEquals(this.hybridization1, dataSet.getHybridizationDataList().get(1).getHybridization());
        assertEquals(2, dataSet.getHybridizationDataList().get(1).getColumns().size());
        assertEquals(this.quantitationType2, dataSet.getHybridizationDataList().get(1).getColumns().get(0)
                .getQuantitationType());
        assertEquals(this.quantitationType1, dataSet.getHybridizationDataList().get(1).getColumns().get(1)
                .getQuantitationType());
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(2, dataSet.getDesignElementList().getDesignElements().size());
    }

    private class LocalDataFactoryStub extends DaoFactoryStub {
        @Override
        public SearchDao getSearchDao() {
            return new SearchDaoStub() {
                @Override
                public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
                    if (Hybridization.class.equals(entityClass)) {
                        if (entityId == 1L) {
                            return (T) DataRetrievalServiceTest.this.hybridization1;
                        } else if (entityId == 2L) {
                            return (T) DataRetrievalServiceTest.this.hybridization2;
                        }
                    }
                    return super.retrieve(entityClass, entityId);
                }
            };
        }
    }
}
