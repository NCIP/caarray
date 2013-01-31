//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.ParameterValue;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Protocol DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class ProtocolDaoTest  extends AbstractDaoTest {

    private static ParameterValue DUMMY_PARAMETER_VALUE_1 = new ParameterValue();
    private static ParameterValue DUMMY_PARAMETER_VALUE_2 = new ParameterValue();
    private static ParameterValue DUMMY_PARAMETER_VALUE_3 = new ParameterValue();
    private static TermSource DUMMY_TERM_SOURCE = new TermSource();
    private static Category DUMMY_CATEGORY = new Category();
    private static TermSource DUMMY_TERM_SOURCE_1 = new TermSource();
    private static Term DUMMY_TERM_1 = new Term();
    private static Term DUMMY_TERM_2 = new Term();
    private static Protocol DUMMY_PROTOCOL_1;
    private static Protocol DUMMY_PROTOCOL_2;
    private static Protocol DUMMY_PROTOCOL_3;
    private static Parameter DUMMY_PARAMETER_1 = new Parameter("param 1", DUMMY_PROTOCOL_1);
    private static Parameter DUMMY_PARAMETER_2 = new Parameter("param 2", DUMMY_PROTOCOL_1);
    private static ProtocolApplication DUMMY_PROTOCOL_APPLICATION_1 = new ProtocolApplication();
    private static ProtocolApplication DUMMY_PROTOCOL_APPLICATION_2 = new ProtocolApplication();

    private static final ProtocolDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getProtocolDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUpBeforeClass() {
        // Initialize all the dummy objects needed for the tests.
        DUMMY_PARAMETER_VALUE_1 = new ParameterValue();
        DUMMY_PARAMETER_VALUE_2 = new ParameterValue();
        DUMMY_PARAMETER_VALUE_3 = new ParameterValue();
        DUMMY_TERM_SOURCE = new TermSource();
        DUMMY_CATEGORY = new Category();
        DUMMY_TERM_1 = new Term();
        DUMMY_TERM_2 = new Term();
        DUMMY_TERM_SOURCE_1 = new TermSource();
        DUMMY_TERM_SOURCE_1.setName("TestTermSource1");
        DUMMY_PROTOCOL_1 = new Protocol("DummyTestProtocol1", DUMMY_TERM_1, DUMMY_TERM_SOURCE_1);
        DUMMY_PROTOCOL_2 = new Protocol("DummyTestProtocol2", DUMMY_TERM_2, DUMMY_TERM_SOURCE_1);
        DUMMY_PROTOCOL_3 = new Protocol("DummyTestProtocol3", DUMMY_TERM_2, DUMMY_TERM_SOURCE_1);
        DUMMY_PARAMETER_1 = new Parameter("param 1", DUMMY_PROTOCOL_1);
        DUMMY_PARAMETER_2 = new Parameter("param 2", DUMMY_PROTOCOL_1);
        DUMMY_PROTOCOL_APPLICATION_1 = new ProtocolApplication();
        DUMMY_PROTOCOL_APPLICATION_2 = new ProtocolApplication();
        initializeParametersAndParamValues();
        initializeProtocols();
        initializeProtocolApps();
    }

    /**
     * Initialize the dummy <code>Parameter</code> and <code>ParameterValue</code> objects.
     */
    private static void initializeParametersAndParamValues() {
        DUMMY_PARAMETER_1.setName("DummyTestParameter1");
        //DUMMY_PARAMETER_1.setDefaultValue(DUMMY_PARAMETER_VALUE_3);
        DUMMY_PARAMETER_2.setName("DummyTestParameter2");

        DUMMY_PARAMETER_VALUE_1.setUnit("DummyUnitForParameterValue1");
        DUMMY_PARAMETER_VALUE_1.setValue("DummyValueForParameterValue1");
        DUMMY_PARAMETER_VALUE_1.setParameter(DUMMY_PARAMETER_1);
        DUMMY_PARAMETER_VALUE_2.setUnit("DummyUnitForParameterValue2");
        DUMMY_PARAMETER_VALUE_2.setValue("DummyValueForParameterValue2");
        DUMMY_PARAMETER_VALUE_2.setParameter(DUMMY_PARAMETER_2);
        DUMMY_PARAMETER_VALUE_3.setUnit("DummyUnitForParameterValue3");
        DUMMY_PARAMETER_VALUE_3.setValue("DummyValueForParameterValue3");
        DUMMY_PARAMETER_VALUE_3.setParameter(DUMMY_PARAMETER_1);
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
        DUMMY_TERM_2.setValue("DummyTestTerm2");
        DUMMY_TERM_2.setCategory(DUMMY_CATEGORY);
        DUMMY_TERM_2.setSource(DUMMY_TERM_SOURCE);

        DUMMY_PROTOCOL_1.setDescription("DummyDescForProtocol");
        DUMMY_PROTOCOL_1.setUrl("DummyUrlForProtocol1");
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_1);
        DUMMY_PROTOCOL_1.getParameters().add(DUMMY_PARAMETER_2);
        DUMMY_PROTOCOL_2.setDescription("DummyDescForProtocol");
        DUMMY_PROTOCOL_3.setDescription("DummyDescForProtocol");
    }

    /**
     * Initialize the dummy <code>ProtocolApplication</code> objects.
     */
    private static void initializeProtocolApps() {
        DUMMY_PROTOCOL_APPLICATION_1.setProtocol(DUMMY_PROTOCOL_1);
        DUMMY_PROTOCOL_APPLICATION_1.getValues().add(DUMMY_PARAMETER_VALUE_1);
        DUMMY_PROTOCOL_APPLICATION_1.getValues().add(DUMMY_PARAMETER_VALUE_2);
        DUMMY_PROTOCOL_APPLICATION_2.setProtocol(DUMMY_PROTOCOL_2);
    }

    /**
     * Tests retrieving the <code>Protocol</code> with the given id.
     * Test encompasses save and delete of a <code>Protocol</code> and its <code>Parameter</code>s.
     */
    @Test
    public void testGetProtocol() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_1);
            Protocol retrievedProtocol = DAO_OBJECT.getProtocol(DUMMY_PROTOCOL_1.getId());
            tx.commit();
            if (DUMMY_PROTOCOL_1.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved protocol is different from saved protocol.");
            }
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol: " + e.getMessage());
        }
    }

    /**
     * Tests saving a <code>Protocol</code> collection.
     */
    @Test
    public void testSaveProtocolCollection() {
        Transaction tx = null;
        List<Protocol> protocolList = new ArrayList<Protocol>();
        protocolList.add(DUMMY_PROTOCOL_1);
        protocolList.add(DUMMY_PROTOCOL_2);
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(protocolList);
            Logger.getLogger(this.getClass()).error("STM:  Here 1");
            tx.commit();
            Logger.getLogger(this.getClass()).error("STM:  Here 2");
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).error("STM:  Here 3");
            Logger.getLogger(this.getClass()).error("STM:  ", e);
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of protocol collection: " + e.getMessage());
        }
    }

    /**
     * Tests save, retrieve, update and remove operations on a <code>ProtocolApplication</code>.
     * Test encompasses save and delete of the associated <code>ParameterValue</code>s as well.
     */
    @Test
    public void testProtocolApplicationCrud() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_APPLICATION_1);
            ProtocolApplication retrievedProtocolApp =
                (ProtocolApplication) HibernateUtil.getCurrentSession().get(ProtocolApplication.class,
                                                                            DUMMY_PROTOCOL_APPLICATION_1.getId());
            if (DUMMY_PROTOCOL_APPLICATION_1.equals(retrievedProtocolApp)) {
                // The retrieved protocol app is the same as the saved protocol app. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved protocol app is different from saved protocol app.");
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol app: " + e.getMessage());
        }
    }

    /**
     * Tests saving a <code>ProtocolApplication</code> collection.
     */
    @Test
    public void testSaveProtocolAppCollection() {
        Transaction tx = null;
        List<ProtocolApplication> protocolAppList = new ArrayList<ProtocolApplication>();
        protocolAppList.add(DUMMY_PROTOCOL_APPLICATION_1);
        protocolAppList.add(DUMMY_PROTOCOL_APPLICATION_2);
        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(protocolAppList);
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save of protocol app collection: " + e.getMessage());
        }
        assertTrue(true);
    }

    /**
     * Tests searching for a <code>Protocol</code> by example.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testSearchProtocolByExample() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_1);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            Protocol exampleProtocol = new Protocol();
            exampleProtocol.setDescription(DUMMY_PROTOCOL_1.getDescription());
            Protocol retrievedProtocol = null;
            List<Protocol> matchingProtocols = DAO_OBJECT.queryEntityByExample(exampleProtocol);
            if ((matchingProtocols != null) && (matchingProtocols.size() >= 1)) {
                retrievedProtocol = matchingProtocols.get(0);
            }
            assertEquals(DUMMY_PROTOCOL_1.getDescription(), retrievedProtocol.getDescription());
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of protocol: " + e.getMessage());
        }
    }

    /**
     * Tests searching for a <code>Protocol</code> by name and type
     */
    @Test
    public void testSearchProtocolsByNameAndType() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_1);
            DAO_OBJECT.save(DUMMY_PROTOCOL_2);
            DAO_OBJECT.save(DUMMY_PROTOCOL_3);
            Protocol p4 = new Protocol("DummyProtocol4", DUMMY_TERM_2, DUMMY_TERM_SOURCE_1);
            DAO_OBJECT.save(p4);
            tx.commit();

            tx = HibernateUtil.beginTransaction();
            List<Protocol> protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_1, "dummy");
            assertEquals(1, protocols.size());
            assertEquals(DUMMY_PROTOCOL_1, protocols.get(0));

            protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_2, null);
            assertEquals(3, protocols.size());
            assertTrue(protocols.contains(DUMMY_PROTOCOL_2));
            assertTrue(protocols.contains(DUMMY_PROTOCOL_3));
            assertTrue(protocols.contains(p4));

            protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_2, "");
            assertEquals(3, protocols.size());
            assertTrue(protocols.contains(DUMMY_PROTOCOL_2));
            assertTrue(protocols.contains(DUMMY_PROTOCOL_3));
            assertTrue(protocols.contains(p4));

            protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_2, "dummy");
            assertEquals(3, protocols.size());
            assertTrue(protocols.contains(DUMMY_PROTOCOL_2));
            assertTrue(protocols.contains(DUMMY_PROTOCOL_3));
            assertTrue(protocols.contains(p4));

            protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_2, "dummyp");
            assertEquals(1, protocols.size());
            assertTrue(protocols.contains(p4));

            protocols = DAO_OBJECT.getProtocols(DUMMY_TERM_2, "dummyadfadsf");
            assertEquals(0, protocols.size());

            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of protocol: " + e.getMessage());
        }
    }

    /**
     * Tests searching for a <code>Protocol</code> by example, including associations
     * in the search.
     * Both dummy protocols 2 and 3 have the same text, but only protocol 3 has the matching type.
     */
    @Test
    public void testDeepSearchProtocolByExample() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_2);
            DAO_OBJECT.save(DUMMY_PROTOCOL_3);
            tx.commit();
            tx = HibernateUtil.beginTransaction();
            Protocol exampleProtocol = setupDeepSearchExample();
            Protocol retrievedProtocol = null;
            List<Protocol> matchingProtocols =
                DAO_OBJECT.queryEntityAndAssociationsByExample(exampleProtocol);
            assertEquals(2, matchingProtocols.size());
            retrievedProtocol = matchingProtocols.get(0);
            if (DUMMY_PROTOCOL_2.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved protocol is different from saved protocol.");
            }
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during search of protocol: " + e.getMessage());
        }
    }

    /**
     * Set up example objects for deep search of protocol.
     *
     * @return the example Protocol object with search attributes and associations filled in.
     */
    @SuppressWarnings("deprecation")
    private Protocol setupDeepSearchExample() {
        Protocol exampleProtocol = new Protocol();
        exampleProtocol.setDescription(DUMMY_PROTOCOL_2.getDescription());
        Term exampleTerm = new Term();
        exampleTerm.setValue(DUMMY_TERM_2.getValue());
        exampleProtocol.setType(exampleTerm);
        return exampleProtocol;
    }

    @Test
    public void testGetProtocolByUniquefields() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_1);
            Protocol retrievedProtocol = DAO_OBJECT.getProtocol(DUMMY_PROTOCOL_1.getName(), DUMMY_PROTOCOL_1.getSource());
            if (DUMMY_PROTOCOL_1.equals(retrievedProtocol)) {
                // The retrieved protocol is the same as the saved protocol. Test passed.
                assertTrue(true);
            } else {
                fail("Retrieved protocol is different from saved protocol.");
            }
            assertEquals(null,  DAO_OBJECT.getProtocol("   ", DUMMY_PROTOCOL_1.getSource()));
            assertEquals(null,  DAO_OBJECT.getProtocol(DUMMY_PROTOCOL_1.getName(), null));
            TermSource s = new TermSource();
            s.setName("foo");
            assertEquals(null,  DAO_OBJECT.getProtocol(DUMMY_PROTOCOL_1.getName(), s));
            tx.commit();
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol: " + e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetParameters() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.beginTransaction();
            DAO_OBJECT.save(DUMMY_PROTOCOL_1);
            Parameter param1 = DAO_OBJECT.getParameter(DUMMY_PARAMETER_1.getName(), DUMMY_PARAMETER_1.getProtocol());
            Parameter param2 = DAO_OBJECT.getParameter(DUMMY_PARAMETER_1.getName() +  "foo", DUMMY_PARAMETER_1.getProtocol());
            Parameter param3 = DAO_OBJECT.getParameter(DUMMY_PARAMETER_1.getName(), null);
            Parameter param4 = DAO_OBJECT.getParameter(DUMMY_PARAMETER_1.getName(), new Protocol());
            tx.commit();
            assertEquals(DUMMY_PARAMETER_1, param1);
            assertEquals(null, param2);
            assertEquals(null, param3);
            assertEquals(null, param4);
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of protocol: " + e.getMessage());
        }
    }
}
