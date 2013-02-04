//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.util;

import com.google.inject.AbstractModule;

/**
 * Guice module for the util package.

 * @author tparnell
 */
public class UtilModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        bind(CaArrayFileSetSplitter.class).to(CaArrayFileSetSplitterImpl.class);
    }

}
