/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caArray
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caArray Software License (the License) is between NCI and You. You (or
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
 * its rights in the caArray Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caArray Software; (ii) distribute and
 * have distributed to and by third parties the caArray Software and any
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

package gov.nih.nci.caarray.domain.file;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.inject.internal.Sets;

/**
 * Describes a type of file that can be uploaded to caArray. This is not a persistent bean - instances of this class are
 * registered with FileTypeRegistry by platform plugins which know how to parse particular file types.
 */
public class FileType implements Comparable<FileType> {
    private String name;
    private Set<String> extensions = Sets.newHashSet();
    private FileCategory category;
    private boolean parsed;
    private boolean dataMatrix;

    /**
     * create an uninitialized file type. mostly for dozer and other tooling frameworks, should avoid using in client
     * code.
     */
    public FileType() {
        // no-op
    }

    /**
     * Constructs a file type with given properties.
     * 
     * @param name type name. Names should be unique across all types registered in the type registry
     * @param extensions the extensions associated with this type. files whose names end in these extensions should be
     *            considered to have this type.
     * @param category the category to which files of this type belong.
     * @param parsed whether this type can be parsed
     */
    public FileType(String name, FileCategory category, boolean parsed, String... extensions) {
        this.name = name;
        this.category = category;
        this.parsed = parsed;
        this.extensions.addAll(Arrays.asList(extensions));
    }

    /**
     * Constructs a file type with given properties.
     * 
     * @param name type name. Names should be unique across all types registered in the type registry
     * @param extensions the extensions associated with this type. files whose names end in these extensions should be
     *            considered to have this type.
     * @param category the category to which files of this type belong.
     * @param parsed whether this type can be parsed
     * @param dataMatrix whether files of this type are data matrices
     */
    public FileType(String name, FileCategory category, boolean parsed, boolean dataMatrix, String... extensions) {
        this(name, category, parsed, extensions);
        this.dataMatrix = dataMatrix;
    }

    /**
     * @return whether this is a data matrix type
     */
    public boolean isDataMatrix() {
        return this.dataMatrix;
    }

    /**
     * @param dataMatrix the dataMatrix to set
     */
    public void setDataMatrix(boolean dataMatrix) {
        this.dataMatrix = dataMatrix;
    }

    /**
     * @return the extensions associated with this type
     */
    public Set<String> getExtensions() {
        return this.extensions;
    }

    /**
     * @param extensions the extensions to set
     */
    public void setExtensions(Set<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return the category to which files of this type belong
     */
    public FileCategory getCategory() {
        return this.category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(FileCategory category) {
        this.category = category;
    }

    /**
     * @return the name of the type
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return whether files of this type can be parsed
     */
    public boolean isParsed() {
        return this.parsed;
    }

    /**
     * @param parsed the parsed to set
     */
    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    /**
     * @return true if this file type is an array design.
     */
    public boolean isArrayDesign() {
        return this.category == FileCategory.ARRAY_DESIGN;
    }

    /**
     * @return true if the system supports parsing this array design format.
     */
    public boolean isParseableArrayDesign() {
        return isArrayDesign() && isParsed();
    }

    /**
     * @return true if the system supports parsing this data format.
     */
    public boolean isParseableData() {
        return isArrayData() && isParsed();
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isDerivedArrayData() {
        return this.category == FileCategory.DERIVED_DATA;
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isRawArrayData() {
        return this.category == FileCategory.RAW_DATA;
    }

    /**
     * @return true if the file type is used for mage tab annotations.
     */
    public boolean isMageTab() {
        return this.category == FileCategory.MAGE_TAB;
    }

    /**
     * @return true if this file type is array data.
     */
    public boolean isArrayData() {
        return isRawArrayData() || isDerivedArrayData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FileType)) {
            return false;
        }
        final FileType other = (FileType) obj;
        return new EqualsBuilder().append(this.name, other.name).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.name).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(FileType ft) {
        return new CompareToBuilder().append(this.name, ft.name).toComparison();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
