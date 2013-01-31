//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ServiceType;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import com.fiveamsolutions.nci.commons.data.search.PageSortParams;
import com.fiveamsolutions.nci.commons.util.HibernateHelper;

/**
 * Unit tests for the Search DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class SearchDaoTest {
    private static final Logger LOG = Logger.getLogger(SearchDaoTest.class);

    private static final String FAIL_NO_MATCH = "Retrieved protocol is different from saved protocol.";
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static final Category DUMMY_CATEGORY = new Category();
    private static final Term DUMMY_TERM_1 = new Term();
    private static final Protocol DUMMY_PROTOCOL_1 = new Protocol("DummyTestProtocol1", DUMMY_TERM_1, new TermSource());
    private static final Parameter DUMMY_PARAMETER_1 = new Parameter("param 1", DUMMY_PROTOCOL_1);
    private static final Parameter DUMMY_PARAMETER_2 = new Parameter("param 2", DUMMY_PROTOCOL_1);

    private static final SearchDao SEARCH_DAO = CaArrayDaoFactory.INSTANCE.getSearchDao();
    private static final ProtocolDao PROTOCOL_DAO = CaArrayDaoFactory.INSTANCE.getProtocolDao();

    private static AssayType DUMMY_ASSAYTYPE_1 = new AssayType("aCGH");

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUp() {
        // Initialize all the dummy objects needed for the tests.
        initializeParameters();
        initializeProtocols();
        Transaction tx = null;

        // Save dummy objects to database.
        try {
            tx = HibernateUtil.beginTransaction();
            DUMMY_PROTOCOL_1.getSource().setName("testName");
            PROTOCOL_DAO.save(DUMMY_PROTOCOL_1);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.error("Error setting up dummy protocol.", e);
        }
    }

    /**
     * Initialize the dummy <code>Parameter</code> objects.
     */
    private static void initializeParameters() {
        DUMMY_PARAMETER_1.setName("DummyTestParameter1");
        DUMMY_PARAMETER_2.setName("DummyTestParameter2");
    }


    /**
     * Initialize the dummy <code>Protocol</code> objects.
     */
    private static void initializeProtocols() {
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_CATEGORY.setName("DummyTestCategory");
        DUMMY_TERM_1.setValue("DummyTestTerm1");
        DUMMY_TERM_1.setCategory(DUMMY_CATEGORY);
        DUMMY_TERM_1.setSource(DUMMY_TERM_SOURCE);

        DUMMY_PROTOCOL_1.setDescription("DummyDescForProtocol");
        DUMMY_PROTOCOL_1.setUrl("DummyUrlForProtocol1");
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_1);
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_2);
    }

    /**
     * Tests searching for an entity by example.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testSearchByExample() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            Protocol exampleProtocol = setUpExampleProtocol();
            Protocol retrievedProtocol = null;
            List<Protocol> matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            if ((matchingProtocols != null) && (matchingProtocols.size() >= 1)) {
                retrievedProtocol = matchingProtocols.get(0);
            }
            if (DUMMY_PROTOCOL_1.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail(FAIL_NO_MATCH);
            }

            // search by id
            exampleProtocol = new Protocol();
            exampleProtocol.setId(DUMMY_PROTOCOL_1.getId());
            exampleProtocol.setDescription("differentDescription");
            matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            if (matchingProtocols != null && matchingProtocols.size() > 0
                    && DUMMY_PROTOCOL_1.equals(matchingProtocols.get(0))) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail(FAIL_NO_MATCH);
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    /**
     * Tests searching for an entity by example.
     */
    @Test
    public void testRetrieveByIds() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            List<Long> paramIds = Arrays.asList(new Long[] { DUMMY_PARAMETER_1.getId(), DUMMY_PARAMETER_2.getId() });
            Long protocolId = DUMMY_PROTOCOL_1.getId();
            List<Long> combinedIds = new ArrayList<Long>(paramIds);
            combinedIds.add(-1L);
            List<Long> emptyIds = Collections.emptyList();

            List<Parameter> params = SEARCH_DAO.retrieveByIds(Parameter.class, paramIds);
            assertEquals(2, params.size());
            assertTrue(params.contains(DUMMY_PARAMETER_1));
            assertTrue(params.contains(DUMMY_PARAMETER_2));

            params = SEARCH_DAO.retrieveByIds(Parameter.class, combinedIds);
            assertEquals(2, params.size());
            assertTrue(params.contains(DUMMY_PARAMETER_1));
            assertTrue(params.contains(DUMMY_PARAMETER_2));

            List<Protocol> protocols = SEARCH_DAO.retrieveByIds(Protocol.class, Collections.singletonList(protocolId));
            assertEquals(1, protocols.size());
            assertEquals(DUMMY_PROTOCOL_1, protocols.get(0));

            protocols = SEARCH_DAO.retrieveByIds(Protocol.class, Collections.singletonList(-1L));
            assertEquals(0, protocols.size());

            protocols = SEARCH_DAO.retrieveByIds(Protocol.class, emptyIds);
            assertEquals(0, protocols.size());

            for (int i = 0; i < HibernateHelper.MAX_IN_CLAUSE_LENGTH + 50; i++) {
                combinedIds.add(DUMMY_PARAMETER_1.getId());
            }
            for (int i = 0; i < HibernateHelper.MAX_IN_CLAUSE_LENGTH + 50; i++) {
                combinedIds.add(DUMMY_PARAMETER_2.getId());
            }
            for (int i = 0; i < HibernateHelper.MAX_IN_CLAUSE_LENGTH + 50; i++) {
                combinedIds.add(-1L);
            }
            params = SEARCH_DAO.retrieveByIds(Parameter.class, combinedIds);
            assertEquals(2, params.size());
            assertTrue(params.contains(DUMMY_PARAMETER_1));
            assertTrue(params.contains(DUMMY_PARAMETER_2));

            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    /**
     * tests the findValuesWithSamePrefix method
     */
    @Test
    public void testFindValuesByPrefix() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            List<String> values = SEARCH_DAO.findValuesWithSamePrefix(Parameter.class, "name", "DummyTest");
            assertNotNull(values);
            assertEquals(2, values.size());
            assertTrue(values.contains("DummyTestParameter1"));
            assertTrue(values.contains("DummyTestParameter2"));
            values = SEARCH_DAO.findValuesWithSamePrefix(Parameter.class, "name", "DummyTestParameter1");
            assertNotNull(values);
            assertEquals(1, values.size());
            assertTrue(values.contains("DummyTestParameter1"));
            values = SEARCH_DAO.findValuesWithSamePrefix(Parameter.class, "name", "DummyTestParameter23");
            assertNotNull(values);
            assertEquals(0, values.size());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    /**
     * Tests searching for an entity using CQL.
     */
    @Test
    public void testCqlSearch() {
        CQLQuery cqlQuery = formulateCqlQuery();

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            Protocol retrievedProtocol = null;
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            if ((matchingProtocols != null) && (matchingProtocols.size() >= 1)) {
                retrievedProtocol = (Protocol) matchingProtocols.get(0);
            }
            if (DUMMY_PROTOCOL_1.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail(FAIL_NO_MATCH);
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during CQL search: " + e.getMessage());
        }
    }

    /**
     * Tests searching for an entity using CQL, where the search involves associations.
     */
    @Test
    public void testCqlSearchWithAssociations() {
        CQLQuery cqlQuery = formulateCqlQueryWithAssociations();

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            Protocol retrievedProtocol = null;
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            if ((matchingProtocols != null) && (matchingProtocols.size() >= 1)) {
                retrievedProtocol = (Protocol) matchingProtocols.get(0);
            }
            if (DUMMY_PROTOCOL_1.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail(FAIL_NO_MATCH);
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during CQL search: " + e.getMessage());
        }
    }

    private CQLQuery formulateCqlQuery() {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.protocol.Protocol");
        Attribute attribute = new Attribute();
        attribute.setName("name");
        attribute.setValue(DUMMY_PROTOCOL_1.getName());
        attribute.setPredicate(Predicate.LIKE);
        target.setAttribute(attribute);
        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private CQLQuery formulateCqlQueryWithAssociations() {
        CQLQuery cqlQuery = new CQLQuery();

        // Set the target object to Protocol.
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.protocol.Protocol");

        // Set the protocol's "type" association to a Term with a certain value.
        Association type = new Association();
        type.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute termValue = new Attribute();
        termValue.setName("value");
        termValue.setValue(DUMMY_TERM_1.getValue());
        termValue.setPredicate(Predicate.EQUAL_TO);
        type.setAttribute(termValue);

        // Set the target for the query.
        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    @SuppressWarnings("deprecation")
    private Protocol setUpExampleProtocol() {
        Protocol exampleProtocol = new Protocol();
        exampleProtocol.setDescription(DUMMY_PROTOCOL_1.getDescription());
        Term exampleTerm = new Term();
        exampleTerm.setValue(DUMMY_TERM_1.getValue());
        exampleProtocol.setType(exampleTerm);
        return exampleProtocol;
    }

    @Test
    public void testLoadById() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            Object obj = SEARCH_DAO.retrieve(Protocol.class, 999l);
            assertEquals(null, obj);

            obj = SEARCH_DAO.retrieve(Protocol.class, DUMMY_PROTOCOL_1.getId());
            assertEquals(DUMMY_PROTOCOL_1, obj);

            obj = SEARCH_DAO.retrieve(Term.class, DUMMY_TERM_1.getId());
            assertEquals(DUMMY_TERM_1, obj);
            ((Term) obj).setValue("Foo");

            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }
    @Test
    public void testCollectionFilterPaging() {
        Transaction tx = null;
        try {
            // set up dummy data
            tx = HibernateUtil.beginTransaction();
            Session s = HibernateUtil.getCurrentSession();
            Organism org = new Organism();
            org.setScientificName("Foo");
            org.setTermSource(DUMMY_TERM_SOURCE);
            Project project = new Project();
            project.getExperiment().setTitle("test experiment.");
            SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAYTYPE_1);
            project.getExperiment().setAssayTypes(assayTypes);
            project.getExperiment().setServiceType(ServiceType.FULL);
            project.getExperiment().setOrganism(org);
            project.getExperiment().setManufacturer(new Organization());
            Source source = new Source();
            source.setName("Source 1 Name");
            source.setDescription("ZZZ");
            project.getExperiment().getSources().add(source);
            Source source2 = new Source();
            source2.setName("Source 2 Name");
            source2.setDescription("AAA");
            project.getExperiment().getSources().add(source2);
            s.save(DUMMY_ASSAYTYPE_1);
            s.save(project);
            s.flush();
            s.clear();

            Experiment retrievedExperiment = SEARCH_DAO.retrieve(Experiment.class, project.getExperiment().getId());
            List<Source> filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "");
            assertEquals(2, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce ");
            assertEquals(2, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce 2");
            assertEquals(1, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce 3");
            assertEquals(0, filteredList.size());

            s.clear();
            retrievedExperiment = SEARCH_DAO.retrieve(Experiment.class, project.getExperiment().getId());
            int collSize = SEARCH_DAO.collectionSize(retrievedExperiment.getSources());
            assertEquals(2, collSize);

            s.clear();
            retrievedExperiment = SEARCH_DAO.retrieve(Experiment.class, project.getExperiment().getId());
            PageSortParams<Source> params = new PageSortParams<Source>(1, 1, SourceSortCriterion.NAME, false);
            filteredList = SEARCH_DAO.pageCollection(retrievedExperiment.getSources(), params);
            assertEquals(1, filteredList.size());
            assertEquals(source2, filteredList.get(0));
            params.setDesc(true);
            params.setSortCriterion(SourceSortCriterion.DESCRIPTION);
            params.setPageSize(2);
            params.setIndex(0);
            filteredList = SEARCH_DAO.pageCollection(retrievedExperiment.getSources(), params);
            assertEquals(2, filteredList.size());
            assertEquals(source, filteredList.get(0));
            assertEquals(source2, filteredList.get(1));
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    @Test
    public void testDefect10709() {
        CQLQuery cqlQuery = new CQLQuery();
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.sample.Sample");

        Attribute sampleNameAttribute = new Attribute();
        sampleNameAttribute.setName("name");
        String sampleName = "sampleName";
        sampleNameAttribute.setValue(sampleName );
        sampleNameAttribute.setPredicate(Predicate.EQUAL_TO);

        Association tissueSiteAssociation = new Association();
        tissueSiteAssociation.setName("gov.nih.nci.caarray.domain.vocabulary.Term");
        Attribute tissueSiteAttribute = new Attribute();
        tissueSiteAttribute.setName("value");
        String tissueSite = "tissueSite";
        tissueSiteAttribute.setValue(tissueSite);
        tissueSiteAttribute.setPredicate(Predicate.EQUAL_TO);
        tissueSiteAssociation.setAttribute(tissueSiteAttribute);
        tissueSiteAssociation.setRoleName(tissueSite); // This is the key line

        Group associations = new Group();
        associations.setAttribute(new Attribute[] {sampleNameAttribute});
        associations.setAssociation(new Association[] {tissueSiteAssociation});
        associations.setLogicRelation(LogicalOperator.AND);
        target.setGroup(associations);

        cqlQuery.setTarget(target);

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            // just making sure it ran through w/o exception or null return
            assertNotNull(matchingProtocols);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            throw e;
        }
    }
}
