//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.data;

import gov.nih.nci.caarray.magetab.AbstractMageTabDocument;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;

import java.io.File;

/**
 * A raw or derived data file in original, native format.
 */
public final class NativeDataFile extends AbstractMageTabDocument {

    private static final long serialVersionUID = 7535068841248711861L;

    /**
     * Creates a new data file instance from an existing file.
     *
     * @param documentSet document set containing the file
     * @param file the file
     */
    public NativeDataFile(MageTabDocumentSet documentSet, File file) {
        super(documentSet, file);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void parse(boolean reimportingMagetab) throws MageTabParsingException {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void export() {
        // unimplemented
    }
}
