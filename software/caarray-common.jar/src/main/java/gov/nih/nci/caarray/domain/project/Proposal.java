package gov.nih.nci.caarray.domain.project;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import gov.nih.nci.caarray.domain.AbstractCaArrayEntity;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.domain.project.ProposalStatus;

/**
 * A proposal for a new project.
 */
@Entity
public class Proposal extends AbstractCaArrayEntity {

    private static final long serialVersionUID = 6882932219345651741L;

    private Project project;
    private ProposalStatus status;

    /**
     * Generates a new, empty project proposal for use at the start of the proposal process.
     * 
     * @return the initialized
     */
    public static Proposal createNew() {
        Proposal proposal = new Proposal();
        proposal.setProject(Project.createNew());
        return proposal;
    }

    /**
     * @return the project
     */
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ForeignKey(name = "PROPOSAL_PROJECT_FK")
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the workflow status of this proposal
     * 
     * @return the status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ProposalStatus getStatus() {
        return status;
    }

    /**
     * Sets the workflow status of this proposal
     * 
     * @param status the status to set
     */
    public void setStatus(ProposalStatus status) {
        this.status = status;
    }

    /**
     * Returns whether the proposal can be saved as a draft in its current state
     * @return whether the proposal can be saved as a draft in its current state
     */
    @Transient
    public boolean isSaveDraftAllowed() {
        return getId() == null || getStatus().equals(ProposalStatus.DRAFT);
    }

    /**
     * Returns whether the proposal can be submitted in its current state
     * @return whether the proposal can be submitted in its current state
     */
    @Transient
    public boolean isSubmissionAllowed() {
        return getId() == null || getStatus().equals(ProposalStatus.DRAFT)
                || getStatus().equals(ProposalStatus.RETURNED_FOR_REVISION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
