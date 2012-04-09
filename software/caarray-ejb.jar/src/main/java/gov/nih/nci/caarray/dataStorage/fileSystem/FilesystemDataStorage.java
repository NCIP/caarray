/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.dataStorage.fileSystem;

import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * An implementation of data storage based on storing as files in the file system.
 * 
 * @author dkokotov
 */
public class FilesystemDataStorage implements DataStorage {
    static final String SCHEME = "file-system";

    private final String baseDir;

    /**
     * Create a FilesystemDataStorage that uses the given directory to store the data.
     * 
     * @param baseDir the directory where where data will be stored as files
     */
    @Inject
    public FilesystemDataStorage(@Named(FileSystemStorageModule.BASE_DIR_KEY) String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        final String fileHandle = UUID.randomUUID().toString();
        final StorageMetadata metadata = new StorageMetadata();
        metadata.setHandle(makeHandle(fileHandle));

        final File compressedFile = compressedFile(fileHandle);
        final File uncompressedFile = uncompressedFile(fileHandle);

        try {
            if (compressed) {
                writeFileData(stream, new FileOutputStream(compressedFile));
                final FileInputStream fis = Files.newInputStreamSupplier(compressedFile).getInput();
                uncompressAndWriteFile(fis, uncompressedFile);
                IOUtils.closeQuietly(fis);
            } else {
                writeFileData(stream, new FileOutputStream(uncompressedFile));
                final FileInputStream fis = Files.newInputStreamSupplier(uncompressedFile).getInput();
                compressAndWriteFile(fis, compressedFile);
                IOUtils.closeQuietly(fis);
            }
            metadata.setUncompressedSize(uncompressedFile.length());
            metadata.setCompressedSize(compressedFile.length());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        final StorageMetadata metadata = new StorageMetadata();
        try {
            File file;
            if (handle == null) {
                final String fileHandle = UUID.randomUUID().toString();
                file = uncompressedFile(fileHandle);
                writeFileData(stream, new FileOutputStream(file));
                metadata.setHandle(makeHandle(fileHandle));
            } else {
                file = openFile(handle, false);
                writeFileData(stream, new FileOutputStream(file, true));
                metadata.setHandle(handle);
            }
            metadata.setPartialSize(file.length());
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
        return metadata;
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        String fileHandle = handle.getSchemeSpecificPart();
        File uncompressedFile = openFile(handle, false);
        File compressedFile = compressedFile(fileHandle);

        try {
            final FileInputStream fis = Files.newInputStreamSupplier(uncompressedFile).getInput();
            compressAndWriteFile(fis, compressedFile(handle.getSchemeSpecificPart()));
            IOUtils.closeQuietly(fis);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(handle);
            metadata.setUncompressedSize(uncompressedFile.length());
            metadata.setCompressedSize(compressedFile.length());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageMetadata> list() {
        final Set<StorageMetadata> metadatas = Sets.newHashSet();
        final File[] files = baseStorageDir().listFiles(new PatternFilenameFilter(".*\\.unc"));
        if (files != null) {
            for (final File file : files) {
                final StorageMetadata sm = new StorageMetadata();
                sm.setCreationTimestamp(new Date(file.lastModified()));
                sm.setHandle(handleFromFile(file));
                sm.setUncompressedSize(file.length());
                metadatas.add(sm);
            }
        }
        return metadatas;
    }

    private URI handleFromFile(File file) {
        final String fileName = file.getName();
        if (fileName.endsWith(".comp")) {
            return makeHandle(StringUtils.substringBeforeLast(fileName, ".comp"));
        } else if (fileName.endsWith(".unc")) {
            return makeHandle(StringUtils.substringBeforeLast(fileName, ".unc"));
        } else {
            throw new IllegalArgumentException("File not managed by filestorage engine: " + fileName);
        }
    }

    private URI makeHandle(String id) {
        return CaArrayUtils.makeUriQuietly(SCHEME, id);
    }

    private File baseStorageDir() {
        return new File(this.baseDir);
    }

    private File compressedFile(String fileHandle) {
        return new File(baseStorageDir(), fileHandle + ".comp");
    }

    private File uncompressedFile(String fileHandle) {
        return new File(baseStorageDir(), fileHandle + ".unc");
    }

    private File file(String fileHandle, boolean compressed) {
        return compressed ? compressedFile(fileHandle) : uncompressedFile(fileHandle);
    }

    private void writeFileData(InputStream in, OutputStream out) throws IOException {
        ByteStreams.copy(in, out);
        out.flush();
        out.close();
    }

    private void compressAndWriteFile(InputStream in, File file) throws IOException {
        writeFileData(in, new GZIPOutputStream(new FileOutputStream(file)));
    }

    private void uncompressAndWriteFile(InputStream in, File file) throws IOException {
        writeFileData(new GZIPInputStream(in), new FileOutputStream(file));
    }

    private void checkScheme(URI handle) {
        if (!SCHEME.equals(handle.getScheme())) {
            throw new UnsupportedSchemeException("Unsupported scheme: " + handle.getScheme());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(URI handle) {
        checkScheme(handle);
        FileUtils.deleteQuietly(compressedFile(handle.getSchemeSpecificPart()));
        FileUtils.deleteQuietly(uncompressedFile(handle.getSchemeSpecificPart()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Collection<URI> handles) {
        for (final URI handle : handles) {
            remove(handle);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        final File file = file(handle.getSchemeSpecificPart(), compressed);
        if (!file.exists()) {
            throw new DataStoreException("No data available for handle " + handle);
        }
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        try {
            return new AutoCloseInputStream(FileUtils.openInputStream(openFile(handle, compressed)));
        } catch (final IOException e) {
            throw new DataStoreException("Could not open input stream for data: ", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseFile(URI handle, boolean compressed) {
        checkScheme(handle);
        // no-op - the file is just there
    }
}
