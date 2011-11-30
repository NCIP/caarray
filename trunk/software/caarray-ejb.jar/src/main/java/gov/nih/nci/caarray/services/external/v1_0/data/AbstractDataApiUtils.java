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
package gov.nih.nci.caarray.services.external.v1_0.data;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.server.UID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Base implementation of the DataApiUtils interface.
 * 
 * @author dkokotov
 */
public abstract class AbstractDataApiUtils implements DataApiUtils {
    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsToTempFile(CaArrayEntityReference fileRef, boolean compressed)
            throws InvalidReferenceException, DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadFileContentsToFile(fileRef, compressed, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsToFile(CaArrayEntityReference fileRef, boolean compressed, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyFileContentsToOutputStream(fileRef, compressed, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public byte[] getFileContents(CaArrayEntityReference fileRef, boolean compressed) throws InvalidReferenceException,
            DataTransferException, IOException {
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        try {
            copyFileContentsToOutputStream(fileRef, compressed, ostream);
            return ostream.toByteArray();
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsZipToTempFile(Iterable<CaArrayEntityReference> fileRefs)
            throws InvalidReferenceException, DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadFileContentsZipToFile(fileRefs, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsZipToFile(Iterable<CaArrayEntityReference> fileRefs, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyFileContentsZipToOutputStream(fileRefs, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public File downloadFileContentsToTempDir(Iterable<CaArrayEntityReference> fileRefs)
            throws InvalidReferenceException, DataTransferException, IOException {
        String tempDirName = new UID().toString().replace(':', '_');
        File tempDir = new File(System.getProperty("java.io.tmpdir"), tempDirName);
        downloadFileContentsToDir(fileRefs, tempDir);
        return tempDir;
    }

    /**
     * {@inheritDoc}
     */
    public File downloadMageTabZipToTempFile(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException, IOException {
        File tempFile = File.createTempFile("retrievedFile", null);
        downloadMageTabZipToFile(experimentRef, tempFile);
        return tempFile;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadMageTabZipToFile(CaArrayEntityReference experimentRef, File toFile)
            throws InvalidReferenceException, DataTransferException, IOException {
        OutputStream ostream = FileUtils.openOutputStream(toFile);
        try {
            copyMageTabZipToOutputStream(experimentRef, ostream);
        } finally {
            if (ostream != null) {
                ostream.close();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void copyMageTabZipToOutputStream(CaArrayEntityReference experimentRef, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException {
        MageTabFileSet mtset = exportMageTab(experimentRef);
        ZipOutputStream zos = new ZipOutputStream(ostream);
        zos.putNextEntry(new ZipEntry(mtset.getIdf().getMetadata().getName()));
        IOUtils.write(mtset.getIdf().getContents(), zos);
        zos.putNextEntry(new ZipEntry(mtset.getSdrf().getMetadata().getName()));
        IOUtils.write(mtset.getSdrf().getContents(), zos);
        for (gov.nih.nci.caarray.external.v1_0.data.File dataFile : mtset.getDataFiles()) {
            zos.putNextEntry(new ZipEntry(dataFile.getMetadata().getName()));
            copyFileContentsToOutputStream(dataFile.getReference(), false, zos);
        }
        zos.finish();
    }


    /**
     * {@inheritDoc}
     */
    public File downloadMageTabFilesetToTempDir(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException, DataTransferException, IOException {
        String tempDirName = new UID().toString().replace(':', '_');
        File tempDir = new File(System.getProperty("java.io.tmpdir"), tempDirName);
        downloadMageTabFileSetToDir(experimentRef, tempDir);
        return tempDir;
    }

    /**
     * {@inheritDoc}
     */
    public void downloadMageTabFileSetToDir(CaArrayEntityReference experimentRef, File dir)
            throws InvalidReferenceException, DataTransferException, IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create temporary directory " + dir.getAbsolutePath());
        }

        MageTabFileSet mtset = exportMageTab(experimentRef);
        File idf = new File(dir, mtset.getIdf().getMetadata().getName());
        FileUtils.writeByteArrayToFile(idf, mtset.getIdf().getContents());
        File sdrf = new File(dir, mtset.getSdrf().getMetadata().getName());
        FileUtils.writeByteArrayToFile(sdrf, mtset.getSdrf().getContents());
        for (gov.nih.nci.caarray.external.v1_0.data.File dataFile : mtset.getDataFiles()) {
            File dataFileOnDisk = new File(dir, dataFile.getMetadata().getName());
            downloadFileContentsToFile(dataFile.getReference(), false, dataFileOnDisk);
        }
    }
    
    /**
     * Retrieves a set of files containing the mage-tab IDF and SDRF for the experiment identified by the given
     * reference. The IDF and SDRF are generated dynamically. The file set also contains references to the data files
     * referenced by the mage-tab SDRF. This method just reflects the corresponding method from DataService, which
     * subclasses must route to appropriate service implementation.
     * 
     * @param experimentRef reference identifying the experiment
     * @return the set of IDF and SDRF files, and references to corresponding data files.
     * @throws InvalidReferenceException if the experimentRef is not a valid experiment reference.
     * @throws DataTransferException if there is an error generating the mage-tab file data
     */    
    protected abstract MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef)
            throws InvalidReferenceException, DataTransferException;
}
