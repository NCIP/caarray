package gov.nih.nci.caarray.web.interceptor;


import org.apache.commons.lang.CharUtils;

import com.opensymphony.xwork2.interceptor.ParametersInterceptor;

/**
 * Extension of the standard parameters interceptor to remove display tag parameters.
 *
 * @author Scott Miller
 */
public class DisplayTagParametersInterceptor extends ParametersInterceptor {
    private static final long serialVersionUID = 1L;

    private static final String DISPLAY_TAG_PARAM_PREFIX = "d-";
    private static final int DISPLAY_TAG_PARAM_FIRST_NUMBER_INDEX = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isExcluded(String paramName) {
        return DisplayTagParametersInterceptor.isDisplayTagParam(paramName) || super.isExcluded(paramName);
    }

    /**
     * determines if the given param is a display tag param.
     *
     * @param parameterName the name of the param
     * @return true if the param is a display tag param
     */
    public static boolean isDisplayTagParam(String parameterName) {
        boolean retVal = false;
        if (parameterName != null && parameterName.startsWith(DISPLAY_TAG_PARAM_PREFIX)
                && parameterName.length() > DISPLAY_TAG_PARAM_FIRST_NUMBER_INDEX
                && CharUtils.isAsciiNumeric(parameterName.charAt(DISPLAY_TAG_PARAM_FIRST_NUMBER_INDEX))) {
            retVal = true;
        }
        return retVal;
    }
}
