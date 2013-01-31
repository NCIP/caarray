//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.file.CaArrayFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Utility class for working with files.
 * 
 * @author dkokotov
 */
public final class FileAccessUtils {
    /**
     * Suffix to add to file names when they contain the gzipped contents of another file.
     */
    public static final String GZIP_FILE_SUFFIX = ".gz";
    
    private FileAccessUtils() {
        // empty - utility class
    }
    
    /**
     * Adds the given file to the given zip output stream, using the file's name as the zip entry name. This method will
     * NOT call finish on the zip output stream at the end.
     *
     * @param zos the zip output stream to add the file to. This stream must already be open.
     * @param file the file to put in the zip.
     * @param addAsStored if true, then the file will be added to the zip as a STORED entry (e.g. without applying
     *            compression to it); if false, then the file will be added to the zip as a DEFLATED entry.
     * @throws IOException if there is an error writing to the stream
     */
    public static void writeZipEntry(ZipOutputStream zos, File file, boolean addAsStored) throws IOException {
        writeZipEntry(zos, file, file.getName(), addAsStored);
}

    /**
     * Adds the given file to the given zip output stream using the given name as the zip entry name. This method will
     * NOT call finish on the zip output stream at the end.
     *
     * @param zos the zip output stream to add the file to. This stream must already be open.
     * @param file the file to put in the zip.
     * @param name the name to use for this zip entry.
     * @param addAsStored if true, then the file will be added to the zip as a STORED entry (e.g. without applying
     *            compression to it); if false, then the file will be added to the zip as a DEFLATED entry.
     * @throws IOException if there is an error writing to the stream
     */
    public static void writeZipEntry(ZipOutputStream zos, File file, String name, boolean addAsStored)
            throws IOException {
        ZipEntry ze = new ZipEntry(name);
        ze.setMethod(addAsStored ? ZipEntry.STORED : ZipEntry.DEFLATED);
        if (addAsStored) {
            ze.setSize(file.length());
            ze.setCrc(FileUtils.checksumCRC32(file));
        }
        zos.putNextEntry(ze);
        InputStream is = FileUtils.openInputStream(file);
        IOUtils.copy(is, zos);
        zos.closeEntry();
        zos.flush();
        IOUtils.closeQuietly(is);
    }

    /**
     * Add a file to an archive.
     * @param f the file to add.
     * @param zout the archive stream. {@link TarArchiveOutputStream} or {@link ZipArchiveOutputStream}
     * @throws IOException when writeing to the stream fails.
     */
    public static void addFileToArchive(CaArrayFile f, ArchiveOutputStream zout) throws IOException {
        ArchiveEntry ae = createArchiveEntry(zout, f.getName(), f.getUncompressedSize());
        zout.putArchiveEntry(ae);
        InputStream is = f.readContents();
        IOUtils.copy(is, zout);
        is.close();
        zout.closeArchiveEntry();
    }

    /**
     * This method creates an ArchiveEntry w/o the need of a File required by
     * ArchiveOutputStream.createArchiveEntry(File inputFile, String entryName).
     * @param aos archive output stream.
     * @param name name of entry.
     * @param size size in bytes of the entry.
     * @return an archive entry that matches the archive stream.
     */
    public static ArchiveEntry createArchiveEntry(ArchiveOutputStream aos, String name, long size) {
        if (aos instanceof TarArchiveOutputStream) {
            TarArchiveEntry te = new TarArchiveEntry(name);
            te.setSize(size);
            return te;
        } else if (aos instanceof ZipArchiveOutputStream) {
            ZipArchiveEntry ze = new ZipArchiveEntry(name);
            ze.setSize(size);
            return ze;
        }
        throw new UnsupportedOperationException("unsupported archive " + aos.getClass());
    }
}
