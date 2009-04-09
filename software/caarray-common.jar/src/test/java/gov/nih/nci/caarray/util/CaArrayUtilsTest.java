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

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

/**
 * @author dkokotov
 */
public class CaArrayUtilsTest extends AbstractCaarrayTest {
    private static String EMPTY_STRING = "";

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
        assertEquals("Iam\\,Dan\n", CaArrayUtils.joinAsCsv(new String[] { "Iam,Dan" }));
        assertEquals("I,m\\,e,mi ne\n", CaArrayUtils.joinAsCsv(new String[] { "I", "m,e", "mi ne"}));
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
}
