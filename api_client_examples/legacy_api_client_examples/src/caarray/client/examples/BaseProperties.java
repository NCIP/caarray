//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples;

public final class BaseProperties {
    // Connection properties
    public static final String SERVER_HOSTNAME_KEY = "server.hostname";
    public static final String SERVER_JNDI_PORT_KEY = "server.jndi.port";
    public static final String GRID_SERVER_HOSTNAME_KEY = "globoss.server.hostname";
    public static final String GRID_SERVER_PORT_KEY = "globoss.server.http.port";

    public static final String SERVER_HOSTNAME_DEFAULT = "localhost";
    public static final String SERVER_PORT_DEFAULT = "28080";
    public static final String SERVER_JNDI_PORT_DEFAULT = "1099";
    public static final String GRID_SERVER_HOSTNAME_DEFAULT = "localhost";
    public static final String GRID_SERVER_PORT_DEFAULT = "8080";

    // Array designs
    public static final String AFFYMETRIX_DESIGN = "Test3";
    public static final String GENEPIX_DESIGN = "JoeDeRisi-fix";
    public static final String ILLUMINA_DESIGN = "Human_WG-6";

    // Experiment names
    public static final String AFFYMETRIX_EXPERIMENT = "Affymetrix Experiment with CHP Data 01";
    public static final String GENEPIX_EXPERIMENT = "Genepix Mouse Experiment with Data 01";
    public static final String ILLUMINA_EXPERIMENT = "Illumina Mouse Experiment with Data 01";

    // Quantitation types
    public static final String AFFYMETRIX_CEL_QUANTITATION_TYPES = "CELX,CELY,CELintensity,CELintensityStdev";
    public static final String AFFYMETRIX_CHP_QUANTITATION_TYPES = "CHPSignal,CHPSignalLogRatio";
    public static final String GENEPIX_QUANTITATION_TYPES = "F635 Mean,F635 Median";
    public static final String ILLUMINA_QUANTITATION_TYPES = "AVG_Signal,BEAD_STDEV,Avg_NBEADS,Detection";

    // Getters for Connection properties
    public static String getServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }

    public static String getGridServerHostname() {
        return System.getProperty(GRID_SERVER_HOSTNAME_KEY, GRID_SERVER_HOSTNAME_DEFAULT);
    }

    public static int getGridServerPort() {
        return Integer.parseInt(System.getProperty(GRID_SERVER_PORT_KEY, GRID_SERVER_PORT_DEFAULT));
    }

    public static String getGridServiceUrl() {
        return ("http://" + getGridServerHostname() + ":" + getGridServerPort() + "/wsrf/services/cagrid/CaArraySvc");
    }
}
