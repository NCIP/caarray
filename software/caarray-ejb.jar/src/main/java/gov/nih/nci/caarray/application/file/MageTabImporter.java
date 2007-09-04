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
package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabInputFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationResult;

import java.io.File;

/**
 * Responsible for importing parsed MAGE-TAB data into caArray.
 */
class MageTabImporter {
    private final FileAccessService fileAccessService;
    private final CaArrayDaoFactory daoFactory;
    private final MageTabTranslator translator;
    private final VocabularyService vocabularyService;

    MageTabImporter(FileAccessService fileAccessService, VocabularyService vocabularyService,
            MageTabTranslator translator, CaArrayDaoFactory daoFactory) {
        this.fileAccessService = fileAccessService;
        this.vocabularyService = vocabularyService;
        this.translator = translator;
        this.daoFactory = daoFactory;
    }

    void validateFiles(CaArrayFileSet fileSet) {
        updateFileStatus(fileSet, FileStatus.VALIDATING);
        MageTabInputFileSet inputSet = getInputFileSet(fileSet);
        try {
            updateFileStatus(fileSet, FileStatus.VALIDATED);
            handleResult(fileSet, MageTabParser.INSTANCE.validate(inputSet));
        } catch (MageTabParsingException e) {
            updateFileStatus(fileSet, FileStatus.VALIDATION_ERRORS);
        }
    }

    private void handleResult(CaArrayFileSet fileSet, ValidationResult result) {
        for (FileValidationResult fileValidationResult : result.getFileValidationResults()) {
            CaArrayFile caArrayFile = fileSet.getFile(fileValidationResult.getFile());
            if (!result.isValid()) {
                caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
            } else {
                caArrayFile.setFileStatus(FileStatus.VALIDATED);
            }
            caArrayFile.setValidationResult(fileValidationResult);
        }
    }

    void importFiles(Project targetProject, CaArrayFileSet fileSet) throws MageTabParsingException {
        updateFileStatus(fileSet, FileStatus.IMPORTING);
        MageTabInputFileSet inputSet = getInputFileSet(fileSet);
        MageTabDocumentSet documentSet;
        try {
            documentSet = MageTabParser.INSTANCE.parse(inputSet);
            CaArrayTranslationResult translationResult = translator.translate(documentSet, fileSet);
            save(targetProject, translationResult);
            updateFileStatus(fileSet, FileStatus.IMPORTED);
        } catch (InvalidDataException e) {
            handleInvalidMageTab(fileSet, e);
        }
    }

    private void handleInvalidMageTab(CaArrayFileSet fileSet, InvalidDataException e) {
        ValidationResult validationResult = e.getValidationResult();
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            File file = getFile(caArrayFile);
            FileValidationResult fileValidationResult = validationResult.getFileValidationResult(file);
            if (fileValidationResult != null) {
                handleValidationResult(caArrayFile, fileValidationResult);
            }
        }
    }

    private void handleValidationResult(CaArrayFile caArrayFile, FileValidationResult fileValidationResult) {
        if (fileValidationResult.isValid()) {
            caArrayFile.setFileStatus(FileStatus.VALIDATED);
        } else {
            caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
        }
        caArrayFile.setValidationResult(fileValidationResult);
        daoFactory.getProjectDao().save(caArrayFile);
    }

    private void updateFileStatus(CaArrayFileSet fileSet, FileStatus status) {
        for (CaArrayFile file : fileSet.getFiles()) {
            if (isMageTabFile(file)) {
                file.setFileStatus(status);
                getProjectDao().save(file);
            }
        }
    }

    private boolean isMageTabFile(CaArrayFile file) {
        return FileType.MAGE_TAB_ADF.equals(file.getType())
        || FileType.MAGE_TAB_DATA_MATRIX.equals(file.getType())
        || FileType.MAGE_TAB_IDF.equals(file.getType())
        || FileType.MAGE_TAB_SDRF.equals(file.getType());
    }

    private MageTabInputFileSet getInputFileSet(CaArrayFileSet fileSet) {
        MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
        for (CaArrayFile caArrayFile : fileSet.getFiles()) {
            addInputFile(inputFileSet, caArrayFile);
        }
        return inputFileSet;
    }

    private void addInputFile(MageTabInputFileSet inputFileSet, CaArrayFile caArrayFile) {
        FileType type = caArrayFile.getType();
        if (FileType.MAGE_TAB_IDF.equals(type)) {
            inputFileSet.addIdf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_SDRF.equals(type)) {
            inputFileSet.addSdrf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_ADF.equals(type)) {
            inputFileSet.addAdf(getFile(caArrayFile));
        } else if (FileType.MAGE_TAB_DATA_MATRIX.equals(type)) {
            inputFileSet.addDataMatrix(getFile(caArrayFile));
        } else {
            inputFileSet.addNativeData(getFile(caArrayFile));
        }
    }

    private File getFile(CaArrayFile caArrayFile) {
        return fileAccessService.getFile(caArrayFile);
    }

    private void save(Project targetProject, CaArrayTranslationResult translationResult) {
        saveTerms(translationResult);
        saveArrayDesigns(translationResult);
        saveInvestigations(targetProject, translationResult);
    }

    private void saveTerms(CaArrayTranslationResult translationResult) {
        // TODO Better handling of sources and categories (don't look up each and every time)
        for (Term term : translationResult.getTerms()) {
            TermSource source = getActualSource(term.getSource().getName());
            term.setSource(source);
            term.setCategory(getActualCategory(source, term.getCategory().getName()));
            if (isNew(term)) {
                vocabularyService.createTerm(term.getSource(), term.getCategory(), term.getValue());
            }
        }
    }

    private TermSource getActualSource(String name) {
        TermSource source = vocabularyService.getSource(name);
        if (source == null) {
            source = vocabularyService.createSource(name);
        }
        return source;
    }

    private Category getActualCategory(TermSource source, String name) {
        Category category = vocabularyService.getCategory(source, name);
        if (category == null) {
            category = vocabularyService.createCategory(source, name);
        }
        return category;
    }

    private boolean isNew(Term term) {
        return vocabularyService.getTerm(term.getSource(), term.getCategory(), term.getValue()) == null;
    }

    private void saveArrayDesigns(CaArrayTranslationResult translationResult) {
        // TODO import array design content
        getCaArrayDao().save(translationResult.getArrayDesigns());
    }

    private void saveInvestigations(Project targetProject, CaArrayTranslationResult translationResult) {
        // TODO Handle case where multiple IDFs exist: either disallow or allow Project 1 --> 1..* Investigation
        if (!translationResult.getInvestigations().isEmpty()) {
            targetProject.setExperiment(translationResult.getInvestigations().iterator().next());
            getProjectDao().save(targetProject);
        }
        getCaArrayDao().save(translationResult.getInvestigations());
    }

    private CaArrayDao getCaArrayDao() {
        return getProjectDao();
    }

    private ProjectDao getProjectDao() {
        return daoFactory.getProjectDao();
    }

}
