//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.staticinjection;

import gov.nih.nci.caarray.web.action.project.ProjectFilesAction;
import gov.nih.nci.caarray.web.filter.OpenSessionInViewFilter;
import gov.nih.nci.caarray.web.helper.DownloadHelper;

import com.google.inject.AbstractModule;

/**
 * @author jscott
 * 
 */
public class CaArrayWarStaticInjectionModule extends AbstractModule {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        requestStaticInjection(OpenSessionInViewFilter.class);
        requestStaticInjection(DownloadHelper.class);
        requestStaticInjection(ProjectFilesAction.class);
    }
}
