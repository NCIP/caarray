//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.file.FileType;

/**
 * Provides access to an appropriate <code>AbstractDataHandler</code> for a given <code>AbstractArrayData</code>
 * instance.
 */
final class ArrayDataHandlerFactory {

    private static final ArrayDataHandlerFactory INSTANCE = new ArrayDataHandlerFactory();

    static ArrayDataHandlerFactory getInstance() {
        return INSTANCE;
    }

    AbstractDataFileHandler getHandler(FileType type) {
        if (FileType.AFFYMETRIX_CEL.equals(type)) {
            return new AffymetrixCelHandler();
        } else if (FileType.AFFYMETRIX_CHP.equals(type)) {
            return new AffymetrixChpHandler();
        } else if (FileType.GENEPIX_GPR.equals(type)) {
            return new GenepixGprHandler();
        } else if (FileType.ILLUMINA_DATA_CSV.equals(type)) {
            return new IlluminaDataHandler();
        } else if (type.isArrayData()) {
            return new UnsupportedDataFormatHandler();
        } else {
            throw new IllegalArgumentException("Unsupported type " + type);
        }
    }

}
