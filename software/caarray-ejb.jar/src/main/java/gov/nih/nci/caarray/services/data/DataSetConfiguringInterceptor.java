//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.data;

import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.MaxSerializableSize;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.services.EntityPruner;
import gov.nih.nci.caarray.util.CaArrayHibernateHelper;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.hibernate.Hibernate;

import com.google.inject.Inject;

/**
 * Ensures that the <code>Hybridizations</code> within a <code>DataSet</code> are made leaves, preventing lazy
 * initialization errors.
 */
public class DataSetConfiguringInterceptor {
    @Inject
    private static CaArrayHibernateHelper hibernateHelper;
    @Inject
    private static ParsedDataPersister parsedDataPersister;

    /**
     * Ensures that any object returned and its direct associated entities are loaded.
     * 
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // method invocation wrapper requires throws Exception
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        final Object returnValue = invContext.proceed();
        if (returnValue instanceof DataSet) {
            prepareDataSet((DataSet) returnValue);
        }

        // TODO see EntityConfiguringInterceptor for discussion of hibernate flush/clear issue

        // keep hibernate from performing write behind of all the cutting we just did
        hibernateHelper.getCurrentSession().clear();

        return returnValue;
    }

    private void prepareDataSet(DataSet dataSet) {
        // Need to perform our own cutting here - the default isn't what users will expect.
        final EntityPruner pruner = new EntityPruner();
        for (final HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
            pruner.makeLeaf(hybridizationData.getHybridization());
            pruner.makeLeaf(hybridizationData.getLabeledExtract());
            hybridizationData.setDataSet(null);
            for (final AbstractDataColumn adc : hybridizationData.getColumns()) {
                adc.setHybridizationData(null);
                Hibernate.initialize(adc.getQuantitationType());
                // make sure the blobs are converted to value array.
                parsedDataPersister.loadFromStorage(adc);
            }
        }
        final MaxSerializableSize maxSize = getMaxSerializableDesignElements();
        if (maxSize != null && maxSize.value() < dataSet.getDesignElementList().getDesignElements().size()) {
            throw new IllegalStateException("Couldn't prepare result for serialization: too many design elements");
        }
        for (final AbstractDesignElement ade : dataSet.getDesignElementList().getDesignElements()) {
            pruner.makeLeaf(ade);
        }
        Hibernate.initialize(dataSet.getQuantitationTypes());
    }

    private MaxSerializableSize getMaxSerializableDesignElements() {
        try {
            final Method designElementsGetter = DesignElementList.class.getDeclaredMethod("getDesignElements");
            return designElementsGetter.getAnnotation(MaxSerializableSize.class);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }
}
