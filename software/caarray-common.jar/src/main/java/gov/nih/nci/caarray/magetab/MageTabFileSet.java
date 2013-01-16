//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.util.io.FileUtility;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An set of potentially interrelated MAGE-TAB document files to be validated or parsed.
 */
public class MageTabFileSet implements Serializable {

    private static final long serialVersionUID = 7824150081647257549L;

    private final Set<FileRef> idfFiles = new HashSet<FileRef>();
    private final Set<FileRef> adfFiles = new HashSet<FileRef>();
    private final Set<FileRef> sdrfFiles = new HashSet<FileRef>();
    private final Set<FileRef> dataMatrixFiles = new HashSet<FileRef>();
    private final Set<FileRef> nativeDataFiles = new HashSet<FileRef>();

    /**
     * Adds the file as an IDF to the document set to be parsed.
     *
     * @param file the IDF
     */
    public void addIdf(FileRef file) {
        checkFile(file);
        idfFiles.add(file);
    }

    /**
     * Adds the file as an ADF to the document set to be parsed.
     *
     * @param file the ADF
     */
    public void addAdf(FileRef file) {
        checkFile(file);
        adfFiles.add(file);
    }

    /**
     * Adds the file as an SDRF to the document set to be parsed.
     *
     * @param file the SDRF
     */
    public void addSdrf(FileRef file) {
        checkFile(file);
        sdrfFiles.add(file);
    }

    /**
     * Adds the file as a data matrix file to the document set to be parsed.
     *
     * @param file the data matrix file
     */
    public void addDataMatrix(FileRef file) {
        checkFile(file);
        dataMatrixFiles.add(file);
    }

    /**
     * Adds the file as a native data file to the document set to be parsed.
     *
     * @param file the native data file
     */
    public void addNativeData(FileRef file) {
        checkFile(file);
        nativeDataFiles.add(file);
    }

    private void checkFile(FileRef file) {
        FileUtility.checkFileExists(file);
    }

    Set<FileRef> getAdfFiles() {
        return this.adfFiles;
    }

    /**
     * Gets the set of Data Matrix files.
     * 
     * @return current set of data matrix files.
     */
    public Set<FileRef> getDataMatrixFiles() {
        return this.dataMatrixFiles;
    }

    /**
     * Gets the set of Idf files.
     *
     * @return current list of idf files.
     */
    public Set<FileRef> getIdfFiles() {
        return this.idfFiles;
    }

    /**
     * Gets the set of native data files.
     * 
     * @return current set of native data files.
     */
    public Set<FileRef> getNativeDataFiles() {
        return this.nativeDataFiles;
    }

    /**
     * Gets the set of Sdrf files.
     *
     * @return current list of sdrf files.
     */
    public Set<FileRef> getSdrfFiles() {
        return this.sdrfFiles;
    }

    /**
     * Get all the files in this file set.
     * @return all files
     */
    public Set<FileRef> getAllFiles() {
        Set<FileRef> files = new HashSet<FileRef>();
        files.addAll(this.adfFiles);
        files.addAll(this.dataMatrixFiles);
        files.addAll(this.idfFiles);
        files.addAll(this.nativeDataFiles);
        files.addAll(this.sdrfFiles);
        return files;
    }

    /**
     * Makes a shallow copy of the files contained in this set.
     * @return shallow copy
     */
    public MageTabFileSet makeCopy() {
        MageTabFileSet other = new MageTabFileSet();
        other.adfFiles.addAll(adfFiles);
        other.dataMatrixFiles.addAll(dataMatrixFiles);
        other.idfFiles.addAll(idfFiles);
        other.nativeDataFiles.addAll(nativeDataFiles);
        other.sdrfFiles.addAll(sdrfFiles);
        return other;
    }

    /**
     * Removes any/all sdrf files contained within the file set.
     */
    public void clearSdrfs() {
        this.sdrfFiles.clear();
    }

}
