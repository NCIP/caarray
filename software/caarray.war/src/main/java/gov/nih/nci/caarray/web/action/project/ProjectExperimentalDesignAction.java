//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.vocabulary.VocabularyUtils;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.vocabulary.Term;

import java.util.Set;

import com.opensymphony.xwork2.validator.annotations.FieldExpressionValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

/**
 * Action implementing the experimental design tab.
 * @author Dan Kokotov
 */
public class ProjectExperimentalDesignAction extends ProjectTabAction {
    private static final long serialVersionUID = 1L;

    private Set<Term> experimentDesignTypes;
    private Set<Term> qualityControlTypes;
    private Set<Term> replicateTypes;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        super.prepare();

        this.experimentDesignTypes = VocabularyUtils
                .getTermsFromCategory(ExperimentOntologyCategory.EXPERIMENT_DESIGN_TYPE);
        this.qualityControlTypes = VocabularyUtils
                .getTermsFromCategory(ExperimentOntologyCategory.QUALITY_CONTROL_TYPE);
        this.replicateTypes = VocabularyUtils.getTermsFromCategory(ExperimentOntologyCategory.REPLICATE_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Validations(
        fieldExpressions = {
            @FieldExpressionValidator(fieldName = "project.experiment.experimentDesignTypes",
                message = "", key = "struts.validator.requiredString",
                expression = "!project.experiment.experimentDesignTypes.isEmpty")
        },
        requiredFields = {
            @RequiredFieldValidator(fieldName = "project.experiment.designDescription",
                key = "errors.required", message = "")
        }
    )
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public String save() {
        return super.save();
    }

    /**
     * @return the experimentDesignTypes
     */
    public Set<Term> getExperimentDesignTypes() {
        return this.experimentDesignTypes;
    }

    /**
     * @param experimentDesignTypes the experimentDesignTypes to set
     */
    public void setExperimentDesignTypes(Set<Term> experimentDesignTypes) {
        this.experimentDesignTypes = experimentDesignTypes;
    }

    /**
     * @return the qualityControlTypes
     */
    public Set<Term> getQualityControlTypes() {
        return this.qualityControlTypes;
    }

    /**
     * @param qualityControlTypes the qualityControlTypes to set
     */
    public void setQualityControlTypes(Set<Term> qualityControlTypes) {
        this.qualityControlTypes = qualityControlTypes;
    }

    /**
     * @return the replicateTypes
     */
    public Set<Term> getReplicateTypes() {
        return this.replicateTypes;
    }

    /**
     * @param replicateTypes the replicateTypes to set
     */
    public void setReplicateTypes(Set<Term> replicateTypes) {
        this.replicateTypes = replicateTypes;
    }
}
