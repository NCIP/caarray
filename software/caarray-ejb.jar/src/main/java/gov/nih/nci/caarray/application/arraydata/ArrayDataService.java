//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;


/**
 * EJB interface for validating and importing array data. This should generally not be used directly from the UI layer;
 * instead, file import and validation should be done through FileManagementService, which will eventually
 * make use of this service.
 * 
 * @author dkokotov
 */
public interface ArrayDataService {
    /**
     * The default JNDI name to use to lookup <code>ArrayDataService</code>.
     */
    String JNDI_NAME = "caarray/ArrayDataServiceBean/local";

    /**
     * Invoked at system start-up, registers all handlers with the system, ensuring that all supported
     * array data types and quantitation types are included in the system.
     */
    void initialize();

    /**
     * Validates the array data in the file given, ensuring that it can be imported.
     *
     * @param arrayDataFile the array data file to validate.
     * @param mTabSet the set of mage tab docs used in validating the data file.
     * @param reimport whether this validation is being performed as part of a data reimport
     * @return the results of the validation.
     */
    FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet, boolean reimport);

    /**
     * Imports array data from the file given, creating annotation if necessary and requested
     * by the client.
     *
     * @param file the data file to import.
     * @param createAnnotation if no annotation is associated with the file, create a chain
     * of annotation to <code>AbstractArrayData</code>. How the chain is created is controlled
     * by the dataImportOptions paramter.
     * @param dataImportOptions options that control creation of the annotion chain
     * @param mTabSet parsed MageTabDocumentSet containing SDRF file(s). The SDRF file(s) specify 
     * data file to array design mapping. This is required for certain types of data files such as 
     * AgilentRawText. May be null if not applicable. 
     * @throws InvalidDataFileException if the file is not a valid data file.
     */
    void importData(CaArrayFile file, boolean createAnnotation, DataImportOptions dataImportOptions, 
            MageTabDocumentSet mTabSet)
            throws InvalidDataFileException;
    
    /**
     * Same functionality as overloaded method 
     * {@link #importData(CaArrayFile, boolean, DataImportOptions, MageTabDocumentSet)} 
     * except there is no MageTabDocumentSet param. Used in cases where the MageTabDocumentSet is 
     * not applicable. 
     * 
     * @param file see the overloaded method.
     * @param createAnnotation see the overloaded method.
     * @param dataImportOptions see the overloaded method.
     * @throws InvalidDataFileException see the overloaded method.
     */
    void importData(CaArrayFile file, boolean createAnnotation, DataImportOptions dataImportOptions)
            throws InvalidDataFileException;
    
}
