//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.test.base;

/**
 * Environment properties passed in to tests.
 */
public final class TestProperties {

    public static final String SERVER_HOSTNAME_KEY = "server.hostname";
    public static final String SERVER_PORT_KEY = "server.port";
    public static final String SERVER_JNDI_PORT_KEY = "server.jndi.port";
    public static final String GRID_SERVER_HOSTNAME_KEY = "globoss.server.hostname";
    public static final String GRID_SERVER_PORT_KEY = "globoss.server.http.port";

    public static final String SERVER_HOSTNAME_DEFAULT = "localhost";
    public static final String SERVER_PORT_DEFAULT = "8080";
    public static final String SERVER_JNDI_PORT_DEFAULT = "1099";
    public static final String GRID_SERVER_PORT_DEFAULT = "8080";

    public static final String SELENIUM_SERVER_PORT_KEY = "selenium.server.port";
    public static final String SELENIUM_SERVER_PORT_DEFAULT = "8081";

    // Experiment names and array designs used in API tests
    public static final String AFFYMETRIX_SPECIFICATION_DESIGN = "Test3";
    public static final String AFFYMETRIX_HUMAN_DESIGN = "HG-U133_Plus_2";
    public static final String GENEPIX_DESIGN = "JoeDeRisi-fix";
    public static final String ILLUMINA_DESIGN = "Human_WG-6";
    public static final String AFFYMETRIX_U133A_DESIGN = "HT_HG-U133A";
    public static final String NIMBLEGEN_2005_HUMAN_DESIGN = "2005-04-20_Human_60mer_1in2";
    public static final String NIMBLEGEN_2006_HUMAN_DESIGN = "2006-08-03_HG18_60mer_expr";

    public static final String AFFYMETRIX_SPECIFICATION_WITH_DATA_01 = "Affymetrix Specification with Data 01";
    public static final String AFFYMETRIX_HUMAN_WITH_DATA_01 = "Affymetrix Human with Data 01";
    public static final String GENEPIX_COW_WITH_DATA_01 = "Genepix Cow with Data 01 ";
    public static final String ILLUMINA_RAT_WITH_DATA_01 = "Illumina Rat with Data 01";
    public static final String AFFYMETRIX_EXPERIMENT_WITH_CHP_DATA_01 = "Affymetrix Experiment with CHP Data 01";

    public static final String AFFYMETRIX_CEL_QUANTITATION_TYPES = "CELX,CELY,CELintensity,CELintensityStdev,CELMask,CELOutlier,CELPixels";
    public static final String AFFYMETRIX_CHP_QUANTITATION_TYPES = "CHPSignal";
    public static final String GENEPIX_QUANTITATION_TYPES = "F635 Mean,F635 Median";
    public static final String ILLUMINA_QUANTITATION_TYPES = "AVG_Signal,BEAD_STDEV,Avg_NBEADS,Detection";

    public static String getServerHostname() {
        return System.getProperty(SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getServerPort() {
        return Integer.parseInt(System.getProperty(SERVER_PORT_KEY, SERVER_PORT_DEFAULT));
    }

    public static int getServerJndiPort() {
        return Integer.parseInt(System.getProperty(SERVER_JNDI_PORT_KEY, SERVER_JNDI_PORT_DEFAULT));
    }

    public static int getSeleniumServerPort() {
        return Integer.parseInt(System.getProperty(SELENIUM_SERVER_PORT_KEY, SELENIUM_SERVER_PORT_DEFAULT));
    }

    public static String getGridServerHostname() {
        return System.getProperty(GRID_SERVER_HOSTNAME_KEY, SERVER_HOSTNAME_DEFAULT);
    }

    public static int getGridServerPort() {
        return Integer.parseInt(System.getProperty(GRID_SERVER_PORT_KEY, GRID_SERVER_PORT_DEFAULT));
    }

    public static String getBaseGridServiceUrl() {
        return ("http://" + getGridServerHostname() + ":" + getGridServerPort() + "/wsrf/services/cagrid/");
    }
    // Experiment names and array designs used in API tests
    public static String getAffymetrixSpecificationDesignName() {
        return AFFYMETRIX_SPECIFICATION_DESIGN;
    }
    public static String getAffymetrixU133ADesignName() {
        return AFFYMETRIX_U133A_DESIGN;
    }

    public static String getAffymetrixHumanDesignName() {
        return AFFYMETRIX_HUMAN_DESIGN;
    }

    public static String getGenepixDesignName() {
        return GENEPIX_DESIGN;
    }

    public static String getIlluminaDesignName() {
        return ILLUMINA_DESIGN;
    }

    public static String getAffymetricSpecificationName() {
        return AFFYMETRIX_SPECIFICATION_WITH_DATA_01;
    }

    public static String getAffymetricHumanName() {
        return AFFYMETRIX_HUMAN_WITH_DATA_01;
    }

    public static String getGenepixCowName() {
        return GENEPIX_COW_WITH_DATA_01;
    }

    public static String getIlluminaRatName() {
        return ILLUMINA_RAT_WITH_DATA_01;
    }

    public static String getAffymetricChpName() {
        return AFFYMETRIX_EXPERIMENT_WITH_CHP_DATA_01;
    }
}
