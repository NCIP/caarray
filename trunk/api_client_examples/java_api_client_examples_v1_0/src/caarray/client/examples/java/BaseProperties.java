//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
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

    // Getters for Connection properties
    public static String getServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }
}
