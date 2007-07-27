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

import gov.nih.nci.caarray.dao.CaArrayDaoFactory;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.hybridization.Hybridization;
import gov.nih.nci.caarray.domain.project.Investigation;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;
import gov.nih.nci.caarray.domain.sample.Extract;
import gov.nih.nci.caarray.domain.sample.LabeledExtract;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.sample.Source;
import gov.nih.nci.caarray.magetab2.MageTabDocumentSet;
import gov.nih.nci.caarray.magetab2.sdrf.AbstractSampleDataRelationshipNode;
import gov.nih.nci.caarray.magetab2.sdrf.SdrfDocument;
import gov.nih.nci.caarray.magetab2.sdrf.SdrfNodeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Translates entities in SDRF documents.
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

    SdrfTranslator(MageTabDocumentSet documentSet, MageTabTranslationResult translationResult,
            CaArrayDaoFactory daoFactory) {
        super(documentSet, translationResult, daoFactory);
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
        String investigationTitle = document.getIdfDocument().getInvestigation().getTitle();
        for (Investigation investigation : getTranslationResult().getInvestigations()) {
            if (investigationTitle.equals(investigation.getTitle())) {
                investigation.getSources().addAll(allSources);
                investigation.getSamples().addAll(allSamples);
                // TODO Add all extracts and labeled extracts. Not in Investigation POJO or DDL yet.
            }
        }
    }

    private void translateNodesToEntities(SdrfDocument document) {
        translateSources(document);
        translateSamples(document);
        translateExtracts(document);
        translateLabeledExtracts(document);
        translateHybridizations(document);
        // TODO etc...
    }

    private void translateSources(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab2.sdrf.Source sdrfSource : document.getAllSources()) {
            Source source = new Source();
            translateBioMaterial(source, sdrfSource);
            // TODO Translate the providers of the source.
            nodeTranslations.put(sdrfSource, source);
            allSources.add(source);
        }
    }

    private void translateSamples(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab2.sdrf.Sample sdrfSample : document.getAllSamples()) {
            Sample sample = new Sample();
            translateBioMaterial(sample, sdrfSample);
            nodeTranslations.put(sdrfSample, sample);
            allSamples.add(sample);
        }
    }

    private void translateExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab2.sdrf.Extract sdrfExtract : document.getAllExtracts()) {
            Extract extract = new Extract();
            translateBioMaterial(extract, sdrfExtract);
            nodeTranslations.put(sdrfExtract, extract);
            allExtracts.add(extract);
        }
    }

    private void translateLabeledExtracts(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab2.sdrf.LabeledExtract sdrfLabeledExtract : document.getAllLabeledExtracts()) {
            LabeledExtract labeledExtract = new LabeledExtract();
            translateBioMaterial(labeledExtract, sdrfLabeledExtract);
            // TODO Translate the label into a caArray Compound. Compound is empty right now.
            nodeTranslations.put(sdrfLabeledExtract, labeledExtract);
            allLabeledExtracts.add(labeledExtract);
        }
    }

    private void translateHybridizations(SdrfDocument document) {
        for (gov.nih.nci.caarray.magetab2.sdrf.Hybridization sdrfHybridization : document.getAllHybridizations()) {
            Hybridization hybridization = new Hybridization();
            hybridization.setName(sdrfHybridization.getName());
            nodeTranslations.put(sdrfHybridization, hybridization);
        }
    }

    private void translateBioMaterial(AbstractBioMaterial bioMaterial,
            gov.nih.nci.caarray.magetab2.sdrf.AbstractBioMaterial sdrfBiomaterial) {
        bioMaterial.setName(sdrfBiomaterial.getName());
        bioMaterial.setDescription(sdrfBiomaterial.getDescription());
        bioMaterial.setMaterialType(getTerm(sdrfBiomaterial.getMaterialType()));
        // TODO Translate characteristics
    }

    private void linkNodes(SdrfDocument document) {
        for (AbstractSampleDataRelationshipNode currNode : document.getLeftmostNodes()) {
            linkNode(currNode);
        }

    }

    // Recursively link this node to its successors.
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
        AbstractCaArrayEntity leftCaArrayNode = nodeTranslations.get(leftNode);
        AbstractCaArrayEntity rightCaArrayNode = nodeTranslations.get(rightNode);
        SdrfNodeType leftNodeType = leftNode.getNodeType();
        SdrfNodeType rightNodeType = rightNode.getNodeType();
        if (isBioMaterial(leftNodeType)) {
            // Use the left node's name as part of any generated biomaterial names.
            String baseGeneratedNodeName = ((AbstractBioMaterial) leftCaArrayNode).getName();
            linkBioMaterial(leftCaArrayNode, rightCaArrayNode, leftNodeType, rightNodeType, baseGeneratedNodeName);
        }
        // TODO Linkage of nodes where left node is not a BioMaterial is not yet implemented.
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
    private void linkBioMaterial(AbstractCaArrayEntity leftCaArrayNode, AbstractCaArrayEntity rightCaArrayNode,
            SdrfNodeType leftNodeType, SdrfNodeType rightNodeType, String baseGeneratedNodeName) {
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
        Sample generatedSample = new Sample();
        generatedSample.setName(GENERATED_SAMPLE_PREFIX + baseGeneratedNodeName);
        linkSourceAndSample(source, generatedSample);
        allSamples.add(generatedSample);
        return generatedSample;
    }

    private Extract generateExtractAndLink(String baseGeneratedNodeName, Sample generatedSample) {
        Extract generatedExtract = new Extract();
        generatedExtract.setName(GENERATED_EXTRACT_PREFIX + baseGeneratedNodeName);
        linkSampleAndExtract(generatedSample, generatedExtract);
        allExtracts.add(generatedExtract);
        return generatedExtract;
    }

    private LabeledExtract generateLabeledExtractAndLink(String baseGeneratedNodeName, Extract generatedExtract) {
        LabeledExtract generatedLabeledExtract = new LabeledExtract();
        generatedLabeledExtract.setName(GENERATED_LABELED_EXTRACT_PREFIX + baseGeneratedNodeName);
        linkExtractAndLabeledExtract(generatedExtract, generatedLabeledExtract);
        allLabeledExtracts.add(generatedLabeledExtract);
        return generatedLabeledExtract;
    }

    private void linkSourceAndSample(Source source, Sample sample) {
        source.getSamples().add((Sample) sample);
        sample.getSources().add((Source) source);
    }

    private void linkSampleAndExtract(Sample sample, Extract extract) {
        sample.getExtracts().add((Extract) extract);
        extract.getSamples().add((Sample) sample);
    }

    private void linkExtractAndLabeledExtract(Extract extract, LabeledExtract labeledExtract) {
        extract.getLabeledExtracts().add((LabeledExtract) labeledExtract);
        labeledExtract.getExtracts().add((Extract) extract);
    }

    private void linkLabeledExtractAndHybridization(LabeledExtract labeledExtract, Hybridization hybridization) {
        hybridization.getLabeledExtract().add((LabeledExtract) labeledExtract);
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
