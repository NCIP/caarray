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

import gov.nih.nci.caarray.magetab.io.FileRef;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReader;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileReaderFactoryImpl;
import com.fiveamsolutions.nci.commons.util.io.DelimitedWriter;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileWriterFactory;
import com.fiveamsolutions.nci.commons.util.io.DelimitedFileWriterFactoryImpl;

import gov.nih.nci.caarray.util.io.FileUtility;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Base class for all MAGE-TAB documents.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class AbstractMageTabDocument implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DelimitedFileReaderFactory READER_FACTORY = new DelimitedFileReaderFactoryImpl();
    private static final DelimitedFileWriterFactory WRITER_FACTORY = new DelimitedFileWriterFactoryImpl();

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Lines starting with '#' will be ignored.
     */
    public static final String COMMENT_CHARACTER = "#";

    private final MageTabDocumentSet documentSet;
    private final FileRef file;

    /**
     * Creates a new MAGE-TAB document from an existing file.
     *
     * @param documentSet the MAGE-TAB document set the MAGE-TAB document belongs to.
     * @param file the file containing the MAGE-TAB document content.
     */
    protected AbstractMageTabDocument(final MageTabDocumentSet documentSet, final FileRef file) {
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
    public final FileRef getFile() {
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
     * Exports the MAGE-TAB object graph into the file corresponding to this document.
     */
    protected abstract void export();

    /**
     * Instantiates a tab-delimited file reader to use to parse the contents.
     *
     * @return the reader.
     * @throws MageTabParsingException if the reader couldn't be created.
     */
    protected final DelimitedFileReader createTabDelimitedReader() throws MageTabParsingException {
        try {
            return READER_FACTORY.createTabDelimitedFileReader(getFile().getAsFile());
        } catch (IOException e) {
            throw new MageTabParsingException("Couldn't create the tab-delimited file reader", e);
        }
    }

    /**
     * Instantiates a tab-delimited file writer to use for export.
     *
     * @return the writer.
     */
    protected final DelimitedWriter createTabDelimitedWriter() {
        try {
            return WRITER_FACTORY.createTabDelimitedWriter(getFile().getAsFile());
        } catch (IOException e) {
            throw new MageTabExportException(e);
        }
    }

    /**
     * Writes a row to a file.
     *
     * @param row a list of Strings to write in a row
     * @param writer the writer
     */
    protected void writeRow(List<String> row, DelimitedWriter writer) {
        try {
            writer.writeLine(row);
        } catch (IOException e) {
            throw new MageTabExportException(e);
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
    protected final OntologyTerm addOntologyTerm(String category, String value) {
        return getDocumentSet().addOntologyTerm(category, value);
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
    protected final OntologyTerm addOntologyTerm(MageTabOntologyCategory category, String value) {
        return addOntologyTerm(category.getCategoryName(), value);
    }

    /**
     * Returns a <code>ArrayDesign</code> that has the given name. Reuses an existing matching
     * <code>ArrayDesign</code> if one exists, otherwise creates one.
     *
     * @param value the name of the array design
     * @return ArrayDesign the new or matching array design
     */
    protected final ArrayDesign getArrayDesign(String value) {
        return getDocumentSet().getArrayDesign(value);
    }

    /**
     * Returns a <code>TermSource</code> that has the given name. Reuses an existing matching
     * <code>TermSource</code> if one exists, otherwise creates one.
     *
     * @param termSourceName the name of the source
     * @return the term source
     */
    protected TermSource getOrCreateTermSource(String termSourceName) {
        return getDocumentSet().getOrCreateTermSource(termSourceName);
    }

    /**
     * Returns a <code>TermSource</code> that has the given name (defined in an IDF).
     * If no matching <code>TermSource</code> exists, returns null.
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
     * Adds a new informational validation message to the document set's validation results.
     *
     * @param message message content.
     * @return the message.
     */
    protected final ValidationMessage addInfoMessage(String message) {
        return addMessage(ValidationMessage.Type.INFO, message);
    }

    /**
     * Adds a new warning validation message to the document set's validation results.
     *
     * @param message message content.
     * @return the message.
     */
    public final ValidationMessage addWarningMessage(String message) {
        return addMessage(ValidationMessage.Type.WARNING, message);
    }

    /**
     * Adds a new error validation message to the document set's validation results.
     *
     * @param message message content.
     * @return the message.
     */
    public final ValidationMessage addErrorMessage(String message) {
        return addMessage(ValidationMessage.Type.ERROR, message);
    }

    /**
     * Adds a new informational validation message to the document set's validation results.
     *
     * @param line line number in file that message applies to.
     * @param column column number in file (on given line) that message applies to.
     * @param message message content.
     * @return the message.
     */
    protected final ValidationMessage addInfoMessage(int line, int column, String message) {
        return addMessage(line, column, ValidationMessage.Type.INFO, message);
    }

    /**
     * Adds a new warning validation message to the document set's validation results.
     *
     * @param line line number in file that message applies to.
     * @param column column number in file (on given line) that message applies to.
     * @param message message content.
     * @return the message.
     */
    protected final ValidationMessage addWarningMessage(int line, int column, String message) {
        return addMessage(line, column, ValidationMessage.Type.WARNING, message);
    }

    /**
     * Adds a new error validation message to the document set's validation results.
     *
     * @param line line number in file that message applies to.
     * @param column column number in file (on given line) that message applies to.
     * @param message message content.
     * @return the message.
     */
    protected final ValidationMessage addErrorMessage(int line, int column, String message) {
        return addMessage(line, column, ValidationMessage.Type.ERROR, message);
    }

    private ValidationMessage addMessage(int line, int column, Type type, String message) {
        ValidationMessage validationMessage = addMessage(type, message);
        validationMessage.setLine(line);
        validationMessage.setColumn(column);
        return validationMessage;
    }

    private ValidationMessage addMessage(Type type, String message) {
        return getDocumentSet().createValidationMessage(getFile(), type, message);
    }

    /**
     * Parse the given date, expected to be in the standard MAGE-TAB format of YYYY-MM-DD.
     * If the date is not in this format, adds a warning message to the validation result
     * @param value the date string
     * @param columnName the name of the column from which this value came (used in the warning if needed)
     * @return the parsed date, or null if the date was not in the required format
     */
    protected Date parseDateValue(String value, String columnName) {
        try {
            format.setLenient(false);
            return format.parse(value);
        } catch (ParseException pe) {
            addWarningMessage("Invalid Date or Date Format(expected YYYY-MM-DD) for column " + columnName + ": "
                    + value);
            return null;
        }

    }

    /**
     * Gets names of files passed in.
     * @param data data files
     * @return list of file names
     */
    protected List<String> getFileNames(List<? extends AbstractSampleDataRelationshipNode> data) {
        List<String> fileNames = new ArrayList<String>();
        for (AbstractSampleDataRelationshipNode adf : data) {
                fileNames.add(adf.getName());
        }
        return fileNames;
    }
}
