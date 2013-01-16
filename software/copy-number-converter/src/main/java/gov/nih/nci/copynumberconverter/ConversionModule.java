//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
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
