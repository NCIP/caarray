package gov.nih.nci.caarray.plugins.copynumber;

import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * 
 * @author dharley
 *
 */
public class DataMatrixCopyNumberModule extends AbstractModule {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {    
        // data files
        Multibinder<DataFileHandler> dataFileBinder = Multibinder.newSetBinder(binder(),
                DataFileHandler.class);
        dataFileBinder.addBinding().to(DataMatrixCopyNumberHandler.class);
        
        //array data descriptors
        Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder = Multibinder.newSetBinder(binder(),
                ArrayDataTypeDescriptor.class);       
        for (ArrayDataTypeDescriptor desc : DataMatrixCopyNumberDataTypes.values()) {
            arrayDataDescriptorBinder.addBinding().toInstance(desc);            
        }
    }

}
