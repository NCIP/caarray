package gov.nih.nci.caarray.domain.project;


/**
 * An enumeration of different type of nodes in the experiment design graph. 
 * these correspond to the node types in an SDRF in mage tab terminology.
 * @author dkokotov
 */
public enum ExperimentDesignNodeType {
    /**
     * The Source node type.
     */
    SOURCE,
    /**
     * The Sample node type.
     */
    SAMPLE,
    /**
     * The Extract node type.
     */
    EXTRACT,
    /**
     * The Labeled Extract node type.
     */
    LABELED_EXTRACT,
    /**
     * The Hybridization node type.
     */
    HYBRIDIZATION;
    
    /**
     * @return the node type to the right of this node type in a hybridization channel or experiment design graph.
     */
    public ExperimentDesignNodeType getSuccessorType() {
        ExperimentDesignNodeType[] values = ExperimentDesignNodeType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this) {
                return i == values.length - 1 ? null : values[i + 1];
            }
        }
        throw new IllegalStateException("Could not find self in enum values");
    }
    
    /**
     * @return the node type to the left of this node type in a hybridization channel or experiment design graph.
     */
    public ExperimentDesignNodeType getPredecesorType() {
        ExperimentDesignNodeType[] values = ExperimentDesignNodeType.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this) {
                return i == 0 ? null : values[i - 1];
            }
        }
        throw new IllegalStateException("Could not find self in enum values");
    }

    /**
     * Return whether this node type is to the right of the given node type in a hybridization channel or 
     * experiment design graph.
     * @param type the node type to check
     * @return true if this node type is to the right of the given node type, false otherwise.
     */
    public boolean isSuccessorOf(ExperimentDesignNodeType type) {
        return this.compareTo(type) > 0;
    }

    /**
     * Return whether this node type is immediately to the right of the given node type in a hybridization channel or 
     * experiment design graph.
     * @param type the node type to check
     * @return true if this node type is to the immediate right of the given node type, false otherwise.
     */
    public boolean isDirectSuccessorOf(ExperimentDesignNodeType type) {
        return this.ordinal() == type.ordinal() + 1;
    }

    /**
     * Return whether this node type is to the left of the given node type in a hybridization channel or 
     * experiment design graph.
     * @param type the node type to check
     * @return true if this node type is to the left of the given node type, false otherwise.
     */
    public boolean isPredecessorOf(ExperimentDesignNodeType type) {
        return this.compareTo(type) < 0;
    }

    /**
     * Return whether this node type is to the immediate left of the given node type in a hybridization channel or 
     * experiment design graph.
     * @param type the node type to check
     * @return true if this node type is to the immediate left of the given node type, false otherwise.
     */
    public boolean isDirectPredecessorOf(ExperimentDesignNodeType type) {
        return this.ordinal() == type.ordinal() - 1;
    }
}
