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
package gov.nih.nci.caarray.services.data;

import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataRetrievalRequest;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;
import gov.nih.nci.caarray.services.StorageInterceptor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.TransactionTimeout;

import com.google.inject.Inject;

/**
 * Implementation for remote API data retrieval.
 */
@Remote(DataRetrievalService.class)
@Stateless
@PermitAll
/*
 * Transaction required for these "getter" methods because database changes may occur as a side effect.
 */
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@TransactionTimeout(DataRetrievalServiceBean.TIMEOUT_SECONDS)
@Interceptors({AuthorizationInterceptor.class, HibernateSessionInterceptor.class, DataSetConfiguringInterceptor.class,
        InjectionInterceptor.class, StorageInterceptor.class })
public class DataRetrievalServiceBean implements DataRetrievalService {
    private static final Logger LOG = Logger.getLogger(DataRetrievalServiceBean.class);

    static final int TIMEOUT_SECONDS = 1800;
    private SearchDao searchDao;

    /**
     * 
     * @param searchDao the SearchDao dependency
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSet getDataSet(DataRetrievalRequest request) {
        LOG.info("Received data retrieval request");
        checkRequest(request);
        final List<DataSet> dataSets = getDataSets(request);
        final DataSet mergedDataSet = createMergedDataSet(dataSets, request);
        LOG.info("Retrieved " + mergedDataSet.getHybridizationDataList().size() + " hybridization data elements, "
                + mergedDataSet.getQuantitationTypes().size() + " quant types");
        return mergedDataSet;
    }

    private List<DataSet> getDataSets(DataRetrievalRequest request) {
        final List<AbstractArrayData> arrayDatas = getArrayDatas(request);
        final List<DataSet> dataSets = new ArrayList<DataSet>(arrayDatas.size());
        for (final AbstractArrayData data : arrayDatas) {
            dataSets.add(data.getDataSet());
        }
        return dataSets;
    }

    private DataSet createMergedDataSet(List<DataSet> dataSets, DataRetrievalRequest request) {
        final DataSet dataSet = new DataSet();
        dataSet.getQuantitationTypes().addAll(request.getQuantitationTypes());
        addDesignElementList(dataSet, dataSets);
        addHybridizationDatas(dataSet, dataSets, request);
        return dataSet;
    }

    private void addDesignElementList(DataSet dataSet, List<DataSet> dataSets) {
        if (dataSets.isEmpty()) {
            dataSet.setDesignElementList(new DesignElementList());
        } else if (allDesignElementListsAreConsistent(dataSets)) {
            dataSet.setDesignElementList(dataSets.get(0).getDesignElementList());
        } else {
            throw new IllegalArgumentException("The DataSet requested data from inconsistent design elements");
        }
    }

    private boolean allDesignElementListsAreConsistent(List<DataSet> dataSets) {
        final DesignElementList firstList = dataSets.get(0).getDesignElementList();
        for (int i = 1; i < dataSets.size(); i++) {
            final DesignElementList nextList = dataSets.get(i).getDesignElementList();
            if (!ListUtils.isEqualList(firstList.getDesignElements(), nextList.getDesignElements())) {
                return false;
            }
        }
        return true;
    }

    private void addHybridizationDatas(DataSet dataSet, List<DataSet> dataSets, DataRetrievalRequest request) {
        for (final DataSet nextDataSet : dataSets) {
            for (final HybridizationData nextHybridizationData : nextDataSet.getHybridizationDataList()) {
                addHybridizationData(dataSet, nextHybridizationData, request);
            }
        }
    }

    private void addHybridizationData(DataSet dataSet, HybridizationData copyFromHybridizationData,
            DataRetrievalRequest request) {
        final HybridizationData hybridizationData = new HybridizationData();
        hybridizationData.setHybridization(copyFromHybridizationData.getHybridization());
        hybridizationData.setId(copyFromHybridizationData.getId());
        dataSet.getHybridizationDataList().add(hybridizationData);
        copyColumns(hybridizationData, copyFromHybridizationData, request.getQuantitationTypes());
        hybridizationData.setDataSet(dataSet);
    }

    private void copyColumns(HybridizationData hybridizationData, HybridizationData copyFromHybridizationData,
            List<QuantitationType> quantitationTypes) {
        for (final QuantitationType type : quantitationTypes) {
            hybridizationData.getColumns().add(copyFromHybridizationData.getColumn(type));
        }
    }

    private void checkRequest(DataRetrievalRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("DataRetrievalRequest was null");
        } else if (request.getHybridizations().isEmpty()) {
            throw new IllegalArgumentException("DataRetrievalRequest didn't specify Hybridizations");
        } else if (request.getQuantitationTypes().isEmpty()) {
            throw new IllegalArgumentException("DataRetrievalRequest didn't specify QuantitationTypes");
        } else if (request.getHybridizations().contains(null)) {
            throw new IllegalArgumentException("DataRetrievalRequest Hybridizations included a null value");
        } else if (request.getQuantitationTypes().contains(null)) {
            throw new IllegalArgumentException("DataRetrievalRequest QuantitationTypes included a null value");
        }
    }

    private List<AbstractArrayData> getArrayDatas(DataRetrievalRequest request) {
        final List<Hybridization> hybridizations = getHybridizations(request);
        return getArrayDatas(hybridizations, request.getQuantitationTypes());
    }

    private List<AbstractArrayData> getArrayDatas(List<Hybridization> hybridizations,
            List<QuantitationType> quantitationTypes) {
        final List<AbstractArrayData> arrayDatas = new ArrayList<AbstractArrayData>(hybridizations.size());
        for (final Hybridization hybridization : hybridizations) {
            addArrayDatas(arrayDatas, hybridization, quantitationTypes);
        }
        return arrayDatas;
    }

    private void addArrayDatas(List<AbstractArrayData> arrayDatas, Hybridization hybridization,
            List<QuantitationType> quantitationTypes) {
        for (final RawArrayData rawArrayData : hybridization.getRawDataCollection()) {
            if (shouldAddData(arrayDatas, rawArrayData, quantitationTypes)) {
                arrayDatas.add(rawArrayData);
            }
        }
        for (final DerivedArrayData derivedArrayData : hybridization.getDerivedDataCollection()) {
            if (shouldAddData(arrayDatas, derivedArrayData, quantitationTypes)) {
                arrayDatas.add(derivedArrayData);
            }
        }
    }

    private boolean shouldAddData(List<AbstractArrayData> arrayDatas, AbstractArrayData arrayData,
            List<QuantitationType> quantitationTypes) {
        return arrayData != null && !arrayDatas.contains(arrayData) && containsAllTypes(arrayData, quantitationTypes);
    }

    private boolean containsAllTypes(AbstractArrayData arrayData, List<QuantitationType> quantitationTypes) {
        return arrayData.getDataSet() != null
                && arrayData.getDataSet().getQuantitationTypes().containsAll(quantitationTypes);
    }

    private List<Hybridization> getHybridizations(DataRetrievalRequest request) {
        final List<Hybridization> hybridizations = new ArrayList<Hybridization>(request.getHybridizations().size());
        for (final Hybridization hybridization : request.getHybridizations()) {
            hybridizations.add(this.searchDao.retrieve(Hybridization.class, hybridization.getId()));
        }
        return hybridizations;
    }
}
