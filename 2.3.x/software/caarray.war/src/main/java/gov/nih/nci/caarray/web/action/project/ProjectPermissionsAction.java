//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getPermissionsManagementService;
import static gov.nih.nci.caarray.web.action.CaArrayActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.fiveamsolutions.nci.commons.web.struts2.action.ActionHelper;
import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Base Action for implementing a single tab of the Project management ui.
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectPermissionsAction extends AbstractBaseProjectAction {
    private static final long serialVersionUID = 1L;

    private List<CollaboratorGroup> collaboratorGroups = new ArrayList<CollaboratorGroup>();
    private CollaboratorGroup collaboratorGroup = new CollaboratorGroup();
    private Set<AccessProfile> accessProfiles = new TreeSet<AccessProfile>();
    private AccessProfile accessProfile = new AccessProfile(SecurityLevel.NONE);
    private Map<Long, SampleSecurityLevel> sampleSecurityLevels = new TreeMap<Long, SampleSecurityLevel>();
    private boolean useTcgaPolicy;

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();

        this.collaboratorGroups = getPermissionsManagementService().getCollaboratorGroups();
        if (this.collaboratorGroup.getId() != null) {
            CollaboratorGroup retrieved = getGenericDataService().getPersistentObject(CollaboratorGroup.class,
                                                                           this.collaboratorGroup.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.collaboratorGroup,
                        SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
            } else {
                this.collaboratorGroup = retrieved;
            }
        }
        if (this.accessProfile.getId() != null) {
            AccessProfile retrieved = getGenericDataService().getPersistentObject(AccessProfile.class,
                    this.accessProfile.getId());
            if (retrieved == null) {
                throw new PermissionDeniedException(this.accessProfile,
                        SecurityUtils.PERMISSIONS_PRIVILEGE, UsernameHolder.getUser());
            } else {
                this.accessProfile = retrieved;
            }
        }
        accessProfiles = new TreeSet<AccessProfile>();
        accessProfiles.add(getProject().getPublicProfile());
        accessProfiles.addAll(getProject().getGroupProfiles().values());
    }

    /**
     * edit existing project permissions.
     *
     * @return path String
     */
    @SkipValidation
    public String editPermissions() {
        return SUCCESS;
    }

    /**
     * Saves whether to use tcga policy.
     *
     * @return success
     */
    @SkipValidation
    public String setTcgaPolicy() {
        try {
            getProjectManagementService().setUseTcgaPolicy(getProject().getId(), this.useTcgaPolicy);
            ActionHelper.saveMessage(getText("project.tcgaPolicyUpdated", new String[]{getText("project.tcgaPolicy."
                    + (this.useTcgaPolicy ? "enabled" : "disabled")) }));
            return SUCCESS;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText("project.permissionsSaveProblem", args));
            return INPUT;
        }
    }

    /**
     * loads the public access profile for editing.
     *
     * @return success
     */
    @SkipValidation
    public String loadPublicProfile() {
        this.accessProfile = getProject().getPublicProfile();
        setupSamplePermissions();
        return "accessProfile";
    }

    /**
     * loads the access profile of given collaboration group for editing.
     *
     * @return success
     */
    @SkipValidation
    public String loadGroupProfile() {
        this.accessProfile = getProject().getGroupProfiles().get(this.collaboratorGroup);
        if (this.accessProfile == null) {
            this.accessProfile = new AccessProfile(SecurityLevel.NONE);
            this.accessProfile.setGroup(this.collaboratorGroup);
            this.accessProfile.setProjectForGroupProfile(getProject());
        }
        setupSamplePermissions();
        return "accessProfile";
    }

    private void setupSamplePermissions() {
        for (Map.Entry<Sample, SampleSecurityLevel> sampleEntry : this.accessProfile.getSampleSecurityLevels()
                                                                                    .entrySet()) {
            this.sampleSecurityLevels.put(sampleEntry.getKey().getId(), sampleEntry.getValue());
        }
    }

    private void saveSamplePermissions() {
        // if the new experiment-wide security level does not allow sample-level permissions
        // then any existing sample-level security levels get wiped
        this.accessProfile.getSampleSecurityLevels().clear();
        if (this.accessProfile.getSecurityLevel().isSampleLevelPermissionsAllowed()) {
            for (Map.Entry<Long, SampleSecurityLevel> sampleEntry : this.sampleSecurityLevels.entrySet()) {
                Sample sample = getGenericDataService().getPersistentObject(Sample.class, sampleEntry.getKey());
                if (this.accessProfile.getSecurityLevel().getSampleSecurityLevels().contains(sampleEntry.getValue())) {
                    this.accessProfile.getSampleSecurityLevels().put(sample, sampleEntry.getValue());
                }
            }
        }
    }

    /**
     * Saves an access profile.
     *
     * @return success
     */
    @SkipValidation
    public String saveAccessProfile() {
        try {
            if (this.accessProfile.getId() == null && this.collaboratorGroup != null) {
                // must be a new access profile
                AccessProfile newProfile =
                        getProjectManagementService().addGroupProfile(getProject(), this.collaboratorGroup);
                newProfile.setSecurityLevel(this.accessProfile.getSecurityLevel());
                this.accessProfile = newProfile;
                this.accessProfiles.add(newProfile);
            }
            saveSamplePermissions();
            getPermissionsManagementService().saveAccessProfile(this.accessProfile);
            ActionHelper.saveMessage(getText("project.permissionsSaved"));
            return SUCCESS;
        } catch (ProposalWorkflowException e) {
            List<String> args = new ArrayList<String>();
            args.add(getProject().getExperiment().getTitle());
            ActionHelper.saveMessage(getText("project.permissionsSaveProblem", args));
            return INPUT;
        }
    }

    /**
     * @return the collaboratorGroup
     */
    public CollaboratorGroup getCollaboratorGroup() {
        return this.collaboratorGroup;
    }

    /**
     * @param collaboratorGroup the collaboratorGroup to set
     */
    public void setCollaboratorGroup(CollaboratorGroup collaboratorGroup) {
        this.collaboratorGroup = collaboratorGroup;
    }

    /**
     * @return the accessProfile
     */
    public AccessProfile getAccessProfile() {
        return this.accessProfile;
    }

    /**
     * @param accessProfile the accessProfile to set
     */
    public void setAccessProfile(AccessProfile accessProfile) {
        this.accessProfile = accessProfile;
    }

    /**
     * @return the accessProfiles
     */
    public Set<AccessProfile> getAccessProfiles() {
        return this.accessProfiles;
    }

    /**
     * @return the sampleSecurityLevels
     */
    public Map<Long, SampleSecurityLevel> getSampleSecurityLevels() {
        return this.sampleSecurityLevels;
    }

    /**
     * @param sampleSecurityLevels the sampleSecurityLevels to set
     */
    public void setSampleSecurityLevels(Map<Long, SampleSecurityLevel> sampleSecurityLevels) {
        this.sampleSecurityLevels = sampleSecurityLevels;
    }

    /**
     * @return the useTcgaPolicy
     */
    public boolean isUseTcgaPolicy() {
        return this.useTcgaPolicy;
    }

    /**
     * @param useTcgaPolicy the useTcgaPolicy to set
     */
    public void setUseTcgaPolicy(boolean useTcgaPolicy) {
        this.useTcgaPolicy = useTcgaPolicy;
    }

    /**
     * @return the collaboratorGroups
     */
    public List<CollaboratorGroup> getCollaboratorGroups() {
        return this.collaboratorGroups;
    }
}
