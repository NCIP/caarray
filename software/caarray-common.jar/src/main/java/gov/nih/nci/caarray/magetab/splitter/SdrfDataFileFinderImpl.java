//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab.splitter;

import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Default implementation of the interface.
 * 
 * @author tparnell
 */
public class SdrfDataFileFinderImpl implements SdrfDataFileFinder {

    private static final Logger LOG = Logger.getLogger(SdrfDataFileFinderImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> identifyReferencedDataFiles(FileRef sdrf) throws IOException {
        SdrfDocument doc = extractDocFromRef(sdrf);
        parse(doc);
        return extractReferencedFileNames(doc);        
    }

    private SdrfDocument extractDocFromRef(FileRef sdrf) {
        MageTabFileSet mageTabFileSet = new MageTabFileSet();
        SdrfDocument doc = new SdrfDocument(new MageTabDocumentSet(mageTabFileSet), sdrf);
        
        return doc;
    }
    
    private void parse(SdrfDocument doc) {
        try {
            doc.parseNoIdfCheck();
        } catch (MageTabParsingException e) {
           LOG.warn(String.format("Got parsing exception inside splitter.  " 
                   + "Code assumes the referenced sdrf (%s) is valid. ", doc.getFile().getName()), e);
           throw new IllegalArgumentException("Invalid sdrf file: " + doc.getFile().getName(), e);
        }
    }
    
    private Set<String> extractReferencedFileNames(SdrfDocument doc) {
        Set<String> result = new HashSet<String>();
        result.addAll(doc.getReferencedDataMatrixFileNames());
        result.addAll(doc.getReferencedDerivedFileNames());
        result.addAll(doc.getReferencedRawFileNames());
        
        return result;
    }
}
