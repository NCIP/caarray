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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public enum FileType implements Comparable<FileType>  {

    /**
     * Affymetrix native array design file.
     */
    AFFYMETRIX_CDF,

    /**
     * Affymetrix native CEL data format.
     */
    AFFYMETRIX_CEL,

    /**
     * Affymetrix native CHP data format.
     */
    AFFYMETRIX_CHP,

    /**
     * Affymetrix native DAT image format.
     */
    AFFYMETRIX_DAT,

    /**
     * Affymetrix EXP format.
     */
    AFFYMETRIX_EXP,

    /**
     * Affymetrix TXT format.
     */
    AFFYMETRIX_RPT,

    /**
     * Affymetrix TXT format.
     */
    AFFYMETRIX_TXT,

    /**
     * Agilent CSV format.
     */
    AGILENT_CSV,

    /**
     * Agilent TSV format.
     */
    AGILENT_TSV,

    /**
     * Agilent raw TXT format.
     */
    AGILENT_RAW_TXT,

    /**
     * Agilent derived TXT format.
     */
    AGILENT_DERIVED_TXT,

    /**
     * Agilent XML format.
     */
    AGILENT_XML,

    /**
     * Genepix array design GAL file.
     */
    GENEPIX_GAL,

    /**
     * Genepix array data GPR file.
     */
    GENEPIX_GPR,

    /**
     * Illumina raw array data file.
     */
    ILLUMINA_IDAT,

    /**
     * Illumina array data CSV file.
     */
    ILLUMINA_DATA_CSV,

    /**
     * Illumina array design CSV file.
     */
    ILLUMINA_DESIGN_CSV,

    /**
     * Illumina array data TXT file.
     */
    ILLUMINA_DATA_TXT,

    /**
     * Imagene TXT format.
     */
    IMAGENE_TXT,

    /**
     * Imagene TIF format.
     */
    IMAGENE_TIF,

    /**
     * Imagene TPL array design format.
     */
    IMAGENE_TPL,

    /**
     * Nimblegen TXT format.
     */
    NIMBLEGEN_TXT,

    /**
     * Nimblegen GFF format.
     */
    NIMBLEGEN_GFF,

    /**
     * Nimblegen NDF format.
     */
    NIMBLEGEN_NDF,

    /**
     * The MAGE_TAB Array Design Format file type.
     */
    MAGE_TAB_ADF,

    /**
     * The MAGE_TAB data file type.
     */
    MAGE_TAB_DATA_MATRIX,

    /**
     * The MAGE_TAB Investigation Description Format file type.
     */
    MAGE_TAB_IDF,

    /**
     * The MAGE_TAB Sample and Data Relationship Format file type.
     */
    MAGE_TAB_SDRF,

    /**
     * The UCSF Spot SPT array design file type.
     */
    UCSF_SPOT_SPT;

    private static final Set<FileType> ARRAY_DESIGN_FILE_TYPES = new LinkedHashSet<FileType>();
    private static final Set<FileType> PARSEABLE_ARRAY_DESIGN_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> RAW_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> DERIVED_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();
    private static final Set<FileType> PARSEABLE_ARRAY_DATA_FILE_TYPES = new HashSet<FileType>();

    static {
        ARRAY_DESIGN_FILE_TYPES.add(AFFYMETRIX_CDF);
        ARRAY_DESIGN_FILE_TYPES.add(AGILENT_CSV);
        ARRAY_DESIGN_FILE_TYPES.add(AGILENT_XML);
        ARRAY_DESIGN_FILE_TYPES.add(GENEPIX_GAL);
        ARRAY_DESIGN_FILE_TYPES.add(ILLUMINA_DESIGN_CSV);
        ARRAY_DESIGN_FILE_TYPES.add(IMAGENE_TPL);
        ARRAY_DESIGN_FILE_TYPES.add(NIMBLEGEN_NDF);
        ARRAY_DESIGN_FILE_TYPES.add(UCSF_SPOT_SPT);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(AFFYMETRIX_CDF);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(ILLUMINA_DESIGN_CSV);
        PARSEABLE_ARRAY_DESIGN_FILE_TYPES.add(GENEPIX_GAL);
        RAW_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_IDAT);
        RAW_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CEL);
        RAW_ARRAY_DATA_FILE_TYPES.add(AGILENT_RAW_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CHP);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_EXP);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_RPT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_CSV);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(GENEPIX_GPR);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(IMAGENE_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(AGILENT_DERIVED_TXT);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(NIMBLEGEN_GFF);
        DERIVED_ARRAY_DATA_FILE_TYPES.add(NIMBLEGEN_TXT);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CEL);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(AFFYMETRIX_CHP);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(ILLUMINA_DATA_CSV);
        PARSEABLE_ARRAY_DATA_FILE_TYPES.add(GENEPIX_GPR);
    }

    /**
     * @return true if this file type is an array design.
     */
    public boolean isArrayDesign() {
        return ARRAY_DESIGN_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the system supports parsing this array design format.
     */
    public boolean isParseableArrayDesign() {
        return PARSEABLE_ARRAY_DESIGN_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the system supports parsing this data format.
     */
    public boolean isParseableData() {
        return PARSEABLE_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isDerivedArrayData() {
        return DERIVED_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if the file type is used for derived array data.
     */
    public boolean isRawArrayData() {
        return RAW_ARRAY_DATA_FILE_TYPES.contains(this);
    }

    /**
     * @return true if this file type is array data.
     */
    public boolean isArrayData() {
        return isRawArrayData() || isDerivedArrayData();
    }

    /**
     * @return the enum name -- necessary for bean property access.
     */
    public String getName() {
        return name();
    }

    /**
     * @return the ArrayDesignFileTypes
     */
    public static Set<FileType> getArrayDesignFileTypes() {
        return ARRAY_DESIGN_FILE_TYPES;
    }

}
