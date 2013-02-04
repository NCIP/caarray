//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.geosoft;

import gov.nih.nci.caarray.domain.project.Experiment;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gax
 */
public class GeoSoftFileWriterUtilTest {
    private GeoSoftExporterBeanTest helper;
    

    @Before
    public void setUp() throws Exception {
        helper = new GeoSoftExporterBeanTest();
        helper.baseIntegrationSetUp();
        helper.setUp();
    }

    @Test
    public void testWriteSoftFile() throws Exception {
        Experiment experiment = helper.makeGoodExperiment();
        String permaLinkUrl = "http://example.com/my-experiment";
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        GeoSoftFileWriterUtil.writeSoftFile(experiment, permaLinkUrl, out);
        out.close();
        @SuppressWarnings("unchecked")
        List<String> lines = IOUtils.readLines(new StringReader(sw.toString()));

        final String[] expected =  {
            "^SAMPLE=test-hyb",
            "!Sample_title=test-hyb",
            "!Sample_description=test-sample1, test-sample2",
            "!Sample_supplementary_file=raw_file.data",
            "!Sample_table=derived_file.data",
            "!Sample_source_name=test-source",
            "!Sample_organism=test Organizm 1",
            "!Sample_organism=test Organizm 2",
            "!Sample_treatment_protocol=\"some treatment:treatment desc\"; \"another treatment:another treatment desc\"",
            "!Sample_growth_protocol=\"some growth:growth desc\"",
            "!Sample_extract_protocol=\"some extract:extract desc\"",
            "!Sample_label_protocol=\"some label:labeling desc\"",
            "!Sample_hyb_protocol=\"some hybridization:hybridization desc\"",
            "!Sample_scan_protocol=\"some scan:scan desc\"",
            "!Sample_data_processing=\"data processing:data proc desc\"",
            "!Sample_label=label",
            "!Sample_biomaterial_provider=Affymetrix",
            "!Sample_platform_id=test-ga",
            "!Sample_characteristics=test-factor:test-value growth",
            "!Sample_characteristics=OrganismPart:some tissue site",
            "!Sample_characteristics=DiseaseState:some disease state",
            "!Sample_characteristics=ExternalId:test external id",
            "!Sample_characteristics=test-cat:test-val growth",
            "!Sample_characteristics=CellType:some cell type",
            "!Sample_molecule=other",
            "^SERIES=test-exp-id",
            "!Series_title=test-title",
            "!Series_summary=test-title",
            "!Series_overall_design=\"test-design-type1 (MO)\"; \"test-design-type2 (MO)\"",
            "!Series_pubmed_id=test-pub",
            "!Series_contributor=fff,mmm,lll",
            "!Series_web_link=http://example.com/my-experiment",
            "!Series_sample_id=test-hyb"
        };

        for (int i = 0; i < expected.length; i++) {
            String result = lines.get(i);
            assertEquals("line " + (i+1), expected[i], result);
        }

    }

}
