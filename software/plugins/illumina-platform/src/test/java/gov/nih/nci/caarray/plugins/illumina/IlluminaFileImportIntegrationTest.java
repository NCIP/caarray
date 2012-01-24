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
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.application.file.AbstractFileManagementServiceIntegrationTest;
import gov.nih.nci.caarray.dataStorage.ParsedDataPersister;
import gov.nih.nci.caarray.domain.array.AbstractDesignElement;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.data.AbstractDataColumn;
import gov.nih.nci.caarray.domain.data.FloatColumn;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.IntegerColumn;
import gov.nih.nci.caarray.domain.data.StringColumn;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.test.data.arraydata.IlluminaArrayDataFiles;
import gov.nih.nci.caarray.test.data.arraydesign.IlluminaArrayDesignFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Integration test for the FileManagementService.
 * 
 * @author Steve Lustbader
 */
@SuppressWarnings("PMD")
public class IlluminaFileImportIntegrationTest extends AbstractFileManagementServiceIntegrationTest {
    @BeforeClass
    public static void configurePlatforms() {
        InjectorFactory.addPlatform(new IlluminaModule());
    }

    @Test
    @Ignore("Large test takes >1min, does not add to coverage.")
    public void testIlluminaCsvDataImport() throws Exception {
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.HUMAN_WG6_CSV, 
                CsvDataHandler.DATA_CSV_FILE_TYPE, IlluminaArrayDataFiles.HUMAN_WG6_SMALL);
        
        final List<String> intColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
        final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                        SampleProbeProfileQuantitationType.DETECTION.getName()));

        assertImportCorrect(10, 19, 4, intColumns, floatColumns, Collections.<String>emptyList());
    }

    @Test
    public void testIlluminaSampleProbeProfileImport() throws Exception {
        importDesignAndDataFilesIntoProject(IlluminaArrayDesignFiles.MOUSE_REF_8,
                SampleProbeProfileHandler.SAMPLE_PROBE_PROFILE_FILE_TYPE, IlluminaArrayDataFiles.SAMPLE_PROBE_PROFILE);
        
        final List<String> intColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.NARRAYS.getName(),
                        SampleProbeProfileQuantitationType.AVG_NBEADS.getName()));
            final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(SampleProbeProfileQuantitationType.MIN_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.AVG_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.MAX_SIGNAL.getName(),
                        SampleProbeProfileQuantitationType.ARRAY_STDEV.getName(),
                        SampleProbeProfileQuantitationType.BEAD_STDEV.getName(),
                        SampleProbeProfileQuantitationType.DETECTION.getName()));

        assertImportCorrect(3, 16, 8, intColumns, floatColumns, Collections.<String>emptyList());
    }

    @Test
    public void testIlluminaGenotypingProcessedMatrixImport() throws Exception {
        Transaction tx = this.hibernateHelper.beginTransaction();
        ArrayDesign design = IlluminaGenotypingProcessedMatrixHandlerTest.buildArrayDesign();
        this.hibernateHelper.getCurrentSession().save(design.getAssayTypes().iterator().next());
        this.hibernateHelper.getCurrentSession().save(design);
        tx.commit();

        importDataFilesIntoProject(design, GenotypingProcessedMatrixHandler.GENOTYPING_MATRIX_FILE_TYPE,
                IlluminaArrayDataFiles.ILLUMINA_DERIVED_1_HYB);
        
        final List<String> stringColumns =
                new ArrayList<String>(Arrays.asList(IlluminaGenotypingProcessedMatrixQuantitationType.ALLELE.getName()));
        final List<String> floatColumns =
                new ArrayList<String>(Arrays.asList(
                        IlluminaGenotypingProcessedMatrixQuantitationType.B_ALLELE_FREQ.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.GC_SCORE.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.LOG_R_RATIO.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.R.getName(),
                        IlluminaGenotypingProcessedMatrixQuantitationType.THETA.getName()));
        assertImportCorrect(3, 1, 6, Collections.<String>emptyList(), floatColumns, stringColumns);
    }

    private void assertImportCorrect(int numberOfProbes, int hybridizationDataListSize,
            int hybridizationColumns, List<String> intColumns, List<String> floatColumns, List<String> stringColumns) {
        final ParsedDataPersister pdp = this.injector.getInstance(ParsedDataPersister.class);
        final Transaction tx = this.hibernateHelper.beginTransaction();
        final Project project = getTestProject();
        final ArrayDesign design = project.getExperiment().getArrayDesigns().iterator().next();
        final Hybridization hyb = project.getExperiment().getHybridizations().iterator().next();
        final List<AbstractDesignElement> l =
            hyb.getDerivedDataCollection().iterator().next().getDataSet().getDesignElementList()
            .getDesignElements();
        assertEquals(numberOfProbes, l.size());
        for (final AbstractDesignElement de : l) {
            final PhysicalProbe p = (PhysicalProbe) de;
            assertTrue(design.getDesignDetails().getProbes().contains(p));
        }
        final List<HybridizationData> hdl =
            hyb.getDerivedDataCollection().iterator().next().getDataSet().getHybridizationDataList();
        assertEquals(hybridizationDataListSize, hdl.size());
        for (final HybridizationData hd : hdl) {
            List<String> hybIntColumns = new ArrayList<String>();
            List<String> hybFloatColumns = new ArrayList<String>();
            List<String> hybStringColumns = new ArrayList<String>();
            hybIntColumns.addAll(intColumns);
            hybFloatColumns.addAll(floatColumns);
            hybStringColumns.addAll(stringColumns);
            assertEquals(hybridizationColumns, hd.getColumns().size());
            for (final AbstractDataColumn c : hd.getColumns()) {
                pdp.loadFromStorage(c);
                final String name = c.getQuantitationType().getName();
                if (hybIntColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((IntegerColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybIntColumns.remove(c.getQuantitationType().getName()));
                } else if (hybFloatColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((FloatColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybFloatColumns.remove(c.getQuantitationType().getName()));
                } else if (hybStringColumns.contains(name)) {
                    assertEquals(numberOfProbes, ((StringColumn) c).getValues().length);
                    assertTrue("missing " + c.getQuantitationType(),
                            hybStringColumns.remove(c.getQuantitationType().getName()));
                } else {
                    fail("unexpected column: " + name);
                }
            }
            assertTrue("not all columns present", hybIntColumns.isEmpty() 
                    && hybFloatColumns.isEmpty() && hybStringColumns.isEmpty());
        }
        tx.commit();
    }
}
