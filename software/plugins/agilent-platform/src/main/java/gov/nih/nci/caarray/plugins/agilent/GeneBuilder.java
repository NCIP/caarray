//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;


/**
     * Used during the parsing of an array design definition file to create the object graph
     * associated with a gene.
     * 
     * @author jscott
     */
    public interface GeneBuilder extends AccessionBuilder {       
        /**
         * Sets the chromosome location for the gene.
         * @param chromosome the number (1-22) or name ("x" or "y") of the chromosome
         * @param startPosition the position on the chromosome where the gene begins
         * @param endPosition the position on the chromosome where the gene ends
         * @return a reference to itself
         */
        GeneBuilder setChromosomeLocation(String chromosome, long startPosition, long endPosition);
}
