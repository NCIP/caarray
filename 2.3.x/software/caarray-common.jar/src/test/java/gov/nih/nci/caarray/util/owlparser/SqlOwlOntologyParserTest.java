//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.owlparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nih.nci.caarray.AbstractCaarrayTest;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Tests the OWL Ontology Parser
 * @author dkokotov
 */
public class SqlOwlOntologyParserTest extends AbstractCaarrayTest {
    @Test
    public void testParser() {
        File out = null;
        try {
            out = File.createTempFile("owltst", ".tmp");
            SqlOntologyOwlParser parser = new SqlOntologyOwlParser(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                    ParseMgedOntology.MGED_URL, out);
            parser.parse(SqlOntologyOwlParser.class.getResourceAsStream(ParseMgedOntology.MGED_OWL_PATH));

            TermSource ts = parser.getTermSource();
            assertNotNull(ts);
            assertEquals(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(), ts.getName());
            assertEquals(ParseMgedOntology.MGED_URL, ts.getUrl());
            assertEquals("1.3.1.1", ts.getVersion()); // NOPMD - PMD thinks the version is a hard-coded IP address

            Map<String, Category> categoryCache = parser.getCategoryCache();
            Category c = categoryCache.get("FactorValue");
            assertNotNull(c);
            assertEquals("FactorValue", c.getName());
            assertEquals("MO_203", c.getAccession());
            assertEquals(ParseMgedOntology.MGED_URL + "#FactorValue", c.getUrl());
            assertEquals(1, c.getParents().size());
            assertEquals("ExperimentPackage", c.getParents().iterator().next().getName());
            assertEquals(233, categoryCache.size());

            Set<Term> terms = parser.getTermCache();
            Term orgPart = new Term();
            orgPart.setValue("organism_part");
            orgPart.setSource(ts);
            assertTrue(terms.contains(orgPart));
            assertEquals(667, terms.size());

        } catch (IOException e) {
            e.printStackTrace();
            fail("Error parsing OWL file: " + e);
        } catch (ParseException e) {
            e.printStackTrace();
            fail("Error parsing OWL file: " + e);
        } finally {
            if (out != null) {
                out.delete();
            }
        }
    }
}
