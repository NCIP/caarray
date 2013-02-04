//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * MageTabFileSet is a container for the mage-tab representation of an experiment. It consists of the IDF and SDRF
 * representing experiment metadata, and a list of DataFiles for the associated data files.
 * 
 * @author dkokotov
 */
public class MageTabFileSet implements Serializable {
    private static final long serialVersionUID = 1L;

    private FileContents idf;
    private FileContents sdrf;
    private Set<File> dataFiles = new HashSet<File>();

    /**
     * @return the idf
     */
    public FileContents getIdf() {
        return idf;
    }

    /**
     * @param idf the idf to set
     */
    public void setIdf(FileContents idf) {
        this.idf = idf;
    }

    /**
     * @return the sdrf
     */
    public FileContents getSdrf() {
        return sdrf;
    }

    /**
     * @param sdrf the sdrf to set
     */
    public void setSdrf(FileContents sdrf) {
        this.sdrf = sdrf;
    }

    /**
     * @return the dataFiles
     */
    public Set<File> getDataFiles() {
        return dataFiles;
    }

    /**
     * @param dataFiles the dataFiles to set
     */
    public void setDataFiles(Set<File> dataFiles) {
        this.dataFiles = dataFiles;
    }
}
