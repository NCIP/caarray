//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/master/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.domain.ConfigParamEnum;

import javax.servlet.FilterConfig;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter;

/**
 * filter to initialize struts 2 of caarray 2.
 * @author Scott Miller
 */
public class CaarrayStruts2FilterDispatcher extends StrutsPrepareAndExecuteFilter {
    /**
     * set the upload temporary directory based on our config.
     * {@inheritDoc}
     */
    @Override
    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
        DataConfiguration config = ConfigurationHelper.getConfiguration();
        String multiPartSaveDir = config.getString(ConfigParamEnum.STRUTS_MULTIPART_SAVEDIR.name());
        if (StringUtils.isNotBlank(multiPartSaveDir)) {
            dispatcher.setMultipartSaveDir(multiPartSaveDir);
        }
    }
}
