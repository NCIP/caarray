//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================

package gov.nih.nci.caarray.application.util;

import gov.nih.nci.caarray.application.ConfigurationHelper;
import gov.nih.nci.caarray.domain.ConfigParamEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Commonly used utility methods.
 * @author gax
 * @since 2.4.0
 */
public final class Utils {
    private static final int DEFAULT_TIMEOUT_SECONDS = 3600;
    private static final Logger LOG = Logger.getLogger(Utils.class);

    private Utils() {
        //
    }

    /**
     * @param value the value to test.
     * @return true is the value can be parsed as an Integer.
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param value the value to test.
     * @return true is the value can be parsed as an Long.
     */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param value the value to test.
     * @return true is the value can be parsed as an Short.
     */
    public static boolean isShort(String value) {
        try {
            Short.parseShort(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param value the value to test.
     * @return true is the value can be parsed as an Float.
     */
    public static boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param value the value to test.
     * @return true is the value can be parsed as an Double.
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get the background thread timeout.
     * @return the timeout val
     */
    public static int getBackgroundThreadTransactionTimeout() {
        final String backgroundThreadTransactionTimeout =
                ConfigurationHelper.getConfiguration().getString(
                        ConfigParamEnum.BACKGROUND_THREAD_TRANSACTION_TIMEOUT.name());
        int timeout = DEFAULT_TIMEOUT_SECONDS;
        if (StringUtils.isNumeric(backgroundThreadTransactionTimeout)) {
            timeout = Integer.parseInt(backgroundThreadTransactionTimeout);
        }

        LOG.debug("Transaction Timeout = " + timeout);
        return timeout;
    }
}
