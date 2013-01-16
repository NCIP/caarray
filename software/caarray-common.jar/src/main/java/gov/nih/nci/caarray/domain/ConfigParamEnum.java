//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

/**
 * Various configuration parameters.
 */
public enum ConfigParamEnum {
    /**
     * The email address to send email from.
     */
    EMAIL_FROM,
    /**
     * Boolean property on whether to send an confirmation email to the end user
     * after registering.
     */
    SEND_CONFIRM_EMAIL,
    /**
     * Subject line of the confirmation email.
     */
    CONFIRM_EMAIL_SUBJECT,
    /**
     * Content of the confirmation email.
     */
    CONFIRM_EMAIL_CONTENT,
    /**
     * Boolean property on whether to send an email to the administrator
     * after registering.
     */
    SEND_ADMIN_EMAIL,
    /**
     * What email address to send registration information to (ie, the helpdesk email address).
     */
    REG_EMAIL_TO,
    /**
     * Subject line of the registration email.
     */
    REG_EMAIL_SUBJECT,
    /**
     * The thankyou for registering text that should appear after a user submits their registration.
     */
    THANKS_MESSAGE,
    /**
     * the subject for the submit experiment email.
     */
    SUBMIT_EXPERIMENT_EMAIL_SUBJECT,
    /**
     * the html content for the submit experiment email.
     */
    SUBMIT_EXPERIMENT_EMAIL_HTML_CONTENT,
    /**
     * the plain-text content for the submit experiment email.
     */
    SUBMIT_EXPERIMENT_EMAIL_PLAIN_CONTENT,

    /**
     * the location to save uploaded files too.
     */
    STRUTS_MULTIPART_SAVEDIR,

    /**
     * the background import file thread transaction timeout in seconds.
     */
    BACKGROUND_THREAD_TRANSACTION_TIMEOUT,

    /**
     * Development mode flag.  Should only be true for local development purposes.
     */
    DEVELOPMENT_MODE,

    /**
     * caArray version number, for schema migration purposes.
     */
    SCHEMA_VERSION,
    
    /**
     * Chunk size for the RMIIO-based file retrieval API.
     */
    FILE_RETRIEVAL_API_CHUNK_SIZE;
}
