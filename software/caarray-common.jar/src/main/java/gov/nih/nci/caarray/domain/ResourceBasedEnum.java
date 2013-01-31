//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain;

/**
 * Interface to be implemented by Enums which have a field holding a resource
 * key that represents the UI label for that enum.
 */
public interface ResourceBasedEnum {
    /**
     * @return the resource key that should be used to retrieve a label
     * for the Enum instance in the UI
     */
    String getResourceKey();
}
