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

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.FileDao;
import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Implementation of the FileAccess subsystem.
 */
@Local(FileAccessService.class)
@Stateless
@Interceptors({ExceptionLoggingInterceptor.class, InjectionInterceptor.class })
public class FileAccessServiceBean implements FileAccessService {
    /** Minimum age of a data block that can be removed if it is unreferenced. */
    public static final int MIN_UNREFERENCABLE_DATA_AGE = 300000;

    private static final Logger LOG = Logger.getLogger(FileAccessServiceBean.class);

    private FileDao fileDao;
    private ArrayDao arrayDao;
    private DataStorageFacade dataStorageFacade;
    private FileTypeRegistry typeRegistry;

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile parent = null;
        final CaArrayFile caArrayFile = add(file, parent);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, CaArrayFile parent) {
        LogUtil.logSubsystemEntry(LOG, file, parent);
        String name = (parent == null) ? file.getName() : parent.getName();
        final CaArrayFile caArrayFile = add(file, name, parent);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename) {
        LogUtil.logSubsystemEntry(LOG, file, filename);
        final CaArrayFile caArrayFile = add(file, filename, null);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename, CaArrayFile parent) {
        LogUtil.logSubsystemEntry(LOG, file, filename, parent);
        try {
            final InputStream is = FileUtils.openInputStream(file);
            final CaArrayFile caArrayFile = add(is, filename, parent);
            IOUtils.closeQuietly(is);
            return caArrayFile;
        } catch (final IOException e) {
            LogUtil.logSubsystemExit(LOG);
            throw new FileAccessException("File " + filename + " couldn't be read", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile addChunk(File file, String fileName, Long fileSize, CaArrayFile caArrayFile) {
        if (caArrayFile == null) {
            caArrayFile = getFile(fileName, fileSize);
        }
        readChunk(file, caArrayFile);
        if (caArrayFile.getPartialSize() == fileSize) {
            finalizeUpload(caArrayFile);
        }
        return caArrayFile;
    }

    private CaArrayFile getFile(String fileName, long fileSize) {
        CaArrayFile caArrayFile = createCaArrayFile(fileName, null, FileStatus.UPLOADING);
        caArrayFile.setUncompressedSize(fileSize);
        return caArrayFile;
    }
    
    private void readChunk(File chunk, CaArrayFile caArrayFile) {
        try {
            final InputStream is = FileUtils.openInputStream(chunk);
            final StorageMetadata metadata = this.dataStorageFacade.addFileChunk(caArrayFile.getDataHandle(), is);
            caArrayFile.setDataHandle(metadata.getHandle());
            caArrayFile.setPartialSize(metadata.getPartialSize());
            IOUtils.closeQuietly(is);
        } catch (final IOException e) {
            throw new FileAccessException("File " + caArrayFile.getName() + " couldn't be read", e);
        }
    }
    
    private void finalizeUpload(CaArrayFile caArrayFile) {
        StorageMetadata metadata = dataStorageFacade.finalizeChunkedFile(caArrayFile.getDataHandle());
        caArrayFile.setDataHandle(metadata.getHandle());
        caArrayFile.setCompressedSize(metadata.getCompressedSize());
        caArrayFile.setUncompressedSize(metadata.getUncompressedSize());
        caArrayFile.setFileStatus(FileStatus.UPLOADED);
    }
    
    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename) {
        return add(stream, filename, null);
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename, CaArrayFile parent) {
        final CaArrayFile caArrayFile = createCaArrayFile(filename, parent, FileStatus.UPLOADED);
        try {
            final StorageMetadata metadata = this.dataStorageFacade.addFile(stream, false);
            caArrayFile.setCompressedSize(metadata.getCompressedSize());
            caArrayFile.setUncompressedSize(metadata.getUncompressedSize());
            caArrayFile.setDataHandle(metadata.getHandle());
        } catch (final DataStoreException e) {
            throw new FileAccessException("Stream " + filename + " couldn't be written", e);
        }
        return caArrayFile;
    }

    private CaArrayFile createCaArrayFile(String filename, CaArrayFile parent, FileStatus status) {
        final CaArrayFile caArrayFile = new CaArrayFile(parent);
        caArrayFile.setFileStatus(status);
        caArrayFile.setName(filename);
        caArrayFile.setFileType(this.typeRegistry.getTypeFromExtension(filename));

        // set the child file's project to that of the parent. Add the child to the parent.
        if (parent != null) {
            caArrayFile.setProject(parent.getProject());
            parent.addChild(caArrayFile);
        }

        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        
        this.fileDao.flushSession(); // bean called in Session FlushMode.COMMIT.  Must flush prior to dao check

        if (caArrayFile.getProject() != null
                && !this.fileDao.getDeletableFiles(caArrayFile.getProject().getId()).contains(caArrayFile)) {
            return false;
        }

        final AbstractArrayData data = this.arrayDao.getArrayData(caArrayFile.getId());
        if (data != null) {
            for (final Hybridization h : data.getHybridizations()) {
                h.removeArrayData(data);
                h.propagateLastModifiedDataTime(new Date());
            }
            this.arrayDao.remove(data);
        }
        
        if (data != null || FileStatus.SUPPLEMENTAL.equals(caArrayFile.getFileStatus())) {
            caArrayFile.getProject().getExperiment().setLastDataModificationDate(new Date());
        }

        for (CaArrayFile childFile : caArrayFile.getChildren()) {
            removeFile(childFile);
        }

        if (caArrayFile.getParent() != null) {
            caArrayFile.getParent().removeChild(caArrayFile);
        }
        removeFile(caArrayFile);
        // the data storage will get cleaned up by the reaper

        LogUtil.logSubsystemExit(LOG);
        return true;
    }

    private void removeFile(CaArrayFile caArrayFile) {
        // A hibernate bug is preventing us from simply calling caArrayFile.getProject().getFiles().remove(caArrayFile)
        // https://hibernate.onjira.com/browse/HHH-3799
        // The workaround is to clear the collection and re-add everything we don't want to delete.
        // This is in reference to issue ARRAY-2349.
        SortedSet<CaArrayFile> files = caArrayFile.getProject().getFiles();
        SortedSet<CaArrayFile> filesToKeep = new TreeSet<CaArrayFile>();
        Long fileId = caArrayFile.getId();
        for (CaArrayFile file : files) {
            if (!file.getId().equals(fileId)) {
                filesToKeep.add(file);
            }
        }
        files.clear();
        files.addAll(filesToKeep);
        this.fileDao.remove(caArrayFile);
    }

    /**
     * {@inheritDoc}
     */
    public void synchronizeDataStorage() {
        final Set<URI> references = getActiveReferences();
        LOG.debug("Currently active references:" + references);
        if (references.isEmpty()) {
            LOG.warn("No active references found.  No files will be deleted.");
        } else {
            this.dataStorageFacade.removeUnreferencedData(references, MIN_UNREFERENCABLE_DATA_AGE);
        }
    }

    private Set<URI> getActiveReferences() {
        final Set<URI> references = Sets.newHashSet(this.fileDao.getAllFileHandles());
        references.addAll(this.arrayDao.getAllParsedDataHandles());
        return references;
    }

    /**
     * {@inheritDoc}
     */
    public void cleanupUnreferencedChildren() {
        LogUtil.logSubsystemEntry(LOG);
        fileDao.cleanupUnreferencedChildren();
        LogUtil.logSubsystemExit(LOG);
    }


    /**
     * @param fileDao the fileDao to set
     */
    @Inject
    public void setFileDao(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    /**
     * @param arrayDao the arrayDao to set
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * @param dataStorageFacade the dataStorageFacade to set
     */
    @Inject
    public void setDataStorageFacade(DataStorageFacade dataStorageFacade) {
        this.dataStorageFacade = dataStorageFacade;
    }

    /**
     * @param typeRegistry the typeRegistry to set
     */
    @Inject
    public void setTypeRegistry(FileTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }
}
