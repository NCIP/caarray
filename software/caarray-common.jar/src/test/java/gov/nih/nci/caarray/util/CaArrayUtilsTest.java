/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray2
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray2 Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray2 Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray2 Software; (ii) distribute and
 * have distributed to and by third parties the caArray2 Software and any
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
package gov.nih.nci.caarray.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.external.v1_0.data.DataType;
import gov.nih.nci.caarray.external.v1_0.data.FloatColumn;
import gov.nih.nci.caarray.external.v1_0.data.QuantitationType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/**
 * @author dkokotov
 */
public class CaArrayUtilsTest extends AbstractCaarrayTest {
    private final static String EMPTY_STRING = "";
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private final static float[] TEST_FLOAT_ARRAY = { 34.055073f, 22.046793f, 7.7991714f, 16.462706f, 48.54733f, 56.01541f,
            25.884272f, 58.606827f, 50.735817f, 196.47493f, 55.92979f, 11.630499f, 156.9128f, 36.543858f, 398.65283f, 4.8906174f,
            1916.2703f, 11.698614f, 83.24945f, 3.637869f, 44.192177f, 29.173893f, 382.02103f, 114.56265f, 20.872303f, 348.15024f,
            354.7858f, 472.20734f, 1626.6553f, 107.29906f, 170.63292f, 446.0172f, 196.95464f, 147.25851f, 175.85199f, 52.355545f,
            25.44639f, 140.29434f, 513.8328f, 13.760226f, 109.14554f, 112.09177f, 116.92022f, 9.315108f, 159.92604f, 80.43913f,
            120.14907f, 125.664536f, 338.0443f, 37.670486f, 167.25427f, 4.106606f, 47.98621f, 7.989276f, 2.7991848f, 990.2205f,
            1208.8339f, 985.9316f, 6212.3413f, 2470.5154f, 262.81418f, 57.655598f, 158.40436f, 99.25447f, 57.50396f, 100.955055f,
            215.37514f, 174.15752f, 145.38664f, 136.75465f, 135.32437f, 28.423773f, 54.369812f, 215.80902f, 14.054392f,
            14.38574f, 29.748743f, 8.718602f, 93.42058f, 10.122841f, 5.231818f, 75.988464f, 8.840353f, 48.227272f, 27.665382f,
            115.13449f, 126.58664f, 22.547718f, 24.904177f, 37.642624f, 16.594177f, 3.3969517f, 50.951088f, 24.484886f,
            126.80343f, 11.428655f, 55.496178f, 122.15511f, 13.305698f, 52.544346f, 206.43619f, 239.12396f, 40.14191f, 25.01289f,
            132.97606f, 14.076855f, 955.4074f, 685.2097f, 220.73239f, 25.517431f, 645.99805f, 18.088753f, 79.79526f, 17.385845f,
            8.942123f, 13.736944f, 7.4648523f, 150.89023f, 5.0989885f, 46.32765f, 27.681639f, 72.864494f, 5.3998933f, 18.4344f,
            23.167255f, 3.7396388f, 10.864964f, 41.643513f, 4.919775f, 21.779146f, 54.49041f, 9.730406f, 69.968315f, 14.632505f,
            11.634936f, 243.94531f, 7450.3438f, 3664.2249f, 730.31287f, 436.90335f, 558.69727f, 193.97205f, 650.8422f, 149.9585f,
            164.31314f, 34.81567f, 53.093784f, 240.37248f, 108.573784f, 143.66547f, 184.08853f, 93.956924f, 9.288328f,
            100.26961f, 56.01392f, 8.374274f, 147.15007f, 1647.1127f, 140.54498f, 17.029993f, 12.733529f, 35.62738f, 23.817852f,
            230.37183f, 220.33499f, 10.445749f, 108.4807f, 50.13516f, 128.38242f, 44.17535f, 219.40509f, 17.842049f, 72.04096f,
            21.90069f, 62.495003f, 34.67865f, 114.81621f, 47.76873f, 298.52277f, 582.3627f, 210.7369f, 43.696785f, 10.6682205f,
            8.002985f, 7.358196f, 79.856544f, 13.217081f, 24.206354f, 81.743416f, 12.28308f, 9.510899f, 22.653309f, 68.24097f,
            10.8911915f, 15.005591f, 25.241083f, 36.70708f, 45.189407f, 100.51679f, 385.3216f, 15.592766f, 27.891926f, 68.55752f,
            12.009363f, 10.068444f, 13.568725f, 42093.355f, 31497.465f, 2628.1946f, 1386.2671f, 1036.4277f, 987.78296f,
            2798.3877f, 190.24518f, 1003.80817f, 327.3878f, 1979.4509f, 2101.67f, 1191.541f, 1098.3336f, 928.95105f, 480.01855f,
            760.0775f, 5.3733845f, 32.114517f, 50.40364f, 52.744473f, 50.26978f, 30.18479f, 25.023588f, 23.140326f, 82.12754f,
            1769.6235f, 9.194587f, 173.44164f, 13.822241f, 15.404175f, 31.117777f, 23.016163f, 32.850315f, 8.179581f, 10.261767f,
            337.794f, 892.9338f, 293.59598f, 194.24747f, 70.23776f, 694.9997f, 74.915085f, 100.8536f, 7.7295337f, 7.3153725f,
            44.547913f, 20.350853f, 190.65515f, 84.20199f, 2745.7395f, 2714.1992f, 1200.3197f, 617.3598f, 622.5596f, 1728.1221f,
            1874.128f, 2467.8186f, 12238.786f, 2814.0754f, 40516.492f, 28618.748f, 13.233339f, 38.503277f, 45.710823f,
            427.88394f, 710.22815f, 1308.4233f, 19061.89f, 8208.6455f, 16455.84f, 63009.21f, 11106.189f, 9409.68f, 8061.241f,
            390.89294f, 89.64542f, 46.25952f, 177.19882f, 492.3477f, 285.86438f, 280.44434f, 242.50616f, 85.13499f, 127.11953f,
            26.316498f, 9.658841f, 21.240406f, 111.77535f, 1432.427f, 1916.964f, 127.66697f, 100.43046f, 167.83728f, 27.65848f,
            40.999023f, 11.629804f, 28.748062f, 31.550705f, 7.5030446f, 21.049856f, 49.63352f, 191.11856f, 18.126328f,
            25.788998f, 13.929088f, 9.149916f, 79.73079f, 140.76971f, 34.367577f, 10.4747095f, 11.950751f, 24.544659f,
            22.693546f, 32.499187f, 9.117973f, 32.94043f, 75.050865f, 17.533886f, 84.10114f, 11.43998f, 25.99866f, 10.128586f,
            12.606695f, 199.39462f, 67.65492f, 28.048536f, 484.8352f, 3644.754f, 1826.7297f, 3843.327f, 666.3698f, 1275.2133f,
            1857.1359f, 82.62829f, 46.82943f, 44.412727f, 9.08907f, 235.829f };

    @Test
    public void testJoinValues() {
        assertEquals(EMPTY_STRING, CaArrayUtils.join((boolean[]) null, " "));
        assertEquals("true", CaArrayUtils.join(new boolean[] { true }, ","));
        assertEquals("true;false;true", CaArrayUtils.join(new boolean[] { true, false, true }, ";"));

        assertEquals(EMPTY_STRING, CaArrayUtils.join((short[]) null, " "));
        assertEquals("0", CaArrayUtils.join(new short[] { 0 }, ","));
        assertEquals("3;1;2", CaArrayUtils.join(new short[] { 3, 1, 2 }, ";"));

        assertEquals(EMPTY_STRING, CaArrayUtils.join((long[]) null, " "));
        assertEquals("5", CaArrayUtils.join(new long[] { 5L }, ","));
        assertEquals("15/19/13", CaArrayUtils.join(new long[] { 15L, 19L, 13L }, "/"));

        assertEquals(EMPTY_STRING, CaArrayUtils.join((int[]) null, " "));
        assertEquals("1001", CaArrayUtils.join(new int[] { 1001 }, ","));
        assertEquals("-1 0 -2", CaArrayUtils.join(new int[] { -1, 0, -2 }, " "));

        assertEquals(EMPTY_STRING, CaArrayUtils.join((float[]) null, " "));
        assertEquals("1.65", CaArrayUtils.join(new float[] { 1.65f }, ","));
        assertEquals("0.0 0.33 -3.76", CaArrayUtils.join(new float[] { 0f, 0.33f, -3.76f }, " "));

        assertEquals(EMPTY_STRING, CaArrayUtils.join((double[]) null, " "));
        assertEquals("0.3333333333333333", CaArrayUtils.join(new double[] { 1.0 / 3.0 }, ","));
        assertEquals("0.0;-0.7142857142857143;0.8461538461538461", CaArrayUtils.join(new double[] { 0, -5.0 / 7.0,
                11.0 / 13.0 }, ";"));

        assertEquals(EMPTY_STRING, CaArrayUtils.joinAsCsv(null));
        assertEquals("Iam\\,Dan" + LINE_SEPARATOR, CaArrayUtils.joinAsCsv(new String[] { "Iam,Dan" + LINE_SEPARATOR }));
        assertEquals("I,m\\,e,mi ne" + LINE_SEPARATOR, CaArrayUtils.joinAsCsv(new String[] { "I", "m,e", "mi ne" + LINE_SEPARATOR}));
    }

    @Test
    public void testSplitValues() {
        assertTrue(Arrays.equals(ArrayUtils.EMPTY_BOOLEAN_ARRAY, CaArrayUtils.splitIntoBooleans(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new boolean[] { true }, CaArrayUtils.splitIntoBooleans("true", ",")));
        assertTrue(Arrays.equals(new boolean[] { true, false, true }, CaArrayUtils.splitIntoBooleans("true;false;true", ";")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_SHORT_ARRAY, CaArrayUtils.splitIntoShorts(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new short[] { 0 }, CaArrayUtils.splitIntoShorts("0", ",")));
        assertTrue(Arrays.equals(new short[] { 3, 1, 2 }, CaArrayUtils.splitIntoShorts("3;1;2", ";")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_LONG_ARRAY, CaArrayUtils.splitIntoLongs(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new long[] { 5L }, CaArrayUtils.splitIntoLongs("5", ",")));
        assertTrue(Arrays.equals(new long[] { 15L, 19L, 13L }, CaArrayUtils.splitIntoLongs("15/19/13", "/")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_INT_ARRAY, CaArrayUtils.splitIntoInts(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new int[] { 1001 }, CaArrayUtils.splitIntoInts("1001", ",")));
        assertTrue(Arrays.equals(new int[] { -1, 0, -2 }, CaArrayUtils.splitIntoInts("-1 0 -2", " ")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_FLOAT_ARRAY, CaArrayUtils.splitIntoFloats(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new float[] { 1.65f }, CaArrayUtils.splitIntoFloats("1.65", ",")));
        assertTrue(Arrays.equals(new float[] { 0f, 0.33f, -3.76f }, CaArrayUtils.splitIntoFloats("0 0.33 -3.76", " ")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_DOUBLE_ARRAY, CaArrayUtils.splitIntoDoubles(EMPTY_STRING, " ")));
        assertTrue(Arrays.equals(new double[] { 1.0 / 3.0 }, CaArrayUtils.splitIntoDoubles("0.3333333333333333", ",")));
        assertTrue(Arrays.equals(new double[] { 0, -5.0 / 7.0, 11.0 / 13.0 }, CaArrayUtils.splitIntoDoubles(
                "0.0;-0.7142857142857143;0.8461538461538461", ";")));

        assertTrue(Arrays.equals(ArrayUtils.EMPTY_STRING_ARRAY, CaArrayUtils.splitFromCsv(EMPTY_STRING)));
        assertTrue(Arrays.equals(new String[] { "Iam,Dan" }, CaArrayUtils.splitFromCsv("Iam\\,Dan")));
        assertTrue(Arrays.equals(new String[] { "I", "m,e", "mi ne"}, CaArrayUtils.splitFromCsv("I,m\\,e,mi ne")));
    }
    
    @Test
    public void testSerializeDeserialize() {
        byte[] serialized = CaArrayUtils.serialize(TEST_FLOAT_ARRAY);
        System.out.println("Wrote " + serialized.length + " bytes out");
        float[] deserialized = (float[]) CaArrayUtils.deserialize(serialized);
        assertEquals(TEST_FLOAT_ARRAY.length, deserialized.length);
        for (int i = 0; i < TEST_FLOAT_ARRAY.length; i++) {
            assertEquals(TEST_FLOAT_ARRAY[i], deserialized[i], 0);            
        }
    }
    
    @Test
    public void testSerializeDeserialize2() throws IOException, ClassNotFoundException {
        QuantitationType qt = new QuantitationType();
        qt.setDataType(DataType.FLOAT);
        qt.setName("Foo");
        FloatColumn fc = new FloatColumn();
        fc.setQuantitationType(qt);
        fc.setValues(TEST_FLOAT_ARRAY);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(fc);
        oos.flush();        
        byte[] serialized = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
        ObjectInputStream ois = new ObjectInputStream(bais);
        FloatColumn in = (FloatColumn) ois.readObject();
        assertEquals(qt, in.getQuantitationType());        
        float[] deserialized = in.getValues();
        assertEquals(TEST_FLOAT_ARRAY.length, deserialized.length);
        for (int i = 0; i < TEST_FLOAT_ARRAY.length; i++) {
            assertEquals(TEST_FLOAT_ARRAY[i], deserialized[i], 0);            
        }
    }

}
