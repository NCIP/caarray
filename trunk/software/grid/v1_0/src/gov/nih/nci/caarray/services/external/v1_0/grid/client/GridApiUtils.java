//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.external.v1_0.grid.client;

import gov.nih.nci.caarray.services.external.v1_0.IncorrectEntityTypeException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidInputException;
import gov.nih.nci.caarray.services.external.v1_0.InvalidReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.NoEntityMatchingReferenceException;
import gov.nih.nci.caarray.services.external.v1_0.UnsupportedCategoryException;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.IncorrectEntityTypeFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidInputFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.InvalidReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.NoEntityMatchingReferenceFault;
import gov.nih.nci.caarray.services.external.v1_0.grid.stubs.types.UnsupportedCategoryFault;

import org.apache.commons.lang.ArrayUtils;
import org.oasis.wsrf.faults.BaseFaultType;

/**
 * @author dkokotov
 *
 */
public final class GridApiUtils {
    private GridApiUtils() {
        // empty - utility class
    }
    
    static String getMessage(BaseFaultType fault) {
        if (!ArrayUtils.isEmpty(fault.getDescription())) {
            return fault.getDescription(0).get_value();
        }
        return null;
    }
    
    static InvalidInputException translateFault(InvalidInputFault f) {
        InvalidInputException e = null;
        String msg = getMessage(f);
        if (f instanceof InvalidReferenceFault) {
            InvalidReferenceFault irf = (InvalidReferenceFault) f;
            if (f instanceof IncorrectEntityTypeFault) {
                e = new IncorrectEntityTypeException(irf.getCaArrayEntityReference(), msg);
            } else if (f instanceof NoEntityMatchingReferenceFault) {
                e = new NoEntityMatchingReferenceException(irf.getCaArrayEntityReference(), msg);
            } else {
                e = new InvalidReferenceException(irf.getCaArrayEntityReference(), msg); 
            }
        } else if (f instanceof UnsupportedCategoryFault) {
            e = new UnsupportedCategoryException(((UnsupportedCategoryFault) f).getCaArrayEntityReference(), msg);
        } else {
            e = new InvalidInputException(msg);
        }
        
        return e;
    }
    
}
