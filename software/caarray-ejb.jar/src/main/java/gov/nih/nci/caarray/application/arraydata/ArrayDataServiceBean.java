//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Entry point to the ArrayDataService subsystem.
 * 
 * @author dkokotov
 */
@Local(ArrayDataService.class)
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors(InjectionInterceptor.class)
public class ArrayDataServiceBean extends AbstractArrayDataService {
    private static final Logger LOG = Logger.getLogger(ArrayDataServiceBean.class);

    private TypeRegistrationManager typeRegistrationManager;
    private DataSetImporter dataSetImporter;
    private DataSetLoader loader;
    private DataFileValidator dataFileValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void initialize() {
        LogUtil.logSubsystemEntry(LOG);
        this.typeRegistrationManager.registerNewTypes();
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void importData(CaArrayFile caArrayFile, boolean createAnnnotation, DataImportOptions dataImportOptions, 
            MageTabDocumentSet mTabSet)
            throws InvalidDataFileException {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        final AbstractArrayData arrayData =
                this.dataSetImporter.importData(caArrayFile, dataImportOptions, createAnnnotation, mTabSet);
        this.loader.load(arrayData, mTabSet);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet, boolean reimport) {
        this.dataFileValidator.validate(arrayDataFile, mTabSet, reimport);
        return arrayDataFile.getValidationResult();
    }

    /**
     * @param dataSetImporter the dataSetImporter to set
     */
    @Inject
    public void setDataSetImporter(DataSetImporter dataSetImporter) {
        this.dataSetImporter = dataSetImporter;
    }

    /**
     * @param loader the loader to set
     */
    @Inject
    public void setLoader(DataSetLoader loader) {
        this.loader = loader;
    }

    /**
     * @param dataFileValidator the dataFileValidator to set
     */
    @Inject
    public void setDataFileValidator(DataFileValidator dataFileValidator) {
        this.dataFileValidator = dataFileValidator;
    }

    /**
     * @param typeRegistrationManager the typeRegistrationManager to set
     */
    @Inject
    public void setTypeRegistrationManager(TypeRegistrationManager typeRegistrationManager) {
        this.typeRegistrationManager = typeRegistrationManager;
    }
}
