/**
 * The software subject to this notice and license includes both human readable
 * source code form and machine readable, binary, object code form. The caarray-common-jar
 * Software was developed in conjunction with the National Cancer Institute
 * (NCI) by NCI employees and 5AM Solutions, Inc. (5AM). To the extent
 * government employees are authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 *
 * This caarray-common-jar Software License (the License) is between NCI and You. You (or
 * Your) shall mean a person or an entity, and all other entities that control,
 * are controlled by, or are under common control with the entity. Control for
 * purposes of this definition means (i) the direct or indirect power to cause
 * the direction or management of such entity, whether by contract or otherwise,
 * or (ii) ownership of fifty percent (50%) or more of the outstanding shares,
 * or (iii) beneficial ownership of such entity.
 *
 * This License is granted provided that You agree to the conditions described
 * below. NCI grants You a non-exclusive, worldwide, perpetual, fully-paid-up,
 * no-charge, irrevocable, transferable and royalty-free right and license in
 * its rights in the caarray-common-jar Software to (i) use, install, access, operate,
 * execute, copy, modify, translate, market, publicly display, publicly perform,
 * and prepare derivative works of the caarray-common-jar Software; (ii) distribute and
 * have distributed to and by third parties the caarray-common-jar Software and any
 * modifications and derivative works thereof; and (iii) sublicense the
 * foregoing rights set out in (i) and (ii) to third parties, including the
 * right to license such rights to further third parties. For sake of clarity,
 * and not by way of limitation, NCI shall have no right of accounting or right
 * of payment from You or Your sub-licensees for the rights granted under this
 * License. This License is granted at no charge to You.
 *
 * Your redistributions of the source code for the Software must retain the
 * above copyright notice, this list of conditions and the disclaimer and
 * limitation of liability of Article 6, below. Your redistributions in object
 * code form must reproduce the above copyright notice, this list of conditions
 * and the disclaimer of Article 6 in the documentation and/or other materials
 * provided with the distribution, if any.
 *
 * Your end-user documentation included with the redistribution, if any, must
 * include the following acknowledgment: This product includes software
 * developed by 5AM and the National Cancer Institute. If You do not include
 * such end-user documentation, You shall include this acknowledgment in the
 * Software itself, wherever such third-party acknowledgments normally appear.
 *
 * You may not use the names "The National Cancer Institute", "NCI", or "5AM"
 * to endorse or promote products derived from this Software. This License does
 * not authorize You to use any trademarks, service marks, trade names, logos or
 * product names of either NCI or 5AM, except as required to comply with the
 * terms of this License.
 *
 * For sake of clarity, and not by way of limitation, You may incorporate this
 * Software into Your proprietary programs and into any third party proprietary
 * programs. However, if You incorporate the Software into third party
 * proprietary programs, You agree that You are solely responsible for obtaining
 * any permission from such third parties required to incorporate the Software
 * into such third party proprietary programs and for informing Your
 * sub-licensees, including without limitation Your end-users, of their
 * obligation to secure any required permissions from such third parties before
 * incorporating the Software into such third party proprietary software
 * programs. In the event that You fail to obtain such permissions, You agree
 * to indemnify NCI for any claims against NCI by such third parties, except to
 * the extent prohibited by law, resulting from Your failure to obtain such
 * permissions.
 *
 * For sake of clarity, and not by way of limitation, You may add Your own
 * copyright statement to Your modifications and to the derivative works, and
 * You may provide additional or different license terms and conditions in Your
 * sublicenses of modifications of the Software, or any derivative works of the
 * Software as a whole, provided Your use, reproduction, and distribution of the
 * Work otherwise complies with the conditions stated in this License.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO
 * EVENT SHALL THE NATIONAL CANCER INSTITUTE, 5AM SOLUTIONS, INC. OR THEIR
 * AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gov.nih.nci.caarray.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.OutputSupplier;
import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Utility classes for our project.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
public final class CaArrayUtils {
    private static final Logger LOG = Logger.getLogger(CaArrayUtils.class);
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
     * Returns an empty collection or map of the appropriate type for a given collection class. By default, returns an
     * empty list, but will return an empty set, empty sorted set, or empty map if the passed in type is a subclass of
     * Set, SortedSet, or Map respectively.
     * 
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
     * Removes matched quotes (single or double) from a string. Quotes are only removed from the first and last
     * characters of the string.
     * 
     * @param str string to dequote
     * @return the dequoted string or the original string, if no changes were made
     */
    public static String dequoteString(String str) {
        if (str != null && str.length() > 1
                && ((str.charAt(0) == '"' || str.charAt(0) == '\'') && str.charAt(str.length() - 1) == str.charAt(0))) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    /**
     * For given class, returns a ReflectionHelper instance with property accessors for the class.
     * 
     * @param clazz the class
     * @return the ReflectionHelper
     */
    public static ReflectionHelper createReflectionHelper(Class<?> clazz) {
        final List<PropertyAccessor> accessors = new ArrayList<PropertyAccessor>();

        Class<?> currentClass = clazz;
        while (currentClass != null) {
            final Method[] methods = currentClass.getDeclaredMethods();
            for (final Method getter : methods) {
                if (getter.getName().startsWith("get") && getter.getParameterTypes().length == 0) {
                    for (final Method setter : methods) {
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
     * For each String bean property on o, if o is blank or empty, converts that property to null.
     * 
     * @param o object to convert properties on.
     */
    public static void blankStringPropsToNull(Object o) {
        if (o == null) {
            return;
        }

        final ReflectionHelper helper = createReflectionHelper(o.getClass());
        for (final PropertyAccessor accessor : helper.getAccessors()) {
            if (accessor.getType().equals(String.class)) {
                try {
                    if (StringUtils.isBlank((String) accessor.get(o))) {
                        accessor.set(o, null);
                    }
                } catch (final IllegalArgumentException e) {
                    LOG.debug(e.getMessage(), e);
                } catch (final IllegalAccessException e) {
                    LOG.debug(e.getMessage(), e);
                } catch (final InvocationTargetException e) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(boolean[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(int[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(long[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(short[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(values[i]);
        }
        return sb.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(double[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(toXmlString(values[i]));
        }
        return sb.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     * </p>
     * 
     * @param values the values to join together, may be null (in which case an empty String is returned)
     * @param separator the separator to use
     * @return the joined String, empty if null array input
     */
    public static String join(float[] values, String separator) {
        if (values == null || values.length == 0) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(values[0]);
        for (int i = 1; i < values.length; i++) {
            sb.append(separator).append(toXmlString(values[i]));
        }
        return sb.toString();
    }

    /**
     * Joins the given values as a comma-separated string. Each value will be encoded in this string by escaping any
     * commas in the value with a backslash.
     * 
     * @param values the values to join (null is acceptable).
     * @return the CSV string consisting of the values. If the values were null, an empty String.
     */
    public static String joinAsCsv(String[] values) {
        if (values == null) {
            return StringUtils.EMPTY;
        }
        try {
            final StringWriter sw = new StringWriter();
            final CsvWriter csvWriter = new CsvWriter(sw, ',');
            csvWriter.setEscapeMode(CsvWriter.ESCAPE_MODE_BACKSLASH);
            csvWriter.setUseTextQualifier(false);
            csvWriter.writeRecord(values);
            csvWriter.flush();
            csvWriter.close();
            return sw.toString();
        } catch (final IOException e) {
            throw new IllegalStateException("Could not encode as CSV record: " + e, e);
        }
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed boolean values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:boolean type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static boolean[] splitIntoBooleans(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final boolean[] values = new boolean[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToBoolean(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed short values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:short type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static short[] splitIntoShorts(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final short[] values = new short[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Short.parseShort(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed long values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:long type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static long[] splitIntoLongs(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final long[] values = new long[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Long.parseLong(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed int values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:int type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static int[] splitIntoInts(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final int[] values = new int[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = Integer.parseInt(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed float values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:float type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static float[] splitIntoFloats(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final float[] values = new float[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToFloat(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided text into an array of parsed double values, using specified separator.
     * </p>
     * 
     * <p>
     * Each value should be encoded using the literal representation of the XML Schema xs:double type
     * </p>
     * 
     * @param s the string to parse
     * @param separator the separator between values
     * @return the array of parsed values
     */
    public static double[] splitIntoDoubles(String s, String separator) {
        final String[] splits = StringUtils.split(s, separator);
        final double[] values = new double[splits.length];
        for (int i = 0; i < splits.length; i++) {
            values[i] = xmlStringToDouble(splits[i]);
        }
        return values;
    }

    /**
     * <p>
     * Splits the provided CSV String into an array of parsed values.
     * </p>
     * 
     * Each value within the String will be unescaped by converting any backslash-comma combinations back to commas.
     * 
     * @param s string containing a comma-separated list of strings.
     * @return the array of parsed Strings. If s did not contain any comma separated Strings, an empty String. If s was
     *         not a valid CSV string, an IllegalArgumentException is thrown.
     */
    public static String[] splitFromCsv(String s) {
        try {
            final CsvReader csvReader = new CsvReader(new StringReader(s), ',');
            csvReader.setEscapeMode(CsvReader.ESCAPE_MODE_BACKSLASH);
            csvReader.setUseTextQualifier(false);
            String[] values = ArrayUtils.EMPTY_STRING_ARRAY;
            if (csvReader.readRecord()) {
                final int length = csvReader.getColumnCount();
                values = new String[length];
                for (int i = 0; i < length; i++) {
                    values[i] = csvReader.get(i);
                }
            }
            csvReader.close();
            return values;
        } catch (final IOException e) {
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
     * 
     * @param <E> the type of the enum instances
     * @param enums the enum instances whose constant names to return
     * @return the names, e.g. the set of enum.name() for each enum in enums
     */
    public static <E extends Enum<E>> Set<String> namesForEnums(Iterable<E> enums) {
        final Set<String> names = new HashSet<String>();
        for (final Enum<?> oneEnum : enums) {
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
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't serialize object", e);
        } finally {
            IOUtils.closeQuietly(objectOutputStream);
            IOUtils.closeQuietly(gZipOutputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserializes an object from a gzipped serialized representation.
     * 
     * @param bytes the byte array containing the gzipped serialized representation of an object
     * @return the deserialized object
     */
    public static Serializable deserialize(byte[] bytes) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        final Serializable value = deserialize(byteArrayInputStream);
        IOUtils.closeQuietly(byteArrayInputStream);
        return value;
    }

    /**
     * Deserializes an object from a gzipped serialized representation.
     * 
     * @param is the InputStream which will produce the gzipped serialized representation of an object
     * @return the deserialized object
     */
    public static Serializable deserialize(InputStream is) {
        GZIPInputStream gzipInputStream = null;
        ObjectInputStream objectInputStream = null;
        Serializable object = null;
        try {
            gzipInputStream = new GZIPInputStream(is);
            objectInputStream = new ObjectInputStream(gzipInputStream);
            object = (Serializable) objectInputStream.readObject();
        } catch (final IOException e) {
            throw new IllegalStateException("Couldn't read object", e);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException("Couldn't read object", e);
        } finally {
            IOUtils.closeQuietly(objectInputStream);
            IOUtils.closeQuietly(gzipInputStream);
        }
        return object;
    }

    /**
     * Returns the first element in iterable that satisfies the given predicate, or null if no elements do. This is
     * identical to Iterables.find from Google's Collections library, except it returns null rather than throw
     * NoSuchElementException if there are no matches.
     * 
     * @param <T> the type of elements of iterable
     * @param iterable the iterable to search throw
     * @param predicate the predicate to satisfy
     * @return the first element matching the predicate, or null if no matches
     */
    public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate) {
        try {
            return Iterables.find(iterable, predicate);
        } catch (final NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Utility method to create a URI from given string without throwing a checked exception. This simply wraps new
     * URI(uri), throwing a runtime exception if this results in a URISyntaxException. For use with known-good uri
     * strings, to avoid having a try-catch block.
     * 
     * @param uri the URI string
     * @return the URI corresponding to uri
     */
    public static URI makeUriQuietly(String uri) {
        try {
            return new URI(uri);
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Couldn't create dummy URI");
        }
    }

    /**
     * Utility method to create a URI from given scheme and scheme specific part without throwing a checked exception.
     * This simply wraps new URI(scheme, schemeSpecificPart), throwing a runtime exception if this results in a
     * URISyntaxException. For use with known-good scheme and scheme specific part, to avoid having a try-catch block.
     * 
     * @param scheme the scheme
     * @param schemeSpecificPart the schemeSpecificPart
     * @return the URI corresponding to scheme:schemeSpecificPart
     */
    public static URI makeUriQuietly(String scheme, String schemeSpecificPart) {
        try {
            return new URI(scheme, schemeSpecificPart, null);
        } catch (final URISyntaxException e) {
            throw new IllegalStateException("Couldn't create dummy URI");
        }
    }

    /**
     * returns a byte array representing the input byte array, gzipped. Use this method with caution - for large arrays,
     * you should use streams instead.
     * 
     * @param input the content to compress
     * @return the gzipped input
     * @throws IOException if there is a problem zipping the content
     */
    public static byte[] gzip(byte[] input) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.write(input, new OutputSupplier<OutputStream>() {
            @Override
            public OutputStream getOutput() throws IOException {
                return new GZIPOutputStream(baos);
            }
        });
        baos.close();
        return baos.toByteArray();
    }

    /**
     * returns a byte array representing the input byte array, un-gzipped. Use this method with caution - for large
     * arrays, you should use streams instead.
     * 
     * @param input the content to uncompress, should be in gzip format
     * @return the uncompressed input
     * @throws IOException if there is a problem uncompressing the content, including if the input is not gzipped
     */
    public static byte[] gunzip(byte[] input) throws IOException {
        final InputStream in = new GZIPInputStream(new ByteArrayInputStream(input));
        final byte[] output = ByteStreams.toByteArray(in);
        in.close();
        return output;
    }

    /**
     * Load a properties file from given classpath resource, and create a constant binding to @Named(key) for each
     * property using the given binder. If the resource cannot be loaded, does nothing.
     * 
     * @param resourceName the resource to load. it is loaded using the classloader of this class.
     * @param binder the binder to use for pinding the key=property values
     */
    public static void bindPropertiesAsNamed(String resourceName, Binder binder) {
        final Properties props = new Properties();
        try {
            props.load(CaArrayUtils.class.getResourceAsStream(resourceName));
        } catch (final IOException e) {
            LOG.warn("Could not load properties from " + resourceName);
        }
        Names.bindProperties(binder, props);
    }
}
