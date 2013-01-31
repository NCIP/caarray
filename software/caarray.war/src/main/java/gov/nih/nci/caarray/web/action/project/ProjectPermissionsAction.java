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
import gov.nih.nci.caarray.application.project.ProjectManagementService;
import gov.nih.nci.caarray.application.project.ProposalWorkflowException;
import gov.nih.nci.caarray.business.vocabulary.VocabularyServiceException;
import gov.nih.nci.caarray.domain.permissions.AccessProfile;
import gov.nih.nci.caarray.domain.permissions.CollaboratorGroup;
import gov.nih.nci.caarray.domain.permissions.SampleSecurityLevel;
import gov.nih.nci.caarray.domain.permissions.SecurityLevel;
import gov.nih.nci.caarray.domain.sample.Sample;
import gov.nih.nci.caarray.domain.search.SearchSampleCategory;
import gov.nih.nci.caarray.security.PermissionDeniedException;
import gov.nih.nci.caarray.security.SecurityUtils;
import gov.nih.nci.caarray.util.UsernameHolder;
import gov.nih.nci.caarray.web.action.CaArrayActionHelper;
import gov.nih.nci.caarray.web.util.LabelValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private List<Long> sampleSecurityLevels = new ArrayList<Long>();
    private boolean useTcgaPolicy;
    private String permSampleKeyword;
    private String permSampleSearch;
    private List<Sample> sampleResults = new ArrayList<Sample>();
    private int sampleResultsCount;
    private String actionButton;
    private SampleSecurityLevel securityChoices;

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
        return "accessProfile";
    }

    private void saveSamplePermissions() {
        // if the new experiment-wide security level does not allow sample-level permissions
        // then any existing sample-level security levels get wiped

        if (this.accessProfile.getSecurityLevel().isSampleLevelPermissionsAllowed()) {
            checkSamplePermissions();
            modifySamplePermissions();
        } else {
            this.accessProfile.getSampleSecurityLevels().clear();
        }
    }

    private void checkSamplePermissions() {
        LOG.debug("Checking sample perms");
        // check whether this is the first time sample permissions are being set.
        if (this.accessProfile.getSampleSecurityLevels() == null
                || this.accessProfile.getSampleSecurityLevels().isEmpty()) {
            Map<Sample, SampleSecurityLevel> samplePermissions = new HashMap<Sample, SampleSecurityLevel>();
            for (Sample sam : getProject().getExperiment().getSamples()) {
                samplePermissions.put(sam, SampleSecurityLevel.NONE);
            }

            this.accessProfile.getSampleSecurityLevels().putAll(samplePermissions);
        }
        LOG.debug("Done checking sample perms");
    }

    private void modifySamplePermissions() {
        LOG.debug("Modifying sample perms");
        for (Long sid : this.sampleSecurityLevels) {
            // -1 is preloaded into the list of checkboxes to
            // get around struts issues.
            if (sid.intValue() == -1) {
                continue;
            }
            Sample sample = getGenericDataService().getPersistentObject(Sample.class, sid);
            if (this.accessProfile.getSecurityLevel().getSampleSecurityLevels().contains(this.securityChoices)) {
                this.accessProfile.getSampleSecurityLevels().put(sample, this.securityChoices);
            }
        }
        LOG.debug("Done modifying sample perms");
    }

    /**
     * Saves an access profile.
     *
     * @return success
     */
    @SkipValidation
    public String saveAccessProfile() {
        if ("search".equals(this.actionButton)) {
            return listSamples();
        } else if ("save".equals(this.actionButton)) {
            try {
                LOG.debug("Saving access profile for project " + getProject().getId());
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
                LOG.debug("Done saving access profile for project " + getProject().getId());
                ActionHelper.saveMessage(getText("project.permissionsSaved"));
                return SUCCESS;
            } catch (ProposalWorkflowException e) {
                List<String> args = new ArrayList<String>();
                args.add(getProject().getExperiment().getTitle());
                ActionHelper.saveMessage(getText("project.permissionsSaveProblem", args));
                return INPUT;
            }
        } else {
            return INPUT;
        }
    }

    private String listSamples() {
        ProjectManagementService pms = CaArrayActionHelper.getProjectManagementService();
        SearchSampleCategory[] categories = new SearchSampleCategory[]{SearchSampleCategory.valueOf(permSampleSearch)};
        sampleResults = pms.searchSamplesByExperimentAndCategory(permSampleKeyword,
                getProject().getExperiment(), categories);
        sampleResultsCount = sampleResults.size();

        return "accessProfile";
    }

    /**
     * @return list of label value beans.
     */
    public static List<LabelValue> getSearchSampleCategories() {
        List<LabelValue> searchCats = new ArrayList<LabelValue>();
        for (SearchSampleCategory cat : SearchSampleCategory.getPermSearchCategories()) {
            searchCats.add(new LabelValue(cat.getResourceKey(), cat.name()));
        }
        return searchCats;
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
    public List<Long> getSampleSecurityLevels() {
        return this.sampleSecurityLevels;
    }

    /**
     * @param sampleSecurityLevels the sampleSecurityLevels to set
     */
    public void setSampleSecurityLevels(List<Long> sampleSecurityLevels) {
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

    /**
     * @return the permSampleKeyword
     */
    public String getPermSampleKeyword() {
        return permSampleKeyword;
    }

    /**
     * @param permSampleKeyword the permSampleKeyword to set
     */
    public void setPermSampleKeyword(String permSampleKeyword) {
        try {
            this.permSampleKeyword = URLDecoder.decode(permSampleKeyword, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            this.permSampleKeyword = permSampleKeyword;
        }
    }

    /**
     * @return the permSampleSearch
     */
    public String getPermSampleSearch() {
        return permSampleSearch;
    }

    /**
     * @param permSampleSearch the permSampleSearch to set
     */
    public void setPermSampleSearch(String permSampleSearch) {
        this.permSampleSearch = permSampleSearch;
    }

    /**
     * @return the sampleResults
     */
    public List<Sample> getSampleResults() {
        return sampleResults;
    }

    /**
     * @param sampleResults the sampleResults to set
     */
    public void setSampleResults(List<Sample> sampleResults) {
        this.sampleResults = sampleResults;
    }

    /**
     * @return the actionButton
     */
    public String getActionButton() {
        return actionButton;
    }

    /**
     * @param actionButton the actionButton
     */
    public void setActionButton(String actionButton) {
        this.actionButton = actionButton;
    }

    /**
     * @return the securityChoices
     */
    public SampleSecurityLevel getSecurityChoices() {
        return securityChoices;
    }

    /**
     * @param securityChoices the securityChoice to set
     */
    public void setSecurityChoices(SampleSecurityLevel securityChoices) {
        this.securityChoices = securityChoices;
    }

    /**
     * @return the sampleResultsCount
     */
    public int getSampleResultsCount() {
        return sampleResultsCount;
    }

    /**
     * @param sampleResultsCount the sampleResultsCount to set
     */
    public void setSampleResultsCount(int sampleResultsCount) {
        this.sampleResultsCount = sampleResultsCount;
    }
}
