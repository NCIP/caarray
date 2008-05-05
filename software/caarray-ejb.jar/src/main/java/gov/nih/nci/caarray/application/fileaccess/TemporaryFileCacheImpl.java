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

import gov.nih.nci.caarray.domain.ConfigParamEnum;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.util.ConfigurationHelper;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.server.UID;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Default implementation of TemporaryFileCache.
 * @author dkokotov
 */
public final class TemporaryFileCacheImpl implements TemporaryFileCache {
    private static final Logger LOG = Logger.getLogger(TemporaryFileCacheImpl.class);
    private static final String WORKING_DIRECTORY_PROPERTY_KEY = "caarray.working.directory";
    private static final String TEMP_DIR_PROPERTY_KEY = "java.io.tmpdir";

    private final Map<CaArrayFile, File> openFiles = new HashMap<CaArrayFile, File>();
    private File sessionWorkingDirectory;

    TemporaryFileCacheImpl() {
        // nothing to do
    }

    /**
     * Returns a file <code>java.io.File</code> which will hold the uncompressed data for the
     * <code>CaArrayFile</code> object provided. The client should eventually call closeFile() for this
     * <code>CaArrayFile</code> (or closeFiles()) to allow the temporary file to be cleanded up.
     *
     * @param caArrayFile logical file whose contents are needed
     * @return the <code>java.io.File</code> pointing to the temporary file on the filesystem which will
     * hold the uncompressed contents of the given logical file.
     */
    public File getFile(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        File file;
        if (fileAlreadyOpened(caArrayFile)) {
            file = getOpenFile(caArrayFile);
        } else {
            file = openFile(caArrayFile);
        }
        LogUtil.logSubsystemExit(LOG);
        return file;
    }

    private File openFile(CaArrayFile caArrayFile) {
        File file = new File(getSessionWorkingDirectory(), caArrayFile.getName());
        try {
            // re-fetch the CaArrayFile instance to ensure that its blob contents are loaded
            HibernateUtil.getCurrentSession().refresh(caArrayFile);

            InputStream inputStream = caArrayFile.readContents();
            OutputStream outputStream = FileUtils.openOutputStream(file);
            IOUtils.copy(inputStream, outputStream);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(outputStream);
            caArrayFile.clearAndEvictContents();
        } catch (IOException e) {
            throw new FileAccessException("Couldn't access file contents " + caArrayFile.getName(), e);
        }
        this.openFiles.put(caArrayFile, file);
        return file;
    }

    private File getSessionWorkingDirectory() {
        if (this.sessionWorkingDirectory == null) {
            createSessionWorkingDirectory();
        }
        return this.sessionWorkingDirectory;
    }

    private void createSessionWorkingDirectory() {
        String sessionSubdirectoryName = new UID().toString().replace(':', '_');
        this.sessionWorkingDirectory = new File(getWorkingDirectory(), sessionSubdirectoryName);
        if (!this.sessionWorkingDirectory.mkdirs()) {
            LOG.error("Couldn't create directory: " + this.sessionWorkingDirectory.getAbsolutePath());
            throw new IllegalStateException("Couldn't create directory: "
                    + this.sessionWorkingDirectory.getAbsolutePath());
        }
    }

    private File getWorkingDirectory() {
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        String tempDir = config.getString(ConfigParamEnum.STRUTS_MULTIPART_SAVEDIR.name());
        if (StringUtils.isBlank(tempDir)) {
            tempDir = System.getProperty(TEMP_DIR_PROPERTY_KEY);
        }
        String workingDirectoryPath = System.getProperty(WORKING_DIRECTORY_PROPERTY_KEY, tempDir);
        return new File(workingDirectoryPath);
    }

    private boolean fileAlreadyOpened(CaArrayFile caArrayFile) {
        return this.openFiles.containsKey(caArrayFile);
    }

    private File getOpenFile(CaArrayFile caArrayFile) {
        return this.openFiles.get(caArrayFile);
    }

    /**
     * Closes all temporary files opened in the current session and deletes the temporary directory used to store them.
     * This method should always be called at the conclusion of a session of working with file data.
     */
    public void closeFiles() {
        Set<CaArrayFile> filesToClose = new HashSet<CaArrayFile>(this.openFiles.keySet());
        for (CaArrayFile caarrayFile : filesToClose) {
            closeFile(caarrayFile);
        }
        delete(getSessionWorkingDirectory());
        this.sessionWorkingDirectory = null;
    }

    /**
     * Closes the file corresponding to the given logical file opened in the current session. Note that at the end
     * of the session of working with file data, you should still call closeFiles() to perform final cleanup
     * even if all files had been previously closed via calls to this method
     * @param caarrayFile the logical file to close the filesystem file for
     */
    public void closeFile(CaArrayFile caarrayFile) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing session file in directory: " + getSessionWorkingDirectory() + " for caarray file "
                    + caarrayFile.getName());
        }
        if (this.sessionWorkingDirectory == null) {
            return;
        }
        File file = getOpenFile(caarrayFile);
        if (file != null) {
            delete(file);
        }
        this.openFiles.remove(caarrayFile);
    }

    private void delete(File file) {
        LOG.debug("Deleting file: " + file.getAbsolutePath());
        if (!file.delete()) {
            LOG.warn("Couldn't delete file: " + file.getAbsolutePath());
            file.deleteOnExit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize() throws Throwable {
        closeFiles();
        super.finalize();
    }
}
