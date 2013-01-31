//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.upgrade;

import gov.nih.nci.caarray.application.GenericDataService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;

import org.w3c.dom.Element;

import affymetrix.fusion.chp.FusionCHPGenericData;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionCHPTilingData;

/**
 * Base class with useful resources for migrators.
 */
public abstract class AbstractMigrator implements Migrator {

    private final ArrayDesignService designService;
    private final GenericDataService dataService;
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;;

    /**
     * Constructor.
     */
    protected AbstractMigrator() {
        designService =
            (ArrayDesignService) ServiceLocatorFactory.getLocator().lookup(ArrayDesignService.JNDI_NAME);
        dataService =
            (GenericDataService) ServiceLocatorFactory.getLocator().lookup(GenericDataService.JNDI_NAME);
        FusionCHPLegacyData.registerReader();
        FusionCHPGenericData.registerReader();
        FusionCHPTilingData.registerReader();
    }

    /**
     * @return the daoFactory
     */
    protected CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    /**
     * Set the daoFactory.
     * @param daoFactory DAO factory
     */
    protected void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * @return the dataService
     */
    protected final GenericDataService getDataService() {
        return dataService;
    }

    /**
     * @return the designService
     */
    protected final ArrayDesignService getDesignService() {
        return designService;
    }

    /**
     * {@inheritDoc}
     */
    public void setElement(Element element) {
        // no-op
    }

}
