//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.permissions;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Enum of access types for sample-level security.
 */
public enum SampleSecurityLevel implements ResourceBasedEnum {
    /** No access to the sample. */
    NONE(true, false, false, "SampleSecurityLevel.none"),
    /** Read access to the sample. */
    READ(true, true, false, "SampleSecurityLevel.read"),
    /** Read / write access to the sample. */
    READ_WRITE(false, true, true, "SampleSecurityLevel.readWrite");

    private final boolean availableToPublic;
    private final boolean allowsRead;
    private final boolean allowsWrite;
    private String resourceKey;

    private SampleSecurityLevel(boolean availableToPublic, boolean allowsRead, boolean allowsWrite,
            String resourceKey) {
        this.availableToPublic = availableToPublic;
        this.allowsRead = allowsRead;
        this.allowsWrite = allowsWrite;
        this.resourceKey = resourceKey;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * @return whether or not this security level can be granted in the public access profile
     */
    public boolean isAvailableToPublic() {
        return availableToPublic;
    }

    /**
     * @return the list of SecurityLevels that are available to the public access profile
     */
    public static List<SampleSecurityLevel> publicLevels() {
        List<SampleSecurityLevel> levels = new ArrayList<SampleSecurityLevel>(Arrays.asList(SampleSecurityLevel
                .values()));
        CollectionUtils.filter(levels, new Predicate() {
            public boolean evaluate(Object o) {
                return ((SampleSecurityLevel) o).isAvailableToPublic();
            }
        });
        return levels;
    }

    /**
     * @return whether this security level allows read access
     */
    public boolean isAllowsRead() {
        return allowsRead;
    }

    /**
     * @return whether this security level allows write access
     */
    public boolean isAllowsWrite() {
        return allowsWrite;
    }

}
