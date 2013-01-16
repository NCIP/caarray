//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.plugins;

import gov.nih.nci.caarray.plugins.ProjectTab;

import org.dom4j.Element;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;

/**
 * ModuleDescritor for plugin modules that define a new project tab to be displayed in the project details view.
 * 
 * @author dkokotov
 */
public class ProjectTabModuleDescriptor extends AbstractModuleDescriptor<Void> {
    private ProjectTab tab;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Plugin plugin, Element element) throws PluginParseException {
        super.init(plugin, element);

        this.tab = new ProjectTab();
        this.tab.setLabel(element.attributeValue("label"));
        this.tab.setUrl(element.attributeValue("url"));
        this.tab.setKey(element.attributeValue("tab-key"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Void getModule() {
        return null;
    }

    /**
     * @return a ProjectTab bean describing the tab defined by the module
     */
    public ProjectTab getTab() {
        return this.tab;
    }

    /**
     * Setter exposed solely for JavaBean convention.
     * 
     * @param tab ProjectTab to set.
     */
    public void setTab(ProjectTab tab) {
        this.tab = tab;
    }
}
