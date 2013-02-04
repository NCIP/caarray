//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
