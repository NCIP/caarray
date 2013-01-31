//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.file.FileManagementMDB;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.DesignElementType;
import gov.nih.nci.caarray.util.HibernateUtil;

/**
 * Utility class used to generate and retrieve the singleton <code>DesignElementList</code> to be used for all parsed
 * CHP files.
 */
public abstract class AbstractAffymetrixChpDesignElementListUtility {
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_ELEMENT_LIST = "DesignElementList";
    private static final String LSID_OBJECT_ID_ELEMENT_LIST_PREFIX = "LogicalProbes";

    static final int BATCH_SIZE = 500;
    static final int TRANSACTION_SIZE = 5000;

    private final ArrayDesign design;
    private final ArrayDao arrayDao;

    /**
     * Create a new instance.
     * @param design the design to create the <code>DesignElementList</code> for
     * @param arrayDao used to retrieve <code>LogicalProges</code> from design
     */
    AbstractAffymetrixChpDesignElementListUtility(ArrayDesign design, ArrayDao arrayDao) {
        this.design = design;
        this.arrayDao = arrayDao;
    }

    /**
     * Creates the singleton <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design.
     *
     * @throws AffymetrixArrayDesignReadException if the design file associated with the design couldn't be read.
     */
    public void createDesignElementList() throws AffymetrixArrayDesignReadException {
        checkDesign(design);
        DesignElementList designElementList = new DesignElementList();
        designElementList.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE_ELEMENT_LIST
                + ":" + getDesignElementListObjectId(design));
        designElementList.setDesignElementTypeEnum(DesignElementType.LOGICAL_PROBE);
        arrayDao.save(designElementList);

        createDesignElementListEntries(designElementList);
    }

    abstract void createDesignElementListEntries(DesignElementList designElementList)
            throws AffymetrixArrayDesignReadException;

    /**
     * Returns the existing <code>DesignElementList</code> for CHP files associated with the given
     * Affymetrix design, or null if none exists.
     *
     * @param design the design to retrieve the <code>DesignElementList</code> for
     * @param arrayDesignService service instance used to retrieve the list
     * @return the corresponding list or null.
     */
    public static DesignElementList getDesignElementList(ArrayDesign design, ArrayDesignService arrayDesignService) {
        return arrayDesignService.getDesignElementList(LSID_AUTHORITY, LSID_NAMESPACE_ELEMENT_LIST,
                getDesignElementListObjectId(design));
    }

    private static void checkDesign(ArrayDesign design) {
        if (!LSID_AUTHORITY.equals(design.getLsidAuthority())) {
            throw new IllegalArgumentException("ArrayDesign must be an Affymetrix design");
        }
    }

    private static String getDesignElementListObjectId(ArrayDesign design) {
        return LSID_OBJECT_ID_ELEMENT_LIST_PREFIX + "." + design.getLsidObjectId();
    }

    void flushAndCommitTransaction() {
        getArrayDao().flushSession();
        getArrayDao().clearSession();

        FileManagementMDB currentMDB = FileManagementMDB.getCurrentMDB();
        if (currentMDB != null) {
            currentMDB.commitTransaction();
            currentMDB.beginTransaction();
        } else {
            HibernateUtil.getCurrentSession().getTransaction().commit();
            HibernateUtil.getCurrentSession().beginTransaction();
        }
    }

    ArrayDao getArrayDao() {
        return arrayDao;
    }

    ArrayDesign getDesign() {
        return design;
    }

}
