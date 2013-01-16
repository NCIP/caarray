//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.application.JtaSessionTransactionManager;

import com.google.inject.AbstractModule;

/**
 * Module configuring JTA transactions for platforms.
 * 
 * @author dkokotov
 */
public class PlatformJtaTransactionModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(SessionTransactionManager.class).to(JtaSessionTransactionManager.class);
    }
}
