//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.domain.project.AbstractExperimentDesignNode;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.ExperimentDesignNodeType;
import gov.nih.nci.caarray.domain.sample.AbstractBioMaterial;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * Enum of different types of nodes in the Experiment tree used to select target annotations for
 * data import.
 * 
 * @author dkokotov
 *
 */
public enum ExperimentDesignTreeNodeType {    
    /** The root node, representing the experiment. */
    ROOT,
    /** The sources root node, whose children are all the sources in the experiment. */
    EXPERIMENT_SOURCES {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
            return experiment.getSources();
        }   
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return SOURCE;
        }
    },
    /** The samples root node, whose children are all the samples in the experiment. */
    EXPERIMENT_SAMPLES {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
            return experiment.getSamples();
        }   
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return SAMPLE;
        }

    },
    /** The extracted root node, whose children are all the extracts in the experiment. */
    EXPERIMENT_EXTRACTS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
            return experiment.getExtracts();
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return EXTRACT;
        }
    },
    /** The labeled extracts root node, whose children are all the labeled extracts in the experiment. */
    EXPERIMENT_LABELED_EXTRACTS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
            return experiment.getLabeledExtracts();
        }   
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return LABELED_EXTRACT;
        }
    },
    /** The hybridizations root node, whose children are all the hybridizations in the experiment. */
    EXPERIMENT_HYBRIDIZATIONS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
            return experiment.getHybridizations();
        }   
        
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return HYBRIDIZATION;
        }
    },
    /** The samples root node, whose children are all the samples in the experiment. */
    BIOMATERIAL_SAMPLES {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(AbstractBioMaterial bioMaterial) {
            return bioMaterial.getSuccessorsOfType(ExperimentDesignNodeType.SAMPLE);
        }   

        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return SAMPLE;
        }
    },
    /** The extracted root node, whose children are all the extracts in the biomaterial. */
    BIOMATERIAL_EXTRACTS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(AbstractBioMaterial bioMaterial) {
            return bioMaterial.getSuccessorsOfType(ExperimentDesignNodeType.EXTRACT);
        }   

        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return EXTRACT;
        }
    },
    /** The labeled extracts root node, whose children are all the labeled extracts in the biomaterial. */
    BIOMATERIAL_LABELED_EXTRACTS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(AbstractBioMaterial bioMaterial) {
            return bioMaterial.getSuccessorsOfType(ExperimentDesignNodeType.LABELED_EXTRACT);
        }   

        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return LABELED_EXTRACT;
        }
    },
    /** The hybridizations root node, whose children are all the hybridizations in the biomaterial. */
    BIOMATERIAL_HYBRIDIZATIONS {
        /**
         * {@inheritDoc}
         */
        @Override
        public Set<? extends AbstractExperimentDesignNode> getContainedNodes(AbstractBioMaterial bioMaterial) {
            return bioMaterial.getSuccessorsOfType(ExperimentDesignNodeType.HYBRIDIZATION);
        }   

        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignTreeNodeType getChildrenNodeType() {
            return HYBRIDIZATION;
        }
    },
    /** A biomaterial node. */
    SOURCE {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignNodeType getNodeType() {
            return ExperimentDesignNodeType.SOURCE;
        }
    },
    /** A biomaterial node. */
    SAMPLE {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignNodeType getNodeType() {
            return ExperimentDesignNodeType.SAMPLE;
        }
    },
    /** A biomaterial node. */
    EXTRACT {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignNodeType getNodeType() {
            return ExperimentDesignNodeType.EXTRACT;
        }
    },
    /** A biomaterial node. */
    LABELED_EXTRACT {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignNodeType getNodeType() {
            return ExperimentDesignNodeType.LABELED_EXTRACT;
        }
    },
    /** A hybridization node. */
    HYBRIDIZATION {
        /**
         * {@inheritDoc}
         */
        @Override
        public ExperimentDesignNodeType getNodeType() {
            return ExperimentDesignNodeType.HYBRIDIZATION;
        }
    };
    
    /**
     * @return whether this node type represents a root  
     */
    public boolean isExperimentRootNode() {
        return getExperimentRootNodeTypes().contains(this);
    }

    /**
     * @return the biomaterialRootNode
     */
    public boolean isBiomaterialRootNode() {
        return getBiomaterialRootNodeTypes().contains(this);
    }
    
    /**
     * @return whether this node type represents a root  
     */
    public boolean isBiomaterialNode() {
        return getBiomaterialNodeTypes().contains(this);
    }

    /**
     * For experiment root node types, obtain the nodes of appropriate type from given experiment. For all other 
     * node types, returns the empty set.
     * @param experiment the experiment 
     * @return the nodes of the correct type from the given experiment, or empty set for other node types
     */
    public Set<? extends AbstractExperimentDesignNode> getContainedNodes(Experiment experiment) {
        return Collections.emptySet();
    }

    /**
     * For biomaterial root node types, obtain the nodes of appropriate type from given biomaterial. For all other 
     * node types, returns the empty set.
     * @param bioMaterial the biomaterial 
     * @return the nodes of the correct type from the given biomaterial, or empty set for other node types
     */
    public Set<? extends AbstractExperimentDesignNode> getContainedNodes(AbstractBioMaterial bioMaterial) {
        return Collections.emptySet();
    }
    
    /**
     * @return For root node types, returns the type of tree node that children nodes of this node type should have.
     * For all other node types return null
     */
    public ExperimentDesignTreeNodeType getChildrenNodeType() {
        return null;
    }

    /**
     * @return For biomaterial and hybridization node types, return the experiment design node type corresponding to 
     * this tree type. For other node types, return null.
     */
    public ExperimentDesignNodeType getNodeType() {
        return null;
    }

    /**
     * @return the set of node types that are experiment root nodes, ie nodes which act as holders
     * of particular kind of experiment design nodes at the experiment level. E.g. Sources, Samples, etc.
     */
    public static EnumSet<ExperimentDesignTreeNodeType> getExperimentRootNodeTypes() {
        return EnumSet.range(EXPERIMENT_SOURCES, EXPERIMENT_HYBRIDIZATIONS);
    }
    
    /**
     * @return the set of node types that are biomaterial root nodes, ie nodes which act as holders
     * of particular kind of experiment design nodes downstream of a biomaterial. E.g. Associated Sources, 
     * Samples, etc.
     */
    public static EnumSet<ExperimentDesignTreeNodeType> getBiomaterialRootNodeTypes() {
        return EnumSet.range(BIOMATERIAL_SAMPLES, BIOMATERIAL_HYBRIDIZATIONS);
    }

    /**
     * @return the set of node types for actual experiment design nodes.
     */
    public static EnumSet<ExperimentDesignTreeNodeType> getExperimentDesignNodeTypes() {
        return EnumSet.range(SOURCE, HYBRIDIZATION);
    }

    /**
     * @return the set of node types for actual experiment design nodes that are biomaterial nodes.
     */
    public static EnumSet<ExperimentDesignTreeNodeType> getBiomaterialNodeTypes() {
        return EnumSet.range(SOURCE, LABELED_EXTRACT);
    }
}
