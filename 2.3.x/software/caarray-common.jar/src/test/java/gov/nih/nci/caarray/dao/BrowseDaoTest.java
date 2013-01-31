//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.search.BrowseCategory;
import gov.nih.nci.caarray.domain.search.ProjectSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;

/**
 * @author Winston Cheng
 *
 */
@SuppressWarnings("PMD")
public class BrowseDaoTest extends AbstractDaoTest {
    // Experiment
    private static Organism DUMMY_ORGANISM_1 = new Organism();
    private static Organism DUMMY_ORGANISM_2 = new Organism();
    private static Organism DUMMY_ORGANISM_3 = new Organism();
    private static Organization DUMMY_PROVIDER = new Organization();
    private static Project DUMMY_PROJECT_1 = new Project();
    private static Project DUMMY_PROJECT_2 = new Project();
    private static Project DUMMY_PROJECT_3 = new Project();
    private static Project DUMMY_PROJECT_4 = new Project();
    private static Experiment DUMMY_EXPERIMENT_1 = new Experiment();
    private static Experiment DUMMY_EXPERIMENT_2 = new Experiment();
    private static Experiment DUMMY_EXPERIMENT_3 = new Experiment();
    private static Experiment DUMMY_EXPERIMENT_4 = new Experiment();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static TermSource DUMMY_TERM_SOURCE2 = new TermSource();
    private static AssayType DUMMY_ASSAYTYPE_1;
    private static AssayType DUMMY_ASSAYTYPE_2;

    private static final BrowseDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getBrowseDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setup() {
        DUMMY_ORGANISM_1 = new Organism();
        DUMMY_ORGANISM_2 = new Organism();
        DUMMY_ORGANISM_3 = new Organism();
        DUMMY_PROVIDER = new Organization();
        DUMMY_PROJECT_1 = new Project();
        DUMMY_PROJECT_2 = new Project();
        DUMMY_PROJECT_3 = new Project();
        DUMMY_PROJECT_4 = new Project();
        DUMMY_EXPERIMENT_1 = new Experiment();
        DUMMY_EXPERIMENT_2 = new Experiment();
        DUMMY_EXPERIMENT_3 = new Experiment();
        DUMMY_EXPERIMENT_4 = new Experiment();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_TERM_SOURCE2 = new TermSource();
        DUMMY_ASSAYTYPE_1 = new AssayType("aCGH");
        DUMMY_ASSAYTYPE_2 = new AssayType("Methylation");
        initializeProjects();
    }

    /**
     * Initialize the dummy <code>Project</code> objects.
     */
    private static void initializeProjects() {
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");

        DUMMY_TERM_SOURCE2.setName("Dummy MGED Ontology 2");
        DUMMY_TERM_SOURCE2.setUrl("test url 2");

        DUMMY_ORGANISM_1.setScientificName("organism1");
        DUMMY_ORGANISM_1.setTermSource(DUMMY_TERM_SOURCE);
        DUMMY_ORGANISM_2.setScientificName("organism2");
        DUMMY_ORGANISM_2.setTermSource(DUMMY_TERM_SOURCE);

        DUMMY_ORGANISM_3.setScientificName("organism1");
        DUMMY_ORGANISM_3.setTermSource(DUMMY_TERM_SOURCE2);

        DUMMY_PROJECT_1.setExperiment(DUMMY_EXPERIMENT_1);
        DUMMY_EXPERIMENT_1.setTitle("DummyExperiment1");
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAYTYPE_1);
        DUMMY_EXPERIMENT_1.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_1.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_1.setOrganism(DUMMY_ORGANISM_1);
        DUMMY_EXPERIMENT_1.setManufacturer(DUMMY_PROVIDER);

        DUMMY_PROJECT_2.setExperiment(DUMMY_EXPERIMENT_2);
        DUMMY_EXPERIMENT_2.setTitle("DummyExperiment2");
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAYTYPE_1);
        assayTypes.add(DUMMY_ASSAYTYPE_2);
        DUMMY_EXPERIMENT_2.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT_2.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_2.setOrganism(DUMMY_ORGANISM_2);

        DUMMY_PROJECT_3.setExperiment(DUMMY_EXPERIMENT_3);
        DUMMY_EXPERIMENT_3.setTitle("DummyExperiment3");
        DUMMY_EXPERIMENT_3.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_3.setOrganism(DUMMY_ORGANISM_1);
        DUMMY_EXPERIMENT_3.setManufacturer(DUMMY_PROVIDER);

        DUMMY_PROJECT_4.setExperiment(DUMMY_EXPERIMENT_4);
        DUMMY_EXPERIMENT_3.setTitle("DummyExperiment4");
        DUMMY_EXPERIMENT_3.setServiceType(ServiceType.FULL);
        DUMMY_EXPERIMENT_3.setOrganism(DUMMY_ORGANISM_3);
        DUMMY_EXPERIMENT_3.setManufacturer(DUMMY_PROVIDER);


    }

    @Test
    public void testCountByBrowseCategory() {
        saveProjects();
        Transaction tx = HibernateUtil.beginTransaction();
        assertEquals(3, DAO_OBJECT.countByBrowseCategory(BrowseCategory.ORGANISMS));
        tx.commit();
    }

    @Test
    public void testHybridizationCount() {
        Transaction tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.hybridizationCount();
        tx.commit();
    }

    @Test
    public void testInstitutionCount() {
        Transaction tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.institutionCount();
        tx.commit();
    }

    @Test
    public void testUserCount() {
        Transaction tx = HibernateUtil.beginTransaction();
        DAO_OBJECT.userCount();
        tx.commit();
    }

    @Test
    public void testTabList() {
        saveProjects();
        Transaction tx = HibernateUtil.beginTransaction();
        List<Object[]> tabs = DAO_OBJECT.tabList(BrowseCategory.ORGANISMS);
        assertEquals(2, tabs.size());
        Object[] tab1 = tabs.get(0);
        Object[] tab2 = tabs.get(1);
        assertEquals("organism1", tab1[0]);
        assertEquals(2, tab1[2]);
        assertEquals("organism2", tab2[0]);
        assertEquals(1, tab2[2]);
        tx.commit();
    }

    @Test
    public void testBrowseList() {
        saveProjects();
        Transaction tx = HibernateUtil.beginTransaction();
        Long id = DUMMY_ORGANISM_1.getId();
        PageSortParams<Project> psp = new PageSortParams<Project>(20,0,ProjectSortCriterion.TITLE,false);
        assertEquals(2, DAO_OBJECT.browseCount(BrowseCategory.ORGANISMS, id));
        assertEquals(2, DAO_OBJECT.browseList(psp, BrowseCategory.ORGANISMS, id).size());
        tx.commit();
    }

    private void saveProjects() {
        Transaction tx = HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getCurrentSession();
        session.save(DUMMY_ASSAYTYPE_1);
        session.save(DUMMY_ASSAYTYPE_2);
        session.save(DUMMY_ORGANISM_1);
        session.save(DUMMY_ORGANISM_2);
        session.save(DUMMY_PROJECT_1);
        session.save(DUMMY_PROJECT_2);
        session.save(DUMMY_PROJECT_3);
        tx.commit();
    }
}
