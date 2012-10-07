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

import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.platforms.spi.DataFileHandler;
import gov.nih.nci.caarray.platforms.spi.PlatformFileReadException;
import gov.nih.nci.caarray.platforms.unparsed.FallbackUnparsedDataHandler;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Helper class for validating array data files.
 * 
 * @author dkokotov
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
final class DataFileValidator extends AbstractArrayDataUtility {
    private static final Logger LOG = Logger.getLogger(DataFileValidator.class);

    @Inject
    DataFileValidator(ArrayDao arrayDao, Set<DataFileHandler> handlers,
            Provider<FallbackUnparsedDataHandler> fallbackHandlerProvider) {
        super(arrayDao, handlers, fallbackHandlerProvider);
    }

    void validate(CaArrayFile caArrayFile, MageTabDocumentSet mTabSet, boolean reimport) {
        DataFileHandler handler = null;
        try {
            final FileValidationResult result = new FileValidationResult();
            
            try {
                handler = findAndSetupHandler(caArrayFile, mTabSet);
                assert handler != null : "findAndSetupHandler must never return null";
                if (!reimport && handler.requiresMageTab()) {
                    validateMageTabPresent(mTabSet, result);
                }
                if (result.isValid()) {
                    final ArrayDesign design = getArrayDesign(caArrayFile, handler);
                    if (design != null && design.isUnparsedAndReimportable()) {
                        result.addMessage(Type.ERROR, "Associated array design " + design.getName()
                                + " must be re-parsed");
                    } else {
                        handler.validate(mTabSet, result, design);
                        if (result.isValid()) {
                            validateArrayDesignInExperiment(caArrayFile, result, handler);
                        }
                    }
                }
            } catch (final PlatformFileReadException e) {
                LOG.error("Error obtaining a data handler for validating data file", e);
                result.addMessage(Type.ERROR, "File is not a valid file of type " + caArrayFile.getFileType() + ": "
                        + e.getMessage());
            } catch (final RuntimeException e) {
                LOG.error("Unexpected RuntimeException validating data file", e);
                result.addMessage(Type.ERROR, "Unexpected error validating data file: " + e.getMessage());
            }
            caArrayFile.setValidationResult(result);
            if (result.isValid()) {
                caArrayFile.setFileStatus(
                        handler.parsesData() ? FileStatus.VALIDATED : FileStatus.VALIDATED_NOT_PARSED);
            } else {
                caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            }
            getArrayDao().save(caArrayFile);
        } finally {
            if (handler != null) {
                handler.closeFiles();
            }
        }
    }

    private void validateMageTabPresent(MageTabDocumentSet mTabSet, FileValidationResult result) {
        if (mTabSet == null || mTabSet.getIdfDocuments().isEmpty() || mTabSet.getSdrfDocuments().isEmpty()) {
            result.addMessage(Type.ERROR, "An IDF and SDRF must be provided for this data file type.");
        }
    }

    private void validateArrayDesignInExperiment(CaArrayFile caArrayFile, FileValidationResult result,
            DataFileHandler handler) throws PlatformFileReadException {
        final ArrayDesign design = getArrayDesign(caArrayFile, handler);
        if (design == null) {
            Experiment experiment = caArrayFile.getProject().getExperiment();
            if (experiment.getArrayDesigns().size() > 1) {
                result.addMessage(Type.ERROR, "This experiment has multiple array designs. "
                        + "Please explicitly associate this data file with the appropriate array design in the sdrf");
            } else if (caArrayFile.getFileType().isParseableData()) {
                result.addMessage(Type.ERROR, "The array design referenced by this data file could not be found.");
            }
        } else if (!caArrayFile.getProject().getExperiment().getArrayDesigns().contains(design)) {
            result.addMessage(Type.ERROR, "The array design referenced by this data file (" + design.getName()
                    + ") is not associated with this experiment");
        }
    }
}
