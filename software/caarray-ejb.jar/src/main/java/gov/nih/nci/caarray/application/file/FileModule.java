//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.domain.project.DefaultJobMessageSenderImpl;
import gov.nih.nci.caarray.util.UsernameHolderModule;

import com.google.inject.AbstractModule;

/**
 * Guice module for the file package.
 * 
 * @author jscott
 */
public class FileModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // install(new TranslationModule());
        install(new UsernameHolderModule());
        
        bind(MageTabImporter.class).to(MageTabImporterImpl.class);       
        bind(ArrayDataImporter.class).to(ArrayDataImporterImpl.class);       
        bind(DefaultJobMessageSenderImpl.class).to(JobMessageSenderImpl.class);
        bind(FileManagementJobSubmitter.class).to(JobQueueSubmitter.class);
        bind(JobFactory.class).to(JobFactoryImpl.class);
    }
}
