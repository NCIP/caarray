//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

/**
 * Comment class.
 * 
 */
public class Comment extends AbstractSampleDataRelationshipNode {

    private static final long serialVersionUID = 8963454780738369441L;
    private String value;

    /**
     * 
     * @return String the comment
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value the comment
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    void addToSdrfList(SdrfDocument document) {
        document.getAllComments().add(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.COMMENT;
    }

}
