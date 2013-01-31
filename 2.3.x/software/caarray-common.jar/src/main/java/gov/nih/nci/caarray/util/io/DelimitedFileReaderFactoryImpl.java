//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.io;

import java.io.File;
import java.io.IOException;

/**
 * Default implementation of factory.
 */
class DelimitedFileReaderFactoryImpl extends AbstractDelimitedFileReaderFactory {

    @Override
    public DelimitedFileReader getReader(File file, char separator, char delimiter) throws IOException {
        return new CSVReaderDelimitedFileReader(file, separator, delimiter);
    }

}
