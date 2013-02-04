//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms.unparsed;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * @author dkokotov
 */
public class UnparsedModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        // data files
        final Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(), DataFileHandler.class);
        dataFileBinder.addBinding().to(UnparsedDataHandler.class);

        // special fallback handler used to handle normally parsable files that can't temporarily be parsed due to
        // missing or faulty designs. we bind it separately.
        bind(FallbackUnparsedDataHandler.class);

        // design files
        final Multibinder<DesignFileHandler> designFileBinder =
                Multibinder.newSetBinder(binder(), DesignFileHandler.class);
        designFileBinder.addBinding().to(UnparsedArrayDesignFileHandler.class);

        // array data descriptors
        final Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder =
                Multibinder.newSetBinder(binder(), ArrayDataTypeDescriptor.class);
        arrayDataDescriptorBinder.addBinding().toInstance(UnsupportedDataFormatDescriptor.INSTANCE);
    }
}
