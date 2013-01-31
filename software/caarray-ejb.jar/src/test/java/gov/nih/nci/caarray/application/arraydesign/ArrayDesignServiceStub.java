//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.validation.InvalidDataFileException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArrayDesignServiceStub implements ArrayDesignService {

    public ArrayDesignDetails getDesignDetails(ArrayDesign arrayDesign) {
        return null;
    }

    public ValidationResult validateDesign(CaArrayFile designFile) {
        return null;
    }

    public ValidationResult validateDesign(Set<CaArrayFile> designFiles) {
        return null;
    }

    public ArrayDesign getArrayDesign(Long id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getArrayDesignProviders() {
        return new ArrayList<Organization>();
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getImportedArrayDesignsForProvider(Organization provider) {
        return new ArrayList<ArrayDesign>();
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getImportedArrayDesigns(Organization provider, AssayType assayType) {
        return new ArrayList<ArrayDesign>();
    }

    /**
     * {@inheritDoc}
     */
    public List<ArrayDesign> getArrayDesigns() {
        return new ArrayList<ArrayDesign>();
    }

    public void importDesignDetails(ArrayDesign arrayDesign) {
        // no-op
    }

    public void importDesign(ArrayDesign arrayDesign) {
        // no-op
    }

    public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamesapce, String lsidObjectId) {
        return null;
    }

    public ValidationResult validateDesign(ArrayDesign design) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Organization> getAllProviders() {
        return new ArrayList<Organization>();
    }

    public boolean isArrayDesignLocked(Long id) {
        return false;
    }

    public void saveArrayDesign(ArrayDesign arrayDesign) throws IllegalAccessError, InvalidDataFileException {
        // no op
    }

    public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        return null;
    }

    public void deleteArrayDesign(ArrayDesign arrayDesign)throws ArrayDesignDeleteException {
    }

    public boolean isDuplicate(ArrayDesign arrayDesign) {
        if ("duplicate".equals(arrayDesign.getDescription())) {
            return true;
        }

        return false;
    }
}
