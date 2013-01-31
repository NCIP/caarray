//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;

import java.io.File;

/**
 * Exports an Experiment into MAGE-TAB format. An IDF file and an SDRF file are generated.
 *
 * @author Rashmi Srinivasa
 */
public interface MageTabExporter {

    /**
     * The default JNDI name to use to lookup <code>MageTabExporter</code>.
     */
    String JNDI_NAME = "caarray/MageTabExporterBean/local";

    /**
     * Takes an Experiment, constructs a MAGE-TAB IDF and SDRF describing the sample-data relationships
     * and annotations, and exports the MAGE-TAB into files.
     *
     * @param experiment the experiment whose content needs to be translated into MAGE-TAB files.
     * @param idfFile the File which will hold the IDF document.
     * @param sdrfFile the File which will hold the SDRF document.
     * @return the MageTabDocumentSet 
     */
    MageTabDocumentSet exportToMageTab(Experiment experiment, File idfFile, File sdrfFile);
}
