//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.helper;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
/**
 * Allows for both project and aray design downloads of files.
 * @author mshestopalov
 *
 */
public final class DownloadHelper {
    private static final String DOWNLOAD_CONTENT_TYPE = "application/zip";
    private static final Logger LOG = Logger.getLogger(DownloadHelper.class);
    /**
     * an instance of a Comparator that compares CaArrayFile instances by name.
     */
    public static final Comparator<CaArrayFile> CAARRAYFILE_NAME_COMPARATOR_INSTANCE = new CaArrayFileNameComparator();
    private DownloadHelper() {
        // nothing here.
    }

    /**
     * Comparator class that compares CaArrayFile instances by name, alphabetically.
     */
    private static class CaArrayFileNameComparator implements Comparator<CaArrayFile> {
        public int compare(CaArrayFile f1, CaArrayFile f2) {
            return new CompareToBuilder().append(f1.getName(), f2.getName()).toComparison();
        }
    }

    /**
     * Zips the selected files and writes the result to the servlet output stream. Also sets content type and
     * disposition appropriately.
     *
     * @param files the files to zip and send
     * @param filename the filename to use for the zip file. This filename will be set as the Content-disposition header
     * @throws IOException if there is an error writing to the stream
     */
    public static void downloadFiles(Collection<CaArrayFile> files, String filename)
            throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        FileInputStream fis = null;
        try {
            response.setContentType(DOWNLOAD_CONTENT_TYPE);
            response.addHeader("Content-disposition", "filename=\"" + filename + "\"");

            List<CaArrayFile> sortedFiles = new ArrayList<CaArrayFile>(files);
            Collections.sort(sortedFiles, CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
            OutputStream sos = response.getOutputStream();
            ZipOutputStream zos = new ZipOutputStream(sos);
            TemporaryFileCache tempCache = TemporaryFileCacheLocator.getTemporaryFileCache();
            for (CaArrayFile caf : sortedFiles) {
                File f = tempCache.getFile(caf);
                fis = new FileInputStream(f);
                ZipEntry ze = new ZipEntry(f.getName());
                zos.putNextEntry(ze);
                IOUtils.copy(fis, zos);
                zos.closeEntry();
                fis.close();
                tempCache.closeFile(caf);
                zos.flush();
            }
            zos.finish();
        } catch (Exception e) {
            LOG.error("Error streaming download", e);
            IOUtils.closeQuietly(fis);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }



}
