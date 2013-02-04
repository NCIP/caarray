//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.web.util;

import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.displaytag.model.Cell;

/**
 * Comparator that correctly sorts user list, as displayed in ui, with HTML A HREF.
 */
public class UserComparator implements Comparator<Cell> {

    /**
     * {@inheritDoc}
     */
    public int compare(Cell c1, Cell c2) {
        String s1 = c1.getStaticValue().toString();
        String s2 = c2.getStaticValue().toString();
        String[] first = getNames(s1);
        String[] second = getNames(s2);

        return new CompareToBuilder()
            .append(first[0], second[0])
            .append(first[1], second[1])
            .toComparison();
    }

    /**
     * Extracts last and first name from a string like <code>&lt;a href="..."&gt;last, first&lt;/a&gt;</code>.
     * @param s the html to extrat names from
     * @return UPPER(last), UPPER(first)
     */
    public static String[] getNames(String s) {
        Pattern p = Pattern.compile("[^>]*>\\s*(.*),\\s*(.*)<[^<]*");
        Matcher m = p.matcher(s.toUpperCase(Locale.US));
        m.lookingAt();
        return new String[] {m.group(1).trim(), m.group(2).trim()};
    }
}
