//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.protocol.ParameterValue;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplicable;
import gov.nih.nci.caarray.domain.protocol.ProtocolApplication;

import java.util.ArrayList;
import java.util.Collections;
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

                    for (ParameterValue pv : pa.getValues()) {
                        boolean foundPv = false;
                        for (ParameterValue currPv : currPa.getValues()) {
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

}
