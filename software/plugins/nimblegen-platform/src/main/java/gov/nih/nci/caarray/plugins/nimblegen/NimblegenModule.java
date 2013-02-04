//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.nimblegen;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Module class for Nimblegen platform support.
 * 
 * @author dkokotov
 */
public class NimblegenModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        final Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(), DataFileHandler.class);
        dataFileBinder.addBinding().to(PairDataHandler.class);

        // design files
        final Multibinder<DesignFileHandler> designFileBinder = Multibinder.newSetBinder(binder(),
                DesignFileHandler.class);
        designFileBinder.addBinding().to(NdfHandler.class);

        // array data descriptors
        final Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                ArrayDataTypeDescriptor.class);
        for (final ArrayDataTypeDescriptor desc : NimblegenArrayDataTypes.values()) {
            arrayDataDescriptorBinder.addBinding().toInstance(desc);
        }
    }
}
