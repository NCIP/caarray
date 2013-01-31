//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util.owlparser;

import gov.nih.nci.caarray.domain.project.ExperimentOntology;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Executable class to parse the MGED Ontology OWL from a well-defined place in the classpath and write out a
 * corresponding SQL file.
 * 
 * @author dkokotov
 */
public final class ParseMgedOntology {
    /** 
     * Path to the MGED owl file (as a classpath).
     */
    public static final String MGED_OWL_PATH = "/MGEDOntology.owl";
    
    /**
     * Url for MGED ontology.
     */
    public static final String MGED_URL = "http://mged.sourceforge.net/ontologies/MGEDontology.php";
    
    /**
     * Name of sql file to write out (filename only, directory to be passed in as command line argument).
     */
    public static final String SQL_FILE_NAME = "populate-mged.sql";
    
    private ParseMgedOntology() { }

    /**
     * Main method.
     * @param args expected to have a single parameter which should have the pathname of the directory
     * into which the sql file is to be written
     */
    public static void main(String[] args) {
        InputStream owlStream = ParseMgedOntology.class.getResourceAsStream(MGED_OWL_PATH);
        String sqlDirectory = args[0];
        SqlOntologyOwlParser owlParser = new SqlOntologyOwlParser(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                MGED_URL, new File(sqlDirectory, SQL_FILE_NAME));
        try {
            owlParser.parse(owlStream);
        } catch (ParseException e) {
            e.printStackTrace(); // NOPMD
        } finally {
            try {
                owlStream.close();
            } catch (IOException e) { // NOPMD
                // nothing to be done
            }
        }
    }
}
