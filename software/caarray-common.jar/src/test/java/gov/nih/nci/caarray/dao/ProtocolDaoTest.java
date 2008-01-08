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
    private static Parameter DUMMY_PARAMETER_1 = new Parameter(DUMMY_PROTOCOL_1);
    private static Parameter DUMMY_PARAMETER_2 = new Parameter(DUMMY_PROTOCOL_1);
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
        DUMMY_PARAMETER_1 = new Parameter(DUMMY_PROTOCOL_1);
        DUMMY_PARAMETER_2 = new Parameter(DUMMY_PROTOCOL_1);
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
    @SuppressWarnings("unchecked")
    private static void initializeProtocols() {
        DUMMY_TERM_SOURCE.setName("Dummy MGED Ontology");
        DUMMY_TERM_SOURCE.setUrl("test url");
        DUMMY_CATEGORY.setTermSource(DUMMY_TERM_SOURCE);
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
    @SuppressWarnings("unchecked")
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
     * Tests searching for a <code>Protocol</code> by example, including associations
     * in the search.
     * Both dummy protocols 2 and 3 have the same text, but only protocol 3 has the matching type.
     */
    @Test
    @SuppressWarnings("deprecation")
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
}
