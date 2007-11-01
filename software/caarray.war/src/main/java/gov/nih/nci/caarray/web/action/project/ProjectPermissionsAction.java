package gov.nih.nci.caarray.web.action.project;

import static gov.nih.nci.caarray.web.action.ActionHelper.getGenericDataService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getPermissionsManagementService;
import static gov.nih.nci.caarray.web.action.ActionHelper.getProjectManagementService;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;

import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() throws VocabularyServiceException {
        super.prepare();
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
        this.collaboratorGroupsWithoutProfiles = getPermissionsManagementService().getCollaboratorGroups();
        this.collaboratorGroupsWithoutProfiles.removeAll(getProject().getGroupProfiles().keySet());
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
        return "success";
    }

    /**
     * loads the public access profile for editing
     *
     * @return success
     */
    public String loadPublicProfile() {
        this.accessProfile = getProject().getPublicProfile();
        return "accessProfile";
    }

    /**
     * loads the access profile of given collaboration group for editing
     *
     * @return success
     */
    public String loadGroupProfile() {
        this.accessProfile = getProject().getGroupProfiles().get(this.collaboratorGroup);
        return "accessProfile";
    }

    /**
     * @return the collaboratorGroup
     */
    public CollaboratorGroup getCollaboratorGroup() {
        return collaboratorGroup;
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
        return collaboratorGroupsWithoutProfiles;
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
        return accessProfile;
    }

    /**
     * @param accessProfile the accessProfile to set
     */
    public void setAccessProfile(AccessProfile accessProfile) {
        this.accessProfile = accessProfile;
    }
}
