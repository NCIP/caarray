package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.query.FileDownloadRequest;
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
     * Retrieve a ZIP of the caArray files identified by the given request and save it in a temporary file. 
     * The temporary file will be created in the directory specified by the <b>java.io.tempDir</b> property
     * 
     * @param request the FileDownloadRequest identifying the files to retrieve
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @return a File handle for the temporary file which will hold the contents of the ZIP.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the temporary file
     */
    File downloadFileContentsZipToTempFile(FileDownloadRequest request, boolean compressIndividually)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve a ZIP of the caArray files identified by the given request and save it in the given file. 
     * 
     * @param request the FileDownloadRequest identifying the files to retrieve
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @param toFile the File handle identifying the file in which to save the ZIP. If the file does not
     * exist, it will be created otherwise it will be overwritten.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the file
     */
    void downloadFileContentsZipToFile(FileDownloadRequest request, boolean compressIndividually, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve a ZIP of the caArray files identified by the given request and copy the ZIP contents to the given 
     * OutputStream. 
     * 
     * @param request the FileDownloadRequest identifying the files to retrieve
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @param ostream the output stream into which the retrieved ZIP should be written. 
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the output stream
     */
    void copyFileContentsZipToOutputStream(FileDownloadRequest request, boolean compressIndividually,
            OutputStream ostream) throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieve a ZIP of the caArray files identified by the given request, and extract the files in the ZIP
     * to a temporary directory.
     * The temporary directory will be created in the directory specified by the <b>java.io.tempDir</b> property.
     * 
     * @param request the FileDownloadRequest identifying the files to retrieve
     * @return the File handle to the directory into which the files in the ZIP will be extracted.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the temporary directory or extracting files into it.
     */
    File downloadAndExtractFileContentsZipToTempDir(FileDownloadRequest request) throws InvalidReferenceException,
            DataTransferException, IOException;

    /**
     * Retrieve a ZIP of the caArray files identified by the given request, and extract the files in the ZIP
     * to the given directory.
     * 
     * @param request the FileDownloadRequest identifying the files to retrieve
     * @param dir the File handle identifying the directory into which to extract the files in the ZIP. 
     * If the directory does not exist, it will be created.
     * 
     * @throws InvalidReferenceException if any of the file reference in the download request is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the directory or extracting files into it.
     */
    void downloadAndExtractFileContentsZipToDir(FileDownloadRequest request, File dir)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and save it to a temporary file. The IDF and SDRF are generated dynamically. The ZIP also contains the
     * data files referenced by the mage-tab SDRF. The temporary file will be created in the directory specified by the
     * <b>java.io.tempDir</b> property
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @return a File handle for the temporary file which will hold the contents of the ZIP.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the temporary file
     */
    File downloadMageTabZipToTempFile(CaArrayEntityReference experimentRef, boolean compressIndividually)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and save it to the given file. The IDF and SDRF are generated dynamically. The ZIP also contains the
     * data files referenced by the mage-tab SDRF.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @param toFile the File handle identifying the file in which to save the ZIP. If the file does not exist, it will
     *            be created otherwise it will be overwritten.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the file
     */
    void downloadMageTabZipToFile(CaArrayEntityReference experimentRef, boolean compressIndividually, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference and copy its contents to given OutputStream. The IDF and SDRF are generated dynamically. The ZIP also
     * contains the data files referenced by the mage-tab SDRF.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param compressIndividually if true, then each file in the Zip will be compressed using GZip, and will then be
     *            added to the Zip using the STORED method. If false, then each file will be added to the zip as-is
     *            using the DEFLATED method
     * @param ostream the output stream into which the retrieved ZIP should be written. 
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error writing the data to the output stream
     */
    void copyMageTabZipToOutputStream(CaArrayEntityReference experimentRef, boolean compressIndividually,
            OutputStream ostream) throws InvalidReferenceException, DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference, and extract the files in the ZIP to a temporary directory. The IDF and SDRF are generated dynamically.
     * The ZIP also contains the data files referenced by the mage-tab SDRF. The temporary directory will be created in
     * the directory specified by the <b>java.io.tempDir</b> property.
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @return the File handle to the directory into which the files in the ZIP will be extracted.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the temporary directory or extracting files into it.
     */
    File downloadAndExtractMageTabZipToTempDir(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException, IOException;

    /**
     * Retrieves a ZIP of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference, and extract the files in the ZIP to the given directory. The IDF and SDRF are generated dynamically.
     * The ZIP also contains the data files referenced by the mage-tab SDRF. 
     * 
     * @param experimentRef reference identifying the experiment for which to download the MAGE-TAB ZIP.
     * @param dir the File handle identifying the directory into which to extract the files in the ZIP. 
     * If the directory does not exist, it will be created.
     * 
     * @throws InvalidReferenceException if the experiment reference is invalid.
     * @throws DataTransferException if there is an error transferring the data
     * @throws IOException if there is an error creating the directory or extracting files into it.
     */
    void downloadAndExtractMageTabZipToDir(CaArrayEntityReference experimentRef, File dir)
            throws InvalidReferenceException, DataTransferException, IOException;

}