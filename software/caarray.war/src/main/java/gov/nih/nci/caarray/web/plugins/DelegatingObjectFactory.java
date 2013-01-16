//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.ObjectFactoryDestroyable;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Struts2 ObjectFactory implementation that can load classes from OSGi bundles if they are not found by the root
 * ObjectFactory.
 * 
 * Adapted from the Struts2 OSGi integration module.
 * 
 * @author dkokotov
 */
public class DelegatingObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {
    private static final Logger LOG = Logger.getLogger(DelegatingObjectFactory.class);

    private ObjectFactory delegateObjectFactory;
    private OsgiConfigurationProvider osgiConfigurationProvider;
    private Container container;
    private String delegate = "struts";

    /**
     * @param delegateObjectFactory the root ObjectFactory, which will first try to load classes.
     */
    @Inject("struts.objectFactory.delegate")
    public void setDelegateObjectFactory(String delegateObjectFactory) {
        this.delegate = delegateObjectFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNoArgConstructorRequired() {
        return this.delegateObjectFactory.isNoArgConstructorRequired();
    }

    private void initDelegate() {
        if (this.delegateObjectFactory == null) {
            this.delegateObjectFactory = this.container.getInstance(ObjectFactory.class, this.delegate);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        initDelegate();
        return this.delegateObjectFactory.buildBean(clazz, extraContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal)
            throws Exception {
        initDelegate();
        try {
            return this.delegateObjectFactory.buildBean(className, extraContext, injectInternal);
        } catch (final Exception e) {
            LOG.warn("Could not build bean using the delegate object factory: ", e);
            final DefaultBundleAccessor accessor = DefaultBundleAccessor.getInstance();
            final Class<?> cls = accessor.loadClass(className);
            final Object object = cls.newInstance();
            if (injectInternal) {
                injectInternalBeans(object);
            }
            return object;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getClassInstance(String className) throws ClassNotFoundException {
        initDelegate();
        try {
            return this.delegateObjectFactory.getClassInstance(className);
        } catch (final Exception e) {
            return DefaultBundleAccessor.getInstance().loadClass(className);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (this.osgiConfigurationProvider != null) {
            this.osgiConfigurationProvider.destroy();
        }
    }

    /**
     * @param osgiConfigurationProvider the OSGi aware configuration provider
     */
    @Inject("osgi")
    public void setOsgiConfigurationProvider(PackageProvider osgiConfigurationProvider) {
        this.osgiConfigurationProvider = (OsgiConfigurationProvider) osgiConfigurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Inject
    public void setContainer(Container container) {
        super.setContainer(container);
        this.container = container;
    }
}
