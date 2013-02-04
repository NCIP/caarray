//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.magetab;

import java.io.Serializable;

/**
 * The row or column heading from a MAGE-TAB IDF or SDRF.
 */
public final class EntryHeading implements Serializable {


    private static final long serialVersionUID = -7457510799166808162L;
    private static final String LSID_NAMESPACE_START = " REF:";
    private static final char LSID_COMPONENT_SEPARATOR = ':';
    private static final char CATEGORY_START_DELIMITER = '[';
    private static final char CATEGORY_END_DELIMITER = ']';
    
    private String authority;
    private String namespace;
    private String qualifier;
    private String typeName;
    private final String headingString;

    /**
     * Create a new EntryHeading based on the given column header text.
     * @param headingString the column header as found in the SDRF or IDF
     */
    public EntryHeading(final String headingString) {
        this.headingString = headingString;
        parseName();
        parseQualifier();
    }

     private void parseName() {
        parseAuthority();
        parseNamespace();
        parseQualifier();
        parseTypeName();
    }

    private void parseAuthority() {
        if (containsAuthority()) {
            authority = 
                headingString.substring(getAuthorityStartIndex(), getAuthorityEndIndex() + 1);
        }
    }

    private int getAuthorityStartIndex() {
        if (containsAuthority()) {
            return headingString.indexOf(LSID_NAMESPACE_START) + LSID_NAMESPACE_START.length();
        } else {
            return -1;
        }
    }

    private int getAuthorityEndIndex() {
        if (containsAuthority()) {
            int namespaceSeparatorIndex = 
                headingString.indexOf(LSID_COMPONENT_SEPARATOR, getAuthorityStartIndex());
            if (namespaceSeparatorIndex != -1) {
                return namespaceSeparatorIndex - 1;
            } else {
                return headingString.length() - 1;
            }
        } else {
            return -1;
        }
    }

    private boolean containsAuthority() {
        return headingString.indexOf(LSID_NAMESPACE_START) != -1;
    }

    private void parseNamespace() {
        if (containsNamespace()) {
            int startIndex = headingString.indexOf(LSID_COMPONENT_SEPARATOR, getAuthorityEndIndex()) + 1;
            int endIndex = headingString.endsWith(":") ? headingString.length() - 2 : headingString.length() - 1;
            namespace = headingString.substring(startIndex, endIndex + 1);
        }
    }

    private boolean containsNamespace() {
        return containsAuthority() 
        && headingString.indexOf(LSID_COMPONENT_SEPARATOR, getAuthorityEndIndex()) != -1;
    }

    private void parseTypeName() {
        if (containsAuthority()) {
            typeName = headingString.substring(0, headingString.indexOf(LSID_COMPONENT_SEPARATOR)).trim();
        } else if (hasQualifier()) {
            typeName = headingString.substring(0, headingString.indexOf(CATEGORY_START_DELIMITER)).trim();
        } else {
            typeName = headingString.trim();
        }
    }

    private boolean hasQualifier() {
        return headingString.indexOf(CATEGORY_START_DELIMITER) != -1
        && headingString.lastIndexOf(CATEGORY_END_DELIMITER) != -1;
    }

    private void parseQualifier() {
        if (hasQualifier()) {
            int leftBracketIndex = headingString.indexOf(CATEGORY_START_DELIMITER);
            int rightBracketIndex = headingString.lastIndexOf(CATEGORY_END_DELIMITER);
            qualifier = headingString.substring(leftBracketIndex + 1, rightBracketIndex);
        }
    }

    /**
     * Returns the name of the heading type. As an example, getName for the heading
     * "Characteristics[Age]" returns "Characteristics".
     * 
     * @return the name of the column type.
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return the qualifier
     */
    public String getQualifier() {
        return qualifier;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return headingString;
    }
    /**
     * @return the lsid
     */
    public String getLsid() {
        if (authority == null || namespace == null) {
            return null;
        }
        return authority + ":" + namespace + ":";
    }

      
}
