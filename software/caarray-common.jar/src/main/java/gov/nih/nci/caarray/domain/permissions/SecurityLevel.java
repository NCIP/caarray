//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain.permissions;

import gov.nih.nci.caarray.domain.ResourceBasedEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * For access profiles, what type of access is permitted.
 */
public enum SecurityLevel implements ResourceBasedEnum {
    /** No access to project or any samples. */
    NONE("SecurityLevel.none",  false, true, false, false, false, false),
    /** Limited access to project and no access to samples. */
    VISIBLE("SecurityLevel.visible",  true, false, false, false, false, false),
    /** Read access to project and all samples. */
    READ("SecurityLevel.read",  true, true, true, false, false, false),
    /** Partial Read access to project and specified samples. */
    READ_SELECTIVE("SecurityLevel.readSelective", true, true, false, false, true, false, SampleSecurityLevel.NONE,
            SampleSecurityLevel.READ),
    /** Write access to project and all samples. */
    WRITE("SecurityLevel.write",  false, true, true, true, false, false),
    /** Partial Write access to project. Read access and/or write access to specificed samples. */
    READ_WRITE_SELECTIVE("SecurityLevel.readWriteSelective", false, true, false, false, true, true,
            SampleSecurityLevel.NONE, SampleSecurityLevel.READ, SampleSecurityLevel.READ_WRITE),
    /** No access to project or any samples - public profile version. */
    NO_VISIBILITY("SecurityLevel.noVisibility", true, false, false, false, false, false);

    private final boolean availableToPublic;
    private final boolean availableToGroups;    
    private final String resourceKey;
    private final boolean allowsRead;
    private final boolean allowsWrite;
    private final boolean partialRead;
    private final boolean partialWrite;
    private final List<SampleSecurityLevel> sampleSecurityLevels = new ArrayList<SampleSecurityLevel>();

    @SuppressWarnings("PMD.ExcessiveParameterList")
    // CHECKSTYLE:OFF more than 7 parameters are okay for enum constructor
    private SecurityLevel(String resourceKey, boolean availableToPublic, boolean availableToGroups,
            boolean allowsRead, boolean allowsWrite, boolean partialRead, boolean partialWrite,
            SampleSecurityLevel... sampleSecurityLevels) {
        // CHECKSTYLE:ON
        this.availableToPublic = availableToPublic;
        this.availableToGroups = availableToGroups;
        this.allowsRead = allowsRead;
        this.allowsWrite = allowsWrite;
        this.partialRead = partialRead;
        this.partialWrite = partialWrite;
        this.resourceKey = resourceKey;
        this.sampleSecurityLevels.addAll(Arrays.asList(sampleSecurityLevels));
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
        return this.availableToPublic;
    }

    /**
     * @return whether or not this security level can be granted in a group access profile
     */
    public boolean isAvailableToGroups() {
        return availableToGroups;
    }

    /**
     * @return the set of security levels that can be assigned to samples given this project security level
     */
    public List<SampleSecurityLevel> getSampleSecurityLevels() {
        return Collections.unmodifiableList(this.sampleSecurityLevels);
    }

    /**
     * @return whether this project security level allows permissions to be set at the sample level
     */
    public boolean isSampleLevelPermissionsAllowed() {
        return !getSampleSecurityLevels().isEmpty();
    }

    /**
     * @return the list of SecurityLevels that are available to the public access profile
     */
    public static List<SecurityLevel> publicLevels() {
        return valuesSubset(new Predicate() {
            public boolean evaluate(Object o) {
                return ((SecurityLevel) o).isAvailableToPublic();
            }
        });
    }

    /**
     * @return the list of SecurityLevels that are available to the group access profile
     */
    public static List<SecurityLevel> collaboratorGroupLevels() {
        return valuesSubset(new Predicate() {
            public boolean evaluate(Object o) {
                return ((SecurityLevel) o).isAvailableToGroups();
            }
        });
    }

    /**
     * @return the list of SecurityLevels that match the given predicate
     */
    private static List<SecurityLevel> valuesSubset(Predicate p) {
        List<SecurityLevel> levels = new ArrayList<SecurityLevel>(Arrays.asList(SecurityLevel.values()));
        CollectionUtils.filter(levels, p);
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

    /**
     * @return the partialRead
     */
    public boolean isPartialRead() {
        return partialRead;
    }

    /**
     * @return the partialWrite
     */
    public boolean isPartialWrite() {
        return partialWrite;
    }
}
