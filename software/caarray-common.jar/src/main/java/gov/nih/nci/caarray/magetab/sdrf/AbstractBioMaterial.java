//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;

import java.util.ArrayList;
import java.util.List;

/**
 * A biomaterial defined in a MAGE-TAB SDRF.
 */
public abstract class AbstractBioMaterial extends AbstractSampleDataRelationshipNode {

    private static final long serialVersionUID = 690334748116662920L;

    private final List<Characteristic> characteristics = new ArrayList<Characteristic>();
    private OntologyTerm materialType;
    private String description;

    /**
     * @return the characteristics
     */
    public List<Characteristic> getCharacteristics() {
        return characteristics;
    }

    /**
     * @return the characteristic for a specific category
     *
     * @param category the category of the characteristic of interest.
     */
    public Characteristic getCharacteristic(String category) {
        if (category == null) {
            return null;
        }
        for (Characteristic characteristic : characteristics) {
            if (category.equals(characteristic.getCategory())) {
                return characteristic;
            }
        }
        return null;
    }

    /**
     * @return the characteristic value for a specific category
     *
     * @param category the category of the characteristic of interest.
     */
    public String getCharacteristicValue(String category) {
        if (category == null) {
            return null;
        }
        for (Characteristic characteristic : characteristics) {
            OntologyTerm term = characteristic.getTerm();
            if (term != null && category.equals(term.getCategory())) {
                // Term-based characteristic
                return term.getValue();
            } else if (category.equals(characteristic.getCategory())) {
                // Measurement-based characteristic
                return characteristic.getValue();
            }
        }
        return null;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the materialType
     */
    public OntologyTerm getMaterialType() {
        return materialType;
    }

    /**
     * @param materialType the materialType to set
     */
    public void setMaterialType(OntologyTerm materialType) {
        this.materialType = materialType;
    }
}
