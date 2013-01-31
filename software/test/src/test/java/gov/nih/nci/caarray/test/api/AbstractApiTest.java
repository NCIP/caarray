//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.api;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Base class for all API tests that test the Remote Java API and Grid API.
 * Provides utilities to log all output needed for caBIG Silver Compatibility review.
 *
 * @author Rashmi Srinivasa
 */
public class AbstractApiTest {
    protected static final String TEST_NAME = "TEST NAME";
    protected static final String API_CALL = "API CALL";
    protected static final String TRAVERSE_OBJECT_GRAPH = "TRAVERSING OBJECT GRAPH";
    protected static final String TEST_OUTPUT = "TEST OUTPUT";

    protected static void logForSilverCompatibility (String header, String outputText) {
        System.out.println(header + ": " + outputText);
    }

    protected StringBuilder buildStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return new StringBuilder(sw.toString());
    }
}
