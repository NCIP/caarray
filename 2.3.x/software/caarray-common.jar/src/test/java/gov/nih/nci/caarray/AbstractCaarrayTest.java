//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray;

import gov.nih.nci.caarray.security.SecurityUtils;

import org.junit.BeforeClass;

/**
 * Base class for all caArray tests, ensures setup of security framework and other common tasks
 * 
 * @author dkokotov
 *
 */
public abstract class AbstractCaarrayTest {
    public static final String STANDARD_USER = "caarrayadmin";
    
    @BeforeClass
    public static void globalInit() {
        SecurityUtils.init();
    }
}
