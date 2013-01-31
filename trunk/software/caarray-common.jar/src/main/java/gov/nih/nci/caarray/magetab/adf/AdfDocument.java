//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.adf;

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import java.io.File;

/**
 * Represents an Array Design Format (ADF) file - a tab-delimited file defining each array
 * type used. An ADF file describes the design of an array, e.g., what sequence is located at
 * each position on an array and what the annotation of this sequence is. If the investigation
 * uses arrays for which a description has been previously provided, such as a standard commercial
 * array, cross-references to entries in a public repository (e.g., an ArrayExpress accession number)
 * can be included instead of explicit array descriptions.
 */
public final class AdfDocument extends AbstractMageTabDocument {

    private static final long serialVersionUID = 2340826408569786767L;

    /**
     * Instantiates a new <code>AdfDocument</code> from an existing file.
     *
     * @param documentSet the document set the ADF belongs to
     * @param file thd ADF
     */
    public AdfDocument(MageTabDocumentSet documentSet, File file) {
        super(documentSet, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse() throws MageTabParsingException {
        // unimplemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void export() {
        // unimplemented
    }
}
