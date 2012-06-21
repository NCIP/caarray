/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-war
 * Software was developed in conjunction with the National Cancer Institute 
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent 
 * government employees are authors, any rights in such works shall be subject 
 * to Title 17 of the United States Code, section 105. 
 *
 * This caarray-war Software License (the License) is between NCI and You. You (or 
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
 * its rights in the caarray-war Software to (i) use, install, access, operate, 
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-war Software; (ii) distribute and 
 * have distributed to and by third parties the caarray-war Software and any 
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