//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

import java.util.Collection;

/**
 * Utility methods to use on <code>AbstractCaArrayEntities</code>.
 */
@SuppressWarnings("PMD")
public final class CaArrayEntityTestUtility {

    private CaArrayEntityTestUtility() {
        super();
    }

    /**
     *
     * @param entities .
     */
    public static void printEntities(Collection<? extends AbstractCaArrayEntity> entities) {
        for (AbstractCaArrayObject entity : entities) {
            System.out.println(entity);
        }
    }

}
