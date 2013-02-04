//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.staticinjection;

/**
 * @author jscott
 * 
 */
public class CaArrayEjbStaticInjectionModule extends CaArrayCommonStaticInjectionModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        super.configure();
        requestStaticInjection(gov.nih.nci.caarray.services.EntityPruner.class);
        requestStaticInjection(gov.nih.nci.caarray.application.ConfigurationHelper.class);
        requestStaticInjection(gov.nih.nci.caarray.services.data.DataSetConfiguringInterceptor.class);
        requestStaticInjection(gov.nih.nci.caarray.services.EntityConfiguringInterceptor.class);
        requestStaticInjection(gov.nih.nci.caarray.services.HibernateSessionInterceptor.class);
    }
}
