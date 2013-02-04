//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * @author jscott
 *
 */
public class AgilentModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {        
        // data files
        Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(),
                DataFileHandler.class);
        dataFileBinder.addBinding().to(AgilentRawTextDataHandler.class);
         
       // design files
        Multibinder<DesignFileHandler> designFileBinder = Multibinder.newSetBinder(binder(),
                DesignFileHandler.class);
        designFileBinder.addBinding().to(AgilentXmlDesignFileHandler.class);
        
        //array data descriptors
        Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                ArrayDataTypeDescriptor.class);       
        for (ArrayDataTypeDescriptor desc : AgilentArrayDataTypes.values()) {
            arrayDataDescriptorBinder.addBinding().toInstance(desc);            
        }
    }

}
