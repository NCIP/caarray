//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.application.translation.magetab;

import gov.nih.nci.caarray.domain.contact.AbstractContact;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.contact.Person;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
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
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.MageTabFileSet;
import gov.nih.nci.caarray.magetab.OntologyTerm;
import gov.nih.nci.caarray.magetab.TermSource;
import gov.nih.nci.caarray.magetab.idf.IdfDocument;
import gov.nih.nci.caarray.magetab.idf.Investigation;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile;
import gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocumentNodes;
import gov.nih.nci.caarray.util.io.logging.LogUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

/**
 * Exports an Experiment into MAGE-TAB format. An IDF file and an SDRF file are generated.
 *
 * @author Rashmi Srinivasa
 */
@Local(MageTabExporter.class)
@Stateless
@SuppressWarnings("PMD.CyclomaticComplexity")
public class MageTabExporterBean implements MageTabExporter {
    private static final Logger LOG = Logger.getLogger(MageTabExporterBean.class);

    // Generated SDRF nodes
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Source> allSources =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Source>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Sample> allSamples =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Sample>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Extract> allExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Extract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract> allLabeledExtracts =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.LabeledExtract>();
    private final Set<gov.nih.nci.caarray.magetab.sdrf.Hybridization> allHybridizations =
        new HashSet<gov.nih.nci.caarray.magetab.sdrf.Hybridization>();
    private final Set<ArrayDataFile> allArrayDataFiles = new HashSet<ArrayDataFile>();
    private final Set<DerivedArrayDataFile> allDerivedArrayDataFiles = new HashSet<DerivedArrayDataFile>();
    private final Set<ArrayDataMatrixFile> allArrayDataMatrixFiles = new HashSet<ArrayDataMatrixFile>();
    private final Set<DerivedArrayDataMatrixFile> allDerivedArrayDataMatrixFiles =
        new HashSet<DerivedArrayDataMatrixFile>();

    // Temporary caches
    private final Map<NodeKey, AbstractSampleDataRelationshipNode> nodeCache =
        new HashMap<NodeKey, AbstractSampleDataRelationshipNode>();
    private final Map<Term, OntologyTerm> termMap = new HashMap<Term, OntologyTerm>();
    private final Map<gov.nih.nci.caarray.domain.vocabulary.TermSource, TermSource> termSourceMap =
        new HashMap<gov.nih.nci.caarray.domain.vocabulary.TermSource, TermSource>();

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public MageTabDocumentSet exportToMageTab(Experiment experiment, File idfFile, File sdrfFile) {
        LogUtil.logSubsystemEntry(LOG, experiment);

        MageTabFileSet fileSet = new MageTabFileSet();
        fileSet.addIdf(idfFile);
        fileSet.addSdrf(sdrfFile);

        // Translate caArray domain object graph into MAGE-TAB object graph, and create MAGE-TAB documents.
        MageTabDocumentSet mageTabDocumentSet = translateToMageTab(experiment, fileSet);

        // Ask the MAGE-TAB documents to export themselves into their respective files.
        mageTabDocumentSet.export();

        // Clear private caches.
        nodeCache.clear();
        termMap.clear();
        termSourceMap.clear();
        allSources.clear();
        allSamples.clear();
        allExtracts.clear();
        allLabeledExtracts.clear();
        allHybridizations.clear();
        allArrayDataFiles.clear();
        allDerivedArrayDataFiles.clear();
        allArrayDataMatrixFiles.clear();
        allDerivedArrayDataMatrixFiles.clear();

        LogUtil.logSubsystemExit(LOG);
        return mageTabDocumentSet;
    }

    /**
     * Takes an Experiment and constructs a MAGE-TAB IDF and SDRF describing the sample-data relationships and
     * annotations.
     *
     * @param experiment the experiment whose contents need to be translated into MAGE-TAB.
     * @return a MAGE-TAB SDRF.
     */
    private MageTabDocumentSet translateToMageTab(Experiment experiment, MageTabFileSet fileSet) {
        MageTabDocumentSet mageTabDocumentSet = new MageTabDocumentSet(fileSet);
        IdfDocument idfDocument = mageTabDocumentSet.getIdfDocuments().iterator().next();
        SdrfDocument sdrfDocument = mageTabDocumentSet.getSdrfDocuments().iterator().next();

        // Create the IDF elements.
        translateIdfElements(experiment, idfDocument);

        // Create the SDRF elements.
        translateSdrfElements(experiment, sdrfDocument);

        // Add terms and term sources to the document set.
        mageTabDocumentSet.getTerms().addAll(termMap.values());
        for (TermSource termSource : termSourceMap.values()) {
            String name = termSource.getName();
            mageTabDocumentSet.getTermSourceMap().put(name, termSource);
        }

        // Link the IDF and SDRF documents together.
        idfDocument.getSdrfDocuments().add(sdrfDocument);
        sdrfDocument.setIdfDocument(idfDocument);

        return mageTabDocumentSet;
    }

    private void translateIdfElements(Experiment experiment, IdfDocument idfDocument) {
        Investigation investigation = idfDocument.getInvestigation();
        investigation.setTitle(experiment.getTitle());
        investigation.setDescription(experiment.getDescription());
        // TODO Translate other IDF elements.
    }

    private void translateSdrfElements(Experiment experiment, SdrfDocument sdrfDocument) {
        // Translate caArray domain biomaterial-data chains into MAGE-TAB biomaterial-data chains.
        for (Source source : experiment.getSources()) {
            AbstractSampleDataRelationshipNode sdrfSource = getOrCreateNode(source);
            addBiomaterialAttributes(source, (gov.nih.nci.caarray.magetab.sdrf.Source) sdrfSource);
            addSourceAttributes(source, sdrfSource);
            handleSamples(source, sdrfSource);
        }

        // Initialize the SDRF document with all the object graph nodes.
        SdrfDocumentNodes sdrfDocumentNodes = new SdrfDocumentNodes();
        sdrfDocumentNodes.initNonDataNodes(allSources, allSamples, allExtracts, allLabeledExtracts, allHybridizations);
        sdrfDocumentNodes.initDataNodes(allArrayDataFiles, allArrayDataMatrixFiles, allDerivedArrayDataFiles,
                allDerivedArrayDataMatrixFiles);
        sdrfDocument.initializeNodes(sdrfDocumentNodes);

        // TODO Translate other SDRF elements, i.e., protocols, experimental factors.
    }

    private void addSourceAttributes(Source source, AbstractSampleDataRelationshipNode sdrfNode) {
        gov.nih.nci.caarray.magetab.sdrf.Source sdrfSource = (gov.nih.nci.caarray.magetab.sdrf.Source) sdrfNode;

        // Set providers.
        for (AbstractContact contact : source.getProviders()) {
            if (contact.getClass() == Organization.class) {
                Organization org = (Organization) contact;
                Provider provider = new Provider();
                provider.setName(org.getName());
                sdrfSource.getProviders().add(provider);
            } else if (contact.getClass() == Person.class) {
                Person person = (Person) contact;
                Provider provider = new Provider();
                provider.setName(person.getLastName());
                sdrfSource.getProviders().add(provider);
            }
        }
    }

    private void addBiomaterialAttributes(AbstractBioMaterial biomaterial,
            gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        // Set material type.
        Term term = biomaterial.getMaterialType();
        if (term != null) {
            OntologyTerm sdrfTerm = getSdrfTerm(term);
            sdrfBiomaterial.setMaterialType(sdrfTerm);
        }

        // Set all other characteristics.
        addPredefinedCharacteristics(biomaterial, sdrfBiomaterial);
        for (AbstractCharacteristic characteristic : biomaterial.getCharacteristics()) {
            String category = characteristic.getCategory().getName();
            Characteristic sdrfCharacteristic = new Characteristic();
            if (characteristic instanceof TermBasedCharacteristic) {
                Term characteristicTerm = ((TermBasedCharacteristic) characteristic).getTerm();
                OntologyTerm sdrfTerm = getSdrfTerm(characteristicTerm);
                sdrfCharacteristic.setTerm(sdrfTerm);
            } else if (characteristic instanceof MeasurementCharacteristic) {
                float value = ((MeasurementCharacteristic) characteristic).getValue();
                sdrfCharacteristic.setValue(Float.toString(value));
            } else {
                sdrfCharacteristic.setValue(((UserDefinedCharacteristic) characteristic).getValue());
            }
            sdrfCharacteristic.setCategory(category);
            OntologyTerm sdrfUnit = getSdrfTerm(characteristic.getUnit());
            sdrfCharacteristic.setUnit(sdrfUnit);
            sdrfBiomaterial.getCharacteristics().add(sdrfCharacteristic);
        }

        // TODO Organism and ProtocolApplications
    }

    private void addPredefinedCharacteristics(AbstractBioMaterial biomaterial,
            gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        Term term;
        OntologyTerm sdrfTerm;
        term = biomaterial.getDiseaseState();
        if (term != null) {
            sdrfTerm = getSdrfTerm(term);
            Characteristic sdrfTermBasedCharacteristic = createSdrfTermBasedCharacteristic(
                    ExperimentOntologyCategory.DISEASE_STATE.getCategoryName(), sdrfTerm);
            sdrfBiomaterial.getCharacteristics().add(sdrfTermBasedCharacteristic);
        }
        term = biomaterial.getCellType();
        if (term != null) {
            sdrfTerm = getSdrfTerm(term);
            Characteristic sdrfTermBasedCharacteristic = createSdrfTermBasedCharacteristic(
                    ExperimentOntologyCategory.CELL_TYPE.getCategoryName(), sdrfTerm);
            sdrfBiomaterial.getCharacteristics().add(sdrfTermBasedCharacteristic);
        }
        term = biomaterial.getTissueSite();
        if (term != null) {
            sdrfTerm = getSdrfTerm(term);
            Characteristic sdrfTermBasedCharacteristic = createSdrfTermBasedCharacteristic(
                    ExperimentOntologyCategory.ORGANISM_PART.getCategoryName(), sdrfTerm);
            sdrfBiomaterial.getCharacteristics().add(sdrfTermBasedCharacteristic);
        }
    }

    private Characteristic createSdrfTermBasedCharacteristic(String category, OntologyTerm sdrfTerm) {
        Characteristic sdrfCharacteristic = new Characteristic();
        sdrfCharacteristic.setCategory(category);
        sdrfCharacteristic.setTerm(sdrfTerm);
        return sdrfCharacteristic;
    }

    private OntologyTerm getSdrfTerm(Term term) {
        if (term == null) {
            return null;
        }
        OntologyTerm sdrfTerm = termMap.get(term);
        if (sdrfTerm == null) {
            sdrfTerm = createSdrfTerm(term);
        }
        return sdrfTerm;
    }

    private OntologyTerm createSdrfTerm(Term term) {
        OntologyTerm sdrfTerm = new OntologyTerm();
        sdrfTerm.setValue(term.getValue());
        if (!(term.getCategories().isEmpty())) {
            sdrfTerm.setCategory(term.getCategories().iterator().next().getName());
        }
        TermSource sdrfTermSource = getSdrfTermSource(term);
        sdrfTerm.setTermSource(sdrfTermSource);
        termMap.put(term, sdrfTerm);
        return sdrfTerm;
    }

    private TermSource getSdrfTermSource(Term term) {
        gov.nih.nci.caarray.domain.vocabulary.TermSource termSource = term.getSource();
        // If the term doesn't have a source, get the term's category's source.
        if ((termSource == null) && (!(term.getCategories().isEmpty()))) {
            Iterator<Category> iterator = term.getCategories().iterator();
            if (iterator.hasNext()) {
                termSource = iterator.next().getSource();
            }
        }
        if (termSource == null) {
            return null;
        }

        TermSource sdrfTermSource = termSourceMap.get(termSource);
        if (sdrfTermSource == null) {
            sdrfTermSource = new TermSource(termSource.getName());
            sdrfTermSource.setVersion(term.getSource().getVersion());
            sdrfTermSource.setFile(term.getSource().getUrl());
            termSourceMap.put(termSource, sdrfTermSource);
        }
        return sdrfTermSource;
    }

    private void handleSamples(Source source, AbstractSampleDataRelationshipNode sdrfSource) {
        for (Sample sample : source.getSamples()) {
            AbstractSampleDataRelationshipNode sdrfSample = getOrCreateNode(sample);
            sdrfSample.getPredecessors().add(sdrfSource);
            sdrfSource.getSuccessors().add(sdrfSample);
            addBiomaterialAttributes(sample, (gov.nih.nci.caarray.magetab.sdrf.Sample) sdrfSample);
            // TODO Add external Sample ID
            handleExtracts(sample, sdrfSample);
        }
    }

    private void handleExtracts(Sample sample, AbstractSampleDataRelationshipNode sdrfSample) {
        for (Extract extract : sample.getExtracts()) {
            AbstractSampleDataRelationshipNode sdrfExtract = getOrCreateNode(extract);
            sdrfExtract.getPredecessors().add(sdrfSample);
            sdrfSample.getSuccessors().add(sdrfExtract);
            addBiomaterialAttributes(extract, (gov.nih.nci.caarray.magetab.sdrf.Extract) sdrfExtract);
            handleLabeledExtracts(extract, sdrfExtract);
        }
    }

    private void handleLabeledExtracts(Extract extract, AbstractSampleDataRelationshipNode sdrfExtract) {
        for (LabeledExtract labeledExtract : extract.getLabeledExtracts()) {
            AbstractSampleDataRelationshipNode sdrfLabeledExtract = getOrCreateNode(labeledExtract);
            sdrfLabeledExtract.getPredecessors().add(sdrfExtract);
            sdrfExtract.getSuccessors().add(sdrfLabeledExtract);
            addBiomaterialAttributes(labeledExtract,
                    (gov.nih.nci.caarray.magetab.sdrf.LabeledExtract) sdrfLabeledExtract);
            addLabeledExtractAttributes(labeledExtract,
                    (gov.nih.nci.caarray.magetab.sdrf.LabeledExtract) sdrfLabeledExtract);
            handleHybridizations(labeledExtract, sdrfLabeledExtract);
        }
    }

    private void handleHybridizations(LabeledExtract labeledExtract,
            AbstractSampleDataRelationshipNode sdrfLabeledExtract) {
        for (Hybridization hybridization : labeledExtract.getHybridizations()) {
            AbstractSampleDataRelationshipNode sdrfHybridization = getOrCreateNode(hybridization);
            sdrfHybridization.getPredecessors().add(sdrfLabeledExtract);
            sdrfLabeledExtract.getSuccessors().add(sdrfHybridization);
            handleRawData(hybridization, sdrfHybridization);
            handleDerivedData(hybridization, sdrfHybridization);
        }
    }

    private void handleRawData(Hybridization hybridization, AbstractSampleDataRelationshipNode sdrfHybridization) {
        for (RawArrayData rawData : hybridization.getRawDataCollection()) {
            AbstractSampleDataRelationshipNode sdrfRawData = getOrCreateNode(rawData);
            sdrfRawData.getPredecessors().add(sdrfHybridization);
            sdrfHybridization.getSuccessors().add(sdrfRawData);
        }
    }

    private void handleDerivedData(Hybridization hybridization, AbstractSampleDataRelationshipNode sdrfHybridization) {
        for (DerivedArrayData derivedData : hybridization.getDerivedDataCollection()) {
            AbstractSampleDataRelationshipNode sdrfDerivedData = getOrCreateNode(derivedData);
            Set<AbstractArrayData> derivedFromDataSet = derivedData.getDerivedFromArrayDataCollection();
            if (derivedFromDataSet.isEmpty()) {
                AbstractSampleDataRelationshipNode unavailableRawData = createNode(new RawArrayData());
                sdrfDerivedData.getPredecessors().add(unavailableRawData);
                unavailableRawData.getSuccessors().add(sdrfDerivedData);
                unavailableRawData.getPredecessors().add(sdrfHybridization);
                sdrfHybridization.getSuccessors().add(unavailableRawData);
            } else {
                for (AbstractArrayData derivedFromData : derivedFromDataSet) {
                    AbstractSampleDataRelationshipNode sdrfDerivedFromData = getOrCreateNode(derivedFromData);
                    sdrfDerivedData.getPredecessors().add(sdrfDerivedFromData);
                    sdrfDerivedFromData.getSuccessors().add(sdrfDerivedData);
                }
            }
        }
    }

    private void addLabeledExtractAttributes(LabeledExtract labeledExtract,
            gov.nih.nci.caarray.magetab.sdrf.LabeledExtract sdrfLabeledExtract) {
        // Set label.
        Term term = labeledExtract.getLabel();
        if (term != null) {
            OntologyTerm sdrfTerm = getSdrfTerm(term);
            sdrfLabeledExtract.setLabel(sdrfTerm);
        }
    }

    private AbstractSampleDataRelationshipNode getOrCreateNode(
            gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode caarrayNode) {
        NodeKey nodeKey = new NodeKey(caarrayNode.getClass(), caarrayNode.getName());
        AbstractSampleDataRelationshipNode node = nodeCache.get(nodeKey);
        if (node == null) {
            node = createNode(caarrayNode);
            nodeCache.put(nodeKey, node);
        }
        return node;
    }

    @SuppressWarnings("PMD")
    // warnings suppressed due to long switch statement
    private AbstractSampleDataRelationshipNode createNode(
            gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode caarrayNode) {
        AbstractSampleDataRelationshipNode node = null;
        switch (caarrayNode.getNodeType()) {
        case SOURCE:
            node = new gov.nih.nci.caarray.magetab.sdrf.Source();
            allSources.add((gov.nih.nci.caarray.magetab.sdrf.Source) node);
            break;
        case SAMPLE:
            node = new gov.nih.nci.caarray.magetab.sdrf.Sample();
            allSamples.add((gov.nih.nci.caarray.magetab.sdrf.Sample) node);
            break;
        case EXTRACT:
            node = new gov.nih.nci.caarray.magetab.sdrf.Extract();
            allExtracts.add((gov.nih.nci.caarray.magetab.sdrf.Extract) node);
            break;
        case LABELED_EXTRACT:
            node = new gov.nih.nci.caarray.magetab.sdrf.LabeledExtract();
            allLabeledExtracts.add((gov.nih.nci.caarray.magetab.sdrf.LabeledExtract) node);
            break;
        case HYBRIDIZATION:
            node = new gov.nih.nci.caarray.magetab.sdrf.Hybridization();
            allHybridizations.add((gov.nih.nci.caarray.magetab.sdrf.Hybridization) node);
            break;
        default:
            // Should never get here.
            break;
        }
        node.setName(caarrayNode.getName());
        return node;
    }

    private AbstractSampleDataRelationshipNode getOrCreateNode(
            gov.nih.nci.caarray.domain.data.AbstractArrayData caarrayNode) {
        NodeKey nodeKey = new NodeKey(caarrayNode.getClass(), caarrayNode.getName());
        AbstractSampleDataRelationshipNode node = nodeCache.get(nodeKey);
        if (node == null) {
            node = createNode(caarrayNode);
            nodeCache.put(nodeKey, node);
        }
        return node;
    }

    private AbstractSampleDataRelationshipNode createNode(AbstractArrayData caarrayNode) {
        AbstractSampleDataRelationshipNode node = null;
        if (caarrayNode.getClass() == RawArrayData.class) {
            CaArrayFile file = ((RawArrayData) caarrayNode).getDataFile();
            if ((file == null) || (file.getFileType() != FileType.MAGE_TAB_DATA_MATRIX)) {
                node = new gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile();
                allArrayDataFiles.add((gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile) node);
            } else {
                node = new gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile();
                allArrayDataMatrixFiles.add((gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile) node);
            }
        } else if (caarrayNode.getClass() == DerivedArrayData.class) {
            FileType fileType = ((DerivedArrayData) caarrayNode).getDataFile().getFileType();
            if (fileType == FileType.MAGE_TAB_DATA_MATRIX) {
                node = new gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile();
                allDerivedArrayDataMatrixFiles.add((gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile) node);
            } else {
                node = new gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile();
                allDerivedArrayDataFiles.add((gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile) node);
            }
        }
        node.setName(caarrayNode.getName());
        return node;
    }

    /**
     * Compound key for node lookup and caching.
     */
    private static final class NodeKey {

        private final Class<?> nodeClass;
        private final String nodeName;

        NodeKey(Class<?> nodeClass, String nodeName) {
            this.nodeClass = nodeClass;
            this.nodeName = nodeName;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof NodeKey)) {
                return false;
            } else {
                NodeKey nodeKey = (NodeKey) obj;
                return nodeClass.equals(nodeKey.nodeClass) && nodeName.equals(nodeKey.nodeName);
            }
        }

        @Override
        public int hashCode() {
            return nodeClass.hashCode() + nodeName.hashCode();
        }
    }
}
