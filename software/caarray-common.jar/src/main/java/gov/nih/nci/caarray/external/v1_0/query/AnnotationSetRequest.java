//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dkokotov
 */
public class AnnotationSetRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CaArrayEntityReference> experimentGraphNodes = new ArrayList<CaArrayEntityReference>();
    private List<CaArrayEntityReference> categories = new ArrayList<CaArrayEntityReference>();

    /**
     * @return the biomaterials
     */
    public List<CaArrayEntityReference> getExperimentGraphNodes() {
        return experimentGraphNodes;
    }

    /**
     * @param biomaterials the biomaterials to set
     */
    public void setExperimentGraphNodes(List<CaArrayEntityReference> biomaterials) {
        this.experimentGraphNodes = biomaterials;
    }

    /**
     * @return the categories
     */
    public List<CaArrayEntityReference> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(List<CaArrayEntityReference> categories) {
        this.categories = categories;
    }
}
