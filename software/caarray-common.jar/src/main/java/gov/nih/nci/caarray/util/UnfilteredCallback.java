//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.util;

import org.hibernate.Session;

/**
 * @author Winston Cheng
 *
 */
public interface UnfilteredCallback {
    /**
     * Do something with an unfiltered session.
     * @param s Hibernate session
     * @return the result
     */
    Object doUnfiltered(Session s);
}
