/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray2-trunk
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray2-trunk Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray2-trunk Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray2-trunk Software; (ii) distribute and 
 * have distributed to and by third parties the caarray2-trunk Software and any 
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
package gov.nih.nci.caarray.plugins.agilent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import gov.nih.nci.caarray.plugins.agilent.AgilentTextParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * @author jscott
 *
 */
public class AgilentTextParserTest {

    @Test
    public void emptyInputTest() throws Exception{
        File file = createFile(new String[] {});
        AgilentTextParser parser = new AgilentTextParser(file);
        
        assertFalse(parser.hasNext());
        FileUtils.deleteQuietly(file);
    }

    @Test
    public void simpleSingleLineSectionTest() throws Exception {
        File file = createFile(
                new String[] { "TYPE", "text" },
                new String[] { "SECTION_1", "column1" },
                new String[] { "DATA", "test string1" }
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        
        assertTrue(parser.hasNext());
        parser.next();
        
        assertEquals("test string1", parser.getStringValue("column1"));
        assertFalse(parser.hasNext());
        FileUtils.deleteQuietly(file);
    }

    @Test
    public void simpleTwoLineSectionTest() throws Exception {
        File file = createFile(
                new String[] { "TYPE", "text" },
                new String[] { "SECTION_1", "column1" },
                new String[] { "DATA", "test string1" },
                new String[] { "DATA", "test string2" }
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        
        assertTrue(parser.hasNext());
        parser.next();        
        assertEquals("test string1", parser.getStringValue("column1"));
               
        assertTrue(parser.hasNext());
        parser.next();        
        assertEquals("test string2", parser.getStringValue("column1"));
       
        assertFalse(parser.hasNext());
        FileUtils.deleteQuietly(file);
    }
    
    @Test
    public void dataTypesTest() throws Exception {
        final double delta = 0.0001;
        File file = createFile(
                new String[] { "TYPE", "text", "integer", "float", "boolean" },
                new String[] { "SECTION_1", "column1", "column2", "column3", "column4"},
                new String[] { "DATA", "test string1", "1", "123.456", "0"},
                new String[] { "DATA", "test string2", "2", "654.321", "1"}
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        
        assertTrue(parser.hasNext());
        parser.next();        
        assertEquals("test string1", parser.getStringValue("column1"));
        assertEquals(1, parser.getIntValue("column2"));
        assertEquals(123.456, parser.getFloatValue("column3"), delta);
        assertFalse(parser.getBooleanValue("column4"));
               
        assertTrue(parser.hasNext());
        parser.next();        
        assertEquals("test string2", parser.getStringValue("column1"));
        assertEquals(2, parser.getIntValue("column2"));
        assertEquals(654.321, parser.getFloatValue("column3"), delta);
        assertTrue(parser.getBooleanValue("column4"));

        assertFalse(parser.hasNext());
        FileUtils.deleteQuietly(file);
    }

    @Test
    public void twoSectionsTest() throws Exception {
        File file = createFile(
                new String[] { "TYPE", "text", "integer", "float" },
                new String[] { "SECTION_1", "column1", "column2", "column3"},
                new String[] { "DATA", "test string1", "1", "123.456"},
                new String[] { "DATA", "test string2", "2", "654.321"},
                new String[] { "*", },
                new String[] { "TYPE", "text", "integer", "integer", "integer" },
                new String[] { "SECTION_1", "first column", "second column", "third column", "fourth column"},
                new String[] { "DATA", "test string3", "3", "4", "5"}
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        
        parser.next();        
        parser.next();        
               
        assertTrue(parser.hasNext());
        parser.next();        
        assertEquals("test string3", parser.getStringValue("first column"));
        assertEquals(3, parser.getIntValue("second column"));
        assertEquals(4, parser.getIntValue("third column"));
        assertEquals(5, parser.getIntValue("fourth column"));
        
       assertFalse(parser.hasNext());
       FileUtils.deleteQuietly(file);
    }

    @Test
    public void sectionNameTest() throws Exception {
        File file = createFile(
                new String[] { "TYPE", "text" },
                new String[] { "SECTION_1", "column1" },
                new String[] { "DATA", "test string1" },
                new String[] { "DATA", "test string2" },
                new String[] { "*", },
                new String[] { "TYPE", "text" },
                new String[] { "SECTION_2", "column1" },
                new String[] { "DATA", "test string3" },
                new String[] { "DATA", "test string4" }
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        
        parser.next();        
        assertEquals("SECTION_1", parser.getSectionName());
        
        parser.next();        
        assertEquals("SECTION_1", parser.getSectionName());
        
        parser.next();        
        assertEquals("SECTION_2", parser.getSectionName());
        
        parser.next();        
        assertEquals("SECTION_2", parser.getSectionName());
        
       assertFalse(parser.hasNext());
       FileUtils.deleteQuietly(file);
    }

    @Test
    public void columnNamesTest() throws Exception {
        File file = createFile(
                new String[] { "TYPE", "integer", "integer", "integer" },
                new String[] { "SECTION_1", "q", "w", "r" },
                new String[] { "DATA", "1", "2", "3" },
                new String[] { "*", },
                new String[] { "TYPE", "integer", "integer", "integer", "integer" },
                new String[] { "SECTION_1", "c", "a", "b", "d" },
                new String[] { "DATA", "1", "2", "3" , "4" }
                );
        
        AgilentTextParser parser = new AgilentTextParser(file);
        Collection<String> columnNames;
        
        parser.next();
        columnNames = parser.getColumnNames();
        assertEquals(3, columnNames.size());
        assertThat(columnNames, hasItems("q", "r", "w"));

        parser.next();        
        columnNames = parser.getColumnNames();
        assertEquals(4, columnNames.size());
        assertThat(columnNames, hasItems("a", "b", "c", "d"));

        assertEquals(7, parser.getCurrentLineNumber());
        assertEquals(3, parser.getColumnIndex("b"));
        assertEquals(-1, parser.getColumnIndex("foo"));
        
        assertFalse(parser.hasNext());
        FileUtils.deleteQuietly(file);
    }
    
    private File createFile(String[]... lines) throws IOException {
        File outFile = File.createTempFile("outfile-", ".txt");
        StringBuffer stringCatcher = new StringBuffer();
        for (String[] line : lines) {
            for (int i = 0; i < line.length; i++) {
                stringCatcher.append(line[i]);
                if (i < (line.length - 1)) {
                    stringCatcher.append("\t");
                } else {
                    stringCatcher.append("\n");
                }
            }
        }
        FileUtils.writeStringToFile(outFile, stringCatcher.toString());
        return outFile;
    }
}
