//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.services.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.injection.InjectorFactory;

import javax.interceptor.InvocationContext;

import org.hibernate.Transaction;
import org.junit.Test;

import com.google.inject.Inject;

/**
 * 
 * @author gax
 */
public class DataSetConfiguringInterceptorIntegrationTest extends AbstractServiceIntegrationTest {

    @Inject
    private final SearchDao searchDao = null;
    @Inject
    private final DataSetConfiguringInterceptor interceptor = null;
    @Inject
    private ParsedDataPersister persister;

    @Test
    public void testBlobInit() throws Exception {
        InjectorFactory.getInjector().injectMembers(this);

        Transaction tx = this.hibernateHelper.beginTransaction();
        DataSet ds = new DataSet();
        final DesignElementList l = new DesignElementList();
        ds.setDesignElementList(l);
        final HybridizationData data = new HybridizationData();
        ds.getHybridizationDataList().add(data);
        IntegerColumn col = new IntegerColumn();
        col.setHybridizationData(data);
        data.getColumns().add(col);
        col.initializeArray(1);
        col.getValues()[0] = 99;
        this.persister.saveToStorage(col);
        this.searchDao.save(ds);
        this.searchDao.flushSession();
        this.hibernateHelper.unbindAndCleanupSession();
        tx.commit();

        System.out.println("Data handle: " + col.getDataHandle());

        tx = this.hibernateHelper.beginTransaction();
        ds = this.searchDao.retrieve(DataSet.class, ds.getId());

        final InvocationContext ctx = mock(InvocationContext.class);
        when(ctx.proceed()).thenReturn(ds);

        ds = (DataSet) this.interceptor.prepareReturnValue(ctx);

        this.hibernateHelper.unbindAndCleanupSession();
        tx.commit();

        // let's see if we can get to the values now that the we cant be lazy.
        col = (IntegerColumn) ds.getHybridizationDataList().get(0).getColumns().get(0);
        final int[] values = col.getValues();
        assertNotNull(values);
        assertEquals(99, values[0]);

    }

}
