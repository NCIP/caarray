//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.plugins.agilent;

/**
 * Processes domain object persistence chunks.
 * @author dharley
 *
 */
interface PersistenceChunkProcessor {
    
    /**
     * Process a chunk of domain object peristence.
     */
    void processChunk(boolean forceFlush);

}
