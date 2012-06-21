/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
