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
package gov.nih.nci.caarray.magetab.sdrf;

import gov.nih.nci.caarray.magetab.ProtocolApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An entity within an SDRF document -- may be a bio material, hybridization, or
 * data object.
 */
public abstract class AbstractSampleDataRelationshipNode extends AbstractCommentable {
    private static final long serialVersionUID = -2710483246399354549L;

    private final List<ProtocolApplication> protocolApplications =
        new ArrayList<ProtocolApplication>();
    private final Set<AbstractSampleDataRelationshipNode> predecessors =
        new HashSet<AbstractSampleDataRelationshipNode>();
    private final Set<AbstractSampleDataRelationshipNode> successors =
        new HashSet<AbstractSampleDataRelationshipNode>();
    private String name;
    private boolean repeated;

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public final void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the protocolApplications
     */
    public List<ProtocolApplication> getProtocolApplications() {
        return protocolApplications;
    }

    /**
     * @return the successors
     */
    public Set<AbstractSampleDataRelationshipNode> getSuccessors() {
        return successors;
    }

    /**
     * @return the predecessors
     */
    public Set<AbstractSampleDataRelationshipNode> getPredecessors() {
        return predecessors;
    }

    /**
     * Link the given node as a predecessor of <code>this</code>.
     * @param predecessorNode node preceding <code>this</code>
     */
    void link(AbstractSampleDataRelationshipNode predecessorNode) {
        predecessors.add(predecessorNode);
        predecessorNode.getSuccessors().add(this);
    }

    /**
     * Returns the type of this node.
     *
     * @return the type.
     */
    public abstract SdrfNodeType getNodeType();

    private Set<AbstractSampleDataRelationshipNode> getSuccessorsOfType(SdrfNodeType type, boolean recursive) {
        HashSet<AbstractSampleDataRelationshipNode> result = new HashSet<AbstractSampleDataRelationshipNode>();
        for (AbstractSampleDataRelationshipNode node : getSuccessors()) {
            if (node.getNodeType().equals(type)) {
                result.add(node);
            }
            if (recursive) {
                result.addAll(node.getSuccessorsOfType(type, recursive));
            }
        }
        return result;
    }

    private Set<AbstractSampleDataRelationshipNode> getPredecessorsOfType(SdrfNodeType type, boolean recursive) {
        HashSet<AbstractSampleDataRelationshipNode> result = new HashSet<AbstractSampleDataRelationshipNode>();
        for (AbstractSampleDataRelationshipNode node : getPredecessors()) {
            if (node.getNodeType().equals(type)) {
                result.add(node);
            }
            if (recursive) {
                result.addAll(node.getPredecessorsOfType(type, recursive));
            }
        }
        return result;
    }

    /**
     * Returns all <code>Sources</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Sources</code>.
     */
    public Set<Source> getSuccessorSources() {
        Set<AbstractSampleDataRelationshipNode> sourceNodes = getSuccessorsOfType(SdrfNodeType.SOURCE, true);
        HashSet<Source> sources = new HashSet<Source>(sourceNodes.size());
        for (AbstractSampleDataRelationshipNode sourceNode : sourceNodes) {
            sources.add((Source) sourceNode);
        }
        return sources;
    }

    /**
     * Returns all <code>Samples</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Samples</code>.
     */
    public Set<Sample> getSuccessorSamples() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.SAMPLE, true);
        HashSet<Sample> result = new HashSet<Sample>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Sample) node);
        }
        return result;
    }

    /**
     * Returns all <code>Extracts</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Extracts</code>.
     */
    public Set<Extract> getSuccessorExtracts() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.EXTRACT, true);
        HashSet<Extract> result = new HashSet<Extract>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Extract) node);
        }
        return result;
    }

    /**
     * Returns all <code>LabeledExtracts</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>LabeledExtracts</code>.
     */
    public Set<LabeledExtract> getSuccessorLabeledExtracts() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.LABELED_EXTRACT, true);
        HashSet<LabeledExtract> result = new HashSet<LabeledExtract>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((LabeledExtract) node);
        }
        return result;
    }

    /**
     * Returns all <code>Hybridizations</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Hybridizations</code>.
     */
    public Set<Hybridization> getSuccessorHybridizations() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.HYBRIDIZATION, true);
        HashSet<Hybridization> result = new HashSet<Hybridization>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Hybridization) node);
        }
        return result;
    }

    /**
     * Returns all <code>Scans</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Scans</code>.
     */
    public Set<Scan> getSuccessorScans() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.SCAN, true);
        HashSet<Scan> result = new HashSet<Scan>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Scan) node);
        }
        return result;
    }

    /**
     * Returns all <code>Scans</code> that this node originated from (searched recursively).
     *
     * @return the originating <code>Scans</code>.
     */
    public Set<Scan> getPredecessorScans() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.SCAN, true);
        HashSet<Scan> result = new HashSet<Scan>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Scan) node);
        }
        return result;
    }

    /**
     * Returns all <code>Normalizations</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Normalizations</code>.
     */
    public Set<Normalization> getSuccessorNormalizations() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.NORMALIZATION, true);
        HashSet<Normalization> result = new HashSet<Normalization>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Normalization) node);
        }
        return result;
    }

    /**
     * Returns all <code>Normalizations</code> that this node originated from (searched recursively).
     *
     * @return the originating <code>Normalizations</code>.
     */
    public Set<Normalization> getPredecessorNormalizations() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.NORMALIZATION, true);
        HashSet<Normalization> result = new HashSet<Normalization>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Normalization) node);
        }
        return result;
    }

    /**
     * Returns all <code>ArrayDataFiles</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>ArrayDataFiles</code>.
     */
    public Set<ArrayDataFile> getSuccessorArrayDataFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.ARRAY_DATA_FILE, true);
        HashSet<ArrayDataFile> result = new HashSet<ArrayDataFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((ArrayDataFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>ArrayDataFiles</code> that this node originated from (only immediate predecessors,
     * not searched recursively).
     *
     * @return the originating <code>ArrayDataFiles</code>.
     */
    public Set<ArrayDataFile> getPredecessorArrayDataFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.ARRAY_DATA_FILE, false);
        HashSet<ArrayDataFile> result = new HashSet<ArrayDataFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((ArrayDataFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>DerivedArrayDataFiles</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>DerivedArrayDataFiles</code>.
     */
    public Set<DerivedArrayDataFile> getSuccessorDerivedArrayDataFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.DERIVED_ARRAY_DATA_FILE, true);
        HashSet<DerivedArrayDataFile> result = new HashSet<DerivedArrayDataFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((DerivedArrayDataFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>DerivedArrayDataFiles</code> that this node originated from (only immediate predecessors,
     * not searched recursively).
     *
     * @return the originating <code>DerivedArrayDataFiles</code>.
     */
    public Set<DerivedArrayDataFile> getPredecessorDerivedArrayDataFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.DERIVED_ARRAY_DATA_FILE,
                false);
        HashSet<DerivedArrayDataFile> result = new HashSet<DerivedArrayDataFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((DerivedArrayDataFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>DerivedArrayDataMatrixFiles</code> that this node originated from (only immediate predecessors,
     * not searched recursively).
     *
     * @return the originating <code>DerivedArrayDataMatrixFiles</code>.
     */
    public Set<DerivedArrayDataMatrixFile> getPredecessorDerivedArrayDataMatrixFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.DERIVED_ARRAY_DATA_MATRIX,
                false);
        HashSet<DerivedArrayDataMatrixFile> result = new HashSet<DerivedArrayDataMatrixFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((DerivedArrayDataMatrixFile) node);
        }
        return result;
    }
    
    /**
     * Returns all <code>DerivedArrayDataMatrixFiles</code> that this node originated from (only immediate predecessors,
     * not searched recursively).
     *
     * @return the originating <code>DerivedArrayDataMatrixFiles</code>.
     */
    public Set<ArrayDataMatrixFile> getPredecessorArrayDataMatrixFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getPredecessorsOfType(SdrfNodeType.ARRAY_DATA_MATRIX,
                false);
        HashSet<ArrayDataMatrixFile> result = new HashSet<ArrayDataMatrixFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((ArrayDataMatrixFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>ArrayDataMatrixFiles</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>ArrayDataMatrixFiles</code>.
     */
    public Set<ArrayDataMatrixFile> getSuccessorArrayDataMatrixFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.ARRAY_DATA_MATRIX, true);
        HashSet<ArrayDataMatrixFile> result = new HashSet<ArrayDataMatrixFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((ArrayDataMatrixFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>DerivedArrayDataMatrixFiles</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>DerivedArrayDataMatrixFiles</code>.
     */
    public Set<DerivedArrayDataMatrixFile> getSuccessorDerivedArrayDataMatrixFiles() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.DERIVED_ARRAY_DATA_MATRIX,
                true);
        HashSet<DerivedArrayDataMatrixFile> result = new HashSet<DerivedArrayDataMatrixFile>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((DerivedArrayDataMatrixFile) node);
        }
        return result;
    }

    /**
     * Returns all <code>Images</code> that originate from this node (searched recursively).
     *
     * @return the originating <code>Images</code>.
     */
    public Set<Image> getSuccessorImages() {
        Set<AbstractSampleDataRelationshipNode> nodes = getSuccessorsOfType(SdrfNodeType.IMAGE, true);
        HashSet<Image> result = new HashSet<Image>(nodes.size());
        for (AbstractSampleDataRelationshipNode node : nodes) {
            result.add((Image) node);
        }
        return result;
    }

    abstract void addToSdrfList(SdrfDocument document);

    /**
     * @return whether this node is repeated (due to branches / merges within the graph. This property
     * should be false the first time this node is encountered, and set to true on subsequent times.
     */
    public boolean isRepeated() {
        return repeated;
    }

    /**
     * @param repeated the repeated to set
     */
    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

}
