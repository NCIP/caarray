//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.List;


/**
 * Provides hybridization data storage and retrieval functionality.
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
     * @return the results of the validation.
     */
    FileValidationResult validate(CaArrayFile arrayDataFile, MageTabDocumentSet mTabSet);

    /**
     * Imports array data from the file given, creating annotation if necessary and requested
     * by the client.
     *
     * @param file the data file to import.
     * @param createAnnotation if no annotation is associated with the file, create a chain
     * of annotation to <code>AbstractArrayData</code>. How the chain is created is controlled
     * by the dataImportOptions paramter.
     * @param dataImportOptions options that control creation of the annotion chain
     * @throws InvalidDataFileException if the file is not a valid data file.
     */
    void importData(CaArrayFile file, boolean createAnnotation, DataImportOptions dataImportOptions)
            throws InvalidDataFileException;

    /**
     * Returns the complete data content of the provided array data object.
     *
     * @param arrayData get data from this data object
     * @return the data.
     */
    DataSet getData(AbstractArrayData arrayData);

    /**
     * Returns the data content of the provided array data object for only the specified
     * <code>QuantitationTypes</code>.
     *
     * @param arrayData get data from this data object
     * @param types get data for these types only
     * @return the data.
     */
    DataSet getData(AbstractArrayData arrayData, List<QuantitationType> types);

}
