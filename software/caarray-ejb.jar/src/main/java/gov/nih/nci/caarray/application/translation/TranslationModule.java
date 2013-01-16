//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation;

import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslatorBean;

import com.google.inject.AbstractModule;

/**
 * Guice module for the file package.
 * 
 * @author jscott
 */
public class TranslationModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(MageTabTranslator.class).to(MageTabTranslatorBean.class);
    }
}
