//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.translation.geosoft.GeoSoftExporter;
import gov.nih.nci.caarray.application.translation.geosoft.PackagingInfo;
import gov.nih.nci.caarray.application.translation.geosoft.PackagingInfo.PackagingMethod;
import gov.nih.nci.caarray.application.translation.magetab.MageTabExporter;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.injection.InjectorFactory;

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

import com.google.inject.Injector;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Preparable;

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
        return this.geoValidation;
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
        return this.geoZipOk;
    }

    /**
     * show export page.
     * 
     * @return success
     */
    @SkipValidation
    public String details() {

        final GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
        this.geoValidation = service.validateForExport(getExperiment());
        final List<PackagingInfo> infos = service.getAvailablePackagingInfos(getProject());
        this.geoZipOk = false;
        for (final PackagingInfo pi : infos) {
            this.geoZipOk |= pi.getMethod() == PackagingMethod.ZIP;
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
        final Experiment experiment = getExperiment();
        final HttpServletResponse response = ServletActionContext.getResponse();
        final FileInputStream fis = null;
        // Create temporary files to store the resulting MAGE-TAB.
        final String baseFileName = experiment.getPublicIdentifier();
        final String idfFileName = baseFileName + ".idf";
        final String sdrfFileName = baseFileName + ".sdrf";

        final Injector injector = InjectorFactory.getInjector();
        final TemporaryFileCache tempCache = injector.getInstance(TemporaryFileCache.class);
        final File idfFile = tempCache.createFile(idfFileName);
        final File sdrfFile = tempCache.createFile(sdrfFileName);
        try {
            // Translate the experiment and export to the temporary files.
            final MageTabExporter exporter = ServiceLocatorFactory.getMageTabExporter();
            exporter.exportToMageTab(getExperiment(), idfFile, sdrfFile);

            // Zip up the temporary files and send as response.
            zipAndSendResponse(response, baseFileName, idfFile, sdrfFile);

        } catch (final Exception e) {
            LOG.error("Error exporting to MAGE-TAB", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Delete temporary files.
            tempCache.delete(idfFileName);
            tempCache.delete(sdrfFileName);
        }
        return Action.NONE;
    }

    private void zipAndSendResponse(HttpServletResponse response, String baseFileName, File idfFile, File sdrfFile)
            throws IOException {
        final Injector injector = InjectorFactory.getInjector();
        final FileAccessUtils fileAccessUtils = injector.getInstance(FileAccessUtils.class);
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "filename=\"" + baseFileName + ".magetab.zip" + "\"");
        final OutputStream outStream = response.getOutputStream();
        final ZipOutputStream zipOutStream = new ZipOutputStream(outStream);
        fileAccessUtils.writeZipEntry(zipOutStream, idfFile, false);
        fileAccessUtils.writeZipEntry(zipOutStream, sdrfFile, false);
        zipOutStream.flush();
        zipOutStream.finish();
    }

    /**
     * GEO SOFT archive export, used the type property to determin the packaging format.
     * 
     * @return success
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String exportToGeoArchive() throws IOException {
        try {
            final GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
            final HttpServletResponse response = ServletActionContext.getResponse();
            final String fileName = getExperiment().getPublicIdentifier() + this.type.getExtension();
            response.setContentType(this.type.getMimeType());
            response.addHeader("Content-Disposition", "filename=\"" + fileName + "\"");
            final String permaLink = getProjectPermaLink();
            final OutputStream out = response.getOutputStream();
            service.export(getProject(), permaLink, this.type, out);
            out.flush();
            return Action.NONE;
        } catch (final IOException e) {
            LOG.error(e);
            throw e;
        } catch (final RuntimeException e) {
            LOG.error(e);
            throw e;
        }
    }

    /**
     * GEO SOFT Info file export.
     * 
     * @return success
     * @throws IOException if there is an error writing to the stream
     */
    @SkipValidation
    public String exportToGeoInfo() throws IOException {
        try {
            final GeoSoftExporter service = ServiceLocatorFactory.getGeoSoftExporter();
            final HttpServletResponse response = ServletActionContext.getResponse();
            final String fileName = getExperiment().getPublicIdentifier() + ".soft.txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            final String permaLink = getProjectPermaLink();
            final PrintWriter pw = response.getWriter();
            service.writeGeoSoftFile(getProject(), permaLink, pw);
            pw.flush();
            return Action.NONE;
        } catch (final IOException e) {
            LOG.error(e);
            throw e;
        } catch (final RuntimeException e) {
            LOG.error(e);
            throw e;
        }
    }

}
