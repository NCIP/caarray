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
package gov.nih.nci.caarray.application.translation.magetab;

import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.business.vocabulary.VocabularyService;
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
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentOntology;
import gov.nih.nci.caarray.domain.project.ExperimentOntologyCategory;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.protocol.Parameter;
import gov.nih.nci.caarray.domain.protocol.ParameterValue;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.protocol.ProtocolTypeAssociation;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.MeasurementCharacteristic;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.domain.sample.TermBasedCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.Normalization;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.Scan;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfNodeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;

/**
 * Translates entities in SDRF documents into caArray entities.
 */
@SuppressWarnings("PMD")
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

    SdrfTranslator(MageTabDocumentSet documentSet, CaArrayFileSet fileSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory, VocabularyService vocabularyService) {
        super(documentSet, fileSet, translationResult, daoFactory);
        this.vocabularyService = vocabularyService;
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
        for (SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
            validateSdrf(document);
        }
    }

    private void validateSdrf(SdrfDocument document) {
        validateArrayDesigns(document);
    }

    private void validateArrayDesigns(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            String arrayDesignName = sdrfArrayDesign.getName();
                ArrayDesign arrayDesign = new ArrayDesign();
                arrayDesign.setLsidForEntity(arrayDesignName);
                if (getDaoFactory().getArrayDao().queryEntityAndAssociationsByExample(arrayDesign).isEmpty()) {
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
            Source source = new Source();
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
            Sample sample = new Sample();
            translateBioMaterial(sample, sdrfSample);
            this.nodeTranslations.put(sdrfSample, sample);
            this.allSamples.add(sample);
        }
    }

    private void translateExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Extract sdrfExtract : document.getAllExtracts()) {
            Extract extract = new Extract();
            translateBioMaterial(extract, sdrfExtract);
            this.nodeTranslations.put(sdrfExtract, extract);
            this.allExtracts.add(extract);
        }
    }

    private void translateLabeledExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.LabeledExtract sdrfLabeledExtract : document.getAllLabeledExtracts()) {
            LabeledExtract labeledExtract = new LabeledExtract();
            translateBioMaterial(labeledExtract, sdrfLabeledExtract);
            labeledExtract.setLabel(getTerm(sdrfLabeledExtract.getLabel()));
            this.nodeTranslations.put(sdrfLabeledExtract, labeledExtract);
            this.allLabeledExtracts.add(labeledExtract);
        }
    }

    private void translateHybridizations(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization : document.getAllHybridizations()) {
            Hybridization hybridization = new Hybridization();
            hybridization.setName(sdrfHybridization.getName());
            for (gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal : sdrfHybridization.getFactorValues()) {
                FactorValue factorValue = translateFactor(sdrfFactorVal);
                hybridization.getFactorValues().add(factorValue);
                factorValue.setHybridization(hybridization);
            }

            for (gov.nih.nci.caarray.magetab.ProtocolApplication sdrfProtocolApp
                    : sdrfHybridization.getProtocolApplications()) {
                ProtocolApplication protocolApplication =
                    getProtocolApplicationFromMageTabProtocolApplication(sdrfProtocolApp);
                hybridization.addProtocolApplication(protocolApplication);
            }

            this.allHybridizations.add(hybridization);
            this.nodeTranslations.put(sdrfHybridization, hybridization);
        }
    }

    private FactorValue translateFactor(gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal) {
        FactorValue factorValue = new FactorValue();
        factorValue.setValue(sdrfFactorVal.getValue());
        factorValue.setUnit(getTerm(sdrfFactorVal.getUnit()));
        Factor factor = getTranslationResult().getFactor(sdrfFactorVal.getFactor());
        factorValue.setFactor(factor);
        factor.getFactorValues().add(factorValue);
        return factorValue;
    }

    private void translateBioMaterial(AbstractBioMaterial bioMaterial,
            gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        bioMaterial.setName(sdrfBiomaterial.getName());
        bioMaterial.setDescription(sdrfBiomaterial.getDescription());
        bioMaterial.setMaterialType(getTerm(sdrfBiomaterial.getMaterialType()));
        for (Characteristic sdrfCharacteristic : sdrfBiomaterial.getCharacteristics()) {
            AbstractCharacteristic characteristic =
                translateCharacteristic(sdrfCharacteristic);
            String category = characteristic.getCategory().getName();
            if (ExperimentOntologyCategory.ORGANISM_PART.getCategoryName().equals(category)) {
                bioMaterial.setTissueSite(((TermBasedCharacteristic) characteristic).getTerm());
            } else if (ExperimentOntologyCategory.CELL_TYPE.getCategoryName().equals(category)) {
                bioMaterial.setCellType(((TermBasedCharacteristic) characteristic).getTerm());
            } else if (ExperimentOntologyCategory.DISEASE_STATE.getCategoryName().equals(category)) {
                bioMaterial.setDiseaseState(((TermBasedCharacteristic) characteristic).getTerm());
            } else if (ExperimentOntologyCategory.ORGANISM.getCategoryName().equals(category)) {
                Organism organism = getOrganism(((TermBasedCharacteristic) characteristic).getTerm());
                bioMaterial.setOrganism(organism);
            } else {
                bioMaterial.getCharacteristics().add(characteristic);
                characteristic.setBioMaterial(bioMaterial);
            }
        }
        for (gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication
                : sdrfBiomaterial.getProtocolApplications()) {
            ProtocolApplication protocolApplication =
                getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
            bioMaterial.addProtocolApplication(protocolApplication);
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

    private ProtocolApplication getProtocolApplicationFromMageTabProtocolApplication(
            gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication) {
        Protocol protocol = getProtocolFromMageTabProtocol(mageTabProtocolApplication.getProtocol());
        ProtocolApplication protocolApplication = new ProtocolApplication();
        protocolApplication.setProtocol(protocol);
        for (gov.nih.nci.caarray.magetab.ParameterValue mageTabValue
                : mageTabProtocolApplication.getParameterValues()) {
            ParameterValue value = new ParameterValue();
            if (mageTabValue.getParameter() != null) {
                Parameter param = getOrCreateParameter(mageTabValue.getParameter().getName(), protocol);
                value.setParameter(param);
            }
            value.setValue(mageTabValue.getValue());
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

    private AbstractCharacteristic translateCharacteristic(
            Characteristic sdrfCharacteristic) {
        Category category = TermTranslator.getOrCreateCategory(this.vocabularyService, this.getTranslationResult(),
                sdrfCharacteristic.getCategory());
        if (sdrfCharacteristic.getUnit() != null) {
            return new MeasurementCharacteristic(category, Float.valueOf(sdrfCharacteristic.getValue()),
                    getTerm(sdrfCharacteristic.getUnit()));
        } else {
            return new TermBasedCharacteristic(category, getTerm(sdrfCharacteristic.getTerm()));
        }
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
        ArrayDesign arrayDesign = null;
            arrayDesign = processArrayDesignRef(sdrfArrayDesign.getName());
        return arrayDesign;
    }

    // Process a reference to an array design in the caArray or in an external database.
    private ArrayDesign processArrayDesignRef(String arrayDesignName) {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setLsidForEntity(arrayDesignName);
        List<ArrayDesign> designs = getDaoFactory().getArrayDao().queryEntityAndAssociationsByExample(arrayDesign);
        if (designs.isEmpty()) {
            return null;
        } else {
            return designs.get(0);
        }
    }

    private void translateImages(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : document.getAllImages()) {
            Image image = new Image();
            String imageName = sdrfImage.getName();
            image.setName(imageName);
            CaArrayFile imageFile = getFile(imageName);
            image.setImageFile(imageFile);
            this.nodeTranslations.put(sdrfImage, image);
        }
    }

    private void translateRawArrayData(SdrfDocument document) {
        // Translate native raw data files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfData : document.getAllArrayDataFiles()) {
            RawArrayData caArrayData = new RawArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            dataFile.setFileType(dataFile.getFileType().getRawType());
            caArrayData.setDataFile(dataFile);
            // Associate Scan with the raw data.
            for (Scan scan : sdrfData.getPredecessorScans()) {
                associateScanWithData(caArrayData, scan);
            }
            this.nodeTranslations.put(sdrfData, caArrayData);
        }
        // Translate MAGE-TAB raw data matrix files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfData : document.getAllArrayDataMatrixFiles()) {
            RawArrayData caArrayData = new RawArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            caArrayData.setDataFile(dataFile);
            // Associate Scan with the raw data.
            for (Scan scan : sdrfData.getPredecessorScans()) {
                associateScanWithData(caArrayData, scan);
            }
            this.nodeTranslations.put(sdrfData, caArrayData);
        }
    }

    private void associateScanWithData(RawArrayData caArrayData, Scan scan) {
        for (gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication
                : scan.getProtocolApplications()) {
            ProtocolApplication protocolApplication =
                getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
            caArrayData.getProtocolApplications().add(protocolApplication);
        }
    }

    private void translateDerivedArrayData(SdrfDocument document) {
        // Translate native derived data files.
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfData : document.getAllDerivedArrayDataFiles()) {
            DerivedArrayData caArrayData = new DerivedArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            dataFile.setFileType(dataFile.getFileType().getDerivedType());
            caArrayData.setDataFile(dataFile);
            // Associate Normalization with the derived data.
            for (Normalization normalization : sdrfData.getPredecessorNormalizations()) {
                associateNormalizationWithData(caArrayData, normalization);
            }

            // Associate array data from which this data was derived
            setDerivedFromData(sdrfData, caArrayData);

            this.nodeTranslations.put(sdrfData, caArrayData);
        }
        // Translate MAGE-TAB derived data matrix files.
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfData
                : document.getAllDerivedArrayDataMatrixFiles()) {
            DerivedArrayData caArrayData = new DerivedArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            caArrayData.setDataFile(dataFile);
            // Associate Normalization with the derived data.
            for (Normalization normalization : sdrfData.getPredecessorNormalizations()) {
                associateNormalizationWithData(caArrayData, normalization);
            }

            // Associate array data from which this data was derived
            setDerivedFromData(sdrfData, caArrayData);
            this.nodeTranslations.put(sdrfData, caArrayData);
        }
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

    private void associateNormalizationWithData(DerivedArrayData caArrayData, Normalization normalization) {
        for (gov.nih.nci.caarray.magetab.ProtocolApplication mageTabProtocolApplication
                : normalization.getProtocolApplications()) {
            ProtocolApplication protocolApplication =
                getProtocolApplicationFromMageTabProtocolApplication(mageTabProtocolApplication);
            caArrayData.getProtocolApplications().add(protocolApplication);
        }
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
        if (isBioMaterial(leftNodeType)) {
            // Use the left node's name as part of any generated biomaterial names.
            String baseGeneratedNodeName = ((AbstractBioMaterial) leftCaArrayNode).getName();
            List<ProtocolApplication> pas = ((AbstractBioMaterial) leftCaArrayNode).getProtocolApplications();
            linkBioMaterial(leftCaArrayNode, rightCaArrayNode, leftNodeType, rightNodeType, baseGeneratedNodeName, pas);
        } else if (SdrfNodeType.HYBRIDIZATION.equals(leftNodeType)) {
            linkHybridizationToArrays((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode,
                    (Hybridization) leftCaArrayNode);
            linkHybridizationToImages((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode,
                    (Hybridization) leftCaArrayNode);
            linkHybridizationToArrayData((gov.nih.nci.caarray.magetab.sdrf.Hybridization) leftNode,
                    (Hybridization) leftCaArrayNode);
        } else {
            // Ignore other nodes - Image, Scan, Raw/Derived Data, Normalization; they've already been linked.
            return;
        }
    }

    private void linkHybridizationToArrays(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        Array array = new Array();
        if (sdrfHybridization.getArrayDesign() != null) {
            array.setDesign(getArrayDesign(sdrfHybridization.getArrayDesign()));
        }
        hybridization.setArray(array);
    }

    private void linkHybridizationToImages(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : sdrfHybridization.getSuccessorImages()) {
            Image image = (Image) this.nodeTranslations.get(sdrfImage);
            hybridization.getImages().add(image);
        }
    }

    private void linkHybridizationToArrayData(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        // Link raw array data
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataFiles()) {
            RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            arrayData.addHybridization(hybridization);
            hybridization.addRawArrayData(arrayData);
        }
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataMatrixFiles()) {
            RawArrayData arrayData = (RawArrayData) this.nodeTranslations.get(sdrfArrayData);
            arrayData.addHybridization(hybridization);
            hybridization.addRawArrayData(arrayData);
        }
        // Link derived array data
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            arrayData.getHybridizations().add(hybridization);
            hybridization.getDerivedDataCollection().add(arrayData);
        }
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataMatrixFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) this.nodeTranslations.get(sdrfArrayData);
            arrayData.getHybridizations().add(hybridization);
            hybridization.getDerivedDataCollection().add(arrayData);
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
        // TODO Handle case where Extract goes to Extract, as shown in ChIP-chip example in MAGE-TAB spec.
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
        } else if ((leftNodeType.equals(SdrfNodeType.LABELED_EXTRACT))
                && (rightNodeType.equals(SdrfNodeType.HYBRIDIZATION))) {
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
            generatedSample = new Sample();
            generatedSample.setName(sampleName);
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
            generatedExtract = new Extract();
            generatedExtract.setName(extractName);
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
            generatedLabeledExtract = new LabeledExtract();
            generatedLabeledExtract.setName(labeledExtractName);
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

    @Override
    Logger getLog() {
        return LOG;
    }
}
