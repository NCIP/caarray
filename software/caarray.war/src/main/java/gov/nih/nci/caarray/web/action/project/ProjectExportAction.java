/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-war Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and
 * have distributed to and by third parties the caarray-war Software and any
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
package gov.nih.nci.caarray.web.action.project;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Preparable;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.translation.geosoft.GeoSoftExporter;
import gov.nih.nci.caarray.application.translation.geosoft.PackagingInfo;
import gov.nih.nci.caarray.application.translation.geosoft.PackagingInfo.PackagingMethod;
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.domain.project.Experiment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.validation.SkipValidation;

/**
 *
 * @author gax
 */
public class ProjectExportAction extends AbstractBaseProjectAction implements Preparable {

    private static final Logger LOG = Logger.getLogger(ProjectExportAction.class);

    private List<String> geoValidation;
    private PackagingMethod type;
    private boolean geoZipOk;

    /**
     * @return validation messages.
     */
    public List<String> getGeoValidation() {
        return geoValidation;
    }

    /**
     * @param type GEO SOFT packaging type.
     */
    public void setType(PackagingMethod type) {
        this.type = type;
    }

    /**
     * @return true if this the experiment is small enough to fit in a ZIP archive.
     */
    public boolean isGeoZipOk() {
        return geoZipOk;
    }





    /**
     * show export page.
     * @return success
     */
    @SkipValidation
    public String details() {

        GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
        geoValidation = service.validateForExport(getExperiment());
        List<PackagingInfo> infos = service.getAvailablePackagingInfos(getProject());
        geoZipOk = false;
        for (PackagingInfo pi : infos) {
            geoZipOk |= pi.getMethod() == PackagingMethod.ZIP;
        }
        return Action.SUCCESS;
    }

    /**
     * Exports the content of the Experiment, constructing a MAGE-TAB file set describing the sample-data relationships
     * and annotations.
     *
     * @return the result of performing the action
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String exportToMageTab() throws IOException {
        Experiment experiment = getExperiment();
        HttpServletResponse response = ServletActionContext.getResponse();
        FileInputStream fis = null;
        TemporaryFileCache tempCache = TemporaryFileCacheLocator.getTemporaryFileCache();
        // Create temporary files to store the resulting MAGE-TAB.
        String baseFileName = experiment.getPublicIdentifier();
        String idfFileName = baseFileName + ".idf";
        String sdrfFileName = baseFileName + ".sdrf";

        File idfFile = tempCache.createFile(idfFileName);
        File sdrfFile = tempCache.createFile(sdrfFileName);
        try {            
            // Translate the experiment and export to the temporary files.
            MageTabExporter exporter = ServiceLocatorFactory.getMageTabExporter();
            exporter.exportToMageTab(getExperiment(), idfFile, sdrfFile);

            // Zip up the temporary files and send as response.
            zipAndSendResponse(response, baseFileName, idfFile, sdrfFile);

        } catch (Exception e) {
            LOG.error("Error exporting to MAGE-TAB", e);            
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Delete temporary files.
            tempCache.delete(idfFile);
            tempCache.delete(sdrfFile);
        }
        return Action.SUCCESS;
    }

    private void zipAndSendResponse(HttpServletResponse response, String baseFileName, File idfFile, File sdrfFile)
            throws IOException {
        FileInputStream fis;
        response.setContentType("application/zip");
        response.addHeader("Content-disposition", "filename=\"" + baseFileName + ".magetab.zip" + "\"");
        OutputStream outStream = response.getOutputStream();
        ZipOutputStream zipOutStream = new ZipOutputStream(outStream);
        FileAccessUtils.writeZipEntry(zipOutStream, idfFile, false);
        FileAccessUtils.writeZipEntry(zipOutStream, sdrfFile, false);
        zipOutStream.flush();
        zipOutStream.finish();
    }

    /**
     * GEO SOFT archive export, used the type property to determin the packaging format.
     * @return success
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String exportToGeoArchive() throws IOException {
        GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
        HttpServletResponse response = ServletActionContext.getResponse();
        String fileName = getExperiment().getPublicIdentifier() + type.getExtension();
        response.setContentType(type.getMimeType());
        response.addHeader("Content-disposition", "filename=\"" + fileName + "\"");
        String permaLink = getProjectPermaLink();
        service.export(getProject(), permaLink, type, response.getOutputStream());
        return Action.SUCCESS;
    }

    /**
     * GEO SOFT Info file export.
     * @return success
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String exportToGeoInfo() throws IOException {
        GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
        HttpServletResponse response = ServletActionContext.getResponse();
        String fileName = getExperiment().getPublicIdentifier() + ".soft.txt";
        response.setContentType("text/plain");
        response.addHeader("Content-disposition", "filename=\"" + fileName + "\"");
        String permaLink = getProjectPermaLink();
        service.writeGeoSoftFile(getProject(), permaLink, response.getWriter());
        return Action.SUCCESS;
    }


}