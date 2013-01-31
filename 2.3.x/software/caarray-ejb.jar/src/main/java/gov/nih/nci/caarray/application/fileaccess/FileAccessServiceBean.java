//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.application.ExceptionLoggingInterceptor;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

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

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 * Implementation of the FileAccess subsystem.
 */
@Local(FileAccessService.class)
@Stateless
@Interceptors(ExceptionLoggingInterceptor.class)
public class FileAccessServiceBean implements FileAccessService {

    private static final Logger LOG = Logger.getLogger(FileAccessServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile caArrayFile = doAddFile(file, file.getName());
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(File file, String filename) {
        LogUtil.logSubsystemEntry(LOG, file);
        CaArrayFile caArrayFile = doAddFile(file, filename);
        LogUtil.logSubsystemExit(LOG);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename) {
        CaArrayFile caArrayFile = createCaArrayFile(filename);
        try {
            caArrayFile.writeContents(stream);
        } catch (IOException e) {
            throw new FileAccessException("Stream " + filename + " couldn't be written", e);
        }
        return caArrayFile;
    }


    private CaArrayFile doAddFile(File file, String filename) {
        CaArrayFile caArrayFile = createCaArrayFile(filename);
        try {
            InputStream inputStream = FileUtils.openInputStream(file);
            caArrayFile.writeContents(inputStream);
            IOUtils.closeQuietly(inputStream);
        } catch (IOException e) {
            throw new FileAccessException("File " + file.getAbsolutePath() + " couldn't be written", e);
        }
        return caArrayFile;
    }

    private CaArrayFile createCaArrayFile(String filename) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        setTypeFromExtension(caArrayFile, filename);
        return caArrayFile;
    }

    /**
     * {@inheritDoc}
     */
    public void remove(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        // special condition must be addressed if this file has import status
        // if imported the file must not be associated with a hyb
        if (!caArrayFile.isDeletable()) {
            throw new IllegalArgumentException("Illegal attempt to delete " + caArrayFile.getName()
                    + ", status is " + caArrayFile.getFileStatus());
        }

        caArrayFile.getProject().getFiles().remove(caArrayFile);

        this.daoFactory.getFileDao().remove(caArrayFile);
        LogUtil.logSubsystemExit(LOG);
    }

    /**
     * {@inheritDoc}
     */
    public void save(CaArrayFile caArrayFile) {
        LogUtil.logSubsystemEntry(LOG, caArrayFile);
        this.daoFactory.getFileDao().save(caArrayFile);
        LogUtil.logSubsystemExit(LOG);
    }

    private void setTypeFromExtension(CaArrayFile caArrayFile, String filename) {
        FileType type = FileExtension.getTypeFromExtension(filename);
        if (type != null) {
            caArrayFile.setFileType(type);
        }
    }

    CaArrayDaoFactory getDaoFactory() {
        return this.daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void unzipFiles(List<File> files, List<String> fileNames) {
        try {
            Pattern p = Pattern.compile("\\.zip$");
            int index = 0;
            for (int i = 0; i < fileNames.size(); i++) {
                Matcher m = p.matcher(fileNames.get(i).toLowerCase());

                if (m.find()) {
                    File file = files.get(i);
                    String fileName = file.getAbsolutePath();
                    String directoryPath = file.getParent();
                    ZipFile zipFile = new ZipFile(fileName);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        File entryFile = new File(directoryPath + "/" + entry.getName());

                        InputStream fileInputStream = zipFile.getInputStream(entry);
                        FileOutputStream fileOutputStream = new FileOutputStream(entryFile);
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
        } catch (IOException e) {
            throw new FileAccessException("Couldn't unzip archive.", e);
        }
    }
}
