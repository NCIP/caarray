//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

/**
 * Entry point to the ArrayDataService subsystem.
 */
@Local(ArrayDataService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ArrayDataServiceBean implements ArrayDataService {

    private static final Logger LOG = Logger.getLogger(ArrayDataServiceBean.class);

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void initialize() {
        LogUtil.logSubsystemEntry(LOG);
        new TypeRegistrationManager(getDaoFactory().getArrayDao()).registerNewTypes();
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public DataSet getData(AbstractArrayData arrayData) {
        LogUtil.logSubsystemEntry(LOG);
        checkArguments(arrayData);
        loadDataSet(arrayData);
        LogUtil.logSubsystemExit(LOG);
        return arrayData.getDataSet();
    }

    private void loadDataSet(AbstractArrayData arrayData) {
        DataSetLoader loader = new DataSetLoader(arrayData, getDaoFactory(), getArrayDesignService());
        loader.load();
    }

    /**
     * {@inheritDoc}
     */
    public DataSet getData(AbstractArrayData arrayData, List<QuantitationType> types) {
        LogUtil.logSubsystemEntry(LOG);
        checkArguments(arrayData);
        loadDataSet(arrayData, types);
        LogUtil.logSubsystemExit(LOG);
        return arrayData.getDataSet();
    }

    private void loadDataSet(AbstractArrayData arrayData, List<QuantitationType> types) {
        DataSetLoader loader = new DataSetLoader(arrayData, getDaoFactory(), getArrayDesignService());
        loader.load(types);
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importData(CaArrayFile caArrayFile, boolean createAnnnotation, DataImportOptions dataImportOptions)
            throws InvalidDataFileException {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        AbstractDataSetImporter<? extends AbstractArrayData> abstractDataSetImporter =
            AbstractDataSetImporter.create(caArrayFile, getDaoFactory(), dataImportOptions);
        AbstractArrayData arrayData = abstractDataSetImporter.importData(createAnnnotation);
        loadDataSet(arrayData);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public ArrayDesign getArrayDesign(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        if (!caArrayFile.getFileType().isArrayData()) {
            return null;
        }
        AbstractDataFileHandler handler = ArrayDataHandlerFactory.getInstance().getHandler(caArrayFile.getFileType());
        File file = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
        ArrayDesign ad = handler.getArrayDesign(ServiceLocatorFactory.getArrayDesignService(), file);
        TemporaryFileCacheLocator.getTemporaryFileCache().closeFile(caArrayFile);
        LogUtil.logSubsystemExit(LOG);
        return ad;
    }
    
    private void checkArguments(AbstractArrayData arrayData) {
        if (arrayData == null) {
            throw new IllegalArgumentException("Argument arrayData was null");
        }
        if (arrayData.getDataFile() == null) {
            throw new IllegalArgumentException("No data file is associated with array data object");
        }
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet) {
        DataFileValidator dataFileValidator =
            new DataFileValidator(arrayDataFile, mTabSet, getDaoFactory(), getArrayDesignService());
        dataFileValidator.validate();
        return arrayDataFile.getValidationResult();
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    private ArrayDesignService getArrayDesignService() {
        return (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
    }



}
