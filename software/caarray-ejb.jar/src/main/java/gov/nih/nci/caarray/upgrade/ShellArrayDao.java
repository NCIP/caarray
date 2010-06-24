package gov.nih.nci.caarray.upgrade;

import gov.nih.nci.caarray.dao.ArrayDaoUnsupportedOperationImpl;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;

/**
 * Provides a shell of an ArrayDao that does nothing but assign IDs to persistent objects and
 * maintain the reverse relationship between PhysicalProbe and ArrayDesignDetails.
 * 
 * @author jscott
 *
 */
class ShellArrayDao extends ArrayDaoUnsupportedOperationImpl {
    private long nextId = 0;
    
    @Override
    public void flushSession() {
        // No-Op
    }

    @SuppressWarnings("deprecation")
    @Override
    public void save(PersistentObject persistentObject) {
        AbstractCaArrayObject caArrayObject = (AbstractCaArrayObject) persistentObject;
        caArrayObject.setId(nextId++);
        
        if (caArrayObject instanceof PhysicalProbe) {
            PhysicalProbe physicalProbe = (PhysicalProbe) caArrayObject;
            physicalProbe.getArrayDesignDetails().getProbes().add(physicalProbe);
        }
    }   
}