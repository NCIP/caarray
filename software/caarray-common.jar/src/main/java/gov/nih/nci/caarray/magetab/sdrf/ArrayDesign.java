//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.TermSourceable;

import java.io.File;

/**
 * An Array Design and Array Design Ref as defined by the SDRF file.
 *
 */
public class ArrayDesign extends AbstractSampleDataRelationshipNode implements TermSourceable {

    private static final long serialVersionUID = -5219974664880778410L;
    private TermSource termSource;
    private File file;

    @Override
    void addToSdrfList(SdrfDocument document) {
        if (!document.getAllArrayDesigns().contains(this)) {
            document.getAllArrayDesigns().add(this);
        }

    }

    /**
     * @return SdrfNodeType the node thype
     */
    @Override
    public SdrfNodeType getNodeType() {
        return SdrfNodeType.ARRAY_DESIGN;
    }

    /**
     * @return TermSouce the term Source
     */
    public TermSource getTermSource() {
        return termSource;
    }

    /**
     * @param termSource set the term Source
     */
    public void setTermSource(TermSource termSource) {
        this.termSource = termSource;
    }

    /**
     * @return File the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }
}
