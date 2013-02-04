//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.registration;

import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.domain.register.RegistrationRequest;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

/**
 * Implementation entry point for the Registration subsystem.
 */
@Local(RegistrationService.class)
@Stateless
@Interceptors(InjectionInterceptor.class)
public class RegistrationServiceBean implements RegistrationService {
    private static final Logger LOG = Logger.getLogger(RegistrationServiceBean.class);
    private SearchDao searchDao;

    /**
     * 
     * @param searchDao the SearchDao dependency
     */
    @Inject
    public void setSearchDao(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void register(RegistrationRequest registrationRequest) {
        LogUtil.logSubsystemEntry(LOG, registrationRequest);
        this.searchDao.save(registrationRequest);
        LogUtil.logSubsystemExit(LOG);
    }
}
