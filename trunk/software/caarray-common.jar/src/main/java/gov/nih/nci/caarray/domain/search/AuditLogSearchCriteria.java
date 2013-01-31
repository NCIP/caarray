//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.search;

/**
 *
 * @author gax
 */
public class AuditLogSearchCriteria {
    private String username;
    private String message;

    /**
     * @return message to search for.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message message to search for.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return username to searc for.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username username to searc for.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
