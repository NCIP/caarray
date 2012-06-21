/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.file.FileTypeRegistryImpl;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.DesignFileHandler;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.commons.lang.BooleanUtils;

import com.google.inject.Inject;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * Stub implementation for testing.
 */
public class FileAccessServiceStub implements FileAccessService, DataStorage {
    public static final String SCHEME = "fsstub";

    private final Map<String, File> nameToFile = new HashMap<String, File>();
    private final Map<String, Boolean> deletables = new HashMap<String, Boolean>();
    private int savedFileCount = 0;
    private int removedFileCount = 0;
    private FileTypeRegistry typeRegistry;

    public FileAccessServiceStub() {
        this(new FileTypeRegistryImpl(Collections.<DataFileHandler> emptySet(),
                Collections.<DesignFileHandler> emptySet()));
    }

    @Inject
    public FileAccessServiceStub(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    @Override
    public CaArrayFile add(File file) {
        CaArrayFile parent = null;
        return add(file, parent);
    }

    public CaArrayFile add(File file, CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setName(file.getName());
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }
        setTypeFromExtension(caArrayFile, file.getName());
        this.nameToFile.put(caArrayFile.getName(), file);
        caArrayFile.setDataHandle(CaArrayUtils.makeUriQuietly(SCHEME, file.getName()));
        return caArrayFile;
    }

    private void setTypeFromExtension(CaArrayFile caArrayFile, String filename) {
        final FileType type = this.typeRegistry.getTypeFromExtension(filename);
        if (type != null) {
            caArrayFile.setFileType(type);
        }
    }

    public File createFile(String fileName) {
        this.savedFileCount++;
        return new File(fileName);
    }

    @Override
    public CaArrayFile add(File file, String filename) {
        return add(file, filename, null);
    }

    public CaArrayFile add(File file, String filename, CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setName(filename);
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }
        this.nameToFile.put(caArrayFile.getName(), file);
        return caArrayFile;
    }
    
    public CaArrayFile addChunk(File file, String fileName, Long fileSize, CaArrayFile caArrayFile) {
        return null; // unused.  Trying to get eventually rid of stub classes.
    }

    @Override
    public boolean remove(CaArrayFile caArrayFile) {
        if (BooleanUtils.isTrue(this.deletables.get(caArrayFile.getName()))) {
            this.removedFileCount++;
        }
        if (caArrayFile.getProject() != null) {
            caArrayFile.getProject().getFiles().remove(caArrayFile);
        }
        return false;
    }

    public void save(CaArrayFile caArrayFile) {
        this.savedFileCount++;
        if (caArrayFile.getId() == null) {
            caArrayFile.setId((long) this.savedFileCount);
        }
    }

    public void reset() {
        this.nameToFile.clear();
        this.deletables.clear();
        this.savedFileCount = 0;
        this.removedFileCount = 0;
    }

    public void setDeletableStatus(CaArrayFile file, boolean deletable) {
        this.deletables.put(file.getName(), deletable);
    }

    /**
     * @return the savedFileCount
     */
    public int getSavedFileCount() {
        return this.savedFileCount;
    }

    /**
     * @return the removedFileCount
     */
    public int getRemovedFileCount() {
        return this.removedFileCount;
    }

    /**
     * @return the nameToFile
     */
    public Map<String, File> getNameToFile() {
        return this.nameToFile;
    }

    /**
     * {@inheritDoc}
     */
    public void closeFile(CaArrayFile caarrayFile) {
        closeFile(caarrayFile, true);
    }

    public void closeFile(CaArrayFile caarrayFile, boolean uncompressed) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void deleteFiles() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void delete(File file) {
        this.removedFileCount++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CaArrayFile add(InputStream stream, String filename) {
        return add(stream, filename, null);
    }

    public CaArrayFile add(InputStream stream, String filename,
            CaArrayFile parent) {
        final CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
        this.nameToFile.put(caArrayFile.getName(), new File(filename));
        return caArrayFile;
    }

    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void synchronizeDataStorage() {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanupUnreferencedChildren() {
        // no-op
    }


    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        return null;
    }

    @Override
    public Iterable<StorageMetadata> list() {
        final List<StorageMetadata> metadatas = Lists.newArrayList();
        for (final Map.Entry<String, File> fileEntry : this.nameToFile.entrySet()) {
            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(CaArrayUtils.makeUriQuietly(SCHEME, fileEntry.getKey()));
            metadata.setCreationTimestamp(new Date(fileEntry.getValue().lastModified()));
            metadata.setCompressedSize(fileEntry.getValue().length());
            metadata.setUncompressedSize(fileEntry.getValue().length());
            metadatas.add(metadata);
        }
        return metadatas;
    }

    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        return this.nameToFile.get(handle.getSchemeSpecificPart());
    }

    @Override
    public InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        try {
            return new AutoCloseInputStream(FileUtils.openInputStream(openFile(handle, compressed)));
        } catch (final IOException e) {
            throw new DataStoreException("Could not open input stream for data " + handle + ":", e);
        }
    }

    @Override
    public void releaseFile(URI handle, boolean compressed) {
        // no-op
    }

    @Override
    public void remove(Collection<URI> handles) throws DataStoreException {
        // no-op
    }

    @Override
    public void remove(URI handle) throws DataStoreException {
        // no-op
    }

    private void checkScheme(URI handle) {
        if (!SCHEME.equals(handle.getScheme())) {
            throw new UnsupportedSchemeException("Unsupported scheme: " + handle.getScheme());
        }
    }

    /**
     * @param typeRegistry the typeRegistry to set
     */
    public void setTypeRegistry(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    /**
     * @return the typeRegistry
     */
    public FileTypeRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    /**
     * @return a DataStorageFacade that knows about a single storage engine - this stub class
     */
    public DataStorageFacade createStorageFacade() {
        final Map<String, DataStorage> engines = Maps.newHashMap();
        engines.put(FileAccessServiceStub.SCHEME, this);
        return new DataStorageFacade(engines, FileAccessServiceStub.SCHEME, FileAccessServiceStub.SCHEME);
    }
}
