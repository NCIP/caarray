//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
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
import java.io.PrintWriter;
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
     * @param typeStr GEO SOFT packaging type.
     */
    public void setType(String typeStr) {
        this.type = PackagingMethod.valueOf(typeStr);
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
        return Action.NONE;
    }

    private void zipAndSendResponse(HttpServletResponse response, String baseFileName, File idfFile, File sdrfFile)
            throws IOException {
        FileInputStream fis;
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "filename=\"" + baseFileName + ".magetab.zip" + "\"");
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
        response.addHeader("Content-Disposition", "filename=\"" + fileName + "\"");
        String permaLink = getProjectPermaLink();
        OutputStream out = response.getOutputStream();
        service.export(getProject(), permaLink, type, out);
        out.flush();
        return Action.NONE;
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
        response.setContentType("text/plain; charset=UTF-8");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        String permaLink = getProjectPermaLink();
        PrintWriter pw = response.getWriter();
        service.writeGeoSoftFile(getProject(), permaLink, pw);
        pw.flush();
        return Action.NONE;
    }


}
