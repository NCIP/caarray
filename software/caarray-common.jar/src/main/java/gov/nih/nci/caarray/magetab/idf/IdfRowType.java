//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab.idf;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of legal row headings in an IDF document.
 */
public enum IdfRowType {
    /**
     * Investigation Title.
     */
    INVESTIGATION_TITLE ("Investigation Title"),

    /**
     * Experimental Design.
     */
    EXPERIMENTAL_DESIGN ("Experimental Design"),

    /**
     * Experimental Design Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    EXPERIMENTAL_DESIGN_TERM_SOURCE_REF ("Experimental Design Term Source REF"),
    /**
     * Experimental Factor Name.
     */
    EXPERIMENTAL_FACTOR_NAME ("Experimental Factor Name"),

    /**
     * Experimental Factor Type.
     */
    EXPERIMENTAL_FACTOR_TYPE ("Experimental Factor Type"),

    /**
     * Experimental Factor Term Source REF.
     */
    EXPERIMENTAL_FACTOR_TERM_SOURCE_REF ("Experimental Factor Term Source REF"),

    /**
     * Experimental Factor Type Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    EXPERIMENTAL_FACTOR_TYPE_TERM_SOURCE_REF ("Experimental Factor Type Term Source REF"),

    /**
     * Person Last Name.
     */
    PERSON_LAST_NAME ("Person Last Name"),

    /**
     * Person Last Names. (accepting a variation)
     */
    PERSON_LAST_NAMES ("Person Last Names"),

    /**
     * Person First Name.
     */
    PERSON_FIRST_NAME ("Person First Name"),


    /**
     * Person First Names. (accepting a variation)
     */
    PERSON_FIRST_NAMES ("Person First Names"),

    /**
     * Person Mid Initials.
     */
    PERSON_MID_INITIALS ("Person Mid Initials"),

    /**
     * Person Mid Initial. (accepting a variation)
     */
    PERSON_MID_INITIAL ("Person Mid Initial"),

    /**
     * Person Email.
     */
    PERSON_EMAIL ("Person Email"),

    /**
     * Person Phone.
     */
    PERSON_PHONE ("Person Phone"),

    /**
     * Person Fax.
     */
    PERSON_FAX ("Person Fax"),

    /**
     * Person Address.
     */
    PERSON_ADDRESS ("Person Address"),

    /**
     * Person Affiliation.
     */
    PERSON_AFFILIATION ("Person Affiliation"),

    /**
     * Person Roles.
     */
    PERSON_ROLES ("Person Roles"),

    /**
     * Person Roles Term Source REF.
     */
    PERSON_ROLES_TERM_SOURCE_REF ("Person Roles Term Source REF"),

    /**
     * Quality Control Type.
     */
    QUALITY_CONTROL_TYPE ("Quality Control Type"),


    /**
     * Quality Control Types.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    QUALITY_CONTROL_TYPES ("Quality Control Types"),

    /**
     * Quality Control Term Source REF.
     */
    QUALITY_CONTROL_TERM_SOURCE_REF ("Quality Control Term Source REF"),

    /**
     * Quality Control Types Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    QUALITY_CONTROL_TYPES_TERM_SOURCE_REF ("Quality Control Types Term Source REF"),

    /**
     * Replicate Type.
     */
    REPLICATE_TYPE ("Replicate Type"),

    /**
     * Replicate Term Source REF.
     */
    REPLICATE_TERM_SOURCE_REF ("Replicate Term Source REF"),

    /**
     * Replicate Type Term Source REF.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    REPLICATE_TYPE_TERM_SOURCE_REF ("Replicate Type Term Source REF"),

    /**
     * Normalization Type.
     */
    NORMALIZATION_TYPE ("Normalization Type"),

    /**
     * Normalization Term Source REF.
     */
    NORMALIZATION_TERM_SOURCE_REF ("Normalization Term Source REF"),

    /**
     * Date of Experiment.
     */
    DATE_OF_EXPERIMENT ("Date of Experiment"),

    /**
     * Public Release Date.
     */
    PUBLIC_RELEASE_DATE ("Public Release Date"),

    /**
     * PubMed ID.
     */
    PUBMED_ID ("PubMed ID"),

    /**
     * Publication DOI.
     */
    PUBLICATION_DOI ("Publication DOI"),

    /**
     * Publication Author List.
     */
    PUBLICATION_AUTHOR_LIST ("Publication Author List"),

    /**
     * Publication Title.
     */
    PUBLICATION_TITLE ("Publication Title"),

    /**
     * Publication Status.
     */
    PUBLICATION_STATUS ("Publication Status"),

    /**
     * Publication Status Term Source REF.
     */
    PUBLICATION_STATUS_TERM_SOURCE_REF ("Publication Status Term Source REF"),

    /**
     * Experiment Description.
     */
    EXPERIMENT_DESCRIPTION ("Experiment Description"),

    /**
     * Protocol Name.
     */
    PROTOCOL_NAME ("Protocol Name"),

    /**
     * Protocol Type.
     */
    PROTOCOL_TYPE ("Protocol Type"),

    /**
     * Protocol Description.
     */
    PROTOCOL_DESCRIPTION ("Protocol Description"),

    /**
     * Protocol Parameters.
     */
    PROTOCOL_PARAMETERS ("Protocol Parameters"),

    /**
     * Protocol Hardware.
     */
    PROTOCOL_HARDWARE ("Protocol Hardware"),

    /**
     * Protocol Software.
     */
    PROTOCOL_SOFTWARE ("Protocol Software"),

    /**
     * Protocol Contact.
     */
    PROTOCOL_CONTACT ("Protocol Contact"),

    /**
     * Protocol Term Source REF.
     */
    PROTOCOL_TERM_SOURCE_REF ("Protocol Term Source REF"),

    /**
     * SDRF File.
     */
    SDRF_FILE ("SDRF File"),

    /**
     * SDRF Files.
     * Not included in MAGE-TAB 1.0 specification, but included for compatibility with real world
     * MAGE-TAB data.
     */
    SDRF_FILES ("SDRF Files"),

    /**
     * Term Source Name.
     */
    TERM_SOURCE_NAME ("Term Source Name"),

    /**
     * Term Source File.
     */
    TERM_SOURCE_FILE ("Term Source File"),

    /**
     * Term Source Version.
     */
    TERM_SOURCE_VERSION ("Term Source Version"),

    /**
     * Comment.
     */
    COMMENT ("Comment"),

    /**
     * Catchall placeholder for row headings that did not match any of the above.
     */
    INVALID_TYPE ("Invalid Type");

    private final String displayName;

    static final IdfRowType[] TERM_SOURCE_TYPES = {TERM_SOURCE_FILE, TERM_SOURCE_NAME, TERM_SOURCE_VERSION };

    /**
     * An IDF row header with the specified display name.
     *
     * @param displayName the actual name of the row that will be displayed.
     */
    IdfRowType(String displayName) {
        this.displayName = displayName;
    }

    static IdfRowType get(String name) {
        String enumName = StringUtils.replaceChars(name, ' ', '_').toUpperCase();
        try {
            return valueOf(enumName);
        } catch (IllegalArgumentException e) {
            return INVALID_TYPE;
        }
    }

    /**
     * Returns the display name of the row.
     *
     * @return the display name of the row.
     */
    public String toString() {
        return displayName;
    }
}
