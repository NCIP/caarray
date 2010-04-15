package gov.nih.nci.caarray.platforms.illumina;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author gax
 */
public class IlluminaExpressionCsvDesignHandlerTest {
    private List<String> headerValues = Arrays.asList(
            "SEARCH_KEY", "TARGET", "PROBEID", "GID", "TRANSCRIPT", "ACCESSION", "SYMBOL", "TYPE", "START",
        "PROBE_SEQUENCE", "DEFINITION", "ONTOLOGY", "SYNONYM");

    @Test
    public void testValidateValues() {
        List<String> values = Arrays.asList(
            "xSEARCH_KEY", "xTARGET", "xPROBEID", "xGID", "xTRANSCRIPT", "xACCESSION", "xSYMBOL", "xTYPE", "xSTART",
        "xPROBE_SEQUENCE", "xDEFINITION", "xONTOLOGY", "xSYNONYM");
        FileValidationResult result_2 = new FileValidationResult(null);
        ExpressionCsvDesignHelper instance = new ExpressionCsvDesignHelper();
        instance.initHeaderIndex(headerValues);
        instance.validateValues(values, result_2, 2);
        assertEquals(0, result_2.getMessages().size());

        values = Arrays.asList(
            "xSEARCH_KEY", "xTARGET", "123", "xGID", "xTRANSCRIPT", "xACCESSION", "xSYMBOL", "xTYPE", "xSTART",
        "xPROBE_SEQUENCE", "xDEFINITION", "xONTOLOGY", "xSYNONYM");
        instance.validateValues(values, result_2, 2);
        assertEquals(0, result_2.getMessages().size());

        values = Arrays.asList(
            "xSEARCH_KEY", "xTARGET", "", "xGID", "xTRANSCRIPT", "xACCESSION", "xSYMBOL", "xTYPE", "xSTART",
        "xPROBE_SEQUENCE", "xDEFINITION", "xONTOLOGY", "xSYNONYM");
        instance.validateValues(values, result_2, 2);
        assertEquals(1, result_2.getMessages().size());
    }

    @Test
    public void testIsHeaderLine() {
        ExpressionCsvDesignHelper instance = new ExpressionCsvDesignHelper();
        boolean result = instance.isHeaderLine(headerValues);
        assertTrue(result);

        List<String> reversed = new ArrayList<String>(headerValues);
        Collections.reverse(reversed);
        result = instance.isHeaderLine(reversed);
        assertTrue(result);

        List<String> bad = new ArrayList<String>(headerValues);
        assertTrue(bad.remove("SEARCH_KEY"));
        result = instance.isHeaderLine(bad);
        assertTrue(result);

        bad.add("something bad but same count");
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        bad = new ArrayList<String>(headerValues);
        bad.add("something bad extra");
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        List<String> lowercase = new ArrayList<String>(headerValues);
        lowercase.remove("START");
        lowercase.add("Start");
        result = instance.isHeaderLine(lowercase);
        assertTrue(result);
    }
}