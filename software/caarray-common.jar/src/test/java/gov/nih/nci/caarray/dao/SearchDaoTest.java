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
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static final Category DUMMY_CATEGORY = new Category();
    private static final Term DUMMY_TERM_1 = new Term();
    private static final Protocol DUMMY_PROTOCOL_1 = new Protocol("DummyTestProtocol1", DUMMY_TERM_1, new TermSource());
    private static final Parameter DUMMY_PARAMETER_1 = new Parameter("param 1", DUMMY_PROTOCOL_1);
    private static final Parameter DUMMY_PARAMETER_2 = new Parameter("param 2", DUMMY_PROTOCOL_1);

    private static final SearchDao SEARCH_DAO = CaArrayDaoFactory.INSTANCE.getSearchDao();
    private static final ProtocolDao PROTOCOL_DAO = CaArrayDaoFactory.INSTANCE.getProtocolDao();

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
            List<Protocol> matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));

            // search by id
            exampleProtocol = new Protocol();
            exampleProtocol.setId(DUMMY_PROTOCOL_1.getId());
            exampleProtocol.setDescription("differentDescription");
            matchingProtocols = SEARCH_DAO.query(exampleProtocol);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
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
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
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
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
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
    public void testCqlSearchWithManyValuedAssociations() {
        CQLQuery cqlQuery = formulateCqlQueryWithManyValuedAssociation();

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            List<?> matchingProtocols = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, matchingProtocols.size());
            assertEquals(DUMMY_PROTOCOL_1, matchingProtocols.get(0));
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
    public void testCqlSearchWithCount() {
        CQLQuery cqlQuery = formulateCqlQueryWithAssociations();
        QueryModifier modifier = new QueryModifier();
        modifier.setCountOnly(true);
        cqlQuery.setQueryModifier(modifier);

        Transaction tx = null;
        try {
            tx = HibernateUtil.beginTransaction();
            List<?> results = SEARCH_DAO.query(cqlQuery);
            assertEquals(1, results.size());
            assertEquals(1, results.get(0));
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
            project.getExperiment().setAssayTypeEnum(AssayType.ACGH);
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
