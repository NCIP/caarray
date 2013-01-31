//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * A biological sample taken from a source.
 */
public final class Sample extends AbstractBioMaterial {

    private static final long serialVersionUID = 7180426686673666046L;

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.SAMPLE;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllSamples().add(this);
    }

}
