//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.application.translation.geosoft;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 *
 * @author gax
 */
public class PackagingInfo {
    /**
     * a conservative 2GB (~ 1.8GB).
     */
    // CHECKSTYLE:OFF magic numbers
    public static final long MAX_ZIP_SIZE = 1024L * 1024L * (768L + 1024L); // ~ 1.8GB
    // CHECKSTYLE:ON

    private String name;
    private PackagingMethod method;

    /**
     * create a package info.
     * @param name file name of the archie.
     * @param method archive format to be use.
     * @see GeoSoftExporter#getPackageingInfo(gov.nih.nci.caarray.domain.project.Experiment) 
     */
    public PackagingInfo(String name, PackagingMethod method) {
        this.name = name;
        this.method = method;
    }

    /**
     * @return packageing method
     */
    public PackagingMethod getMethod() {
        return method;
    }

    /**
     * @return file name of the package.
     */
    public String getName() {
        return name;
    }

    /**
     * Prefered packaging method for GeoSoftExporter.
     */
    public static enum PackagingMethod {
        /**
         * GZIP compressed TAR (tape archive).
         */
        TGZ("gzip compressed tar", "application/x-tar-gz", ".tgz"),

        /**
         * ZIP compressed archive.
         */
        ZIP("zip", "application/zip", ".zip");

        private static final long serialVersionUID = -617456696041521359L;

        private String description;
        private String mimeType;
        private String extension;

        private PackagingMethod(String description, String mimeType, String extension) {
            this.description = description;
            this.mimeType = mimeType;
            this.extension = extension;
        }

        /**
         * @return simple description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return prefered file name extention.
         */
        public String getExtension() {
            return extension;
        }

        /**
         * @return mime type of the archive stream.
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * wrap the output stream with an archive output stream.
         * @param out the scream to write the archive to.
         * @throws IOException when creating the archive stream fails.
         * @return wrapper archive stream.
         */
        public ArchiveOutputStream createArchiveOutputStream(OutputStream out) throws IOException {
            ArchiveOutputStream arOut;
            switch(this) {
                case ZIP:
                    arOut = new ZipArchiveOutputStream(out);
                    break;
                case TGZ:
                    GZIPOutputStream gz = new GZIPOutputStream(out);
                    arOut = new TarArchiveOutputStream(gz);
                    break;
                default:
                    throw new UnsupportedOperationException(this.name());
            }
            return arOut;
        }
    }

}
