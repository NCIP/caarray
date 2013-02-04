//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.genepix;

import gov.nih.nci.caarray.platforms.DefaultValueParser;

/**
 * Genepix value parser.
 * 
 * @author dkokotov
 */
class GenepixValueParser extends DefaultValueParser {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean parseBoolean(String value) {
        return !"0".equals(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected float parseFloat(String value) {
        if ("Error".equals(value)) {
            return Float.NaN;
        } else {
            return super.parseFloat(value);
        }
    }
}
