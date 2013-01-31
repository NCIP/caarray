//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.fileaccess;

import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stub implementation for testing.
 */
public class FileAccessServiceStub implements FileAccessService, TemporaryFileCache {

    private final Map<String, File> nameToFile = new HashMap<String, File>();
    private int savedFileCount = 0;
    private int removedFileCount = 0;

    public CaArrayFile add(File file) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(file.getName());
        setTypeFromExtension(caArrayFile, file.getName());
        this.nameToFile.put(caArrayFile.getName(), file);
        return caArrayFile;
    }

    private void setTypeFromExtension(CaArrayFile caArrayFile, String filename) {
        FileType type = FileExtension.getTypeFromExtension(filename);
        if (type != null) {
            caArrayFile.setFileType(type);
        }
    }

    public File getFile(CaArrayFile caArrayFile) {
        if (this.nameToFile.containsKey(caArrayFile.getName())) {
            return this.nameToFile.get(caArrayFile.getName());
        }
        return new File(caArrayFile.getName());
    }

    public File createFile(String fileName) {
        this.savedFileCount++;
        return new File(fileName);
    }

    public Set<File> getFiles(CaArrayFileSet fileSet) {
        Set<File> files = new HashSet<File>();
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            files.add(getFile(caArrayFile));
        }
        return files;
    }

    public CaArrayFile add(File file, String filename) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        this.nameToFile.put(caArrayFile.getName(), file);
        return caArrayFile;
    }

    public void remove(CaArrayFile caArrayFile) {
        if (caArrayFile.isDeletable()) {
            this.removedFileCount++;
        }
    }


    public void save(CaArrayFile caArrayFile) {
        this.savedFileCount++;
    }

    public void unzipFiles(List<File> uploads, List<String> uploadFileNames) {
        //do nothing
    }

    public void reset() {
        this.nameToFile.clear();
        this.savedFileCount = 0;
        this.removedFileCount = 0;
    }

    /**
     * @return the savedFileCount
     */
    public int getSavedFileCount() {
        return this.savedFileCount;
    }

    /**
     * @return the removedFileCount
     */
    public int getRemovedFileCount() {
        return this.removedFileCount;
    }

    /**
     * @return the nameToFile
     */
    public Map<String, File> getNameToFile() {
        return this.nameToFile;
    }

    /**
     * {@inheritDoc}
     */
    public void closeFile(CaArrayFile caarrayFile) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void closeFiles() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    public void delete(File file) {
        this.removedFileCount++;
    }

    /**
     * {@inheritDoc}
     */
    public CaArrayFile add(InputStream stream, String filename) {
        CaArrayFile caArrayFile = new CaArrayFile();
        caArrayFile.setName(filename);
        this.nameToFile.put(caArrayFile.getName(), new File(filename));
        return caArrayFile;
    }
}
