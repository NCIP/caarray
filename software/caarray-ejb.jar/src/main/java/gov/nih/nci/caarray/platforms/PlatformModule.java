//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.platforms;

import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.unparsed.UnparsedModule;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * Guice module aggregating all the platform implementation modules. After implementing array and design file handlers
 * for a new platform, create a Guice module to add their bindings and install it in this module.
 * 
 * A platform implementation module should add bindings for all its data handlers, design handlers, and array data type
 * descriptors. See one of the existing implementation modules for an example.
 * 
 * @author dkokotov
 */
public final class PlatformModule extends AbstractModule {
    private final Set<Module> modules = Sets.newHashSet();

    /**
     * Constructor, creates a new Platform module with only unparsed implementation module registered.
     */
    public PlatformModule() {
        addPlatform(new UnparsedModule());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        for (final Module module : this.modules) {
            install(module);
        }

        bind(FileTypeRegistry.class).to(FileTypeRegistryImpl.class);
    }

    /**
     * Register a new platform implementation module with this. The module will be installed when this module is
     * processed by Guice.
     * 
     * @param platformModule module to register
     */
    public void addPlatform(Module platformModule) {
        this.modules.add(platformModule);
    }
}
