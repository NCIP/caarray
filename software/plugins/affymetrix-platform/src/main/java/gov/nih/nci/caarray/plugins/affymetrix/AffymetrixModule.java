//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * @author dkokotov
 *
 */
public class AffymetrixModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {        
        // data files
        Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(),
                DataFileHandler.class);
        dataFileBinder.addBinding().to(CelHandler.class);
        dataFileBinder.addBinding().to(ChpHandler.class);
        
        // design files
        Multibinder<DesignFileHandler> designFileBinder = Multibinder.newSetBinder(binder(),
                DesignFileHandler.class);
        designFileBinder.addBinding().to(CdfHandler.class);
        designFileBinder.addBinding().to(PgfClfDesignHandler.class);
        
        //array data descriptors
        Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                ArrayDataTypeDescriptor.class);       
        for (ArrayDataTypeDescriptor desc : AffymetrixArrayDataTypes.values()) {
            arrayDataDescriptorBinder.addBinding().toInstance(desc);            
        }
        
        // internal
        bind(AbstractChpDesignElementListUtility.class).annotatedWith(Names.named("pgfClf")).to(
                ChpPgfClfDesignElementListUtility.class);
        bind(AbstractChpDesignElementListUtility.class).annotatedWith(Names.named("cdf")).to(
                ChpCdfDesignElementListUtility.class);
    }
}
