//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.plugins.illumina;

import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.array.PhysicalProbe;
import gov.nih.nci.caarray.validation.FileValidationResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Base class for both gene expression and genotype Illumina CSV design handlers.
 * 
 * @param <T> type of Header enum class listing expected headers for the files
 */
abstract class AbstractCsvDesignHelper<T extends Enum<T>> {
    /**
     * The array is indexed by the ordinal of the Header enums, the values in this array are the indexes of the columns.
     */
    private int[] headerIndex;
    private final Class<T> headerEnumClass;
    private final Set<T> requiredColumns;

    protected AbstractCsvDesignHelper(Class<T> headerEnumClass, Set<T> requiredColumns) {
        this.headerEnumClass = headerEnumClass;
        this.requiredColumns = requiredColumns;
    }

    public Set<T> getRequiredColumns() {
        return requiredColumns;
    }

    /**
     * init headerIndex by looking at the order of the headers row.
     */
    @SuppressWarnings("unchecked")
    void initHeaderIndex(List<String> headerValues) {
        EnumSet<? extends Enum> headers = EnumSet.allOf(headerEnumClass);
        headerIndex = new int[headers.size()];
        for (int i = 0; i < headerValues.size(); i++) {
            Enum h = Enum.valueOf(headerEnumClass, headerValues.get(i).toUpperCase(Locale.getDefault()));
            headerIndex[h.ordinal()] = i;
        }
    }

    int indexOf(T header) {
        return headerIndex[header.ordinal()];
    }

    abstract boolean isLineFollowingAnnotation(List<String> values);

    abstract void validateValues(List<String> values, FileValidationResult result, int currentLineNumber);

    abstract PhysicalProbe createProbe(ArrayDesignDetails details, List<String> name);

    boolean isHeaderLine(List<String> values) {
        if (values.size() < requiredColumns.size()) {
            return false;
        }
        Collection<T> headers = new ArrayList<T>(values.size());
        try {
            for (String v : values) {
                T h = Enum.valueOf(headerEnumClass, v.toUpperCase(Locale.getDefault()));
                headers.add(h);
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        return EnumSet.allOf(headerEnumClass).containsAll(headers);
    }

    final String getValue(List<String> values, T header) {
        return values.get(indexOf(header));
    }

    final Long getLongValue(List<String> values, T header) {
        String stringValue = getValue(values, header);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        } else {
            return Long.parseLong(stringValue);
        }
    }

    final Integer getIntegerValue(List<String> values, T header) {
        String stringValue = getValue(values, header);
        if (StringUtils.isBlank(stringValue)) {
            return null;
        } else {
            return Integer.parseInt(stringValue);
        }
    }
}
