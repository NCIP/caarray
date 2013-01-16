//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package caarray.client.examples.grid;

public final class BaseProperties {
    // Connection properties
    public static final String GRID_SERVER_HOSTNAME_KEY = "grid.server.hostname";
    public static final String GRID_SERVER_PORT_KEY = "grid.server.http.port";

    public static final String GRID_SERVER_HOSTNAME_DEFAULT = "array-stage.nci.nih.gov";
    public static final String GRID_SERVER_PORT_DEFAULT = "80";

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
    public static String getGridServerHostname() {
        return System.getProperty(GRID_SERVER_HOSTNAME_KEY, GRID_SERVER_HOSTNAME_DEFAULT);
    }

    public static int getGridServerPort() {
        return Integer.parseInt(System.getProperty(GRID_SERVER_PORT_KEY, GRID_SERVER_PORT_DEFAULT));
    }

    public static String getGridServiceUrl() {
        return ("http://" + getGridServerHostname() + ":" + getGridServerPort() + "/wsrf/services/cagrid/CaArraySvc_v1_0");
    }
}
