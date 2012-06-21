/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
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
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
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
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.protocol.AbstractParameterValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplicable;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;
import gov.nih.nci.caarray.domain.sample.AbstractCharacteristic;
import gov.nih.nci.caarray.domain.vocabulary.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Common interface for the nodes in the experiment design graph. The nodes
 * include biomaterials, hybridizations.
 * @author dkokotov
 */
@MappedSuperclass
@SuppressWarnings("PMD.CyclomaticComplexity")
public abstract class AbstractExperimentDesignNode extends AbstractCaArrayEntity implements ProtocolApplicable {
    private static final long serialVersionUID = 1L;

    /**
     * @return the name of this node
     */
    @Transient
    public abstract String getName();

    /**
     * Merges another AbstractExperimentDesignNode into this one.  For simple properties, the other node's value will
     * only be used if the corresponding value in this is null.  For unordered Collections, the collections will be
     * merged.  For ordered Lists, the specific behavior will be determined by the subclass.  The behavior for other
     * types of properties is dependent on subclasses but should be consistent with the behavior of simple properties.
     * <p>
     * Most of the information will actually be <strong>moved</strong> from <code>node</code> to this in preparation
     * for deleting <code>node</code>.
     * @param node node to merge into this
     */
    public void merge(AbstractExperimentDesignNode node) {
        mergeProtocolApplications(node);

        for (AbstractExperimentDesignNode successor : node.getDirectSuccessors()) {
            this.addDirectSuccessor(successor);
        }

        for (AbstractExperimentDesignNode predecessor : node.getDirectPredecessors()) {
            predecessor.getDirectSuccessors().remove(node);
            predecessor.addDirectSuccessor(this);
        }
    }

    /**
     * Get the experiment to which this node belongs.
     * @return the experiment to which this node belongs
     */
    @Transient
    public abstract Experiment getExperiment();

    /**
     * @return the type of this node
     */
    @Transient
    public abstract ExperimentDesignNodeType getNodeType();

    /**
     * @return the set of all nodes that are immediately to the right of this node in the experment design
     * or hybridization channel
     */
    @Transient
    public abstract Set<? extends AbstractExperimentDesignNode> getDirectSuccessors();

    /**
     * @return the set of all nodes that are immediately to the left of this node in the experment design
     * or hybridization channel
     */
    @Transient
    public abstract Set<? extends AbstractExperimentDesignNode> getDirectPredecessors();

    /**
     * @param type type of nodes to return
     * @return the set of all nodes that are to the right of this node in the experment design
     * or hybridization channel and are of the given type. This is taking the transitive
     * closure of getDirectSuccessors() and filtering out all nodes not of the given type.
     */
    public Set<? extends AbstractExperimentDesignNode> getSuccessorsOfType(ExperimentDesignNodeType type) {
        if (!type.isSuccessorOf(getNodeType())) {
            return Collections.emptySet();
        }
        Set<? extends AbstractExperimentDesignNode> directSuccs = getDirectSuccessors();
        if (type.isDirectSuccessorOf(getNodeType())) {
            return directSuccs;
        }
        Set<AbstractExperimentDesignNode> succs = new HashSet<AbstractExperimentDesignNode>();
        for (AbstractExperimentDesignNode node : directSuccs) {
            succs.addAll(node.getSuccessorsOfType(type));
        }
        return succs;
    }

    /**
     * @param type type of nodes to return
     * @return the set of all nodes that are to the left of this node in the experment design
     * or hybridization channel and are of the given type. This is taking the transitive
     * closure of getDirectPredecessors() and filtering out all nodes not of the given type.
     */
    public Set<? extends AbstractExperimentDesignNode> getPredecessorsOfType(ExperimentDesignNodeType type) {
        if (!type.isPredecessorOf(getNodeType())) {
            return Collections.emptySet();
        }
        Set<? extends AbstractExperimentDesignNode> directPreds = getDirectPredecessors();
        if (type.isDirectPredecessorOf(getNodeType())) {
            return directPreds;
        }
        Set<AbstractExperimentDesignNode> preds = new HashSet<AbstractExperimentDesignNode>();
        for (AbstractExperimentDesignNode node : directPreds) {
            preds.addAll(node.getPredecessorsOfType(type));
        }
        return preds;
    }

    /**
     * Add a new node as a direct successor of this node. This method ensures the node is of the correct type;
     * subclasses should implement doAddDirectSuccessor to actually perform the addition.
     * @param successor the new node
     */
    public void addDirectSuccessor(AbstractExperimentDesignNode successor) {
        if (!successor.getNodeType().isDirectSuccessorOf(getNodeType())) {
            throw new IllegalArgumentException("Node type must be the direct successor of this node's type: "
                    + successor);
        }
        doAddDirectSuccessor(successor);
    }

    /**
     * Actually add the new node as a direct successor of this node.
     * subclasses must implement this method to actually perform the addition.
     * @param successor the new node. it is assumed this node is of the correct type
     */
    protected abstract void doAddDirectSuccessor(AbstractExperimentDesignNode successor);

    /**
     * Add a new node as a direct predecessor of this node. This method ensures the node is of the correct type;
     * subclasses should implement doAddDirectPredecessor to actually perform the addition.
     * @param predecessor the new node
     */
    public void addDirectPredecessor(AbstractExperimentDesignNode predecessor) {
        if (!predecessor.getNodeType().isDirectPredecessorOf(getNodeType())) {
            throw new IllegalArgumentException("Node type must be the direct predecessor of this node's type: "
                    + predecessor);
        }
        doAddDirectPredecessor(predecessor);
    }

    /**
     * Actually add the new node as a direct predecessor of this node.
     * subclasses must implement this method to actually perform the addition.
     * @param predecessor the new node. it is assumed this node is of the correct type
     */
    protected abstract void doAddDirectPredecessor(AbstractExperimentDesignNode predecessor);

    private void mergeProtocolApplications(ProtocolApplicable protocolApplicable) {
        List<ProtocolApplication> newPas = new ArrayList<ProtocolApplication>();
        newPas.addAll(protocolApplicable.getProtocolApplications());

        for (ProtocolApplication pa : protocolApplicable.getProtocolApplications()) {
            for (ProtocolApplication currPa : this.getProtocolApplications()) {
                if (currPa.getProtocol().equals(pa.getProtocol())) {
                    boolean allPvMatch = true;

                    for (AbstractParameterValue pv : pa.getValues()) {
                        boolean foundPv = false;
                        for (AbstractParameterValue currPv : currPa.getValues()) {
                            if (currPv.matches(pv)) {
                                foundPv = true;
                                break;
                            }
                        }
                        if (!foundPv) {
                            allPvMatch = false;
                            break;
                        }
                    }

                    if (allPvMatch && pa.getValues().size() == currPa.getValues().size()) {
                        newPas.remove(pa);
                    }
                }
            }
        }

        Collections.reverse(newPas);
        for (ProtocolApplication newPa : newPas) {
            this.getProtocolApplications().add(0, new ProtocolApplication(newPa));
        }
    }

    /**
     * Return the characteristics with given category that are present in the chain terminating at this node.
     * 
     * The set of returned characteristics is determined as follows:
     * <ul>
     * <li>If this node has characteristics with the given category directly, then the returned set consists of all such
     * characteristics</li>
     * <li>Otherwise, the returned set is given by the union of the results of calling getCharacteristicsRecursively on
     * each direct predecessor of this ExperimentDesignNode</li>
     * </ul>
     * 
     * @param category the category of characteristics to find
     * @return the characteristics with given category that are present in the chain terminating at this node, as
     *         described above.
     */
    public Set<AbstractCharacteristic> getCharacteristicsRecursively(final Category category) {
        Set<AbstractCharacteristic> chars = new HashSet<AbstractCharacteristic>(getCharacteristics(category));
        
        if (chars.isEmpty()) {
            for (AbstractExperimentDesignNode predecessor : getDirectPredecessors()) {
                chars.addAll(predecessor.getCharacteristicsRecursively(category));
            }
        }
        
        return chars;
    }
    
    /**
     * Return the characteristics with given category that are directly present on this ExperimentDesignNode.
     *  
     * @param category category
     * @return the characteristics with given category that are directly present on this.
     */
    public Set<AbstractCharacteristic> getCharacteristics(final Category category) {
        return Collections.emptySet();
    }

    /**
     * @return all the data files related to this node.
     */
    @Transient
    public abstract Set<CaArrayFile> getAllDataFiles();

    /**
     * Propagates the last modified data time up the chain.
     * @param lastModifiedDataTime the lastDataModificationDate to set.
     */
    public void propagateLastModifiedDataTime(final Date lastModifiedDataTime) {
        for (AbstractExperimentDesignNode node : getDirectPredecessors()) {
            node.propagateLastModifiedDataTime(lastModifiedDataTime);
        }
    }

}
