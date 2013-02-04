//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.external.v1_0.query;

import gov.nih.nci.caarray.external.v1_0.CaArrayEntityReference;
import gov.nih.nci.caarray.external.v1_0.sample.BiomaterialType;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A BiomaterialSearchCriteria specifies a set of criterions against which Biomaterials are matched.
 * Each non-null or non-empty (in case of collections) field represents a distinct criterion; a biomaterial must
 * match all of them in order to match the whole Criteria.
 * 
 * @author dkokotov
 */
public class BiomaterialSearchCriteria implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CaArrayEntityReference experiment;
    private Set<String> names = new HashSet<String>();
    private Set<String> externalIds = new HashSet<String>();
    private Set<AnnotationCriterion> annotationCriterions = new HashSet<AnnotationCriterion>();
    private Set<BiomaterialType> types = new HashSet<BiomaterialType>();
    
    /**
     * @return a Set of names. A Biomaterial's name must be one of the names in this set in order to match.
     */
    public Set<String> getNames() {
        return names;
    }

    /**
     * @param names the names to set
     */
    public void setNames(Set<String> names) {
        this.names = names;
    }

    /**
     * @return a Set of BiomaterialTypes. A Biomaterial must be of one of the types in this set in order to match.
     */
    public Set<BiomaterialType> getTypes() {
        return types;
    }

    /**
     * @param types the types to set
     */
    public void setTypes(Set<BiomaterialType> types) {
        this.types = types;
    }

    /**
     * @return a reference to an Experiment. A Biomaterial must belong to this experiment in order to match.
     */
    public CaArrayEntityReference getExperiment() {
        return experiment;
    }

    /**
     * @param experiment the experiment to set
     */
    public void setExperiment(CaArrayEntityReference experiment) {
        this.experiment = experiment;
    }

    /**
     * @return a Set of external ids. A Biomaterial's external id must be one of the ids in this set in order to match.
     */
    public Set<String> getExternalIds() {
        return externalIds;
    }

    /**
     * @param externalIds the externalIds to set
     */
    public void setExternalIds(Set<String> externalIds) {
        this.externalIds = externalIds;
    }

    /**
     * Returns the Set of AnnotationCriterions to match the Biomaterial against. To determine if a Biomaterial matches,
     * all the AnnotationCriterions of the same category are OR-ed together, while AnnotationCriterions of different
     * categories are AND-ed together.
     * <p>
     * For example, suppose you have the following AnnotationCriterions:
     * <ul>
     * <li>{category=OrganismPart value=Brain}
     * <li>{category=OrganismPart value=Skin}
     * <li>{category=DiseaseState value=AbcCarcinoma}
     * </ul>
     * This will search for biomaterials with an organism part(tissue site) of either Brain or Skin, and a disease state
     * of AbcCarcinoma.
     * 
     * @return a Set of AnnotationCriterions.
     */
    public Set<AnnotationCriterion> getAnnotationCriterions() {
        return annotationCriterions;
    }

    /**
     * @param annotationCriterions the annotationCriterions to set
     */
    public void setAnnotationCriterions(Set<AnnotationCriterion> annotationCriterions) {
        this.annotationCriterions = annotationCriterions;
    }
}
