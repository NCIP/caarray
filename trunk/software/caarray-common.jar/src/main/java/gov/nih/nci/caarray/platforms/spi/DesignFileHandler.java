package gov.nih.nci.caarray.platforms.spi;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.Set;

/**
 * SPI interface for handling of array design files. This is the interface to implement in order to support handling
 * design files for a new array platform.
 * 
 * @author dkokotov
 */
public interface DesignFileHandler {
    /**
     * If this handler is capable of processing the given design files, associate any resources (streams, readers, etc)
     * necessary to process them. All operations on this interface subsequent to this and until closeFiles() is called
     * are implicitly assumed to refer to the given files.
     * 
     * @param designFiles the files to process. These files are for a single array design; generally it will be a
     *            singleton set, but some formats use multiple files per design
     * @return true if this handler is able to process the file, and successfully initialized the resources necessary to
     *         do so; false if this handler does not process files of this type.
     * 
     * @throws PlatformFileReadException if the handler does support this type of file, but there was an error
     *             initializing resources to process it
     */
    boolean openFiles(Set<CaArrayFile> designFiles) throws PlatformFileReadException;

    /**
     * Release any open resources for this handler's current files.
     */
    void closeFiles();

    /**
     * Populate the given ArrayDesign instance with the metadata about the design defined in the currently open file
     * set. This should consist of things like name, number of features, and so on. This method should not load the full
     * design details (e.g. create the design elements) - this should be done in the loadDesignDetails method.
     * 
     * @param arrayDesign the ArrayDesign instance to be populated
     * @throws PlatformFileReadException if there is an error processing the design file set.
     */
    void load(ArrayDesign arrayDesign) throws PlatformFileReadException;

    /**
     * Populate the given ArrayDesign instance with an ArrayDesignDetails instance holding the design elements making up
     * the array design defined in the currently open file set.
     * 
     * @param arrayDesign the ArrayDesign instance to be populated
     * @throws PlatformFileReadException if there is an error processing the design file set.
     */
    void createDesignDetails(ArrayDesign arrayDesign) throws PlatformFileReadException;

    /**
     * Validate the contents of the currently open file set.
     * 
     * @param result the ValidationResult to which any errors or warnings from the validation should be added. The
     *            handler should create a FileValidationResult for each open file, add it to this result, and associate
     *            it with the appropriate CaArrayFile instance, even if no further validation is done.
     * 
     * @throws PlatformFileReadException if there is an error processing the design file set.
     */
    void validate(ValidationResult result) throws PlatformFileReadException;

    /**
     * @return a set of FileType instances describing the files that this DesignFileHandler can handle.
     */
    Set<FileType> getSupportedTypes();
    
    /**
     * @return true if this handler parses the files, false if it does not.
     */
    boolean parsesData();
    
}