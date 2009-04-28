/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.AdHocSortCriterion;
import gov.nih.nci.caarray.domain.search.FileSearchCriteria;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.test.data.magetab.MageTabDataFiles;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Scott Miller
 *
 */
public class FileDaoTest extends AbstractDaoTest {

    private static final FileDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getFileDao();

    // Experiment
    private static Organism DUMMY_ORGANISM = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();
    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();

    // Annotations
    private static Term DUMMY_REPLICATE_TYPE = new Term();
    private static Term DUMMY_NORMALIZATION_TYPE = new Term();
    private static Term DUMMY_QUALITY_CTRL_TYPE = new Term();
    
    private static final VocabularyDao VOCABULARY_DAO = CaArrayDaoFactory.INSTANCE.getVocabularyDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        // Experiment
        DUMMY_ORGANISM = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();

        // Annotations
        DUMMY_REPLICATE_TYPE = new Term();
        DUMMY_NORMALIZATION_TYPE = new Term();
        DUMMY_QUALITY_CTRL_TYPE = new Term();

        // Initialize all the dummy objects needed for the tests.
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        setExperimentSummary();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM.setScientificName("Foo");
        DUMMY_ORGANISM.setTermSource(DUMMY_TERM_SOURCE);
        setExperimentAnnotations();
        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);
    }


    private static void setExperimentSummary() {
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        DUMMY_EXPERIMENT_1.setDescription("DummyExperiment1Desc");
        Date currDate = new Date();
        DUMMY_EXPERIMENT_1.setDate(currDate);
        DUMMY_EXPERIMENT_1.setPublicReleaseDate(currDate);
        DUMMY_EXPERIMENT_1.setDesignDescription("Working on it");
    }

    private static void setExperimentAnnotations() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_EXPERIMENT_1.getReplicateTypes().add(DUMMY_REPLICATE_TYPE);
        DUMMY_EXPERIMENT_1.getNormalizationTypes().add(DUMMY_NORMALIZATION_TYPE);
        DUMMY_EXPERIMENT_1.getQualityControlTypes().add(DUMMY_QUALITY_CTRL_TYPE);
    }

    private static void saveSupportingObjects() {
        DUMMY_REPLICATE_TYPE.setValue("Dummy Replicate Type");
        DUMMY_REPLICATE_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_REPLICATE_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_NORMALIZATION_TYPE.setValue("Dummy Normalization Type");
        DUMMY_NORMALIZATION_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_NORMALIZATION_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_QUALITY_CTRL_TYPE.setValue("Dummy Quality Control Type");
        DUMMY_QUALITY_CTRL_TYPE.setSource(DUMMY_TERM_SOURCE);
        DUMMY_QUALITY_CTRL_TYPE.setCategory(DUMMY_CATEGORY);
        VOCABULARY_DAO.save(DUMMY_REPLICATE_TYPE);
        VOCABULARY_DAO.save(DUMMY_QUALITY_CTRL_TYPE);
        VOCABULARY_DAO.save(DUMMY_NORMALIZATION_TYPE);
        DAO_OBJECT.save(DUMMY_PROJECT_1);
    }

    @Test
    public void testSaveAndRemove() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        CaArrayFile DUMMY_FILE_1 = new CaArrayFile();
        DUMMY_FILE_1.setName(MageTabDataFiles.SPECIFICATION_EXAMPLE_IDF.getName());
        DUMMY_FILE_1.setFileType(FileType.MAGE_TAB_IDF);
        DUMMY_FILE_1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test blob".getBytes());
        DUMMY_FILE_1.writeContents(in1);

        DUMMY_FILE_1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(DUMMY_FILE_1);
        DAO_OBJECT.save(DUMMY_FILE_1);
        tx.commit();

        tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.deleteHqlBlobsByProjectId(DUMMY_PROJECT_1.getId());
        tx.commit();
    }

    @Test
    public void testUnknownProjectIds() {
        Transaction tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.deleteHqlBlobsByProjectId(12345678L);
        DAO_OBJECT.deleteSqlBlobsByProjectId(12345678L);
        tx.commit();
    }

    @Test
    public void testSearchByCriteria() throws Exception {
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        
        
        CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1.idf");
        file1.setFileType(FileType.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test idf".getBytes());
        file1.writeContents(in1);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        Hybridization h1 = new Hybridization();
        h1.setName("h1");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h1);
        h1.setExperiment(DUMMY_EXPERIMENT_1);
        RawArrayData rawArrayData = new RawArrayData();
        h1.addRawArrayData(rawArrayData);
        rawArrayData.addHybridization(h1);
        rawArrayData.setName("h1");
        CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2.cel");
        file2.setFileType(FileType.AFFYMETRIX_CEL);
        file2.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test cel".getBytes());
        file2.writeContents(in1);
        file2.setProject(DUMMY_PROJECT_1);
        rawArrayData.setDataFile(file2);
        DUMMY_PROJECT_1.getFiles().add(file2);
        DAO_OBJECT.save(file2);
        DAO_OBJECT.save(rawArrayData);        
        
        Hybridization h2 = new Hybridization();
        h2.setName("h2");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h2);
        h2.setExperiment(DUMMY_EXPERIMENT_1);
        DerivedArrayData derivedArrayData = new DerivedArrayData();
        h2.getDerivedDataCollection().add(derivedArrayData);
        derivedArrayData.addHybridization(h2);        
        CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3.chp");
        file3.setFileType(FileType.AFFYMETRIX_CHP);
        file3.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test chp".getBytes());
        file3.writeContents(in1);
        file3.setProject(DUMMY_PROJECT_1);
        derivedArrayData.setDataFile(file3);
        DUMMY_PROJECT_1.getFiles().add(file3);
        DAO_OBJECT.save(file3);
        DAO_OBJECT.save(derivedArrayData);

        Source so1 = new Source();
        so1.setName("source");
        DUMMY_EXPERIMENT_1.getSources().add(so1);
        Sample sa1 = new Sample();
        sa1.setName("sample");
        so1.getSamples().add(sa1);
        sa1.getSources().add(so1);
        DUMMY_EXPERIMENT_1.getSamples().add(sa1);
        Extract ex1 = new Extract();
        ex1.setName("extract1");
        sa1.getExtracts().add(ex1);
        ex1.getSamples().add(sa1);
        DUMMY_EXPERIMENT_1.getExtracts().add(ex1);
        Extract ex2 = new Extract();
        ex2.setName("extract2");
        DUMMY_EXPERIMENT_1.getExtracts().add(ex2);
        LabeledExtract le1 = new LabeledExtract();
        le1.setName("LE1");
        ex1.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex1);
        ex2.getLabeledExtracts().add(le1);
        le1.getExtracts().add(ex2);
        le1.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le1);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le1);
        LabeledExtract le2 = new LabeledExtract();
        le2.setName("LE2");        
        ex2.getLabeledExtracts().add(le2);
        le2.getExtracts().add(ex2);
        le2.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le2);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le2);
        LabeledExtract le3 = new LabeledExtract();
        le3.setName("LE3");        
        le3.getHybridizations().add(h1);
        h1.getLabeledExtracts().add(le3);
        le3.getHybridizations().add(h2);
        h2.getLabeledExtracts().add(le3);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le3);
        
        CaArrayFile file4 = new CaArrayFile();
        file4.setName("file4.cdf");
        file4.setFileType(FileType.AFFYMETRIX_CDF);
        file4.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test ad".getBytes());
        file4.writeContents(in1);
        DAO_OBJECT.save(file4);

        CaArrayFile file5 = new CaArrayFile();
        file5.setName("file5.txt");
        file5.setFileStatus(FileStatus.SUPPLEMENTAL);
        ByteArrayInputStream in5 = new ByteArrayInputStream("blah blah".getBytes());
        file5.writeContents(in5);
        file5.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file5);
        DAO_OBJECT.save(file5);

        Hybridization h3 = new Hybridization();
        h3.setName("h3");        
        DUMMY_EXPERIMENT_1.getHybridizations().add(h3);
        h3.setExperiment(DUMMY_EXPERIMENT_1);
        LabeledExtract le4 = new LabeledExtract();
        le4.setName("LE4");        
        le4.getHybridizations().add(h3);
        h3.getLabeledExtracts().add(le4);
        DUMMY_EXPERIMENT_1.getLabeledExtracts().add(le4);

        tx.commit();

        tx = HibernateUtil.beginTransaction();
        Experiment e = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieve(Experiment.class, DUMMY_EXPERIMENT_1.getId());
        
        PageSortParams<CaArrayFile> params = new PageSortParams<CaArrayFile>(5, 0, new AdHocSortCriterion<CaArrayFile>("name"), false);
        FileSearchCriteria criteria = new FileSearchCriteria();        
        criteria.setExperiment(e);
        criteria.setIncludeDerived(true);
        criteria.setIncludeRaw(true);
        criteria.setIncludeSupplemental(false);
        
        List<CaArrayFile> files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.setIncludeDerived(false);
        criteria.setIncludeRaw(false);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(0, files.size());

        criteria.setIncludeDerived(true);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());

        criteria.setIncludeSupplemental(true);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file3.getName(), files.get(0).getName());
        assertEquals(file5.getName(), files.get(1).getName());

        criteria.getHybridizations().add(h1);
        criteria.getHybridizations().add(h2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());        

        criteria.setIncludeRaw(true);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());
        assertEquals(file3.getName(), files.get(1).getName());

        criteria.getHybridizations().clear();
        criteria.getHybridizations().add(h1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());                

        criteria.getHybridizations().clear();
        criteria.getHybridizations().add(h3);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(0, files.size());

        criteria.getHybridizations().clear();
        criteria.setExtension("CHP");
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());                

        criteria.setExtension(null);
        criteria.getTypes().add(FileType.AFFYMETRIX_CEL);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());                

        criteria.getTypes().clear();
        criteria.getBiomaterials().add(so1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());                

        criteria.getBiomaterials().add(sa1);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file2.getName(), files.get(0).getName());

        criteria.getBiomaterials().clear();
        criteria.getBiomaterials().add(ex1);
        criteria.getBiomaterials().add(ex2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());                
        assertEquals(file3.getName(), files.get(1).getName());                

        criteria.getBiomaterials().clear();
        criteria.getBiomaterials().add(le3);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(2, files.size());
        assertEquals(file2.getName(), files.get(0).getName());                
        assertEquals(file3.getName(), files.get(1).getName());                

        criteria.getBiomaterials().clear();
        criteria.getBiomaterials().add(le4);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(0, files.size());

        criteria.getBiomaterials().clear();
        criteria.getBiomaterials().add(le3);
        criteria.getHybridizations().add(h2);
        files = DAO_OBJECT.searchFiles(params, criteria);
        assertEquals(1, files.size());
        assertEquals(file3.getName(), files.get(0).getName());                

        tx.commit();
    }
    
    @Test
    public void testFilePermissions() throws Exception {
        UsernameHolder.setUser(STANDARD_USER);
        Transaction tx = HibernateUtil.beginTransaction();

        saveSupportingObjects();
        
        CaArrayFile file1 = new CaArrayFile();
        file1.setName("file1");
        file1.setFileType(FileType.MAGE_TAB_IDF);
        file1.setFileStatus(FileStatus.UPLOADED);
        ByteArrayInputStream in1 = new ByteArrayInputStream("test idf".getBytes());
        file1.writeContents(in1);
        file1.setProject(DUMMY_PROJECT_1);
        DUMMY_PROJECT_1.getFiles().add(file1);
        DAO_OBJECT.save(file1);

        CaArrayFile file2 = new CaArrayFile();
        file2.setName("file2");
        file2.setFileType(FileType.AFFYMETRIX_CDF);
        file2.setFileStatus(FileStatus.UPLOADED);
        in1 = new ByteArrayInputStream("test cdf".getBytes());
        file2.writeContents(in1);
        DAO_OBJECT.save(file2);

        CaArrayFile file3 = new CaArrayFile();
        file3.setName("file3");
        file3.setFileType(FileType.AFFYMETRIX_CDF);
        file3.setFileStatus(FileStatus.IMPORTED);
        in1 = new ByteArrayInputStream("test cdf".getBytes());
        file3.writeContents(in1);
        DAO_OBJECT.save(file3);

        tx.commit();

        tx = HibernateUtil.beginTransaction();
        List<CaArrayFile> files = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(3, files.size());
        assertEquals("file1", files.get(0).getName());
        assertEquals("file2", files.get(1).getName());
        assertEquals("file3", files.get(2).getName());
        tx.commit();
        
        tx = HibernateUtil.beginTransaction();
        UsernameHolder.setUser(SecurityUtils.ANONYMOUS_USERNAME);
        files = CaArrayDaoFactory.INSTANCE.getSearchDao().retrieveAll(CaArrayFile.class, Order.asc("name"));
        assertEquals(2, files.size());
        assertEquals("file2", files.get(0).getName());
        assertEquals("file3", files.get(1).getName());
        tx.commit();
    }
}
