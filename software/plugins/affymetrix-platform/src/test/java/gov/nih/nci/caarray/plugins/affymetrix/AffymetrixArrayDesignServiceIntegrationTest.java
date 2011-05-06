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
package gov.nih.nci.caarray.plugins.affymetrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.AbstractServiceIntegrationTest;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.injection.InjectorFactory;
import gov.nih.nci.caarray.platforms.LocalSessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;
import gov.nih.nci.caarray.validation.InvalidDataFileException;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Transaction;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;

/**
 * Integration Test class for ArrayDesignService subsystem.
 */
@SuppressWarnings("PMD")
public class AffymetrixArrayDesignServiceIntegrationTest extends AbstractServiceIntegrationTest {
    private ArrayDesignService arrayDesignService;
    private FileAccessServiceStub fileAccessServiceStub;
    private final VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Term DUMMY_TERM = new Term();
    private static ArrayDesign DUMMY_ARRAY_DESIGN = new ArrayDesign();
    private static AssayType DUMMY_ASSAY_TYPE = new AssayType("Gene Expression");

    private static FileType TEST_DESIGN_TYPE = new FileType("TEST_DESIGN_TYPE", FileCategory.ARRAY_DESIGN, true);
    private static FileType TEST_NONDESIGN_TYPE = new FileType("TEST_NONDESIGN_TYPE", FileCategory.RAW_DATA, true);
    private static LSID TEST_LSID = new LSID("TestAuthority", "TestNamespace", "TestId");
    private static String TEST_DESIGN_NAME = "TestName";

    @Before
    @SuppressWarnings("deprecation")
    public void setUp() {
        this.fileAccessServiceStub = this.injector.getInstance(FileAccessServiceStub.class);

        this.arrayDesignService = createArrayDesignService(this.fileAccessServiceStub, this.vocabularyServiceStub);

        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANISM.setScientificName("Homo sapiens");
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);
        DUMMY_ORGANISM.setTermSource(ts);
        DUMMY_TERM.setValue("testval");
        DUMMY_TERM.setCategory(cat);
        DUMMY_TERM.setSource(ts);

        DUMMY_ARRAY_DESIGN.setId(2L);
        DUMMY_ARRAY_DESIGN.setName("DummyTestArrayDesign1");
        DUMMY_ARRAY_DESIGN.setVersion("2.0");
        DUMMY_ARRAY_DESIGN.setGeoAccession("GPL0000");
        DUMMY_ARRAY_DESIGN.setProvider(DUMMY_ORGANIZATION);
        DUMMY_ARRAY_DESIGN.setTechnologyType(DUMMY_TERM);
        DUMMY_ARRAY_DESIGN.setOrganism(DUMMY_ORGANISM);
        DUMMY_ARRAY_DESIGN.addDesignFile(getCaArrayFile(AffymetrixArrayDesignFiles.TEST3_CDF));
        DUMMY_ARRAY_DESIGN.getDesignFileSet().updateStatus(null);
    }

    private ArrayDesignService createArrayDesignService(final FileAccessServiceStub fileAccessServiceStub,
            VocabularyServiceStub vocabularyServiceStub) {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, vocabularyServiceStub);

        final ArrayDesignServiceBean bean = new ArrayDesignServiceBean();
        this.injector.injectMembers(bean);

        return bean;
    }

    @BeforeClass
    public static void registerAffymetrix() {
        InjectorFactory.setPlatformTransactionModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SessionTransactionManager.class).to(LocalSessionTransactionManager.class);
            }
        });
        InjectorFactory.addPlatform(new AffymetrixModule());
        InjectorFactory.addPlatform(new AbstractModule() {
            @Override
            protected void configure() {
                final MapBinder<String, DataStorage> mapBinder = MapBinder.newMapBinder(binder(), String.class,
                        DataStorage.class);
                bind(FileAccessServiceStub.class).in(Scopes.SINGLETON);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).to(FileAccessServiceStub.class).in(Scopes.SINGLETON);
            }
        });
    }

    @Override
    protected Injector createInjector() {
        return InjectorFactory.getInjector();
    }

    private ArrayDesign setupAndSaveDesign(File... designFiles) throws IllegalAccessException, InvalidDataFileException {
        this.hibernateHelper.getCurrentSession().save(DUMMY_ORGANIZATION);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ORGANISM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_TERM);
        this.hibernateHelper.getCurrentSession().save(DUMMY_ASSAY_TYPE);

        final ArrayDesign design = new ArrayDesign();
        design.setName("DummyTestArrayDesign1");
        design.setVersion("2.0");
        design.setProvider(DUMMY_ORGANIZATION);
        design.setLsidForEntity("authority:namespace:" + designFiles[0].getName());
        design.getAssayTypes().add(DUMMY_ASSAY_TYPE);
        final Set<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE);
        for (final File designFile : designFiles) {
            design.addDesignFile(getCaArrayFile(designFile));
        }
        design.setTechnologyType(DUMMY_TERM);
        design.setOrganism(DUMMY_ORGANISM);
        try {
            this.arrayDesignService.saveArrayDesign(design);
        } catch (final InvalidStateException e) {
            e.printStackTrace();
            for (final InvalidValue iv : e.getInvalidValues()) {
                System.out.println("Invalid value: " + iv);
            }
        }
        return design;
    }

    @Test
    public void testImportDesignDetails_AffymetrixTest3() throws Exception {
        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(AffymetrixArrayDesignFiles.TEST3_CDF);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertNotNull(designFile.getFileType());
            }
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertEquals("", designFile.getValidationResult().toString());
            }

            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals("Test3", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("Test3", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            assertEquals(15876, design.getNumberOfFeatures().intValue());

            // now try to re-import the design over itself
            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertEquals("", designFile.getValidationResult().toString());
            }
            this.arrayDesignService.importDesignDetails(design);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals("Test3", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("Test3", design.getLsidObjectId());
            assertEquals(FileStatus.IMPORTED, design.getDesignFileSet().getStatus());
            t.commit();

            assertEquals(15876, design.getNumberOfFeatures().intValue());
        } catch (final Exception e) {
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }

    private CaArrayFile getCaArrayFile(File file) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        return caArrayFile;
    }

    @Test
    public void testImportDesignDetails_AffymetrixHuEx() throws Exception {
        Transaction t = null;
        try {
            t = this.hibernateHelper.beginTransaction();
            ArrayDesign design = setupAndSaveDesign(AffymetrixArrayDesignFiles.HUEX_TEST_CLF,
                    AffymetrixArrayDesignFiles.HUEX_TEST_PGF);
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            this.arrayDesignService.importDesign(design);
            this.arrayDesignService.importDesignDetails(design);
            this.hibernateHelper.getCurrentSession().getTransaction().commit();

            assertEquals("HuEx-1_0-st-v1-test", design.getName());
            assertEquals("Affymetrix.com", design.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", design.getLsidNamespace());
            assertEquals("HuEx-1_0-st-v1-test", design.getLsidObjectId());

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            assertEquals(1024, design.getNumberOfFeatures().intValue());
            assertEquals(94, design.getDesignDetails().getLogicalProbes().size());
            assertEquals(364, design.getDesignDetails().getProbes().size());
            assertEquals(1024, design.getDesignDetails().getFeatures().size());
            t.commit();

            t = this.hibernateHelper.beginTransaction();
            design = this.arrayDesignService.getArrayDesign(design.getId());
            final ArrayDesign otherDesign = this.arrayDesignService.getArrayDesign("Affymetrix.com",
                    "PhysicalArrayDesign", "HuEx-1_0-st-ta1-test");
            assertEquals("HuEx-1_0-st-ta1-test", otherDesign.getName());
            assertEquals("Affymetrix.com", otherDesign.getLsidAuthority());
            assertEquals("PhysicalArrayDesign", otherDesign.getLsidNamespace());
            assertEquals("HuEx-1_0-st-ta1-test", otherDesign.getLsidObjectId());
            assertEquals(1024, otherDesign.getNumberOfFeatures().intValue());
            assertEquals(94, otherDesign.getDesignDetails().getLogicalProbes().size());
            assertEquals(364, otherDesign.getDesignDetails().getProbes().size());
            assertEquals(1024, otherDesign.getDesignDetails().getFeatures().size());
            assertEquals(2, otherDesign.getDesignFiles().size());
            for (final CaArrayFile designFile : design.getDesignFiles()) {
                assertTrue(otherDesign.getDesignFiles().contains(designFile));
            }
            t.commit();

        } catch (final Exception e) {
            e.printStackTrace();
            if (t != null && t.isActive()) {
                t.rollback();
            }
            throw e;
        }
    }
}