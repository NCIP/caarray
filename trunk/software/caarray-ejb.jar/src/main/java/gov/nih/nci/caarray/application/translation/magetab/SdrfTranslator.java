//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCacheLocator;
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

import java.io.File;
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
    private final Map<String, AbstractCaArrayEntity> generatedNodes =
        new HashMap<String, AbstractCaArrayEntity>();
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

        Project project = getDaoFactory().getSearchDao().retrieve(Project.class, getFileSet().getProjectId());
        if (project != null) {
            this.experiment = project.getExperiment();
        } else {
            this.experiment = null;
        }
        
        this.termTranslator = new TermTranslator(documentSet, translationResult, vocabularyService, daoFactory);        
    }

    @Override
    void translate() {
        for (SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
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

        Set<String> externalIds = getExistingExternalIdsForCurrentExperiment();
        for (SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
            validateSdrf(document, externalIds);
        }
        validateFileReferences();
    }

    private void validateFileReferences() {
        List<String> referencedRawFiles = getDocumentSet().getSdrfReferencedRawFileNames();
        List<String> referencedDerivedFiles = getDocumentSet().getSdrfReferencedDerivedFileNames();
        List<String> referencedDataMatrixFiles = getDocumentSet().getSdrfReferencedDataMatrixFileNames();

        for (CaArrayFile file : getFileSet().getFiles()) {
            FileType fileType = file.getFileType();
            boolean isRaw = fileType.isRawArrayData();
            boolean hasRawVersion = fileType.hasRawVersion();
            boolean referencedAsRaw = referencedRawFiles.contains(file.getName());
            boolean isDerived = fileType.isDerivedArrayData();
            boolean hasDerivedVersion = fileType.hasDerivedVersion();
            boolean referencedAsDerived = referencedDerivedFiles.contains(file.getName());
            boolean isMatrix = fileType == FileType.MAGE_TAB_DATA_MATRIX;
            boolean referencedAsMatrix = referencedDataMatrixFiles.contains(file.getName());
            boolean referencedAsAny = referencedAsRaw || referencedAsDerived || referencedAsMatrix;

            if (isRaw && !referencedAsRaw && (!hasDerivedVersion || !referencedAsDerived)) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.ARRAY_DATA_FILE.getDisplayName());
            } else if (isDerived && !referencedAsDerived && (!hasRawVersion || !referencedAsRaw)) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.DERIVED_ARRAY_DATA_FILE.getDisplayName());
            } else if (isMatrix && !referencedAsMatrix) {
                addFileReferenceError(file, referencedAsAny, SdrfColumnType.ARRAY_DATA_MATRIX_FILE.getDisplayName()
                        + " or " + SdrfColumnType.DERIVED_ARRAY_DATA_MATRIX_FILE.getDisplayName());
            }
        }
    }

    private void addFileReferenceError(CaArrayFile caArrayFile, boolean referencedAsAny, String correctColumn) {
        File file = TemporaryFileCacheLocator.getTemporaryFileCache().getFile(caArrayFile);
        String message = referencedAsAny ? "This file is not correctly referenced from an SDRF file. "
                + "It should be referenced using an " + correctColumn + " column"
                : "This data file is not referenced from an SDRF file.";
        getDocumentSet().getValidationResult().addMessage(file, Type.ERROR, message);
    }



    private void validateSdrf(SdrfDocument document, Set<String> externalIds) {
        validateArrayDesigns(document);
        validateSamples(document, externalIds);
    }

    private void validateSamples(SdrfDocument document, Set<String> externalIds) {
        for (gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBm : document.getAllBiomaterials()) {
            for (Characteristic sdrfCharacteristic : sdrfBm.getCharacteristics()) {
                String category = sdrfCharacteristic.getCategory();
                boolean isExternalId = ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName().equals(category)
                        || ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName().equals(category); 
                if (isExternalId && !StringUtils.isEmpty(sdrfCharacteristic.getValue())) {
                    boolean added = externalIds.add(sdrfCharacteristic.getValue());
                    if (!added) {
                        document.addErrorMessage("[" + category + "] value '" + sdrfCharacteristic.getValue()
                                + "' is referenced multiple times (" + category + " must be unique). "
                                + "Please correct and try again.");
                    }
                }
            }
        }
    }

    private Set<String> getExistingExternalIdsForCurrentExperiment() {
        Set<String> results = new HashSet<String>();
        Set<AbstractBioMaterial> persistedBms = getProjectDao().getUnfilteredBiomaterialsForProject(
                getFileSet().getProjectId());
        for (AbstractBioMaterial bm : persistedBms) {
            if (bm.getExternalId() != null && !results.add(bm.getExternalId())) {
                throw new IllegalStateException("System contains samples with duplicate external sample id " + "("
                        + bm.getExternalId() + ") already. Unable to continue, please correct"
                        + " existing samples and try again.");
            }
        }
        return results;
    }


    private void validateArrayDesigns(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            String arrayDesignName = sdrfArrayDesign.getName();
                ArrayDesign arrayDesign = new ArrayDesign();
                arrayDesign.setLsidForEntity(arrayDesignName);
                if (getDaoFactory().getArrayDao().queryEntityByExample(arrayDesign).isEmpty()) {
                    document.addErrorMessage("Your reference to '" + arrayDesignName + "' can not be resolved because "
                            + "an array design with that LSID is not in caArray.  Please import it and try again.");
                }
            }
        }

    private void translateSdrf(SdrfDocument document) {
        translateNodesToEntities(document);
        linkNodes(document);
        /**
         * added following if statement b/c sdrf doesn't have idf document.  i could be wrong.
         * this was causing error on imports of sdrf.  JH 10/4/07
         *
         */
        if (document.getIdfDocument() != null) {
            String investigationTitle = document.getIdfDocument().getInvestigation().getTitle();
            for (Experiment investigation : getTranslationResult().getInvestigations()) {
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
        for (LabeledExtract labeledExtract : this.allLabeledExtracts) {
            if (labeledExtract.getExtracts().isEmpty()) {
                Extract extract = new Extract();
                extract.setName(labeledExtract.getName());
                extract.getLabeledExtracts().add(labeledExtract);
                labeledExtract.getExtracts().add(extract);
                this.allExtracts.add(extract);
            }
        }
    }

    private void addImplicitSamples() {
        for (Extract extract : this.allExtracts) {
            if (extract.getSamples().isEmpty()) {
                Sample sample = new Sample();
                sample.setName(extract.getName());
                sample.getExtracts().add(extract);
                extract.getSamples().add(sample);
                this.allSamples.add(sample);
            }
        }
    }

    private void addImplicitSources() {
        for (Sample sample : this.allSamples) {
            if (sample.getSources().isEmpty()) {
                Source source = new Source();
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
        for (gov.nih.nci.caarray.magetab.sdrf.Source sdrfSource : document.getAllSources()) {
            Source source = getProjectDao().getSourceForExperiment(this.experiment, sdrfSource.getName());
            if (source == null) {
                source = new Source();
            }
            translateBioMaterial(source, sdrfSource);
            for (Provider sdrfProvider : sdrfSource.getProviders()) {
                Organization organization = getOrCreateOrganization(sdrfProvider.getName());
                source.getProviders().add(organization);
            }
            this.nodeTranslations.put(sdrfSource, source);
            this.allSources.add(source);
        }
    }

    private void translateSamples(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Sample sdrfSample : document.getAllSamples()) {
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
        for (gov.nih.nci.caarray.magetab.sdrf.Extract sdrfExtract : document.getAllExtracts()) {
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
        for (gov.nih.nci.caarray.magetab.sdrf.LabeledExtract sdrfLabeledExtract : document.getAllLabeledExtracts()) {
            LabeledExtract labeledExtract = getProjectDao().getLabeledExtractForExperiment(this.experiment,
                    sdrfLabeledExtract.getName());
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
        for (gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization : document.getAllHybridizations()) {
            String hybridizationName = sdrfHybridization.getName();
            Hybridization hybridization = null;
            if (this.experiment != null) {
                hybridization = this.experiment.getHybridizationByName(hybridizationName);
            }
            // don't update existing hybridizations
            if (hybridization == null) {
                hybridization = new Hybridization();
                hybridization.setName(hybridizationName);
                for (gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal : sdrfHybridization.getFactorValues()) {
                    AbstractFactorValue factorValue = translateFactorValue(sdrfFactorVal);
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
        Term unit = getTerm(sdrfFactorVal.getUnit());
        if (sdrfFactorVal.getTerm() != null) {
            factorValue = new TermBasedFactorValue(getTerm(sdrfFactorVal.getTerm()), unit);
        } else {
            if (unit != null) {
                try {
                    factorValue = new MeasurementFactorValue(Float.valueOf(sdrfFactorVal.getValue()), unit);
                } catch (NumberFormatException e) {
                    // non-Float values that have Units will end up as UserDefined
                }
            }
            if (factorValue == null) {
                factorValue = new UserDefinedFactorValue(sdrfFactorVal.getValue(), unit);
            }
        }
        Factor factor = getTranslationResult().getFactor(sdrfFactorVal.getFactor());
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
            for (gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication : sdrfBiomaterial
                    .getProtocolApplications()) {
                ProtocolApplication protocolApplication
                    = getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
                bioMaterial.addProtocolApplication(protocolApplication);
            }
        }
        bioMaterial.setMaterialType(getTerm(sdrfBiomaterial.getMaterialType()));
        for (Characteristic sdrfCharacteristic : sdrfBiomaterial.getCharacteristics()) {
            AbstractCharacteristic characteristic = translateCharacteristic(sdrfCharacteristic);
            String category = characteristic.getCategory().getName();
            if (ExperimentOntologyCategory.ORGANISM_PART.getCategoryName().equals(category)) {
                bioMaterial.setTissueSite(forceToTerm(characteristic));
            } else if (ExperimentOntologyCategory.CELL_TYPE.getCategoryName().equals(category)) {
                bioMaterial.setCellType(forceToTerm(characteristic));
            } else if (ExperimentOntologyCategory.DISEASE_STATE.getCategoryName().equals(category)) {
                bioMaterial.setDiseaseState(forceToTerm(characteristic));
            } else if (ExperimentOntologyCategory.ORGANISM.getCategoryName().equals(category)) {
                Organism organism = getOrganism(forceToTerm(characteristic));
                bioMaterial.setOrganism(organism);
            } else if (ExperimentOntologyCategory.EXTERNAL_SAMPLE_ID.getCategoryName().equals(category)
                    || ExperimentOntologyCategory.EXTERNAL_ID.getCategoryName().equals(category)) {
                bioMaterial.setExternalId(sdrfCharacteristic.getValue());
            } else {
                for (AbstractCharacteristic existingCharacteristic : bioMaterial.getCharacteristics()) {
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
    }
    
    private Term forceToTerm(AbstractCharacteristic characteristic) {
        if (characteristic instanceof TermBasedCharacteristic) {
            return ((TermBasedCharacteristic) characteristic).getTerm();
        } else {
            String value = characteristic.getDisplayValueWithoutUnit();
            OntologyTerm fakeSdrfTerm = new OntologyTerm();
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
        ProtocolKey key = new ProtocolKey(p.getName(), p.getSource());

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
        Organism o = termToOrganism.get(term);
        if (o == null && term.getSource().getId() != null) {
            o = vocabularyService.getOrganism(term.getSource(), term.getValue());
        }
        if (o == null) {
            o = new Organism();
            o.setScientificName(term.getValue());
            o.setTermSource(term.getSource());
            termToOrganism.put(term, o);
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
            termSource = this.vocabularyService.getSource(ExperimentOntology.CAARRAY.getOntologyName(),
                    ExperimentOntology.CAARRAY.getVersion());
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
        Protocol protocol = getProtocolFromMageTabProtocol(mageTabProtocolApplication.getProtocol());
        ProtocolApplication protocolApplication = new ProtocolApplication();
        protocolApplication.setProtocol(protocol);
        for (gov.nih.nci.caarray.magetab.ParameterValue mageTabValue
                : mageTabProtocolApplication.getParameterValues()) {
            AbstractParameterValue value = null;
            Term unit = getTerm(mageTabValue.getUnit());
            if (mageTabValue.getTerm() != null) {
                value = new TermBasedParameterValue(getTerm(mageTabValue.getTerm()), unit);
            } else {
                if (unit != null) {
                    try {
                        value = new MeasurementParameterValue(Float.valueOf(mageTabValue.getValue()), unit);
                    } catch (NumberFormatException e) {
                        // non-Float values that have Units will end up as UserDefined
                    }
                }
                if (value == null) {
                    value = new UserDefinedParameterValue(mageTabValue.getValue(), unit);
                }
            }
            if (mageTabValue.getParameter() != null) {
                Parameter param = getOrCreateParameter(mageTabValue.getParameter().getName(), protocol);
                value.setParameter(param);
            }
            value.setProtocolApplication(protocolApplication);
            protocolApplication.getValues().add(value);
        }
        return protocolApplication;
    }

    private Parameter getOrCreateParameter(String name, Protocol protocol) {
        Parameter param = (Parameter) paramMap.get(name, protocol);
        if (param == null) {
            param = this.getDaoFactory().getProtocolDao().getParameter(name, protocol);
        }
        if (param == null) {
            param = new Parameter(name, protocol);
            paramMap.put(name, protocol, param);
        }
        return param;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private AbstractCharacteristic translateCharacteristic(Characteristic sdrfCharacteristic) {
        Category category = TermTranslator.getOrCreateCategory(this.vocabularyService, this.getTranslationResult(),
                sdrfCharacteristic.getCategory());
        
        Term unit = getTerm(sdrfCharacteristic.getUnit());
        AbstractCharacteristic chr = null;
        if (sdrfCharacteristic.getTerm() != null) {
            chr = new TermBasedCharacteristic(category, getTerm(sdrfCharacteristic.getTerm()), unit);
        } else {
            if (unit != null) {
                try {
                    chr = new MeasurementCharacteristic(category, Float.valueOf(sdrfCharacteristic.getValue()), unit);
                } catch (NumberFormatException e) {
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
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            ArrayDesign arrayDesign = getArrayDesign(sdrfArrayDesign);
            getTranslationResult().getArrayDesigns().add(arrayDesign);
            if (getTranslationResult().getInvestigations().size() > 0) {
                getTranslationResult().getInvestigations().iterator().next().getArrayDesigns().add(arrayDesign);
            }
        }
    }

    /**
     * Get a caArray ArrayDesign object from an MAGETAB ArrayDesign.
     * @param sdrfArrayDesign MAGETAB array design - must not be null
     */
    private ArrayDesign getArrayDesign(gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign) {
        return processArrayDesignRef(sdrfArrayDesign.getName());
    }

    // Process a reference to an array design in the caArray or in an external database.
    private ArrayDesign processArrayDesignRef(String arrayDesignName) {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setLsidForEntity(arrayDesignName);
        List<ArrayDesign> designs = getDaoFactory().getArrayDao().queryEntityByExample(arrayDesign);
        if (designs.isEmpty()) {
            return null;
        } else {
            return designs.get(0);
        }
    }

    private void translateImages(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : document.getAllImages()) {
            String imageName = sdrfImage.getName();
            CaArrayFile imageFile = getFile(imageName);
            if (imageFile != null) {
                Image image = new Image();
                image.setName(imageName);
                image.setImageFile(imageFile);
                this.nodeTranslations.put(sdrfImage, image);
            }
        }
    }

    private void translateRawArrayData(SdrfDocument document) {
        // Translate native raw data files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfData : document.getAllArrayDataFiles()) {
            translateIndividualRawArrayDataFile(sdrfData, false);
        }
        // Translate MAGE-TAB raw data matrix files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfData : document.getAllArrayDataMatrixFiles()) {
            translateIndividualRawArrayDataFile(sdrfData, true);
        }
    }

    private void translateIndividualRawArrayDataFile(
            gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData, boolean isMatrix) {
        String fileName = sdrfData.getName();
        CaArrayFile dataFile = getFile(fileName);
        RawArrayData caArrayData = null;
        if (EnumSet.of(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED).contains(dataFile.getFileStatus())) {
            caArrayData = getDaoFactory().getArrayDao().getRawArrayData(dataFile);
        } else {
            // this is a re-import referencing an existing data file
            caArrayData = new RawArrayData();
            caArrayData.setName(fileName);
            if (!isMatrix) {
                dataFile.setFileType(dataFile.getFileType().getRawType());
            }
            caArrayData.setDataFile(dataFile);

            Set<gov.nih.nci.caarray.magetab.ProtocolApplication> all =
                    new HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication>();
            all.addAll(sdrfData.getProtocolApplications());
            for (DerivedArrayDataFile df : sdrfData.getSuccessorDerivedArrayDataFiles()) {
                all.addAll(getAllProtocols(df.getPredecessorNormalizations()));
            }
            for (DerivedArrayDataMatrixFile df : sdrfData.getSuccessorDerivedArrayDataMatrixFiles()) {
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
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfData : document.getAllDerivedArrayDataFiles()) {
            translateIndividualDerivedArrayDataFile(sdrfData, false);
        }
        // Translate MAGE-TAB derived data matrix files.
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfData
                : document.getAllDerivedArrayDataMatrixFiles()) {
            translateIndividualDerivedArrayDataFile(sdrfData, true);
        }
    }

    private void translateIndividualDerivedArrayDataFile(
            gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData, boolean isDataMatrix) {
        String fileName = sdrfData.getName();
        CaArrayFile dataFile = getFile(fileName);
        DerivedArrayData caArrayData = null;
        if (EnumSet.of(FileStatus.IMPORTED, FileStatus.IMPORTED_NOT_PARSED).contains(dataFile.getFileStatus())) {
            caArrayData = getDaoFactory().getArrayDao().getDerivedArrayData(dataFile);
        } else {
            caArrayData = new DerivedArrayData();
            caArrayData.setName(fileName);
            if (!isDataMatrix) {
                dataFile.setFileType(dataFile.getFileType().getDerivedType());
            }
            caArrayData.setDataFile(dataFile);
            associateProtocolApplications(caArrayData.getProtocolApplications(), sdrfData.getProtocolApplications());
        }
        setDerivedFromData(sdrfData, caArrayData);
        this.nodeTranslations.put(sdrfData, caArrayData);        
    }

    private void setDerivedFromData(gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode sdrfData,
            DerivedArrayData caArrayData) {
        List<AbstractSampleDataRelationshipNode> allArrayData =
            new ArrayList<AbstractSampleDataRelationshipNode>(sdrfData.getPredecessorArrayDataFiles());
        allArrayData.addAll(sdrfData.getPredecessorDerivedArrayDataFiles());
        for (AbstractSampleDataRelationshipNode sdrfArrayData : allArrayData) {
            AbstractArrayData arrayData = (AbstractArrayData) this.nodeTranslations.get(sdrfArrayData);
            caArrayData.getDerivedFromArrayDataCollection().add(arrayData);
        }
    }

    private void associateProtocolApplications(Collection<ProtocolApplication> dest,
            Collection<gov.nih.nci.caarray.magetab.ProtocolApplication> sdrfProtocolapplications) {
        for (gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication
                : sdrfProtocolapplications) {
            ProtocolApplication protocolApplication =
                getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
            dest.add(protocolApplication);

        }
    }

    private static Set<gov.nih.nci.caarray.magetab.ProtocolApplication> getAllProtocols(
            Set<? extends AbstractSampleDataRelationshipNode> nodes) {
       HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication> all =
               new HashSet<gov.nih.nci.caarray.magetab.ProtocolApplication>();
       for (AbstractSampleDataRelationshipNode n : nodes) {
           all.addAll(n.getProtocolApplications());
       }
       return all;
    }

    private void linkNodes(SdrfDocument document) {
        for (AbstractSampleDataRelationshipNode currNode : document.getLeftmostNodes()) {
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
        Boolean isLinked = this.isNodeLinked.get(node);
        if ((isLinked != null) && (isLinked.booleanValue())) {
            return;
        }
        for (AbstractSampleDataRelationshipNode successor : node.getSuccessors()) {
            // Recursively link all successors of this node.
            linkNode(successor);
            // Link this node to its successor.
            linkTwoNodes(node, successor);
        }
        // Finished linking node. Mark it so that we don't do it again.
        this.isNodeLinked.put(node, Boolean.TRUE);
    }

    // Link a node with one successor.
    private void linkTwoNodes(AbstractSampleDataRelationshipNode leftNode,
        AbstractSampleDataRelationshipNode rightNode) {
        AbstractCaArrayObject leftCaArrayNode = this.nodeTranslations.get(leftNode);
        AbstractCaArrayObject rightCaArrayNode = this.nodeTranslations.get(rightNode);
        SdrfNodeType leftNodeType = leftNode.getNodeType();
        SdrfNodeType rightNodeType = rightNode.getNodeType();
        // if either node is null, it means it wasn't translated (because it was an update of existing data and that
        // type of node doesn't get updated), so don't try to link it
        if (isBioMaterial(leftNodeType) && rightCaArrayNode != null) {
            // Use the left node's name as part of any generated biomaterial names.
            String baseGeneratedNodeName = ((AbstractBioMaterial) leftCaArrayNode).getName();
            List<ProtocolApplication> pas = ((AbstractBioMaterial) leftCaArrayNode).getProtocolApplications();
            linkBioMaterial(leftCaArrayNode, rightCaArrayNode, leftNodeType, rightNodeType, baseGeneratedNodeName, pas);
        } else if (SdrfNodeType.HYBRIDIZATION.equals(leftNodeType)) {
            Hybridization hybridization = (Hybridization) leftCaArrayNode;
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
        Array array = new Array();
        Array currArray = hybridization.getArray();
        // a new hyb should always have an array, even if the array isn't associated with a design
        if (currArray == null) {
            hybridization.setArray(array);
        }

        // if the sdrf hyb has an array design, only associate it with the array if the current hyb
        // doesn't have an array or if the new design is different from the old one
        if (sdrfHybridization.getArrayDesign() != null) {
            ArrayDesign sdrfArrayDesign = getArrayDesign(sdrfHybridization.getArrayDesign());
            if (sdrfArrayDesign != null
                    && (currArray == null || !currArray.getDesign().getLsid().equals(sdrfArrayDesign.getLsid()))) {
                array.setDesign(sdrfArrayDesign);
                hybridization.setArray(array);
            }
        }
    }

    private void linkHybridizationToImages(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : sdrfHybridization.getSuccessorImages()) {
            Image image = (Image) this.nodeTranslations.get(sdrfImage);
            if (image != null) {
                hybridization.getImages().add(image);
            }
        }
    }

    private void linkHybridizationToArrayData(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        // Link raw array data
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataFiles()) {
            RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.addRawArrayData(arrayData);
            }
        }
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataMatrixFiles()) {
            RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.addRawArrayData(arrayData);
            }
        }
        // Link derived array data
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getDerivedDataCollection().add(arrayData);
            }
        }
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataMatrixFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            if (arrayData != null) {
                arrayData.addHybridization(hybridization);
                hybridization.getDerivedDataCollection().add(arrayData);
            }
        }
    }

    /**
     * Links a BioMaterial node with one successor.
     * If a node is missing in the chain Source -> Sample -> Extract -> LabeledExtract -> Hybridization,
     * appropriate intermediate nodes will be generated to complete the chain. The number of nodes
     * generated depends on the left side of the graph. E.g., 1 Source going to 3 Extracts will result
     * in 1 Sample being generated. On the other hand, 3 Sources going to 1 Extract will result in 3 Samples
     * being generated.
     */
    @SuppressWarnings("PMD")
    private void linkBioMaterial(AbstractCaArrayObject leftCaArrayNode, AbstractCaArrayObject rightCaArrayNode,
            SdrfNodeType leftNodeType, SdrfNodeType rightNodeType, String baseGeneratedNodeName,
            Collection<ProtocolApplication> protocolApplications) {
        if (leftNodeType.equals(SdrfNodeType.SOURCE)) {
            if (rightNodeType.equals(SdrfNodeType.SAMPLE)) {
                linkSourceAndSample((Source) leftCaArrayNode, (Sample) rightCaArrayNode);
            } else {
                Sample generatedSample = generateSampleAndLink(baseGeneratedNodeName, (Source) leftCaArrayNode);
                reassociateProtocolApplications(generatedSample, protocolApplications);
                linkBioMaterial(generatedSample, rightCaArrayNode, SdrfNodeType.SAMPLE, rightNodeType,
                        baseGeneratedNodeName, protocolApplications);
            }
        } else if (leftNodeType.equals(SdrfNodeType.SAMPLE)) {
            if (rightNodeType.equals(SdrfNodeType.EXTRACT)) {
                linkSampleAndExtract((Sample) leftCaArrayNode, (Extract) rightCaArrayNode);
            } else {
                Extract generatedExtract = generateExtractAndLink(baseGeneratedNodeName, (Sample) leftCaArrayNode);
                reassociateProtocolApplications(generatedExtract, protocolApplications);
                linkBioMaterial(generatedExtract, rightCaArrayNode, SdrfNodeType.EXTRACT, rightNodeType,
                        baseGeneratedNodeName, protocolApplications);
            }
        } else if (leftNodeType.equals(SdrfNodeType.EXTRACT)) {
            if (rightNodeType.equals(SdrfNodeType.LABELED_EXTRACT)) {
                linkExtractAndLabeledExtract((Extract) leftCaArrayNode, (LabeledExtract) rightCaArrayNode);
            } else {
                LabeledExtract generatedLabeledExtract = generateLabeledExtractAndLink(baseGeneratedNodeName,
                        (Extract) leftCaArrayNode);
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
        for (Iterator<ProtocolApplication> i = protocolApplications.iterator(); i.hasNext();) {
            ProtocolApplication pa = i.next();
            Term protocolType = pa.getProtocol().getType();
            for (ProtocolTypeAssociation typeAssoc : ProtocolTypeAssociation.values()) {
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
        String sampleName = GENERATED_SAMPLE_PREFIX + baseGeneratedNodeName;
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
        String extractName = GENERATED_EXTRACT_PREFIX + baseGeneratedNodeName;
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
        String labeledExtractName = GENERATED_LABELED_EXTRACT_PREFIX + baseGeneratedNodeName;
        LabeledExtract generatedLabeledExtract = (LabeledExtract) this.generatedNodes.get(labeledExtractName);
        if (generatedLabeledExtract == null) {
            generatedLabeledExtract = getProjectDao().getLabeledExtractForExperiment(this.experiment,
                    labeledExtractName);
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
        CaArrayFileSet fs = new CaArrayFileSet(getFileSet());
        if (experiment != null) {
            fs.addAll(Collections2.filter(experiment.getProject().getImportedFiles(), new Predicate<CaArrayFile>() {
                public boolean apply(CaArrayFile f) {
                    return f.getFileType().isArrayData() || FileType.MAGE_TAB_DATA_MATRIX.equals(f.getFileType());
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
