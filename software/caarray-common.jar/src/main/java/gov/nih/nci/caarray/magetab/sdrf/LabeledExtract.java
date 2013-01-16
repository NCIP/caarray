//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;

/**
 * Extracted genetic material labeled for hybridization.
 */
public final class LabeledExtract extends AbstractBioMaterial {

    private static final long serialVersionUID = 7180426686673666046L;

    private OntologyTerm label;

    /**
     * 
     * @return OntologyTerm the label type
     */
    public OntologyTerm getLabel() {
        return label;
    }

    /**
     * 
     * @param label the returned OntologyTerm
     */
    public void setLabel(OntologyTerm label) {
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.LABELED_EXTRACT;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllLabeledExtracts().add(this);
    }

}
