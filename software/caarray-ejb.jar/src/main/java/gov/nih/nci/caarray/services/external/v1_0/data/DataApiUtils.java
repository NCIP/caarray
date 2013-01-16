//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * DataApiUtils is a helper interface for working with the caArray Data API. It makes it easier to perform a variety of
 * common tasks. 
 * 
 * There are implementations for the Java and Grid API's. Using this interface in client code will make it
 * easier to migrate from one flavor of the API to another in the future.
 * 
 * @author dkokotov
 */
public interface DataApiUtils {

    /**
     * Retrieve the contents of the caArray file with given reference and save them in a temporary file.
     * The temporary file will be created in the directory specified by the <b>java.io.tempDir</b> property
     * 
     * @param fileRef the reference identifying the file
     * @param compressed whether to download the actual contents of the file or the compressed contents.
     * @return a File handle for the temporary file which will hold the contents of the file.
     * 
     * @throws InvalidReferenceException if the file reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to a temporary file
     */
    File downloadFileContentsToTempFile(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the contents of the caArray file with given reference and save them in the given file.
     * 
     * @param fileRef the reference identifying the file
     * @param compressed whether to download the actual contents of the file or the compressed contents.
     * @param toFile the File handle identifying the file in which to save the data. If the file does not
     * exist, it will be created otherwise it will be overwritten.
     * 
     * @throws InvalidReferenceException if the file reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the file
     */
    void downloadFileContentsToFile(CaArrayEntityReference fileRef, boolean compressed, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the contents of the caArray file with given reference and return them as a byte array.
     * 
     * @param fileRef the reference identifying the file
     * @param compressed whether to download the actual contents of the file or the compressed contents.
     * @return the byte array with the file contents
     * 
     * @throws InvalidReferenceException if the file reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the byte array with the data.
     */
    byte[] getFileContents(CaArrayEntityReference fileRef, boolean compressed) throws InvalidReferenceException,
            DataTransferException, IOException;

    /**
     * Retrieve the contents of the caArray file with given reference and write them into the given OutputStream.
     * 
     * @param fileRef the reference identifying the file
     * @param compressed whether to download the actual contents of the file or the compressed contents.
     * @param os the output stream into which the retrieved contents should be written. 
     * 
     * @throws InvalidReferenceException if the file reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the output stream
     */
    void copyFileContentsToOutputStream(CaArrayEntityReference fileRef, boolean compressed, OutputStream os)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the caArray files identified by the given references and create a temporary Zip file containing them.
     * The temporary file will be created in the directory specified by the <b>java.io.tempDir</b> property
     * 
     * @param fileRefs the file references identifying the files to retrieve
     * @return a File handle for the temporary Zip file, whose entries will be the retrieved files.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the temporary file
     */
    File downloadFileContentsZipToTempFile(Iterable<CaArrayEntityReference> fileRefs)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the caArray files identified by the given references and create a Zip file containing them at the
     * location specified by the given file.
     * 
     * @param fileRefs the file references identifying the files to retrieve
     * @param toFile the File handle identifying the absolute pathname of the file in which to save the Zip. If the file
     *            does not exist, it will be created otherwise it will be overwritten.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the file
     */
    void downloadFileContentsZipToFile(Iterable<CaArrayEntityReference> fileRefs, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the caArray files identified by the given references and write a Zip file containing them to the given
     * OutputStream.
     * 
     * @param fileRefs the file references identifying the files to retrieve
     * @param ostream the output stream into which the Zip of the retrieved files should be written.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the output stream
     */
    void copyFileContentsZipToOutputStream(Iterable<CaArrayEntityReference> fileRefs, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve the caArray files identified by the given references, and put them in a temporary directory. The
     * temporary directory will be created as a child of directory specified by the <b>java.io.tempDir</b> property.
     * 
     * @param fileRefs the file references identifying the files to retrieve
     * @return the File handle to the directory into which the files will be downloaded
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the temporary directory or extracting files into it.
     */
    File downloadFileContentsToTempDir(Iterable<CaArrayEntityReference> fileRefs) throws InvalidReferenceException,
            DataTransferException, IOException;

    /**
     * Retrieve the caArray files identified by the given references, and put them in the given directory.
     * 
     * @param fileRefs the file references identifying the files to retrieve
     * @param dir the File handle identifying the directory into which to download the files.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the directory or extracting files into it.
     */
    void downloadFileContentsToDir(Iterable<CaArrayEntityReference> fileRefs, File dir)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and save it to a temporary file. The IDF and SDRF are generated dynamically. The ZIP also contains the
     * data files referenced by the mage-tab SDRF. The temporary file will be created in the directory specified by the
     * <b>java.io.tempDir</b> property
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @return a File handle for the temporary file which will hold the contents of the ZIP.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the temporary file
     */
    File downloadMageTabZipToTempFile(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and save it to the given file. The IDF and SDRF are generated dynamically. The ZIP also contains the
     * data files referenced by the mage-tab SDRF.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param toFile the File handle identifying the file in which to save the ZIP. If the file does not exist, it will
     *            be created otherwise it will be overwritten.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the file
     */
    void downloadMageTabZipToFile(CaArrayEntityReference experimentRef, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and copy its contents to given OutputStream. The IDF and SDRF are generated dynamically. The ZIP also
     * contains the data files referenced by the mage-tab SDRF.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param ostream the output stream into which the retrieved ZIP should be written. 
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the output stream
     */
    void copyMageTabZipToOutputStream(CaArrayEntityReference experimentRef, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a set of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference, as well as all data files referenced from the SDRF, to a temporary directory. The IDF and SDRF are
     * generated dynamically. The temporary directory will be created as a child of the directory specified by the
     * <b>java.io.tempDir</b> property.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB fileset
     * @return the File handle to the directory into which the files comprising the MAGE-TAB fileset will
     * be downloaded
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the temporary directory or extracting files into it.
     */
    File downloadMageTabFilesetToTempDir(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException, IOException;

    /**
     * Retrieves a set of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference, as well as all data files referenced from the SDRF, to the given directory. The IDF and SDRF are
     * generated dynamically. 
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB fileset
     * @param dir the File handle identifying the directory into which to download the files comprising
     * the MAGE-TAB fileset
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the directory or extracting files into it.
     */
    void downloadMageTabFileSetToDir(CaArrayEntityReference experimentRef, File dir)
            throws InvalidReferenceException, DataTransferException, IOException;

}
