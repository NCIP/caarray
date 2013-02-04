//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileTypeRegistry;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple stub with no functionality.
 */
public class FileManagementServiceStub implements FileManagementService {

    int validatedFileCount = 0;
    int importedFilecCount = 0;
    int supplementalFileCount = 0;

    @Override
    public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions options) {
        this.importedFilecCount += fileSet.getFiles().size();
    }

    @Override
    public void validateFiles(Project project, CaArrayFileSet fileSet) {
        this.validatedFileCount += fileSet.getFiles().size();
    }

    /**
     * @return the validatedFileCount
     */
    public int getValidatedFileCount() {
        return this.validatedFileCount;
    }

    /**
     * @return the importedFilecCount
     */
    public int getImportedFilecCount() {
        return this.importedFilecCount;
    }

    /**
     * @return the supplementalFileCount
     */
    public int getSupplementalFileCount() {
        return this.supplementalFileCount;
    }

    public void reset() {
        this.validatedFileCount = 0;
        this.importedFilecCount = 0;
        this.supplementalFileCount = 0;
    }

    @Override
    public void addSupplementalFiles(Project targetProject, CaArrayFileSet fileSet) {
        this.supplementalFileCount += fileSet.getFiles().size();
    }

    public void importArrayDesignAnnotationFile(ArrayDesign arrayDesign, CaArrayFile annotationFile) {
        arrayDesign.setAnnotationFile(annotationFile);
    }

    @Override
    public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException {
        arrayDesign.setDesignFileSet(designFiles);
    }

    @Override
    public void importArrayDesignDetails(ArrayDesign arrayDesign) {
        // no-op
    }

    @Override
    public List<String> findIdfRefFileNames(CaArrayFile idfFile, Project project) {
        final List<String> filenames = new ArrayList<String>();
        for (final CaArrayFile caf : project.getFileSet().getFiles()) {
            if (FileTypeRegistry.MAGE_TAB_SDRF.equals(caf.getFileType())) {
                filenames.add(caf.getName());
            }
        }

        return filenames;
    }

    @Override
    public void reimportAndParseArrayDesign(Long arrayDesignId) throws InvalidDataFileException, IllegalAccessException {
        // no-op
    }

    @Override
    public void reimportAndParseProjectFiles(Project targetProject, CaArrayFileSet fileSet) {
        // no-op
    }
}
