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
package gov.nih.nci.caarray.domain.data;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject_HibernateIntegrationTest;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.util.HibernateUtil;

import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

public class DataSet_HibernateIntegrationTest extends AbstractCaArrayObject_HibernateIntegrationTest {

    private static final int NUMBER_OF_DATA_ROWS = 100;
    private QuantitationType stringType;
    private QuantitationType floatType;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        stringType = new QuantitationType();
        stringType.setName("string");
        stringType.setTypeClass(String.class);
        floatType = new QuantitationType();
        floatType.setName("float");
        floatType.setTypeClass(Float.class);
    }

    @Test
    @Override
    @SuppressWarnings("PMD")
    public void testSave() {
        super.testSave();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setValues(AbstractCaArrayObject caArrayObject) {
        DataSet dataSet = (DataSet) caArrayObject;
        StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        stringColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        setValues(stringColumn.getValues());
        FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        floatColumn.initializeArray(NUMBER_OF_DATA_ROWS);
        setValues(floatColumn.getValues());

        DesignElementList del = new DesignElementList();
        del.setDesignElementTypeEnum(getNextValue(DesignElementType.values(), del.getDesignElementTypeEnum()));
        del.getDesignElements().add(new Feature());
        dataSet.setDesignElementList(del);
    }

    private void setValues(String[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = "test" + getUniqueIntValue();
        }
    }

    private void setValues(float[] values) {
        for (int i = 0; i < values.length; i++) {
            values[i] = getUniqueIntValue() + 0.1f;
        }
    }

    @Override
    protected void compareValues(AbstractCaArrayObject caArrayObject, AbstractCaArrayObject retrievedCaArrayObject) {
        DataSet original = (DataSet) caArrayObject;
        DataSet retrieved = (DataSet) retrievedCaArrayObject;
        assertEquals(original.getQuantitationTypes().get(0).getName(), retrieved.getQuantitationTypes().get(0).getName());
        StringColumn originalColumn1 = (StringColumn) original.getHybridizationDataList().get(0).getColumns().get(0);
        StringColumn retrievedColumn1 = (StringColumn) retrieved.getHybridizationDataList().get(0).getColumns().get(0);
        assertArrayEquals(originalColumn1.getValues(), retrievedColumn1.getValues());
        assertEquals(original.getQuantitationTypes().get(1).getName(), retrieved.getQuantitationTypes().get(1).getName());
        FloatColumn originalColumn2 = (FloatColumn) original.getHybridizationDataList().get(0).getColumns().get(1);
        FloatColumn retrievedColumn2 = (FloatColumn) retrieved.getHybridizationDataList().get(0).getColumns().get(1);
        assertFloatArrayEquals(originalColumn2.getValues(), retrievedColumn2.getValues());
    }

    private void assertFloatArrayEquals(float[] expected, float[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Override
    protected AbstractCaArrayObject createTestObject() {
        Sample sample = new Sample();
        sample.setName("Foo");
        Extract extract = new Extract();
        extract.setName("Foobar");
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);
        LabeledExtract le = new LabeledExtract();
        le.setName("Foofoo");
        extract.getLabeledExtracts().add(le);
        le.getExtracts().add(extract);
        Hybridization hybridization = new Hybridization();
        hybridization.setName("Test Hyb");
        le.getHybridizations().add(hybridization);
        hybridization.getLabeledExtracts().add(le);
        Transaction tx = HibernateUtil.beginTransaction();
        HibernateUtil.getCurrentSession().saveOrUpdate(sample);
        tx.commit();
        DataSet dataSet = new DataSet();
        dataSet.addHybridizationData(hybridization);
        dataSet.addQuantitationType(stringType);
        dataSet.addQuantitationType(floatType);
        DesignElementList del = new DesignElementList();
        del.setDesignElementTypeEnum(null);
        dataSet.setDesignElementList(del);
        return dataSet;
    }

    @Override
    protected void setNullableValuesToNull(AbstractCaArrayObject caArrayObject) {
        DataSet dataSet = (DataSet) caArrayObject;
        StringColumn stringColumn = (StringColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(0);
        FloatColumn floatColumn = (FloatColumn) dataSet.getHybridizationDataList().get(0).getColumns().get(1);
        stringColumn.setValues(null);
        floatColumn.setValues(null);
        dataSet.setDesignElementList(null);
    }

}