//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Collects name/value pairs in the heading section.
 *
 * @author gax
 */
class HeadingSectionHandler implements ContentHandler {
    private final Map<String, String> values = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public void startDocument() {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    public boolean startSection(String sectionName, int lineNumber) {
        return BgxDesignHandler.Section.HEADING.name().equalsIgnoreCase(sectionName);
    }

    /**
     * {@inheritDoc}
     */
    public void parseFirstRow(String[] line, int lineNumber) {
        parseRow(line, lineNumber);
    }

    /**
     * {@inheritDoc}
     */
    public void parseRow(String[] line, int lineNumber) {
        String key = line[0];
        String value = null;
        if (line.length > 1) {
            value = line[1];
        }
        values.put(key.toUpperCase(Locale.getDefault()), value);
    }

    /**
     * {@inheritDoc}
     */
    public boolean endSection(String sectionName, int lineNumber) {
        return !BgxDesignHandler.Section.HEADING.name().equalsIgnoreCase(sectionName);
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument() {
        //
    }

    /**
     * @return the collected name/value pairs.
     */
    public Map<String, String> getValues() {
        return values;
    }
    
}
