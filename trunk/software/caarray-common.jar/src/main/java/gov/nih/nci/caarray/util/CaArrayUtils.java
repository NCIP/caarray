//============================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See https://github.com/NCIP/caarray/blob/nimblegen-parser/LICENSE for details.
//============================================================================
package gov.nih.nci.caarray.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

/**
 * Utility classes for our project.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
public final class CaArrayUtils {
    private static final SortedSet<Object> EMPTY_SORTED_SET = new TreeSet<Object>();

    private CaArrayUtils() {
        // prevent instantiation;
    }

    /**
     * Method to take a get a unique result from a set and return it or null.
     *
     * @param <T> the type of the returned object
     * @param results the set of results returned from a query
     * @return the first result in the set or null
     */
    public static <T> T uniqueResult(Collection<T> results) {
        return results.isEmpty() ? null : results.iterator().next();
    }

    /**
     * Returns an empty collection or map of the appropriate type for a given collection class. By default,
     * returns an empty list, but will return an empty set, empty sorted set, or empty map if the passed
     * in type is a subclass of Set, SortedSet, or Map respectively.
     * @param collectionType the class of whose type to return an empty collection or map
     * @return the empty collection or map
     */
    public static Object emptyCollectionOrMapFor(Class<?> collectionType) {
        Object val = Collections.EMPTY_LIST;
        if (SortedSet.class.isAssignableFrom(collectionType)) {
            val = EMPTY_SORTED_SET;
        } else if (Set.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_SET;
        } else if (List.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_LIST;
        } else if (Map.class.isAssignableFrom(collectionType)) {
            val = Collections.EMPTY_MAP;
        }
        return val;
    }

    /**
     * Removes matched quotes (single or double) from a string.  Quotes are only removed from the first and last
     * characters of the string.
     * @param string string to dequote
     * @return the dequoted string or the original string, if no changes were made
     */
    public static String dequoteString(String string) {
        if (string != null && string.length() > 1
                && ((string.charAt(0) == '"' || string.charAt(0) == '\'')
                        && string.charAt(string.length() - 1) == string.charAt(0))) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    /**
     * For given class, returns a ReflectionHelper instance with property accessors for the class.
     *
     * @param clazz the class
     * @return the ReflectionHelper
     */
    public static ReflectionHelper createReflectionHelper(Class<?> clazz) {
        List<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>();

        Class<?> currentClass = clazz;
        while (currentClass != null) {
            Method[] methods = currentClass.getDeclaredMethods();
            for (Method getter : methods) {
                if (getter.getName().startsWith("get") && getter.getParameterTypes().length == 0) {
                    for (Method setter : methods) {
                        if (setter.getName().equals('s' + getter.getName().substring(1))
                                && setter.getParameterTypes().length == 1 && Void.TYPE.equals(setter.getReturnType())
                                && getter.getReturnType().equals(setter.getParameterTypes()[0])) {
                            getter.setAccessible(true);
                            setter.setAccessible(true);
                            accessors.add(new PropertyAccessor(getter, setter));
                        }
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return new ReflectionHelper(accessors.toArray(new PropertyAccessor[accessors.size()]));
    }

    /**
     * For each String bean property on o, if o is blank or empty,
     * converts that property to null.
     *
     * @param o object to convert properties on.
     */
    public static void blankStringPropsToNull(Object o) {
        if (o == null) {
            return;
        }
    
        ReflectionHelper helper = createReflectionHelper(o.getClass());
        for (PropertyAccessor accessor : helper.getAccessors()) {
            if (accessor.getType().equals(String.class)) {
                try {
                    if (StringUtils.isBlank((String) accessor.get(o))) {
                        accessor.set(o, null);
                    }
                } catch (IllegalArgumentException e) {
                    EntityPruner.LOG.debug(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    EntityPruner.LOG.debug(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    EntityPruner.LOG.debug(e.getMessage(), e);
                }
            }
        }
    }
    
    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(boolean[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
     }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(int[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
     }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(long[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
     }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(short[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
     }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(double[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(toXmlString(values[i]));
        }
        return sb.toString();
     }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     *
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(float[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(toXmlString(values[i]));
        }
        return sb.toString();
     }
    
    /**
     * Joins the given values as a comma-separated string. Each value will be encoded in this string
     * by escaping any commas in the value with a backslash.
     * @param values the values to join (null is acceptable).
     * @return the CSV string consisting of the values. If the values were null, an empty String.
     */
    public static String joinAsCsv(String[] values) {
        if (values == null) {
            return StringUtils.EMPTY;
        }
        try {
            StringWriter sw = new StringWriter();
            CsvWriter csvWriter = new CsvWriter(sw, ',');
            csvWriter.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);
            csvWriter.setUseTextQualifier(false);
            csvWriter.writeRecord(values);
            csvWriter.flush();
            csvWriter.close();
            return sw.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Could not encode as CSV record: " + e, e);
        }
    }

    
    /**
     * <p>Splits the provided text into an array of parsed boolean values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:boolean type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static boolean[] splitIntoBooleans(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        boolean[] values = new boolean[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToBoolean(splits[i]);
        }
        return values;
    }

    /**
     * <p>Splits the provided text into an array of parsed short values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:short type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static short[] splitIntoShorts(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        short[] values = new short[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Short.parseShort(splits[i]);
        }
        return values;
    }

    /**
     * <p>Splits the provided text into an array of parsed long values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:long type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static long[] splitIntoLongs(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        long[] values = new long[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Long.parseLong(splits[i]);
        }
        return values;
    }

    /**
     * <p>Splits the provided text into an array of parsed int values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:int type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static int[] splitIntoInts(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        int[] values = new int[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Integer.parseInt(splits[i]);
        }
        return values;
    }

    /**
     * <p>Splits the provided text into an array of parsed float values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:float type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static float[] splitIntoFloats(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        float[] values = new float[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToFloat(splits[i]);
        }
        return values;
    }

    /**
     * <p>Splits the provided text into an array of parsed double values, using specified separator.</p>
     * 
     * <p>Each value should be encoded using the literal representation of the XML Schema xs:double type</p>
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static double[] splitIntoDoubles(String s, String separator) {
        String[] splits = StringUtils.split(s, separator);
        double[] values = new double[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToDouble(splits[i]);
        }
        return values;
    }
    
    /**
     * <p>Splits the provided CSV String into an array of parsed values.</p>
     * 
     * Each value within the String will be unescaped by converting any backslash-comma combinations back to commas.
     * @param s string containing a comma-separated list of strings.
     * @return the array of parsed Strings. If s did not contain any comma separated Strings, an empty 
     * String. If s was not a valid CSV string, an IllegalArgumentException is thrown. 
     */
    public static String[] splitFromCsv(String s) {
        try {
            CsvReader csvReader = new CsvReader(new StringReader(s), ',');
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
            csvReader.setUseTextQualifier(false);
            String[] values = ArrayUtils.EMPTY_STRING_ARRAY;
            if (csvReader.readRecord()) {
                int length = csvReader.getColumnCount();
                values = new String[length];
                for (int i = 0; i < length; i++) {
                    values[i] = csvReader.get(i);
                }
            }
            csvReader.close();
            return values;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not parse as CSV record: " + s, e);
        }        
    }
    
    private static String toXmlString(float value) {
        if (Float.isNaN(value)) {
            return "NaN";
        } else if (value == Float.POSITIVE_INFINITY) {
            return "INF";
        } else if (value == Float.NEGATIVE_INFINITY) {
            return "-INF";
        } else { 
            return Float.toString(value);
        }
    }
    
    private static String toXmlString(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        } else if (value == Double.POSITIVE_INFINITY) {
            return "INF";
        } else if (value == Double.NEGATIVE_INFINITY) {
            return "-INF";
        } else { 
            return Double.toString(value);
        }
    }
    
    private static boolean xmlStringToBoolean(String value) {
        if ("true".equals(value) || "1".equals(value)) {
            return true;
        } else if ("false".equals(value) || "0".equals(value)) {
            return false;
        } else { 
            throw new IllegalArgumentException(value + " is not a valid boolean"); 
        }
    }
    
    private static float xmlStringToFloat(String value) {
        if ("NaN".equals(value)) {
            return Float.NaN;
        } else if ("INF".equals(value)) {
            return Float.POSITIVE_INFINITY;
        } else if ("-INF".equals(value)) {
            return Float.NEGATIVE_INFINITY;
        } else {
            return Float.parseFloat(value);
        }
    }

    private static double xmlStringToDouble(String value) {
        if ("NaN".equals(value)) {
            return Double.NaN;
        } else if ("INF".equals(value)) {
            return Double.POSITIVE_INFINITY;
        } else if ("-INF".equals(value)) {
            return Double.NEGATIVE_INFINITY;
        } else {
            return Double.parseDouble(value);
        }
    }
    
    /**
     * Return the constant names of the given enum instances.
     * @param <E> the type of the enum instances
     * @param enums the enum instances whose constant names to return
     * @return the names, e.g. the set of enum.name() for each enum in enums 
     */
    public static <E extends Enum<E>> Set<String> namesForEnums(Iterable<E> enums) {
        Set<String> names = new HashSet<String>();
        for (Enum<?> oneEnum : enums) {
            names.add(oneEnum.name());
        }
        return names;
    }

    /**
     * Serializes the given object (zipped) to a byte array.
     *
     * @param serializable object to serialize
     * @return the serialized object as a byte array.
     */
    public static byte[] serialize(Serializable serializable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gZipOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            gZipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            objectOutputStream = new ObjectOutputStream(gZipOutputStream);
            objectOutputStream.writeObject(serializable);
            objectOutputStream.flush();
            gZipOutputStream.finish();
            gZipOutputStream.flush();
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't serialize object", e);
        } finally {
            IOUtils.closeQuietly(objectOutputStream);
            IOUtils.closeQuietly(gZipOutputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes an object from a zipped serialized representation in a byte array.
     *
     * @param bytes the byte array
     * @return the deserialized object.
     */
    public static Serializable deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipInputStream = null;
        ObjectInputStream objectInputStream = null;
        Serializable object = null;
        try {
            gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            objectInputStream = new ObjectInputStream(gzipInputStream);
            object = (Serializable) objectInputStream.readObject();
        } catch (IOException e) {
            throw new IllegalStateException("Couldn't read object", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't read object", e);
        } finally {
            IOUtils.closeQuietly(objectInputStream);
            IOUtils.closeQuietly(gzipInputStream);
            IOUtils.closeQuietly(byteArrayInputStream);
        }
        return object;
    }
}
