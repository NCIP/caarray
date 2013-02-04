//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.plugins.illumina;


import java.util.Locale;

/**
 * Count all the non-header lines in Probes and Controls sections.
 * @author gax
 */
class LineCountHandler implements ContentHandler {
    private int count;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.MissingBreakInSwitch")
    public boolean startSection(String sectionName, int lineNumber) {
        if (sectionName == null) {
            return false;
        }
        try {
            switch (BgxDesignHandler.Section.valueOf(sectionName.toUpperCase(Locale.getDefault()))) {
                case PROBES :
                case CONTROLS:
                    return true;
                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parseFirstRow(String[] values, int lineNumber) {
        // don't count the header line.
    }

    /**
     * {@inheritDoc}
     */
    public void parseRow(String[] values, int lineNumber) {
        count++;
    }

    /**
     * {@inheritDoc}
     */
    public boolean endSection(String sectionName, int lineNumber) {
        return true;
    }

    /**
     * @return lines counted.
     */
    public int getCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public void startDocument() {
        count = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument() {
        // nothing to do.
    }

}
