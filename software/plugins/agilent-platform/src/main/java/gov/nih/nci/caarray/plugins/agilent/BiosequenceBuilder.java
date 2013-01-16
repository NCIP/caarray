//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Used during the parsing of an array design definition file to create the accessions
 * associated with a physical probe.
 * 
 * @author jscott
 */
public interface BiosequenceBuilder extends AccessionBuilder {
    /**
     * Called when there are no more accessions to add to the biosequence.
     * @return a reference to itself.
     */
    BiosequenceBuilder finish();   
}
