//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

/**
 * @author jscott
 * 
 */
public final class CaArrayHibernateHelperFactory {
    
    // Static utility class should not have a default constructor.
    private CaArrayHibernateHelperFactory() {
    }
    
    /**
     * Holds a CaArrayHibernateHelper reference.  Inner classes are initialized only when first used,
     * so the CaArrayHibernateHelper is not created until needed.
     * 
     * @author jscott
     */
    private static final class CaArrayHibernateHelperHolder {
       private static CaArrayHibernateHelperImpl hibernateHelper;
        
       static {
            hibernateHelper = (CaArrayHibernateHelperImpl) CaArrayHibernateHelperImpl.create();
        }
       
       private CaArrayHibernateHelperHolder() {
       }
    }

    /**
     * @return the single CaArrayHibernateHelper instance.
     */
    public static CaArrayHibernateHelper getCaArrayHibernateHelper() {
        return CaArrayHibernateHelperHolder.hibernateHelper;
    }
}
