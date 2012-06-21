/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
package gov.nih.nci.caarray.plugins.genepix;

import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.B_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.DIA;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_MEDIAN_B532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F532_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_MEDIAN_B635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_PERCENT_SAT;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F635_TOTAL_INTENSITY;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.FLAGS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.F_PIXELS;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.LOG_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.MEDIAN_OF_RATIOS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.NORMALIZE;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B532_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_1SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.PERCENT_GT_B635_2SD;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIOS_SD_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RATIO_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_R2_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.RGN_RATIO_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SNR_635;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.SUM_OF_MEDIANS_635_532;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.X;
import static gov.nih.nci.caarray.plugins.genepix.GenepixQuantitationType.Y;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.GenepixArrayDataFiles;

import java.util.List;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test for file import of genepix data files
 * 
 * @author dkokotov, jscott
 */
public class GenepixFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new GenepixModule());
    }

    @Test
    public void testGenepixGprImport() throws Exception {
        importDesignAndDataFilesIntoProject(GenepixArrayDataFiles.JOE_DERISI_FIX, GprHandler.GPR_FILE_TYPE,
                GenepixArrayDataFiles.SMALL_IDF, GenepixArrayDataFiles.SMALL_SDRF, GenepixArrayDataFiles.GPR_4_1_1);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();

        final List<AbstractDesignElement> l =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
                        .getDesignElements();
        assertEquals(6528, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
                hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(1, hdl.size());

        final GenepixQuantitationType[] expectedTypes =
                new GenepixQuantitationType[] {X, Y, DIA, F635_MEDIAN, F635_MEAN, F635_SD, B635_MEDIAN, B635_MEAN,
                        B635_SD, PERCENT_GT_B635_1SD, PERCENT_GT_B635_2SD, F635_PERCENT_SAT, F532_MEDIAN, F532_MEAN,
                        F532_SD, B532_MEDIAN, B532_MEAN, B532_SD, PERCENT_GT_B532_1SD, PERCENT_GT_B532_2SD,
                        F532_PERCENT_SAT, RATIO_OF_MEDIANS_635_532, RATIO_OF_MEANS_635_532, MEDIAN_OF_RATIOS_635_532,
                        MEAN_OF_RATIOS_635_532, RATIOS_SD_635_532, RGN_RATIO_635_532, RGN_R2_635_532, F_PIXELS,
                        B_PIXELS, SUM_OF_MEDIANS_635_532, SUM_OF_MEANS_635_532, LOG_RATIO_635_532, F635_MEDIAN_B635,
                        F532_MEDIAN_B532, F635_MEAN_B635, F532_MEAN_B532, F635_TOTAL_INTENSITY, F532_TOTAL_INTENSITY,
                        SNR_635, SNR_532, FLAGS, NORMALIZE };
        for (final HybridizationData hd : hdl) {
            assertEquals(expectedTypes.length, hd.getColumns().size());
            for (int i = 0; i < expectedTypes.length; i++) {
                assertEquals(expectedTypes[i].getName(), hd.getColumns().get(i).getQuantitationType().getName());
            }
        }
        tx.commit();
    }
}
