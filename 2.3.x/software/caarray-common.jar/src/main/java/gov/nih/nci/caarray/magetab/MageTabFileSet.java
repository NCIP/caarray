//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.util.io.FileUtility;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * An set of potentially interrelated MAGE-TAB document files to be validated or parsed.
 */
public final class MageTabFileSet implements Serializable {

    private static final long serialVersionUID = 7824150081647257549L;

    private final Set<File> idfFiles = new HashSet<File>();
    private final Set<File> adfFiles = new HashSet<File>();
    private final Set<File> sdrfFiles = new HashSet<File>();
    private final Set<File> dataMatrixFiles = new HashSet<File>();
    private final Set<File> nativeDataFiles = new HashSet<File>();

    /**
     * Adds the file as an IDF to the document set to be parsed.
     *
     * @param file the IDF
     */
    public void addIdf(File file) {
        checkFile(file);
        idfFiles.add(file);
    }

    /**
     * Adds the file as an ADF to the document set to be parsed.
     *
     * @param file the ADF
     */
    public void addAdf(File file) {
        checkFile(file);
        adfFiles.add(file);
    }

    /**
     * Adds the file as an SDRF to the document set to be parsed.
     *
     * @param file the SDRF
     */
    public void addSdrf(File file) {
        checkFile(file);
        sdrfFiles.add(file);
    }

    /**
     * Adds the file as a data matrix file to the document set to be parsed.
     *
     * @param file the data matrix file
     */
    public void addDataMatrix(File file) {
        checkFile(file);
        dataMatrixFiles.add(file);
    }

    /**
     * Adds the file as a native data file to the document set to be parsed.
     *
     * @param file the native data file
     */
    public void addNativeData(File file) {
        checkFile(file);
        nativeDataFiles.add(file);
    }

    private void checkFile(File file) {
        FileUtility.checkFileExists(file);
    }

    Set<File> getAdfFiles() {
        return this.adfFiles;
    }

    Set<File> getDataMatrixFiles() {
        return this.dataMatrixFiles;
    }

    Set<File> getIdfFiles() {
        return this.idfFiles;
    }

    Set<File> getNativeDataFiles() {
        return this.nativeDataFiles;
    }

    Set<File> getSdrfFiles() {
        return this.sdrfFiles;
    }

}
