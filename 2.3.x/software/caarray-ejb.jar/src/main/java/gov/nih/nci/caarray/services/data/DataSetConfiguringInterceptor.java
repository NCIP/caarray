//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.data;

import gov.nih.nci.caarray.domain.MaxSerializableSize;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.util.EntityPruner;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.hibernate.Hibernate;

/**
 * Ensures that the <code>Hybridizations</code> within a <code>DataSet</code> are made leaves,
 * preventing lazy initialization errors.
 */
public class DataSetConfiguringInterceptor {
    /**
     * Ensures that any object returned and its direct associated entities are loaded.
     *
     * @param invContext the method context
     * @return the method result
     * @throws Exception if invoking the method throws an exception.
     */
    @AroundInvoke
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // method invocation wrapper requires throws Exception
    public Object prepareReturnValue(InvocationContext invContext) throws Exception {
        Object returnValue = invContext.proceed();
        if (returnValue instanceof DataSet) {
            prepareDataSet((DataSet) returnValue);
        }

        // TODO see EntityConfiguringInterceptor for discussion of hibernate flush/clear issue

        // keep hibernate from performing write behind of all the cutting we just did
        HibernateUtil.getCurrentSession().clear();

        return returnValue;
    }

    private void prepareDataSet(DataSet dataSet) {
        // Need to perform our own cutting here - the default isn't what users will expect.
        EntityPruner pruner = new EntityPruner();
        for (HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
            pruner.makeLeaf(hybridizationData.getHybridization());
            pruner.makeLeaf(hybridizationData.getLabeledExtract());
            hybridizationData.setDataSet(null);
            for (AbstractDataColumn adc : hybridizationData.getColumns()) {
                adc.setHybridizationData(null);
                Hibernate.initialize(adc.getQuantitationType());
            }
        }
        MaxSerializableSize maxSize = getMaxSerializableDesignElements();
        if (maxSize != null && maxSize.value() < dataSet.getDesignElementList().getDesignElements().size()) {
            throw new IllegalStateException("Couldn't prepare result for serialization: too many design elements");
        }
        for (AbstractDesignElement ade : dataSet.getDesignElementList().getDesignElements()) {
            pruner.makeLeaf(ade);
        }
        Hibernate.initialize(dataSet.getQuantitationTypes());
    }

    private MaxSerializableSize getMaxSerializableDesignElements() {
        try {
            Method designElementsGetter = DesignElementList.class.getDeclaredMethod("getDesignElements");
            return designElementsGetter.getAnnotation(MaxSerializableSize.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
