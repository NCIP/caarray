/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
