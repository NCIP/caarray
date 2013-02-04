//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.staticinjection;

import com.google.inject.AbstractModule;

/**
 * @author jscott
 * 
 */
public class CaArrayCommonStaticInjectionModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        requestStaticInjection(gov.nih.nci.caarray.dao.CaArrayDaoFactoryImpl.class);
        requestStaticInjection(gov.nih.nci.caarray.domain.file.CaArrayFile.class);
        requestStaticInjection(gov.nih.nci.caarray.domain.permissions.CollaboratorGroup.class);
        requestStaticInjection(gov.nih.nci.caarray.security.AuthorizationManagerExtensions.class);
        requestStaticInjection(gov.nih.nci.caarray.security.SecurityUtils.class);
        requestStaticInjection(gov.nih.nci.caarray.validation.UniqueConstraintValidator.class);
    }
}
