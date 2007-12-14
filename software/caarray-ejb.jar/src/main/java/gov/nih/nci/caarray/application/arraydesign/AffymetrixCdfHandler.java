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
package gov.nih.nci.caarray.application.arraydesign;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.Feature;
import gov.nih.nci.caarray.domain.array.LogicalProbe;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.domain.array.ProbeGroup;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import org.apache.log4j.Logger;

import affymetrix.fusion.cdf.FusionCDFData;
import affymetrix.fusion.cdf.FusionCDFHeader;
import affymetrix.fusion.cdf.FusionCDFProbeGroupInformation;
import affymetrix.fusion.cdf.FusionCDFProbeInformation;
import affymetrix.fusion.cdf.FusionCDFProbeSetInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeInformation;
import affymetrix.fusion.cdf.FusionCDFQCProbeSetInformation;

/**
 * Contains logic to read Affymetrix CDF files.
 */
class AffymetrixCdfHandler extends AbstractArrayDesignHandler {

    private static final String LSID_AUTHORITY = "Affymetrix.com";
    private static final String LSID_NAMESPACE = "PhysicalArrayDesign";
    private static final Logger LOG = Logger.getLogger(AffymetrixCdfHandler.class);
    private static final int PROBE_SET_BATCH_SIZE = 25;

    private boolean[][] featureCreated;

    private FusionCDFData fusionCDFData;

    private ProbeGroup probeGroup;

    AffymetrixCdfHandler(CaArrayFile designFile, VocabularyService vocabularyService,
            FileAccessService fileAccessService, CaArrayDaoFactory daoFactory) {
        super(designFile, vocabularyService, fileAccessService, daoFactory);
    }

    @Override
    void validate(FileValidationResult result) {
        if (!loadFusionCDFData()) {
            if (fusionCDFData == null) {
                result.addMessage(ValidationMessage.Type.ERROR, "CDF file is missing");
            } else {
                result.addMessage(ValidationMessage.Type.ERROR,
                        "Unable to read the CDF file : " + fusionCDFData.getFileName());
            }
        }
        checkForDuplicateDesign(result);
        closeCdf();
    }

    /**
     * @param result
     */
    private void checkForDuplicateDesign(FileValidationResult result) {
        ArrayDesign existingDesign =
            getDaoFactory().getArrayDao().getArrayDesign(LSID_AUTHORITY, LSID_NAMESPACE,
                    getFusionCDFData().getChipType());
        if (existingDesign != null) {
            result.addMessage(Type.ERROR, "Affymetrix design " + getFusionCDFData().getChipType()
                    + " has already been imported");
        }
    }

    private void closeCdf() {
        // see development tracker issue #9735 for details on why system.gc() used here
        fusionCDFData.clear();
        fusionCDFData = null;
        if (System.getProperty("os.name").startsWith("Windows")) {
            System.gc();
        }
    }

    @Override
    void load(ArrayDesign arrayDesign) {
        arrayDesign.setName(getFusionCDFData().getChipType());
        arrayDesign.setLsidForEntity(LSID_AUTHORITY + ":" + LSID_NAMESPACE + ":" + getFusionCDFData().getChipType());
        int rows = getFusionCDFData().getHeader().getRows();
        int cols = getFusionCDFData().getHeader().getCols();
        arrayDesign.setNumberOfFeatures(rows * cols);
        closeCdf();
    }

    @Override
    ArrayDesignDetails createDesignDetails(ArrayDesign arrayDesign) {
        ArrayDesignDetails designDetails = new ArrayDesignDetails();
        getArrayDao().save(designDetails);
        probeGroup = new ProbeGroup(designDetails);
        probeGroup.setName(LSID_AUTHORITY + ":" + probeGroup.getClass().getSimpleName() + ":All."
                + this.getFusionCDFData().getChipType());
        getDaoFactory().getSearchDao().save(probeGroup);
        initializeFeaturesCreated(getFusionCDFHeader());

        handleProbeSets(designDetails);
        handleQCProbeSets(designDetails);
        createMissingFeatures(designDetails);
        closeCdf();
        return designDetails;
    }

    private void initializeFeaturesCreated(FusionCDFHeader fusionCDFHeader) {
        featureCreated = new boolean[fusionCDFHeader.getCols()][fusionCDFHeader.getRows()];
    }

    private void handleProbeSets(ArrayDesignDetails designDetails) {
        int numProbeSets = getFusionCDFHeader().getNumProbeSets();
        FusionCDFProbeSetInformation probeSetInformation = new FusionCDFProbeSetInformation();
        for (int index = 0; index < numProbeSets; index++) {
            fusionCDFData.getProbeSetInformation(index, probeSetInformation);
            handleProbeSet(probeSetInformation, fusionCDFData.getProbeSetName(index), designDetails);
            if (index % PROBE_SET_BATCH_SIZE == 0) {
                flushAndClearSession();
                this.probeGroup = getDaoFactory().getSearchDao().retrieve(ProbeGroup.class, probeGroup.getId());        
            }
        }
    }

    private void handleProbeSet(FusionCDFProbeSetInformation probeSetInformation, String probeSetName,
            ArrayDesignDetails designDetails) {
        LogicalProbe logicalProbe = new LogicalProbe(designDetails);
        logicalProbe.setName(probeSetName);
        getArrayDao().save(logicalProbe);
        int numLists = probeSetInformation.getNumLists();
        for (int listIndex = 0; listIndex < numLists; listIndex++) {
            PhysicalProbe probe = new PhysicalProbe(designDetails, probeGroup);
            probe.setName(probeSetName + ".ProbePair" + listIndex);
            getArrayDao().save(probe);
        }
        int numGroups = probeSetInformation.getNumGroups();
        FusionCDFProbeGroupInformation probeGroupInformation = new FusionCDFProbeGroupInformation();
        for (int index = 0; index < numGroups; index++) {
            probeSetInformation.getGroup(index, probeGroupInformation);
            handleProbeGroup(probeGroupInformation, designDetails);
        }
    }

    private void handleProbeGroup(FusionCDFProbeGroupInformation probeGroupInformation,
            ArrayDesignDetails designDetails) {
        int numCells = probeGroupInformation.getNumCells();
        FusionCDFProbeInformation probeInformation = new FusionCDFProbeInformation();
        for (int index = 0; index < numCells; index++) {
            probeGroupInformation.getCell(index, probeInformation);
            handleProbe(probeInformation, designDetails);
        }
    }

    private void handleProbe(FusionCDFProbeInformation probeInformation, ArrayDesignDetails designDetails) {
        createFeature(probeInformation.getX(), probeInformation.getY(), designDetails);
    }

    private Feature createFeature(int x, int y, ArrayDesignDetails details) {
        Feature feature = new Feature(details);
        feature.setColumn((short) x);
        feature.setRow((short) y);
        featureCreated[x][y] = true;
        getArrayDao().save(feature);
        return feature;
    }

    private void handleQCProbeSets(ArrayDesignDetails designDetails) {
        int numQCProbeSets = getFusionCDFHeader().getNumQCProbeSets();
        FusionCDFQCProbeSetInformation qcProbeSetInformation = new FusionCDFQCProbeSetInformation();
        for (int index = 0; index < numQCProbeSets; index++) {
            getFusionCDFData().getQCProbeSetInformation(index, qcProbeSetInformation);
            handleQCProbeSet(qcProbeSetInformation, designDetails);
            if (index % PROBE_SET_BATCH_SIZE == 0) {
                flushAndClearSession();
                this.probeGroup = getDaoFactory().getSearchDao().retrieve(ProbeGroup.class, probeGroup.getId());        
            }
        }
    }

    private void handleQCProbeSet(FusionCDFQCProbeSetInformation qcProbeSetInformation,
            ArrayDesignDetails designDetails) {
        int numCells = qcProbeSetInformation.getNumCells();
        FusionCDFQCProbeInformation qcProbeInformation = new FusionCDFQCProbeInformation();
        for (int index = 0; index < numCells; index++) {
            qcProbeSetInformation.getCell(index, qcProbeInformation);
            handleQCProbe(qcProbeInformation, designDetails);
        }
    }

    private void handleQCProbe(FusionCDFQCProbeInformation qcProbeInformation, ArrayDesignDetails designDetails) {
        createFeature(qcProbeInformation.getX(), qcProbeInformation.getY(), designDetails);
    }

    private void createMissingFeatures(ArrayDesignDetails designDetails) {
        for (int x = 0; x < featureCreated.length; x++) {
            for (int y = 0; y < featureCreated[x].length; y++) {
                if (!featureCreated[x][y]) {
                    createFeature(x, y, designDetails);
                }
            }
            flushAndClearSession();
            this.probeGroup = getDaoFactory().getSearchDao().retrieve(ProbeGroup.class, probeGroup.getId());
        }
    }

    private FusionCDFData getFusionCDFData() {
        if (fusionCDFData == null) {
            loadFusionCDFData();
        }
        return fusionCDFData;
    }

    private FusionCDFHeader getFusionCDFHeader() {
        return getFusionCDFData().getHeader();
    }

    private boolean loadFusionCDFData() {
        fusionCDFData = new FusionCDFData();
        fusionCDFData.setFileName(getFile().getAbsolutePath());
        return fusionCDFData.read();
    }

    @Override
    Logger getLog() {
        return LOG;
    }

}
