//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.Set;

import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;

/**
 * Hibernate Post-load event listener that applies attribute security.
 *
 * @author dkokotov
 */
public class SecurityPolicyPostLoadEventListener implements PostLoadEventListener {
    private static final long serialVersionUID = -2071964672876972370L;

    /**
     * {@inheritDoc}
     */
    public void onPostLoad(PostLoadEvent event) {
        if (SecurityUtils.isPrivilegedMode()) {
            return;
        }
        
        if (event.getEntity() instanceof AbstractCaArrayObject) {
            AbstractCaArrayObject object = (AbstractCaArrayObject) event.getEntity();
            Set<SecurityPolicy> policies = object.getPostLoadSecurityPolicies(UsernameHolder.getCsmUser());
            if (!policies.isEmpty()) {
                SecurityPolicy.applySecurityPolicies(object, policies);
            }
        }
    }
}
