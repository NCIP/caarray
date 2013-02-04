//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The result of validating a set of files.
 */
public final class ValidationResult implements Serializable {

    private static final long serialVersionUID = -5781574225752015910L;

    private final Map<String, FileValidationResult> fileValidationResults = Maps.newTreeMap();

    /**
     * Instantiates a new, empty result.
     */
    public ValidationResult() {
        super();
    }

    /**
     * Returns true if all the documents in the set were valid.
     * 
     * @return true if set was valid.
     */
    public boolean isValid() {
        for (final FileValidationResult fileValidationResult : this.fileValidationResults.values()) {
            if (!fileValidationResult.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the messages ordered by file, type and location.
     * 
     * @return the messages.
     */
    public List<ValidationMessage> getMessages() {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (final FileValidationResult fileValidationResult : getFileValidationResults()) {
            messages.addAll(fileValidationResult.getMessages());
        }
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns the messages of given type, ordered by file and location.
     * 
     * @param type type of messages to return
     * @return the messages.
     */
    public List<ValidationMessage> getMessages(ValidationMessage.Type type) {
        final List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (final FileValidationResult fileValidationResult : getFileValidationResults()) {
            messages.addAll(fileValidationResult.getMessages(type));
        }
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns all the file validation results in order by file.
     * 
     * @return the file validation results.
     */
    public List<FileValidationResult> getFileValidationResults() {
        return Lists.newArrayList(this.fileValidationResults.values());
    }

    /**
     * Returns the names of all the files for which this has validation results.
     * 
     * @return set of names of the files for which this contains validation results
     */
    public Set<String> getFileNames() {
        return Collections.unmodifiableSet(this.fileValidationResults.keySet());
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param fileName validation message is associated with the file of this name
     * @param type the type/level of the message
     * @param message the actual message content
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(String fileName, Type type, String message) {
        return getOrCreateFileValidationResult(fileName).addMessage(type, message);
    }

    /**
     * Adds a new validation message to the result.
     * 
     * @param fileName validation message is associated with the file of this name
     * @param message the validation message
     */
    public void addMessage(String fileName, ValidationMessage message) {
        getOrCreateFileValidationResult(fileName).addMessage(message);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the file with given name. If one does not exist
     * yet, creates a new one and adds it to this.
     * 
     * @param fileName get or create validation results for the file of this name
     * @return an existing or new validation result for the file
     */
    public FileValidationResult getOrCreateFileValidationResult(String fileName) {
        if (!this.fileValidationResults.containsKey(fileName)) {
            this.fileValidationResults.put(fileName, new FileValidationResult());
        }
        return getFileValidationResult(fileName);
    }

    /**
     * Add the results for a file, replacing any previous results for that file.
     * 
     * @param fileName name of file to add
     * @param fileResult results to add
     */
    public void addFile(String fileName, FileValidationResult fileResult) {
        this.fileValidationResults.put(fileName, fileResult);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the given file, or null if non exists.
     * 
     * @param fileName get validation results for the file with this name
     * @return the validation result.
     */
    public FileValidationResult getFileValidationResult(String fileName) {
        return this.fileValidationResults.get(fileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
