//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.helper;

import gov.nih.nci.caarray.application.fileaccess.FileAccessUtils;
import gov.nih.nci.caarray.application.translation.geosoft.PackagingInfo;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

/**
 * Helper for actions that implement download of files.
 * 
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
     * @param baseFilename the filename w/o the suffix to use for the archive file. This filename will be set as the
     * Content-disposition header
     * @throws IOException if there is an error writing to the stream
     */
    public static void downloadFiles(Collection<CaArrayFile> files, String baseFilename)
            throws IOException {
        HttpServletResponse response = ServletActionContext.getResponse();
        try {
            PackagingInfo info = getPreferedPackageInfo(files, baseFilename);
            response.setContentType(info.getMethod().getMimeType());
            response.addHeader("Content-disposition", "filename=\"" + info.getName() + "\"");

            List<CaArrayFile> sortedFiles = new ArrayList<CaArrayFile>(files);
            Collections.sort(sortedFiles, CAARRAYFILE_NAME_COMPARATOR_INSTANCE);
            OutputStream sos = response.getOutputStream();
            OutputStream closeShield = new CloseShieldOutputStream(sos);
            ArchiveOutputStream arOut = info.getMethod().createArchiveOutputStream(closeShield);
            try {
                for (CaArrayFile f : sortedFiles) {
                    FileAccessUtils.addFileToArchive(f, arOut);
                    f.clearAndEvictContents();
                }
            } finally {
                // note that the caller's stream is shielded from the close(),
                // but this is the only way to finish and flush the (gzip) stream.
                try {
                    arOut.close();
                } catch (Exception e) {
                    LOG.error(e);
                }
            }
        } catch (Exception e) {
            LOG.error("Error streaming download", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static long getEstimatedPackageSize(Collection<CaArrayFile> files) {
        long size = 0L;
        for (CaArrayFile f : files) {
            size += f.getCompressedSize();
        }
        return size;
    }

    private static PackagingInfo getPreferedPackageInfo(Collection<CaArrayFile> files, String baseFilename) {
        long size = getEstimatedPackageSize(files);
        if (size > PackagingInfo.MAX_ZIP_SIZE) {
            return new PackagingInfo(
                    baseFilename + PackagingInfo.PackagingMethod.TGZ.getExtension(),
                    PackagingInfo.PackagingMethod.TGZ);
        } else {
            return new PackagingInfo(
                    baseFilename + PackagingInfo.PackagingMethod.ZIP.getExtension(),
                    PackagingInfo.PackagingMethod.ZIP);
        }
    }
}
