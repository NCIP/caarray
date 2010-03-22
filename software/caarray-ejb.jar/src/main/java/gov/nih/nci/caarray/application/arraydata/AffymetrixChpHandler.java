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
package gov.nih.nci.caarray.application.arraydata;

import affymetrix.fusion.chp.FusionCHPData;
import affymetrix.fusion.chp.FusionCHPDataReg;
import affymetrix.fusion.chp.FusionCHPHeader;
import affymetrix.fusion.chp.FusionCHPLegacyData;
import affymetrix.fusion.chp.FusionCHPMultiDataData;
import affymetrix.fusion.chp.FusionCHPQuantificationData;
import gov.nih.nci.caarray.application.arraydesign.AbstractAffymetrixChpDesignElementListUtility;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataSet;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.HybridizationData;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Array data handler for all versions and types of the Affymetrix CHP file format.
 */
final class AffymetrixChpHandler extends AbstractDataFileHandler {

    private static final Logger LOG = Logger.getLogger(AffymetrixChpHandler.class);
    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE_DESIGN = "PhysicalArrayDesign";

    AffymetrixChpHandler() {
        FusionCHPLegacyData.registerReader();
        FusionCHPQuantificationData.registerReader();
        FusionCHPMultiDataData.registerReader();
    }

    @Override
    QuantitationTypeDescriptor[] getQuantitationTypeDescriptors(final File chpFile) {
        final AbstractCHPData chpData = getChpData(chpFile);
        return chpData.getQuantitationTypeDescriptors();
    }

    @Override
    void loadData(final DataSet dataSet, final List<QuantitationType> types, final File file,
            final ArrayDesignService arrayDesignService) {
        final Set<QuantitationType> typeSet = new HashSet<QuantitationType>();
        typeSet.addAll(types);
        if (dataSet.getDesignElementList() == null) {
            getDesignElementList(dataSet, file, arrayDesignService);
        }
        final AbstractCHPData chpData = getChpData(file);
        prepareColumns(dataSet, types, chpData.getNumProbeSets());
        final HybridizationData hybridizationData = dataSet.getHybridizationDataList().get(0);
        chpData.loadData(typeSet, hybridizationData);
    }

    private void getDesignElementList(final DataSet dataSet, final File file,
            final ArrayDesignService arrayDesignService) {
        final ArrayDesign design = getArrayDesign(arrayDesignService, file);
        final DesignElementList probeList =
            AbstractAffymetrixChpDesignElementListUtility.getDesignElementList(design, arrayDesignService);
        dataSet.setDesignElementList(probeList);
    }

    @Override
    void validate(final CaArrayFile caArrayFile, final File file, final MageTabDocumentSet mTabSet,
            final FileValidationResult result, final ArrayDesignService arrayDesignService) {
        final AbstractCHPData chpData = getChpData(file);
        if (chpData == null) {
            result.addMessage(Type.ERROR, "Couldn't read Affymetrix CHP file: " + file.getName());
        } else {
            validateAgainstDesign(chpData, result, arrayDesignService);
        }
    }

    private void validateAgainstDesign(final AbstractCHPData chpData, final FileValidationResult result,
            final ArrayDesignService arrayDesignService) {
        final String lsidObjectId = chpData.getChipType();
        if (arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE_DESIGN, lsidObjectId) == null) {
            result.addMessage(Type.ERROR, "The system doesn't contain the required Affymetrix array design: "
                    + lsidObjectId);
        }
    }

    private AbstractCHPData getChpData(final File file) {
        AbstractCHPData chpData = null;
        final FusionCHPData data = FusionCHPDataReg.read(file.getAbsolutePath());
            
        if (data instanceof FusionCHPLegacyData) {
            chpData = getChpLegacyData(data);
        } else if (data instanceof FusionCHPQuantificationData) {
            chpData = getChpExpressionSignalData(data);
        } else if (data instanceof FusionCHPMultiDataData) {
            chpData = getChpMultiDataData(data);
        }
         
        if (chpData == null) {
            throw new IllegalArgumentException("Unsupported Affymetrix CHP type");
        }

        return chpData;
   }

    private AbstractCHPData getChpLegacyData(final FusionCHPData fusionCHPData) {
        final FusionCHPLegacyData fusionCHPLegacyData = FusionCHPLegacyData.fromBase(fusionCHPData);
        final int assayType = fusionCHPLegacyData.getHeader().getAssayType();
        switch (assayType) {
        case FusionCHPHeader.EXPRESSION_ASSAY:
            return new CHPExpressionMAS5Data(fusionCHPLegacyData);
        case FusionCHPHeader.GENOTYPING_ASSAY:
            return new CHPSNPData(fusionCHPLegacyData);
        default:
            return null;
        }
    }

    private AbstractCHPData getChpExpressionSignalData(final FusionCHPData fusionCHPData) {
        final FusionCHPQuantificationData fusionCHPQuantificationData 
            = FusionCHPQuantificationData.fromBase(fusionCHPData);
        return new CHPExpressionSignalData(fusionCHPQuantificationData);
    }

    private AbstractCHPData getChpMultiDataData(final FusionCHPData fusionCHPData) {
        final FusionCHPMultiDataData fusionCHPMultiDataData 
            = FusionCHPMultiDataData.fromBase(fusionCHPData);
        
        final String algorithm = fusionCHPMultiDataData.getAlgName();
        String dataSetName = fusionCHPMultiDataData.getGenericData().findDataGroupHeader(0).getDataSet(0).getName();
        
        boolean algorihmIsBRLMM = algorithm.startsWith("brlmm");
        boolean algorihmIsBirdseed = algorithm.startsWith("birdseed");
        boolean algorihmIsAxiomGT = algorithm.startsWith("axiomgt");

        if (algorihmIsBRLMM) {
            return new CHPSnpBrlmmData(fusionCHPMultiDataData);
        } else if (algorihmIsBirdseed) {
            return new CHPSnpBirdseedData(fusionCHPMultiDataData);
        } else if (algorihmIsAxiomGT) {
            return new CHPSnpAxiomGTData(fusionCHPMultiDataData);
        }

        return null;
    }

    @Override
    Logger getLog() {
        return LOG;
    }

    @Override
    ArrayDataTypeDescriptor getArrayDataTypeDescriptor(final File dataFile) {
        final AbstractCHPData chpData = getChpData(dataFile);
        return chpData.getArrayDataTypeDescriptor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayDesign getArrayDesign(final ArrayDesignService arrayDesignService, final File file) {
        final String lsidObjectId = getChpData(file).getChipType();
        return arrayDesignService.getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE_DESIGN, lsidObjectId);
    }

}
