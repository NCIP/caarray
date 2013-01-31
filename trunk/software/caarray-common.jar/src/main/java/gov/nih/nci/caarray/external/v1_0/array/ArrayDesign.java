//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.external.v1_0.array;

import java.util.HashSet;
import java.util.Set;

import gov.nih.nci.caarray.external.v1_0.AbstractCaArrayEntity;
import gov.nih.nci.caarray.external.v1_0.data.File;
import gov.nih.nci.caarray.external.v1_0.experiment.Organism;
import gov.nih.nci.caarray.external.v1_0.vocabulary.Term;

/**
 * ArrayDesign describes a particular array model.
 * 
 * @author dkokotov
 */
public class ArrayDesign extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String lsid;
    private String version;
    private ArrayProvider arrayProvider;
    private Term technologyType;
    private Organism organism;
    private Set<AssayType> assayTypes = new HashSet<AssayType>();
    private Set<File> files = new HashSet<File>();

    /**
     * @return the name of this array design.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version of the design.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the provider of this array design.
     */
    public ArrayProvider getArrayProvider() {
        return arrayProvider;
    }

    /**
     * @param arrayProvider the arrayProvider to set
     */
    public void setArrayProvider(ArrayProvider arrayProvider) {
        this.arrayProvider = arrayProvider;
    }
    
    /**
     * @return the MGED term describing the tecnology used in this array design.
     */
    public Term getTechnologyType() {
        return technologyType;
    }

    /**
     * @param technologyType the technologyType to set
     */
    public void setTechnologyType(Term technologyType) {
        this.technologyType = technologyType;
    }

    /**
     * @return the organism for whose genetic tissue the array is designed.
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * @param organism the organism to set
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * @return the LSID for this array design.
     */
    public String getLsid() {
        return lsid;
    }

    /**
     * @param lsid the lsid to set
     */
    public void setLsid(String lsid) {
        this.lsid = lsid;
    }

    /**
     * @return the set of files which contain actual information about the layout of this array. The format of these
     * files is specific to each array provider.
     */
    public Set<File> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(Set<File> files) {
        this.files = files;
    }

    /**
     * @return the assay types which this array design supports.
     */
    public Set<AssayType> getAssayTypes() {
        return assayTypes;
    }

    /**
     * @param assayTypes the assayTypes to set
     */
    public void setAssayTypes(Set<AssayType> assayTypes) {
        this.assayTypes = assayTypes;
    }
}
