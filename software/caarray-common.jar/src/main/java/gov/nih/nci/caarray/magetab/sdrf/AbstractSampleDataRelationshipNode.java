//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
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
