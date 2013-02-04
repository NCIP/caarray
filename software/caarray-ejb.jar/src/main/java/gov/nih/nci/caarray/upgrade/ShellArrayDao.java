//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.dao.ArrayDaoUnsupportedOperationImpl;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Provides a shell of an ArrayDao that does nothing but assign IDs to persistent objects and maintain the reverse
 * relationship between PhysicalProbe and ArrayDesignDetails.
 * 
 * @author jscott
 * 
 */
class ShellArrayDao extends ArrayDaoUnsupportedOperationImpl {
    private long nextId = 0; // NOPMD - PMD erroneously thinks this can be final

    @Override
    public void flushSession() {
        // No-Op
    }

    @SuppressWarnings("deprecation")
    @Override
    public Long save(PersistentObject persistentObject) {
        final AbstractCaArrayObject caArrayObject = (AbstractCaArrayObject) persistentObject;
        caArrayObject.setId(this.nextId++);

        if (caArrayObject instanceof PhysicalProbe) {
            final PhysicalProbe physicalProbe = (PhysicalProbe) caArrayObject;
            physicalProbe.getArrayDesignDetails().getProbes().add(physicalProbe);
        }

        return caArrayObject.getId();
    }
}
