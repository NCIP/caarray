//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external;

import net.sf.dozer.util.mapping.converters.ConfigurableCustomConverter;

/**
 * @author dkokotov
 *
 */
public class IdToExternalIdConverter implements ConfigurableCustomConverter {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object convert(Object dest, Object src, Class destClass, Class srcClass, String param) {
        if (src == null) {
            return null;
        } else if (src instanceof Long) {
            return AbstractExternalService.makeExternalId(param, src.toString());
        } else if (src instanceof String) {
            return Long.valueOf(AbstractExternalService.getIdFromExternalId(src.toString()));
        } else {
            throw new IllegalArgumentException("This converter cannot convert object of type " + srcClass);
        }
    }
}
