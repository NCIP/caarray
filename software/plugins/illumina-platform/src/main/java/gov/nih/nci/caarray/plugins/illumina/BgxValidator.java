//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Content handler that generates validation messages.
 * @author gax
 */
public class BgxValidator implements ContentHandler {

    private static final String KEY_NUMBER_OF_PROBES = "NUMBER OF PROBES";
    private static final String KEY_NUMBER_OF_CONTROLS = "NUMBER OF CONTROLS";

    private int rowWidth;
    private final FileValidationResult result;
    private final Set<BgxDesignHandler.Section> visited = new HashSet<BgxDesignHandler.Section>();
    private final HeadingSectionHandler headingHelper = new HeadingSectionHandler();
    private final ProbeLineValidator probesHelper = new ProbeLineValidator();
    private final ProbeLineValidator controlsHelper = new ProbeLineValidator();
    private ContentHandler currentHelper;
    private BgxDesignHandler.Section currentSection;


    /**
     * @param result Where the validation messages should go.
     */
    public BgxValidator(FileValidationResult result) {
        this.result = result;
    }


    /**
     * {@inheritDoc}
     */
    public boolean startSection(String sectionName, int lineNumber) {
        try {
            currentSection = BgxDesignHandler.Section.valueOf(sectionName.toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            result.addMessage(ValidationMessage.Type.WARNING, "Unexpected section " + sectionName, lineNumber, 0);
            return false;
        }

        if (!visited.add(currentSection)) {
            result.addMessage(ValidationMessage.Type.ERROR, "Too many " + sectionName + "sections", lineNumber, 0);
            return false;
        }
        switch (currentSection) {
            case HEADING: currentHelper = headingHelper; break;
            case PROBES: currentHelper = probesHelper; break;
            case CONTROLS: currentHelper = controlsHelper; break;
            default: return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void endDocument() {
        if (!visited.contains(BgxDesignHandler.Section.PROBES)) {
            result.addMessage(ValidationMessage.Type.ERROR, "Missing " + BgxDesignHandler.Section.PROBES
                    + " section");
        }
        validateCount(KEY_NUMBER_OF_PROBES, probesHelper.getCount());
        validateCount(KEY_NUMBER_OF_CONTROLS, controlsHelper.getCount());
    }

    private void validateCount(String key, int counted) {
        String value = headingHelper.getValues().get(key);
        if (value != null) {
            int expected = Integer.parseInt(value);
            if (expected != counted) {
                result.addMessage(ValidationMessage.Type.ERROR, "Heading '" + key + "' expected " + expected
                        + " probes, but counted " + counted);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startDocument() {
        // nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    public boolean endSection(String sectionName, int lineNumber) {
        currentHelper = null;
        currentSection = null;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void parseFirstRow(String[] values, int lineNumber) {
        currentHelper.parseFirstRow(values, lineNumber);
    }

    /**
     * {@inheritDoc}
     */
    public void parseRow(String[] values, int lineNumber) {
        currentHelper.parseRow(values, lineNumber);
    }

    /**
     * Validates rows in a Probes or Controls section.
     */
    private class ProbeLineValidator extends LineCountHandler {
        private int[] colIndex;

        /**
         * {@inheritDoc}
         */
        @Override
        public void parseFirstRow(String[] values, int lineNumber) {
            colIndex = new int[BgxDesignHandler.Header.values().length];
            Arrays.fill(colIndex, -1);
            for (int i = 0; i < values.length; i++) {
                String col = values[i].toUpperCase(Locale.getDefault());
                try {
                    BgxDesignHandler.Header h = BgxDesignHandler.Header.valueOf(col);
                    colIndex[h.ordinal()] = i;
                } catch (IllegalArgumentException e) {
                    result.addMessage(ValidationMessage.Type.WARNING, "Unexpected column name " + col,
                            lineNumber, i + 1);
                }
            }
            if (colIndex[BgxDesignHandler.Header.PROBE_ID.ordinal()] == -1) {
                result.addMessage(ValidationMessage.Type.ERROR, "Missing column PROBE_ID",
                        lineNumber, values.length + 1);
            }
            rowWidth = values.length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void parseRow(String[] values, int lineNumber) {
            super.parseRow(values, lineNumber);
            if (values.length != rowWidth) {
                ValidationMessage error = result.addMessage(ValidationMessage.Type.ERROR, "Expected " + rowWidth
                            + " values but row has " + values.length);
                error.setLine(lineNumber);
            }
            if (colIndex[BgxDesignHandler.Header.PROBE_ID.ordinal()] != -1) {
                String probId = getValue(BgxDesignHandler.Header.PROBE_ID, values);
                if (StringUtils.isBlank(probId)) {
                    result.addMessage(ValidationMessage.Type.ERROR, "Missing or blank PROBE_ID", lineNumber,
                            colIndex[BgxDesignHandler.Header.PROBE_ID.ordinal()] + 1);
                }
            }
        }

        private String getValue(BgxDesignHandler.Header column, String[] line) {
            int idx = colIndex[column.ordinal()];
            return idx == -1 ? null : line[idx];
        }
    }
}
