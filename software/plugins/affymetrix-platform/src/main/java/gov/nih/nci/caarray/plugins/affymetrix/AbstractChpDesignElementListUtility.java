//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.affymetrix;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files.
 */
abstract class AbstractChpDesignElementListUtility {
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_ELEMENT_LIST = "DesignElementList";
    private static final String LSID_OBJECT_ID_ELEMENT_LIST_PREFIX = "LogicalProbes";

    static final int BATCH_SIZE = 500;
    static final int TRANSACTION_SIZE = 5000;

    private final ArrayDao arrayDao;
    private final SessionTransactionManager sessionTransactionManager;

    /**
     * Create a new instance.
     * @param arrayDao ArrayDao instance
     * @param sessionTransactionManager the session/transaction manager
     */
    AbstractChpDesignElementListUtility(ArrayDao arrayDao, SessionTransactionManager sessionTransactionManager) {
        this.arrayDao = arrayDao;
        this.sessionTransactionManager = sessionTransactionManager;
    }

    /**
     * Creates the singleton <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design.
     *
     * @param design the array design. This must be an affymetrix array design 
     * @throws AffymetrixArrayDesignReadException if the design file associated with the design couldn't be read.
     */
    void createDesignElementList(ArrayDesign design) throws PlatformFileReadException {
        if (!LSID_AUTHORITY.equals(design.getLsidAuthority())) {
            throw new IllegalArgumentException("ArrayDesign must be an Affymetrix design");
        }
        
        DesignElementList designElementList = new DesignElementList();
        designElementList.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE_ELEMENT_LIST
                + ":" + getDesignElementListObjectId(design));
        designElementList.setDesignElementTypeEnum(DesignElementType.LOGICAL_PROBE);
        arrayDao.save(designElementList);

        createDesignElementListEntries(designElementList, design);
    }

    protected abstract void createDesignElementListEntries(DesignElementList designElementList, ArrayDesign design)
            throws PlatformFileReadException;

    /**
     * Returns the existing <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design, or null if none exists.
     *
     * @param design the design to retrieve the <code>DesignElementList</code> for
     * @param arrayDesignService service instance used to retrieve the list
     * @return the corresponding list or null.
     */
    DesignElementList getDesignElementList(ArrayDesign design) {
        return arrayDao.getDesignElementList(LSID_AUTHORITY, LSID_NAMESPACE_ELEMENT_LIST,
                getDesignElementListObjectId(design));
    }

    private static String getDesignElementListObjectId(ArrayDesign design) {
        return LSID_OBJECT_ID_ELEMENT_LIST_PREFIX + "." + design.getLsidObjectId();
    }

    protected void flushAndCommitTransaction() {
        sessionTransactionManager.flushSession();
        sessionTransactionManager.clearSession();
        sessionTransactionManager.commitTransaction();
        sessionTransactionManager.beginTransaction();        
    }

    protected ArrayDao getArrayDao() {
        return arrayDao;
    }
}
