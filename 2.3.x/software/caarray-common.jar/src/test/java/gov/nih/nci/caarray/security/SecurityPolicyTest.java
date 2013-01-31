//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import org.junit.Test;

/**
 * @author dkokotov
 * 
 */
public class SecurityPolicyTest extends AbstractCaarrayTest {    
    @Test
    public void testSecurityPolicy() {
        TestClass test = new TestClass();
        assertEquals(SecurityPolicyMode.WHITELIST, SecurityPolicy.BROWSE.getMode());
        assertTrue(SecurityPolicy.BROWSE.allowProperty(test, "foo"));
        assertFalse(SecurityPolicy.BROWSE.allowProperty(test, "baz"));        
        assertEquals(SecurityPolicyMode.BLACKLIST, SecurityPolicy.TCGA.getMode());
        assertTrue(SecurityPolicy.TCGA.allowProperty(test, "foo"));
        assertFalse(SecurityPolicy.TCGA.allowProperty(test, "baz"));        
    }

    private static class TestClass {
        private String foo;
        private String baz;

        /**
         * @return the foo
         */
        @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME)
        public String getFoo() {
            return foo;
        }

        /**
         * @param foo the foo to set
         */
        public void setFoo(String foo) {
            this.foo = foo;
        }

        /**
         * @return the baz
         */
        @AttributePolicy(deny = SecurityPolicy.TCGA_POLICY_NAME)
        public String getBaz() {
            return baz;
        }

        /**
         * @param baz the baz to set
         */
        public void setBaz(String baz) {
            this.baz = baz;
        }

    }
}
