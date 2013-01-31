//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.arraydata.DataImportOptions;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

/**
 * Simple stub with no functionality.
 */
public class FileManagementServiceStub implements FileManagementService {

    int validatedFileCount = 0;
    int importedFilecCount = 0;
    int supplementalFileCount = 0;

    public void importFiles(Project targetProject, CaArrayFileSet fileSet, DataImportOptions options) {
        this.importedFilecCount += fileSet.getFiles().size();
    }

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

    public void addSupplementalFiles(Project targetProject, CaArrayFileSet fileSet) {
        this.supplementalFileCount += fileSet.getFiles().size();
    }

    public void importArrayDesignAnnotationFile(ArrayDesign arrayDesign, CaArrayFile annotationFile) {
        arrayDesign.setAnnotationFile(annotationFile);
    }

    public void saveArrayDesign(ArrayDesign arrayDesign, CaArrayFileSet designFiles) throws InvalidDataFileException {
        arrayDesign.setDesignFileSet(designFiles);
    }

    public void importArrayDesignDetails(ArrayDesign arrayDesign) {
        // no-op
    }
}
