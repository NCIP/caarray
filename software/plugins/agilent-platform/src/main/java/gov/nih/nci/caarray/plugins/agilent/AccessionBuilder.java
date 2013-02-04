//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Used during the parsing of an array design definition file to handle accessions associated with
 * a gene, expression probe or miRNA probe.
 * 
 * @author jscott
 */
public interface AccessionBuilder {
    /**
     * Associates a new Genbank accession to the gene.
     * @param accessionNumber the Genbank accession number
     * @return a reference to itself
     */   
    AccessionBuilder createNewGBAccession(String accessionNumber);

    /**
     * Associates a new Ensembl accession to the gene object.
     * @param accessionNumber the Ensembl accession number
     * @return a reference to itself
     */
    AccessionBuilder createNewEnsemblAccession(String accessionNumber);

    /**
     * Associates a new RefSeq accession to the gene object.
     * @param accessionNumber the RefSeq accession number
     * @return a reference to itself
     */
    AccessionBuilder createNewRefSeqAccession(String accessionNumber);

    /**
     * Associates a new THC accession to the gene object.
     * @param accessionNumber the THC accession number
     * @return a reference to itself
     */
    AccessionBuilder createNewTHCAccession(String accessionNumber);

    /**
     * Handles an AGP accession, which is used to map a biosequences and biosequence_refs.
     * @param accessionNumber the AGP accession number
     * @return a reference to itself
     */
    AccessionBuilder agpAccession(String accessionNumber);

    /**
     * Associates a new Mir accession.
     * @param accessionNumber the Mir accession number
     * @return a reference to itself
     */
    AccessionBuilder createNewMirAccession(String accessionNumber);
}
