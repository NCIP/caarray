package gov.nih.nci.caarray.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Date Utility Class used to convert Strings to Dates and Timestamps.
 */
public final class DateUtil {
    //~ Static fields/initializers =============================================

    private static final Log LOG = LogFactory.getLog(DateUtil.class);
    private static String timePattern = "HH:mm";

    //~ Methods ================================================================

    private DateUtil() {
        //private default constructor
    }

    /**
     * Return default datePattern (MM/dd/yyyy).
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultDatePattern;
        try {
            defaultDatePattern = ResourceBundle.getBundle(Constants.BUNDLE_KEY, locale)
                .getString("date.format");
        } catch (MissingResourceException mse) {
            defaultDatePattern = "MM/dd/yyyy";
        }

        return defaultDatePattern;
    }

    /**
     * get date time pattern.
     * @return String String
     */
    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate(Date aDate) {
        Locale locale = Locale.US;
        SimpleDateFormat df;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern(), locale);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input.
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws ParseException ParseException
     */
    public static Date convertStringToDate(String aMask, String strDate)
      throws ParseException {
        Locale locale = Locale.US;
        SimpleDateFormat df;
        Date date;
        df = new SimpleDateFormat(aMask, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("converting '" + strDate + "' to date with mask '"
                      + aMask + "'");
        }

        date = df.parse(strDate);
        return (date);
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a.
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(timePattern, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy.
     *
     * @return the current date
     * @throws ParseException ParseException
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        Locale locale = Locale.US;
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern(), locale);

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input.
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     *
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        Locale locale = Locale.US;
        String returnValue = "";

        if (aDate == null) {
            LOG.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask, locale);
            returnValue = df.format(aDate);
        }
        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input.
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern.
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     *
     * @throws ParseException ParseException
     */
    public static Date convertStringToDate(String strDate)
      throws ParseException {
        Date aDate = null;
        aDate = convertStringToDate(getDatePattern(), strDate);
        return aDate;
    }
}
