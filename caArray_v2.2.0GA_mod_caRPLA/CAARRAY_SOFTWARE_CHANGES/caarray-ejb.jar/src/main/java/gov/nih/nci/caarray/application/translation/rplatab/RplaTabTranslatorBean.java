//======================================================================================
// Copyright 5AM Solutions Inc, Yale University, Lawrence Berkeley National Laboratory
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/caRPLA/LICENSE for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.rplatab;

import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.util.io.logging.LogUtil;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorFactory;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

/**
 * Implementation of the RPLA-TAB translation component.
 */
@Local
@Stateless
public class RplaTabTranslatorBean implements RplaTabTranslator {

	private static final Logger	LOG			= Logger.getLogger(RplaTabTranslatorBean.class);

	private CaArrayDaoFactory	daoFactory	= CaArrayDaoFactory.INSTANCE;

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RplaTabTranslationResult  translate ( RplaTabDocumentSet documentSet,
												CaArrayFileSet fileSet)
	{
		LogUtil.logSubsystemEntry(LOG, documentSet);
		LOG.info("");
		RplaTabTranslationResult translationResult = new RplaTabTranslationResult();
		translateTermSources(documentSet, translationResult);
		translateTerms(documentSet, translationResult, getVocabularyService());

		translateRplaIdfs(documentSet, translationResult);
		translateSradfs(documentSet, fileSet, translationResult);
		LogUtil.logSubsystemExit(LOG);
		return translationResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ValidationResult validate (	RplaTabDocumentSet documentSet,
										CaArrayFileSet fileSet)
	{

		LOG.info("carplafix: validate:  ");
		// validateSdrfs(documentSet, fileSet);

		return documentSet.getValidationResult();
	}

	private void translateTermSources ( RplaTabDocumentSet documentSet,
										RplaTabTranslationResult translationResult)
	{
		new TermSourceTranslator(	documentSet,
									translationResult,
									getVocabularyService(),
									this.daoFactory).translate();
	}

	//
	private void translateTerms (	RplaTabDocumentSet documentSet,
									RplaTabTranslationResult translationResult,
									VocabularyService service)
	{
		new TermTranslator(	documentSet,
							translationResult,
							service,
							this.daoFactory).translate();
	}

	//
	private void translateRplaIdfs (	RplaTabDocumentSet documentSet,
										RplaTabTranslationResult translationResult)
	{
		new RplaIdfTranslator(documentSet, translationResult, this.daoFactory).translate();
	}

	//
	private void translateSradfs (	RplaTabDocumentSet documentSet,
									CaArrayFileSet fileSet,
									RplaTabTranslationResult translationResult)
	{
		new SradfTranslator(documentSet,
							fileSet,
							translationResult,
							this.daoFactory,
							getVocabularyService()).translate();
	}

	//
	// private void validateSdrfs(MageTabDocumentSet documentSet, CaArrayFileSet
	// fileSet) {
	// new SdrfTranslator(documentSet, fileSet, null, this.daoFactory,
	// getVocabularyService()).
	// validate();
	// }

	private VocabularyService getVocabularyService () {
		return (VocabularyService) ServiceLocatorFactory.getLocator()
														.lookup(VocabularyService.JNDI_NAME);
	}

	/**
	 * @return the daoFactory
	 */
	CaArrayDaoFactory getDaoFactory () {
		return this.daoFactory;
	}

	/**
	 * @param daoFactory
	 *            the daoFactory to set
	 */
	void setDaoFactory ( CaArrayDaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}
