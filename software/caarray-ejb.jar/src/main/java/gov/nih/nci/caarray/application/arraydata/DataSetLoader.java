//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Helper class for ensuring that data columns for a <code>DataSet</code> are loaded. This enables loading of data on
 * demand.
 * 
 * @author dkokotov
 */
final class DataSetLoader extends AbstractArrayDataUtility {
    private static final Logger LOG = Logger.getLogger(DataSetLoader.class);

    private final ParsedDataPersister parsedDataPersister;

    @Inject
    DataSetLoader(ArrayDao arrayDao, Set<DataFileHandler> handlers, ParsedDataPersister parsedDataPersister,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        super(arrayDao, handlers, fallbackHandlerProvider);
        this.parsedDataPersister = parsedDataPersister;
    }

    void load(AbstractArrayData arrayData, MageTabDocumentSet mTabSet) throws InvalidDataFileException {
        DataFileHandler handler = null;
        try {
            LOG.debug("Parsing required for file " + arrayData.getDataFile().getName());
            try {
                handler = findAndSetupHandler(arrayData.getDataFile(), mTabSet);
                assert handler != null : "findAndSetupHandler must never return null";
                final ArrayDesign design = getArrayDesign(arrayData.getDataFile(), handler);
                final List<QuantitationType> types = getQuantitationTypes(handler);
                handler.loadData(arrayData.getDataSet(), types, design);
                saveDataToStorage(arrayData.getDataSet());
            } catch (final PlatformFileReadException e) {
                LOG.warn("Error loading data: ", e);
                final FileValidationResult validationResult = arrayData.getDataFile().getValidationResult();
                validationResult.addMessage(Type.ERROR, "Error loading data set: " + e.getMessage());
                throw new InvalidDataFileException(validationResult, e);
            }
            getArrayDao().save(arrayData.getDataSet());
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }

    private void saveDataToStorage(DataSet dataSet) {
        for (final HybridizationData hdata : dataSet.getHybridizationDataList()) {
            for (final AbstractDataColumn column : hdata.getColumns()) {
                this.parsedDataPersister.saveToStorage(column);
            }
        }

    }

    private QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        return getArrayDao().getQuantitationType(descriptor);
    }

    private List<QuantitationType> getQuantitationTypes(DataFileHandler handler) {
        final List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
        for (final QuantitationTypeDescriptor descriptor : handler.getQuantitationTypeDescriptors()) {
            quantitationTypes.add(getQuantitationType(descriptor));
        }
        return quantitationTypes;
    }
}
