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
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.ParameterValue;
import gov.nih.nci.caarray.magetab.TermSource;

import java.util.EnumSet;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of legal column headings in an SDRF document.
 */
public enum SdrfColumnType {
    /**
     * Source Name.
     */
    SOURCE_NAME(Source.class, "Source Name"),

    /**
     * Sample Name.
     */
    SAMPLE_NAME(Sample.class, "Sample Name"),

    /**
     * Extract Name.
     */
    EXTRACT_NAME(Extract.class, "Extract Name"),

    /**
     * Labeled Extract Name.
     */
    LABELED_EXTRACT_NAME(LabeledExtract.class, "Labeled Extract Name"),

    /**
     * Hybridization Name.
     */
    HYBRIDIZATION_NAME(Hybridization.class, "Hybridization Name"),

    /**
     * Scan Name.
     */
    SCAN_NAME(Scan.class, "Scan Name"),

    /**
     * Normalization Name.
     */
    NORMALIZATION_NAME(Normalization.class, "Normalization Name"),

    /**
     * Array Data File.
     */
    ARRAY_DATA_FILE(ArrayDataFile.class, "Array Data File"),

    /**
     * Derived Array Data File.
     */
    DERIVED_ARRAY_DATA_FILE(DerivedArrayDataFile.class, "Derived Array Data File"),

    /**
     * Array Data Matrix File.
     */
    ARRAY_DATA_MATRIX_FILE(ArrayDataMatrixFile.class, "Array Data Matrix File"),

    /**
     * Derived Array Data Matrix File.
     */
    DERIVED_ARRAY_DATA_MATRIX_FILE(DerivedArrayDataMatrixFile.class, "Derived Array Data Matrix File"),

    /**
     * Image File.
     */
    IMAGE_FILE(Image.class, "Image File"),


    /**
     * Array Design REF.
     */
    ARRAY_DESIGN_REF (ArrayDesign.class, "Array Design REF"),

    /**
     * Array Design File.
     */
    ARRAY_DESIGN_FILE (ArrayDesign.class, "Array Design File"),

    /**
     * Protocol REF.
     */
    PROTOCOL_REF ("Protocol REF"),

    /**
     * Characteristics.
     */
    CHARACTERISTICS (Characteristic.class, "Characteristics"),

    /**
     * Provider.
     */
    PROVIDER ("Provider"),

    /**
     * Material Type.
     */
    MATERIAL_TYPE (OntologyTerm.class, "Material Type"),

    /**
     * Label.
     */
    LABEL ("Label"),

    /**
     * Factor Value.
     */
    FACTOR_VALUE ("Factor Value"),

    /**
     * Performer.
     */
    PERFORMER ("Performer"),

    /**
     * Date.
     */
    DATE ("Date"),

    /**
     * Parameter Value.
     */
    PARAMETER_VALUE (ParameterValue.class, "Parameter Value"),

    /**
     * Unit.
     */
    UNIT ("Unit"),

    /**
     * Description.
     */
    DESCRIPTION ("Description"),

    /**
     * Term Source REF.
     */
    TERM_SOURCE_REF (TermSource.class, "Term Source REF"),

    /**
     * Comment.
     */
    COMMENT (Comment.class, "Comment");

    private static final EnumSet<SdrfColumnType> BIOMATERIAL_NODES = EnumSet.of(SOURCE_NAME, SAMPLE_NAME, EXTRACT_NAME,
            LABELED_EXTRACT_NAME);

    private static final EnumSet<SdrfColumnType> DATA_FILE_NODES = EnumSet.of(ARRAY_DATA_FILE, ARRAY_DATA_MATRIX_FILE,
            DERIVED_ARRAY_DATA_FILE, DERIVED_ARRAY_DATA_MATRIX_FILE);

    private static final EnumSet<SdrfColumnType> NODES = EnumSet.of(SOURCE_NAME, SAMPLE_NAME, EXTRACT_NAME,
            LABELED_EXTRACT_NAME, HYBRIDIZATION_NAME, SCAN_NAME, IMAGE_FILE, ARRAY_DATA_FILE, ARRAY_DATA_MATRIX_FILE,
            NORMALIZATION_NAME, DERIVED_ARRAY_DATA_FILE, DERIVED_ARRAY_DATA_MATRIX_FILE);
    
    private static final EnumSet<SdrfColumnType> TERM_SOURCEABLES = EnumSet.of(PROTOCOL_REF, CHARACTERISTICS, 
            MATERIAL_TYPE, UNIT, LABEL, ARRAY_DESIGN_REF, PARAMETER_VALUE, FACTOR_VALUE);

    private final Class<?> nodeClass;
    private final String displayName;

    /**
     * An SDRF column header with the specified display name.
     *
     * @param displayName the actual name of the column that will be displayed.
     */
    SdrfColumnType(String displayName) {
        this(null, displayName);
    }

    /**
     * An SDRF column header with the specified class and display name.
     *
     * @param nodeClass the Java class that represents this column.
     * @param displayName the actual name of the column that will be displayed.
     */
    SdrfColumnType(Class<?> nodeClass, String displayName) {
        this.nodeClass = nodeClass;
        this.displayName = displayName;
    }

    static SdrfColumnType get(String name) {
        String enumName = StringUtils.replaceChars(name, ' ', '_').toUpperCase();
        return valueOf(enumName);
    }

    Class<?> getNodeClass() {
        return nodeClass;
    }

    /**
     * @return whether a column of this type can be followed by a Term Source REF column.
     */
    public boolean isTermSourceable() {
        return TERM_SOURCEABLES.contains(this);
    }

    /**
     * @return whether a column of this type is a sample-relationship graph node (a biomaterial, hybridization or data
     *         product column)
     */
    public boolean isNode() {
        return NODES.contains(this);
    }

    /**
     * @return whether a column of this type is a biomaterial graph node
     */
    public boolean isBiomaterialNode() {
        return BIOMATERIAL_NODES.contains(this);
    }

    /**
     * @return whether a column of this type is a data file graph node
     */
    public boolean isDataFileNode() {
        return DATA_FILE_NODES.contains(this);
    }

    /**
     * Returns the display name of the column.
     *
     * @return the display name of the column.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return displayName;
    }
}
