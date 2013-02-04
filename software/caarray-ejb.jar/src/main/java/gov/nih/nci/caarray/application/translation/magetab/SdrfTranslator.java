//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.util.MessageTemplates;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.AbstractFactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.MeasurementFactorValue;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.TermBasedFactorValue;
import gov.nih.nci.caarray.domain.project.UserDefinedFactorValue;
import gov.nih.nci.caarray.domain.protocol.AbstractParameterValue;
import gov.nih.nci.caarray.domain.protocol.MeasurementParameterValue;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.protocol.ProtocolTypeAssociation;
import gov.nih.nci.caarray.domain.protocol.TermBasedParameterValue;
import gov.nih.nci.caarray.domain.protocol.UserDefinedParameterValue;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.sample.UserDefinedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.SdrfColumnType;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfNodeType;
import gov.nih.nci.caarray.validation.ValidationMessage.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * Translates entities in SDRF documents into caArray entities.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.ExcessiveClassLength" })
final class SdrfTranslator extends AbstractTranslator {

    private static final Logger LOG = Logger.getLogger(SdrfTranslator.class);

    private static final String GENERATED_SAMPLE_PREFIX = "GeneratedSample.";
    private static final String GENERATED_EXTRACT_PREFIX = "GeneratedExtract.";
    private static final String GENERATED_LABELED_EXTRACT_PREFIX = "GeneratedLabeledExtract.";

    private final Map<AbstractSampleDataRelationshipNode, AbstractCaArrayEntity> nodeTranslations =
            new HashMap<AbstractSampleDataRelationshipNode, AbstractCaArrayEntity>();
    private final Map<AbstractSampleDataRelationshipNode, Boolean> isNodeLinked =
            new HashMap<AbstractSampleDataRelationshipNode, Boolean>();
    private final List<Source> allSources = new ArrayList<Source>();
    private final List<Sample> allSamples = new ArrayList<Sample>();
    private final List<Extract> allExtracts = new ArrayList<Extract>();
    private final List<LabeledExtract> allLabeledExtracts = new ArrayList<LabeledExtract>();
    private final List<Hybridization> allHybridizations = new ArrayList<Hybridization>();
    private final Map<String, AbstractCaArrayEntity> generatedNodes = new HashMap<String, AbstractCaArrayEntity>();
    private final Map<ProtocolKey, Protocol> importedProtocolMap = new HashMap<ProtocolKey, Protocol>();
    private final Map<Term, Organism> termToOrganism = new HashMap<Term, Organism>();
    private final VocabularyService vocabularyService;
    private final MultiKeyMap paramMap = new MultiKeyMap();
    private final Experiment experiment;
    private final TermTranslator termTranslator;

    SdrfTranslator(MageTabDocumentSet documentSet, CaArrayFileSet fileSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory, VocabularyService vocabularyService) {
        super(documentSet, fileSet, translationResult, daoFactory);
        this.vocabularyService = vocabularyService;

        final Project project = getDaoFactory().getSearchDao().retrieve(Project.class, getFileSet().getProjectId());
        if (project != null) {
            this.experiment = project.getExperiment();
        } else {
            this.experiment = null;
        }

        this.termTranslator = new TermTranslator(documentSet, translationResult, vocabularyService, daoFactory);
    }

    @Override
    void translate() {
        for (final SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
            translateSdrf(document);
        }
        // cleanup the organism terms
        getTranslationResult().removeOrganismTerms();
    }

    void validate() {
        // if there are no sdrf docs in the set, then this a data-only import,
        // so no mage-tab specific validation to perform
        if (getDocumentSet().getSdrfDocuments().isEmpty()) {
            return;
        }

        final Set<String> externalIds = getExistingExternalIdsForCurrentExperiment();
        for (final SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
            validateSdrf(document, externalIds);
        }
        validateFileReferences();
    }

    private void validateFileReferences() {
        final List<String> referencedRawFiles = getDocumentSet().getSdrfReferencedRawFileNames();
        final List<String> referencedDerivedFiles = getDocumentSet().getSdrfReferencedDerivedFileNames();
        final List<String> referencedDataMatrixFiles = getDocumentSet().getSdrfReferencedDataMatrixFileNames();

        for (final CaArrayFile file : getFileSet().getFiles()) {
            final FileType fileType = file.getFileType();
            final boolean isRaw = fileType.isRawArrayData();
            final boolean referencedAsRaw = referencedRawFiles.contains(file.getName());
            final boolean isDerived = fileType.isDerivedArrayData();
            final boolean referencedAsDerived = referencedDerivedFiles.contains(file.getName());
            final boolean isMatrix = fileType.isDataMatrix();
            final boolean referencedAsMatrix = referencedDataMatrixFiles.contains(file.getName());
            final boolean referencedAsAny = referencedAsRaw || referencedAsDerived || referencedAsMatrix;

            if (isMatrix && !referencedAsMatrix) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.ARRAY_DATA_MATRIX_FILE.getDisplayName()
                        + " or " + SdrfColumnType.DERIVED_ARRAY_DATA_MATRIX_FILE.getDisplayName());
            } else if (isRaw && !isMatrix && !referencedAsRaw) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.ARRAY_DATA_FILE.getDisplayName());
            } else if (isDerived && !isMatrix && !referencedAsDerived) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.DERIVED_ARRAY_DATA_FILE.getDisplayName());
            }
        }
    }

    private void addFileReferenceError(CaArrayFile caArrayFile, boolean referencedAsAny, String correctColumn) {
        final String message =
                referencedAsAny ? "This file is not correctly referenced from an SDRF file. "
                        + "It should be referenced using an " + correctColumn + " column"
                        : "This data file is not referenced from an SDRF file.";
        getDocumentSet().getValidationResult().addMessage(caArrayFile.getName(), Type.ERROR, message);
    }

    private void validateSdrf(SdrfDocument document, Set<String> externalIds) {
        validateArrayDesigns(document);
        validateSamples(document, externalIds);
    }

    private void validateSamples(SdrfDocument document, Set<String> externalIds) {
        for (final gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBm : document.getAllBiomaterials()) {
            for (final Characteristic sdrfCharacteristic : sdrfBm.getCharacteristics()) {
                final String category = sdrfCharacteristic.getCategory();
                if (category != null) {
                    checkForDuplicateExternalIds(document, externalIds, sdrfCharacteristic, category);
                    checkForIncorrectOrganismTermSource(document, sdrfCharacteristic, category);
                }
            }
        }
    }
    
    private void checkForIncorrectOrganismTermSource(SdrfDocument document, final Characteristic sdrfCharacteristic,
            final String category) {
        if (ExperimentOntologyCategory.ORGANISM.getCategoryName().equalsIgnoreCase(category)
                && (null != sdrfCharacteristic.getTerm().getTermSource() && (!sdrfCharacteristic.getTerm()
                        .getTermSource().getName().equals(ExperimentOntology.NCBI.getOntologyName())))) {
            document.addErrorMessage("The Characteristics [" + category + "] associated Term Source '"
                    + sdrfCharacteristic.getTerm().getTermSource().getName() + "' is invalid.  It must be '"
                    + ExperimentOntology.NCBI.getOntologyName() + "', or the Term Source should be omitted"
                    + ", so the system can then auto-assign the " + ExperimentOntology.NCBI.getOntologyName()
                    + " Term Source.");
        }
    }
    
    private void checkForDuplicateExternalIds(SdrfDocument document, Set<String> externalIds,
            final Characteristic sdrfCharacteristic, final String category) {
        final boolean isExternalId = ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName()
                .equalsIgnoreCase(category.replaceAll("\\s", ""))
                || ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName().equalsIgnoreCase(
                        category.replaceAll("\\s", ""));
        if (isExternalId && !StringUtils.isEmpty(sdrfCharacteristic.getValue())) {
            final boolean added = externalIds.add(sdrfCharacteristic.getValue());
            if (!added) {
                document.addWarningMessage("[" + category + "] value '" + sdrfCharacteristic.getValue()
                        + "' is referenced multiple times (" + category + " must be unique). "
                        + "Existing value will be reused.");
            }
        }
    }

    private Set<String> getExistingExternalIdsForCurrentExperiment() {
        final Set<String> results = new HashSet<String>();
        final Set<AbstractBioMaterial> persistedBms =
                getProjectDao().getUnfilteredBiomaterialsForProject(getFileSet().getProjectId());
        for (final AbstractBioMaterial bm : persistedBms) {
            if (bm.getExternalId() != null && !results.add(bm.getExternalId())) {
                throw new IllegalStateException("System contains samples with duplicate external sample id " + "("
                        + bm.getExternalId() + ") already. Unable to continue, please correct"
                        + " existing samples and try again.");
            }
        }
        return results;
    }

    private void validateArrayDesigns(SdrfDocument document) {
        final Set<String> namesOfArrayDesignsForExperiment = new HashSet<String>();
        if (null != this.experiment) {
            for (final ArrayDesign experimentArrayDesign : this.experiment.getArrayDesigns()) {
                namesOfArrayDesignsForExperiment.add(experimentArrayDesign.getName());
            }
        }
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            final String arrayDesignName = sdrfArrayDesign.getValue();
            final ArrayDesign modelArrayDesign = new ArrayDesign();
            modelArrayDesign.setLsidForEntity(arrayDesignName);
            final List<ArrayDesign> matchingArrayDesignsAlreadyInSystemList =
                    getDaoFactory().getArrayDao().queryEntityByExample(modelArrayDesign);
            if (null == matchingArrayDesignsAlreadyInSystemList || matchingArrayDesignsAlreadyInSystemList.isEmpty()) {
                document.addErrorMessage(String.format(
                        MessageTemplates.NON_EXISTING_ARRAY_DESIGN_ERROR_MESSAGE_TEMPLATE, arrayDesignName));
            }
            String nameToCheck = arrayDesignName;
            if (null != matchingArrayDesignsAlreadyInSystemList && !matchingArrayDesignsAlreadyInSystemList.isEmpty()) {
                nameToCheck = matchingArrayDesignsAlreadyInSystemList.get(0).getName();
            }
            if (!namesOfArrayDesignsForExperiment.isEmpty() && !
                    namesOfArrayDesignsForExperiment.contains(nameToCheck)) {
                document.addErrorMessage(String.format(
                        MessageTemplates.ARRAY_DESIGN_NOT_ASSOCIATED_WITH_EXPERIMENT_ERROR_MESSAGE_TEMPLATE,
                        nameToCheck));
            }
        }
    }

    private void translateSdrf(SdrfDocument document) {
        translateNodesToEntities(document);
        linkNodes(document);
        /**
         * added following if statement b/c sdrf doesn't have idf document. i could be wrong. this was causing error on
         * imports of sdrf. JH 10/4/07
         * 
         */
        if (document.getIdfDocument() != null) {
            final String investigationTitle = document.getIdfDocument().getInvestigation().getTitle();
            for (final Experiment investigation : getTranslationResult().getInvestigations()) {
                if (investigationTitle.equals(investigation.getTitle())) {
                    addImplicitExtracts();
                    addImplicitSamples();
                    addImplicitSources();
                    investigation.getSources().addAll(this.allSources);
                    investigation.getSamples().addAll(this.allSamples);
                    investigation.getExtracts().addAll(this.allExtracts);
                    investigation.getLabeledExtracts().addAll(this.allLabeledExtracts);
                    investigation.getHybridizations().addAll(this.allHybridizations);
                }
            }
        }
    }

    private void addImplicitExtracts() {
        for (final LabeledExtract labeledExtract : this.allLabeledExtracts) {
            if (labeledExtract.getExtracts().isEmpty()) {
                final Extract extract = new Extract();
                extract.setName(labeledExtract.getName());
                extract.getLabeledExtracts().add(labeledExtract);
                labeledExtract.getExtracts().add(extract);
                this.allExtracts.add(extract);
            }
        }
    }

    private void addImplicitSamples() {
        for (final Extract extract : this.allExtracts) {
            if (extract.getSamples().isEmpty()) {
                final Sample sample = new Sample();
                sample.setName(extract.getName());
                sample.getExtracts().add(extract);
                extract.getSamples().add(sample);
                this.allSamples.add(sample);
            }
        }
    }

    private void addImplicitSources() {
        for (final Sample sample : this.allSamples) {
            if (sample.getSources().isEmpty()) {
                final Source source = new Source();
                source.setName(sample.getName());
                source.getSamples().add(sample);
                sample.getSources().add(source);
                this.allSources.add(source);
            }
        }
    }

    private void translateNodesToEntities(SdrfDocument document) {
        translateSources(document);
        translateSamples(document);
        translateExtracts(document);
        translateLabeledExtracts(document);
        translateHybridizations(document);
        translateArrayDesigns(document);
        translateImages(document);
        translateRawArrayData(document);
        translateDerivedArrayData(document);
    }

    private void translateSources(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Source sdrfSource : document.getAllSources()) {
            Source source = getProjectDao().getSourceForExperiment(this.experiment, sdrfSource.getName());
            if (source == null) {
                source = new Source();
            }
            translateBioMaterial(source, sdrfSource);
            for (final Provider sdrfProvider : sdrfSource.getProviders()) {
                final Organization organization = getOrCreateOrganization(sdrfProvider.getName());
                source.getProviders().add(organization);
            }
            this.nodeTranslations.put(sdrfSource, source);
            this.allSources.add(source);
        }
    }

    private void translateSamples(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Sample sdrfSample : document.getAllSamples()) {
            Sample sample = getProjectDao().getSampleForExperiment(this.experiment, sdrfSample.getName());
            if (sample == null) {
                sample = new Sample();
            }
            translateBioMaterial(sample, sdrfSample);
            this.nodeTranslations.put(sdrfSample, sample);
            this.allSamples.add(sample);
        }
    }

    private void translateExtracts(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Extract sdrfExtract : document.getAllExtracts()) {
            Extract extract = getProjectDao().getExtractForExperiment(this.experiment, sdrfExtract.getName());
            if (extract == null) {
                extract = new Extract();
            }
            translateBioMaterial(extract, sdrfExtract);
            this.nodeTranslations.put(sdrfExtract, extract);
            this.allExtracts.add(extract);
        }
    }

    private void translateLabeledExtracts(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.LabeledExtract sdrfLabeledExtract : document
                .getAllLabeledExtracts()) {
            LabeledExtract labeledExtract =
                    getProjectDao().getLabeledExtractForExperiment(this.experiment, sdrfLabeledExtract.getName());
            if (labeledExtract == null) {
                labeledExtract = new LabeledExtract();
            }
            translateBioMaterial(labeledExtract, sdrfLabeledExtract);
            labeledExtract.setLabel(getTerm(sdrfLabeledExtract.getLabel()));
            this.nodeTranslations.put(sdrfLabeledExtract, labeledExtract);
            this.allLabeledExtracts.add(labeledExtract);
        }
    }

    private void translateHybridizations(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization : document.getAllHybridizations()) {
            final String hybridizationName = sdrfHybridization.getName();
            Hybridization hybridization = null;
            if (this.experiment != null) {
                hybridization = this.experiment.getHybridizationByName(hybridizationName);
            }
            // don't update existing hybridizations
            if (hybridization == null) {
                hybridization = new Hybridization();
                hybridization.setName(hybridizationName);
                for (final gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal : sdrfHybridization
                        .getFactorValues()) {
                    final AbstractFactorValue factorValue = translateFactorValue(sdrfFactorVal);
                    hybridization.getFactorValues().add(factorValue);
                    factorValue.setHybridization(hybridization);
                }
                associateProtocolApplications(hybridization.getProtocolApplications(),
                        sdrfHybridization.getProtocolApplications());
                associateProtocolApplications(hybridization.getProtocolApplications(),
                        getAllProtocols(sdrfHybridization.getSuccessorScans()));
            }
            this.allHybridizations.add(hybridization);
            this.nodeTranslations.put(sdrfHybridization, hybridization);
        }
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private AbstractFactorValue translateFactorValue(gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal) {
        AbstractFactorValue factorValue = null;
        final Term unit = getTerm(sdrfFactorVal.getUnit());
        if (sdrfFactorVal.getTerm() != null) {
            factorValue = new TermBasedFactorValue(getTerm(sdrfFactorVal.getTerm()), unit);
        } else {
            if (unit != null) {
                try {
                    factorValue = new MeasurementFactorValue(Float.valueOf(sdrfFactorVal.getValue()), unit);
                } catch (final NumberFormatException e) {
                    // non-Float values that have Units will end up as UserDefined
                }
            }
            if (factorValue == null) {
                factorValue = new UserDefinedFactorValue(sdrfFactorVal.getValue(), unit);
            }
        }
        final Factor factor = getTranslationResult().getFactor(sdrfFactorVal.getFactor());
        factorValue.setFactor(factor);
        factor.getFactorValues().add(factorValue);
        return factorValue;
    }

    private void translateBioMaterial(AbstractBioMaterial bioMaterial,
            gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        if (bioMaterial.getId() == null) {
            // only set these properties for new bio materials, since only a few properties should be updated
            bioMaterial.setName(sdrfBiomaterial.getName());
            bioMaterial.setDescription(sdrfBiomaterial.getDescription());
            for (final gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication : sdrfBiomaterial
                    .getProtocolApplications()) {
                final ProtocolApplication protocolApplication =
                        getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
                bioMaterial.addProtocolApplication(protocolApplication);
            }
        }
        final Term materialTypeTerm = getTerm(sdrfBiomaterial.getMaterialType());
        if (null != materialTypeTerm && null == materialTypeTerm.getSource()) {
            materialTypeTerm.setSource(this.vocabularyService.getSource(
                    ExperimentOntology.MGED_ONTOLOGY.getOntologyName(), ExperimentOntology.MGED_ONTOLOGY.getVersion()));
        }
        bioMaterial.setMaterialType(materialTypeTerm);
        for (final Characteristic sdrfCharacteristic : sdrfBiomaterial.getCharacteristics()) {
            processSdrfCharacteristic(bioMaterial, sdrfCharacteristic);
        }
    }

    private void processSdrfCharacteristic(final AbstractBioMaterial bioMaterial,
            final Characteristic sdrfCharacteristic) {
        final AbstractCharacteristic characteristic = translateCharacteristic(sdrfCharacteristic);
        final String category = characteristic.getCategory().getName();
        if (ExperimentOntologyCategory.ORGANISM_PART.getCategoryName().equals(category)) {
            bioMaterial.setTissueSite(forceToTerm(characteristic));
        } else if (ExperimentOntologyCategory.CELL_TYPE.getCategoryName().equals(category)) {
            bioMaterial.setCellType(forceToTerm(characteristic));
        } else if (ExperimentOntologyCategory.DISEASE_STATE.getCategoryName().equals(category)) {
            bioMaterial.setDiseaseState(forceToTerm(characteristic));
        } else if (ExperimentOntologyCategory.ORGANISM.getCategoryName().equals(category)) {
            final Organism organism = getOrganism(forceToTerm(characteristic));
            if (null == organism.getTermSource()) {
                organism.setTermSource(this.vocabularyService.getSource(ExperimentOntology.NCBI.getOntologyName(),
                        ExperimentOntology.NCBI.getVersion()));
            }
            bioMaterial.setOrganism(organism);
        } else if (ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName().equalsIgnoreCase(
                category.replaceAll("\\s", ""))
                || ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName().equalsIgnoreCase(
                        category.replaceAll("\\s", ""))) {
            bioMaterial.setExternalId(sdrfCharacteristic.getValue());
        } else {
            for (final AbstractCharacteristic existingCharacteristic : bioMaterial.getCharacteristics()) {
                if (existingCharacteristic.getCategory().equals(characteristic.getCategory())) {
                    bioMaterial.getCharacteristics().remove(existingCharacteristic);
                    getProjectDao().remove(existingCharacteristic);
                    break;
                }
            }
            bioMaterial.getCharacteristics().add(characteristic);
            characteristic.setBioMaterial(bioMaterial);
        }
    }

    private Term forceToTerm(AbstractCharacteristic characteristic) {
        if (characteristic instanceof TermBasedCharacteristic) {
            return ((TermBasedCharacteristic) characteristic).getTerm();
        } else {
            final String value = characteristic.getDisplayValueWithoutUnit();
            final OntologyTerm fakeSdrfTerm = new OntologyTerm();
            fakeSdrfTerm.setValue(value);
            this.termTranslator.translateTerm(fakeSdrfTerm);
            return getTerm(fakeSdrfTerm);
        }
    }

    private Term getUnknownProtocolType() {
        TermSource source = this.vocabularyService.getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                ExperimentOntology.MGED_ONTOLOGY.getVersion());
        return this.vocabularyService.getTerm(source, VocabularyService.UNKNOWN_PROTOCOL_TYPE_NAME);
    }

    private Protocol replaceProtocolIfExists(Protocol p) {
        final ProtocolKey key = new ProtocolKey(p.getName(), p.getSource());

        // check in our map of imported protocols
        Protocol returnProtocol = this.importedProtocolMap.get(key);
        if (returnProtocol == null) {
            // not in the map, check in the db
            returnProtocol = getDaoFactory().getProtocolDao().getProtocol(p.getName(), p.getSource());
        }
        if (returnProtocol == null) {
            // protocol not in the map of imported protocols or in the db, add to map as it will be new
            this.importedProtocolMap.put(key, p);
            returnProtocol = p;
        }
        return returnProtocol;
    }

    /**
     * @param term
     * @return
     */
    private Organism getOrganism(Term term) {
        Organism o = this.termToOrganism.get(term);
        if (o == null && term.getSource().getId() != null) {
            o = this.vocabularyService.getOrganism(term.getSource(), term.getValue());
        }
        if (o == null) {
            o = new Organism();
            o.setScientificName(term.getValue());
            o.setTermSource(term.getSource());
            this.termToOrganism.put(term, o);
        }
        return o;
    }

    private Protocol getProtocolFromMageTabProtocol(gov.nih.nci.caarray.magetab.Protocol mageTabProtocol) {
        Term type = getTerm(mageTabProtocol.getType());
        if (type == null) {
            type = getUnknownProtocolType();
        }
        TermSource termSource = null;
        if (mageTabProtocol.getTermSource() != null) {
            termSource = getTranslationResult().getSource(mageTabProtocol.getTermSource());
        } else {
            termSource =
                    this.vocabularyService.getSource(ExperimentOntology.MGED_ONTOLOGY.getOntologyName(),
                            ExperimentOntology.MGED_ONTOLOGY.getVersion());
        }
        Protocol p = new Protocol(mageTabProtocol.getName(), type, termSource);
        p.setContact(mageTabProtocol.getContact());
        p.setDescription(mageTabProtocol.getDescription());
        p.setHardware(mageTabProtocol.getHardware());
        p.setSoftware(mageTabProtocol.getSoftware());
        p = replaceProtocolIfExists(p);
        return p;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private ProtocolApplication getProtocolApplicationFromMageTabProtocolApplication(
            gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication) {
        final Protocol protocol = getProtocolFromMageTabProtocol(mageTabProtocolApplication.getProtocol());
        final ProtocolApplication protocolApplication = new ProtocolApplication();
        protocolApplication.setProtocol(protocol);
        for (final gov.nih.nci.caarray.magetab.ParameterValue mageTabValue : mageTabProtocolApplication
                .getParameterValues()) {
            AbstractParameterValue value = null;
            final Term unit = getTerm(mageTabValue.getUnit());
            if (mageTabValue.getTerm() != null) {
                value = new TermBasedParameterValue(getTerm(mageTabValue.getTerm()), unit);
            } else {
                if (unit != null) {
                    try {
                        value = new MeasurementParameterValue(Float.valueOf(mageTabValue.getValue()), unit);
                    } catch (final NumberFormatException e) {
                        // non-Float values that have Units will end up as UserDefined
                    }
                }
                if (value == null) {
                    value = new UserDefinedParameterValue(mageTabValue.getValue(), unit);
                }
            }
            if (mageTabValue.getParameter() != null) {
                final Parameter param = getOrCreateParameter(mageTabValue.getParameter().getName(), protocol);
                value.setParameter(param);
            }
            value.setProtocolApplication(protocolApplication);
            protocolApplication.getValues().add(value);
        }
        return protocolApplication;
    }

    private Parameter getOrCreateParameter(String name, Protocol protocol) {
        Parameter param = (Parameter) this.paramMap.get(name, protocol);
        if (param == null) {
            param = this.getDaoFactory().getProtocolDao().getParameter(name, protocol);
        }
        if (param == null) {
            param = new Parameter(name, protocol);
            this.paramMap.put(name, protocol, param);
        }
        return param;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private AbstractCharacteristic translateCharacteristic(Characteristic sdrfCharacteristic) {
        final Category category =
                TermTranslator.getOrCreateCategory(this.vocabularyService, this.getTranslationResult(),
                        sdrfCharacteristic.getCategory());

        final Term unit = getTerm(sdrfCharacteristic.getUnit());
        AbstractCharacteristic chr = null;
        if (sdrfCharacteristic.getTerm() != null) {
            chr = new TermBasedCharacteristic(category, getTerm(sdrfCharacteristic.getTerm()), unit);
        } else {
            if (unit != null) {
                try {
                    chr = new MeasurementCharacteristic(category, Float.valueOf(sdrfCharacteristic.getValue()), unit);
                } catch (final NumberFormatException e) {
                    // non-Float values that have Units will end up as UserDefined
                }
            }
            if (chr == null) {
                chr = new UserDefinedCharacteristic(category, sdrfCharacteristic.getValue(), unit);
            }
        }
        return chr;
    }

    // Translates array designs to a linked array-array design pair in the caArray domain.
    private void translateArrayDesigns(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            final ArrayDesign arrayDesign = getArrayDesign(sdrfArrayDesign);
            getTranslationResult().getArrayDesigns().add(arrayDesign);
            if (getTranslationResult().getInvestigations().size() > 0) {
                getTranslationResult().getInvestigations().iterator().next().getArrayDesigns().add(arrayDesign);
            }
        }
    }

    /**
     * Get a caArray ArrayDesign object from an MAGETAB ArrayDesign.
     * 
     * @param sdrfArrayDesign MAGETAB array design - must not be null
     */
    private ArrayDesign getArrayDesign(gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign) {
        return processArrayDesignRef(sdrfArrayDesign.getValue());
    }

    // Process a reference to an array design in the caArray or in an external database.
    private ArrayDesign processArrayDesignRef(String arrayDesignName) {
        final ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setLsidForEntity(arrayDesignName);
        final List<ArrayDesign> designs = getDaoFactory().getArrayDao().queryEntityByExample(arrayDesign);
        if (designs.isEmpty()) {
            return null;
        } else {
            return designs.get(0);
        }
    }

    private void translateImages(SdrfDocument document) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : document.getAllImages()) {
            final String imageName = sdrfImage.getName();
            final CaArrayFile imageFile = getFile(imageName);
            if (imageFile != null) {
                final Image image = new Image();
                image.setName(imageName);
                image.setImageFile(imageFile);
                this.nodeTranslations.put(sdrfImage, image);
            }
        }
    }

    private void translateRawArrayData(SdrfDocument document) {
        // Translate native raw data files.
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfData : document.getAllArrayDataFiles()) {
            translateIndividualRawArrayDataFile(sdrfData, false);
        }
        // Translate MAGE-TAB raw data matrix files.
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfData : document
                .getAllArrayDataMatrixFiles()) {
            translateIndividualRawArrayDataFile(sdrfData, true);
        }
    }

    private void translateIndividualRawArrayDataFile(
            gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData, boolean isMatrix) {
        final String fileName = sdrfData.getName();
        final CaArrayFile dataFile = getFile(fileName);
        if (dataFile == null) {
            // The file could be coming in a future import, possibly due to import splitting.
            return;
        }
        RawArrayData caArrayData = null;
        if (EnumSet.of(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED).contains(dataFile.getFileStatus())) {
            // this is a re-import referencing an existing data file
            caArrayData = (RawArrayData) getDaoFactory().getArrayDao().getArrayData(dataFile.getId());
        } else {
            caArrayData = new RawArrayData();
            caArrayData.setName(fileName);
            caArrayData.setDataFile(dataFile);

            final Set<gov.nih.nci.caarray.magetab.ProtocolApplication> all =
                    new HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication>();
            all.addAll(sdrfData.getProtocolApplications());
            for (final DerivedArrayDataFile df : sdrfData.getSuccessorDerivedArrayDataFiles()) {
                all.addAll(getAllProtocols(df.getPredecessorNormalizations()));
            }
            for (final DerivedArrayDataMatrixFile df : sdrfData.getSuccessorDerivedArrayDataMatrixFiles()) {
                all.addAll(getAllProtocols(df.getPredecessorNormalizations()));
            }
            all.addAll(getAllProtocols(sdrfData.getSuccessorNormalizations()));

            associateProtocolApplications(caArrayData.getProtocolApplications(), all);
            this.nodeTranslations.put(sdrfData, caArrayData);
        }
        this.nodeTranslations.put(sdrfData, caArrayData);
    }

    private void translateDerivedArrayData(SdrfDocument document) {
        // Translate native derived data files.
        for (final gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfData : document
                .getAllDerivedArrayDataFiles()) {
            translateIndividualDerivedArrayDataFile(sdrfData, false);
        }
        // Translate MAGE-TAB derived data matrix files.
        for (final gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfData : document
                .getAllDerivedArrayDataMatrixFiles()) {
            translateIndividualDerivedArrayDataFile(sdrfData, true);
        }
    }

    private void translateIndividualDerivedArrayDataFile(
            gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData, boolean isDataMatrix) {
        final String fileName = sdrfData.getName();
        final CaArrayFile dataFile = getFile(fileName);
        if (dataFile == null) {
            // The file could be coming in a future import, possibly due to import splitting.
            return;
        }
        DerivedArrayData caArrayData = null;
        if (EnumSet.of(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED).contains(dataFile.getFileStatus())) {
            caArrayData = (DerivedArrayData) getDaoFactory().getArrayDao().getArrayData(dataFile.getId());
        } else {
            caArrayData = new DerivedArrayData();
            caArrayData.setName(fileName);
            caArrayData.setDataFile(dataFile);
            associateProtocolApplications(caArrayData.getProtocolApplications(), sdrfData.getProtocolApplications());
        }
        setDerivedFromData(sdrfData, caArrayData);
        this.nodeTranslations.put(sdrfData, caArrayData);
    }

    private void setDerivedFromData(gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData,
            DerivedArrayData caArrayData) {
        final List<AbstractSampleDataRelationshipNode> allArrayData =
                new ArrayList<AbstractSampleDataRelationshipNode>(sdrfData.getPredecessorArrayDataFiles());

        allArrayData.addAll(sdrfData.getPredecessorArrayDataMatrixFiles());
        allArrayData.addAll(sdrfData.getPredecessorDerivedArrayDataFiles());
        allArrayData.addAll(sdrfData.getPredecessorDerivedArrayDataMatrixFiles());

        for (final AbstractSampleDataRelationshipNode sdrfArrayData : allArrayData) {
            final AbstractArrayData arrayData = (AbstractArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                caArrayData.getDerivedFromArrayDataCollection().add(arrayData);
            }
        }
    }

    private void associateProtocolApplications(Collection<ProtocolApplication> dest,
            Collection<gov.nih.nci.caarray.magetab.ProtocolApplication> sdrfProtocolapplications) {
        for (final gov.nih.nci.caarray.magetab.ProtocolApplication mtProtocolApp : sdrfProtocolapplications) {
            final ProtocolApplication protocolApplication =
                    getProtocolApplicationFromMageTabProtocolApplication(mtProtocolApp);
            dest.add(protocolApplication);

        }
    }

    private static Set<gov.nih.nci.caarray.magetab.ProtocolApplication> getAllProtocols(
            Set<? extends AbstractSampleDataRelationshipNode> nodes) {
        final HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication> all =
                new HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication>();
        for (final AbstractSampleDataRelationshipNode n : nodes) {
            all.addAll(n.getProtocolApplications());
        }
        return all;
    }

    private void linkNodes(SdrfDocument document) {
        for (final AbstractSampleDataRelationshipNode currNode : document.getLeftmostNodes()) {
            linkNode(currNode);
        }
    }

    // Recursively link this node to its successors.
    // Does not handle <code>Comment</code> entities.
    // Assumes that nodes occur in the order: Source, Sample, Extract, LabeledExtract, Hybridization,
    // Scan, Raw Data, Normalization, Derived Data. Any of these nodes are optional, and Image
    // can occur anywhere after Hybridization.
    private void linkNode(AbstractSampleDataRelationshipNode node) {
        // Check if we already linked this node before.
        final Boolean isLinked = this.isNodeLinked.get(node);
        if ((isLinked != null) && (isLinked.booleanValue())) {
            return;
        }
        for (final AbstractSampleDataRelationshipNode successor : node.getSuccessors()) {
            // Recursively link all successors of this node.
            linkNode(successor);
            // Link this node to its successor.
            linkTwoNodes(node, successor);
        }
        // Finished linking node. Mark it so that we don't do it again.
        this.isNodeLinked.put(node, Boolean.TRUE);
    }

    // Link a node with one successor.
    private void
            linkTwoNodes(AbstractSampleDataRelationshipNode leftNode, AbstractSampleDataRelationshipNode rightNode) {
        final AbstractCaArrayObject leftCaArrayNode = this.nodeTranslations.get(leftNode);
        final AbstractCaArrayObject rightCaArrayNode = this.nodeTranslations.get(rightNode);
        final SdrfNodeType leftNodeType = leftNode.getNodeType();
        final SdrfNodeType rightNodeType = rightNode.getNodeType();
        // if either node is null, it means it wasn't translated (because it was an update of existing data and that
        // type of node doesn't get updated), so don't try to link it
        if (isBioMaterial(leftNodeType) && rightCaArrayNode != null) {
            // Use the left node's name as part of any generated biomaterial names.
            final String baseGeneratedNodeName = ((AbstractBioMaterial) leftCaArrayNode).getName();
            final List<ProtocolApplication> pas = ((AbstractBioMaterial) leftCaArrayNode).getProtocolApplications();
            linkBioMaterial(leftCaArrayNode, rightCaArrayNode, leftNodeType, rightNodeType, baseGeneratedNodeName, pas);
        } else if (SdrfNodeType.HYBRIDIZATION.equals(leftNodeType)) {
            final Hybridization hybridization = (Hybridization) leftCaArrayNode;
            linkHybridizationToArrays((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode, hybridization);
            linkHybridizationToImages((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode, hybridization);
            linkHybridizationToArrayData((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode, hybridization);
        } else {
            // Ignore other nodes - Image, Scan, Raw/Derived Data, Normalization; they've already been linked.
            return;
        }
    }

    private void linkHybridizationToArrays(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        final Array array = new Array();
        final Array currArray = hybridization.getArray();
        // a new hyb should always have an array, even if the array isn't associated with a design
        if (currArray == null) {
            hybridization.setArray(array);
        }

        // if the sdrf hyb has an array design, only associate it with the array if the current hyb
        // doesn't have an array or if the new design is different from the old one
        if (sdrfHybridization.getArrayDesign() != null) {
            final ArrayDesign sdrfArrayDesign = getArrayDesign(sdrfHybridization.getArrayDesign());
            if (sdrfArrayDesign != null
                    && (currArray == null || !currArray.getDesign().getLsid().equals(sdrfArrayDesign.getLsid()))) {
                array.setDesign(sdrfArrayDesign);
                hybridization.setArray(array);
            }
        }
    }

    private void linkHybridizationToImages(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        for (final gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : sdrfHybridization.getSuccessorImages()) {
            final Image image = (Image) this.nodeTranslations.get(sdrfImage);
            if (image != null) {
                hybridization.getImages().add(image);
            }
        }
    }

    private void linkHybridizationToArrayData(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        // Link raw array data
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfArrayData : sdrfHybridization
                .getSuccessorArrayDataFiles()) {
            final RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getRawDataCollection().add(arrayData);
            }
        }
        for (final gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfArrayData : sdrfHybridization
                .getSuccessorArrayDataMatrixFiles()) {
            final RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getRawDataCollection().add(arrayData);
            }
        }
        // Link derived array data
        for (final gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfArrayData : sdrfHybridization
                .getSuccessorDerivedArrayDataFiles()) {
            final DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getDerivedDataCollection().add(arrayData);
            }
        }
        for (final gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfArrayData : sdrfHybridization
                .getSuccessorDerivedArrayDataMatrixFiles()) {
            final DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getDerivedDataCollection().add(arrayData);
            }
        }
    }

    /**
     * Links a BioMaterial node with one successor. If a node is missing in the chain Source -> Sample -> Extract ->
     * LabeledExtract -> Hybridization, appropriate intermediate nodes will be generated to complete the chain. The
     * number of nodes generated depends on the left side of the graph. E.g., 1 Source going to 3 Extracts will result
     * in 1 Sample being generated. On the other hand, 3 Sources going to 1 Extract will result in 3 Samples being
     * generated.
     */
    @SuppressWarnings("PMD")
    private void linkBioMaterial(AbstractCaArrayObject leftCaArrayNode, AbstractCaArrayObject rightCaArrayNode,
            SdrfNodeType leftNodeType, SdrfNodeType rightNodeType, String baseGeneratedNodeName,
            Collection<ProtocolApplication> protocolApplications) {
        if (leftNodeType.equals(SdrfNodeType.SOURCE)) {
            if (rightNodeType.equals(SdrfNodeType.SAMPLE)) {
                linkSourceAndSample((Source) leftCaArrayNode, (Sample) rightCaArrayNode);
            } else {
                final Sample generatedSample = generateSampleAndLink(baseGeneratedNodeName, (Source) leftCaArrayNode);
                reassociateProtocolApplications(generatedSample, protocolApplications);
                linkBioMaterial(generatedSample, rightCaArrayNode, SdrfNodeType.SAMPLE, rightNodeType,
                        baseGeneratedNodeName, protocolApplications);
            }
        } else if (leftNodeType.equals(SdrfNodeType.SAMPLE)) {
            if (rightNodeType.equals(SdrfNodeType.EXTRACT)) {
                linkSampleAndExtract((Sample) leftCaArrayNode, (Extract) rightCaArrayNode);
            } else {
                final Extract generatedExtract =
                        generateExtractAndLink(baseGeneratedNodeName, (Sample) leftCaArrayNode);
                reassociateProtocolApplications(generatedExtract, protocolApplications);
                linkBioMaterial(generatedExtract, rightCaArrayNode, SdrfNodeType.EXTRACT, rightNodeType,
                        baseGeneratedNodeName, protocolApplications);
            }
        } else if (leftNodeType.equals(SdrfNodeType.EXTRACT)) {
            if (rightNodeType.equals(SdrfNodeType.LABELED_EXTRACT)) {
                linkExtractAndLabeledExtract((Extract) leftCaArrayNode, (LabeledExtract) rightCaArrayNode);
            } else {
                final LabeledExtract generatedLabeledExtract =
                        generateLabeledExtractAndLink(baseGeneratedNodeName, (Extract) leftCaArrayNode);
                reassociateProtocolApplications(generatedLabeledExtract, protocolApplications);
                linkBioMaterial(generatedLabeledExtract, rightCaArrayNode, SdrfNodeType.LABELED_EXTRACT, rightNodeType,
                        baseGeneratedNodeName, protocolApplications);
            }
        } else if (leftNodeType.equals(SdrfNodeType.LABELED_EXTRACT)
                && rightNodeType.equals(SdrfNodeType.HYBRIDIZATION)) {
            linkLabeledExtractAndHybridization((LabeledExtract) leftCaArrayNode, (Hybridization) rightCaArrayNode);
        }
    }

    private void reassociateProtocolApplications(AbstractBioMaterial bioMaterial,
            Collection<ProtocolApplication> protocolApplications) {
        for (final Iterator<ProtocolApplication> i = protocolApplications.iterator(); i.hasNext();) {
            final ProtocolApplication pa = i.next();
            final Term protocolType = pa.getProtocol().getType();
            for (final ProtocolTypeAssociation typeAssoc : ProtocolTypeAssociation.values()) {
                if (protocolType.getValue().equals(typeAssoc.getValue())
                        && protocolType.getSource().getName().equals(typeAssoc.getSource())
                        && bioMaterial.getClass().equals(typeAssoc.getNodeClass())) {
                    bioMaterial.addProtocolApplication(pa);
                    i.remove();
                }
            }
        }
    }

    private Sample generateSampleAndLink(String baseGeneratedNodeName, Source source) {
        // Generate sample if not already generated.
        final String sampleName = GENERATED_SAMPLE_PREFIX + baseGeneratedNodeName;
        Sample generatedSample = (Sample) this.generatedNodes.get(sampleName);
        if (generatedSample == null) {
            generatedSample = getProjectDao().getSampleForExperiment(this.experiment, sampleName);
            if (generatedSample == null) {
                generatedSample = new Sample();
                generatedSample.setName(sampleName);
            }

            this.generatedNodes.put(sampleName, generatedSample);
            this.allSamples.add(generatedSample);
        }
        linkSourceAndSample(source, generatedSample);
        return generatedSample;
    }

    private Extract generateExtractAndLink(String baseGeneratedNodeName, Sample generatedSample) {
        // Generate extract if not already generated.
        final String extractName = GENERATED_EXTRACT_PREFIX + baseGeneratedNodeName;
        Extract generatedExtract = (Extract) this.generatedNodes.get(extractName);
        if (generatedExtract == null) {
            generatedExtract = getProjectDao().getExtractForExperiment(this.experiment, extractName);
            if (generatedExtract == null) {
                generatedExtract = new Extract();
                generatedExtract.setName(extractName);
            }
            this.generatedNodes.put(extractName, generatedExtract);
            this.allExtracts.add(generatedExtract);
        }
        linkSampleAndExtract(generatedSample, generatedExtract);
        return generatedExtract;
    }

    private LabeledExtract generateLabeledExtractAndLink(String baseGeneratedNodeName, Extract generatedExtract) {
        // Generate labeled extract if not already generated.
        final String labeledExtractName = GENERATED_LABELED_EXTRACT_PREFIX + baseGeneratedNodeName;
        LabeledExtract generatedLabeledExtract = (LabeledExtract) this.generatedNodes.get(labeledExtractName);
        if (generatedLabeledExtract == null) {
            generatedLabeledExtract =
                    getProjectDao().getLabeledExtractForExperiment(this.experiment, labeledExtractName);
            if (generatedLabeledExtract == null) {
                generatedLabeledExtract = new LabeledExtract();
                generatedLabeledExtract.setName(labeledExtractName);
            }
            this.generatedNodes.put(labeledExtractName, generatedLabeledExtract);
            this.allLabeledExtracts.add(generatedLabeledExtract);
        }
        linkExtractAndLabeledExtract(generatedExtract, generatedLabeledExtract);
        return generatedLabeledExtract;
    }

    private void linkSourceAndSample(Source source, Sample sample) {
        source.getSamples().add(sample);
        sample.getSources().add(source);
    }

    private void linkSampleAndExtract(Sample sample, Extract extract) {
        sample.getExtracts().add(extract);
        extract.getSamples().add(sample);
    }

    private void linkExtractAndLabeledExtract(Extract extract, LabeledExtract labeledExtract) {
        extract.getLabeledExtracts().add(labeledExtract);
        labeledExtract.getExtracts().add(extract);
    }

    private void linkLabeledExtractAndHybridization(LabeledExtract labeledExtract, Hybridization hybridization) {
        hybridization.getLabeledExtracts().add(labeledExtract);
        labeledExtract.getHybridizations().add(hybridization);
    }

    private boolean isBioMaterial(SdrfNodeType nodeType) {
        if (nodeType.equals(SdrfNodeType.SOURCE) || nodeType.equals(SdrfNodeType.SAMPLE)
                || nodeType.equals(SdrfNodeType.EXTRACT) || nodeType.equals(SdrfNodeType.LABELED_EXTRACT)) {
            return true;
        }
        return false;
    }

    private CaArrayFile getFile(String name) {
        // check both files included in import and already imported files
        final CaArrayFileSet fs = new CaArrayFileSet(getFileSet());
        if (this.experiment != null) {
            fs.addAll(Collections2.filter(this.experiment.getProject().getImportedFiles(),
                    new Predicate<CaArrayFile>() {
                        @Override
                        public boolean apply(CaArrayFile f) {
                            return f.getFileType().isArrayData();
                        }
                    }));
        }
        return fs.getFile(name);
    }

    @Override
    Logger getLog() {
        return LOG;
    }
}
