//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.file.CaArrayFile;

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
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

/**
 * Utility class for working with files.
 * 
 * @author dkokotov
 */
public class FileAccessUtils {
    private final DataStorageFacade dataStorageFacade;

    /**
     * Constructor.
     * 
     * @param fileStorageFacade file storage facade to use for retrieving file data.
     */
    @Inject
    public FileAccessUtils(DataStorageFacade fileStorageFacade) {
        this.dataStorageFacade = fileStorageFacade;
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
    public void writeZipEntry(ZipOutputStream zos, File file, boolean addAsStored) throws IOException {
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
    public void writeZipEntry(ZipOutputStream zos, File file, String name, boolean addAsStored) throws IOException {
        final ZipEntry ze = new ZipEntry(name);
        ze.setMethod(addAsStored ? ZipEntry.STORED : ZipEntry.DEFLATED);
        if (addAsStored) {
            ze.setSize(file.length());
            ze.setCrc(FileUtils.checksumCRC32(file));
        }
        zos.putNextEntry(ze);
        final InputStream is = FileUtils.openInputStream(file);
        IOUtils.copy(is, zos);
        zos.closeEntry();
        zos.flush();
        IOUtils.closeQuietly(is);
    }

    /**
     * Add a file to an archive.
     * 
     * @param f the file to add.
     * @param zout the archive stream. {@link TarArchiveOutputStream} or {@link ZipArchiveOutputStream}
     * @throws IOException when writeing to the stream fails.
     */
    public void addFileToArchive(CaArrayFile f, ArchiveOutputStream zout) throws IOException {
        final ArchiveEntry ae = createArchiveEntry(zout, f.getName(), f.getUncompressedSize());
        zout.putArchiveEntry(ae);
        this.dataStorageFacade.copyDataToStream(f.getDataHandle(), zout);
        zout.closeArchiveEntry();
    }

    /**
     * This method creates an ArchiveEntry w/o the need of a File required by
     * ArchiveOutputStream.createArchiveEntry(File inputFile, String entryName).
     * 
     * @param aos archive output stream.
     * @param name name of entry.
     * @param size size in bytes of the entry.
     * @return an archive entry that matches the archive stream.
     */
    public ArchiveEntry createArchiveEntry(ArchiveOutputStream aos, String name, long size) {
        if (aos instanceof TarArchiveOutputStream) {
            final TarArchiveEntry te = new TarArchiveEntry(name);
            te.setSize(size);
            return te;
        } else if (aos instanceof ZipArchiveOutputStream) {
            final ZipArchiveEntry ze = new ZipArchiveEntry(name);
            ze.setSize(size);
            return ze;
        }
        throw new UnsupportedOperationException("unsupported archive " + aos.getClass());
    }

    /**
     * Unzips a .zip file, removes it from <code>files</code> and adds the extracted files to <code>files</code>.
     * 
     * @param files the list of files to unzip and the files extracted from the zips
     * @param fileNames the list of filenames to go along with the list of files
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void unzipFiles(List<File> files, List<String> fileNames) {
        try {
            final Pattern p = Pattern.compile("\\.zip$");
            int index = 0;
            for (int i = 0; i < fileNames.size(); i++) {
                final Matcher m = p.matcher(fileNames.get(i).toLowerCase());

                if (m.find()) {
                    final File file = files.get(i);
                    final String fileName = file.getAbsolutePath();
                    final String directoryPath = file.getParent();
                    final ZipFile zipFile = new ZipFile(fileName);
                    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        final ZipEntry entry = entries.nextElement();
                        final File entryFile = new File(directoryPath + "/" + entry.getName());

                        final InputStream fileInputStream = zipFile.getInputStream(entry);
                        final FileOutputStream fileOutputStream = new FileOutputStream(entryFile);
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
        } catch (final IOException e) {
            throw new FileAccessException("Couldn't unzip archive.", e);
        }
    }
}
