/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
