//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.services.arraydesign;

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.injection.InjectionInterceptor;
import gov.nih.nci.caarray.services.AuthorizationInterceptor;
import gov.nih.nci.caarray.services.EntityConfiguringInterceptor;
import gov.nih.nci.caarray.services.HibernateSessionInterceptor;

import javax.annotation.security.PermitAll;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.google.inject.Inject;

/**
 * Implementation of the remote API array design detail retrieval subsystem.
 */
@Stateless
@Remote(ArrayDesignDetailsService.class)
@PermitAll
@Interceptors({AuthorizationInterceptor.class, HibernateSessionInterceptor.class, EntityConfiguringInterceptor.class,
        InjectionInterceptor.class })
public class ArrayDesignDetailsServiceBean implements ArrayDesignDetailsService {
    private ArrayDao arrayDao;

    /**
     * 
     * @param arrayDao the ArrayDao dependency
     */
    @Inject
    public void setArrayDao(ArrayDao arrayDao) {
        this.arrayDao = arrayDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesignDetails getDesignDetails(ArrayDesign design) {
        final ArrayDesign retrievedDesign = this.arrayDao.getArrayDesign(design.getId());
        if (retrievedDesign != null) {
            return retrievedDesign.getDesignDetails();
        } else {
            return null;
        }
    }
}
