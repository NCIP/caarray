//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;

import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Common interface for the nodes in the experiment design graph. The nodes
 * include biomaterials, hybridizations.
 * @author dkokotov
 */
@MappedSuperclass
public abstract class AbstractExperimentDesignNode extends AbstractCaArrayEntity {
    private static final long serialVersionUID = 1L;
    
    /**
     * @return the name of this node
     */
    @Transient
    public abstract String getName();

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
}
