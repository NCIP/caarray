//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.web.filter;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.domain.ConfigParamEnum;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterConfig;

import org.apache.commons.configuration.DataConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.FilterDispatcher;

/**
 * filter to initialize struts 2 of caarray 2.
 * @author Scott Miller
 */
public class CaarrayStruts2FilterDispatcher extends FilterDispatcher {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Dispatcher createDispatcher(FilterConfig filterConfig) {
        Map<String, String> params = new HashMap<String, String>();
        for (Enumeration e = filterConfig.getInitParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }

        DataConfiguration config = ConfigurationHelper.getConfiguration();
        String multiPartSaveDir = config.getString(ConfigParamEnum.STRUTS_MULTIPART_SAVEDIR.name());
        if (StringUtils.isNotBlank(multiPartSaveDir)) {
            params.put(StrutsConstants.STRUTS_MULTIPART_SAVEDIR, multiPartSaveDir);
        }
        return new Dispatcher(filterConfig.getServletContext(), params);
    }
}
