//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.search;

import gov.nih.nci.caarray.domain.sample.Source;

/**
 * Enum of possible sort criterions for sources.
 * @author dkokotov
 */
public enum SourceJoinableSortCriterion implements JoinableSortCriterion<Source> {
    /**
     * name.
     */
    NAME("this.name"),

    /**
     * Experiment title.
     */
    TITLE("e.title", "this.experiment e"),

    /**
     * Organism.
     */
    ORGANISM("coalesce(so.scientificName, eo.scientificName)",
            "this.organism so", "this.experiment e", "this.experiment.organism eo"),

    /**
     * tissuesite.
     */
    TISSUESITE("sts.value", "this.tissueSite sts"),

    /**
     * diseasestate.
     */
    DISEASESTATE("sds.value", "this.diseaseState sds"),

    /**
     * material type.
     */
    MATERIALTYPE("sms.value", "this.materialType sms"),

    /**
     * cell type.
     */
    CELLTYPE("scs.value", "this.cellType scs"),

    /**
     * provider name.
     */
    PROVIDER_NAME("sps.name", "this.providers sps"),


    /**
     * description.
     */
    DESCRIPTION("this.description");

    private final String orderField;
    private final String[] joins;


    private SourceJoinableSortCriterion(String orderField, String... joins) {
        this.orderField = orderField;
        this.joins = joins;


    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public String[] getJoins() {
        return joins;
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderField() {
        return this.orderField;
    }

    /**
     * {@inheritDoc}
     */
    public String getLeftJoinField() {
        // this is to support nci-commons-code 1.0.24, but this aspect of the
        // search is not yet used in caaaray or it is implemented diffrently.
        // https://jira.5amsolutions.com/browse/NCIC-60
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
