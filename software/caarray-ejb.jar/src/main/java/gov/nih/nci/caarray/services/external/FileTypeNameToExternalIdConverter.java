//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external;

import gov.nih.nci.caarray.external.v1_0.data.FileType;
import net.sf.dozer.util.mapping.converters.CustomConverter;

/**
 * @author dkokotov
 * 
 */
public class FileTypeNameToExternalIdConverter implements CustomConverter {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object dest, Object src, Class destClass, Class srcClass) {
        if (src == null) {
            return null;
        } else if (src instanceof String) {
            return AbstractExternalService.makeExternalId(FileType.class, src);
        } else {
            throw new IllegalArgumentException("This converter cannot convert object of type " + srcClass);
        }
    }
}
