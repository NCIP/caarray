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
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Implementation of the FileAccess subsystem.
 */
@Local(FileAccessService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
public class FileAccessServiceBean implements FileAccessService {

    private static final Logger LOG = Logger.getLogger(FileAccessServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile caArrayFile = doAddFile(file, file.getName());
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile caArrayFile = doAddFile(file, filename);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename) {
        CaArrayFile caArrayFile = createCaArrayFile(filename);
        try {
            caArrayFile.writeContents(stream);
        } catch (IOException e) {
            throw new FileAccessException("Stream " + filename + " couldn't be written", e);
        }
        return caArrayFile;
    }


    private CaArrayFile doAddFile(File file, String filename) {
        CaArrayFile caArrayFile = createCaArrayFile(filename);
        try {
            InputStream inputStream = FileUtils.openInputStream(file);
            caArrayFile.writeContents(inputStream);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            throw new FileAccessException("File " + file.getAbsolutePath() + " couldn't be written", e);
        }
        return caArrayFile;
    }

    private CaArrayFile createCaArrayFile(String filename) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        setTypeFromExtension(caArrayFile, filename);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public void remove(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        // special condition must be addressed if this file has import status
        // if imported the file must not be associated with a hyb
        if (!caArrayFile.isDeletable()) {
            throw new IllegalArgumentException("Illegal attempt to delete " + caArrayFile.getName()
                    + ", status is " + caArrayFile.getFileStatus());
        }

        caArrayFile.getProject().getFiles().remove(caArrayFile);

        this.daoFactory.getFileDao().remove(caArrayFile);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void save(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        this.daoFactory.getFileDao().save(caArrayFile);
        LogUtil.logSubsystemExit(LOG);
    }

    private void setTypeFromExtension(CaArrayFile caArrayFile, String filename) {
        FileType type = FileExtension.getTypeFromExtension(filename);
        if (type != null) {
            caArrayFile.setFileType(type);
        }
    }

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void unzipFiles(List<File> files, List<String> fileNames) {
        try {
            Pattern p = Pattern.compile("\\.zip$");
            int index = 0;
            for (int i = 0; i < fileNames.size(); i++) {
                Matcher m = p.matcher(fileNames.get(i).toLowerCase());

                if (m.find()) {
                    File file = files.get(i);
                    String fileName = file.getAbsolutePath();
                    String directoryPath = file.getParent();
                    ZipFile zipFile = new ZipFile(fileName);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        File entryFile = new File(directoryPath + "/" + entry.getName());

                        InputStream fileInputStream = zipFile.getInputStream(entry);
                        FileOutputStream fileOutputStream = new FileOutputStream(entryFile);
                        IOUtils.copy(fileInputStream, fileOutputStream);
                        IOUtils.closeQuietly(fileOutputStream);

                        files.add(entryFile);
                        fileNames.add(entry.getName());
                    }
                    zipFile.close();
                    files.remove(index);
                    fileNames.remove(index);
                }
                index++;
            }
        } catch (IOException e) {
            throw new FileAccessException("Couldn't unzip archive.", e);
        }
    }
}