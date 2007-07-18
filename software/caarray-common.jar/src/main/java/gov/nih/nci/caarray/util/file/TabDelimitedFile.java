/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.util.file;

import gov.nih.nci.caarray.magetab.MageTabTextFileLoaderException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * @author John Pike
 *
 */
public class TabDelimitedFile extends AbstractFileUtil {
    private List<String> currentLineContents;
    private String currentLine;

    /**
     *
     */
    public TabDelimitedFile() {
        setDelimiter('\t');
        setDelimiter("\t".charAt(0));
        setSeparator('"');
    }


    /**
     * @param line the string to be parsed
     * @return List of parsed strings
     *
     */
    public List<String> parseLine(String line) {
        if (line == null) {
            return null;
        }
        List<String> values = new ArrayList<String>();
        StringBuffer stringBuffer = new StringBuffer();
        for (int currentIndex = 0; currentIndex < line.length(); currentIndex++) {
            char currentChar = line.charAt(currentIndex);
            if (currentChar == getDelimiter()) {
                values.add(stringBuffer.toString());
                stringBuffer.setLength(0);
            } else if (currentChar == getSeparator()) {
                continue;
            } else {
                stringBuffer.append(currentChar);
            }
        }
        values.add(stringBuffer.toString());
        return values;
    }


    /**
     * @param mageTabFile file
     * @param entityMap map
     * @throws MageTabTextFileLoaderException an exception
     */
    public void handleFileData(File mageTabFile, MultiValueMap entityMap) throws MageTabTextFileLoaderException {
        try {
            LineIterator lineIterator = FileUtils.lineIterator(mageTabFile, "UTF-8");
            while ((currentLineContents = readLine(lineIterator)) != null) {
                Iterator iter = currentLineContents.iterator();
                String key = null;
                if (iter.hasNext()) {
                    key = (String) iter.next();
                    while (iter.hasNext()) {
                        String value = (String) iter.next();
                        entityMap.put(key.toUpperCase(Locale.ENGLISH), value);
                    }
                }
            }
        } catch (IOException e) {
            throw new MageTabTextFileLoaderException(
                "Couldn't load array design from TXT file " + mageTabFile.getAbsolutePath(), e);
        }

    }



    /**
     * @param lineIterator LineIterator
     * @return a list of String
     */
    public List<String> readLine(LineIterator lineIterator) {
        if (lineIterator.hasNext()) {
            currentLine = lineIterator.nextLine();
            currentLineContents = parseLine(currentLine);
            return currentLineContents;
        } else {
            currentLine = null;
            currentLineContents = null;
            return null;
        }
    }

}
