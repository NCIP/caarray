//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.copynumberconverter;

import com.google.inject.AbstractModule;

/**
 * Guice module for the copynumberconverter package.
 * 
 * @author dharley
 */
public class ConversionModule extends AbstractModule {
    
    /**
     * {@inheritDoc}
     */
    @Override 
    protected void configure() {
      bind(CopyNumberDocumentConversionProcess.class).to(CopyNumberDocumentConversionProcessImpl.class);
    }
  }
