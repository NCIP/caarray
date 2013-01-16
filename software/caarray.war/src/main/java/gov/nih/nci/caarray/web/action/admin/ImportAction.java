//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================

package gov.nih.nci.caarray.web.action.admin;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import gov.nih.nci.caarray.application.ServiceLocatorFactory;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.project.Project;
import java.util.List;

/**
 * list reimportable projects or designs.
 * @author gax
 * @since 2.4.0
 */
public class ImportAction extends ActionSupport {
    private static final long serialVersionUID = 1L;

    private List<Project> projects;
    private List<ArrayDesign> arrayDesignes;

    /**
     * @return the projects to reimport.
     */
    public List<Project> getProjects() {
        return projects;
    }

    /**
     * @return designs to reimport.
     */
    public List<ArrayDesign> getArrayDesigns() {
        return arrayDesignes;
    }

    /**
     * list projects action.
     * @return success.
     */
    public String projects() {
        projects = ServiceLocatorFactory.getProjectManagementService().getProjectsWithReImportableFiles();
        return Action.SUCCESS;
    }

    /**
     * list projects action.
     * @return success.
     */
    public String designs() {
        arrayDesignes = ServiceLocatorFactory.getArrayDesignService().getArrayDesignsWithReImportableFiles();
        return Action.SUCCESS;
    }

    /**
     * show Re-Process tabs.
     * @return success.
     */
    public String reimport() {
        return Action.SUCCESS;
    }
    
}
