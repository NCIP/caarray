/**
 *  The caArray Software License, Version 1.0
 *
 *  Copyright 2004 5AM Solutions. This software was developed in conjunction
 *  with the National Cancer Institute, and so to the extent government
 *  employees are co-authors, any rights in such works shall be subject to
 *  Title 17 of the United States Code, section 105.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the disclaimer of Article 3, below.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  2. Affymetrix Pure Java run time library needs to be downloaded from
 *  (http://www.affymetrix.com/support/developer/runtime_libraries/index.affx)
 *  after agreeing to the licensing terms from the Affymetrix.
 *
 *  3. The end-user documentation included with the redistribution, if any,
 *  must include the following acknowledgment:
 *
 *  "This product includes software developed by 5AM Solutions and the National
 *  Cancer Institute (NCI).
 *
 *  If no such end-user documentation is to be included, this acknowledgment
 *  shall appear in the software itself, wherever such third-party
 *  acknowledgments normally appear.
 *
 *  4. The names "The National Cancer Institute", "NCI", and "5AM Solutions"
 *  must not be used to endorse or promote products derived from this software.
 *
 *  5. This license does not authorize the incorporation of this software into
 *  any proprietary programs. This license does not authorize the recipient to
 *  use any trademarks owned by either NCI or 5AM.
 *
 *  6. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 *  EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.util.io.FileUtility;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Base class for all MAGE-TAB documents.
 */
public abstract class AbstractMageTabDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    private final MageTabDocumentSet documentSet;
    private final File file;

    /**
     * Creates a new MAGE-TAB document from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the MAGE-TAB document belongs to.
     * @param file the file containing the MAGE-TAB document content.
     */
    protected AbstractMageTabDocument(final MageTabDocumentSet documentSet, final File file) {
        super();
        if (documentSet == null) {
            throw new IllegalArgumentException("documentSet was null");
        }
        FileUtility.checkFileExists(file);
        this.documentSet = documentSet;
        this.file = file;
    }

    /**
     * @return the file
     */
    public final File getFile() {
        return file;
    }

    /**
     * @return the documentSet
     */
    public final MageTabDocumentSet getDocumentSet() {
        return documentSet;
    }

    /**
     * Parses the MAGE-TAB document, creating the object graph of entities.
     *
     * @throws MageTabParsingException if I/O failed reading the MAGE-TAB file.
     */
    protected abstract void parse() throws MageTabParsingException;

    /**
     * Instantiates a tab-delimited file reader to use to parse the contents.
     *
     * @return the reader.
     * @throws MageTabParsingException if the reader couldn't be created.
     */
    protected final DelimitedFileReader createTabDelimitedReader() throws MageTabParsingException {
        try {
            return DelimitedFileReaderFactory.INSTANCE.getTabDelimitedReader(getFile());
        } catch (IOException e) {
            throw new MageTabParsingException("Couldn't create the tab-delimited file reader", e);
        }
    }

    /**
     * Returns an <code>OntologyTerm</code> matching the category and name given. Reuses an
     * existing matching <code>OntologyTerm</code> in the document set if one exists,
     * otherwise creates one.
     *
     * @param category category of the term
     * @param value value of the term
     * @return the new or matching term.
     */
    protected final OntologyTerm getOntologyTerm(String category, String value) {
        return getDocumentSet().getOntologyTerm(category, value);
    }

    /**
     * Returns a <code>TermSource</code> that has the given name. Reuses an existing matching
     * <code>TermSource</code> if one exists, otherwise creates one.
     *
     * @param termSourceName the name of the source
     * @return the term source
     */
    protected TermSource getTermSource(String termSourceName) {
        return getDocumentSet().getTermSource(termSourceName);
    }

    /**
     * Adds a new Protocol to the document set.
     *
     * @param protocol the new protocol.
     */
    protected final void addProtocol(Protocol protocol) {
        getDocumentSet().addProtocol(protocol);
    }

    /**
     * Returns the protocol with the id (name) provided.
     *
     * @param protocolId find protocol with this name.
     * @return the matching protocol or null if none exists for name.
     */
    protected final Protocol getProtocol(String protocolId) {
        return getDocumentSet().getProtocol(protocolId);
    }

    /**
     * Creates an entry heading from the column or row heading
     * value provided.
     * 
     * @param headingString the heading as given in the file
     * @return the heading object.
     */
    protected final EntryHeading createHeading(String headingString) {
        return new EntryHeading(headingString);
    }

    /**
     * Returns a term that should originate from the MGED ontology.
     * 
     * @param category category of the term.
     * @param value the term.
     * @return the term object.
     */
    protected final OntologyTerm getMgedOntologyTerm(MageTabOntologyCategory category, String value) {
        OntologyTerm term = getOntologyTerm(category.getCategoryName(), value);
        term.setTermSource(getTermSource("MO"));
        return term;
    }

}
