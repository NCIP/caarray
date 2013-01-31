//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

/**
 * Implementation entry point for the Registration subsystem.
 */
@Local(RegistrationService.class)
@Stateless
public class RegistrationServiceBean implements RegistrationService {

    private static final Logger LOG = Logger.getLogger(RegistrationServiceBean.class);
    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void register(RegistrationRequest registrationRequest) {
        LogUtil.logSubsystemEntry(LOG, registrationRequest);
        getDaoFactory().getSearchDao().save(registrationRequest);
        LogUtil.logSubsystemExit(LOG);
    }

    CaArrayDaoFactory getDaoFactory() {
        return daoFactory;
    }

    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
