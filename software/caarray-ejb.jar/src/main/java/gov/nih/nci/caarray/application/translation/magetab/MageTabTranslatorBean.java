//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.validation.ValidationResult;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

/**
 * Implementation of the MAGE-TAB translation component.
 */
@Local(MageTabTranslator.class)
@Stateless
public class MageTabTranslatorBean implements MageTabTranslator {

    private static final Logger LOG = Logger.getLogger(MageTabTranslatorBean.class);

    private CaArrayDaoFactory daoFactory = CaArrayDaoFactory.INSTANCE;
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CaArrayTranslationResult translate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet) {
        LogUtil.logSubsystemEntry(LOG, documentSet);
        MageTabTranslationResult translationResult = new MageTabTranslationResult();
        translateTermSources(documentSet, translationResult);
        translateTerms(documentSet, translationResult, getVocabularyService());
        translateIdfs(documentSet, translationResult);
        translateSdrfs(documentSet, fileSet, translationResult);
        LogUtil.logSubsystemExit(LOG);
        return translationResult;
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public ValidationResult validate(MageTabDocumentSet documentSet, CaArrayFileSet fileSet) {
        validateIdfFile(documentSet);
        validateSdrfs(documentSet, fileSet);
        return documentSet.getValidationResult();
    }

    private void translateTermSources(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult) {
        new TermSourceTranslator(documentSet, translationResult, getVocabularyService(), this.daoFactory).translate();
    }

    private void translateTerms(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            VocabularyService service) {
        new TermTranslator(documentSet, translationResult, service, this.daoFactory).translate();
    }

    private void translateIdfs(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult) {
        new IdfTranslator(documentSet, translationResult, this.daoFactory).translate();
    }

    private void translateSdrfs(MageTabDocumentSet documentSet, CaArrayFileSet fileSet,
            MageTabTranslationResult translationResult) {
        new SdrfTranslator(documentSet, fileSet, translationResult, this.daoFactory, getVocabularyService()).
                translate();
    }

    private void validateSdrfs(MageTabDocumentSet documentSet, CaArrayFileSet fileSet) {
        new SdrfTranslator(documentSet, fileSet, null, this.daoFactory, getVocabularyService()).
                validate();
    }

    private void validateIdfFile(MageTabDocumentSet mageTabDocumentSet) {
        new IdfTranslator(mageTabDocumentSet, null, this.daoFactory).validate();
    }

    private VocabularyService getVocabularyService() {
        return (VocabularyService) ServiceLocatorFactory.getLocator().lookup(VocabularyService.JNDI_NAME);
    }

    /**
     * @param daoFactory the daoFactory to set
     */
    void setDaoFactory(CaArrayDaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
