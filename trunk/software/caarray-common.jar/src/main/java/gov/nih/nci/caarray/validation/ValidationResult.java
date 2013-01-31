//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The result of validating a set of files.
 */
public final class ValidationResult implements Serializable {

    private static final long serialVersionUID = -5781574225752015910L;

    private final Map<File, FileValidationResult> fileValidationResults = new HashMap<File, FileValidationResult>();

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
        for (FileValidationResult fileValidationResult : fileValidationResults.values()) {
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
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (FileValidationResult fileValidationResult : getFileValidationResults()) {
            messages.addAll(fileValidationResult.getMessages());
        }
        return Collections.unmodifiableList(messages);
    }

    /**
     * Returns the messages of given type, ordered by file and location.
     * @param type type of messages to return
     * @return the messages.
     */
    public List<ValidationMessage> getMessages(ValidationMessage.Type type) {
        List<ValidationMessage> messages = new ArrayList<ValidationMessage>();
        for (FileValidationResult fileValidationResult : getFileValidationResults()) {
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
        List<FileValidationResult> fileResultList = new ArrayList<FileValidationResult>(fileValidationResults.size());
        fileResultList.addAll(fileValidationResults.values());
        Collections.sort(fileResultList);
        return Collections.unmodifiableList(fileResultList);
    }

    /**
     * Adds a new validation message to the result.
     *
     * @param file validation message is associated with this file
     * @param type the type/level of the message
     * @param message the actual message content
     * @return the newly added message, if additional configuration of the message is required.
     */
    public ValidationMessage addMessage(File file, Type type, String message) {
        return getOrCreateFileValidationResult(file).addMessage(type, message);
    }

    private FileValidationResult getOrCreateFileValidationResult(File file) {
        if (!fileValidationResults.containsKey(file)) {
            fileValidationResults.put(file, new FileValidationResult(file));
        }
        return getFileValidationResult(file);
    }

    /**
     * Add the results for a file, replacing any previous results for that file.
     * @param file file to add
     * @param fileResult results to add
     */
    public void addFile(File file, FileValidationResult fileResult) {
        fileValidationResults.put(file, fileResult);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the
     * given file, or null if non exists.
     *
     * @param file get validation results for this file
     * @return the validation result.
     */
    public FileValidationResult getFileValidationResult(File file) {
        return fileValidationResults.get(file);
    }

    /**
     * Returns the <code>FileValidationResult</code> corresponding to the
     * file with given , or null if non exists.
     *
     * @param filename get validation results for the file with this name
     * @return the validation result.
     */
    public FileValidationResult getFileValidationResult(String filename) {
        for (Map.Entry<File, FileValidationResult> resultEntry : fileValidationResults.entrySet()) {
            if (resultEntry.getKey().getName().equals(filename)) {
                return resultEntry.getValue();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (FileValidationResult result : fileValidationResults.values()) {
            stringBuffer.append(result.getFile().getName());
            stringBuffer.append(":\n");
            stringBuffer.append(result.toString());
        }
        return stringBuffer.toString();
    }

}
