//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import gov.nih.nci.caarray.AbstractHibernateTest;

/**
 * Base class for DAO tests.
 * 
 * @author dkokotov
 */
public abstract class AbstractDaoTest extends AbstractHibernateTest {
    public AbstractDaoTest() {
        super(true);
    }
}
