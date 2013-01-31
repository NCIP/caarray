//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.validation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * A single entry in a <code>ValidationResult</code> that describes an issue and the location (if known).
 */
@Entity
public final class ValidationMessage implements Serializable, Comparable<ValidationMessage> {

    private static final long serialVersionUID = 8575821452264941994L;

    private static final int MAXIMUM_MESSAGE_LENGTH = 1024;
    private static final int MAX_TYPE_LENGTH = 10;

    private Type type;
    private String message;
    private int line;
    private int column;

    private Long id;

    @SuppressWarnings("unused")
    private ValidationMessage() {
        super();
    }

    ValidationMessage(Type type, String message) {
        super();
        setType(type);
        setMessage(message);
    }

    /**
     * Sorts the message type, line number, and then message.
     * {@inheritDoc}
     */
    public int compareTo(ValidationMessage o) {
        if (!type.equals(o.getType())) {
            return type.compareTo(o.getType());
        } else if (line != o.line) {
            return line - o.line;
        } else if (column != o.column) {
            return column - o.column;
        } else {
            return o.message.compareTo(message);
        }
    }

    /**
     * @return the column
     */
    @Column(name = "file_column")
    public int getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * @return the line
     */
    @Column(name = "file_line")
    public int getLine() {
        return line;
    }

    /**
     * @param line the line to set
     */
    public void setLine(int line) {
        this.line = line;
    }

    /**
     * @return the message
     */
    @Column(length = MAXIMUM_MESSAGE_LENGTH)
    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = MAX_TYPE_LENGTH, nullable = false)
    public Type getType() {
        return type;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private Long getId() {
        return id;
    }

    @SuppressWarnings({"unused", "PMD.UnusedPrivateMethod" })
    private void setId(Long id) {
        this.id = id;
    }

    private void setType(Type type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getType());
        stringBuffer.append(": ");
        stringBuffer.append(getMessage());
        if (line != 0) {
            stringBuffer.append(", line=");
            stringBuffer.append(line);
        }
        if (column != 0) {
            stringBuffer.append(", column=");
            stringBuffer.append(column);
        }
        return stringBuffer.toString();
    }

    /**
     * Indicates the type/level of the message.
     */
    public static enum Type implements Comparable<Type> {

        /**
         * Indicates invalid content.
         */
        ERROR,

        /**
         * Warning of potentially problematic content.
         */
        WARNING,

        /**
         * Informational message.
         */
        INFO;


    }

}
