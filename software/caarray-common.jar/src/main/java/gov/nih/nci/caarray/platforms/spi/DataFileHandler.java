//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.platforms.spi;

import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.List;
import java.util.Set;

/**
 * SPI interface for handling of array data files. This is the interface to implement in order to support handling data
 * files for a new array platform.
 * 
 * @author dkokotov
 */
public interface DataFileHandler {
    /**
     * Common error message for problems reading the data file.
     */
    String READ_FILE_ERROR_MESSAGE = "Couldn't read file";

    /**
     * If this handler is capable of processing the given file, associate any resources (streams, readers, etc)
     * necessary to process it. All operations on this interface subsequent to this and until closeFiles() is called are
     * implicitly assumed to refer to the given file.
     * 
     * @param dataFile the file to process
     * @return true if this handler is able to process the file, and successfully initialized the resources necessary to
     *         do so; false if this handler does not process files of this type.
     * 
     * @throws PlatformFileReadException if the handler does support this type of file, but there was an error
     *             initializing resources to process it
     */
    boolean openFile(CaArrayFile dataFile) throws PlatformFileReadException;

    /**
     * Release any open resources for this handler's current file.
     */
    void closeFiles();

    /**
     * @return the quantitation types corresponding to the measurements that this data file has values for. This should
     *         generally be a subset (often the same set) as this.getArrayDataTypeDescriptor().getQuantitationTypes()
     */
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors();

    /**
     * @return the quantitation types corresponding to the measurements that this data file has values for.
     */
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor();

    /**
     * Return a list of potential LSIDs for the array design that this data file was produced from. The LSIDs will be
     * tried in order to locate the array design for the file in the data store, using the first LSID that succeeds. If
     * the array design cannot be determined from the file, return an empty list.
     * 
     * @return the list of candidate design LSIDs, in the order they should be tried.
     * @throws PlatformFileReadException if there is an error processing the file.
     */
    List<LSID> getReferencedArrayDesignCandidateIds() throws PlatformFileReadException;

    /**
     * Validate the contents of the currently open file. This can include validation against the mage-tab data set, if
     * any, of which this file is a part. Note that the handler should indicate whether a mage-tab set is required via
     * the requiresMageTab() method; in this method, it should only validate that the file matches the mage-tab dataset
     * in ways specific to the file type (e.g. that the hybridization names are defined appropriately).
     * 
     * @param mTabSet the MageTabDocumentSet for the MAGE-TAB import of which this file is a part; can be null, if this
     *            is not done as part of an MAGE-TAB import.
     * @param result the FileValidationResult to which any errors or warnings from the validation should be added.
     * @param design the ArrayDesign corresponding to the currently open file.
     * @throws PlatformFileReadException if there is an error processing the file.
     */
    void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design)
            throws PlatformFileReadException;

    /**
     * Return whether the currently open file must be imported as part of a MAGE-TAB data set.
     * 
     * @return true if file must be part of a MAGE-TAB dataset, false otherwise.
     * @throws PlatformFileReadException if there is an error processing the file.
     */
    boolean requiresMageTab() throws PlatformFileReadException;

    /**
     * load measurement values for the given measurements from the currently open file into the provided data set.
     * 
     * @param dataSet the DataSet into which to load the values
     * @param types the QuantitationTypes for the measurements whose values should be loaded.
     * @param design the ArrayDesign corresponding to the currently open file.
     * 
     * @throws PlatformFileReadException if there is an error processing the file.
     */
    void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) throws PlatformFileReadException;

    /**
     * @return the names of hybridizations this file is associated with. Some data files do not include this
     *         information; in that case, this should return a single-element with the base name of the array data
     *         filename
     */
    List<String> getHybridizationNames();

    /**
     * @param hybridizationName name of the hybridization for which to return sample names that this file has data for
     * @return the names of the samples associated with the given hybridiaation that this file is associated with. Some
     *         data files do not include this information; in that case, this should return a single-element with the
     *         base name of the array data filename
     */
    List<String> getSampleNames(String hybridizationName);

    /**
     * @return true if this handler parses the files, false if it does not.
     */
    boolean parsesData();
    
    /**
     * @return a set of FileType instances describing the files that this DataFileHandler can handle.
     */
    Set<FileType> getSupportedTypes();

    /**
     * 
     * @return MageTabDocumentSet associated with this instance. 
     */
    MageTabDocumentSet getMageTabDocumentSet();
    
    /**
     * Stores reference to MageTabDocumentSet arg.
     * @param mTabSet parsed MageTabDocumentSet with SDRF that specifies data file to array design mappings. 
     */
    void setMageTabDocumentSet(MageTabDocumentSet mTabSet);

}
