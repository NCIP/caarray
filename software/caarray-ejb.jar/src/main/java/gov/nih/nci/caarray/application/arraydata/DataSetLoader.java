//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Responsible for ensuring that data columns for a <code>DataSet</code> are loaded. This enables
 * loading of data on demand.
 */
final class DataSetLoader {

    private static final Logger LOG = Logger.getLogger(DataSetLoader.class);

    private final CaArrayDaoFactory daoFactory;
    private final AbstractArrayData arrayData;
    private AbstractDataFileHandler dataFileHandler;
    private final ArrayDesignService arrayDesignService;

    DataSetLoader(AbstractArrayData arrayData, CaArrayDaoFactory daoFactory,
            ArrayDesignService arrayDesignService) {
        this.arrayData = arrayData;
        this.daoFactory = daoFactory;
        this.arrayDesignService = arrayDesignService;
    }

    void load() {
        List<QuantitationType> types = getQuantitationTypes();
        load(types);
    }

    void load(List<QuantitationType> types) {
        AbstractDataFileHandler handler = getDataFileHandler();
        if (isLoadRequired(getDataSet(), types)) {
            LOG.debug("Parsing required for file " + getArrayData().getDataFile().getName());
            File file = getFile();
            handler.loadData(getDataSet(), types, file, arrayDesignService);
            getDaoFactory().getArrayDao().save(getDataSet());
        } else {
            LOG.debug("File " + getArrayData().getDataFile().getName() + " has already been parsed");
        }
    }

    private File getFile() {
        return TemporaryFileCacheLocator.getTemporaryFileCache().getFile(getArrayData().getDataFile());
    }

    private boolean isLoadRequired(DataSet dataSet, List<QuantitationType> types) {
        for (HybridizationData hybridizationData : dataSet.getHybridizationDataList()) {
            if (isLoadRequired(hybridizationData, types)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLoadRequired(HybridizationData hybridizationData, List<QuantitationType> types) {
        for (AbstractDataColumn column : hybridizationData.getColumns()) {
            if (types.contains(column.getQuantitationType()) && !column.isLoaded()) {
                return true;
            }
        }
        return false;
    }

    private CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    private DataSet getDataSet() {
        return getArrayData().getDataSet();
    }

    private AbstractArrayData getArrayData() {
        return arrayData;
    }

    QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
        return getArrayDao().getQuantitationType(descriptor);
    }

    private ArrayDao getArrayDao() {
        return getDaoFactory().getArrayDao();
    }

    private List<QuantitationType> getQuantitationTypes() {
        List<QuantitationType> quantitationTypes = new ArrayList<QuantitationType>();
        for (QuantitationTypeDescriptor descriptor : getDataFileHandler().getQuantitationTypeDescriptors(getFile())) {
            quantitationTypes.add(getQuantitationType(descriptor));
        }
        return quantitationTypes;
    }

    AbstractDataFileHandler getDataFileHandler() {
        if (dataFileHandler == null) {
            dataFileHandler =
                ArrayDataHandlerFactory.getInstance().getHandler(getArrayData().getDataFile().getFileType());
        }
        return dataFileHandler;
    }

}
