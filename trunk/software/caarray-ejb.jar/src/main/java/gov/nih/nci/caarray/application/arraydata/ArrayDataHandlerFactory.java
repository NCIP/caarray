//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydata;

import gov.nih.nci.caarray.domain.file.FileType;
import java.util.Map;
import java.util.HashMap;


/**
 * Provides access to an appropriate <code>AbstractDataHandler</code> for a given <code>FileType</code>
 * instance.
 */
public final class ArrayDataHandlerFactory {

    private static final ArrayDataHandlerFactory INSTANCE = new ArrayDataHandlerFactory();

    private final Map<FileType, Class> registry
        = new HashMap<FileType, Class>();

    private ArrayDataHandlerFactory() {
        registry.put(FileType.AFFYMETRIX_CEL, AffymetrixCelHandler.class);
        registry.put(FileType.AFFYMETRIX_CHP, AffymetrixChpHandler.class);
        registry.put(FileType.GENEPIX_GPR, GenepixGprHandler.class);
        registry.put(FileType.ILLUMINA_DATA_CSV, IlluminaDataHandler.class);
        registry.put(FileType.NIMBLEGEN_RAW_PAIR, NimblegenPairDataHandler.class);
        registry.put(FileType.NIMBLEGEN_NORMALIZED_PAIR, NimblegenPairDataHandler.class);
    }

    /**
     * @return the singleton factory instance
     */
    public static ArrayDataHandlerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * get the correct handler for given file type.
     * 
     * @param type the type of file for which a handler is needed.
     * @return The AbstractDataFileHandler that knows how to parse this file type 
     * 
     */
    public AbstractDataFileHandler getHandler(FileType type) {
        if (registry.containsKey(type)) {
            try {
                return (AbstractDataFileHandler) registry.get(type).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Unsupported type " + type, e);
            }
        } else if (type.isArrayData()) {
            return new UnsupportedDataFormatHandler();
        } else {
            throw new IllegalArgumentException("Unsupported type " + type);
        }
    }

}
