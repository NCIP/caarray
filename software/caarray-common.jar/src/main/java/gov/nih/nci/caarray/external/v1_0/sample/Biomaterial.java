//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.sample;

import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.value.TermValue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Biomaterial is an experiment graph node which corresponds to a biomaterial at some stage prior
 * to being hybridized with the array.
 *
 * @author dkokotov
 */
public class Biomaterial extends AbstractExperimentGraphNode {
    private static final long serialVersionUID = 1L;

    private String description;
    private String externalId;
    private TermValue diseaseState;
    private TermValue tissueSite;
    private TermValue materialType;
    private TermValue cellType;
    private Organism organism;
    private Set<Characteristic> characteristics = new HashSet<Characteristic>();
    private BiomaterialType type;
    private Date lastModifiedDataTime;

    /**
     * @return an external id. This is an identifier for this biomaterial in some external system. This value should
     * be unique across biomaterials in the same experiment.
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the external id for this biomaterial.
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @return a long-form description of this biomaterial
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description a long-form description of this biomaterial
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return a term from the MageTAB DiseaseState category defining the disease state for this biomaterial.
     */
    public TermValue getDiseaseState() {
        return diseaseState;
    }

    /**
     * @param diseaseState the diseaseState to set
     */
    public void setDiseaseState(TermValue diseaseState) {
        this.diseaseState = diseaseState;
    }

    /**
     * @return the tissueSite
     */
    public TermValue getTissueSite() {
        return tissueSite;
    }

    /**
     * @param tissueSite the tissueSite to set
     */
    public void setTissueSite(TermValue tissueSite) {
        this.tissueSite = tissueSite;
    }

    /**
     * @return the materialType
     */
    public TermValue getMaterialType() {
        return materialType;
    }

    /**
     * @param materialType the materialType to set
     */
    public void setMaterialType(TermValue materialType) {
        this.materialType = materialType;
    }

    /**
     * @return the cellType
     */
    public TermValue getCellType() {
        return cellType;
    }

    /**
     * @param cellType the cellType to set
     */
    public void setCellType(TermValue cellType) {
        this.cellType = cellType;
    }

    /**
     * @return the organism from which this biomaterial was extracted
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism from which this biomaterial was extracted
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the set of characteristics describing properties of this biomaterial.
     */
    public Set<Characteristic> getCharacteristics() {
        return characteristics;
    }

    /**
     * @param characteristics the set of characteristics describing properties of this biomaterial.
     */
    public void setCharacteristics(Set<Characteristic> characteristics) {
        this.characteristics = characteristics;
    }

    /**
     * @return the type of this biomaterial.
     */
    public BiomaterialType getType() {
        return type;
    }

    /**
     * @param type the type of this biomaterial
     */
    public void setType(BiomaterialType type) {
        this.type = type;
    }

    /**
     * @return the lastModifiedDataTime
     */
    public Date getLastModifiedDataTime() {
        return lastModifiedDataTime;
    }

    /**
     * @param lastModifiedDataTime the lastModifiedDataTime to set
     */
    public void setLastModifiedDataTime(Date lastModifiedDataTime) {
        this.lastModifiedDataTime = lastModifiedDataTime;
    }
}
