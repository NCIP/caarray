//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.test.jmeter.base;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Properties used by the load and stress tests.
 *
 * @author Rashmi Srinivasa
 */
public class CaArrayJmeterSampler {
    private static final String HOST_NAME_PARAM = "host.name";
    private static final String JNDI_PORT_PARAM = "jndi.port";
    private static final String GRID_SERVICE_PORT_PARAM = "grid.service.port";
    private static final String DEFAULT_HOST_NAME = "localhost";
    private static final String DEFAULT_JNDI_PORT = "1099";
    private static final String DEFAULT_GRID_SERVICE_PORT = "8080";

    protected static String getHostNameParam() {
        return HOST_NAME_PARAM;
    }

    protected static String getJndiPortParam() {
        return JNDI_PORT_PARAM;
    }

    protected static String getGridServicePortParam() {
        return GRID_SERVICE_PORT_PARAM;
    }

    protected static String getDefaultHostName() {
        return DEFAULT_HOST_NAME;
    }

    protected static String getDefaultJndiPort() {
        return DEFAULT_JNDI_PORT;
    }

    protected static String getDefaultGridServicePort() {
        return DEFAULT_GRID_SERVICE_PORT;
    }

    protected StringBuilder buildStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return new StringBuilder(sw.toString());
    }
}
