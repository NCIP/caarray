/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common.jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common.jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common.jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common.jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common.jar Software and any
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.search.ExampleSearchCriteria;
import gov.nih.nci.caarray.domain.search.SourceSortCriterion;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.UnfilteredCallback;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
public class SearchDaoTest extends AbstractDaoTest {
    private static final Logger LOG = Logger.getLogger(SearchDaoTest.class);

    private TermSource DUMMY_TERM_SOURCE;
    private Category DUMMY_CATEGORY;
    private Term DUMMY_TERM_1;
    private Protocol DUMMY_PROTOCOL_1; 
    private Parameter DUMMY_PARAMETER_1;
    private Parameter DUMMY_PARAMETER_2;
    private Project DUMMY_PROJECT;
    private Experiment DUMMY_EXPERIMENT;
    private Source DUMMY_SOURCE_1;
    private Source DUMMY_SOURCE_2;

    private SearchDao SEARCH_DAO; 
    private ProtocolDao PROTOCOL_DAO; 

    //why is this not initialized in setup()? Intentional? 
    private AssayType DUMMY_ASSAYTYPE_1;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUp() {

        initializeDao(); 

        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY = new Category();
        DUMMY_CATEGORY.setSource(DUMMY_TERM_SOURCE);
        DUMMY_CATEGORY.setName("DummyTestCategory");
        DUMMY_TERM_1 = new Term();
        DUMMY_TERM_1.setValue("DummyTestTerm1");
        DUMMY_TERM_1.setCategory(DUMMY_CATEGORY);
        DUMMY_TERM_1.setSource(DUMMY_TERM_SOURCE);

        DUMMY_PROTOCOL_1 = new Protocol("DummyTestProtocol1", DUMMY_TERM_1, DUMMY_TERM_SOURCE);
        DUMMY_PROTOCOL_1.setDescription("DummyDescForProtocol");
        DUMMY_PROTOCOL_1.setUrl("DummyUrlForProtocol1");
        
        DUMMY_PARAMETER_1 = new Parameter("param 1", DUMMY_PROTOCOL_1);
        DUMMY_PARAMETER_2 = new Parameter("param 2", DUMMY_PROTOCOL_1);
        DUMMY_PARAMETER_1.setName("DummyTestParameter1");
        DUMMY_PARAMETER_2.setName("DummyTestParameter2");

        DUMMY_ASSAYTYPE_1 = new AssayType("aCGH");
        
        Organism org = new Organism();
        org.setScientificName("Foo");
        org.setTermSource(DUMMY_TERM_SOURCE);
        DUMMY_PROJECT = new Project();
        DUMMY_EXPERIMENT = DUMMY_PROJECT.getExperiment();
        DUMMY_EXPERIMENT.setTitle("test experiment.");
        SortedSet <AssayType>assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAYTYPE_1);
        DUMMY_EXPERIMENT.setAssayTypes(assayTypes);
        DUMMY_EXPERIMENT.setOrganism(org);
        DUMMY_EXPERIMENT.setManufacturer(new Organization());
        DUMMY_SOURCE_1 = new Source();
        DUMMY_SOURCE_1.setName("Source 1 Name");
        DUMMY_SOURCE_1.setDescription("ZZZ");
        DUMMY_EXPERIMENT.getSources().add(DUMMY_SOURCE_1);
        DUMMY_SOURCE_1.setExperiment(DUMMY_EXPERIMENT);
        DUMMY_SOURCE_2 = new Source();
        DUMMY_SOURCE_2.setName("Source 2 Name");
        DUMMY_SOURCE_2.setDescription("AAA");
        DUMMY_EXPERIMENT.getSources().add(DUMMY_SOURCE_2);
        DUMMY_SOURCE_2.setExperiment(DUMMY_EXPERIMENT);

    }
    
    private void initializeDao() {
        if (SEARCH_DAO == null) {
            SEARCH_DAO = CaArrayDaoFactory.INSTANCE.getSearchDao();
        }
        
        if (PROTOCOL_DAO == null) {
            PROTOCOL_DAO = CaArrayDaoFactory.INSTANCE.getProtocolDao();
        }
    }

    private void saveSupportingObjects() {
        Transaction tx = hibernateHelper.beginTransaction();
        PROTOCOL_DAO.save(DUMMY_TERM_SOURCE);
        PROTOCOL_DAO.save(DUMMY_CATEGORY);
        PROTOCOL_DAO.save(DUMMY_TERM_SOURCE);
        PROTOCOL_DAO.save(DUMMY_PROTOCOL_1);
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_1);
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_2);
        PROTOCOL_DAO.save(DUMMY_PROTOCOL_1);
        PROTOCOL_DAO.save(DUMMY_ASSAYTYPE_1);
        PROTOCOL_DAO.save(DUMMY_PROJECT);
        tx.commit();
    }

    /**
     * Tests searching for an entity by example.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testSearchByExample() {
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
            Protocol exampleProtocol = setUpExampleProtocol();
            List<Protocol> matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));

            // search by id
            exampleProtocol = new Protocol();
            exampleProtocol.setId(DUMMY_PROTOCOL_1.getId());
            exampleProtocol.setDescription("differentDescription");
            matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1.getName(), matchingProtocols.get(0).getName());
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }
    
    @Test
    public void testSearchByExampleLimitOffset() {
        Transaction tx = hibernateHelper.beginTransaction();

        AssayType at1 = new AssayType("AT1");        
        AssayType at2 = new AssayType("AT2");
        SEARCH_DAO.save(Arrays.asList(at1, at2));
        
        TermSource ts1 = new TermSource();
        ts1.setName("TS1");
        SEARCH_DAO.save(ts1);
        
        Organism o1 = new Organism();
        o1.setScientificName("Foo");
        o1.setTermSource(ts1);
        SEARCH_DAO.save(o1);
        
        Experiment e1 = new Experiment();
        e1.setTitle("e1");
        e1.setOrganism(o1);
        e1.getAssayTypes().add(at1);
        e1.getAssayTypes().add(at2);
        Project p1 = new Project();
        p1.setExperiment(e1);

        Experiment e2 = new Experiment();
        e2.setTitle("e2");
        e2.setOrganism(o1);
        e2.getAssayTypes().add(at1);
        Project p2 = new Project();
        p2.setExperiment(e2);

        Experiment e3 = new Experiment();
        e3.setTitle("e3");
        e3.setOrganism(o1);
        e3.getAssayTypes().add(at2);
        Project p3 = new Project();
        p3.setExperiment(e3);

        Experiment e4 = new Experiment();
        e4.setTitle("e4");
        e4.setOrganism(o1);
        e4.getAssayTypes().add(at2);
        Project p4 = new Project();
        p4.setExperiment(e4);
        
        SEARCH_DAO.save(Arrays.asList(p1, p2, p3, p4));

        tx.commit();
        tx = hibernateHelper.beginTransaction();
        @SuppressWarnings("unchecked")
        List<Experiment> results = (List<Experiment>) hibernateHelper.doUnfiltered(new UnfilteredCallback() {
            public Object doUnfiltered(Session s) {
                return SEARCH_DAO.queryEntityByExample(ExampleSearchCriteria.forEntity(new Experiment()), 3, 0, Order
                        .asc("title"));
            }
        });
        assertEquals(3, results.size());
        assertEquals("e1", results.get(0).getTitle());
        assertEquals("e2", results.get(1).getTitle());
        assertEquals("e3", results.get(2).getTitle());
        tx.commit();
    }

/*
    @Test
    public void testSearchByExampleSuperclassAssociations() {
        Transaction tx =  hibernateHelper.beginTransaction();
        try {
            Organism o = new Organism();
            o.setCommonName("Foo");
            o.setScientificName("Faz");
            o.setTermSource(DUMMY_TERM_SOURCE);
            SEARCH_DAO.save(o);
            Organism o2 = new Organism();
            o2.setCommonName("Boo");
            o2.setScientificName("Baz");
            o2.setTermSource(DUMMY_TERM_SOURCE);
            SEARCH_DAO.save(o2);
            Experiment e = new Experiment();
            Project p = new Project();
            p.setExperiment(e);
            e.setTitle("Foo");
            e.setOrganism(o);
            SEARCH_DAO.save(p);
            Source s1 = new Source();
            s1.setName("TEstSource1");
            s1.setOrganism(o);
            s1.setExperiment(e);
            SEARCH_DAO.save(s1);
            Source s2 = new Source();
            s2.setName("TEstSource2");
            s2.setOrganism(o2);
            s2.setExperiment(e);
            SEARCH_DAO.save(s2);
            tx.commit();

            tx = hibernateHelper.beginTransaction();
            Source exSource = new Source();
            Organism exOrg = new Organism();
            exOrg.setCommonName("Boo");
            exSource.setOrganism(exOrg);
            List<Source> results = SEARCH_DAO.queryEntityByExample(exSource, Order.asc("name"));
            assertEquals(1, results.size());
            assertEquals("TEstSource2", results.get(0).getName());
            exOrg.setCommonName("Moo");
            results = SEARCH_DAO.queryEntityByExample(exSource, Order.asc("name"));
            assertEquals(0, results.size());
            exOrg.setCommonName("oo");
            results = SEARCH_DAO.queryEntityByExample(ExampleSearchCriteria.forEntity(exSource).matchUsing(
                    MatchMode.ANYWHERE), Order.desc("name"));
            assertEquals(2, results.size());
            assertEquals("TEstSource2", results.get(0).getName());
            assertEquals("TEstSource1", results.get(1).getName());
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }
*/

    /**
     * Tests searching for an entity by example.
     */
    @Test
    public void testRetrieveByIds() {
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
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
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    /**
     * tests the findValuesWithSamePrefix method
     */
    @Test
    public void testFindValuesByPrefix() {
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
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
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    /**
     * Tests searching for an entity using CQL.
     */
    @Test
    public void testCqlSearch() {
        saveSupportingObjects();
        CQLQuery cqlQuery = formulateCqlQuery();

        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during CQL search: " + e.getMessage());
        }
    }

    /**
     * Tests searching for an entity using CQL, where the search involves associations.
     */
    @Test
    public void testCqlSearchWithAssociations() {
        saveSupportingObjects();
        CQLQuery cqlQuery = formulateCqlQueryWithAssociations();

        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during CQL search: " + e.getMessage());
        }
    }

// TODO: commented out temporarily per JIRA ticket ARRAY-2131, due to CQL2HQL bug that becomes apparent when
// using Hibernate 3.3.1 but not 3.2.0     
//    /**
//     * Tests searching for an entity using CQL, where the search involves associations.
//     */
//    @Test
//    public void testCqlSearchWithManyValuedAssociations() {
//        saveSupportingObjects();
//        CQLQuery cqlQuery = formulateCqlQueryWithManyValuedAssociation();
//
//        Transaction tx = null;
//        try {
//            tx = hibernateHelper.beginTransaction();
//            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
//            assertEquals(1, matchingProtocols.size());
//            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
//            tx.commit();
//        } catch (DAOException e) {
//            hibernateHelper.rollbackTransaction(tx);
//            fail("DAO exception during CQL search: " + e.getMessage());
//        }
//    }
    
    /**
     * Tests searching for an entity using CQL, where the search involves associations.
     */
    @Test
    public void testCqlSearchWithCount() {
        saveSupportingObjects();
        CQLQuery cqlQuery = formulateCqlQueryWithAssociations();
        QueryModifier modifier = new QueryModifier();
        modifier.setCountOnly(true);
        cqlQuery.setQueryModifier(modifier);

        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
            List<?> results = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, results.size());
            assertEquals(1L, results.get(0));
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
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
        type.setRoleName("type");
        Attribute termValue = new Attribute();
        termValue.setName("value");
        termValue.setValue(DUMMY_TERM_1.getValue());
        termValue.setPredicate(Predicate.EQUAL_TO);
        type.setAttribute(termValue);
        target.setAssociation(type);
        
        // Set the target for the query.
        cqlQuery.setTarget(target);
        return cqlQuery;
    }

    private CQLQuery formulateCqlQueryWithManyValuedAssociation() {
        CQLQuery cqlQuery = new CQLQuery();

        // Set the target object to Protocol.
        gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
        target.setName("gov.nih.nci.caarray.domain.protocol.Protocol");

        Association param = new Association();
        param.setName("gov.nih.nci.caarray.domain.protocol.Parameter");
        param.setRoleName("parameters");
        Attribute paramNotNull = new Attribute();
        paramNotNull.setName("id");
        paramNotNull.setPredicate(Predicate.IS_NOT_NULL);
        param.setAttribute(paramNotNull);
        target.setAssociation(param);
        
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
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();
            Object obj = SEARCH_DAO.retrieve(Protocol.class, 999l);
            assertEquals(null, obj);

            obj = SEARCH_DAO.retrieve(Protocol.class, DUMMY_PROTOCOL_1.getId());
            assertEquals(DUMMY_PROTOCOL_1, obj);

            obj = SEARCH_DAO.retrieve(Term.class, DUMMY_TERM_1.getId());
            assertEquals(DUMMY_TERM_1, obj);
            ((Term) obj).setValue("Foo");

            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }
    @Test
    public void testCollectionFilterPaging() {
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();

            Experiment retrievedExperiment = SEARCH_DAO.retrieve(Experiment.class, DUMMY_EXPERIMENT.getId());
            List<Source> filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "");
            assertEquals(2, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce ");
            assertEquals(2, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce 2");
            assertEquals(1, filteredList.size());

            filteredList = SEARCH_DAO.filterCollection(retrievedExperiment.getSources(), "name", "SoUrce 3");
            assertEquals(0, filteredList.size());

            int collSize = SEARCH_DAO.collectionSize(retrievedExperiment.getSources());
            assertEquals(2, collSize);

            PageSortParams<Source> params = new PageSortParams<Source>(1, 1, SourceSortCriterion.NAME, false);
            filteredList = SEARCH_DAO.pageCollection(retrievedExperiment.getSources(), params);
            assertEquals(1, filteredList.size());
            assertEquals(DUMMY_SOURCE_2, filteredList.get(0));
            params.setDesc(true);
            params.setSortCriterion(SourceSortCriterion.DESCRIPTION);
            params.setPageSize(2);
            params.setIndex(0);
            filteredList = SEARCH_DAO.pageCollection(retrievedExperiment.getSources(), params);
            assertEquals(2, filteredList.size());
            assertEquals(DUMMY_SOURCE_1, filteredList.get(0));
            assertEquals(DUMMY_SOURCE_2, filteredList.get(1));
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    @Test
    public void testPageAndFilterCollection() {
        saveSupportingObjects();
        Transaction tx = null;
        try {
            tx = hibernateHelper.beginTransaction();

            PageSortParams<Source> params = new PageSortParams<Source>(1, 1, SourceSortCriterion.NAME, false);
            Experiment e = SEARCH_DAO.retrieve(Experiment.class, DUMMY_EXPERIMENT.getId());
            
            List<Source> filteredList = SEARCH_DAO.pageAndFilterCollection(e.getSources(), "name", null, params);
            assertEquals(1, filteredList.size());
            assertEquals("Source 2 Name", filteredList.get(0).getName());    
            
            filteredList = SEARCH_DAO.pageAndFilterCollection(e.getSources(), "name", Arrays.asList("Source 1 Name", "Source 2 Name"), params);
            assertEquals(1, filteredList.size());
            assertEquals("Source 2 Name", filteredList.get(0).getName());    

            params.setIndex(0);
            filteredList = SEARCH_DAO.pageAndFilterCollection(e.getSources(), "name", Collections.singletonList("Source 1 Name"), params);
            assertEquals(1, filteredList.size());
            assertEquals("Source 1 Name", filteredList.get(0).getName());    

            params.setPageSize(2);
            params.setIndex(0);
            params.setSortCriterion(SourceSortCriterion.DESCRIPTION);
            params.setDesc(true);
            filteredList = SEARCH_DAO.pageAndFilterCollection(e.getSources(), "name", Arrays.asList("Source 1 Name", "Source 2 Name"), params);
            assertEquals(2, filteredList.size());
            assertEquals("Source 1 Name", filteredList.get(0).getName());    
            assertEquals("Source 2 Name", filteredList.get(1).getName());    
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during search by example: " + e.getMessage());
        }
    }

    @Test
    public void testDefect10709() {
        saveSupportingObjects();
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
            tx = hibernateHelper.beginTransaction();
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            // just making sure it ran through w/o exception or null return
            assertNotNull(matchingProtocols);
            tx.commit();
        } catch (DAOException e) {
            hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }
}
