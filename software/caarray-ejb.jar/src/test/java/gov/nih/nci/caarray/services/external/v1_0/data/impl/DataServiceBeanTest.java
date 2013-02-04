//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.services.external.v1_0.data.impl;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;
import gov.nih.nci.caarray.external.v1_0.query.DataSetRequest;
import gov.nih.nci.caarray.external.v1_0.sample.Hybridization;
import gov.nih.nci.caarray.services.external.BeanMapperLookup;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import net.sf.dozer.util.mapping.MapperIF;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;

/**
 * Unit tests for DataServiceBean.
 */
public class DataServiceBeanTest {

    private DataServiceBean bean;
    private GenericDataService genericDataService;
    private ParsedDataPersister parsedDataPersister;
    
    @Before
    public void before() {
        bean = new DataServiceBean();
        mockHibernate();
        mockBeanMapper();
        mockGenericDataService();
        mockParsedDataPersister();
    }

    private void mockHibernate() {
        Session session = mock(Session.class);
        CaArrayHibernateHelper hibernateHelper = mock(CaArrayHibernateHelper.class);
        when(hibernateHelper.getCurrentSession()).thenReturn(session);
        bean.setHibernateHelper(hibernateHelper);
    }
    
    private void mockBeanMapper() {
        MapperIF mapper = mock(MapperIF.class);
        bean.setMapperVersionKey("testVersion");
        BeanMapperLookup.addMapper("testVersion", mapper);
    }

    private void mockGenericDataService() {
        genericDataService = mock(GenericDataService.class);
        ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(GenericDataService.JNDI_NAME, genericDataService);
    }
    
    private void mockParsedDataPersister() {
        parsedDataPersister = mock(ParsedDataPersister.class);
        bean.setParsedDataPersister(parsedDataPersister);
    }

    @Test
    public void abstractDataColumnInitialization() throws Exception {
        DataSetRequest request = createIntegerColumnRequest();
        bean.getDataSet(request);
        verify(parsedDataPersister).loadFromStorage(any(gov.nih.nci.caarray.domain.data.AbstractDataColumn.class));
    }

    private DataSetRequest createIntegerColumnRequest() {
        DataSetRequest request = new DataSetRequest();
        
        Hybridization hyb = new Hybridization();
        hyb.setId("1");
        request.getHybridizations().add(setupReferenceForEntity(hyb));
        
        gov.nih.nci.caarray.domain.hybridization.Hybridization hybMock = 
                setupGenericDataService(hyb, gov.nih.nci.caarray.domain.hybridization.Hybridization.class);
        setupMockDataSetResult(hybMock);
        
        QuantitationType quantType = new QuantitationType();
        quantType.setId("2");
        request.getQuantitationTypes().add(setupReferenceForEntity(quantType));
        setupGenericDataService(quantType, gov.nih.nci.caarray.domain.data.QuantitationType.class);
        
        return request;
    }

    /**
     * Creates the (elaborate) mock object graph for a hybridization.  This is the object graph explored
     * by the DataServiceBean to construct the result back to its client.  Since it does not delegate
     * to an external class, we need a whole lot of objects and methods.
     */
    private void setupMockDataSetResult(
            gov.nih.nci.caarray.domain.hybridization.Hybridization hybMock) {
        gov.nih.nci.caarray.domain.data.HybridizationData mockHybData = 
                mock(gov.nih.nci.caarray.domain.data.HybridizationData.class);
        gov.nih.nci.caarray.domain.data.AbstractDataColumn mockColumn =
                mock(gov.nih.nci.caarray.domain.data.AbstractDataColumn.class);
        gov.nih.nci.caarray.domain.data.RawArrayData mockRawArrayData =
                mock(gov.nih.nci.caarray.domain.data.RawArrayData.class);
        gov.nih.nci.caarray.domain.data.DataSet mockDataSet =
                mock(gov.nih.nci.caarray.domain.data.DataSet.class);
        gov.nih.nci.caarray.domain.data.DesignElementList mockDesignElementList =
                mock(gov.nih.nci.caarray.domain.data.DesignElementList.class);
        List<gov.nih.nci.caarray.domain.data.QuantitationType> mockTypeList =
                mock(List.class);

        when(hybMock.getHybridizationData()).thenReturn(ImmutableSet.of(mockHybData));
        when(hybMock.getRawDataCollection()).thenReturn(ImmutableSet.of(mockRawArrayData));

        when(mockHybData.getColumn(any(gov.nih.nci.caarray.domain.data.QuantitationType.class))).thenReturn(mockColumn);

        when(mockRawArrayData.getDataSet()).thenReturn(mockDataSet);
        
        when(mockDataSet.getDesignElementList()).thenReturn(mockDesignElementList);                
        when(mockDataSet.getQuantitationTypes()).thenReturn(mockTypeList);        
        when(mockDataSet.getHybridizationDataList()).thenReturn(ImmutableList.of(mockHybData));
        
        when(mockDesignElementList.getDesignElements()).thenReturn(Collections.EMPTY_LIST);
        
        when(mockTypeList.containsAll(any(Collection.class))).thenReturn(true);
    }
        
    private <T extends PersistentObject> T setupGenericDataService(AbstractCaArrayEntity hyb, 
            Class<T> poClass) {
        Long id = Long.parseLong(hyb.getId());
        T po = mock(poClass);
        when(po.getId()).thenReturn(id);
        when(genericDataService.getPersistentObject(eq(poClass), eq(id))).thenReturn(po);
        return po;
    }

    private CaArrayEntityReference setupReferenceForEntity(AbstractCaArrayEntity entity) {
        LSID lsid = new LSID(entity.getClass().getName() + ":" + entity.getId());
        CaArrayEntityReference result = new CaArrayEntityReference(lsid.toString());
        
        return result;
    }
}
