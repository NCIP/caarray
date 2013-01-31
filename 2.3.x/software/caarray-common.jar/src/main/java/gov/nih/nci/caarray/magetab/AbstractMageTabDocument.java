//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.magetab;

import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDesign;
import gov.nih.nci.caarray.util.io.DelimitedFileReader;
import gov.nih.nci.caarray.util.io.DelimitedFileReaderFactory;
import gov.nih.nci.caarray.util.io.DelimitedWriter;
import gov.nih.nci.caarray.util.io.DelimitedWriterFactory;
import gov.nih.nci.caarray.util.io.FileUtility;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

/**
 * Base class for all MAGE-TAB documents.
 */
public abstract class AbstractMageTabDocument implements Serializable {
    private static final long serialVersionUID = 1L;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Lines starting with '#' will be ignored.
     */
    protected static final String COMMENT_CHARACTER = "#";

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
            return DelimitedFileReaderFactory.INSTANCE.getTabDelimitedReader(getFile());
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
            OutputStream outStream = FileUtils.openOutputStream(file);
            return DelimitedWriterFactory.getTabDelimitedWriter(outStream);
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
    protected final ValidationMessage addWarningMessage(String message) {
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
