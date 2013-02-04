//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.nih.nci.caarray.plugins.illumina.GenotypingCsvDesignHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author gax
 */
public class IlluminaGenotypingCsvDesignHandlerTest {
    private List<String> headerValues1 = Arrays.asList(
            "ILMNID", "NAME", "ILMNSTRAND", "SNP", "ADDRESSA_ID", "ALLELEA_PROBESEQ", "ADDRESSB_ID", "ALLELEB_PROBESEQ",
            "GENOMEBUILD", "CHR", "MAPINFO", "PLOIDY", "SPECIES", "SOURCE", "SOURCEVERSION", "SOURCESTRAND",
            "SOURCESEQ", "TOPGENOMICSEQ", "BEADSETID", "CNV_PROBE", "INTENSITY_ONLY", "EXP_CLUSTERS" );
    
    @Test
    public void testIsHeaderLine() {
        GenotypingCsvDesignHandler instance = new GenotypingCsvDesignHandler();
        boolean result = instance.isHeaderLine(headerValues1);
        assertTrue(result);
        
        List<String> reversed = new ArrayList<String>(headerValues1);
        Collections.reverse(reversed);
        result = instance.isHeaderLine(reversed);
        assertTrue(result);

        List<String> bad = new ArrayList<String>(headerValues1);
        bad.remove(0);
        bad.add("something bad but same count");
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        bad = new ArrayList<String>(headerValues1);
        bad.add("something bad extra");
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        bad = new ArrayList<String>(headerValues1);
        bad.add("something bad extra");
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        bad = new ArrayList<String>(headerValues1);
        while (bad.size() >= instance.getRequiredColumns().size()) {
            bad.remove(0);
        }
        result = instance.isHeaderLine(bad);
        assertFalse(result);

        List<String> subset = new ArrayList<String>(headerValues1);
        assertTrue(subset.remove("SOURCESEQ"));
        result = instance.isHeaderLine(subset);
        assertTrue(result);

        List<String> lowercase = new ArrayList<String>(headerValues1);
        lowercase.remove("BEADSETID");
        lowercase.add("BeadsetID");
        result = instance.isHeaderLine(lowercase);
        assertTrue(result);


    }

    @Test
    public void testIsHeaderLine_Invalid() {
        GenotypingCsvDesignHandler instance = new GenotypingCsvDesignHandler();
        List<String> looksLikeHeader = new ArrayList<String>(headerValues1);
        String required = instance.getRequiredColumns().iterator().next().name();
        assertTrue(looksLikeHeader.remove(required));
        boolean result = instance.isHeaderLine(looksLikeHeader);
        assertTrue(result);
    }
}
