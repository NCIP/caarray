//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.data;

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.io.FileRef;


/**
 * A MAGE-TAB data matrix files holding raw or derived data.
 * 
 * @author dkokotov
 */
public class DataMatrix extends AbstractMageTabDocument {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Data Matrix from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the Data Matrix belongs to.
     * @param file the file containing the Data Matrix content.
     */
    public DataMatrix(MageTabDocumentSet documentSet, FileRef file) {
        super(documentSet, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse() throws MageTabParsingException {
        // no-op - currently parsing of data matrices not supported
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void export() {
        // no-op - data matrices are not exported
    }
}
