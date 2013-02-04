//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.TermSourceable;

import java.io.File;

/**
 * An Array Design and Array Design Ref as defined by the SDRF file.
 * @author dkokotov
 */
public class ArrayDesign extends AbstractCommentable implements TermSourceable {
    private static final long serialVersionUID = -5219974664880778410L;
    
    private TermSource termSource;
    private File file;
    private String value;

    /**
     * @return the value of this array design column.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set for this column. 
     */
    public void setValue(String value) {
        this.value = value;
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
