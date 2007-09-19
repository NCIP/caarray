package gov.nih.nci.caarray.web.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.sql.Timestamp;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang.StringUtils;


/**
 * This class is converts a java.util.Date to a String
 * and a String to a java.util.Date.
 */
public class DateConverter implements Converter {

    /**
     * convert date to string.
     * @param type Class
     * @param value Object
     * @return Object object
     */
    @SuppressWarnings("unchecked")
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        } else if (type == Timestamp.class) {
            return convertToDate(type, value, DateUtil.getDateTimePattern());
        } else if (type == Date.class) {
            return convertToDate(type, value, DateUtil.getDatePattern());
        } else if (type == String.class) {
            return convertToString(type, value);
        }

        throw new ConversionException("Could not convert "
                + value.getClass().getName() + " to "
                + type.getName());
    }

    /**
     * convert to date.
     * @param type Class
     * @param value Object
     * @param pattern String
     * @return Object Object
     */
    @SuppressWarnings("unchecked")
    protected Object convertToDate(Class type, Object value, String pattern) {
        Locale locale = Locale.US;
        DateFormat df = new SimpleDateFormat(pattern, locale);
        if (value instanceof String) {
            try {
                if (StringUtils.isEmpty(value.toString())) {
                    return null;
                }

                Date date = df.parse((String) value);
                if (type.equals(Timestamp.class)) {
                    return new Timestamp(date.getTime());
                }
                return date;
            } catch (Exception pe) {
                throw new ConversionException("Error converting String to Date" + pe);
            }
        }

        throw new ConversionException("Could not convert "
                + value.getClass().getName() + " to "
                + type.getName());
    }

    /**
     * convert to string.
     * @param type Class
     * @param value Object
     * @return Object Object
     */
    @SuppressWarnings("unchecked")
    protected Object convertToString(Class type, Object value) {

        if (value instanceof Date) {
            Locale locale = Locale.US;
            DateFormat df = new SimpleDateFormat(DateUtil.getDatePattern(), locale);
            if (value instanceof Timestamp) {
                df = new SimpleDateFormat(DateUtil.getDateTimePattern(), locale);
            }

            try {
                return df.format(value);
            } catch (Exception e) {
                throw new ConversionException("Error converting Date to String" + e);
            }
        } else {
            return value.toString();
        }
    }
}
