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
package gov.nih.nci.caarray.dataStorage.database;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.BlobHolder;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * An implementation of data storage based on storing in database as multipart blobs. Appropriate for mysql and other
 * databases which cannot stream data from blobs.
 * 
 * @author dkokotov
 */
public class DatabaseMultipartBlobDataStorage implements DataStorage {
    private static final Logger LOG = Logger.getLogger(DatabaseMultipartBlobDataStorage.class);

    static final String SCHEME = "db-multipart";

    /**
     * Constant holding the default blob size. By default it will be 50 MB.
     */
    private static final int DEFAULT_BLOB_SIZE = 50 * 1024 * 1024;

    private final MultipartBlobDao blobDao;
    private final SearchDao searchDao;
    private final int blobPartSize = DEFAULT_BLOB_SIZE;
    private final Provider<TemporaryFileCache> tempFileCacheSource;

    /**
     * Constructor.
     * 
     * @param blobDao MultipartBlobDao dependency
     * @param searchDao SearchDao dependency
     * @param tempFileCacheSource Provider which will generate TemporaryfileCache instances as needed, used for caching
     *            blob data to disk.
     */
    @Inject
    public DatabaseMultipartBlobDataStorage(MultipartBlobDao blobDao, SearchDao searchDao,
            @Named("dbMultipartStorageTempCache") Provider<TemporaryFileCache> tempFileCacheSource) {
        this.blobDao = blobDao;
        this.searchDao = searchDao;
        this.tempFileCacheSource = tempFileCacheSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        try {
            final MultiPartBlob multiPartBlob = new MultiPartBlob();
            multiPartBlob.setCreationTimestamp(new Date());
            multiPartBlob.writeData(stream, !compressed, this.blobPartSize);
            this.blobDao.save(multiPartBlob);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(makeHandle(multiPartBlob.getId()));
            metadata.setCompressedSize(multiPartBlob.getCompressedSize());
            metadata.setUncompressedSize(multiPartBlob.getUncompressedSize());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        try {
            MultiPartBlob multiPartBlob;
            if (handle == null) {
                multiPartBlob = new MultiPartBlob();
                multiPartBlob.setCreationTimestamp(new Date());
            } else {
                multiPartBlob = searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
            }
            multiPartBlob.writeData(stream, false, blobPartSize);
            blobDao.save(multiPartBlob);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(makeHandle(multiPartBlob.getId()));
            metadata.setPartialSize(multiPartBlob.getUncompressedSize());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        MultiPartBlob multiPartBlob =
                searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
        try {
            InputStream is = multiPartBlob.readCompressedContents();
            return add(is, false);
        } catch (IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
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
    public void remove(Collection<URI> handles) throws DataStoreException {
        final List<Long> blobIds = new ArrayList<Long>();
        for (final URI handle : handles) {
            checkScheme(handle);
            blobIds.add(Long.valueOf(handle.getSchemeSpecificPart()));
        }
        this.blobDao.deleteByIds(blobIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(URI handle) throws DataStoreException {
        remove(Collections.singleton(handle));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseFile(URI handle, boolean compressed) {
        checkScheme(handle);
        final String tempFileName = fileName(handle.getSchemeSpecificPart(), compressed);
        this.tempFileCacheSource.get().delete(tempFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        final String tempFileName = fileName(handle.getSchemeSpecificPart(), compressed);
        final TemporaryFileCache tempFileCache = this.tempFileCacheSource.get();
        File tempFile = tempFileCache.getFile(tempFileName);
        if (tempFile != null) {
            return tempFile;
        } else {
            tempFile = tempFileCache.createFile(tempFileName);
            final MultiPartBlob blob =
                    this.searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
            if (blob == null) {
                throw new DataStoreException("No data found for handle " + handle);
            }
            try {
                final OutputStream os = FileUtils.openOutputStream(tempFile);
                copyContentsToStream(blob, !compressed, os);
                IOUtils.closeQuietly(os);
            } catch (final IOException e) {
                throw new DataStoreException("Could not write out file ", e);
            }
            return tempFile;
        }
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

    private void copyContentsToStream(MultiPartBlob blob, boolean inflate, OutputStream dest) throws IOException {
        if (refreshIfCleared(blob)) {
            LOG.info("reloaded blobs for " + blob.getId());
        }
        final InputStream in = inflate ? blob.readUncompressedContents() : blob.readCompressedContents();
        try {
            IOUtils.copy(in, dest);
        } finally {
            IOUtils.closeQuietly(in);
            clearAndEvictBlobs(blob);
        }
    }

    private String compressedFileName(String fileHandle) {
        return fileHandle + ".comp";
    }

    private String uncompressedFileName(String fileHandle) {
        return fileHandle + ".unc";
    }

    private String fileName(String fileHandle, boolean compressed) {
        return compressed ? compressedFileName(fileHandle) : uncompressedFileName(fileHandle);
    }

    private boolean refreshIfCleared(MultiPartBlob blobs) {
        final List<BlobHolder> list = blobs.getBlobParts();
        boolean reloaded = false;
        for (final BlobHolder bh : list) {
            if (bh.getContents() == null) {
                this.searchDao.refresh(bh);
                reloaded = true;
            }
        }
        return reloaded;
    }

    private void clearAndEvictBlobs(MultiPartBlob blobs) {
        final List<BlobHolder> parts = blobs.getBlobParts();
        if (parts != null) {
            for (final BlobHolder bh : parts) {
                this.blobDao.evictObject(bh);
                bh.setContents(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageMetadata> list() {
        final Set<StorageMetadata> metadatas = Sets.newHashSet();
        final List<MultiPartBlob> blobs = this.searchDao.retrieveAll(MultiPartBlob.class);
        for (final MultiPartBlob blob : blobs) {
            final StorageMetadata sm = new StorageMetadata();
            sm.setCreationTimestamp(blob.getCreationTimestamp());
            sm.setHandle(makeHandle(blob.getId()));
            sm.setCompressedSize(blob.getCompressedSize());
            sm.setUncompressedSize(blob.getUncompressedSize());
            metadatas.add(sm);
        }
        return metadatas;
    }

    private URI makeHandle(Long id) {
        return CaArrayUtils.makeUriQuietly(SCHEME, String.valueOf(id));
    }
}
