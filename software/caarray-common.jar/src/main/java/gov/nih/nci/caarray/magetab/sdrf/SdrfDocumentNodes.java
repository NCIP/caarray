//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import java.util.Collection;

/**
 * Contains all the experimental graph nodes in an <code>SdrfDocument</code>.
 * This includes, sources, samples, extracts, labeled extracts, hybridizations,
 * raw and derived data files and data matrix files.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SdrfDocumentNodes {
    private Collection<Source> allSources;
    private Collection<Sample> allSamples;
    private Collection<Extract> allExtracts;
    private Collection<LabeledExtract> allLabeledExtracts;
    private Collection<Hybridization> allHybridizations;
    private Collection<ArrayDataFile> allArrayDataFiles;
    private Collection<ArrayDataMatrixFile> allArrayDataMatrixFiles;
    private Collection<DerivedArrayDataFile> allDerivedArrayDataFiles;
    private Collection<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles;

    /**
     * Initialize all the non-data nodes, i.e., biomaterials and hybridizations.
     *
     * @param sources the sources to initialize
     * @param samples the samples to initialize
     * @param extracts the extracts to initialize
     * @param labeledExtracts the labeled extracts to initialize
     * @param hybridizations the hybridizations to initialize
     */
    public void initNonDataNodes(Collection<Source> sources, Collection<Sample> samples, Collection<Extract> extracts,
            Collection<LabeledExtract> labeledExtracts, Collection<Hybridization> hybridizations) {
        allSources = sources;
        allSamples = samples;
        allExtracts = extracts;
        allLabeledExtracts = labeledExtracts;
        allHybridizations = hybridizations;
    }

    /**
     * Initialize all the data nodes, i.e., raw and derived, data and data matrix.
     *
     * @param arrayDataFiles the array data files to initialize
     * @param arrayDataMatrixFiles the array data matrix files to initialize
     * @param derivedArrayDataFiles the derived array data files to initialize
     * @param derivedArrayDataMatrixFiles the derived array data matrix files to initialize
     */
    public void initDataNodes(Collection<ArrayDataFile> arrayDataFiles,
            Collection<ArrayDataMatrixFile> arrayDataMatrixFiles,
            Collection<DerivedArrayDataFile> derivedArrayDataFiles,
            Collection<DerivedArrayDataMatrixFile> derivedArrayDataMatrixFiles) {
        allArrayDataFiles = arrayDataFiles;
        allArrayDataMatrixFiles = arrayDataMatrixFiles;
        allDerivedArrayDataFiles = derivedArrayDataFiles;
        allDerivedArrayDataMatrixFiles = derivedArrayDataMatrixFiles;
    }

    /**
     * @return all ArrayDataFiles
     */
    public Collection<ArrayDataFile> getAllArrayDataFiles() {
        return this.allArrayDataFiles;
    }

    /**
     * @return all ArrayDataMatrixFiles
     */
    public Collection<ArrayDataMatrixFile> getAllArrayDataMatrixFiles() {
        return this.allArrayDataMatrixFiles;
    }

    /**
     * @return all DerivedArrayDataFiles
     */
    public Collection<DerivedArrayDataFile> getAllDerivedArrayDataFiles() {
        return this.allDerivedArrayDataFiles;
    }

    /**
     * @return all DerivedArrayDataMatrixFiles
     */
    public Collection<DerivedArrayDataMatrixFile> getAllDerivedArrayDataMatrixFiles() {
        return this.allDerivedArrayDataMatrixFiles;
    }

    /**
     * @return all Extracts
     */
    public Collection<Extract> getAllExtracts() {
        return this.allExtracts;
    }

    /**
     * @return all Hybridizations
     */
    public Collection<Hybridization> getAllHybridizations() {
        return this.allHybridizations;
    }

    /**
     * @return all LabeledExtracts
     */
    public Collection<LabeledExtract> getAllLabeledExtracts() {
        return this.allLabeledExtracts;
    }

    /**
     * @return all Samples
     */
    public Collection<Sample> getAllSamples() {
        return this.allSamples;
    }

    /**
     * @return all Sources
     */
    public Collection<Source> getAllSources() {
        return this.allSources;
    }

    /**
     * Checks if all the SdrfDocument nodes have been initialized.
     *
     * @return true if all nodes have been initialized, false otherwise.
     */
    public boolean isInitialized() {
        if (allSources == null || allSamples == null || allExtracts == null || allLabeledExtracts == null
                || allHybridizations == null || allArrayDataFiles == null || allArrayDataMatrixFiles == null
                || allDerivedArrayDataFiles == null || allDerivedArrayDataMatrixFiles == null) {
            return false;
        }
        return true;
    }
}
