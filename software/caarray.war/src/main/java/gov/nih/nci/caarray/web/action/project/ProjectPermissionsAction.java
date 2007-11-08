package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getPermissionsManagementService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.web.action.ActionHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.validation.SkipValidation;

import com.opensymphony.xwork2.validator.annotations.Validation;

/**
 * Base Action for implementing a single tab of the Project management ui
 *
 * @author John Hedden, Dan Kokotov, Scott Miller
 */
@Validation
public class ProjectPermissionsAction extends BaseProjectAction {
    private static final long serialVersionUID = 1L;

    private List<CollaboratorGroup> collaboratorGroupsWithoutProfiles = new ArrayList<CollaboratorGroup>();
    private CollaboratorGroup collaboratorGroup = new CollaboratorGroup();
    private AccessProfile accessProfile = new AccessProfile();
    private boolean publicProfile;
    private String profileOwnerName;
    private Map<Long, SampleSecurityLevel> sampleSecurityLevels = new HashMap<Long, SampleSecurityLevel>();
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();

        this.collaboratorGroupsWithoutProfiles = getPermissionsManagementService().getCollaboratorGroups();
        this.collaboratorGroupsWithoutProfiles.removeAll(getProject().getGroupProfiles().keySet());

        if (this.collaboratorGroup.getId() != null) {
            this.collaboratorGroup = getGenericDataService().retrieveEnity(CollaboratorGroup.class, this.collaboratorGroup.getId());
        }
        if (this.accessProfile.getId() != null) {
            this.accessProfile = getGenericDataService().retrieveEnity(AccessProfile.class, this.accessProfile.getId());
        }
    }

    /**
     * edit existing project permissions
     *
     * @return path String
     */
    @SkipValidation
    public String editPermissions() {
        return "success";
    }

    /**
     * Toggles the browsability status.
     *
     * @return success
     */
    @SkipValidation
    public String toggleBrowsability() {
        getProjectManagementService().toggleBrowsableStatus(getProject().getId());
        return "success";
    }

    /**
     * Creates an access profile for a new collaboration group
     *
     * @return success
     */
    public String addGroupProfile() {
        getProjectManagementService().addGroupProfile(getProject(), this.collaboratorGroup);
        this.collaboratorGroupsWithoutProfiles.remove(this.collaboratorGroup);
        return "success";
    }

    /**
     * loads the public access profile for editing
     *
     * @return success
     */
    @SkipValidation
    public String loadPublicProfile() {
        this.accessProfile = getProject().getPublicProfile();
        this.publicProfile = true;
        this.profileOwnerName = getText("project.permissions.publicProfile");
        setupSamplePermissions();
        return "accessProfile";
    }

    /**
     * loads the access profile of given collaboration group for editing
     *
     * @return success
     */
    @SkipValidation
    public String loadGroupProfile() {
        this.accessProfile = getProject().getGroupProfiles().get(this.collaboratorGroup);
        this.publicProfile = false;
        this.profileOwnerName = getText("project.permissions.groupProfile", new String[] { this.collaboratorGroup
                .getGroup().getGroupName() });
        setupSamplePermissions();
        return "accessProfile";
    }

    private void setupSamplePermissions() {
        for (Map.Entry<Sample, SampleSecurityLevel> sampleEntry : this.accessProfile.getSampleSecurityLevels().entrySet()) {
            this.sampleSecurityLevels.put(sampleEntry.getKey().getId(), sampleEntry.getValue());
        }
    }

    private void saveSamplePermissions() {
        // if the new experiment-wide security level does not allow sample-level permissions
        // then any existing sample-level security levels get wiped
        this.accessProfile.getSampleSecurityLevels().clear();
        if (this.accessProfile.getSecurityLevel().isSampleLevelPermissionsAllowed()) {
            for (Map.Entry<Long, SampleSecurityLevel> sampleEntry : this.sampleSecurityLevels.entrySet()) {
                Sample sample = getGenericDataService().retrieveEnity(Sample.class, sampleEntry.getKey());
                if (this.accessProfile.getSecurityLevel().getSampleSecurityLevels().contains(sampleEntry.getValue())) {
                    this.accessProfile.getSampleSecurityLevels().put(sample, sampleEntry.getValue());
                }
            }
        }
    }

    /**
     * Saves an access profile
     *
     * @return success
     */
    @SkipValidation
    public String saveAccessProfile() {
        saveSamplePermissions();
        getPermissionsManagementService().saveAccessProfile(this.accessProfile);
        ActionHelper.saveMessage(getText("project.permissionsSaved"));
        return "success";
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
     * @return the collaboratorGroupsWithoutProfiles
     */
    public List<CollaboratorGroup> getCollaboratorGroupsWithoutProfiles() {
        return this.collaboratorGroupsWithoutProfiles;
    }

    /**
     * @param collaboratorGroupsWithoutProfiles the collaboratorGroupsWithoutProfiles to set
     */
    public void setCollaboratorGroupsWithoutProfiles(List<CollaboratorGroup> collaboratorGroupsWithoutProfiles) {
        this.collaboratorGroupsWithoutProfiles = collaboratorGroupsWithoutProfiles;
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
     * @return the profileOwnerName
     */
    public String getProfileOwnerName() {
        return this.profileOwnerName;
    }

    /**
     * @param profileOwnerName the profileOwnerName to set
     */
    public void setProfileOwnerName(String profileOwnerName) {
        this.profileOwnerName = profileOwnerName;
    }

    /**
     * @return the publicProfile
     */
    public boolean isPublicProfile() {
        return this.publicProfile;
    }

    /**
     * @param publicProfile the publicProfile to set
     */
    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }
}
