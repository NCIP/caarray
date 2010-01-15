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
import gov.nih.nci.caarray.external.v1_0.data.FileStreamableContents;
import gov.nih.nci.caarray.external.v1_0.data.MageTabFileSet;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

/**
 * Implementation of DataApiUtils for the Java API.
 * 
 * @author dkokotov
 */
public class JavaDataApiUtils extends AbstractDataApiUtils {
    private final DataService dataService;

    /**
     * @param dataService the DataService EJB to use to do the API calls.
     */
    public JavaDataApiUtils(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * {@inheritDoc}
     */
    public void copyFileContentsToOutputStream(CaArrayEntityReference fileRef, boolean compressed, OutputStream os)
            throws InvalidReferenceException, DataTransferException, IOException {
        FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
        readFully(fsContents.getContentStream(), os, !compressed);
    }
    
    /**
     * {@inheritDoc}
     */
    public void copyFileContentsZipToOutputStream(Iterable<CaArrayEntityReference> fileRefs, OutputStream ostream)
            throws InvalidReferenceException, DataTransferException, IOException {
        ZipOutputStream zos = new ZipOutputStream(ostream);
        for (CaArrayEntityReference fileRef : fileRefs) {
            FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
            zos.putNextEntry(new ZipEntry(fsContents.getMetadata().getName()));
            readFully(fsContents.getContentStream(), zos, true);
        }
        zos.finish();
    }
    
    /**
     * {@inheritDoc}
     */
    public void downloadFileContentsToDir(Iterable<CaArrayEntityReference> fileRefs, File dir)
            throws InvalidReferenceException, DataTransferException, IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Could not create temporary directory " + dir.getAbsolutePath());
        }
        for (CaArrayEntityReference fileRef : fileRefs) {
            FileStreamableContents fsContents = dataService.streamFileContents(fileRef, true);
            FileOutputStream fos = FileUtils.openOutputStream(new File(dir, fsContents.getMetadata().getName()));
            readFully(fsContents.getContentStream(), fos, true);
            IOUtils.closeQuietly(fos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected MageTabFileSet exportMageTab(CaArrayEntityReference experimentRef) throws InvalidReferenceException,
            DataTransferException {
        return dataService.exportMageTab(experimentRef);
    }

    /**
     * Read data fully from the given RemoteInputStream and write it to the given OutputStream. The RemoteInputStream is
     * always closed at the end of this method, regardless of whether an exception occurs.
     * 
     * @param ris the RemoteInputStream to read from
     * @param ostream the OutputStream to write to
     * @param decompress if true, then the data is expected to be compressed with GZip and will be decompressed before
     *            being written to the OutputStream
     * @throws IOException if there is an error reading from the RemoteInputStream or writing to the OutputStream
     */
    public static void readFully(RemoteInputStream ris, OutputStream ostream, boolean decompress) throws IOException {
        InputStream istream = null;
        try {
            istream = RemoteInputStreamClient.wrap(ris);
            if (decompress) {
                istream = new GZIPInputStream(istream);
            }
            IOUtils.copy(istream, ostream);
        } finally {
            if (istream != null) {
                istream.close();
            }
        }
    }
}
