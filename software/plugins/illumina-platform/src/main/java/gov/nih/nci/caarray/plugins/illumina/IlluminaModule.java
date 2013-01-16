//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * @author dkokotov
 */
public class IlluminaModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {        
        // data files
        Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(),
                DataFileHandler.class);
        dataFileBinder.addBinding().to(CsvDataHandler.class);
        dataFileBinder.addBinding().to(GenotypingProcessedMatrixHandler.class);
        dataFileBinder.addBinding().to(SampleProbeProfileHandler.class);
        
        // design files
        Multibinder<DesignFileHandler> designFileBinder = Multibinder.newSetBinder(binder(),
                DesignFileHandler.class);
        designFileBinder.addBinding().to(BgxDesignHandler.class);
        designFileBinder.addBinding().to(IlluminaCsvDesignHandler.class);
        
        //array data descriptors
        Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                ArrayDataTypeDescriptor.class);       
        for (ArrayDataTypeDescriptor desc : IlluminaArrayDataTypes.values()) {
            arrayDataDescriptorBinder.addBinding().toInstance(desc);            
        }
    }
}
