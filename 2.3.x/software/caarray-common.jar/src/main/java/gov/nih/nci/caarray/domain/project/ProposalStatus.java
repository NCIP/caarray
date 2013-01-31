//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.domain.project;


/**
 * An enumeration of the different statuses that an experiment Proposal can be in.
 */
public enum ProposalStatus {
    /** draft - visible only to owner, still gathering data.*/
    DRAFT("proposalStatus.draft") {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canTransitionTo(ProposalStatus status) {
            return status == IN_PROGRESS;
        }
    },
    /** submitted - permissions can now be set for other people. */
    IN_PROGRESS("proposalStatus.inProgress") { 
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canTransitionTo(ProposalStatus status) {
            return status == PUBLIC;
        }
    },
    /** public - the experiment is finalized and locked down. */
    PUBLIC("proposalStatus.public") {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canTransitionTo(ProposalStatus status) {
            return status == IN_PROGRESS;
        }
    };

    private final String resourceKey;

    private ProposalStatus(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    /**
     * @return the resource key that should be used to retrieve a label
     * for this ServiceType in the UI
     */
    public String getResourceKey() {
        return this.resourceKey;
    }

    /**
     * Returns whether the given status is a legal transition from this one.
     * @param status the status to check for
     * @return whether the given status is a legal transition from this one
     */
    public abstract boolean canTransitionTo(ProposalStatus status);
}
