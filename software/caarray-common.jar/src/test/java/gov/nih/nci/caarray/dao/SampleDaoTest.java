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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.domain.sample.Characteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.util.HibernateUtil;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Transaction;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for the Sample DAO.
 *
 * @author Rashmi Srinivasa
 */
public class SampleDaoTest  extends AbstractDaoTest {
    private static final Log LOG = LogFactory.getLog(SampleDaoTest.class);

    private static final Sample DUMMY_SAMPLE_1 = new Sample();

    private static final TermSource DUMMY_SOURCE = new TermSource();
    private static final Category DUMMY_CATEGORY = new Category();
    private static final Term DUMMY_MATERIAL_TYPE = new Term();

    private static final Term DUMMY_TERM = new Term();
    private static final Term DUMMY_UNIT = new Term();
    private static final Characteristic DUMMY_CHARACTERISTIC = new Characteristic();

    // private static final gov.nih.nci.caarray.domain.sample.Source DUMMY_BIOSOURCE_1 = new
    //     gov.nih.nci.caarray.domain.sample.Source();
    // private static final gov.nih.nci.caarray.domain.sample.Source DUMMY_BIOSOURCE_2 = new
    //     gov.nih.nci.caarray.domain.sample.Source();
    // private static final Extract DUMMY_EXTRACT_1 = new Extract();
    // private static final Extract DUMMY_EXTRACT_2 = new Extract();

    private static final SampleDao DAO_OBJECT = CaArrayDaoFactory.INSTANCE.getSampleDao();

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        DUMMY_SAMPLE_1.setName("DummySample1");
        DUMMY_SAMPLE_1.setDescription("DummySample1Desc");

        DUMMY_SOURCE.setName("Dummy Source");
        DUMMY_CATEGORY.setName("Dummy Category");
        DUMMY_MATERIAL_TYPE.setValue("Dummy Material Type");
        DUMMY_MATERIAL_TYPE.setSource(DUMMY_SOURCE);
        DUMMY_MATERIAL_TYPE.setCategory(DUMMY_CATEGORY);
        DUMMY_SAMPLE_1.setMaterialType(DUMMY_MATERIAL_TYPE);

        DUMMY_CHARACTERISTIC.setValue("Dummy Characteristic");
        DUMMY_TERM.setValue("Dummy Term");
        DUMMY_TERM.setSource(DUMMY_SOURCE);
        DUMMY_TERM.setCategory(DUMMY_CATEGORY);
        DUMMY_CHARACTERISTIC.setTerm(DUMMY_TERM);
        DUMMY_UNIT.setValue("Dummy Unit");
        DUMMY_UNIT.setSource(DUMMY_SOURCE);
        DUMMY_UNIT.setCategory(DUMMY_CATEGORY);
        DUMMY_CHARACTERISTIC.setUnit(DUMMY_UNIT);
    }

    /**
     * Tests retrieving the <code>Sample</code> with the given id. Test encompasses save and delete of a
     * <code>Sample</code>.
     */
    @Test
    public void testGetSample() {
        Transaction tx = null;

        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.save(DUMMY_SAMPLE_1);
            Sample retrievedSample = DAO_OBJECT.getSample(DUMMY_SAMPLE_1.getId());
            tx.commit();
            if (DUMMY_SAMPLE_1.equals(retrievedSample)) {
                if (compareSamples(retrievedSample, DUMMY_SAMPLE_1)) {
                    // The retrieved sample is the same as the saved sample. Test passed.
                    assertTrue(true);
                }
            } else {
                fail("Retrieved sample is different from saved sample.");
            }
        } catch (DAOException e) {
            HibernateUtil.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of sample: " + e.getMessage());
        } finally {
            cleanUpSample();
        }
    }

    /**
     * Compare 2 samples to check if they are the same.
     *
     * @return true if the 2 samples are the same and false otherwise.
     */
    private boolean compareSamples(Sample retrievedSample, Sample dummySample) {
        if (!dummySample.getName().equals(retrievedSample.getName())) {
            return false;
        }
        Term retrievedMaterialType = retrievedSample.getMaterialType();
        if (!DUMMY_MATERIAL_TYPE.getValue().equals(retrievedMaterialType.getValue())) {
            return false;
        }
        Collection characteristics = retrievedSample.getCharacteristics();
        if (characteristics.isEmpty() || characteristics.size() != 1) {
            return false;
        }
        Iterator i = characteristics.iterator();
        Characteristic retrievedCharacteristic = (Characteristic) i.next();
        if (!DUMMY_CHARACTERISTIC.getValue().equals(retrievedCharacteristic.getValue())) {
            return false;
        }
        return true;
    }

    /**
     * Clean up after a test by removing the dummy sample.
     */
    private void cleanUpSample() {
        Transaction tx = null;
        try {
            tx = HibernateUtil.getCurrentSession().beginTransaction();
            DAO_OBJECT.remove(DUMMY_SAMPLE_1);
            DAO_OBJECT.remove(DUMMY_MATERIAL_TYPE);
            DAO_OBJECT.remove(DUMMY_CHARACTERISTIC);
            DAO_OBJECT.remove(DUMMY_TERM);
            DAO_OBJECT.remove(DUMMY_UNIT);
            DAO_OBJECT.remove(DUMMY_SOURCE);
            DAO_OBJECT.remove(DUMMY_CATEGORY);
            tx.commit();
        } catch (DAOException deleteException) {
            HibernateUtil.rollbackTransaction(tx);
            LOG.error("Error cleaning up dummy sample.", deleteException);
            fail("DAO exception during deletion of sample: " + deleteException.getMessage());
        }
    }
}
