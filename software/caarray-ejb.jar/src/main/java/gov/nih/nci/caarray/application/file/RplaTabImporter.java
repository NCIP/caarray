package gov.nih.nci.caarray.application.file;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
import gov.nih.nci.caarray.application.translation.CaArrayTranslationResult;
import gov.nih.nci.caarray.application.translation.magetab.MageTabTranslator;

import gov.nih.nci.caarray.application.translation.rplatab.RplaTabTranslationResult;
import gov.nih.nci.caarray.application.translation.rplatab.RplaTabTranslator;
import gov.nih.nci.caarray.dao.CaArrayDao;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.dao.ProjectDao;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentContact;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabInputFileSet;
import gov.nih.nci.caarray.magetab.MageTabParser;
import gov.nih.nci.caarray.magetab.MageTabParsingException;
import gov.nih.nci.caarray.validation.FileValidationResult;
import gov.nih.nci.caarray.validation.InvalidDataException;
import gov.nih.nci.caarray.validation.ValidationMessage;
import gov.nih.nci.caarray.validation.ValidationResult;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSet;
import gov.nih.nci.carpla.rplatab.RplaTabDocumentSetParser;
import gov.nih.nci.carpla.rplatab.RplaTabInputFileSet;
import gov.nih.nci.carpla.rplatab.RplaTabParsingException;
import gov.nih.nci.carpla.rplatab.files.RplaIdfFile;
import gov.nih.nci.carpla.rplatab.files.SradfFile;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class RplaTabImporter {

	private static final Logger		LOG	= Logger.getLogger(RplaTabImporter.class);

	private final CaArrayDaoFactory	daoFactory;
	private final RplaTabTranslator	translator;

	// #######################################################################
	RplaTabImporter(RplaTabTranslator translator, CaArrayDaoFactory daoFactory) {
		this.translator = translator;
		this.daoFactory = daoFactory;
	}

	// #######################################################################
	void validateFiles ( CaArrayFileSet fileSet) {
		LOG.info("carplafix");

		LOG.info("Validating RPLA-TAB document set");

		updateFileStatus(fileSet, FileStatus.VALIDATING);

		RplaTabInputFileSet inputSet = getInputFileSet(fileSet);
		try {
			updateFileStatus(fileSet, FileStatus.VALIDATED);

			LOG.info("calling RPLA-TAB validate document set");

			ValidationResult vres = RplaTabDocumentSetParser.INSTANCE.validate(inputSet);

			handleResult(fileSet, vres);
			// basically parsing twice
			RplaTabDocumentSet documentSet = null;

			if (!fileSet.statusesContains(FileStatus.VALIDATION_ERRORS)) {

				List<ValidationMessage> validationMessages = vres.getMessages();
				for (ValidationMessage vm : validationMessages) {

					LOG.info(vm.getMessage());

				}
				documentSet = RplaTabDocumentSetParser.INSTANCE.parse(inputSet);
				handleResult(fileSet, translator.validate(documentSet, fileSet));
			}

			else {
				LOG.info("FileStatus.VALIDATION_ERRORS were found.");

				List<ValidationMessage> validationMessages = vres.getMessages();
				for (ValidationMessage vm : validationMessages) {

					LOG.info(vm.getMessage());

				}

			}

		} catch (RplaTabParsingException e) {
			updateFileStatus(fileSet, FileStatus.VALIDATION_ERRORS);
		} catch (InvalidDataException e) {
			handleInvalidRplaTab(fileSet, e);
		}
	}

	// #######################################################################
	private void handleResult ( CaArrayFileSet fileSet, ValidationResult result)
	{
		for (FileValidationResult fileValidationResult : result.getFileValidationResults()) {
			CaArrayFile caArrayFile = fileSet.getFile(fileValidationResult.getFile());
			if (!result.isValid()) {
				caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
				LOG.info("setting with FileStatus.VALIDATION_ERRORS : " + caArrayFile.getName());
			} else {
				caArrayFile.setFileStatus(FileStatus.VALIDATED);
				LOG.info("setting with FileStatus.VALIDATED : " + caArrayFile.getName());
			}
			caArrayFile.setValidationResult(fileValidationResult);
		}
	}
	// #######################################################################
	// #######################################################################
	void importFiles ( Project targetProject, CaArrayFileSet fileSet) throws RplaTabParsingException
																		
	{
		LOG.info("Importing RPLA-TAB document set");
		updateFileStatus(fileSet, FileStatus.IMPORTING);
		RplaTabInputFileSet inputSet = getInputFileSet(fileSet);
		RplaTabDocumentSet documentSet;
		try {
			documentSet = RplaTabDocumentSetParser.INSTANCE.parse(inputSet);
			RplaTabTranslationResult translationResult = translator.translate(	documentSet, 
																				fileSet);
			save(targetProject, translationResult);
			updateFileStatus(fileSet, FileStatus.IMPORTED);
		} catch (InvalidDataException e) {
			handleInvalidRplaTab(fileSet, e);
		}
		catch ( RplaTabParsingException rexc){
			//bad or good?
			throw rexc;
			
		}
	}

	// private void handleInvalidMageTab ( CaArrayFileSet fileSet,
	// InvalidDataException e)
	// {
	// ValidationResult validationResult = e.getValidationResult();
	// for (CaArrayFile caArrayFile : fileSet.getFiles()) {
	// File file = getFile(caArrayFile);
	// FileValidationResult fileValidationResult = validationResult
	// .getFileValidationResult(file);
	// if (fileValidationResult != null) {
	// handleValidationResult(caArrayFile, fileValidationResult);
	// }
	// }
	// }
	//	

	private void handleInvalidRplaTab ( CaArrayFileSet fileSet,
										InvalidDataException e)
	{
		
		LOG.info("carplafix: handleInvalidRplaTab");
		ValidationResult validationResult = e.getValidationResult();
		for (CaArrayFile caArrayFile : fileSet.getFiles()) {
			File file = getFile(caArrayFile);
			FileValidationResult fileValidationResult = validationResult.getFileValidationResult(file);
			if (fileValidationResult != null) {
				handleValidationResult(caArrayFile, fileValidationResult);
			}
		}
	}

	private void handleValidationResult (	CaArrayFile caArrayFile,
											FileValidationResult fileValidationResult)
	{
		if (fileValidationResult.isValid()) {
			caArrayFile.setFileStatus(FileStatus.VALIDATED);
		} else {
			caArrayFile.setFileStatus(FileStatus.VALIDATION_ERRORS);
		}
		caArrayFile.setValidationResult(fileValidationResult);
		daoFactory.getProjectDao().save(caArrayFile);
	}

	private void updateFileStatus ( CaArrayFileSet fileSet, FileStatus status) {
		for (CaArrayFile file : fileSet.getFiles()) {
			// if (isMageTabFile(file)) {
			if (isRplaTabFile(file)) {
				LOG.info(file.getName() + " is considered a RPLA-TAB file");

				file.setFileStatus(status);
				getProjectDao().save(file);
			} else {
				LOG.info(file.getName() + " is not considered a RPLA-TAB file");

			}
		}
	}

	private boolean isMageTabFile ( CaArrayFile file) {
		return FileType.MAGE_TAB_ADF.equals(file.getFileType()) || FileType.MAGE_TAB_DATA_MATRIX.equals(file.getFileType())
				|| FileType.MAGE_TAB_IDF.equals(file.getFileType())
				|| FileType.MAGE_TAB_SDRF.equals(file.getFileType());
	}

	private boolean isRplaTabFile ( CaArrayFile file) {
		return FileType.RPLA_TAB_RPLAIDF.equals(file.getFileType()) || FileType.RPLA_TAB_SRADF.equals(file.getFileType())
				|| FileType.RPLA_TAB_TIFF.equals(file.getFileType())
				|| FileType.RPLA_TAB_TXT.equals(file.getFileType())

		;
	}

	// private MageTabInputFileSet getInputFileSet(CaArrayFileSet fileSet) {
	// MageTabInputFileSet inputFileSet = new MageTabInputFileSet();
	// for (CaArrayFile caArrayFile : fileSet.getFiles()) {
	// addInputFile(inputFileSet, caArrayFile);
	// }
	// return inputFileSet;
	// }

	private RplaTabInputFileSet getInputFileSet ( CaArrayFileSet fileSet) {
		RplaTabInputFileSet inputFileSet = new RplaTabInputFileSet();
		for (CaArrayFile caArrayFile : fileSet.getFiles()) {
			addInputFile(inputFileSet, caArrayFile);
		}
		return inputFileSet;
	}

	// private void addInputFile(MageTabInputFileSet inputFileSet, CaArrayFile
	// caArrayFile) {
	// FileType type = caArrayFile.getFileType();
	// if (FileType.MAGE_TAB_IDF.equals(type)) {
	// inputFileSet.addIdf(getFile(caArrayFile));
	// } else if (FileType.MAGE_TAB_SDRF.equals(type)) {
	// inputFileSet.addSdrf(getFile(caArrayFile));
	// } else if (FileType.MAGE_TAB_ADF.equals(type)) {
	// inputFileSet.addAdf(getFile(caArrayFile));
	// } else if (FileType.MAGE_TAB_DATA_MATRIX.equals(type)) {
	// inputFileSet.addDataMatrix(getFile(caArrayFile));
	// } else {
	// inputFileSet.addNativeData(getFile(caArrayFile));
	// }
	// }

	// carplafix
	private void addInputFile ( RplaTabInputFileSet inputFileSet,
								CaArrayFile caArrayFile)
	{

		FileType type = caArrayFile.getFileType();
		if (FileType.RPLA_TAB_RPLAIDF.equals(type)) {
			RplaIdfFile ridffile = new RplaIdfFile();
			try {
				ridffile.setFile(getFile(caArrayFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			inputFileSet.setRplaIdf(ridffile);

		} else if (FileType.RPLA_TAB_SRADF.equals(type)) {
			SradfFile sfile = new SradfFile();
			try {
				sfile.setFile(getFile(caArrayFile));

				inputFileSet.setSradfFile(sfile);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		else {
			try {
				inputFileSet.addFile(getFile(caArrayFile));
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	private File getFile ( CaArrayFile caArrayFile) {
		return TemporaryFileCacheLocator.getTemporaryFileCache()
										.getFile(caArrayFile);
	}

	private void save ( Project targetProject,
						RplaTabTranslationResult translationResult)
	{
		LOG.info("save !!!!!!!");
		saveTerms(translationResult);
	//	saveArrayDesigns(translationResult);
		saveInvestigations(targetProject, translationResult);
	}

	private void saveTerms ( RplaTabTranslationResult translationResult) {
		
		LOG.info("saveTerms: translationResult has " + translationResult.getTerms().size() +" (number) of terms");
		
		for (Term term : translationResult.getTerms()) {
			LOG.info(term.getValue());
			getCaArrayDao().save(term);
		}
	}

	
	private void saveInvestigations (	Project targetProject,
										RplaTabTranslationResult translationResult)
	{
		LOG.info("saveInvestigations");
		
		// DEVELOPER NOTE: currently, importing multiple IDFs in a single import
		// is not supported, and will
		// be flagged as a validation error. hence we can assume that only a
		// single investigation is present in the
		// translation result. In the future we may support multiple experiments
		// per projects, and therefore
		// multi-IDF import. Hence, continue to allow for this in the object
		// model.
		if (!translationResult.getInvestigations().isEmpty()) {
			mergeTranslatedData(targetProject.getExperiment(),
								translationResult	.getInvestigations()
													.iterator()
													.next());
			getProjectDao().save(targetProject);
		}
	}

	private void mergeTranslatedData (	Experiment originalExperiment,
										Experiment translatedExperiment)
	{
		originalExperiment	.getArrayDesigns()
							.addAll(translatedExperiment.getArrayDesigns());
		originalExperiment.getArrays().addAll(translatedExperiment.getArrays());
		originalExperiment.setDateOfExperiment(translatedExperiment.getDateOfExperiment());
		originalExperiment.setDescription(translatedExperiment.getDescription());
		originalExperiment	.getExtracts()
							.addAll(translatedExperiment.getExtracts());
		originalExperiment	.getFactors()
							.addAll(translatedExperiment.getFactors());
		originalExperiment	.getHybridizations()
							.addAll(translatedExperiment.getHybridizations());
		originalExperiment	.getLabeledExtracts()
							.addAll(translatedExperiment.getLabeledExtracts());
		originalExperiment	.getExperimentDesignTypes()
							.addAll(translatedExperiment.getExperimentDesignTypes());
		originalExperiment	.getNormalizationTypes()
							.addAll(translatedExperiment.getNormalizationTypes());
		originalExperiment	.getPublications()
							.addAll(translatedExperiment.getPublications());
		originalExperiment	.getQualityControlTypes()
							.addAll(translatedExperiment.getQualityControlTypes());
		originalExperiment	.getReplicateTypes()
							.addAll(translatedExperiment.getReplicateTypes());
		
		
		
		
		originalExperiment	.getSamples()
							.addAll(translatedExperiment.getSamples());
		
		LOG.info("adding in mergeTranslatedData , number of samples="+ translatedExperiment.getSamples().size());
		
		originalExperiment	.getSources()
							.addAll(translatedExperiment.getSources());
		originalExperiment	.getExperimentContacts()
							.addAll(translatedExperiment.getExperimentContacts());
		
		originalExperiment.getRplArrays().addAll(translatedExperiment.getRplArrays());
		
		LOG.info("rplafeature size =" + translatedExperiment.getRplArrays().iterator().next().getRplaFeatures().size());
		
		LOG.info("rplareporter size =" + translatedExperiment.getRplArrays().iterator().next().getRplaReporters().size());
		
		
		
		originalExperiment.getRplaHybridizations().addAll (translatedExperiment.getRplaHybridizations());
		
		originalExperiment.getAntibodies().addAll (translatedExperiment.getAntibodies());
		
		originalExperiment.getRplaSamples().addAll(translatedExperiment.getRplaSamples());
		
		
		
		for (ExperimentContact ec : translatedExperiment.getExperimentContacts()) {
			ec.setExperiment(originalExperiment);
		}
	}

	private CaArrayDao getCaArrayDao () {
		return getProjectDao();
	}

	private ProjectDao getProjectDao () {
		return daoFactory.getProjectDao();
	}

}
