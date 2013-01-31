//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.data;

import java.io.File;

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

/**
 * Base class for MAGE-TAB data matrix files.
 */
public class DataMatrix extends AbstractMageTabDocument {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new Data Matrix from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the Data Matrix belongs to.
     * @param file the file containing the Data Matrix content.
     */
    public DataMatrix(MageTabDocumentSet documentSet, File file) {
        super(documentSet, file);
    }

    /**
     * Returns a reader that can be used to iterate through all of the data in the matrix.
     *
     * @return the reader.
     */
    public DataMatrixReader getDataReader() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse() throws MageTabParsingException {
        // TODO Implement parsing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void export() {
        // unimplemented
    }
}
