//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.action.project;

import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.plugins.CaArrayPluginsFacade;
import gov.nih.nci.caarray.plugins.ProjectTab;
import gov.nih.nci.caarray.util.CaArrayUsernameHolder;
import gov.nih.nci.caarray.web.plugins.ProjectTabModuleDescriptor;
import gov.nih.nci.security.authorization.domainobjects.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.validator.annotations.CustomValidator;

/**
 * Base Action class for all actions dealing with Project lifecycle.
 *
 * @author Dan Kokotov
 */
public abstract class AbstractBaseProjectAction extends ActionSupport implements Preparable {
    private static final long serialVersionUID = 1L;

    /**
     * workspace.
     */
    public static final String WORKSPACE_RESULT = "workspace";
    /**
     * details.
     */
    public static final String DETAILS_RESULT = "details";
    /**
     * reload the project in top level frame.
     */
    public static final String RELOAD_PROJECT_RESULT = "reload-project";

    /**
     * Width (in characters) to which the experiment title should be truncated to prevent messages from overflowing the
     * UI.
     */
    public static final int TRUNCATED_TITLE_WIDTH = 80;

    private Project project = new Project();

    private boolean editMode;

    /**
     * constructor.
     */
    public AbstractBaseProjectAction() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepare() {
        Project retrieved = null;
        if (this.project.getId() != null) {
            retrieved =
                    ServiceLocatorFactory.getGenericDataService().getPersistentObject(Project.class,
                            this.project.getId());
        } else if (this.project.getExperiment().getPublicIdentifier() != null) {
            retrieved =
                    ServiceLocatorFactory.getProjectManagementService().getProjectByPublicId(
                            this.project.getExperiment().getPublicIdentifier());
        }
        if (retrieved != null) {
            this.project = retrieved;
        }
    }

    /**
     * get the csm user. This method is extracted so it can be overwritten in test cases.
     *
     * @return the csm user.
     */
    protected User getCsmUser() {
        return CaArrayUsernameHolder.getCsmUser();
    }

    /**
     * @return the project
     */
    @CustomValidator(type = "hibernate")
    public Project getProject() {
        return this.project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Convenience method for getting the experiment of the current project.
     *
     * @return the project's experiment
     */
    protected Experiment getExperiment() {
        return getProject().getExperiment();
    }

    /**
     * @return the editMode
     */
    public boolean isEditMode() {
        return this.editMode;
    }

    /**
     * @param editMode the editMode to set
     */
    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * code inspired by outputUrl.tag.
     *
     * @return the current project's permanent URL.
     */
    protected String getProjectPermaLink() {
        return getAbsoluteLink("/project/" + (getProject().getExperiment().getPublicIdentifier()));
    }

    private String getAbsoluteLink(String relativeUrl) {
        final HttpServletRequest request = ServletActionContext.getRequest();
        final StringBuffer sb = new StringBuffer();
        final String scheme = request.getScheme();
        final String host = request.getServerName();
        final int port = request.getServerPort();
        sb.append(scheme).append("://").append(host);
        // CHECKSTYLE:OFF port 80 and 443 are not magic numbers
        if (("http".equalsIgnoreCase(scheme) && port != 80) || ("https".equalsIgnoreCase(scheme) && port != 443)) {
            sb.append(":").append(port);
        }
        // CHECKSTYLE:ON
        sb.append(request.getContextPath());
        sb.append(relativeUrl);
        return sb.toString();
    }

    /**
     * @return a list of project tabs defined in plugins
     */
    public Collection<ProjectTab> getProjectTabs() {
        final List<ProjectTab> projectTabs = new ArrayList<ProjectTab>();
        for (final Plugin plugin : CaArrayPluginsFacade.getInstance().getPlugins()) {
            for (final ModuleDescriptor<?> md : plugin.getModuleDescriptors()) {
                if (md instanceof ProjectTabModuleDescriptor) {
                    projectTabs.add(((ProjectTabModuleDescriptor) md).getTab());
                }
            }
        }
        return projectTabs;
    }
}
