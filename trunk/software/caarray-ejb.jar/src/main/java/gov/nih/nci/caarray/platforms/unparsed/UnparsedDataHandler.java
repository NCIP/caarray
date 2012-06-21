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
package gov.nih.nci.caarray.platforms.unparsed;

import gov.nih.nci.caarray.dataStorage.DataStorageFacade;
import gov.nih.nci.caarray.domain.LSID;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.FileCategory;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.AbstractDataFileHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

/**
 * Handler for unparsed data formats.
 */
public class UnparsedDataHandler extends AbstractDataFileHandler {
    /**
     * FileType for Illumina IDAT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_IDAT = new FileType("ILLUMINA_IDAT", FileCategory.RAW_DATA, false,
            "IDAT");
    /**
     * FileType for Affymetrix DAT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_DAT = new FileType("AFFYMETRIX_DAT", FileCategory.RAW_DATA,
            false, "DAT");
    /**
     * FileType for Affymetrix EXP files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_EXP = new FileType("AFFYMETRIX_EXP", FileCategory.DERIVED_DATA,
            false, "EXP");
    /**
     * FileType for Affymetrix TXT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_TXT = new FileType("AFFYMETRIX_TXT", FileCategory.DERIVED_DATA,
            false);
    /**
     * FileType for Affymetrix RPT files.
     */
    public static final FileType FILE_TYPE_AFFYMETRIX_RPT = new FileType("AFFYMETRIX_RPT", FileCategory.DERIVED_DATA,
            false, "RPT");
    /**
     * FileType for Imagene TIF files.
     */
    public static final FileType FILE_TYPE_IMAGENE_TIF = new FileType("IMAGENE_TIF", FileCategory.RAW_DATA, false);
    /**
     * FileType for GEO SOFT files.
     */
    public static final FileType FILE_TYPE_GEO_SOFT = new FileType("GEO_SOFT", FileCategory.RAW_DATA, false);
    /**
     * FileType for GEO GSM files.
     */
    public static final FileType FILE_TYPE_GEO_GSM = new FileType("GEO_GSM", FileCategory.RAW_DATA, false);
    /**
     * FileType for SCANARRAY CSV files.
     */
    public static final FileType FILE_TYPE_SCANARRAY_CSV = new FileType("SCANARRAY_CSV", FileCategory.RAW_DATA, false);
    /**
     * FileType for Illumina RAW TXT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_RAW_TXT = new FileType("ILLUMINA_RAW_TXT", FileCategory.RAW_DATA,
            false);
    /**
     * FileType for Illumina DERIVED TXT files.
     */
    public static final FileType FILE_TYPE_ILLUMINA_DERIVED_TXT = new FileType("ILLUMINA_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Imagene TXT files.
     */
    public static final FileType FILE_TYPE_IMAGENE_TXT = new FileType("IMAGENE_TXT", FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Agilent DERIVED TXT files.
     */
    public static final FileType FILE_TYPE_AGILENT_DERIVED_TXT = new FileType("AGILENT_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Nimblegen GFF files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_GFF = new FileType("NIMBLEGEN_GFF", FileCategory.DERIVED_DATA,
            false, "GFF");
    /**
     * FileType for Nimblegen Derived TXT files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_DERIVED_TXT = new FileType("NIMBLEGEN_DERIVED_TXT",
            FileCategory.DERIVED_DATA, false);
    /**
     * FileType for Nimblegen Raw TXT files.
     */
    public static final FileType FILE_TYPE_NIMBLEGEN_RAW_TXT = new FileType("NIMBLEGEN_RAW_TXT", FileCategory.RAW_DATA,
            false);
    /**
     * FileType for Agilent TSV files.
     */
    public static final FileType FILE_TYPE_AGILENT_TSV = new FileType("AGILENT_TSV", FileCategory.RAW_DATA, false,
            "TSV");
    /**
     * FileType for MAGE-TAB base DATA MATRIX files.
     */
    public static final FileType FILE_TYPE_MAGE_TAB_DATA_MATRIX = new FileType("MAGE_TAB_DATA_MATRIX",
            FileCategory.RAW_DATA, false, true, "DATA");

    private static final Set<FileType> SUPPORTED_TYPES = Sets.newHashSet(FILE_TYPE_ILLUMINA_IDAT,
            FILE_TYPE_AFFYMETRIX_DAT, FILE_TYPE_AFFYMETRIX_EXP, FILE_TYPE_AFFYMETRIX_TXT, FILE_TYPE_AFFYMETRIX_RPT,
            FILE_TYPE_IMAGENE_TIF, FILE_TYPE_GEO_SOFT, FILE_TYPE_GEO_GSM, FILE_TYPE_SCANARRAY_CSV,
            FILE_TYPE_ILLUMINA_RAW_TXT, FILE_TYPE_ILLUMINA_DERIVED_TXT, FILE_TYPE_IMAGENE_TXT,
            FILE_TYPE_AGILENT_DERIVED_TXT, FILE_TYPE_NIMBLEGEN_GFF, FILE_TYPE_NIMBLEGEN_DERIVED_TXT,
            FILE_TYPE_NIMBLEGEN_RAW_TXT, FILE_TYPE_AGILENT_TSV, FILE_TYPE_MAGE_TAB_DATA_MATRIX);

    /**
     * Default constructor.
     * 
     * @param dataStorageFacade data storage facade to use for retrieving file data
     */
    @Inject
    UnparsedDataHandler(DataStorageFacade dataStorageFacade) {
        super(dataStorageFacade);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<FileType> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDataTypeDescriptor getArrayDataTypeDescriptor() {
        return UnsupportedDataFormatDescriptor.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuantitationTypeDescriptor[] getQuantitationTypeDescriptors() {
        return new QuantitationTypeDescriptor[] {};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(DataSet dataSet, List<QuantitationType> types, ArrayDesign design) {
        // no-op, data parsing not supported for the current type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(MageTabDocumentSet mTabSet, FileValidationResult result, ArrayDesign design) {
        // no-op, data parsing not supported for the current type
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean requiresMageTab() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LSID> getReferencedArrayDesignCandidateIds() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean parsesData() {
        return false;
    }
}
