//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author dkokotov
 * 
 */
public class SecurityPolicyTest extends AbstractCaarrayTest {    
    private static final String BLACKLIST_POLICY_NAME = "TCGA";    
    private static final SecurityPolicy BLACKLIST = new SecurityPolicy(BLACKLIST_POLICY_NAME, SecurityPolicyMode.BLACKLIST);
    


    @Test
    public void testSecurityPolicy() {
        TestClass test = new TestClass();
        assertEquals(SecurityPolicyMode.WHITELIST, SecurityPolicy.BROWSE.getMode());
        SecurityPolicy.applySecurityPolicies(test, Collections.singleton(SecurityPolicy.BROWSE));
        assertEquals("FOO", test.getFoo());
        assertNull(test.getBaz());
        assertNotNull(test.getBros());
        assertTrue(test.getBros().contains("Bros"));
        SecurityPolicy.applySecurityPolicies(test, Collections.singleton(BLACKLIST));
        assertEquals("FOO", test.getFoo());
        assertNull(test.getBaz());
        assertNotNull(test.getBros());
        assertFalse(test.getBros().contains("Bros"));
        
    }

    private static class TestClass {
        private String foo = "FOO";
        private String baz = "BAZ";
        private Set<String> bros = new HashSet<String>(Collections.singleton("Bros"));

        /**
         * @return the bros
         */
        @AttributePolicy(allow = SecurityPolicy.BROWSE_POLICY_NAME, deny = BLACKLIST_POLICY_NAME)
        public Set<String> getBros() {
            return bros;
        }

        /**
         * @param bros the bros to set
         */
        public void setBros(Set<String> bros) {
            this.bros = bros;
        }

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
        @AttributePolicy(deny = BLACKLIST_POLICY_NAME)
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
