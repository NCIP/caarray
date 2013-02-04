//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
