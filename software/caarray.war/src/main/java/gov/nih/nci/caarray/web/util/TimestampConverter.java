package gov.nih.nci.caarray.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang.StringUtils;

/**
 * This class is converts a java.util.Date to a String and a String to a
 * java.util.Date for use as a Timestamp. It is used by BeanUtils when copying
 * properties.
 */
public class TimestampConverter extends DateConverter {

    /**
     * time stamp format.
     */
    public static final String TS_FORMAT = DateUtil.getDatePattern() + " HH:mm:ss.S";

    /**
     * convert to date.
     * @param type Class
     * @param value Object
     * @return date Object
     */
    @SuppressWarnings("unchecked")
    protected Object convertToDate(Class type, Object value) {
        Locale locale = Locale.US;
        DateFormat df = new SimpleDateFormat(TS_FORMAT, locale);
        if (value instanceof String) {
            try {
                if (StringUtils.isEmpty(value.toString())) {
                    return null;
                }

                return df.parse((String) value);
            } catch (Exception pe) {
                throw new ConversionException("Error converting String to Timestamp" + pe);
            }
        }

        throw new ConversionException("Could not convert "
                + value.getClass().getName() + " to " + type.getName());
    }

    /**
     * convert to string.
     * @param type Class
     * @param value Object
     * @return date Object
     */
    @SuppressWarnings("unchecked")
    protected Object convertToString(Class type, Object value) {
        Locale locale = Locale.US;
        DateFormat df = new SimpleDateFormat(TS_FORMAT, locale);
        if (value instanceof Date) {
            try {
                return df.format(value);
            } catch (Exception e) {
                throw new ConversionException("Error converting Timestamp to String" + e);
            }
        }

        return value.toString();
    }
}