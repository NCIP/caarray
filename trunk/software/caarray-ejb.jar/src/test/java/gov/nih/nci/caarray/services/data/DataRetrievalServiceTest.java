//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydata.ArrayDataService;
import gov.nih.nci.caarray.application.arraydata.ArrayDataServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class DataRetrievalServiceTest extends AbstractServiceTest {

    private DataRetrievalService service;
    private final LocalArrayDataServiceStub arrayDataService = new LocalArrayDataServiceStub();
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
        quantitationType1.setId(1L);
        quantitationType1.setName("qt1");
        quantitationType1.setTypeClass(Float.class);
        quantitationType2.setId(2L);
        quantitationType2.setName("qt2");
        quantitationType2.setTypeClass(Integer.class);
        quantitationType3.setId(3L);
        quantitationType3.setName("qt3");
        quantitationType3.setTypeClass(String.class);
        hybridization1.setId(1L);
        setUpHybridization(hybridization1);
        hybridization2.setId(2L);
        setUpHybridization(hybridization2);
    }

    private void setUpHybridization(Hybridization hybridization) {
        RawArrayData arrayData = new RawArrayData();
        DataSet dataSet = new DataSet();
        dataSet.addHybridizationData(hybridization);
        dataSet.addQuantitationType(quantitationType1);
        dataSet.addQuantitationType(quantitationType2);
        dataSet.addQuantitationType(quantitationType3);
        dataSet.setDesignElementList(new DesignElementList());
        dataSet.getDesignElementList().getDesignElements().add(probe1);
        dataSet.getDesignElementList().getDesignElements().add(probe2);
        arrayData.setDataSet(dataSet);
        hybridization.addRawArrayData(arrayData);
    }

    private void setUpService() {
        DataRetrievalServiceBean serviceBean = new DataRetrievalServiceBean();
        serviceBean.setDaoFactory(daoFactory);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(ArrayDataService.JNDI_NAME, arrayDataService);
        service = serviceBean;
    }

    @Test
    @SuppressWarnings("PMD")
    public void testGetDataSet() {
        DataRetrievalRequest request = new DataRetrievalRequest();
        try {
            service.getDataSet(request);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // Retrieve out of natural order to ensure elements are where requested by client
        request.addQuantitationType(quantitationType2);
        request.addQuantitationType(quantitationType1);
        request.addHybridization(hybridization2);
        request.addHybridization(hybridization1);
        DataSet dataSet = service.getDataSet(request);
        assertEquals(2, dataSet.getQuantitationTypes().size());
        assertEquals(quantitationType2, dataSet.getQuantitationTypes().get(0));
        assertEquals(quantitationType1, dataSet.getQuantitationTypes().get(1));
        assertEquals(hybridization2, dataSet.getHybridizationDataList().get(0).getHybridization());
        assertEquals(2, dataSet.getHybridizationDataList().get(0).getColumns().size());
        assertEquals(quantitationType2, dataSet.getHybridizationDataList().get(0).getColumns().get(0).getQuantitationType());
        assertEquals(quantitationType1, dataSet.getHybridizationDataList().get(0).getColumns().get(1).getQuantitationType());
        assertEquals(hybridization1, dataSet.getHybridizationDataList().get(1).getHybridization());
        assertEquals(2, dataSet.getHybridizationDataList().get(1).getColumns().size());
        assertEquals(quantitationType2, dataSet.getHybridizationDataList().get(1).getColumns().get(0).getQuantitationType());
        assertEquals(quantitationType1, dataSet.getHybridizationDataList().get(1).getColumns().get(1).getQuantitationType());
        assertNotNull(dataSet.getDesignElementList());
        assertEquals(2, dataSet.getDesignElementList().getDesignElements().size());
    }


    private class LocalDataFactoryStub extends DaoFactoryStub {

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

                @Override
                public Hybridization getHybridization(Long id) {
                    if (id == 1L) {
                        return hybridization1;
                    } else if (id == 2L) {
                        return hybridization2;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            };
        }

    }

    private static class LocalArrayDataServiceStub extends ArrayDataServiceStub {

        @Override
        public DataSet getData(AbstractArrayData arrayData, List<QuantitationType> types) {
            return arrayData.getDataSet();
        }

    }

}
