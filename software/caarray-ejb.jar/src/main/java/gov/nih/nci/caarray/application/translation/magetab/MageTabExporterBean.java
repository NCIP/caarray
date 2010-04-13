/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-ejb-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-ejb-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-ejb-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-ejb-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-ejb-jar Software and any
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
import gov.nih.nci.caarray.magetab.io.JavaIOFileRef;
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
        fileSet.addIdf(new JavaIOFileRef(idfFile));
        fileSet.addSdrf(new JavaIOFileRef(sdrfFile));

        // Translate caArray domain object graph into MAGE-TAB object graph, and new_JavaIOFileRef MAGE-TAB documents.
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
