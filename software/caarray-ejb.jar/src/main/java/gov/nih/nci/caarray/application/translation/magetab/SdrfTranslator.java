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

import gov.nih.nci.caarray.application.arraydata.affymetrix.AffymetrixArrayDataTypes;
import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.AbstractCaArrayObject;
import gov.nih.nci.caarray.domain.array.Array;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.DerivedArrayData;
import gov.nih.nci.caarray.domain.data.Image;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.CaArrayFileSet;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Factor;
import gov.nih.nci.caarray.domain.project.FactorValue;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.protocol.Protocol;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab.idf.ExperimentalFactor;
import gov.nih.nci.caarray.magetab.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab.sdrf.Characteristic;
import gov.nih.nci.caarray.magetab.sdrf.Normalization;
import gov.nih.nci.caarray.magetab.sdrf.Provider;
import gov.nih.nci.caarray.magetab.sdrf.Scan;
import gov.nih.nci.caarray.magetab.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab.sdrf.SdrfNodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Translates entities in SDRF documents into caArray entities.
 */
@SuppressWarnings("PMD")
final class SdrfTranslator extends AbstractTranslator {

    private static final Log LOG = LogFactory.getLog(SdrfTranslator.class);

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
    private final Map<String, AbstractCaArrayEntity> generatedNodes =
        new HashMap<String, AbstractCaArrayEntity>();

    SdrfTranslator(MageTabDocumentSet documentSet, CaArrayFileSet fileSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory) {
        super(documentSet, fileSet, translationResult, daoFactory);
    }

    @Override
    void translate() {
        for (SdrfDocument document : getDocumentSet().getSdrfDocuments()) {
            translateSdrf(document);
        }
    }

    private void translateSdrf(SdrfDocument document) {
        translateNodesToEntities(document);
        linkNodes(document);
        /**
         * added following if statement b/c sdrf doesnt have idf document.  i could be wrong.
         * this was causing error on imports of sdrf.  JH 10/4/07
         *
         */
        if (document.getIdfDocument() != null) {
            String investigationTitle = document.getIdfDocument().getInvestigation().getTitle();
            for (Experiment investigation : getTranslationResult().getInvestigations()) {
                if (investigationTitle.equals(investigation.getTitle())) {
                    investigation.getSources().addAll(allSources);
                    investigation.getSamples().addAll(allSamples);
                    investigation.getExtracts().addAll(allExtracts);
                    investigation.getLabeledExtracts().addAll(allLabeledExtracts);
                }
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
                Organization organization = new Organization();
                organization.setName(sdrfProvider.getName());
                organization = replaceIfExists(organization);
                source.getProviders().add(organization);
            }
            nodeTranslations.put(sdrfSource, source);
            allSources.add(source);
        }
    }

    private void translateSamples(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Sample sdrfSample : document.getAllSamples()) {
            Sample sample = new Sample();
            translateBioMaterial(sample, sdrfSample);
            nodeTranslations.put(sdrfSample, sample);
            allSamples.add(sample);
        }
    }

    private void translateExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Extract sdrfExtract : document.getAllExtracts()) {
            Extract extract = new Extract();
            translateBioMaterial(extract, sdrfExtract);
            nodeTranslations.put(sdrfExtract, extract);
            allExtracts.add(extract);
        }
    }

    private void translateLabeledExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.LabeledExtract sdrfLabeledExtract : document.getAllLabeledExtracts()) {
            LabeledExtract labeledExtract = new LabeledExtract();
            translateBioMaterial(labeledExtract, sdrfLabeledExtract);
            labeledExtract.setLabel(getTerm(sdrfLabeledExtract.getLabel()));
            nodeTranslations.put(sdrfLabeledExtract, labeledExtract);
            allLabeledExtracts.add(labeledExtract);
        }
    }

    private void translateHybridizations(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization : document.getAllHybridizations()) {
            Hybridization hybridization = new Hybridization();
            hybridization.setName(sdrfHybridization.getName());
            for (gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal : sdrfHybridization.getFactorValues()) {
                FactorValue factorValue = translateFactor(sdrfFactorVal);
                hybridization.getFactorValues().add(factorValue);
            }
            nodeTranslations.put(sdrfHybridization, hybridization);
        }
    }

    private FactorValue translateFactor(gov.nih.nci.caarray.magetab.sdrf.FactorValue sdrfFactorVal) {
        FactorValue factorValue = new FactorValue();
        factorValue.setValue(sdrfFactorVal.getValue());
        factorValue.setUnit(getTerm(sdrfFactorVal.getUnit()));
        Factor factor = new Factor();
        ExperimentalFactor sdrfFactor = sdrfFactorVal.getFactor();
        if (sdrfFactor != null) {
            factor.setName(sdrfFactor.getName());
            factor.setType(getTerm(sdrfFactor.getType()));
        }
        factorValue.setFactor(factor);
        return factorValue;
    }

    private void translateBioMaterial(AbstractBioMaterial bioMaterial,
            gov.nih.nci.caarray.magetab.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        bioMaterial.setName(sdrfBiomaterial.getName());
        bioMaterial.setDescription(sdrfBiomaterial.getDescription());
        bioMaterial.setMaterialType(getTerm(sdrfBiomaterial.getMaterialType()));
        for (Characteristic sdrfCharacteristic : sdrfBiomaterial.getCharacteristics()) {
            gov.nih.nci.caarray.domain.sample.Characteristic characteristic =
                translateCharacteristic(sdrfCharacteristic);
            bioMaterial.getCharacteristics().add(characteristic);
        }
    }

    private gov.nih.nci.caarray.domain.sample.Characteristic translateCharacteristic(
            Characteristic sdrfCharacteristic) {
        gov.nih.nci.caarray.domain.sample.Characteristic characteristic =
            new gov.nih.nci.caarray.domain.sample.Characteristic();
        characteristic.setValue(sdrfCharacteristic.getValue());
        characteristic.setTerm(getTerm(sdrfCharacteristic.getTerm()));
        characteristic.setUnit(getTerm(sdrfCharacteristic.getUnit()));
        return null;
    }

    // Translates arraydesigns to a linked array-arraydesign pair in the caArray domain.
    private void translateArrayDesigns(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDesign sdrfArrayDesign : document.getAllArrayDesigns()) {
            ArrayDesign arrayDesign = null;
            if (sdrfArrayDesign.isArrayDesignRef()) {
                arrayDesign = processArrayDesignRef(sdrfArrayDesign.getName());
            } else {
                arrayDesign = processArrayDesignFile(sdrfArrayDesign.getName());
            }
            // Generate an array to link the hybridization to the arraydesign.
            Array array = new Array();
            array.setDesign(arrayDesign);
            nodeTranslations.put(sdrfArrayDesign, array);
            getTranslationResult().getArrayDesigns().add(arrayDesign);
            if (getTranslationResult().getInvestigations().size() > 0) {
                getTranslationResult().getInvestigations().iterator().next().getArrayDesigns().add(arrayDesign);
            }
        }
    }

    // Process a reference to an array design in the caArray or in an external database.
    private ArrayDesign processArrayDesignRef(String arrayDesignName) {
        ArrayDesign arrayDesign = new ArrayDesign();
        arrayDesign.setLsidForEntity(arrayDesignName);
        arrayDesign.setName(arrayDesign.getLsidObjectId());
        // Look up database for ArrayDesign with this lsid. If doesn't exist, create.
        arrayDesign = replaceIfExists(arrayDesign);
        return arrayDesign;
    }

    // Processes an array design for which a file is included in the MAGE-TAB document set.
    private ArrayDesign processArrayDesignFile(String arrayDesignName) {
        ArrayDesign arrayDesign = new ArrayDesign();
        CaArrayFile designFile = getFile(arrayDesignName);
        arrayDesign.setDesignFile(designFile);
        return arrayDesign;
    }

    private void translateImages(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : document.getAllImages()) {
            Image image = new Image();
            String imageName = sdrfImage.getName();
            image.setName(imageName);
            CaArrayFile imageFile = getFile(imageName);
            image.setImageFile(imageFile);
            nodeTranslations.put(sdrfImage, image);
        }
    }

    private void translateRawArrayData(SdrfDocument document) {
        // Translate native raw data files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfData : document.getAllArrayDataFiles()) {
            RawArrayData caArrayData = new RawArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            caArrayData.setDataFile(dataFile);
            caArrayData.setType(getArrayDataType(dataFile));
            // Associate Scan with the raw data.
            for (Scan scan : sdrfData.getPredecessorScans()) {
                associateScanWithData(caArrayData, scan);
            }
            nodeTranslations.put(sdrfData, caArrayData);
        }
        // Translate MAGE-TAB raw data matrix files.
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfData : document.getAllArrayDataMatrixFiles()) {
            RawArrayData caArrayData = new RawArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            caArrayData.setDataFile(dataFile);
            caArrayData.setType(getArrayDataType(dataFile));
            // Associate Scan with the raw data.
            for (Scan scan : sdrfData.getPredecessorScans()) {
                associateScanWithData(caArrayData, scan);
            }
            nodeTranslations.put(sdrfData, caArrayData);
        }
    }

    /**
     * @param dataFile
     * @return
     */
    private ArrayDataType getArrayDataType(CaArrayFile dataFile) {
        if (FileType.AFFYMETRIX_CEL.equals(dataFile.getType())) {
            return getDaoFactory().getArrayDao().getArrayDataType(AffymetrixArrayDataTypes.AFFYMETRIX_EXPRESSION_CEL);
        } else {
            throw new IllegalArgumentException("Unsupported data file type: " + dataFile.getType());
        }
    }

    private void associateScanWithData(RawArrayData caArrayData, Scan scan) {
        ProtocolApplication scanProtocolApp = new ProtocolApplication();
        Protocol scanProtocol = new Protocol();
        scanProtocol.setName(scan.getName());
        scanProtocol = replaceIfExists(scanProtocol);
        scanProtocolApp.setProtocol(scanProtocol);
        caArrayData.getProtocolApplications().add(scanProtocolApp);
    }

    private void translateDerivedArrayData(SdrfDocument document) {
        // Translate native derived data files.
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfData : document.getAllDerivedArrayDataFiles()) {
            DerivedArrayData caArrayData = new DerivedArrayData();
            String fileName = sdrfData.getName();
            caArrayData.setName(fileName);
            CaArrayFile dataFile = getFile(fileName);
            caArrayData.setDataFile(dataFile);
            // Associate Normalization with the derived data.
            for (Normalization normalization : sdrfData.getPredecessorNormalizations()) {
                associateNormalizationWithData(caArrayData, normalization);
            }
            nodeTranslations.put(sdrfData, caArrayData);
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
            nodeTranslations.put(sdrfData, caArrayData);
        }
    }

    private void associateNormalizationWithData(DerivedArrayData caArrayData, Normalization normalization) {
        ProtocolApplication normalizationProtocolApp = new ProtocolApplication();
        Protocol normalizationProtocol = new Protocol();
        normalizationProtocol.setName(normalization.getName());
        normalizationProtocol = replaceIfExists(normalizationProtocol);
        normalizationProtocolApp.setProtocol(normalizationProtocol);
        caArrayData.getProtocolApplications().add(normalizationProtocolApp);
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
        Boolean isLinked = isNodeLinked.get(node);
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
        isNodeLinked.put(node, Boolean.TRUE);
    }

    // Link a node with one successor.
    private void linkTwoNodes(AbstractSampleDataRelationshipNode leftNode,
        AbstractSampleDataRelationshipNode rightNode) {
        AbstractCaArrayObject leftCaArrayNode = nodeTranslations.get(leftNode);
        AbstractCaArrayObject rightCaArrayNode = nodeTranslations.get(rightNode);
        SdrfNodeType leftNodeType = leftNode.getNodeType();
        SdrfNodeType rightNodeType = rightNode.getNodeType();
        if (isBioMaterial(leftNodeType)) {
            // Use the left node's name as part of any generated biomaterial names.
            String baseGeneratedNodeName = ((AbstractBioMaterial) leftCaArrayNode).getName();
            linkBioMaterial(leftCaArrayNode, rightCaArrayNode, leftNodeType, rightNodeType, baseGeneratedNodeName);
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

            Array array = (Array) nodeTranslations.get(sdrfHybridization.getArrayDesign());
            hybridization.setArray(array);
    }

    private void linkHybridizationToImages(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        for (gov.nih.nci.caarray.magetab.sdrf.Image sdrfImage : sdrfHybridization.getSuccessorImages()) {
            Image image = (Image) nodeTranslations.get(sdrfImage);
            hybridization.getImages().add(image);
        }
    }

    private void linkHybridizationToArrayData(gov.nih.nci.caarray.magetab.sdrf.Hybridization sdrfHybridization,
            Hybridization hybridization) {
        // Link raw array data
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataFiles()) {
            RawArrayData arrayData = (RawArrayData) nodeTranslations.get(sdrfArrayData);
            arrayData.setHybridization(hybridization);
            hybridization.setArrayData(arrayData);
        }
        for (gov.nih.nci.caarray.magetab.sdrf.ArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorArrayDataMatrixFiles()) {
            RawArrayData arrayData = (RawArrayData) nodeTranslations.get(sdrfArrayData);
            arrayData.setHybridization(hybridization);
            hybridization.setArrayData(arrayData);
        }
        // Link derived array data
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) nodeTranslations.get(sdrfArrayData);
            arrayData.getHybridizations().add(hybridization);
            hybridization.getDerivedDatas().add(arrayData);
        }
        for (gov.nih.nci.caarray.magetab.sdrf.DerivedArrayDataMatrixFile sdrfArrayData
                : sdrfHybridization.getSuccessorDerivedArrayDataMatrixFiles()) {
            DerivedArrayData arrayData = (DerivedArrayData) nodeTranslations.get(sdrfArrayData);
            arrayData.getHybridizations().add(hybridization);
            hybridization.getDerivedDatas().add(arrayData);
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
            SdrfNodeType leftNodeType, SdrfNodeType rightNodeType, String baseGeneratedNodeName) {
        // TODO Handle case where Extract goes to Extract, as shown in ChIP-chip example in MAGE-TAB spec.
        if (leftNodeType.equals(SdrfNodeType.SOURCE)) {
            if (rightNodeType.equals(SdrfNodeType.SAMPLE)) {
                linkSourceAndSample((Source) leftCaArrayNode, (Sample) rightCaArrayNode);
            } else {
                Sample generatedSample = generateSampleAndLink(baseGeneratedNodeName, (Source) leftCaArrayNode);
                linkBioMaterial(generatedSample, rightCaArrayNode, SdrfNodeType.SAMPLE, rightNodeType,
                        baseGeneratedNodeName);
            }
        } else if (leftNodeType.equals(SdrfNodeType.SAMPLE)) {
            if (rightNodeType.equals(SdrfNodeType.EXTRACT)) {
                linkSampleAndExtract((Sample) leftCaArrayNode, (Extract) rightCaArrayNode);
            } else {
                Extract generatedExtract = generateExtractAndLink(baseGeneratedNodeName, (Sample) leftCaArrayNode);
                linkBioMaterial(generatedExtract, rightCaArrayNode, SdrfNodeType.EXTRACT, rightNodeType,
                        baseGeneratedNodeName);
            }
        } else if (leftNodeType.equals(SdrfNodeType.EXTRACT)) {
            if (rightNodeType.equals(SdrfNodeType.LABELED_EXTRACT)) {
                linkExtractAndLabeledExtract((Extract) leftCaArrayNode, (LabeledExtract) rightCaArrayNode);
            } else {
                LabeledExtract generatedLabeledExtract = generateLabeledExtractAndLink(baseGeneratedNodeName,
                        (Extract) leftCaArrayNode);
                linkBioMaterial(generatedLabeledExtract, rightCaArrayNode, SdrfNodeType.LABELED_EXTRACT, rightNodeType,
                        baseGeneratedNodeName);
            }
        } else if ((leftNodeType.equals(SdrfNodeType.LABELED_EXTRACT))
                && (rightNodeType.equals(SdrfNodeType.HYBRIDIZATION))) {
            linkLabeledExtractAndHybridization((LabeledExtract) leftCaArrayNode, (Hybridization) rightCaArrayNode);
        }
    }

    private Sample generateSampleAndLink(String baseGeneratedNodeName, Source source) {
        // Generate sample if not already generated.
        String sampleName = GENERATED_SAMPLE_PREFIX + baseGeneratedNodeName;
        Sample generatedSample = (Sample) generatedNodes.get(sampleName);
        if (generatedSample == null) {
            generatedSample = new Sample();
            generatedSample.setName(sampleName);
            generatedNodes.put(sampleName, generatedSample);
            allSamples.add(generatedSample);
        }
        linkSourceAndSample(source, generatedSample);
        return generatedSample;
    }

    private Extract generateExtractAndLink(String baseGeneratedNodeName, Sample generatedSample) {
        // Generate extract if not already generated.
        String extractName = GENERATED_EXTRACT_PREFIX + baseGeneratedNodeName;
        Extract generatedExtract = (Extract) generatedNodes.get(extractName);
        if (generatedExtract == null) {
            generatedExtract = new Extract();
            generatedExtract.setName(extractName);
            generatedNodes.put(extractName, generatedExtract);
            allExtracts.add(generatedExtract);
        }
        linkSampleAndExtract(generatedSample, generatedExtract);
        return generatedExtract;
    }

    private LabeledExtract generateLabeledExtractAndLink(String baseGeneratedNodeName, Extract generatedExtract) {
        // Generate labeled extract if not already generated.
        String labeledExtractName = GENERATED_LABELED_EXTRACT_PREFIX + baseGeneratedNodeName;
        LabeledExtract generatedLabeledExtract = (LabeledExtract) generatedNodes.get(labeledExtractName);
        if (generatedLabeledExtract == null) {
            generatedLabeledExtract = new LabeledExtract();
            generatedLabeledExtract.setName(labeledExtractName);
            generatedNodes.put(labeledExtractName, generatedLabeledExtract);
            allLabeledExtracts.add(generatedLabeledExtract);
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
    Log getLog() {
        return LOG;
    }
}
