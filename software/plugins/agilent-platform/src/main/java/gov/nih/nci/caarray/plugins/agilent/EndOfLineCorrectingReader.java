//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.UnhandledException;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REFilterReader;

import com.fiveamsolutions.nci.commons.util.io.AbstractResettableReader;

/**
 * A specialty reader which transforms lines ending in carriage return/carriage return/line feed
 * into lines ending in the more reasonable carriage return/line feed.
 * 
 * Added to specifically deal with the malformed files referred to in issue
 * GF-29090, "TCGA Agilent data fail validation with extra carriage return for each row"
 * 
 * @author jscott
 *
 */
public class EndOfLineCorrectingReader extends AbstractResettableReader {
    
    private static final String CRLF = "\015\012"; // one carriage return followed by a line feed
    private static final String CRCRLF = "\015\015\012"; // two carriage returns followed by a line feed
    
    private final File file;
    private final RE regex = createRegularExpression();
    
    /**
     * Constructs a new EndOfLineCorrectingReader.
     * @param file the file to read
     * @throws IOException if there is a problem opening the file.
     */
    public EndOfLineCorrectingReader(File file) throws IOException {
        this.file = file;
        initialize();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void createInnerReader() throws IOException {
        REFilterReader reader = new REFilterReader(new FileReader(file), regex, CRLF);        
        setInnerReader(reader);
    }

    /**
     * @return a regular expression which matches carriage return/carriage return/line feed
     */
    private RE createRegularExpression() {
        try {
            return new RE(CRCRLF, RE.REG_MULTILINE);
        } catch (REException e) {
            throw new UnhandledException(e);
        }
    }
 }
