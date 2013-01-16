//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.java;

public final class BaseProperties {
    // Connection properties
    public static final String SERVER_HOSTNAME_KEY = "server.hostname";
    public static final String SERVER_JNDI_PORT_KEY = "server.jndi.port";

    public static final String SERVER_HOSTNAME_DEFAULT = "array-stage.nci.nih.gov";
    public static final String SERVER_JNDI_PORT_DEFAULT = "8080";

    // Array designs
    public static final String AFFYMETRIX_DESIGN = "Test3";
    public static final String GENEPIX_DESIGN = "JoeDeRisi-fix";
    public static final String ILLUMINA_DESIGN = "Human_WG-6";

    // Experiment names and public identifiers
    public static final String AFFYMETRIX_EXPERIMENT = "Affymetrix Experiment for API Testing";
    public static final String GENEPIX_EXPERIMENT = "Genepix Experiment for API Testing";
    public static final String ILLUMINA_EXPERIMENT = "Illumina Experiment for API Testing";

    // Sample names and hybridization names
    public static final String SAMPLE_NAME_01 = "ApiTestSample01";
    public static final String SAMPLE_NAME_02 = "ApiTestSample02";
    public static final String HYBRIDIZATION_NAME_01 = "ApiTestHybridization01";
    public static final String HYBRIDIZATION_NAME_02 = "ApiTestHybridization02";

    // Quantitation types
    public static final String AFFYMETRIX_CHP_QUANTITATION_TYPES = "CHPSignal,CHPSignalLogRatio";
    public static final String AFFYMETRIX_CEL_QUANTITATION_TYPES = "CELX,CELY,CELintensity,CELintensityStdev";
    public static final String GENEPIX_QUANTITATION_TYPES = "F635 Mean,F635 Median";
    public static final String ILLUMINA_QUANTITATION_TYPES = "AVG_Signal,BEAD_STDEV,Avg_NBEADS,Detection";

    // Data File names
    public static final String CEL_DATA_FILE_NAME = "ApiTestAffymetrixData.CEL";

    // Properties used by for CAS Remote EJB example

    // Next 6 props are for compiling the CAS Service URL according to the pattern: http://localhost:38080/caarray
    public static final String SERVICE_URL_SCHEME_KEY = "cas.service.url.scheme";
    public static final String SERVICE_URL_PORT_KEY = "cas.service.url.port";
    public static final String SERVICE_URL_CTXROOT_KEY = "cas.service.url.ctxroot";
    public static final String SERVICE_URL_SCHEME_DEFAULT = "http";
    public static final String SERVICE_URL_PORT_DEFAULT = "47210";
    public static final String SERVICE_URL_CTXROOT_DEFAULT = "caintegrator";

    // Rest of the CAS properties
    public static final String CAS_URL = "https://localhost:8443/cas";
    public static final String CAS_USERNAME = "casadmin";
    public static final String CAS_PASSWORD = "casadmin";
    public static final String CAS_USER_CAARRAY_PASSWORD = "caArray2!";

    // Getters for Connection properties
    public static String getServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }

    // CAS service URL compilation
    public static String getServiceURLforCAS() {
        String scheme  = System.getProperty( SERVICE_URL_SCHEME_KEY, SERVICE_URL_SCHEME_DEFAULT );
        String host    = System.getProperty( SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
        String port    = System.getProperty( SERVICE_URL_PORT_KEY, SERVICE_URL_PORT_DEFAULT );
        String ctxroot = System.getProperty( SERVICE_URL_CTXROOT_KEY, SERVICE_URL_CTXROOT_DEFAULT );

        String serviceURL = scheme + "://" + host + ":" + port + "/" + ctxroot;
        return serviceURL;
    }
}
